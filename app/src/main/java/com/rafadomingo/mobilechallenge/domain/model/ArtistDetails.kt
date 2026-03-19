package com.rafadomingo.mobilechallenge.domain.model

data class ArtistDetails(
    val id: Int,
    val name: String,
    val profile: String,
    val imageUrl: String?,
    val members: List<ArtistMember>?,
    val releasesUrl: String
)

data class ArtistMember(
    val id: Int,
    val name: String,
    val isActive: Boolean
)
