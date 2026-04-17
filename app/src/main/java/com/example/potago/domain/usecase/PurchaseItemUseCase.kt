package com.example.potago.domain.usecase

import com.example.potago.domain.model.Item
import com.example.potago.domain.model.Result
import com.example.potago.domain.repository.ItemRepository
import javax.inject.Inject

class PurchaseItemUseCase @Inject constructor(
    private val repository: ItemRepository
) {
    suspend operator fun invoke(itemType: String, quantity: Int): Result<Pair<Item, Int>> =
        repository.purchaseItem(itemType, quantity)
}
