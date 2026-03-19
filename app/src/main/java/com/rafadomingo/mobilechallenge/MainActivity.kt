package com.rafadomingo.mobilechallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.rafadomingo.mobilechallenge.presentation.details.ArtistDetailsScreen
import com.rafadomingo.mobilechallenge.presentation.releases.ReleasesScreen
import com.rafadomingo.mobilechallenge.presentation.search.SearchScreen
import com.rafadomingo.mobilechallenge.ui.theme.LocalDimens
import com.rafadomingo.mobilechallenge.ui.theme.MobileChallengeTheme
import dagger.hilt.android.AndroidEntryPoint

private const val EXTENDED_SCREEN_SEARCH_PORTION = 0.4f
private const val EXTENDED_SCREEN_DETAIL_PORTION = 0.6f


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            val isExpanded = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact

            MobileChallengeTheme {
                // Apply background to the whole content for consistency
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(isExpanded = isExpanded)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(isExpanded: Boolean) {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "search") {
        composable("search") {
            if (isExpanded) {
                TwoPaneLayout(
                    onViewReleasesClick = { artistId ->
                        navController.navigate("releases/$artistId")
                    }
                )
            } else {
                SearchScreen(
                    onArtistClick = { artistId ->
                        navController.navigate("details/$artistId")
                    }
                )
            }
        }
        
        composable(
            route = "details/{artistId}",
            arguments = listOf(navArgument("artistId") { type = NavType.IntType })
        ) {
            ArtistDetailsScreen(
                onBackClick = { navController.popBackStack() },
                onViewReleasesClick = { artistId ->
                    navController.navigate("releases/$artistId")
                }
            )
        }

        composable(
            route = "releases/{artistId}",
            arguments = listOf(navArgument("artistId") { type = NavType.IntType })
        ) {
            ReleasesScreen(
                onBackClick = { navController.popBackStack() },
                useTwoColumns = isExpanded
            )
        }
    }
}

@Composable
fun TwoPaneLayout(
    onViewReleasesClick: (Int) -> Unit
) {
    var selectedArtistId by remember { mutableStateOf<Int?>(null) }
    val dimens = LocalDimens.current

    Row(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Box(modifier = Modifier.weight(EXTENDED_SCREEN_SEARCH_PORTION)) {
            SearchScreen(
                selectedArtistId = selectedArtistId,
                onArtistClick = { artistId ->
                    selectedArtistId = artistId
                }
            )
        }
        
        VerticalDivider(
            modifier = Modifier.fillMaxHeight(),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.primary
        )

        Box(modifier = Modifier.weight(EXTENDED_SCREEN_DETAIL_PORTION)) {
            if (selectedArtistId != null) {
                ArtistDetailsScreen(
                    artistId = selectedArtistId,
                    onBackClick = { selectedArtistId = null },
                    onViewReleasesClick = onViewReleasesClick
                )
            } else {
                ArtistDetailsEmptyState()
            }
        }
    }
}

@Composable
fun ArtistDetailsEmptyState() {
    val dimens = LocalDimens.current
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.loading_details)
    )
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )
    // Define which colors to override
    val dynamicProperties = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR,
            value = MaterialTheme.colorScheme.primary.toArgb(),
            keyPath = arrayOf("**", "Fill top")
        ),
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR,
            value = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f).toArgb(),
            keyPath = arrayOf("**", "Fill bottom")
        ),
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR,
            value = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f).toArgb(),
            keyPath = arrayOf("**", "Fill inner")
        ),
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR,
            value = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f).toArgb(),
            keyPath = arrayOf("**", "Fill cloud")
        ),
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR,
            value = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f).toArgb(),
            keyPath = arrayOf("**", "Fill cloud2")
        ),
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            dynamicProperties = dynamicProperties,
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(dimens.paddingLarge))
        Text(
            text = stringResource(R.string.select_artist_empty_state),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
    }
}
