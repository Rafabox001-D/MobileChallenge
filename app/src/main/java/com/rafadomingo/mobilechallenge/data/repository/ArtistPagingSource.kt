package com.rafadomingo.mobilechallenge.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rafadomingo.mobilechallenge.data.mapper.toArtist
import com.rafadomingo.mobilechallenge.data.remote.DiscogsApi
import com.rafadomingo.mobilechallenge.domain.model.Artist

class ArtistPagingSource(
    private val api: DiscogsApi,
    private val query: String
) : PagingSource<Int, Artist>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Artist> {
        val position = params.key ?: 1
        return try {
            val response = api.searchArtists(query = query, page = position, perPage = params.loadSize)
            val artists = response.results.map { it.toArtist() }
            
            LoadResult.Page(
                data = artists,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (artists.isEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Artist>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
