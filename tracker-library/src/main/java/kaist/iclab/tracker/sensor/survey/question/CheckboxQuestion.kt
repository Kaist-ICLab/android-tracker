package kaist.iclab.tracker.sensor.survey.question

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.collections.mapOf

class CheckboxQuestion(
    override val question: String,
    override val isMandatory: Boolean,
    val option: List<Option>,
    questionTrigger: List<QuestionTrigger<Set<String>>>? = null
): Question<Set<String>>(
    question, isMandatory, setOf(), questionTrigger
) {
    private val _otherResponse = MutableStateFlow<Map<String, String>>(mapOf())
    val otherResponse = _otherResponse.asStateFlow()

    init {
        _otherResponse.value = option.filter {it.allowFreeResponse }.associate { it.value to "" }
    }

    override fun isAllowedResponse(response: Set<String>): Boolean {
        val optionValues = option.map { it.value }
        return response.all { it in optionValues }
    }

    override fun isEmpty(response: Set<String>) = response.isEmpty()

    fun toggleResponse(responseItem: String, isChecked: Boolean) {
        val newResponse = this.response.value.toMutableSet()
        newResponse.apply {
            if(isChecked) add(responseItem)
            else remove(responseItem)
        }

        setResponse(newResponse)
    }

    fun setOtherResponse(optionValue: String, response: String) {
        _otherResponse.value = otherResponse.value.toMutableMap().apply {
            this[optionValue] = response
        }
    }
}