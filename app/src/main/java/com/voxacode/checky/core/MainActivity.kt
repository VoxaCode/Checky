package com.voxacode.checky.core

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier

import com.voxacode.checky.core.theme.CheckyAppTheme
import com.voxacode.checky.core.nav.MainNavHost

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CheckyAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainNavHost()
                }
            }
        }
    }
}

