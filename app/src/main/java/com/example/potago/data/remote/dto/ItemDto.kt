package com.example.potago.data.remote.dto

import com.example.potago.domain.model.Item
import com.google.gson.annotations.SerializedName

data class ItemDto(
    @SerializedName("id") val id: Int,
    @SerializedName("water_streak") val waterStreak: Int,
    @SerializedName("super_xp") val superXp: Int,
    @SerializedName("hack_xp") val hackXp: Int,
    @SerializedName("user_id") val userId: Int
)

fun ItemDto.toItem() = Item(
    id = id,
    waterStreak = waterStreak,
    superExperience = superXp,
    hackExperience = hackXp,
    userId = userId
)

data class PurchaseRequest(
    @SerializedName("item_type") val itemType: String,
    @SerializedName("quantity") val quantity: Int
)

data class UseItemRequest(
    @SerializedName("item_type") val itemType: String
)

data class PurchaseResponseData(
    @SerializedName("items") val items: ItemDto,
    @SerializedName("diamond_remaining") val diamondRemaining: Int
)
