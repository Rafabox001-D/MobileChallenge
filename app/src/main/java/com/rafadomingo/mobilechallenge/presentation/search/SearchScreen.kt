package com.rafadomingo.mobilechallenge.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.rafadomingo.mobilechallenge.R
import com.rafadomingo.mobilechallenge.domain.model.Artist
import com.rafadomingo.mobilechallenge.ui.theme.Dimens
import com.rafadomingo.mobilechallenge.ui.theme.LocalDimens

private const val SEARCH_BACKGROUND_ALPHA = 0.1f
private const val HEXAGON_VERTICES = 6
private const val HALF_DIVISOR = 2f
private const val HEXAGON_ROUNDING_DIVISOR = 3f
private const val EMPTY_STATE_BACKGROUND_ALPHA = 0.5f
private const val SECONDARY_TEXT_ALPHA = 0.6f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    selectedArtistId: Int? = null,
    onArtistClick: (Int) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val artists = viewModel.artists.collectAsLazyPagingItems()
    val previousSearches by viewModel.previousSearches.collectAsState()
    val dimens = LocalDimens.current

    var showHistory by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // Record successful searches
    LaunchedEffect(artists.loadState.refresh) {
        if (artists.loadState.refresh is LoadState.NotLoading && artists.itemCount > 0) {
            viewModel.onSearchSuccess(searchQuery)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SearchTopBar(
                searchQuery = searchQuery,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                onSearchClick = viewModel::searchArtists,
                onHistoryClick = { showHistory = true },
                hasHistory = previousSearches.isNotEmpty(),
                dimens = dimens
            )
        }
    ) { padding ->
        SearchContent(
            padding = padding,
            artists = artists,
            selectedArtistId = selectedArtistId,
            onArtistClick = onArtistClick,
            onRetry = { artists.retry() },
            dimens = dimens
        )

        if (showHistory) {
            HistoryBottomSheet(
                previousSearches = previousSearches,
                sheetState = sheetState,
                onDismiss = { showHistory = false },
                onSearchSelected = { search ->
                    viewModel.onSearchQueryChange(search)
                    viewModel.searchArtists()
                    showHistory = false
                },
                dimens = dimens
            )
        }
    }
}

@Composable
private fun SearchTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onHistoryClick: () -> Unit,
    hasHistory: Boolean,
    dimens: Dimens
) {
    Column(modifier = Modifier
        .statusBarsPadding()
        .padding(top = dimens.paddingLarge)) {
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.paddingLarge)
                .shadow(
                    dimens.cardElevation,
                    RoundedCornerShape(dimens.cornerRadiusExtraLarge)
                )
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = SEARCH_BACKGROUND_ALPHA),
                    shape = RoundedCornerShape(dimens.cornerRadiusExtraLarge)
                ),
            placeholder = {
                Text(
                    stringResource(R.string.search_artists_placeholder),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            textStyle = MaterialTheme.typography.bodyLarge,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            },
            trailingIcon = {
                SearchTrailingIcons(
                    searchQuery = searchQuery,
                    onClearClick = { onSearchQueryChange("") },
                    onSearchClick = onSearchClick,
                    onHistoryClick = onHistoryClick,
                    hasHistory = hasHistory,
                    dimens = dimens
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            shape = RoundedCornerShape(dimens.cornerRadiusExtraLarge),
            singleLine = true
        )
    }
}

