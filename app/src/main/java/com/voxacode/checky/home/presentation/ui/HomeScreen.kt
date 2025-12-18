@file:OptIn(ExperimentalMaterial3Api::class)
package com.voxacode.checky.home.presentation.ui

import android.widget.Toast
import android.content.Context
import com.voxacode.checky.R 
import com.voxacode.checky.core.nav.MainRoutes
import com.voxacode.checky.shared.components.AppLogoWithName
import com.voxacode.checky.shared.components.SettingsIconButton
import com.voxacode.checky.connection.domain.model.TimeControl
import com.voxacode.checky.connection.domain.model.PlayerColor
import com.voxacode.checky.home.presentation.viewmodel.AutomaticSetupViewModel
import com.voxacode.checky.home.presentation.viewmodel.AutomaticSetupState
import com.voxacode.checky.home.presentation.viewmodel.AutomaticSetupState.SetupComplete
import com.voxacode.checky.home.presentation.viewmodel.AutomaticSetupState.SetupOngoing
import com.voxacode.checky.home.presentation.viewmodel.AutomaticSetupState.Error
import com.voxacode.checky.home.presentation.viewmodel.NetworkMonitorViewModel
import com.voxacode.checky.home.presentation.viewmodel.GameViewModel
import com.voxacode.checky.home.presentation.viewmodel.GameState
import com.voxacode.checky.home.presentation.viewmodel.JoinState

import com.voxacode.checky.home.presentation.ui.theme.CustomSegmentedButtonShapeStart
import com.voxacode.checky.home.presentation.ui.theme.CustomSegmentedButtonShapeMiddle
import com.voxacode.checky.home.presentation.ui.theme.CustomSegmentedButtonShapeEnd
import com.voxacode.checky.home.presentation.ui.theme.CustomSegmentedButtonShapeTopStart
import com.voxacode.checky.home.presentation.ui.theme.CustomSegmentedButtonShapeTopEnd
import com.voxacode.checky.home.presentation.ui.theme.CustomSegmentedButtonShapeBottomStart
import com.voxacode.checky.home.presentation.ui.theme.CustomSegmentedButtonShapeBottomEnd

import kotlin.OptIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import androidx.navigation.NavController
import androidx.activity.compose.BackHandler
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue 

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.statusBarsPadding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.TopAppBar 
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.SheetValue

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
import androidx.compose.ui.window.Popup
import androidx.compose.ui.platform.LocalContext

private enum class SheetType {
    CreateGame, JoinGame, None
}

private fun SheetType.changeSheet(newValue: SheetType): SheetType {
    return if(this == SheetType.None) newValue else this
}

private fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

