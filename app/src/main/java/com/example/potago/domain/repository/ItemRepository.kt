package com.example.potago.domain.repository

import com.example.potago.domain.model.Item
import com.example.potago.domain.model.Result

interface ItemRepository {
    suspend fun getItems(): Result<Item>
    suspend fun purchaseItem(itemType: String, quantity: Int): Result<Pair<Item, Int>> // Item + diamond remaining
    suspend fun useItem(itemType: String): Result<Item>
}
