package kaist.iclab.tracker.sensor.survey.question

data class QuestionTrigger<T>(
    val predicate: (T) -> Boolean,
    val children: List<Question<*>>
)