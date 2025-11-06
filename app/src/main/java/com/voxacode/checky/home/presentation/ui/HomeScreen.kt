package com.voxacode.checky.home.presentation.ui

import com.voxacode.checky.R 
import com.voxacode.checky.core.nav.MainRoutes
import com.voxacode.checky.shared.components.AppLogoWithName
import com.voxacode.checky.shared.components.SettingsIconButton
import com.voxacode.checky.home.presentation.viewmodel.AutomaticSetupViewModel
import com.voxacode.checky.home.presentation.viewmodel.AutomaticSetupState
import com.voxacode.checky.home.presentation.viewmodel.AutomaticSetupState.SetupComplete
import com.voxacode.checky.home.presentation.viewmodel.AutomaticSetupState.SetupOngoing
import com.voxacode.checky.home.presentation.viewmodel.AutomaticSetupState.Error
import com.voxacode.checky.home.presentation.ui.theme.CustomSegmentedButtonShapeStart
import com.voxacode.checky.home.presentation.ui.theme.CustomSegmentedButtonShapeMiddle
import com.voxacode.checky.home.presentation.ui.theme.CustomSegmentedButtonShapeEnd
import com.voxacode.checky.home.presentation.ui.theme.CustomSegmentedButtonShapeTopStart
import com.voxacode.checky.home.presentation.ui.theme.CustomSegmentedButtonShapeTopEnd
import com.voxacode.checky.home.presentation.ui.theme.CustomSegmentedButtonShapeBottomStart
import com.voxacode.checky.home.presentation.ui.theme.CustomSegmentedButtonShapeBottomEnd

import kotlin.OptIn
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue 

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.heightIn

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar 
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ModalBottomSheetProperties

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun HomeScreen(
    navController: NavController,
    setupViewModel: AutomaticSetupViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = { 
            HomeTopAppBar(
                onSettingsClick = {
                   navController.navigate(MainRoutes.Session.route) 
                }
            ) 
        }
    ) { padding -> 
        var showJoinSheet by rememberSaveable { mutableStateOf(false) }
        var showCreateSheet by rememberSaveable { mutableStateOf(false) }
        val showAutomaticSetupSheet = derivedStateOf { setupViewModel.setupState.value !is SetupComplete }
        val setupState by setupViewModel.setupState.collectAsState()
        
        StartOptions(
            onJoinClick = { showJoinSheet = true },
            onCreateClick = { showCreateSheet = true },
            modifier = Modifier.padding(padding)
        )
        
        if(showAutomaticSetupSheet.value) AutomaticSetupSheet(
            setupState = setupState,
            onRetryRequest = { setupViewModel.startAutomaticSetup() }
        ) 
      
        if(showJoinSheet) JoinGameSheet(
            onDismissRequest = { showJoinSheet = false }     
        )
        
        if(showCreateSheet) CreateGameSheet(
            onDismissRequest = { showCreateSheet = false }
        )

        LaunchedEffect(Unit) {
            if(!setupViewModel.isSignedIn()) {
                setupViewModel.startAutomaticSetup()
            }
        }
    }
}


