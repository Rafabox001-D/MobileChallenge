package com.rafadomingo.mobilechallenge.domain.usecase

import androidx.paging.PagingData
import com.rafadomingo.mobilechallenge.domain.model.Album
import com.rafadomingo.mobilechallenge.domain.repository.DiscogsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetArtistReleasesUseCase @Inject constructor(
    private val repository: DiscogsRepository
) {
    operator fun invoke(artistId: Int): Flow<PagingData<Album>> {
        return repository.getArtistReleases(artistId)
    }
}
