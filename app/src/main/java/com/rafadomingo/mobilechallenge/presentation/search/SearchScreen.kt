package com.rafadomingo.mobilechallenge.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.*
import com.rafadomingo.mobilechallenge.R
import com.rafadomingo.mobilechallenge.domain.model.Artist
import com.rafadomingo.mobilechallenge.ui.theme.LocalDimens

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
            Column(modifier = Modifier.statusBarsPadding().padding(top = dimens.paddingLarge)) {
                TextField(
                    value = searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimens.paddingLarge)
                        .shadow(dimens.cardElevation, RoundedCornerShape(dimens.cornerRadiusExtraLarge))
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), 
                            shape = RoundedCornerShape(dimens.cornerRadiusExtraLarge)
                        ),
                    placeholder = { Text(stringResource(R.string.search_artists_placeholder), style = MaterialTheme.typography.bodyMedium) },
                    textStyle = MaterialTheme.typography.bodyLarge,
                    leadingIcon = { 
                        Icon(
                            imageVector = Icons.Default.Search, 
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface
                        ) 
                    },
                    trailingIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = dimens.paddingMedium)
                        ) {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Close, 
                                        contentDescription = stringResource(R.string.clear_search),
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Button(
                                    onClick = { viewModel.searchArtists() },
                                    shape = RoundedCornerShape(20.dp),
                                    contentPadding = PaddingValues(horizontal = dimens.paddingLarge, vertical = dimens.paddingMedium)
                                ) {
                                    Text(stringResource(R.string.search_button), style = MaterialTheme.typography.labelLarge)
                                }
                            } else if (previousSearches.isNotEmpty()) {
                                IconButton(onClick = { showHistory = true }) {
                                    Icon(
                                        imageVector = Icons.Default.List, 
                                        contentDescription = "Recent Searches",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
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
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (artists.itemCount == 0 && artists.loadState.refresh is LoadState.NotLoading) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(dimens.paddingLarge),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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
                            item { ErrorItem(message = state.error.localizedMessage ?: stringResource(R.string.error_loading_more)) }
                        }
                        is LoadState.Loading -> {
                            item { LoadingItem() }
                        }
                        else -> {}
                    }
                }
            }

            if (artists.loadState.refresh is LoadState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            if (artists.loadState.refresh is LoadState.Error) {
                val error = artists.loadState.refresh as LoadState.Error
                ErrorView(
                    message = error.error.localizedMessage ?: "An error occurred",
                    onRetry = { artists.retry() },
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        if (showHistory) {
            ModalBottomSheet(
                onDismissRequest = { showHistory = false },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                ) {
                    item {
                        Text(
                            text = "Recent Searches",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = dimens.paddingLarge, vertical = dimens.paddingMedium)
                        )
                    }
                    items(previousSearches) { search ->
                        ListItem(
                            headlineContent = { Text(search, style = MaterialTheme.typography.bodyLarge) },
                            leadingContent = { 
                                Icon(
                                    imageVector = Icons.Default.Search, 
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                ) 
                            },
                            modifier = Modifier.clickable {
                                viewModel.onSearchQueryChange(search)
                                viewModel.searchArtists()
                                showHistory = false
                            }
                        )
                    }
                }
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
            .shadow(dimens.shadowElevation, RoundedCornerShape(dimens.cornerRadiusMedium)),
        shape = RoundedCornerShape(dimens.cornerRadiusMedium),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
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
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun EmptyState() {
    val dimens = LocalDimens.current
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.music))
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
                    numVertices = 6,
                    radius = size.minDimension / 2f,
                    centerX = size.width / 2f,
                    centerY = size.height / 2f,
                    rounding = CornerRounding(size.minDimension / 3f)
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
                .size(240.dp),
            contentAlignment = Alignment.Center
        ) {
            // "Liquified" background shape using graphics-shapes
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(liquifiedShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
            )
            
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(200.dp)
            )
        }

        Spacer(modifier = Modifier.height(dimens.paddingLarge))
        Text(
            text = stringResource(R.string.discover_artists),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
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
        Text(text = message, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.error)
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
        modifier = Modifier.fillMaxWidth().padding(dimens.paddingLarge),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.error,
        textAlign = TextAlign.Center
    )
}
