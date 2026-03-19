package com.rafadomingo.mobilechallenge.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafadomingo.mobilechallenge.domain.model.ArtistDetails
import com.rafadomingo.mobilechallenge.domain.repository.DiscogsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ArtistDetailsViewModel @Inject constructor(
    private val repository: DiscogsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow<ArtistDetailsState>(ArtistDetailsState.Loading)
    val state: StateFlow<ArtistDetailsState> = _state.asStateFlow()

    private var currentArtistId: Int? = null

    init {
        // Try to initialize from SavedStateHandle if available (e.g., when navigated to via route)
        savedStateHandle.get<Int>("artistId")?.let { id ->
            currentArtistId = id
            fetchArtistDetails(id)
        }
    }

    /**
     * Updates the artist ID and fetches details if the ID has changed.
     * Useful for two-pane layouts where the ViewModel might be reused or 
     * initialized without navigation arguments.
     */
    fun updateArtistId(artistId: Int) {
        if (currentArtistId != artistId) {
            currentArtistId = artistId
            fetchArtistDetails(artistId)
        }
    }

    fun retry() {
        currentArtistId?.let { fetchArtistDetails(it) }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun fetchArtistDetails(id: Int) {
        viewModelScope.launch {
            _state.value = ArtistDetailsState.Loading
            try {
                val details = repository.getArtistDetails(id)
                _state.value = ArtistDetailsState.Success(details)
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                _state.value = ArtistDetailsState.Error("Network error: ${e.localizedMessage}")
            } catch (e: HttpException) {
                _state.value = ArtistDetailsState.Error("API error: ${e.code()}")
            } catch (e: Exception) {
                _state.value = ArtistDetailsState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}

sealed interface ArtistDetailsState {
    object Loading : ArtistDetailsState
    data class Success(val details: ArtistDetails) : ArtistDetailsState
    data class Error(val message: String) : ArtistDetailsState
}
