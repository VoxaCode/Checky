package com.voxacode.checky.home.presentation.viewmodel

import com.voxacode.checky.R
import com.voxacode.checky.auth.domain.repository.AuthRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.app.Application
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

sealed class AutomaticSetupState {
    object Idle : AutomaticSetupState()
    object SetupOngoing : AutomaticSetupState()
    object SetupComplete : AutomaticSetupState()
    data class Error(val message: String) : AutomaticSetupState()
}

@HiltViewModel
class AutomaticSetupViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _setupState = MutableStateFlow<AutomaticSetupState>(
        if(isSignedIn()) AutomaticSetupState.SetupComplete
        else AutomaticSetupState.Idle
    )
    val setupState = _setupState.asStateFlow()
    
    private fun isSignedIn(): Boolean = authRepository.currentUser() != null
    fun startAutomaticSetup() {
        if(_setupState.value is AutomaticSetupState.SetupOngoing) return
        else if(_setupState.value is AutomaticSetupState.SetupComplete) return
        
        viewModelScope.launch {
            try { 
                _setupState.value = AutomaticSetupState.SetupOngoing
                delay(1000)
                authRepository.signInAnonymously()
                _setupState.value = AutomaticSetupState.SetupComplete
                
            } catch(e: Exception) {
                _setupState.value = AutomaticSetupState.Error(
                    e.message ?: context.getString(R.string.unexpected_error_body)
                )
            }
        }
    }
}
