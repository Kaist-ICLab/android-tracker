package com.example.example_duty_cycling

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val koinModule = module {
    single {
        SimpleDutyCyclingManager(context = androidContext())
    }
}
