package com.voxacode.checky.home.presentation.components;

import com.voxacode.checky.R

import kotlin.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit
) {
    TopAppBar(
         modifier = modifier.height(112.dp),
         title = { AppLogoAndName() },
         actions = {
             Row(
                 verticalAlignment = Alignment.CenterVertically,
                 horizontalArrangement = Arrangement.End,
                 modifier = Modifier
                     .fillMaxHeight()
                     .padding(end = 8.dp)
             ) {
                 SettingsAction(onClick = onSettingsClick)
             }
         }
    )
}

@Composable 
private fun SettingsAction(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.settings_24px),
            contentDescription = "Settings",
            modifier = Modifier.size(36.dp)
        ) 
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable 
private fun AppLogoAndName() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxHeight()
    ) {
        Icon(
            imageVector =  ImageVector.vectorResource(id = R.drawable.ic_app_logo),
            modifier = Modifier.size(64.dp),
            contentDescription = "Checky Logo"
        ) 
        
        Text(
            text = stringResource(id = R.string.app_name)
        ) 
    }
}