package com.voxacode.checky.shared.components

import com.voxacode.checky.R
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable 
fun SettingsIconButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        iconRes = R.drawable.settings_24px,
        contentDescription = "Settings",
        modifier = Modifier.size(28.dp)
    )
}
