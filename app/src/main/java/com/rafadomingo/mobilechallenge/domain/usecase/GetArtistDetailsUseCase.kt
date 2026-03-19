package com.rafadomingo.mobilechallenge.domain.usecase

import com.rafadomingo.mobilechallenge.domain.model.ArtistDetails
import com.rafadomingo.mobilechallenge.domain.repository.DiscogsRepository
import com.rafadomingo.mobilechallenge.domain.util.Resource
import com.rafadomingo.mobilechallenge.domain.util.asResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetArtistDetailsUseCase @Inject constructor(
    private val repository: DiscogsRepository
) {
    operator fun invoke(artistId: Int): Flow<Resource<ArtistDetails>> = flow {
        // We just emit the result of the suspend call
        emit(repository.getArtistDetails(artistId))
    }.asResource() // Logic for Loading/Error is now centralized here
}
