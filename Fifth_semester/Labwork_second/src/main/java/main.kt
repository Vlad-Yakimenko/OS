fun main() {
    var counter = 0
    val lock = DekkerLock()

    val runnable = Runnable {
        lock.registerThread()

        for (i in 0 until 200000) {
            lock.lock()
            counter++
            lock.unlock()
        }

        lock.unregisterThread()
    }

    val thread1 = Thread(runnable)
    val thread2 = Thread(runnable)

    thread1.start()
    thread2.start()

    thread1.join()
    thread2.join()

    println(counter)
}