package com.rafadomingo.mobilechallenge.presentation.releases

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.rafadomingo.mobilechallenge.R
import com.rafadomingo.mobilechallenge.domain.model.Album
import com.rafadomingo.mobilechallenge.ui.theme.Dimens
import com.rafadomingo.mobilechallenge.ui.theme.LocalDimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReleasesScreen(
    onBackClick: () -> Unit,
    useTwoColumns: Boolean = false,
    viewModel: ReleasesViewModel = hiltViewModel()
) {
    val releases = viewModel.releases.collectAsLazyPagingItems()
    val filterYear by viewModel.filterYear.collectAsState()
    val filterType by viewModel.filterType.collectAsState()
    val dimens = LocalDimens.current

    val columns = if (useTwoColumns) 2 else 1

    // Extract available years from the current paging items
    val availableYears = remember(releases.itemCount) {
        val years = mutableSetOf<Int>()
        for (i in 0 until releases.itemCount) {
            releases[i]?.year?.let { years.add(it) }
        }
        years.toList().sortedDescending()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ReleasesTopBar(onBackClick)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            FilterSection(
                selectedYear = filterYear,
                selectedType = filterType,
                availableYears = availableYears,
                onYearChange = viewModel::setYearFilter,
                onTypeChange = viewModel::setTypeFilter
            )

            ReleasesContent(
                releases = releases,
                columns = columns,
                onRetry = { releases.retry() },
                dimens = dimens
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReleasesTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                stringResource(R.string.artist_releases_title),
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

@Composable
private fun ReleasesContent(
    releases: LazyPagingItems<Album>,
    columns: Int,
    onRetry: () -> Unit,
    dimens: Dimens
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(dimens.paddingLarge),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                count = releases.itemCount,
                // Using composite key (id + type) to prevent duplicate key crashes
                key = releases.itemKey { "${it.type}_${it.id}" },
                contentType = releases.itemContentType { "album" }
            ) { index ->
                releases[index]?.let { album ->
                    AlbumItem(album = album)
                }
            }

            if (releases.loadState.append is LoadState.Loading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(dimens.paddingLarge),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }

        if (releases.loadState.refresh is LoadState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        if (releases.loadState.refresh is LoadState.Error) {
            val error = releases.loadState.refresh as LoadState.Error
            ReleasesErrorView(
                message = error.error.localizedMessage ?: "Error",
                onRetry = onRetry,
                dimens = dimens
            )
        }
    }
}

@Composable
private fun ReleasesErrorView(
    message: String,
    onRetry: () -> Unit,
    dimens: Dimens
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(dimens.paddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSection(
    selectedYear: Int?,
    selectedType: String?,
    availableYears: List<Int>,
    onYearChange: (Int?) -> Unit,
    onTypeChange: (String?) -> Unit
) {
    val dimens = LocalDimens.current
    var yearMenuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimens.paddingLarge, vertical = dimens.paddingMedium),
        horizontalArrangement = Arrangement.spacedBy(dimens.paddingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Type Filters
        FilterChip(
            selected = selectedType == "release",
            onClick = { onTypeChange(if (selectedType == "release") null else "release") },
            label = { Text(stringResource(R.string.releases_filter), style = MaterialTheme.typography.labelMedium) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
            )
        )
        FilterChip(
            selected = selectedType == "master",
            onClick = { onTypeChange(if (selectedType == "master") null else "master") },
            label = { Text(stringResource(R.string.masters_filter), style = MaterialTheme.typography.labelMedium) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        // Year Filter with Dropdown
        Box {
            FilterChip(
                selected = selectedYear != null,
                onClick = { yearMenuExpanded = true },
                label = { 
                    Text(
                        text = selectedYear?.toString() ?: stringResource(R.string.filter_by_year),
                        style = MaterialTheme.typography.labelMedium
                    ) 
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                )
            )

            DropdownMenu(
                expanded = yearMenuExpanded,
                onDismissRequest = { yearMenuExpanded = false },
                modifier = Modifier.heightIn(max = 300.dp)
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.all_years)) },
                    onClick = {
                        onYearChange(null)
                        yearMenuExpanded = false
                    }
                )
                availableYears.forEach { year ->
                    DropdownMenuItem(
                        text = { Text(year.toString()) },
                        onClick = {
                            onYearChange(year)
                            yearMenuExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AlbumItem(album: Album) {
    val dimens = LocalDimens.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(dimens.cardElevation, RoundedCornerShape(dimens.cornerRadiusMedium)),
        shape = RoundedCornerShape(dimens.cornerRadiusMedium),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(dimens.cornerRadiusMedium)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = album.thumb?.ifEmpty { R.drawable.image_placeholder },
                contentDescription = album.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(dimens.cornerRadiusSmall)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(dimens.paddingLarge))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = album.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(dimens.paddingSmall))
                val year = album.year?.toString() ?: stringResource(R.string.no_year_placeholder)
                val label = album.label ?: stringResource(R.string.various_labels)
                Text(
                    text = "$year • $label",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                if (album.format != null) {
                    Spacer(modifier = Modifier.height(dimens.paddingSmall))
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(dimens.paddingSmall)
                    ) {
                        Text(
                            text = album.format,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
