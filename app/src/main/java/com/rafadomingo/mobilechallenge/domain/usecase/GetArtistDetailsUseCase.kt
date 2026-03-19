package com.rafadomingo.mobilechallenge.domain.usecase

import com.rafadomingo.mobilechallenge.domain.model.ArtistDetails
import com.rafadomingo.mobilechallenge.domain.repository.DiscogsRepository
import com.rafadomingo.mobilechallenge.domain.util.Resource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetArtistDetailsUseCase @Inject constructor(
    private val repository: DiscogsRepository
) {
    @Suppress("TooGenericExceptionCaught")
    operator fun invoke(artistId: Int): Flow<Resource<ArtistDetails>> = flow {
        emit(Resource.Loading)
        try {
            val details = repository.getArtistDetails(artistId)
            emit(Resource.Success(details))
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            emit(Resource.Error("Network error: ${e.localizedMessage}"))
        } catch (e: HttpException) {
            emit(Resource.Error("API error: ${e.code()}"))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Unknown error"))
        }
    }
}
