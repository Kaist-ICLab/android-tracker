package com.example.test_sync

import kotlinx.serialization.Serializable

@Serializable
data class TestData(
    val message: String,
    val value: Int,
)
