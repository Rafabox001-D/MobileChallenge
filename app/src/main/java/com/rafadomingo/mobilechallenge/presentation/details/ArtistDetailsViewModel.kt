package com.rafadomingo.mobilechallenge.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafadomingo.mobilechallenge.domain.model.ArtistDetails
import com.rafadomingo.mobilechallenge.domain.usecase.GetArtistDetailsUseCase
import com.rafadomingo.mobilechallenge.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistDetailsViewModel @Inject constructor(
    private val getArtistDetailsUseCase: GetArtistDetailsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow<ArtistDetailsState>(ArtistDetailsState.Loading)
    val state: StateFlow<ArtistDetailsState> = _state.asStateFlow()

    private var currentArtistId: Int? = null

    init {
        savedStateHandle.get<Int>("artistId")?.let { id ->
            currentArtistId = id
            fetchArtistDetails(id)
        }
    }

    fun updateArtistId(artistId: Int) {
        if (currentArtistId != artistId) {
            currentArtistId = artistId
            fetchArtistDetails(artistId)
        }
    }

    fun retry() {
        currentArtistId?.let { fetchArtistDetails(it) }
    }

    private fun fetchArtistDetails(id: Int) {
        viewModelScope.launch {
            getArtistDetailsUseCase(id).collect { resource ->
                _state.value = when (resource) {
                    is Resource.Loading -> ArtistDetailsState.Loading
                    is Resource.Success -> ArtistDetailsState.Success(resource.data)
                    is Resource.Error -> ArtistDetailsState.Error(resource.message)
                }
            }
        }
    }
}

sealed interface ArtistDetailsState {
    object Loading : ArtistDetailsState
    data class Success(val details: ArtistDetails) : ArtistDetailsState
    data class Error(val message: String) : ArtistDetailsState
}
