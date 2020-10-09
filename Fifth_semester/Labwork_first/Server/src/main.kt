import sun.misc.Signal
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    Signal.handle(Signal("INT")) {
        println("\nAborted by user, x has not been provided")
        exitProcess(-2)
    }

    val reader = BufferedReader(InputStreamReader(System.`in`))
    var testCase: Int

    while (true) {
        println("Enter x:")
        testCase = reader.readLine().toInt()

        if (testCase in 0 until 6) {
            break
        } else {
            println("Value must be from 0 to 5, but actual: $testCase")
        }
    }

    val manager = Server(host = "localhost", port = 1052, valueType = "int", testCase, promptEnabled = true)

    Signal.handle(Signal("INT")) {
        println("Cancelled by user")
        exitProcess(-2)
    }

    try {
        manager.start()
        printResults(manager)
        manager.quit()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

private fun printResults(manager: Server) {
    println(
        """
           Results: ${manager.results}
           Binary operation on results: ${manager.times()}""".trimIndent()
    )
}