object RaceConditionSimulator {

    private const val AMOUNT = 100000

    fun simulate(lock: FixnumLockable) {
        var counter = 0

        val runnable = Runnable {
            lock.registerThread()

            for (i in IntRange(1, AMOUNT)) {
                lock.lock()
                counter++
                lock.unlock()
            }

            lock.unregisterThread()
        }

        val threadFirst = Thread(runnable)
        val threadSecond = Thread(runnable)

        threadFirst.start()
        threadSecond.start()

        threadFirst.join()
        threadSecond.join()

        verifyingResult(lock, counter)
    }

    private fun verifyingResult(lock: FixnumLockable, actualResult: Int) {
        if (actualResult != AMOUNT * 2) {
            println("Race condition appears for ${lock.javaClass.simpleName}: expected value is ${AMOUNT * 2}, but actual is $actualResult.")
        } else {
            println("Race condition isn't detected for ${lock.javaClass.simpleName}.")
        }
    }
}