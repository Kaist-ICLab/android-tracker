package kaist.iclab.tracker.sensor.survey.question

data class QuestionTrigger<T>(
    val triggerOption: TriggerCondition<T>,
    val question: List<Question<*>>
)