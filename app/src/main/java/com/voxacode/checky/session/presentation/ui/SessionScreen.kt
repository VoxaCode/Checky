package com.voxacode.checky.session.presentation.ui

import kotlin.OptIn
import com.voxacode.checky.R
import com.voxacode.checky.shared.components.IconButton
import com.voxacode.checky.shared.components.MoreIconButton
import com.voxacode.checky.shared.components.NavigateBackIconButton
import com.voxacode.checky.shared.components.AppLogoWithName

import androidx.navigation.NavController
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.Composable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue 

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush

@Composable
fun SessionScreen(navController: NavController) {
    Scaffold(
        topBar = { SessionTopAppBar(onBackClick = { navController.popBackStack() }) }
    ) { padding ->
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OpponentInfo()
            ChessBoard()
            CurrentPlayerInfo()
            Spacer(modifier = Modifier.height(20.dp))
            MoveSlider()
        }
    
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionTopAppBar(
    onBackClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        navigationIcon = { NavigateBackIconButton(onClick = onBackClick) },
        title = { AppLogoAndName() },
        actions = { SessionTopAppBarActions() }
    )
}

@Composable
private fun AppLogoAndName() {
    AppLogoWithName(
        logoSize = 48.dp,
        nameSize = 24.sp,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun SessionTopAppBarActions() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.fillMaxHeight()
    ) {
        MoreIconButton(onClick = {})
    }
}

private fun Int.isEven() = this % 2 == 0

//App is only implemented for portrait mode hence
//defaulting board size to screen width 

//TODO: Refactor this shit
@Composable 
private fun ChessBoard(
    modifier: Modifier = Modifier,
    boardSize: Dp = LocalConfiguration.current.screenWidthDp.dp,
    darkCellColor: Color = MaterialTheme.colorScheme.primary,
    lightCellColor: Color = MaterialTheme.colorScheme.inversePrimary
) {
    val cellSize = boardSize / 8
    
    Column(
        modifier = Modifier
            .then(modifier)
            .size(boardSize)
    ) {
        for(col in 1..8) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cellSize)
            ) {
                for(row in 1..8) {
                    ChessBoardCell(
                        modifier = Modifier
                            .size(cellSize)
                            .background(
                                if(col.isEven() == row.isEven()) lightCellColor 
                                else darkCellColor
                            )
                    ) {
                        
                    }                           
                }
            }
        }
    }
}

@Composable
private fun ChessBoardCell(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier,
        content = content
    )
}

@Composable
private fun PlayerInfo(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()  
            .height(72.dp)
            .padding(horizontal = 16.dp)
            .then(modifier)
    ) {
       content()
    }
}

@Composable
private fun OpponentInfo(modifier: Modifier = Modifier) {
    PlayerInfo(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterStart)
        ) {
            PlayerProfile()   
            PlayerStatus()   
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterEnd)
        ) {
            PlayerTimer(true)
        }
    }
}

@Composable
private fun CurrentPlayerInfo(modifier: Modifier = Modifier) {
    PlayerInfo(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterStart)
        ) {
            PlayerTimer(false)
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterEnd)
        ) {
            PlayerStatus()   
            PlayerProfile()   
        }
    }
}

//just player name for now,
//will implement profile creation later
@Composable
private fun PlayerProfile() {
    Text("Anonymous")
}

@Composable
private fun PlayerStatus() {

}

@Composable 
private fun PlayerTimer(
    //boolean to change background color just for testing 
    white: Boolean,
    modifier: Modifier = Modifier,
    timerStyle: TextStyle = TextStyle(fontSize = 18.sp)
) { 
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if(white) Color.White else Color.Black)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .then(modifier)
    ) {
        Text(
            text = "00:00",
            style = timerStyle.copy(color = if(white) Color.Black else Color.White)
        )
    }
}

@Composable
private fun MoveSlider(
    modifier: Modifier = Modifier,
    moves: List<String> = listOf(
        "e4","e5","Nf3","e4","e5","Nf3",
        "e4","e5","Nf3","e4","e5","Nf3",
        "e4","e5","Nf3","e4","e5","Nf3",
        "e4","e5","Nf3","e4","e5","Nf3"
    )
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .then(modifier)
    ) {
        IconButton(
            iconRes = R.drawable.keyboard_arrow_left_24px,
            modifier = Modifier.weight(1.6f),
            onClick = {}
        )
       
        MovesRowWithBorderShadow(
            moves = moves,
            modifier = Modifier.weight(6.8f)
        ) 
        
        IconButton(
            iconRes = R.drawable.keyboard_arrow_right_24px,
            modifier = Modifier.weight(1.6f),
            onClick = {}
        )
    }
}

@Composable
private fun MovesRowWithBorderShadow(
    modifier: Modifier = Modifier,
    moves: List<String>
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        MovesRow(
            moves = moves,
            modifier = Modifier.fillMaxWidth()
        )
        
        MovesRowBorderShadow(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            Color.Transparent
                        )
                    )        
                ) 
        )
        
        MovesRowBorderShadow(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.surface
                        )
                    )        
                ) 
        )
    }
}

@Composable
private fun MovesRowBorderShadow(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(48.dp)
            .then(modifier)  
    )     
}


@Composable
private fun MovesRow(
    modifier: Modifier = Modifier,
    moves: List<String>
) {
    BoxWithConstraints(modifier = modifier) {
        val halfWidth = maxWidth / 2
     
        LazyRow(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Spacer(modifier = Modifier.width(halfWidth))
            }
            
            itemsIndexed(moves) { index, move ->
                MovesRowItem(
                    move = move, 
                    selected = false,
                    onClick = { }
                )     
            }
            
            item {
                Spacer(modifier = Modifier.width(halfWidth))
            }
        }
    }
}

@Composable
private fun MovesRowItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    move: String,
    selected: Boolean
) {
    Text(
        text = move,
        modifier = Modifier
            .padding(5.dp)
            .then(modifier)
    )
}

