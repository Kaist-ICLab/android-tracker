package kaist.iclab.tracker.sensor.survey

data class SurveySchedule (
    val uuid: String? = null,
    val surveyId: String,
    val triggerTime: Long,
    var isExecuted: Boolean = false,
)