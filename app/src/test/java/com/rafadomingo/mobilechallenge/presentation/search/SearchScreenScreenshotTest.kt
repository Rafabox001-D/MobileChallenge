package com.rafadomingo.mobilechallenge.presentation.search

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.rafadomingo.mobilechallenge.domain.model.Artist
import com.rafadomingo.mobilechallenge.ui.theme.MobileChallengeTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchScreenScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        theme = "android:Theme.Material.Light.NoActionBar"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun artistItem_lightTheme() {
        val artist = Artist(
            id = 1,
            name = "The Beatles",
            thumbnail = ""
        )
        paparazzi.snapshot {
            MobileChallengeTheme(darkTheme = false) {
                ArtistItem(
                    artist = artist,
                    onClick = {}
                )
            }
        }
    }

    @Test
    fun artistItem_darkTheme() {
        val artist = Artist(
            id = 1,
            name = "Daft Punk",
            thumbnail = ""
        )
        paparazzi.snapshot {
            MobileChallengeTheme(darkTheme = true) {
                ArtistItem(
                    artist = artist,
                    onClick = {}
                )
            }
        }
    }

    @Test
    fun artistItem_selected() {
        val artist = Artist(
            id = 1,
            name = "Radiohead",
            thumbnail = ""
        )
        paparazzi.snapshot {
            MobileChallengeTheme(darkTheme = false) {
                ArtistItem(
                    artist = artist,
                    isSelected = true,
                    onClick = {}
                )
            }
        }
    }
}
