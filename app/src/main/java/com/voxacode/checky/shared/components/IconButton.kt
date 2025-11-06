package com.voxacode.checky.shared.components

import kotlin.OptIn
import androidx.compose.runtime.Composable

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun IconButton(
    iconRes: Int,
    contentDescription: String? = null,
    modifier: Modifier = Modifier.size(24.dp),
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = iconRes),
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize()
        )
    }
}
         contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize()
        )
    }
}