@Composable
private fun SearchTrailingIcons(
    searchQuery: String,
    onClearClick: () -> Unit,
    onSearchClick: () -> Unit,
    onHistoryClick: () -> Unit,
    hasHistory: Boolean,
    dimens: Dimens
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = dimens.paddingMedium)
    ) {
        if (searchQuery.isNotEmpty()) {
            IconButton(onClick = onClearClick) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.clear_search),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Button(
                onClick = onSearchClick,
                shape = RoundedCornerShape(dimens.searchButtonCornerRadius),
                contentPadding = PaddingValues(
                    horizontal = dimens.paddingLarge,
                    vertical = dimens.paddingMedium
                )
            ) {
                Text(
                    stringResource(R.string.search_button),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        } else if (hasHistory) {
            IconButton(onClick = onHistoryClick) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "Recent Searches",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun SearchContent(
    padding: PaddingValues,
    artists: LazyPagingItems<Artist>,
    selectedArtistId: Int?,
    onArtistClick: (Int) -> Unit,
    onRetry: () -> Unit,
    dimens: Dimens
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        if (artists.itemCount == 0 && artists.loadState.refresh is LoadState.NotLoading) {
            EmptyState()
        } else {
            ArtistList(
                artists = artists,
                selectedArtistId = selectedArtistId,
                onArtistClick = onArtistClick,
                dimens = dimens
            )
        }

        if (artists.loadState.refresh is LoadState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        if (artists.loadState.refresh is LoadState.Error) {
            val error = artists.loadState.refresh as LoadState.Error
            ErrorView(
                message = error.error.localizedMessage ?: "An error occurred",
                onRetry = onRetry,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
private fun ArtistList(
    artists: LazyPagingItems<Artist>,
    selectedArtistId: Int?,
    onArtistClick: (Int) -> Unit,
    dimens: Dimens
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(dimens.paddingLarge),
        verticalArrangement = Arrangement.spacedBy(dimens.listSpacing)
    ) {
        items(
            count = artists.itemCount,
            key = artists.itemKey { it.id },
            contentType = artists.itemContentType { "artist" }
        ) { index ->
            artists[index]?.let { artist ->
                ArtistItem(
                    artist = artist,
                    isSelected = artist.id == selectedArtistId,
                    onClick = { onArtistClick(artist.id) }
                )
            }
        }

        when (val state = artists.loadState.append) {
            is LoadState.Error -> {
                item {
                    ErrorItem(
                        message = state.error.localizedMessage
                            ?: stringResource(R.string.error_loading_more)
                    )
                }
            }

            is LoadState.Loading -> {
                item { LoadingItem() }
            }

            else -> {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryBottomSheet(
    previousSearches: List<String>,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSearchSelected: (String) -> Unit,
    dimens: Dimens
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = dimens.historyBottomPadding)
        ) {
            item {
                Text(
                    text = "Recent Searches",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(
                        horizontal = dimens.paddingLarge,
                        vertical = dimens.paddingMedium
                    )
                )
            }
            items(previousSearches) { search ->
                ListItem(
                    headlineContent = {
                        Text(
                            search,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.clickable { onSearchSelected(search) }
                )
            }
        }
    }
}

@Composable
fun ArtistItem(
    artist: Artist,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    val dimens = LocalDimens.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(
                dimens.shadowElevation,
                RoundedCornerShape(dimens.cornerRadiusMedium)
            ),
        shape = RoundedCornerShape(dimens.cornerRadiusMedium),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = dimens.cardElevation)
    ) {
        Row(
            modifier = Modifier
                .padding(dimens.cornerRadiusMedium)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = artist.thumbnail.ifEmpty { R.drawable.image_placeholder },
                contentDescription = artist.name,
                modifier = Modifier
                    .size(dimens.iconSizeLarge)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(dimens.paddingLarge))
            Text(
                text = artist.name,
                style = MaterialTheme.typography.titleLarge,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun EmptyState() {
    val dimens = LocalDimens.current
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.music)
    )
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    val liquifiedShape = remember {
        object : Shape {
            override fun createOutline(
                size: Size,
                layoutDirection: LayoutDirection,
                density: Density
            ): Outline {
                val polygon = RoundedPolygon(
                    numVertices = HEXAGON_VERTICES,
                    radius = size.minDimension / HALF_DIVISOR,
                    centerX = size.width / HALF_DIVISOR,
                    centerY = size.height / HALF_DIVISOR,
                    rounding = CornerRounding(size.minDimension / HEXAGON_ROUNDING_DIVISOR)
                )
                return Outline.Generic(polygon.toPath().asComposePath())
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(dimens.emptyStateContainerSize),
            contentAlignment = Alignment.Center
        ) {
            // "Liquified" background shape using graphics-shapes
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(liquifiedShape)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(
                            alpha = EMPTY_STATE_BACKGROUND_ALPHA
                        )
                    )
            )

            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(dimens.lottieAnimationSize)
            )
        }

        Spacer(modifier = Modifier.height(dimens.paddingLarge))
        Text(
            text = stringResource(R.string.discover_artists),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = SECONDARY_TEXT_ALPHA)
        )
    }
}

@Composable
fun ErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimens = LocalDimens.current
    Column(
        modifier = modifier.padding(dimens.paddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(dimens.paddingMedium))
        Button(onClick = onRetry) {
            Text(stringResource(R.string.retry), style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun LoadingItem() {
    val dimens = LocalDimens.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimens.paddingLarge),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorItem(message: String) {
    val dimens = LocalDimens.current
    Text(
        text = message,
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimens.paddingLarge),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.error,
        textAlign = TextAlign.Center
    )
}
