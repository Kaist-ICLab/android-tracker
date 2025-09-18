package kaist.iclab.tracker.sensor.survey.question

class NumberQuestion(
    override val question: String,
    override val isMandatory: Boolean,
    questionTrigger: List<QuestionTrigger<Double?>>? = null
): Question<Double?>(
    question, isMandatory, null, questionTrigger
) {
    override fun isAllowedResponse(response: Double?): Boolean {
        return true
    }

    override fun isEmpty(response: Double?) = (response == null)
}