package kaist.iclab.tracker.sensor.survey

import kaist.iclab.tracker.sensor.survey.question.Question
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class Survey(
    val question: List<Question<*>>
) {
    private val _isAnswerValid = MutableStateFlow(false)
    val isAnswerValid = _isAnswerValid.asStateFlow()

    val flatQuestions: List<Question<*>>

    init {
        this.flatQuestions = getFlatQuestionsRec(question)

        CoroutineScope(Dispatchers.IO).launch {
            combine(flatQuestions.map { it.isValid }) { q -> q.all { it }}.collect {
                _isAnswerValid.value = it
            }
        }
    }

    private fun getFlatQuestionsRec(question: List<Question<*>>): List<Question<*>> {
        return question.map { q ->
            listOf(q) + getFlatQuestionsRec(q.children)
        }.flatMap { it }
    }
}