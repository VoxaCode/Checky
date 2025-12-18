package com.voxacode.checky.connection.di

import com.voxacode.checky.connection.domain.repository.GameRepository
import com.voxacode.checky.connection.data.repository.GameRepositoryImpl

import javax.inject.Singleton
import dagger.Module
import dagger.Binds
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class GameModule {

    @Binds
    @Singleton
    abstract fun bindGameRepository(
        impl: GameRepositoryImpl
    ): GameRepository
}
