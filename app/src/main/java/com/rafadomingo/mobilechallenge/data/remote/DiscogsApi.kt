package com.rafadomingo.mobilechallenge.data.remote

import com.rafadomingo.mobilechallenge.data.remote.dto.ArtistDetailsDto
import com.rafadomingo.mobilechallenge.data.remote.dto.ArtistReleasesResponseDto
import com.rafadomingo.mobilechallenge.data.remote.dto.SearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DiscogsApi {
    @GET("database/search")
    suspend fun searchArtists(
        @Query("q") query: String,
        @Query("type") type: String = "artist",
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 30
    ): SearchResponseDto

    @GET("artists/{artist_id}")
    suspend fun getArtistDetails(
        @Path("artist_id") artistId: Int
    ): ArtistDetailsDto

    @GET("artists/{artist_id}/releases")
    suspend fun getArtistReleases(
        @Path("artist_id") artistId: Int,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 30,
        @Query("sort") sort: String = "year",
        @Query("sort_order") sortOrder: String = "desc"
    ): ArtistReleasesResponseDto

    companion object {
        const val BASE_URL = "https://api.discogs.com/"
    }
}
