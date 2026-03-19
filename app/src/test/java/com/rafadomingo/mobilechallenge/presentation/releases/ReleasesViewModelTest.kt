package com.rafadomingo.mobilechallenge.presentation.releases

import androidx.lifecycle.SavedStateHandle
import com.rafadomingo.mobilechallenge.domain.repository.DiscogsRepository
import io.mockk.coEvery
import io.mockk.mockk
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
class ReleasesViewModelTest {

    private lateinit var viewModel: ReleasesViewModel
    private val repository: DiscogsRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private val savedStateHandle = SavedStateHandle(mapOf("artistId" to 1))

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { repository.getArtistReleases(1) } returns flowOf()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `setYearFilter updates filterYear state`() {
        viewModel = ReleasesViewModel(repository, savedStateHandle)
        val year = 2023
        viewModel.setYearFilter(year)
        assertEquals(year, viewModel.filterYear.value)
    }

    @Test
    fun `setTypeFilter updates filterType state`() {
        viewModel = ReleasesViewModel(repository, savedStateHandle)
        val type = "master"
        viewModel.setTypeFilter(type)
        assertEquals(type, viewModel.filterType.value)
    }

    @Test
    fun `initial filters are null`() {
        viewModel = ReleasesViewModel(repository, savedStateHandle)
        assertEquals(null, viewModel.filterYear.value)
        assertEquals(null, viewModel.filterType.value)
    }
}
