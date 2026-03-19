package com.rafadomingo.mobilechallenge.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ArtistReleasesResponseDto(
    @SerializedName("pagination") val pagination: PaginationDto,
    @SerializedName("releases") val releases: List<ArtistReleaseDto>
)

data class ArtistReleaseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("type") val type: String,
    @SerializedName("format") val format: String?,
    @SerializedName("label") val label: String?,
    @SerializedName("year") val year: Int?,
    @SerializedName("role") val role: String?,
    @SerializedName("artist") val artist: String?,
    @SerializedName("resource_url") val resourceUrl: String?,
    @SerializedName("thumb") val thumb: String?,
    @SerializedName("stats") val stats: ReleaseStatsDto?
)

data class ReleaseStatsDto(
    @SerializedName("community") val community: CommunityStatsDto?,
    @SerializedName("user") val user: UserStatsDto?
)

data class CommunityStatsDto(
    @SerializedName("want") val want: Int,
    @SerializedName("have") val have: Int
)

data class UserStatsDto(
    @SerializedName("want") val want: Int,
    @SerializedName("have") val have: Int
)
