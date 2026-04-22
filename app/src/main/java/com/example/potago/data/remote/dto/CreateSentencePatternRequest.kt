package com.example.potago.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateSentencePatternRequest(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("term_lang_code") val termLangCode: String,
    @SerializedName("def_lang_code") val defLangCode: String,
    @SerializedName("is_public") val isPublic: Boolean = false
)
