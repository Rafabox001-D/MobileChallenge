package com.rafadomingo.mobilechallenge.domain.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import retrofit2.HttpException
import java.io.IOException

sealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
}

fun <T> Flow<T>.asResource(): Flow<Resource<T>> {
    return this
        .map<T, Resource<T>> { Resource.Success(it) }
        .onStart { emit(Resource.Loading) }
        .catch { e ->
            val errorMessage = when (e) {
                is IOException -> "Network error: ${e.localizedMessage}"
                is HttpException -> "API error: ${e.code()}"
                else -> e.localizedMessage ?: "Unknown error"
            }
            emit(Resource.Error(errorMessage))
        }
}
