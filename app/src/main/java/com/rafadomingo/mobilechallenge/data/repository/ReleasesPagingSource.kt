package com.rafadomingo.mobilechallenge.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rafadomingo.mobilechallenge.data.mapper.toAlbum
import com.rafadomingo.mobilechallenge.data.remote.DiscogsApi
import com.rafadomingo.mobilechallenge.domain.model.Album

class ReleasesPagingSource(
    private val api: DiscogsApi,
    private val artistId: Int
) : PagingSource<Int, Album>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Album> {
        val position = params.key ?: 1
        return try {
            val response = api.getArtistReleases(
                artistId = artistId,
                page = position,
                perPage = params.loadSize
            )
            val albums = response.releases.map { it.toAlbum() }
            
            LoadResult.Page(
                data = albums,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (albums.isEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Album>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
