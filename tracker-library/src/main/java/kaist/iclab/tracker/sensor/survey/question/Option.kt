package kaist.iclab.tracker.sensor.survey.question

data class Option(
    val value: String,
    val displayText: String? = null,
    val allowFreeResponse: Boolean = false,
)