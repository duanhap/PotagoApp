package com.example.potago.domain.model.reponse

import com.example.potago.domain.model.Word

data class FlashcardsResponse(
    val words: List<Word>,
    val total: Int
)