package com.rafadomingo.mobilechallenge.di

import com.rafadomingo.mobilechallenge.data.repository.DiscogsRepositoryImpl
import com.rafadomingo.mobilechallenge.domain.repository.DiscogsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindDiscogsRepository(
        discogsRepositoryImpl: DiscogsRepositoryImpl
    ): DiscogsRepository
}
