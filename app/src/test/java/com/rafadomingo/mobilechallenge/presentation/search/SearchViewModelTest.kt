package com.rafadomingo.mobilechallenge.presentation.search

import androidx.paging.PagingData
import com.rafadomingo.mobilechallenge.domain.usecase.SearchArtistsUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private lateinit var viewModel: SearchViewModel
    private val searchArtistsUseCase: SearchArtistsUseCase = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SearchViewModel(searchArtistsUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onSearchQueryChange updates searchQuery state`() {
        val query = "Nirvana"
        viewModel.onSearchQueryChange(query)
        assertEquals(query, viewModel.searchQuery.value)
    }

    @Test
    fun `searchArtists calls use case when query is not blank`() {
        val query = "Nirvana"
        viewModel.onSearchQueryChange(query)
        every { searchArtistsUseCase(query) } returns flowOf(PagingData.empty())

        viewModel.searchArtists()

        verify { searchArtistsUseCase(query) }
    }
}
