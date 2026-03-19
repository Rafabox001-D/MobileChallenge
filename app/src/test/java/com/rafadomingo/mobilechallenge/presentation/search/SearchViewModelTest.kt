package com.rafadomingo.mobilechallenge.presentation.search

import androidx.paging.PagingData
import com.rafadomingo.mobilechallenge.domain.repository.DiscogsRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private lateinit var viewModel: SearchViewModel
    private val repository: DiscogsRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SearchViewModel(repository)
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
    fun `searchArtists calls repository when query is not blank`() {
        val query = "Nirvana"
        viewModel.onSearchQueryChange(query)
        coEvery { repository.searchArtists(query) } returns flowOf(PagingData.empty())

        viewModel.searchArtists()

        io.mockk.verify { repository.searchArtists(query) }
    }
}
