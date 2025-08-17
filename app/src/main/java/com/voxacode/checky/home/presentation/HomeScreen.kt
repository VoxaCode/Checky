package com.voxacode.checky.home.presentation;

import androidx.navigation.NavController
import androidx.compose.runtime.Composable
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

import com.voxacode.checky.home.presentation.components.TopAppBar
import com.voxacode.checky.home.presentation.components.StartOptions

import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize

@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = { TopAppBar(onSettingsClick = {}) }
    ) { padding -> 
        StartOptions(
            onJoinClick = {},
            onCreateClick = {},
            modifier = Modifier.padding(padding)
        )
    }
}
