package com.voxacode.checky.core.nav

import com.voxacode.checky.home.presentation.ui.HomeScreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun MainNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = MainRoutes.Home.route,
        modifier = modifier
    ) {
        composable(route = MainRoutes.Home.route) { 
            HomeScreen(navController = navController)
        }
    }
}