package kaist.iclab.tracker.sensor.survey.question

class TextQuestion(
    override val question: String,
    override val isMandatory: Boolean,
    questionTrigger: List<QuestionTrigger<String>>? = null
): Question<String>(
    question, isMandatory, "", questionTrigger
) {
    override fun isAllowedResponse(response: String): Boolean {
        return true
    }

    override fun isEmpty(response: String) = (response == "")
}