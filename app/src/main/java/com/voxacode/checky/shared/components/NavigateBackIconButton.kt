package com.voxacode.checky.shared.components

import com.voxacode.checky.R
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NavigateBackIconButton(onClick: () -> Unit) {
    IconButton(
        iconRes = R.drawable.arrow_back_24px,
        contentDescription = null,
        onClick = onClick
    )
}

