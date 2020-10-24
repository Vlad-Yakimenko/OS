import java.util.concurrent.locks.Lock

interface FixnumLockable : Lock {

    fun getID(): Int?

    fun registerThread(): Boolean

    fun registerThread(id: Long): Boolean

    fun unregisterThread(): Boolean

    fun unregisterThread(id: Long): Boolean

    fun reset()
}