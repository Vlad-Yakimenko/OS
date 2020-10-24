abstract class FixnumLock(threadsAmount: Int) : FixnumLockable {

    private val threadsId: LongArray = LongArray(threadsAmount)

    init {
        reset()
    }

    @Synchronized
    override fun getId(): Int {
        val id = Thread.currentThread().id

        for (i in threadsId.indices) {
            if (threadsId[i] == id) {
                return i
            }
        }

        throw Exception("Something went wrong!")
    }

    @Synchronized
    override fun registerThread(): Boolean {
        return registerThread(Thread.currentThread().id)
    }

    @Synchronized
    override fun registerThread(id: Long): Boolean {
        for (i in threadsId.indices) {
            if (threadsId[i] <= 0) {
                threadsId[i] = id
                return true
            }
        }

        return false
    }

    @Synchronized
    override fun unregisterThread(): Boolean {
        return unregisterThread(Thread.currentThread().id)
    }

    @Synchronized
    override fun unregisterThread(id: Long): Boolean {
        for (i in threadsId.indices) {
            if (threadsId[i] == id) {
                threadsId[i] = -1
                return true
            }
        }

        return false
    }

    @Synchronized
    final override fun reset() {
        for (i in threadsId.indices) {
            threadsId[i] = -1
        }
    }
}