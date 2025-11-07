package com.voxacode.checky.shared.components

import com.voxacode.checky.R

import kotlin.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size

import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLogoWithName(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    logoSize: Dp = 64.dp,
    nameSize: TextUnit = 32.sp
) {
    Row(
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
        modifier = modifier
    ) {
        Icon(
            imageVector =  ImageVector.vectorResource(id = R.drawable.ic_app_logo),
            modifier = Modifier.size(logoSize),
            contentDescription = null
        ) 
         
        Text(
            fontSize = nameSize,
            text = stringResource(id = R.string.app_name)
        ) 
    }
}
  