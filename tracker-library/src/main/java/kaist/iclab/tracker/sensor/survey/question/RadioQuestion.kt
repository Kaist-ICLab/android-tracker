package kaist.iclab.tracker.sensor.survey.question

class RadioQuestion(
    override val question: String,
    override val isMandatory: Boolean,
    val option: List<String>,
    val optionDisplayText: List<String>?,
    val isVertical: Boolean,
    questionTrigger: List<QuestionTrigger<String>>
): Question<String>(
    question, isMandatory, "", questionTrigger
) {
    init {
        if(optionDisplayText != null && option.size != optionDisplayText.size) {
            throw IllegalArgumentException("Option and optionDisplayText must have the same size")
        }
    }

    override fun isAllowedResponse(response: String): Boolean {
        return response in option
    }

    override fun isEmpty(response: String) = (response == "")
}