package kaist.iclab.tracker.listener

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TestListener(
    val duration: Long
): Listener<Long> {
    private val listeners = mutableListOf<(Long) -> Unit>()
    private var job: Job? = null
    override fun init() {}

    override fun addListener(listener: (Long) -> Unit) {
        listeners.add(listener)
        if(listeners.size == 1) {
            assert(job == null)
            job = CoroutineScope(Dispatchers.IO).launch {
                while(isActive) {
                    listeners.forEach { it(System.currentTimeMillis()) }
                    delay(duration)
                }
            }
        }
    }

    override fun removeListener(listener: (Long) -> Unit) {
        if(listeners.size == 0) {
            job?.cancel()
            job = null
        }
    }
}