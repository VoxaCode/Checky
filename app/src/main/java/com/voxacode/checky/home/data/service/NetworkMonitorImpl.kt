package com.voxacode.checky.home.data.service

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

import android.net.Network
import android.net.NetworkRequest
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

import com.voxacode.checky.home.domain.service.NetworkMonitor
import javax.inject.Inject
import android.content.Context

class NetworkMonitorImpl @Inject constructor(
    private val cm: ConnectivityManager
) : NetworkMonitor {
    
    private val _isOnline = MutableStateFlow<Boolean>(isCurrentlyOnline())
    override val isOnline = _isOnline.asStateFlow()
    
    private var isMonitoring = false
    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) { _isOnline.value = true }
        override fun onLost(network: Network) { _isOnline.value = false }
    }
    
    private fun isCurrentlyOnline(): Boolean {
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
    
    override fun startMonitoringNetwork() {
        if(isMonitoring) return
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        
        isMonitoring = true
        cm.registerNetworkCallback(request, callback)    
    }
    
    override fun stopMonitoringNetwork() {
        if(!isMonitoring) return
        isMonitoring = false
        cm.unregisterNetworkCallback(callback)
    }
}
