package kaist.iclab.tracker.sensor.survey

data class SurveyConfig (
    val id: String,
    val minInterval: Long,
    val maxInterval: Long,
    val numSurvey: Int,
    val questions: List<Question>
)