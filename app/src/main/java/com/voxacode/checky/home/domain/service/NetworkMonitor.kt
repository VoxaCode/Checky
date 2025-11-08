package com.voxacode.checky.home.domain.service

import kotlinx.coroutines.flow.StateFlow

interface NetworkMonitor {
    val isOnline: StateFlow<Boolean>
    fun startMonitoringNetwork()
    fun stopMonitoringNetwork()
}