package com.voxacode.checky.home.presentation.components;

import com.voxacode.checky.R

import kotlin.OptIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable

import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun StartOptions(
    onJoinClick: () -> Unit,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(top = 96.dp)
    ){
        OptionButton(
            iconRes = R.drawable.door_open_24px,
            title = stringResource(id = R.string.start_options_join_button_title),
            description = stringResource(id = R.string.start_options_join_button_description),
            onClick = onJoinClick
        )
    
        Spacer(modifier = Modifier.height(8.dp))
        
        OptionButton(
            iconRes = R.drawable.handyman_24px,
            title = stringResource(id = R.string.start_options_create_button_title),
            description = stringResource(id = R.string.start_options_create_button_description),
            onClick = onCreateClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OptionButton(
    iconRes: Int,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier
            .height(140.dp)
            .width(340.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = iconRes),
                    contentDescription = "Button's Icon",
                    modifier = Modifier.size(72.dp)
                )
            }
           
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 4.dp)
                    .weight(2f)  
            ) {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = description,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }   
    }
}
