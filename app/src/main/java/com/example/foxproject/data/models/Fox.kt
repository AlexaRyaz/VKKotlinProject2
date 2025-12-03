package com.example.foxproject.data.models

data class FoxResponse(
    val image: String,
    val link: String
)

data class Fox(
    val id: Int,
    val imageUrl: String,
    val sourceUrl: String,
    val index: Int,
    val imageWidth: Int? = null,
    val imageHeight: Int? = null
)