@Composable
fun HomeScreen(
    navController: NavController,
    networkViewModel: NetworkMonitorViewModel = hiltViewModel(),
    setupViewModel: AutomaticSetupViewModel = hiltViewModel(),
    gameViewModel: GameViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isOnline by networkViewModel.isOnline.collectAsState()
    val setupState by setupViewModel.setupState.collectAsState()
    val gameState by gameViewModel.gameState.collectAsState()
    val joinState by gameViewModel.joinState.collectAsState()
   
    val startAutomaticSetup by remember { derivedStateOf { isOnline && setupState !is SetupComplete } }
    val showSetupSheet by remember { derivedStateOf { setupState is SetupOngoing || setupState is Error } }
    var selectedColor by rememberSaveable { mutableStateOf<PlayerColor>(PlayerColor.White) }
    var selectedTimeControl by rememberSaveable { mutableStateOf<TimeControl>(TimeControl.TenMinutes) }
    var currentSheet by rememberSaveable { mutableStateOf(SheetType.None) }
    val enabledDismissJoinSheet by remember { derivedStateOf{ joinState !is JoinState.Joining } }
    val enabledDismissCreateSheet by remember {
        derivedStateOf {
            gameState !is GameState.Creating &&
            gameState !is GameState.Deleting &&
            gameState !is GameState.DeleteError &&
            gameState !is GameState.Created 
        }
    }
    
    val initialValue = when(currentSheet) {
        SheetType.None -> SheetValue.Hidden
        else -> SheetValue.Expanded
    }
   
    val sheetState = rememberStandardBottomSheetState(
        initialValue = initialValue,
        skipHiddenState = false
    )
    
    val scope = rememberCoroutineScope()
    BackHandler(enabled = sheetState.currentValue == SheetValue.Expanded){
        when(currentSheet) {
            SheetType.None -> scope.launch { sheetState.hide() }
            SheetType.CreateGame -> if(enabledDismissCreateSheet) scope.launch { sheetState.hide() }
            SheetType.JoinGame -> if(enabledDismissJoinSheet) scope.launch { sheetState.hide() }
        }
    }
    
    BottomSheetScaffold(
        scaffoldState = 
            rememberBottomSheetScaffoldState(sheetState),
        
        topBar = { 
            Box(modifier = Modifier.fillMaxWidth()) {
                HomeTopAppBar(onSettingsClick = {})
                OfflineBanner(
                    visible = !isOnline,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        },
        
        sheetSwipeEnabled = when(currentSheet) {
            SheetType.CreateGame -> enabledDismissCreateSheet
            SheetType.JoinGame -> enabledDismissJoinSheet
            SheetType.None -> true
        },
        
        sheetPeekHeight = 0.dp,
        sheetContent = {
            when(currentSheet) {
                SheetType.None -> {}
                SheetType.CreateGame -> CreateGameSheetContent(
                    gameState = gameState,
                    enabledCreate = isOnline,
                    selectedPlayerColor = selectedColor,
                    onPlayerColorSelected = { selectedColor = it },
                    selectedTimeControl = selectedTimeControl,
                    onTimeControlSelected = { selectedTimeControl = it },
                    onDeleteRequest = { gameViewModel.deleteGame() },
                    onCreateRequest = {
                        gameViewModel.createGame(
                            playerColor = selectedColor,
                            timeControl = selectedTimeControl
                        )
                    }
                )
            
                SheetType.JoinGame -> JoinGameSheetContent(
                    joinState = joinState,
                    enabledJoin = isOnline,
                    onJoinRequest = { gameViewModel.joinGame(it) }
                )
            }
        } 
        
    ) { padding -> 
          
        StartOptions(
            enabledJoin = isOnline,
            enabledCreate = isOnline,
            onJoinClick = { currentSheet = currentSheet.changeSheet(SheetType.JoinGame) },
            onCreateClick = { currentSheet = currentSheet.changeSheet(SheetType.CreateGame) },
            modifier = Modifier.padding(padding)
        )
             
        if(showSetupSheet) AutomaticSetupSheet(
            onRetryRequest = { setupViewModel.startAutomaticSetup() },
            setupState = setupState
        )
    }
    
     
    LaunchedEffect(Unit) {
        networkViewModel.startMonitoringNetwork()
    }
        
    LaunchedEffect(startAutomaticSetup) {
        if(startAutomaticSetup) {
            setupViewModel.startAutomaticSetup()
        }
    }
    
    LaunchedEffect(currentSheet) {
        if(currentSheet  != SheetType.None) {
            sheetState.expand()
        } 
    }
    
    LaunchedEffect(sheetState) {
        snapshotFlow { sheetState.currentValue }
        .collect { value ->
            if(value == SheetValue.PartiallyExpanded) {
                currentSheet = SheetType.None
            }
        }
    }
    
    LaunchedEffect(gameState) {
        val current = gameState
        if(current is GameState.PlayerJoined) {
            sheetState.hide()
            gameViewModel.resetGameState()
            
        } else if(current is GameState.Error) {
            context.showToast(current.message)
        }
    }
    
    LaunchedEffect(joinState) {
        val current = joinState
        if(current is JoinState.Joined) {
            sheetState.hide()
            gameViewModel.resetJoinState()
                
        } else if(current is JoinState.Error) {
            context.showToast(current.message)
        }
    }
}

@Composable
private fun StartOptions(
    enabledJoin: Boolean = true,
    enabledCreate: Boolean = true,
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
            enabled = enabledJoin,
            iconRes = R.drawable.door_open_24px,
            title = stringResource(id = R.string.join_game),
            description = stringResource(id = R.string.start_options_join_button_description),
            onClick = onJoinClick
        )
    
        Spacer(modifier = Modifier.height(8.dp))
        
        StartOptionButton(
            enabled = enabledCreate,
            iconRes = R.drawable.handyman_24px,
            title = stringResource(id = R.string.create_game),
            description = stringResource(id = R.string.start_options_create_button_description),
            onClick = onCreateClick
        )
    }
}

