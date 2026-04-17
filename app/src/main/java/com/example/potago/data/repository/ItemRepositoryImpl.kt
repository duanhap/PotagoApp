package com.example.potago.data.repository

import com.example.potago.data.remote.api.ItemApiService
import com.example.potago.data.remote.dto.PurchaseRequest
import com.example.potago.data.remote.dto.UseItemRequest
import com.example.potago.data.remote.dto.toItem
import com.example.potago.domain.model.Item
import com.example.potago.domain.model.Result
import com.example.potago.domain.repository.ItemRepository
import javax.inject.Inject

class ItemRepositoryImpl @Inject constructor(
    private val apiService: ItemApiService
) : ItemRepository {

    override suspend fun getItems(): Result<Item> {
        return try {
            val response = apiService.getItems()
            if (response.success && response.data != null) {
                Result.Success(response.data.toItem())
            } else {
                Result.Error(response.message ?: "Unknown error")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    override suspend fun purchaseItem(itemType: String, quantity: Int): Result<Pair<Item, Int>> {
        return try {
            val response = apiService.purchaseItem(PurchaseRequest(itemType, quantity))
            if (response.success && response.data != null) {
                Result.Success(Pair(response.data.items.toItem(), response.data.diamondRemaining))
            } else {
                Result.Error(response.message ?: "Unknown error")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    override suspend fun useItem(itemType: String): Result<Item> {
        return try {
            val response = apiService.useItem(UseItemRequest(itemType))
            if (response.success && response.data != null) {
                Result.Success(response.data.toItem())
            } else {
                Result.Error(response.message ?: "Unknown error")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }
}
