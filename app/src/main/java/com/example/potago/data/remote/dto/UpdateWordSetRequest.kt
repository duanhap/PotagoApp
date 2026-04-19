package com.example.potago.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UpdateWordSetRequest(
    @SerializedName("def_lang_code") val defLangCode: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("is_public") val isPublic: Boolean,
    @SerializedName("name") val name: String?,
    @SerializedName("term_lang_code") val termLangCode: String?
)