@Composable
private fun StartOptionButton(
    enabled: Boolean = true,
    iconRes: Int,
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilledTonalButton(
        enabled = enabled,
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

@Composable
private fun HomeTopAppBar(
    modifier: Modifier = Modifier,
    onSettingsClick: () -> Unit
) {
    TopAppBar(
        title = { AppLogoAndName() },
        actions = { HomeTopAppBarActions(onSettingsClick) },
        modifier = Modifier
            .height(112.dp)
            .padding(top = 4.dp)
            .then(modifier)
    )
}
  

@Composable
private fun HomeTopAppBarActions(onSettingsClick: () -> Unit) = Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.End,
    modifier = Modifier
        .fillMaxHeight()
        .padding(end = 16.dp)
) {
   SettingsIconButton(onClick = onSettingsClick) 
}

@Composable
private fun AppLogoAndName() = AppLogoWithName(
    horizontalArrangement = Arrangement.Start,
    modifier = Modifier.fillMaxSize() 
)

@Composable
private fun JoinGameSheetContent(
    joinState: JoinState,
    enabledJoin: Boolean = true,
    onJoinRequest: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var code by rememberSaveable { mutableStateOf("") }
    val enabledChange by derivedStateOf {
        joinState !is JoinState.Joining
    }
       
    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        TextField(
            value = code,
            singleLine = true,
            enabled = enabledChange,
            onValueChange = { if(enabledChange) code = it },
            label = { Text(text = stringResource(R.string.enter_code)) },
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
            JoinGameButton(
                enabled = enabledJoin,
                joinState = joinState,
                onClick = { onJoinRequest(code) }
            )
        }
    }
}

@Composable
private fun JoinGameButton( 
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
    joinState: JoinState
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled && joinState !is JoinState.Joining
    ) {
        when(joinState) { 
            is JoinState.Joining -> JoinGameButtonJoiningStateContent()
            is JoinState.Error -> JoinGameButtonErrorStateContent()
            else -> JoinGameButtonIdleStateContent()
        }
    }
    
}

@Composable
private fun JoinGameButtonIdleStateContent(modifier: Modifier = Modifier) = Text(
    text = stringResource(R.string.join),
    modifier = modifier
)

@Composable
private fun JoinGameButtonErrorStateContent(modifier: Modifier = Modifier) = Text(
    text = stringResource(R.string.retry),
    modifier = modifier
)

@Composable
private fun JoinGameButtonJoiningStateContent(modifier: Modifier = Modifier) = Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center,
    modifier = modifier         
) {
    CircularProgressIndicator(
        strokeWidth = 2.dp,
        modifier = modifier.size(20.dp)
    )
        
    Spacer(modifier = Modifier.width(8.dp))
    Text(text = stringResource(R.string.joining))
}

@Composable
private fun CreateGameSheetContent(
    gameState: GameState,
    selectedPlayerColor: PlayerColor,
    onPlayerColorSelected: (PlayerColor) -> Unit,
    selectedTimeControl: TimeControl,
    onTimeControlSelected: (TimeControl) -> Unit,
    onCreateRequest: () -> Unit,
    onDeleteRequest: () -> Unit,
    modifier: Modifier = Modifier,
    enabledCreate: Boolean = true,
) {
    val enabledSelect by derivedStateOf { 
        gameState !is GameState.Creating &&
        gameState !is GameState.Deleting &&
        gameState !is GameState.DeleteError
    }
   
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
    ){
        PlayerColorGameOption(
            enabledSelect = enabledSelect,
            selectedPlayerColor = selectedPlayerColor,
            onPlayerColorSelected = onPlayerColorSelected
        )
            
        TimeControlGameOption(
            enabledSelect = enabledSelect,
            selectedTimeControl = selectedTimeControl,
            onTimeControlSelected = onTimeControlSelected
        )
            
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 24.dp)
        ) {
            if(gameState is GameState.Created) Text(
                fontSize = 24.sp,
                text = gameState.code.uppercase(),
                modifier = Modifier.align(Alignment.CenterStart)
            )      
                
            CreateGameButton( 
                modifier = Modifier.align(Alignment.CenterEnd),
                enabled = enabledCreate,
                gameState = gameState
            ) {
                when(gameState) {
                    is GameState.Idle -> onCreateRequest()
                    is GameState.CreateError -> onCreateRequest()
                    is GameState.Created -> onDeleteRequest()
                    is GameState.DeleteError -> onDeleteRequest()
                    else -> {}
                }
            }
        }
    }
}

@Composable
private fun CreateGameButton(
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    gameState: GameState,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled && when(gameState) {
            is GameState.Creating -> false
            is GameState.Deleting -> false
            else -> true
        }
    ) {
        when(gameState) {
            is GameState.Creating -> CreateGameButtonCreatingStateContent()
            is GameState.Created ->  CreateGameButtonCreatedStateContent()
            is GameState.Deleting -> CreateGameButtonDeletingStateContent()
            is GameState.Error -> CreateGameButtonErrorStateContent()
            else -> CreateGameButtonIdleStateContent()
        }
    }
}

