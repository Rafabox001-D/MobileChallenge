package com.rafadomingo.mobilechallenge.data.mapper

import com.rafadomingo.mobilechallenge.data.remote.dto.ArtistDetailsDto
import com.rafadomingo.mobilechallenge.data.remote.dto.ArtistReleaseDto
import com.rafadomingo.mobilechallenge.data.remote.dto.SearchResultDto
import com.rafadomingo.mobilechallenge.domain.model.Album
import com.rafadomingo.mobilechallenge.domain.model.Artist
import com.rafadomingo.mobilechallenge.domain.model.ArtistDetails
import com.rafadomingo.mobilechallenge.domain.model.ArtistMember

fun SearchResultDto.toArtist(): Artist {
    return Artist(
        id = id,
        name = title,
        thumbnail = thumb,
        coverImage = coverImage
    )
}

fun ArtistDetailsDto.toArtistDetails(): ArtistDetails {
    return ArtistDetails(
        id = id,
        name = name,
        profile = profile ?: "",
        imageUrl = images?.firstOrNull()?.uri,
        members = members?.map { 
            ArtistMember(
                id = it.id,
                name = it.name,
                isActive = it.active
            )
        },
        releasesUrl = releasesUrl
    )
}

fun ArtistReleaseDto.toAlbum(): Album {
    return Album(
        id = id,
        title = title,
        year = year,
        label = label,
        format = format,
        thumb = thumb,
        type = type
    )
}
