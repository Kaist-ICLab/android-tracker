package kaist.iclab.tracker.sensor.survey.question

class CheckboxQuestion(
    override val question: String,
    override val isMandatory: Boolean,
    val option: List<String>,
    val optionDisplayText: List<String>?,
    val isVertical: Boolean,
    questionTrigger: List<QuestionTrigger<List<String>>>
): Question<List<String>>(
    question, isMandatory, listOf(), questionTrigger
) {
    init {
        if(optionDisplayText != null && option.size != optionDisplayText.size) {
            throw IllegalArgumentException("Option and optionDisplayText must have the same size")
        }
    }

    override fun isAllowedResponse(response: List<String>): Boolean {
        return response.all { it in option }
    }

    override fun isEmpty(response: List<String>) = response.isEmpty()

    fun toggleResponse(response: String) {
        val newResponse = this.response.value.toMutableList()
        if(response in newResponse)
            newResponse.remove(response)
        else
            newResponse.add(response)

        setResponse(newResponse)
    }
}