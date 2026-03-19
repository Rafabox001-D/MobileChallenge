package com.rafadomingo.mobilechallenge.presentation.details

import androidx.lifecycle.SavedStateHandle
import com.rafadomingo.mobilechallenge.domain.model.ArtistDetails
import com.rafadomingo.mobilechallenge.domain.usecase.GetArtistDetailsUseCase
import com.rafadomingo.mobilechallenge.domain.util.Resource
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArtistDetailsViewModelTest {

    private lateinit var viewModel: ArtistDetailsViewModel
    private val getArtistDetailsUseCase: GetArtistDetailsUseCase = mockk()
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
        viewModel = ArtistDetailsViewModel(getArtistDetailsUseCase, savedStateHandle)
        assertTrue(viewModel.state.value is ArtistDetailsState.Loading)
    }

    @Test
    fun `fetchArtistDetails success updates state to Success`() = runTest {
        every { getArtistDetailsUseCase(1) } returns flowOf(
            Resource.Success(mockArtistDetails)
        )
        
        viewModel = ArtistDetailsViewModel(getArtistDetailsUseCase, savedStateHandle)
        viewModel.updateArtistId(1)

        assertEquals(
            ArtistDetailsState.Success(mockArtistDetails),
            viewModel.state.value
        )
    }

    @Test
    fun `fetchArtistDetails error updates state to Error`() = runTest {
        val errorMessage = "Network Error"
        every { getArtistDetailsUseCase(1) } returns flowOf(
            Resource.Error(errorMessage)
        )
        
        viewModel = ArtistDetailsViewModel(getArtistDetailsUseCase, savedStateHandle)
        viewModel.updateArtistId(1)

        val currentState = viewModel.state.value
        assertTrue(currentState is ArtistDetailsState.Error)
        assertEquals(
            errorMessage,
            (currentState as ArtistDetailsState.Error).message
        )
    }

    @Test
    fun `retry calls use case again`() = runTest {
        every { getArtistDetailsUseCase(1) } returns flowOf(
            Resource.Error("First fail")) andThen flowOf(
            Resource.Success(mockArtistDetails)
            )
        
        viewModel = ArtistDetailsViewModel(getArtistDetailsUseCase, savedStateHandle)
        viewModel.updateArtistId(1)
        
        assertTrue(viewModel.state.value is ArtistDetailsState.Error)
        
        viewModel.retry()
        
        assertEquals(
            ArtistDetailsState.Success(mockArtistDetails),
            viewModel.state.value
        )
    }
}
