package kaist.iclab.tracker.sensor.survey.question

data class TriggerCondition<T> (
    val value: T,
    val ordering: Ordering,
) {
    enum class Ordering {
        GREATER,
        LESS,
        EQUAL,
        GREATER_EQUAL,
        LESS_EQUAL
    }
}