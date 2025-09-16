package kaist.iclab.tracker.sensor.survey.activity

import androidx.activity.ComponentActivity
import kaist.iclab.tracker.sensor.survey.Survey

abstract class SurveyActivity: ComponentActivity() {
    companion object {
        lateinit var survey: Survey
        var isActive = false
    }
}