@Composable
private fun StartOptions(
    onJoinClick: () -> Unit,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 96.dp)
            .then(modifier)
    ){
        StartOptionButton(
            iconRes = R.drawable.door_open_24px,
            title = stringResource(id = R.string.join_game),
            description = stringResource(id = R.string.start_options_join_button_description),
            onClick = onJoinClick
        )
    
        Spacer(modifier = Modifier.height(8.dp))
        
        StartOptionButton(
            iconRes = R.drawable.handyman_24px,
            title = stringResource(id = R.string.create_game),
            description = stringResource(id = R.string.start_options_create_button_description),
            onClick = onCreateClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartOptionButton(
    iconRes: Int,
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier
            .height(140.dp)
            .width(340.dp)
            .then(modifier)
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
                    contentDescription = null,
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit
) {
    TopAppBar(
         title = { AppLogoAndName() },
         actions = { HomeTopAppBarActions(onSettingsClick) },
         modifier = Modifier
             .height(112.dp)
             .then(modifier)
    )
}

@Composable
private fun HomeTopAppBarActions(onSettingsClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .fillMaxHeight()
            .padding(end = 16.dp)
    ) {
        SettingsIconButton(onClick = onSettingsClick)
    }
}

@Composable
private fun AppLogoAndName() {
    AppLogoWithName(
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxSize()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JoinGameSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(onDismissRequest = onDismissRequest) {
        var enteredCode by rememberSaveable { mutableStateOf("") }
        
        Column(
            modifier = modifier
                .fillMaxWidth()
                .then(modifier)
        ) {
            TextField(
                value = enteredCode,
                onValueChange = { enteredCode = it },
                label = { Text("Enter Code") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 24.dp)
            ) {
                Button(
                    onClick = {}
                ) {
                    Text("Join")
                }
            }
            
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateGameSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(modifier)
        ){
            PlayAsGameOption()
            TimeControlGameOption()
            
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 24.dp)
            ) {
                var enabled by rememberSaveable { mutableStateOf(true) }
                Text("CODE: ")
                Button(
                    enabled = enabled,
                    onClick = { enabled = false }
                ) {
                    Text("Create")
                }
            }
        }
    }
}

@Composable
private fun GameOption(
    modifier: Modifier = Modifier,
    title: String,
    actions: @Composable () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 16.dp)
            .then(modifier)
    ) {
        Text(title)
        actions()
    }
}

@Composable
private fun PlayAsGameOption(modifier: Modifier = Modifier) {
    GameOption(
        modifier = modifier,
        title = stringResource(id = R.string.create_game_play_as_option_title)
    ) {
        IconWithTextButtonRow {
            IconWithTextTonalButton(
                onClick = {},
                text = stringResource(id = R.string.text_black),
                shape = CustomSegmentedButtonShapeStart
            )
    
            IconWithTextTonalButton(
                onClick = {},
                text = stringResource(id = R.string.text_white),
                shape = CustomSegmentedButtonShapeEnd
            )
        }
    }
}

@Composable
private fun TimeControlGameOption(modifier: Modifier = Modifier) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    
    Column(modifier = modifier) {
        GameOption(
            title = stringResource(id = R.string.create_game_time_control_option_title)
        ) {
        
            IconWithTextButtonRow {
                IconWithTextTonalButton(
                    onClick = {},
                    text = "10 min",
                    shape = CustomSegmentedButtonShapeStart
                )
    
                IconWithTextTonalButton(
                    shape = CustomSegmentedButtonShapeEnd,
                    onClick = { expanded = !expanded },
                    iconRes = if(expanded) R.drawable.keyboard_arrow_down_24px
                              else R.drawable.keyboard_arrow_down_24px
                )
            }
        }
        
       AnimatedVisibility(expanded) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                IconWithTextButtonRow(spacedBy = 8.dp) {
                    IconWithTextTonalButton(
                        onClick = {},
                        text = "1 min",
                        modifier = Modifier.weight(1f),
                        shape = CustomSegmentedButtonShapeTopStart
                    )           
                        
                    IconWithTextTonalButton(
                        onClick = {},
                        text = "3 min",
                        modifier = Modifier.weight(1f),
                        shape = CustomSegmentedButtonShapeMiddle
                    )           
                    
                    IconWithTextTonalButton(
                        onClick = {},
                        text = "5 min",
                        modifier = Modifier.weight(1f),
                        shape = CustomSegmentedButtonShapeTopEnd
                    )           
                }
                      
                IconWithTextButtonRow(spacedBy = 8.dp) {
                    IconWithTextTonalButton(
                        onClick = {},
                        text = "10 min",
                        modifier = Modifier.weight(1f),
                        shape = CustomSegmentedButtonShapeBottomStart
                    )           
                        
                    IconWithTextTonalButton(
                        onClick = {},
                        text = "20 min",
                        modifier = Modifier.weight(1f),
                        shape = CustomSegmentedButtonShapeMiddle
                    )           
                        
                    IconWithTextTonalButton(
                        onClick = {},
                        text = "30 min",
                        modifier = Modifier.weight(1f),
                        shape = CustomSegmentedButtonShapeBottomEnd
                    )           
                }
            }
        }
    }     
}

