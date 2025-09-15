package kaist.iclab.tracker.sensor.survey.question

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

sealed class Question<T>(
    open val question: String,
    open val isMandatory: Boolean,
    initialValue: T,
    private val questionTrigger: List<QuestionTrigger<T>>,
) {
    init {
        CoroutineScope(Dispatchers.IO).launch {
            combine(_response, _isHidden, *childQuestionIsValid.toTypedArray()) {}.collect {
                setIsValid()
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            response.collect { res ->
                questionTrigger.forEach { trigger ->
                    trigger.children.forEach { it.setIsHidden(!trigger.predicate(res)) }
                }
            }
        }
    }

    private val _response = MutableStateFlow(initialValue)
    val response = _response.asStateFlow()

    private val _isHidden = MutableStateFlow(false)
    val isHidden = _isHidden.asStateFlow()

    private val childQuestionIsValid: List<StateFlow<Boolean>> = questionTrigger.flatMap { it.children.map { q -> q.isValid } }

    private val _isValid = MutableStateFlow(false)
    val isValid = _isValid.asStateFlow()

    fun setIsHidden(isHidden: Boolean) {
        _isHidden.value = isHidden
    }

    fun setResponse(response: T) {
        if(!isAllowedResponse(response)) throw IllegalArgumentException("Invalid response value: $response")
        _response.value = response
    }

    abstract fun isAllowedResponse(response: T): Boolean
    abstract fun isEmpty(response: T): Boolean

    private fun setIsValid() {
        if(isHidden.value || !isMandatory) _isValid.value = true
        else if(isEmpty(response.value)) _isValid.value = false
        else _isValid.value = childQuestionIsValid.map { it.value }.all { it }
    }
}