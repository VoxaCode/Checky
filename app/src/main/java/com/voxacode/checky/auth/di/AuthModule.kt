package com.voxacode.checky.auth.di

import com.voxacode.checky.auth.data.repository.AuthRepositoryImpl
import com.voxacode.checky.auth.domain.repository.AuthRepository

import javax.inject.Singleton
import dagger.Module
import dagger.Provides
import dagger.Binds
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {
  
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository
}