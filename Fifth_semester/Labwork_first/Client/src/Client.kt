import spos.lab1.demo.DoubleOps
import spos.lab1.demo.IntOps
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.Writer
import java.net.InetSocketAddress
import java.net.Socket
import java.util.*
import java.util.function.Function
import kotlin.system.exitProcess

private lateinit var address: InetSocketAddress
private lateinit var clientSocket: Socket
private lateinit var functionName: String
private lateinit var function: Function<Int, Any>
private var testCase = 0

fun main(args: Array<String>) {
    println("Client works!")

    if (!parseArgs(args)) {
        println("Invalid arguments.")
        exitProcess(1)
    }

    try {
        start()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

@Throws(IOException::class)
private fun start() {
    lateinit var out: Writer

    try {
        clientSocket = Socket(address.hostString, address.port)

        if (!clientSocket.isConnected) {
            println("Can't connect to server.")
            exitProcess(1)
        }

        val result = when (val computationResult = function.apply(testCase)) {
            is Int -> {
                computationResult.toString()
            }
            is Double -> {
                computationResult.toString()
            }
            else -> {
                println("Wrong functions.")
                exitProcess(1)
            }
        }

        val message = "$functionName $result"
        out = BufferedWriter(OutputStreamWriter(clientSocket.getOutputStream()))
        out.write(message)
        out.flush()
        println("Message send.")

    } finally {
        clientSocket.close()
        out.close()
    }
}

private fun parseArgs(args: Array<String>): Boolean {
    return if (args.size != 5) {
        false
    } else {
        address = InetSocketAddress(args[0], args[1].toInt())
        functionName = args[3]

        when (args[2]) {
            "int" -> if (!parseIntFunction(functionName)) {
                return false
            }
            "double" -> if (!parseDoubleFunction(functionName)) {
                return false
            }
            else -> return false
        }

        testCase = args[4].toInt()
        return testCase in 0..5
    }
}

private fun parseIntFunction(arg: String): Boolean {

    function = when (arg) {
        "f" -> Function { i: Int ->
            try {
                return@Function IntOps.funcF(i)
            } catch (e: InterruptedException) {
                e.printStackTrace()
                return@Function 0
            }
        }
        "g" -> Function { i: Int ->
            try {
                return@Function IntOps.funcG(i)
            } catch (e: InterruptedException) {
                e.printStackTrace()
                return@Function 0
            }
        }
        else -> return false
    }

    return true
}

//private fun parseIntFunction(arg: String): Boolean {
//    function = when (arg) {
//        "f" -> Util.funcF
//        "g" -> Util.funcG
//        else -> return false
//    }
//
//    return true
//}

private fun parseDoubleFunction(arg: String): Boolean {

    function = when (arg) {
        "f" -> Function { i: Int ->
            try {
                return@Function DoubleOps.funcF(i)
            } catch (e: InterruptedException) {
                e.printStackTrace()
                return@Function 0
            }
        }
        "g" -> Function { i: Int ->
            try {
                return@Function DoubleOps.funcG(i)
            } catch (e: InterruptedException) {
                e.printStackTrace()
                return@Function 0
            }
        }
        else -> return false
    }

    return true
}

/**
 * Custom functions
 */
object Util {

    val funcF: Function<Int, Any> = Function { i ->
        val random = Random()
        when (i) {
            0 -> random.nextInt(10)
            1 -> random.nextInt(20)
            2, 3 -> 0
            4, 5 -> {
                Thread.sleep(2000)
                5
            }
            else -> 0
        }
    }

    val funcG: Function<Int, Any> = Function { i ->
        val random = Random()
        when (i) {
            0, 1 -> {
                Thread.sleep(5000)
                random.nextInt(80)
            }
            2 -> random.nextInt(50)
            3 -> 0
            4, 5 -> 25
            else -> 0
        }
    }
}