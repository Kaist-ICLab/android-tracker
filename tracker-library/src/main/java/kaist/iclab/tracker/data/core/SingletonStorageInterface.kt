package kaist.iclab.tracker.data.core

import kotlinx.coroutines.flow.StateFlow

interface SingletonStorageInterface<T> {
    val stateFlow: StateFlow<T>
    fun get(): T
    fun set(value: T)
}