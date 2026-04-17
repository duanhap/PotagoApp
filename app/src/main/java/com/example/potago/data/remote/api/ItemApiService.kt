package com.example.potago.data.remote.api

import com.example.potago.data.remote.dto.ItemDto
import com.example.potago.data.remote.dto.PurchaseRequest
import com.example.potago.data.remote.dto.PurchaseResponseData
import com.example.potago.data.remote.dto.UseItemRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ItemApiService {

    @GET("/api/users/items")
    suspend fun getItems(): ApiResponse<ItemDto>

    @POST("/api/users/items/purchase")
    suspend fun purchaseItem(
        @Body request: PurchaseRequest
    ): ApiResponse<PurchaseResponseData>

    @POST("/api/users/items/use")
    suspend fun useItem(
        @Body request: UseItemRequest
    ): ApiResponse<ItemDto>
}
