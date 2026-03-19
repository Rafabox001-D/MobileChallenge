package com.rafadomingo.mobilechallenge.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SearchResponseDto(
    @SerializedName("pagination") val pagination: PaginationDto,
    @SerializedName("results") val results: List<SearchResultDto>
)

data class PaginationDto(
    @SerializedName("page") val page: Int,
    @SerializedName("pages") val pages: Int,
    @SerializedName("per_page") val perPage: Int,
    @SerializedName("items") val items: Int
)

data class SearchResultDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("thumb") val thumb: String,
    @SerializedName("cover_image") val coverImage: String,
    @SerializedName("resource_url") val resourceUrl: String,
    @SerializedName("type") val type: String
)
