package kaist.iclab.tracker.storage.core

import kaist.iclab.tracker.sensor.survey.SurveySchedule

interface SurveyScheduleStorage {
    fun isTodayScheduleExist(): Boolean
    fun getNextSchedule(): SurveySchedule?
    fun addSchedule(schedule: SurveySchedule)
    fun markExecuted(uuid: String)
}