package com.rafadomingo.mobilechallenge.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rafadomingo.mobilechallenge.domain.model.Artist
import com.rafadomingo.mobilechallenge.domain.usecase.SearchArtistsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val PERSIST_MAX_LIMIT = 10

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchArtistsUseCase: SearchArtistsUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _artists = MutableStateFlow<PagingData<Artist>>(PagingData.empty())
    val artists: StateFlow<PagingData<Artist>> = _artists.asStateFlow()

    private val _previousSearches = MutableStateFlow<List<String>>(emptyList())
    val previousSearches: StateFlow<List<String>> = _previousSearches.asStateFlow()

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun searchArtists() {
        if (_searchQuery.value.isBlank()) return
        
        viewModelScope.launch {
            searchArtistsUseCase(_searchQuery.value)
                .cachedIn(viewModelScope)
                .collectLatest {
                    _artists.value = it
                }
        }
    }

    fun onSearchSuccess(query: String) {
        if (query.isBlank()) return
        val currentList = _previousSearches.value.toMutableList()
        // Remove if exists to move it to top
        currentList.remove(query)
        currentList.add(0, query)
        if (currentList.size > PERSIST_MAX_LIMIT) {
            currentList.removeAt(currentList.size - 1)
        }
        _previousSearches.value = currentList
    }
}
