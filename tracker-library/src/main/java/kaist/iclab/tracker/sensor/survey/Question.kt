package kaist.iclab.tracker.sensor.survey

data class Question(
    val type: Type,
    val title: String,
    val isMandatory: Boolean,
) {
    enum class Type {
        TEXT,
        SINGLE_CHOICE,
        MULTIPLE_CHOICE
    }
}