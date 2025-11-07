package com.voxacode.checky.shared.components;

import com.voxacode.checky.R
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MoreIconButton(onClick: () -> Unit) {
    IconButton(
        iconRes = R.drawable.more_vert_24px,
        contentDescription = null,
        onClick = onClick
    )
}