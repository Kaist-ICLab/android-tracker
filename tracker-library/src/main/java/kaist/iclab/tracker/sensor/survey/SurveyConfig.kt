package kaist.iclab.tracker.sensor.survey

data class SurveyConfig (
    val id: String,
    val scheduleMethod: SurveyScheduleMethod,
    val questions: List<Question>
)