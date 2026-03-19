package com.rafadomingo.mobilechallenge.domain.model

data class Artist(
    val id: Int,
    val name: String,
    val thumbnail: String,
    val coverImage: String? = null
)
