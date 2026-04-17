package com.example.potago.domain.usecase

import com.example.potago.domain.model.Item
import com.example.potago.domain.model.Result
import com.example.potago.domain.repository.ItemRepository
import javax.inject.Inject

class GetItemsUseCase @Inject constructor(
    private val repository: ItemRepository
) {
    suspend operator fun invoke(): Result<Item> = repository.getItems()
}
