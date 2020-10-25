import java.lang.UnsupportedOperationException
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Condition

class DummyLock : FixnumLock(2) {

    override fun lock() {
        // do nothing
    }

    override fun unlock() {
        // do nothing
    }

    override fun tryLock(): Boolean {
        throw UnsupportedOperationException()
    }

    override fun tryLock(time: Long, unit: TimeUnit): Boolean {
        throw UnsupportedOperationException()
    }

    override fun lockInterruptibly() {
        throw UnsupportedOperationException()
    }

    override fun newCondition(): Condition {
        throw UnsupportedOperationException()
    }
}