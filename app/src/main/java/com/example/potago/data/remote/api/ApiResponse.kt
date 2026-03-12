package com.example.potago.data.remote.api

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)