@Composable
private fun CreateGameButtonIdleStateContent(modifier: Modifier = Modifier) = Text(
    text = stringResource(R.string.create),
    modifier = modifier
)

@Composable
private fun CreateGameButtonCreatingStateContent(modifier: Modifier = Modifier) = Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center,
    modifier = modifier         
) {
    CircularProgressIndicator(
        strokeWidth = 2.dp,
        modifier = modifier.size(20.dp)
    )
        
    Spacer(modifier = Modifier.width(8.dp))
    Text(text = stringResource(R.string.creating))
}

@Composable
private fun CreateGameButtonDeletingStateContent(modifier: Modifier = Modifier) = Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center,
    modifier = modifier         
) {
    CircularProgressIndicator(
        strokeWidth = 2.dp,
        modifier = modifier.size(20.dp)
    )
        
    Spacer(modifier = Modifier.width(8.dp))
    Text(text = stringResource(R.string.deleting))
}

@Composable
private fun CreateGameButtonCreatedStateContent(modifier: Modifier = Modifier) = Text(
    text = stringResource(R.string.delete),
    modifier = modifier
)

@Composable
private fun CreateGameButtonErrorStateContent(modifier: Modifier = Modifier) = Text(
    text = stringResource(R.string.retry),
    modifier = modifier
)

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
private fun PlayerColorGameOption(
    enabledSelect: Boolean = true,
    selectedPlayerColor: PlayerColor,
    onPlayerColorSelected: (PlayerColor) -> Unit,
    modifier: Modifier = Modifier
) {
    GameOption(
        modifier = modifier,
        title = stringResource(R.string.create_game_player_color_option_title)
    ) {
        SelectableTonalButtonRow {
            SelectableTonalButton(
                enabled = enabledSelect,
                onClick = { onPlayerColorSelected(PlayerColor.White) },
                selected = selectedPlayerColor is PlayerColor.White,
                text = stringResource(R.string.white),
                shape = CustomSegmentedButtonShapeStart
            )
    
            SelectableTonalButton(
                enabled = enabledSelect,
                onClick = { onPlayerColorSelected(PlayerColor.Black) },
                selected = selectedPlayerColor is PlayerColor.Black,
                text = stringResource(R.string.black),
                shape = CustomSegmentedButtonShapeEnd
            )
        }
    }
}

@Composable
private fun TimeControlGameOption(
    enabledSelect: Boolean = true,
    selectedTimeControl: TimeControl,
    onTimeControlSelected: (TimeControl) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable {
        mutableStateOf(selectedTimeControl !is TimeControl.TenMinutes) 
    }
    
    Column(modifier = modifier) {
        GameOption(
            title = stringResource(R.string.create_game_time_control_option_title)
        ) {
            SelectableTonalButtonRow {
                SelectableTonalButton(
                    enabled = enabledSelect,
                    text = stringResource(R.string.time_control_ten_minutes_label),
                    selected = selectedTimeControl is TimeControl.TenMinutes,
                    onClick = { onTimeControlSelected(TimeControl.TenMinutes) },
                    shape = CustomSegmentedButtonShapeStart
                )
    
                FilledTonalButton(
                    onClick = { expanded = !expanded },
                    shape = CustomSegmentedButtonShapeEnd,
                ) {
                    Icon(
                        contentDescription = null,
                        imageVector = ImageVector.vectorResource(
                            id = if(expanded) R.drawable.keyboard_arrow_up_24px
                                 else R.drawable.keyboard_arrow_down_24px)     
                    )
                }
            }
        }
        
        AnimatedVisibility(expanded) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SelectableTonalButtonRow(spacedBy = 8.dp) {
                    SelectableTonalButton(
                        enabled = enabledSelect,
                        text = stringResource(R.string.time_control_one_minute_label),
                        selected = selectedTimeControl is TimeControl.OneMinute,
                        onClick = { onTimeControlSelected(TimeControl.OneMinute) },
                        modifier = Modifier.weight(1f),
                        shape = CustomSegmentedButtonShapeTopStart
                    )           
                        
                    SelectableTonalButton(
                        enabled = enabledSelect,
                        text = stringResource(R.string.time_control_three_minutes_label),
                        selected = selectedTimeControl is TimeControl.ThreeMinutes,
                        onClick = { onTimeControlSelected(TimeControl.ThreeMinutes) },
                        modifier = Modifier.weight(1f),
                        shape = CustomSegmentedButtonShapeMiddle
                    )           
                    
                    SelectableTonalButton(
                        enabled = enabledSelect,
                        text = stringResource(R.string.time_control_five_minutes_label),
                        selected = selectedTimeControl is TimeControl.FiveMinutes,
                        onClick = { onTimeControlSelected(TimeControl.FiveMinutes) },
                        modifier = Modifier.weight(1f),
                        shape = CustomSegmentedButtonShapeTopEnd
                    )           
                }
                      
                SelectableTonalButtonRow(spacedBy = 8.dp) {
                    SelectableTonalButton(
                        enabled = enabledSelect,
                        text = stringResource(R.string.time_control_ten_minutes_label),
                        selected = selectedTimeControl is TimeControl.TenMinutes,
                        onClick = { onTimeControlSelected(TimeControl.TenMinutes) },
                        modifier = Modifier.weight(1f),
                        shape = CustomSegmentedButtonShapeBottomStart
                    )           
                        
                    SelectableTonalButton(
                        enabled = enabledSelect,
                        text = stringResource(R.string.time_control_twenty_minutes_label),
                        selected = selectedTimeControl is TimeControl.TwentyMinutes,
                        onClick = { onTimeControlSelected(TimeControl.TwentyMinutes) },
                        modifier = Modifier.weight(1f),
                        shape = CustomSegmentedButtonShapeMiddle
                    )           
                        
                    SelectableTonalButton(
                        enabled = enabledSelect,
                        text = stringResource(R.string.time_control_thirty_minutes_label),
                        selected = selectedTimeControl is TimeControl.ThirtyMinutes,
                        onClick = { onTimeControlSelected(TimeControl.ThirtyMinutes) },
                        modifier = Modifier.weight(1f),
                        shape = CustomSegmentedButtonShapeBottomEnd
                    )           
                }
            }
        }
    }     
}

