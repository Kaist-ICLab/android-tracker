package kaist.iclab.tracker.sensor.survey.activity

import androidx.activity.ComponentActivity
import kaist.iclab.tracker.sensor.survey.Survey
import kotlinx.serialization.json.JsonElement

abstract class SurveyActivity: ComponentActivity() {
    companion object {
        lateinit var survey: Survey
        lateinit var surveyCallback: (JsonElement) -> Unit
        var isActive = false
    }

    fun pushSurveyResult(result: JsonElement) {
        surveyCallback(result)
    }
}