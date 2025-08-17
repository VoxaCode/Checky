package com.voxacode.checky.core.nav;

sealed class MainRoutes(val route: String) {
    object Home : MainRoutes("home")
}
