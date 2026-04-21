package com.example.potago.presentation.screen.setting

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.potago.R
import com.example.potago.presentation.screen.UiEvent
import com.example.potago.presentation.ui.theme.Nunito

@Composable
fun SettingScreen(
    navController: NavController,
    rootNavController: NavController,
    settingViewModel: SettingViewModel = hiltViewModel()
) {
    val uiState by settingViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        settingViewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                is UiEvent.Navigate -> {
                    rootNavController.navigate(event.route) {
                        event.popUpTo?.let { popUpTo(it) { inclusive = event.inclusive } }
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SettingTopBar(onBackClick = { navController.popBackStack() })
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // ── Thông báo ─────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_notification),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Thông báo",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = uiState.notification,
                    onCheckedChange = settingViewModel::onNotificationChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF58CC02),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFE5E7EB)
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Ngôn ngữ ──────────────────────────────────────────────
            Text(
                text = "Ngôn ngữ",
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            LanguageDropdown(
                value = uiState.language,
                onValueChange = settingViewModel::onLanguageChange
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Đăng xuất ─────────────────────────────────────────────
            LogoutButton(
                text = "ĐĂNG XUẤT",
                enabled = uiState.authState !is com.example.potago.presentation.screen.UiState.Loading,
                isLoading = uiState.authState is com.example.potago.presentation.screen.UiState.Loading,
                onClick = { settingViewModel.onEvent(LogoutEvent.Submit) }
            )
        }
    }
}

@Composable
private fun SettingTopBar(onBackClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 3.dp,
        shadowElevation = 4.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton(onClick = onBackClick, modifier = Modifier)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Cài đặt",
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun LanguageDropdown(value: String, onValueChange: (String) -> Unit) {
    val languages = listOf("vi" to "Tiếng Việt", "en" to "English", "ja" to "日本語", "zh" to "中文")
    var expanded by remember { mutableStateOf(false) }
    val displayName = languages.find { it.first == value }?.second ?: value

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(Color(0xFFF9FAFB), RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
            .clickable { expanded = true }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = displayName,
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF6B7280),
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer { rotationZ = -90f },
                tint = Color(0xFF6B7280)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            languages.forEach { (code, name) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = name,
                            fontFamily = Nunito,
                            fontWeight = if (code == value) FontWeight.ExtraBold else FontWeight.Normal,
                            color = if (code == value) Color(0xFF58CC02) else Color.Black
                        )
                    },
                    onClick = { onValueChange(code); expanded = false }
                )
            }
        }
    }
}

@Composable
fun BackButton(onClick: () -> Unit, modifier: Modifier) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.85f else 1f, label = "scale")

    IconButton(onClick = onClick, interactionSource = interactionSource, modifier = modifier) {
        Icon(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = "Back",
            modifier = Modifier.graphicsLayer { scaleX = scale; scaleY = scale }
        )
    }
}

@Composable
fun LogoutButton(text: String, enabled: Boolean, isLoading: Boolean, onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(targetValue = if (isPressed) 0.96f else 1f, label = "")
    val animatedHeight by animateDpAsState(targetValue = if (isPressed) 56.dp else 53.dp, label = "")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = animatedScale; scaleY = animatedScale }
            .height(56.dp)
            .background(if (enabled) Color(0xFFFF383C) else Color(0x80FF383C), RoundedCornerShape(16.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(animatedHeight)
                .background(if (enabled) Color(0xFFFF6063) else Color(0x80FF6063), RoundedCornerShape(16.dp))
                .pointerInput(enabled) {
                    detectTapGestures(
                        onPress = { if (!enabled) return@detectTapGestures; isPressed = true; tryAwaitRelease(); isPressed = false },
                        onTap = { if (enabled) onClick() }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
            } else {
                Text(text = text, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color.White)
            }
        }
    }
}
