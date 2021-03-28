package scheduling

import util.Results
import util.sProcess
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.PrintStream
import java.util.*


private enum class ProcessState {
    REGISTERED, COMPLETED, JOINED, FORCEDOUT
}

private fun print(
    time: Int,
    out: PrintStream,
    state: ProcessState,
    process: sProcess?,
    processVector: Vector<sProcess?>
) {
    print(time, out, state, process, processVector, false)
}

private fun print(
    time: Int,
    out: PrintStream,
    state: ProcessState,
    process: sProcess?,
    processVector: Vector<sProcess?>,
    quantumExpired: Boolean
) {
    out.println(time.toString() + "ms ")
    when (state) {
        ProcessState.REGISTERED -> out.println("Process: " + processVector.indexOf(process) + " registered... (" + process!!.cpuTime + " " + process.delay + " " + process.cpuDone + ")")
        ProcessState.COMPLETED -> {
            if (quantumExpired) {
                out.println("Quantum time expired")
            } else {
                out.println("Quantum time reset")
            }
            out.println("Process: " + processVector.indexOf(process) + " completed... (" + process!!.cpuTime + " " + process.delay + " " + process.cpuDone + ")")
        }
        ProcessState.JOINED -> {
            out.println("Process joined queue")
            out.println("Process: " + processVector.indexOf(process) + " (" + process!!.cpuTime + " " + process.delay + " " + process.cpuDone + ")")
        }
        ProcessState.FORCEDOUT -> {
            out.println("Quantum time expired")
            out.println("Process: " + processVector.indexOf(process) + " forced out... (" + process!!.cpuTime + " " + process.delay + " " + process.cpuDone + ")")
        }
    }
}

fun Run(quantum: Int, runtime: Int, processVector: Vector<sProcess?>, result: Results): Results {
    var comptime = 0
    val size = processVector.size
    var completed = 0
    val resultsFile = "Summary-Processes"
    val sortedProcessVector = processVector.clone() as Vector<sProcess>
    sortedProcessVector.sortWith { o1: sProcess, o2: sProcess ->
        when {
            o1.delay == o2.delay -> {
                0
            }
            o1.delay > o2.delay -> {
                1
            }
            else -> {
                -1
            }
        }
    }
    val queue: Queue<sProcess> = LinkedList()
    result.schedulingType = "Batch (Nonpreemptive)"
    result.schedulingName = "Round Robin scheduling"
    var lastProcessIndex = 0
    var process: sProcess? = null
    var quantumCounter = 0
    try {
        val out = PrintStream(FileOutputStream(resultsFile))
        while (comptime < runtime) {
            //add entered processes
            if (lastProcessIndex < size) {
                while (sortedProcessVector.elementAt(lastProcessIndex).delay == comptime) {
                    val processCopy = sortedProcessVector.elementAt(lastProcessIndex)
                    queue.add(processCopy)
                    print(comptime, out, ProcessState.JOINED, processCopy, processVector)
                    ++lastProcessIndex
                    if (lastProcessIndex >= size) {
                        break
                    }
                }
            }
            if (process == null && !queue.isEmpty()) {
                process = queue.peek()
                print(comptime, out, ProcessState.REGISTERED, process, processVector)
            }
            if (process != null && process.cpuDone == process.cpuTime) {
                completed++
                print(comptime, out, ProcessState.COMPLETED, process, processVector, quantumCounter == quantum)
                quantumCounter = 0
                if (completed == size) {
                    result.computationTime = comptime
                    return result
                }
                queue.remove()
                if (!queue.isEmpty()) {
                    process = queue.peek()
                    print(comptime, out, ProcessState.REGISTERED, process, processVector)
                } else {
                    process = null
                }
            }

            //if quantum time expired
            if (quantumCounter == quantum) {
                quantumCounter = 0
                process!!.numBlocked++
                print(comptime, out, ProcessState.FORCEDOUT, process, processVector, true)
                queue.add(queue.poll())
                process = queue.peek()
                print(comptime, out, ProcessState.REGISTERED, process, processVector)
            }
            if (process != null) {
                quantumCounter++
                process.cpuDone++
            }
            comptime++
        }
        out.close()
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }
    result.computationTime = comptime
    return result
}
