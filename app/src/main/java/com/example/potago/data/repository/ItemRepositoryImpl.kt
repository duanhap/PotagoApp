package com.example.potago.data.repository

import com.example.potago.data.local.UserDataStore
import com.example.potago.data.remote.api.ItemApiService
import com.example.potago.data.remote.dto.PurchaseRequest
import com.example.potago.data.remote.dto.UseItemRequest
import com.example.potago.data.remote.dto.toItem
import com.example.potago.domain.model.Item
import com.example.potago.domain.model.Result
import com.example.potago.domain.repository.ItemRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ItemRepositoryImpl @Inject constructor(
    private val apiService: ItemApiService,
    private val userDataStore: UserDataStore
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
                val item = response.data.items.toItem()
                val diamondRemaining = response.data.diamondRemaining
                // Cập nhật diamond trong DataStore
                userDataStore.getUser().first()?.let { user ->
                    userDataStore.saveUser(user.copy(diamond = diamondRemaining))
                }
                Result.Success(Pair(item, diamondRemaining))
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
