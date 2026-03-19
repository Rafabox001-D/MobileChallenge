package com.rafadomingo.mobilechallenge.domain.repository

import androidx.paging.PagingData
import com.rafadomingo.mobilechallenge.domain.model.Album
import com.rafadomingo.mobilechallenge.domain.model.Artist
import com.rafadomingo.mobilechallenge.domain.model.ArtistDetails
import kotlinx.coroutines.flow.Flow

interface DiscogsRepository {
    fun searchArtists(query: String): Flow<PagingData<Artist>>
    suspend fun getArtistDetails(artistId: Int): ArtistDetails
    fun getArtistReleases(artistId: Int): Flow<PagingData<Album>>
}
