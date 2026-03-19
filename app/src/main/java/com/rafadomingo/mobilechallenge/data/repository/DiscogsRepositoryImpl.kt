package com.rafadomingo.mobilechallenge.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rafadomingo.mobilechallenge.data.mapper.toArtistDetails
import com.rafadomingo.mobilechallenge.data.remote.DiscogsApi
import com.rafadomingo.mobilechallenge.domain.model.Album
import com.rafadomingo.mobilechallenge.domain.model.Artist
import com.rafadomingo.mobilechallenge.domain.model.ArtistDetails
import com.rafadomingo.mobilechallenge.domain.repository.DiscogsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DiscogsRepositoryImpl @Inject constructor(
    private val api: DiscogsApi
) : DiscogsRepository {

    override fun searchArtists(query: String): Flow<PagingData<Artist>> {
        return Pager(
            config = PagingConfig(pageSize = 30, enablePlaceholders = false),
            pagingSourceFactory = { ArtistPagingSource(api, query) }
        ).flow
    }

    override suspend fun getArtistDetails(artistId: Int): ArtistDetails {
        return api.getArtistDetails(artistId).toArtistDetails()
    }

    override fun getArtistReleases(artistId: Int): Flow<PagingData<Album>> {
        return Pager(
            config = PagingConfig(pageSize = 30, enablePlaceholders = false),
            pagingSourceFactory = { ReleasesPagingSource(api, artistId) }
        ).flow
    }
}
