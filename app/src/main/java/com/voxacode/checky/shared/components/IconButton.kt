package com.voxacode.checky.shared.components

import kotlin.OptIn
import androidx.compose.runtime.Composable

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.ui.Modifier
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun IconButton(
    iconRes: Int,
    contentDescription: String = "Icon Button",
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = ImageVector.vectorResource(id = iconRes),
            modifier = modifier,
            contentDescription = contentDescription
        )
    }
}
