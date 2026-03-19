package com.rafadomingo.mobilechallenge.presentation.details

import androidx.lifecycle.SavedStateHandle
import com.rafadomingo.mobilechallenge.domain.model.ArtistDetails
import com.rafadomingo.mobilechallenge.domain.repository.DiscogsRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArtistDetailsViewModelTest {

    private lateinit var viewModel: ArtistDetailsViewModel
    private val repository: DiscogsRepository = mockk()
    private val savedStateHandle: SavedStateHandle = SavedStateHandle()
    private val testDispatcher = UnconfinedTestDispatcher()

    private val mockArtistDetails = ArtistDetails(
        id = 1,
        name = "Nirvana",
        profile = "Grunge band",
        imageUrl = "",
        members = emptyList(),
        releasesUrl = ""
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Loading when no artistId in savedStateHandle`() {
        viewModel = ArtistDetailsViewModel(repository, savedStateHandle)
        assertTrue(viewModel.state.value is ArtistDetailsState.Loading)
    }

    @Test
    fun `fetchArtistDetails success updates state to Success`() = runTest {
        coEvery { repository.getArtistDetails(1) } returns mockArtistDetails
        
        viewModel = ArtistDetailsViewModel(repository, savedStateHandle)
        viewModel.updateArtistId(1)

        assertEquals(ArtistDetailsState.Success(mockArtistDetails), viewModel.state.value)
    }

    @Test
    fun `fetchArtistDetails error updates state to Error`() = runTest {
        val errorMessage = "Network Error"
        coEvery { repository.getArtistDetails(1) } throws Exception(errorMessage)
        
        viewModel = ArtistDetailsViewModel(repository, savedStateHandle)
        viewModel.updateArtistId(1)

        val currentState = viewModel.state.value
        assertTrue(currentState is ArtistDetailsState.Error)
        assertEquals(errorMessage, (currentState as ArtistDetailsState.Error).message)
    }

    @Test
    fun `retry calls repository again`() = runTest {
        coEvery { repository.getArtistDetails(1) } throws Exception("First fail") andThen mockArtistDetails
        
        viewModel = ArtistDetailsViewModel(repository, savedStateHandle)
        viewModel.updateArtistId(1)
        
        assertTrue(viewModel.state.value is ArtistDetailsState.Error)
        
        viewModel.retry()
        
        assertEquals(ArtistDetailsState.Success(mockArtistDetails), viewModel.state.value)
    }
}
