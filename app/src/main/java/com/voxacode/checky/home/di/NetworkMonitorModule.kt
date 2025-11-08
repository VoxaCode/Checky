package com.voxacode.checky.home.di

import android.net.ConnectivityManager
import android.content.Context

import javax.inject.Singleton
import dagger.Module
import dagger.Provides
import dagger.Binds
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import com.voxacode.checky.home.data.service.NetworkMonitorImpl
import com.voxacode.checky.home.domain.service.NetworkMonitor

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkMonitorModule {
   
    @Binds
    @Singleton
    abstract fun bindNetworkMonitor(
        impl: NetworkMonitorImpl
    ): NetworkMonitor
    
    companion object {
   
        @Provides
        @Singleton
        fun provideConnectivityManager(
            @ApplicationContext context: Context
        ): ConnectivityManager {
            return context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        }
    }
}
