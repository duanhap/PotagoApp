package com.example.potago.data.remote.api

data class Pagination(
    val size: Int,
    val total: Int,
    val total_pages: Int
)

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?,
    val pagination: Pagination? = null
)