@Composable
private fun SelectableTonalButtonRow(
    spacedBy: Dp = 4.dp,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) = Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(spacedBy),
    content = content
)

@Composable
private fun SelectableTonalButton(
    text: String,
    iconRes: Int = R.drawable.check_24px,
    onClick: () -> Unit,
    selected: Boolean,
    enabled: Boolean = true,
    shape: Shape = MaterialTheme.shapes.small,
    modifier: Modifier = Modifier
) {
     var initial by rememberSaveable { mutableStateOf(true) }
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
            Text(text) 
            Spacer(modifier = Modifier.width(4.dp))
            AnimatedVisibility(
                visible = selected,
                
                enter = if(!initial) expandHorizontally(
                    expandFrom = Alignment.Start,
                    animationSpec = tween(
                        durationMillis = 200,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn() else EnterTransition.None,
                
                exit = shrinkHorizontally(
                    shrinkTowards = Alignment.Start,
                    animationSpec = tween(
                        durationMillis = 200,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeOut()
            ){
                Icon(
                    imageVector = ImageVector.vectorResource(id = iconRes),
                    contentDescription = null
                )
            }
        }
    }
    
    LaunchedEffect(Unit) {
        if(initial) {
            initial = false
        }
    }
}

@Composable
private fun AutomaticSetupSheet(
    setupState: AutomaticSetupState,
    onRetryRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val properties = ModalBottomSheetProperties(
        shouldDismissOnBackPress = false
    )
    
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
        confirmValueChange = { false }
    )
    
    ModalBottomSheet(
        onDismissRequest = {},
        properties = properties,
        sheetState = sheetState,
        modifier = modifier 
    ) {
        if(setupState is Error) AutomaticSetupFailedSheetContent(onRetryRequest)
        else AutomaticSetupOngoingSheetContent()
    }
}

@Composable
private fun AutomaticSetupOngoingSheetContent(
    modifier: Modifier = Modifier
) = AutomaticSetupSheetContentRow(modifier = modifier) {
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

@Composable
private fun AutomaticSetupFailedSheetContent(
    onRetryRequest: () -> Unit,
    modifier: Modifier = Modifier
) = AutomaticSetupSheetContentRow(modifier = modifier) {
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
            text = stringResource(id = R.string.unexpected_error_generic),
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
            Text(text = stringResource(id = R.string.retry))
        }
    }
}

@Composable
private fun AutomaticSetupSheetContentRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) = Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
            .then(modifier)
) {
    content()
}

@Composable
private fun OfflineBanner(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = expandVertically(
                expandFrom = Alignment.Top,
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            ) + fadeIn(),
            exit = shrinkVertically(
                shrinkTowards = Alignment.Top,
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            ) + fadeOut()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceDim)
                    .statusBarsPadding()
                    .then(modifier)
            ) {
                Text(
                    text = stringResource(id = R.string.offline_banner_message),
                    fontSize = 12.sp
                )
            }
        }
    }
}