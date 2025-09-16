package kaist.iclab.tracker.sensor.survey.question

class CheckboxQuestion(
    override val question: String,
    override val isMandatory: Boolean,
    val option: List<String>,
    val optionDisplayText: List<String>? = null,
    val isVertical: Boolean,
    questionTrigger: List<QuestionTrigger<Set<String>>>? = null
): Question<Set<String>>(
    question, isMandatory, setOf(), questionTrigger
) {
    init {
        if(optionDisplayText != null && option.size != optionDisplayText.size) {
            throw IllegalArgumentException("Option and optionDisplayText must have the same size")
        }
    }

    override fun isAllowedResponse(response: Set<String>): Boolean {
        return response.all { it in option }
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
}