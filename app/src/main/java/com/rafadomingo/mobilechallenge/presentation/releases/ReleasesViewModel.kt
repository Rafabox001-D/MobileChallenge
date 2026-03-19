package com.rafadomingo.mobilechallenge.presentation.releases

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.rafadomingo.mobilechallenge.domain.model.Album
import com.rafadomingo.mobilechallenge.domain.repository.DiscogsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReleasesViewModel @Inject constructor(
    private val repository: DiscogsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val artistId: Int = checkNotNull(savedStateHandle["artistId"])

    private val _filterYear = MutableStateFlow<Int?>(null)
    val filterYear: StateFlow<Int?> = _filterYear.asStateFlow()

    private val _filterType = MutableStateFlow<String?>(null)
    val filterType: StateFlow<String?> = _filterType.asStateFlow()

    private val _pagingData = MutableStateFlow<PagingData<Album>>(PagingData.empty())
    
    val releases = combine(_pagingData, _filterYear, _filterType) { pagingData, year, type ->
        pagingData.filter { album ->
            val matchesYear = year == null || album.year == year
            val matchesType = type == null || album.type.equals(type, ignoreCase = true)
            matchesYear && matchesType
        }
    }.cachedIn(viewModelScope)

    init {
        getReleases()
    }

    private fun getReleases() {
        viewModelScope.launch {
            repository.getArtistReleases(artistId)
                .cachedIn(viewModelScope)
                .collect {
                    _pagingData.value = it
                }
        }
    }

    fun setYearFilter(year: Int?) {
        _filterYear.value = year
    }

    fun setTypeFilter(type: String?) {
        _filterType.value = type
    }
}
