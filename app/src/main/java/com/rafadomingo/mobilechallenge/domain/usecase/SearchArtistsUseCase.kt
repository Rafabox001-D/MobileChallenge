package com.rafadomingo.mobilechallenge.domain.usecase

import androidx.paging.PagingData
import com.rafadomingo.mobilechallenge.domain.model.Artist
import com.rafadomingo.mobilechallenge.domain.repository.DiscogsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchArtistsUseCase @Inject constructor(
    private val repository: DiscogsRepository
) {
    operator fun invoke(query: String): Flow<PagingData<Artist>> {
        return repository.searchArtists(query)
    }
}