@Composable
private fun IconWithTextButtonRow(
    spacedBy: Dp = 4.dp,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacedBy),
        content = content
    )
}

@Composable 
private fun IconWithTextTonalButton(
    iconRes: Int? = null,
    text: String? = null,
    enabled: Boolean = true,
    onClick: () -> Unit,
    shape: Shape = MaterialTheme.shapes.small,
    modifier: Modifier = Modifier
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = shape
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            iconRes?.let {
                Icon(
                    imageVector = ImageVector.vectorResource(id = iconRes),
                    contentDescription = null
                )
            }
           
            text?.let { Text(text) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AutomaticSetupSheet(
    setupState: AutomaticSetupState,
    onRetryRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = {},
        dragHandle = null,
        modifier = modifier,
        properties = ModalBottomSheetProperties(
            shouldDismissOnBackPress = false
        ),
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = false,
            confirmValueChange = { false }
        )
    ) {
        if(setupState is SetupOngoing) AutomaticSetupOngoingSheetContent()
        else if(setupState is Error) AutomaticSetupFailedSheetContent(onRetryRequest)
    }
}

@Composable
private fun AutomaticSetupOngoingSheetContent(
    modifier: Modifier = Modifier
) {
    AutomaticSetupSheetContentRow {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(2f) 
                .aspectRatio(1f)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(0.6f)
            )
        }
        
        Column(
             verticalArrangement = Arrangement.Center,
             horizontalAlignment = Alignment.Start,
             modifier = Modifier.weight(8f)
        ) {
            Text( 
                text = stringResource(id = R.string.automatic_setup_ongoing_title),
                fontSize = 24.sp
            )
            
            Text( 
                text = stringResource(id = R.string.automatic_setup_ongoin_body),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun AutomaticSetupFailedSheetContent(
    onRetryRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    AutomaticSetupSheetContentRow {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1.5f) 
                .aspectRatio(1f)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.error_24px),
                modifier = Modifier.fillMaxSize(0.9f),
                contentDescription = null
            )
        }
        
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .weight(6f)
                .padding(start= 8.dp)
        ) {
            Text( 
                text = stringResource(id = R.string.unexpected_error_title),
                fontSize = 24.sp
            )
            
            Text( 
                text = stringResource(id = R.string.unexpected_error_body),
                fontSize = 14.sp
            )
        }
        
        
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .weight(2.5f)
                .aspectRatio(1f)
        ) {
            Button(onClick = onRetryRequest) {
                Text(
                    text = stringResource(id = R.string.retry)          
                )
            }
        }
    }
}

@Composable
private fun AutomaticSetupSheetContentRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
            .then(modifier)
    ) {
        content()
    }
}tentRow {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1.5f) 
                .aspectRatio(1f)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.error_24px),
                modifier = Modifier.fillMaxSize(0.9f),
                contentDescription = null
            )
        }
        
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .weight(6f)
                .padding(start= 8.dp)
        ) {
            Text( 
                text = stringResource(id = R.string.unexpected_error_title),
                fontSize = 24.sp
            )
            
            Text( 
                text = stringResource(id = R.string.unexpected_error_body),
                fontSize = 14.sp
            )
        }
        
        
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .weight(2.5f)
                .aspectRatio(1f)
        ) {
            Button(onClick = onRetryRequest) {
                Text(
                    text = stringResource(id = R.string.retry)          
                )
            }
        }
    }
}

@Composable
private fun AutomaticSetupSheetContentRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
            .then(modifier)
    ) {
        content()
    }
>>>>>>> 22fa990 (Implemented anonymous login)
}