import sun.misc.Signal
import java.io.IOException
import kotlin.system.exitProcess


fun main(args: Array<String>) {
    Signal.handle(Signal("INT")) {
        println("\nAborted by user, x has not been provided")
        exitProcess(-2)
    }
    val manager = Server("localhost", 1052,  "int", 3, true)
    try {
        manager.start()
        println(manager.results)
        println(manager.getConjunction())
        manager.quit()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}