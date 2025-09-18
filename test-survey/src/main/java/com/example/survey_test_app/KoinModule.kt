package com.example.survey_test_app

import com.example.survey_test_app.storage.CouchbaseSensorStateStorage
import com.example.survey_test_app.storage.SimpleStateStorage
import com.example.survey_test_app.ui.SurveyViewModel
import kaist.iclab.tracker.listener.SamsungHealthDataInitializer
import kaist.iclab.tracker.permission.AndroidPermissionManager
import kaist.iclab.tracker.sensor.controller.BackgroundController
import kaist.iclab.tracker.sensor.controller.ControllerState
import kaist.iclab.tracker.sensor.survey.Survey
import kaist.iclab.tracker.sensor.survey.SurveyScheduleMethod
import kaist.iclab.tracker.sensor.survey.SurveySensor
import kaist.iclab.tracker.sensor.survey.question.CheckboxQuestion
import kaist.iclab.tracker.sensor.survey.question.NumberQuestion
import kaist.iclab.tracker.sensor.survey.question.Option
import kaist.iclab.tracker.sensor.survey.question.Question
import kaist.iclab.tracker.sensor.survey.question.QuestionTrigger
import kaist.iclab.tracker.sensor.survey.question.RadioQuestion
import kaist.iclab.tracker.sensor.survey.question.TextQuestion
import kaist.iclab.tracker.storage.couchbase.CouchbaseDB
import kaist.iclab.tracker.storage.couchbase.CouchbaseStateStorage
import kaist.iclab.tracker.storage.couchbase.CouchbaseSurveyScheduleStorage
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

val koinModule = module {
    single {
        SamsungHealthDataInitializer(context = androidContext())
    }

    single {
        CouchbaseDB(context = androidContext())
    }

    single {
        AndroidPermissionManager(context = androidContext())
    }

    // Sensors
    single {
        CouchbaseSurveyScheduleStorage(
            couchbase = get(),
            collectionName = "SurveyScheduleStorage"
        )
    }
    single {
        SurveySensor(
            context = androidContext(),
            permissionManager = get<AndroidPermissionManager>(),
            configStorage = SimpleStateStorage(SurveySensor.Config(
                startTimeOfDay = TimeUnit.HOURS.toMillis(3),
                endTimeOfDay = TimeUnit.HOURS.toMillis(4),
                scheduleMethod = mapOf(
                    "test" to SurveyScheduleMethod.ESM(
                        minInterval = TimeUnit.MINUTES.toMillis(5),
                        maxInterval = TimeUnit.MINUTES.toMillis(15),
                        numSurvey = 10,
                    ),
                    "fixedTime" to SurveyScheduleMethod.Fixed(
                        timeOfDay = listOf(TimeUnit.HOURS.toMillis(15)),
                    ),
                )
            )),
            stateStorage = CouchbaseSensorStateStorage(
                couchbase = get(),
                collectionName = SurveySensor::class.simpleName ?: ""
            ),
            scheduleStorage = get<CouchbaseSurveyScheduleStorage>(),
            icon = R.drawable.ic_launcher_foreground,
            survey = mapOf(
                "test" to Survey(
                    listOf(
                        TextQuestion(
                            question = "Your name?",
                            isMandatory = true,
                        ),
                        NumberQuestion(
                            question = "Your age?",
                            isMandatory = false,
                        ),
                        RadioQuestion(
                            question = "How are you?",
                            isMandatory = true,
                            option = listOf(
                                Option("Good"),
                                Option("Bad"),
                                Option("Okay"),
                                Option("Other", displayText = "Other:", allowFreeResponse = true)
                            )
                        ),
                        CheckboxQuestion(
                            question = "Choose all even numbers",
                            isMandatory = true,
                            option = listOf(
                                Option("1"),
                                Option("2"),
                                Option("3"),
                                Option("4")
                            ),
                            questionTrigger = listOf(
                                QuestionTrigger(
                                    predicate = { it == setOf("2", "4") },
                                    children = listOf(
                                        RadioQuestion(
                                            question = "Is P = NP?",
                                            isMandatory = false,
                                            option = listOf(
                                                Option("Yes", displayText = "Hell yeah"),
                                                Option("No", displayText = "Nah")
                                            ),
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
    }

    // Global Controller
    single {
        BackgroundController(
            context = androidContext(),
            controllerStateStorage = CouchbaseStateStorage(
                couchbase = get(),
                defaultVal = ControllerState(ControllerState.FLAG.DISABLED),
                clazz = ControllerState::class.java,
                collectionName = BackgroundController::class.simpleName ?: ""
            ),
            sensors = listOf(get<SurveySensor>()),
            serviceNotification = BackgroundController.ServiceNotification(
                channelId = "BackgroundControllerService",
                channelName = "TrackerTest",
                notificationId = 1,
                title = "Tracker Test App",
                description = "Background sensor controller is running",
                icon = R.drawable.ic_launcher_foreground
            ),
            allowPartialSensing = true,
        )
    }

    single {
        SurveyDataReceiver(
            context = androidContext()
        )
    }

    // ViewModel
    viewModel {
        SurveyViewModel(
            backgroundController = get(),
            permissionManager = get<AndroidPermissionManager>(),
            surveyDataReceiver = get<SurveyDataReceiver>(),
            surveyScheduleStorage = get<CouchbaseSurveyScheduleStorage>(),
        )
    }
}