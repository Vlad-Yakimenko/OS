import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.system.exitProcess

class Server(
    host: String,
    port: Int,
    private val type: String,
    private val testCase: Int,
    private var promptEnabled: Boolean
) {

    companion object {
        private const val DELTA = 2000
    }

    private val address: InetSocketAddress = InetSocketAddress(host, port)
    private lateinit var server: ServerSocket
    private val clientProcesses = mutableListOf<Process>()
    private val futureResults: MutableList<Future<Any>> = mutableListOf()
    val results: MutableMap<String, Double> = mutableMapOf()

    private var calculationsEnabled = true
    private val time: MutableMap<String, Long> = mutableMapOf()
    private val startTime = System.currentTimeMillis()
    private var lastPromptTime = System.currentTimeMillis()
    private val reader = BufferedReader(InputStreamReader(System.`in`))

    @Throws(IOException::class)
    fun start() {
        server = ServerSocket(address.port)
        val executorService = Executors.newFixedThreadPool(2)

        compute("f")
        compute("g")

        lateinit var socket: Socket
        for (i in 0..1) {
            try {
                socket = server.accept()
            } catch (e: IOException) {
                println("I/O  exception: $e")
            }

            futureResults.add(executorService.submit(MyCallable(socket)))
        }

        while (calculationsEnabled) {
            if (futureResults.size == 0) calculationsEnabled = false

            prompt()

            futureResults.removeIf {
                if (it.isDone) {
                    addResult(it.get().toString())
                    true
                } else {
                    false
                }
            }

            Thread.sleep(500)
        }
    }

    private fun compute(function: String) {
        val clientBuilder = ProcessBuilder(
            "java",
            "-jar",
            "C:\\Games\\Kotlin Code\\University\\OS\\Fifth_semester\\Labwork_first\\out\\artifacts\\Client" + "\\" + "Client.jar",
            address.hostName,
            address.port.toString(),
            type,
            function,
            testCase.toString()
        )

        try {
            clientProcesses.add(clientBuilder.start())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun prompt() {
        if (promptEnabled && System.currentTimeMillis() - lastPromptTime > DELTA) {
            println("Computation taking too long. Would you like to:")
            println("(a) continue")
            println("(b) continue without prompt")
            println("(c) cancel")
            var correct = false
            while (!correct) {
                var line = reader.readLine()
                line = line.toLowerCase()
                correct = true
                lastPromptTime = System.currentTimeMillis()
                when (line) {
                    "a" -> {
                        lastPromptTime = System.currentTimeMillis()
                        println("Continue")
                    }
                    "b" -> {
                        promptEnabled = false
                        println("Prompt disabled")
                    }
                    "c", "q" -> {
                        calculationsEnabled = false
                        println("Canceled")
                    }
                    else -> {
                        correct = false
                        println("Incorrect response: $line")
                    }
                }
            }
        }
    }

    fun quit() {
        killClientProcesses()
        stop()
        System.out.flush()
        exitProcess(0)
    }

    private fun killClientProcesses() {
        for (process in clientProcesses) {
            process.destroy()
        }
    }

    private fun stop() {
        try {
            server.close()
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun addResult(result: String) {
        val args = result.split(" ").toTypedArray()
        if (args.size < 2) return

        val (function, value) = args[0] to args[1].toDouble()
        println("Function: $function\nValue: $value\n")

        results[function] = value
        val computationTime = System.currentTimeMillis() - startTime
        time[function] = computationTime
        if (value == 0.0) calculationsEnabled = false
    }

    fun getConjunction(): Double? {
        if (results.getOrDefault("f", 1.0) == 0.0 || results.getOrDefault("g", 1.0) == 0.0)
            return 0.0
        else if (results.getOrDefault("f", 1.0) == 1.0 || results.getOrDefault("g", 1.0) == 1.0)
            return null

        return results["f"]?.times(results["g"]!!)
    }
}

class MyCallable(private val socket: Socket) : Callable<Any> {
    override fun call(): Any {
        InputStreamReader(socket.getInputStream()).use {
            val buffer = CharArray(128)
            val amount = it.read(buffer)
            socket.close()
            return String(buffer, 0, amount)
        }
    }
}