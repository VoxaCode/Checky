package com.voxacode.checky.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.voxacode.checky.home.domain.service.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NetworkMonitorViewModel @Inject constructor(
    private val networkMonitor: NetworkMonitor        
) : ViewModel() {

    val isOnline = networkMonitor.isOnline
    
    fun startMonitoringNetwork() {
        networkMonitor.startMonitoringNetwork()
    }
    
    fun stopMonitoringNetwork() {
        networkMonitor.stopMonitoringNetwork()
    }
}
