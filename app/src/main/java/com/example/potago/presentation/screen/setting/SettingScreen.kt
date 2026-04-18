package com.example.potago.presentation.screen.setting

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.potago.R
import com.example.potago.presentation.navigation.Screen
import com.example.potago.presentation.screen.UiEvent
import com.example.potago.presentation.screen.potato.SettingButton

@Composable
fun SettingScreen (
    navController: NavController,
    rootNavController: NavController, // Nhận rootNavController để thoát app
    settingViewModel: SettingViewModel = hiltViewModel()
){
    val uiState by settingViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(Unit) {
        settingViewModel.uiEvent
            .collect { event ->
                when (event) {
                    is UiEvent.ShowSnackbar -> {
                        snackbarHostState.showSnackbar(event.message)
                    }
                    is UiEvent.Navigate -> {
                        // Sử dụng rootNavController để điều hướng ra ngoài MainGraph
                        rootNavController.navigate(event.route) {
                            event.popUpTo?.let {
                                popUpTo(it) {
                                    inclusive = event.inclusive
                                }
                            }
                        }
                    }
                }
            }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        topBar = {
            TopAppBar(
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->

        Surface(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(80.dp))
                LogoutButton(
                    text = "ĐĂNG XUẤT",
                    enabled = true,
                    isLoading = uiState.authState is com.example.potago.presentation.screen.UiState.Loading,
                    onClick = { settingViewModel.onEvent(LogoutEvent.Submit) }
                )
            }

        }
    }
}
@Composable
private fun TopAppBar(
    onBackClick: () -> Unit = {},
){
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 3.dp,
        shadowElevation = 4.dp,
        color = Color(0xFFFFFFFF)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {

            // ✅ Row chỉ còn Text → quyết định height
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(60.dp)) // chừa chỗ cho back button
                Text(
                    text = "Cài đặt",
                    style = MaterialTheme.typography.displayMedium,
                )
            }

            // 🔥 BackButton overlay
            Box(
                modifier = Modifier.matchParentSize()
            ) {
                BackButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .wrapContentSize()
                )
            }
        }
    }
}
@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.70f else 0.80f,
        label = "icon_scale"
    )

    IconButton(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = "Back",
            modifier = Modifier.graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
        )
    }
}
@Composable
fun LogoutButton(
    text: String,
    enabled: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        label = ""
    )
    val animatedHeight by animateDpAsState(
        targetValue = if (isPressed) 56.dp else 53.dp,
        label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .height(56.dp)
            .background(
                if (enabled) Color(0xFFFF383C) else Color(0x80FF383C),
                RoundedCornerShape(16.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(animatedHeight)
                .background(
                    if (enabled) Color(0xFFFF6063) else Color(0x80FF6063),
                    RoundedCornerShape(16.dp)
                )
                .pointerInput(enabled) {
                    detectTapGestures(
                        onPress = {
                            if (!enabled) return@detectTapGestures
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                        },
                        onTap = {
                            if (enabled) {
                                onClick()
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        }
    }
}
