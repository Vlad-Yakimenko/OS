import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.Condition

class DekkerLock : FixnumLock(2) {

    private var turn: AtomicLong = AtomicLong()
    private val flags = Array(2) { AtomicBoolean(false) }

    override fun lock() {
        val id = getId()
        val oppositeThreadId = (if (id == 0) 1 else 0).toLong()

        flags[id].set(true)
        while (flags[oppositeThreadId.toInt()].get()) {
            if (turn.get() == oppositeThreadId) {
                flags[id].set(false)

                while (turn.get() == oppositeThreadId);

                flags[id].set(true)
            }
        }
    }

    override fun unlock() {
        val id = getId()
        val oppositeThreadId = if (id == 0) 1 else 0
        turn.set(oppositeThreadId.toLong())
        flags[id].set(false)
    }

    override fun tryLock(): Boolean {
        val id = getId()
        val oppositeThreadId = if (id == 0) 1 else 0

        flags[id].set(true)
        while (flags[oppositeThreadId].get()) {
            if (turn.get() == oppositeThreadId.toLong()) {
                flags[id].set(false)
                return false
            }
        }

        return true
    }

    override fun tryLock(time: Long, unit: TimeUnit): Boolean {
        val id = getId()
        val oppositeThreadId = (if (id == 0) 1 else 0).toLong()

        var currentTime = System.currentTimeMillis()
        val endTime: Long = currentTime + unit.toMillis(time)

        flags[id].set(true)
        while (flags[oppositeThreadId.toInt()].get()) {
            if (turn.get() == oppositeThreadId) {
                flags[id].set(false)

                while (turn.get() == oppositeThreadId) {
                    if (currentTime == endTime) return false
                    currentTime++
                }

                flags[id].set(true)
            }
        }

        return true
    }

    override fun lockInterruptibly() {
        throw UnsupportedOperationException()
    }

    override fun newCondition(): Condition {
        throw UnsupportedOperationException()
    }
}