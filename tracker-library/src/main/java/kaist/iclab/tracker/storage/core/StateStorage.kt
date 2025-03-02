package kaist.iclab.tracker.storage.core

import kotlinx.coroutines.flow.StateFlow

interface StateStorage<T> {
    val stateFlow: StateFlow<T>
    fun get(): T
    fun set(value: T)
}