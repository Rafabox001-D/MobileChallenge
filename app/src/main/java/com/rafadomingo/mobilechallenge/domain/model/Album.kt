package com.rafadomingo.mobilechallenge.domain.model

data class Album(
    val id: Int,
    val title: String,
    val year: Int?,
    val label: String?,
    val format: String?,
    val thumb: String?,
    val type: String,
    val role: String? = null
)
