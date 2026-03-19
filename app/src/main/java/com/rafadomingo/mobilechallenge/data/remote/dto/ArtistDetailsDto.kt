package com.rafadomingo.mobilechallenge.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ArtistDetailsDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("profile") val profile: String?,
    @SerializedName("images") val images: List<ArtistImageDto>?,
    @SerializedName("members") val members: List<ArtistMemberDto>?,
    @SerializedName("releases_url") val releasesUrl: String
)

data class ArtistImageDto(
    @SerializedName("uri") val uri: String,
    @SerializedName("resource_url") val resourceUrl: String,
    @SerializedName("type") val type: String
)

data class ArtistMemberDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("active") val active: Boolean,
    @SerializedName("thumbnail_url") val thumbnailUrl: String?
)
