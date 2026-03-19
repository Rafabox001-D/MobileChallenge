package com.rafadomingo.mobilechallenge.presentation.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.rafadomingo.mobilechallenge.R
import com.rafadomingo.mobilechallenge.domain.model.ArtistDetails
import com.rafadomingo.mobilechallenge.ui.theme.LocalDimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistDetailsScreen(
    artistId: Int? = null,
    onBackClick: () -> Unit,
    onViewReleasesClick: (Int) -> Unit,
    viewModel: ArtistDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val dimens = LocalDimens.current

    LaunchedEffect(artistId) {
        artistId?.let { viewModel.updateArtistId(it) }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.artist_details_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val s = state) {
                is ArtistDetailsState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ArtistDetailsState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(dimens.paddingLarge),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = s.message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(dimens.paddingMedium))
                        Button(onClick = { viewModel.retry() }) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }
                is ArtistDetailsState.Success -> {
                    ArtistDetailsContent(
                        details = s.details,
                        onViewReleasesClick = { onViewReleasesClick(s.details.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ArtistDetailsContent(
    details: ArtistDetails,
    onViewReleasesClick: () -> Unit
) {
    val dimens = LocalDimens.current
    val cleanedProfile = remember(details.profile) {
        formatDiscogsProfile(details.profile)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(dimens.paddingLarge)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(10.dp, RoundedCornerShape(dimens.cornerRadiusLarge)),
                shape = RoundedCornerShape(dimens.cornerRadiusLarge),
                elevation = CardDefaults.cardElevation(defaultElevation = dimens.cornerRadiusSmall)
            ) {
                AsyncImage(
                    model = details.imageUrl,
                    contentDescription = details.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Crop
                )
            }
            
            Spacer(modifier = Modifier.height(dimens.paddingExtraLarge))
            
            Text(
                text = details.name,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(dimens.cornerRadiusMedium))
            
            Text(
                text = cleanedProfile,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(dimens.paddingExtraLarge))
            
            Button(
                onClick = onViewReleasesClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(dimens.cardElevation, RoundedCornerShape(dimens.cornerRadiusExtraLarge)),
                shape = RoundedCornerShape(dimens.cornerRadiusExtraLarge),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(stringResource(R.string.view_discography), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(dimens.paddingHuge))
        }

        details.members?.let { members ->
            if (members.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.band_members),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(dimens.cornerRadiusMedium))
                }
                items(members) { member ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = dimens.paddingSmall),
                        shape = RoundedCornerShape(dimens.cornerRadiusMedium),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                        )
                    ) {
                        ListItem(
                            headlineContent = { 
                                Text(member.name, fontWeight = FontWeight.SemiBold) 
                            },
                            supportingContent = { 
                                Text(
                                    if (member.isActive) stringResource(R.string.active) else stringResource(R.string.past_member),
                                    color = if (member.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            },
                            leadingContent = {
                                Box(
                                    modifier = Modifier
                                        .size(dimens.iconSizeMedium)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = member.name.take(1),
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                    }
                }
            }
        }
    }
}

private fun formatDiscogsProfile(profile: String): String {
    return profile
        .replace(Regex("\\[b\\]"), "")
        .replace(Regex("\\[/b\\]"), "")
        .replace(Regex("\\[i\\]"), "")
        .replace(Regex("\\[/i\\]"), "")
        .replace(Regex("\\[[al]=([^]]+)\\]"), "$1")
        .replace(Regex("\\[/?[al]\\]"), "")
        .trim()
}
