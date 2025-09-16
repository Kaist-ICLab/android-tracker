package kaist.iclab.tracker.sensor.survey

import kaist.iclab.tracker.sensor.survey.question.Question

class Survey(
    val question: List<Question<*>>
) {
    fun getFlatQuestions(): List<Question<*>> {
        return getFlatQuestionsRec(question)
    }

    private fun getFlatQuestionsRec(question: List<Question<*>>): List<Question<*>> {
        return question.map { q ->
            listOf(q) + getFlatQuestionsRec(q.children)
        }.flatMap { it }
    }

    fun isSurveyValid() {

    }
}