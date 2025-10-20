package kaist.iclab.tracker.sensor.survey

import androidx.annotation.DrawableRes

data class SurveyNotificationConfig(
    val title: String,
    val description: String,
    @param:DrawableRes val icon: Int
)
