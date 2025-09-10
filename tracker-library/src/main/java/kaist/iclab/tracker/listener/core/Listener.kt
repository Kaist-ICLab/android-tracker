package kaist.iclab.tracker.listener.core

interface Listener<T> {
    fun init()
    fun addListener(listener: (T) -> Unit)
    fun removeListener(listener: (T) -> Unit): Boolean
}