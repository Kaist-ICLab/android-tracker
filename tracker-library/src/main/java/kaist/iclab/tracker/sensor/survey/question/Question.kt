package kaist.iclab.tracker.sensor.survey.question

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class Question<T>(
    val title: String,
    val value: List<T>,
    val description: List<String>?,
    val isMandatory: Boolean,
    val questionTrigger: List<QuestionTrigger<T>>
) {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            combine(_response, _isHidden, *childQuestionIsValid.toTypedArray()) {}.collect {
                setIsValid()
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            response.collect {
                TODO("Update child question visibility")
            }
        }
    }

    private val _response = MutableStateFlow<T?>(null)
    val response = _response.asStateFlow()

    private val _isHidden = MutableStateFlow(false)
    val isHidden = _isHidden.asStateFlow()

    private val childQuestionIsValid: List<StateFlow<Boolean>> = questionTrigger.flatMap { it.question.map { q -> q.isValid } }

    private val _isValid = MutableStateFlow(false)
    val isValid = _isValid.asStateFlow()

    fun setResponse(response: T) {
        _response.value = response
    }

    private fun setIsValid() {
        if(isHidden.value || !isMandatory) _isValid.value = true
        else if(response.value == null) _isValid.value = false
        else _isValid.value = childQuestionIsValid.map { it.value }.all { it }
    }
}