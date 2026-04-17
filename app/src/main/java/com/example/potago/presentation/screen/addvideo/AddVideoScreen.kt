package com.example.potago.presentation.screen.addvideo

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.potago.R
import com.example.potago.presentation.screen.UiEvent
import com.example.potago.presentation.screen.auth.BigPotagoButton
import com.example.potago.presentation.ui.theme.Nunito

@Composable
fun AddVideoScreen(
    navController: NavController,
    viewModel: AddVideoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                    Log.d("AddVideoScreen", "Show snackbar: ${event.message}")
                }
                is UiEvent.Navigate -> {
                    navController.navigate(event.route)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                onBackClick = { if (!uiState.isLoading) navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding))
        Column(
            modifier = Modifier
                .background(color = Color(0xFFFFFFFF))
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(110.dp))

            // Youtube Icon Box
            Image(
                painter = painterResource(id = R.drawable.ic_youtube),
                contentDescription = "Youtube",
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Nhập nội dung",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Dán một link Youtube (có thể hỗ trợ phụ đề) để bắt đầu học video với phụ đề",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray,
                    lineHeight = 20.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Youtube Link TextField
            CustomTextField(
                value = uiState.youtubeLink,
                onValueChange = { viewModel.onYoutubeLinkChange(it) },
                placeholder = "https://youtube.com/.....",
                leadingIcon = R.drawable.ic_find_add_video_screen,
                onClear = { viewModel.onYoutubeLinkChange("") },
                readOnly = uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Divider Text
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFEEEEEE))
                Text(
                    text = "hoặc tải video của bạn",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFEEEEEE))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // File Upload Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomTextField(
                    value = uiState.filePath,
                    onValueChange = { /* Read-only */ },
                    placeholder = "content://media/.....",
                    onClear = { viewModel.onFilePathChange("") },
                    modifier = Modifier.weight(1f),
                    readOnly = true
                )
                Spacer(modifier = Modifier.width(12.dp))
                UploadButton(
                    buttonEnabled = !uiState.isLoading,
                    onClick = { viewModel.onUploadClick() }
                )

            }

            Spacer(modifier = Modifier.height(24.dp))

            // Language Selection
            CustomLanguageSpinner(
                label = "Thuật ngữ",
                selectedLanguage = uiState.termLanguage,
                onLanguageSelected = { viewModel.onTermLanguageChange(it) },
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomLanguageSpinner(
                label = "Định nghĩa",
                selectedLanguage = uiState.definitionLanguage,
                onLanguageSelected = { viewModel.onDefinitionLanguageChange(it) },
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Start Button
            val isButtonEnabled = !uiState.isLoading &&
                    (uiState.youtubeLink.isNotBlank() || uiState.filePath.isNotBlank()) &&
                    (uiState.termLanguage != uiState.definitionLanguage)

            BigPotagoButton(
                text = "BẮT ĐẦU",
                enabled = isButtonEnabled,
                isLoading = uiState.isLoading,
                onClick = { viewModel.onStartClick() }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Lớp phủ chặn thao tác khi đang loading
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
                    .pointerInput(Unit) { /* Swallow all touch events */ }
            )
        }
    }

}

@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: Int? = null,
    onClear: () -> Unit,
    readOnly: Boolean = false
) {
    var isFocused by remember { mutableStateOf(false) }

    OutlinedTextField(
        textStyle = MaterialTheme.typography.bodyLarge,
        value = value,
        onValueChange = onValueChange,
        readOnly = readOnly,
        placeholder = {
            Text(
                placeholder,
                color = Color.LightGray,
                style = MaterialTheme.typography.bodyLarge
            )
        },

        leadingIcon = {
            leadingIcon?.let {
                Icon(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    tint = if (isFocused) Color(0xFF89E219) else Color.LightGray
                )
            }
        },

        trailingIcon = {
            if (value.isNotEmpty() && !readOnly) {
                IconButton(onClick = onClear) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Clear",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },

        modifier = modifier
            .fillMaxWidth()
            .height(53.dp)
            .onFocusChanged { isFocused = it.isFocused },

        shape = RoundedCornerShape(16.dp),

        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF89E219),
            unfocusedBorderColor = Color(0xFFE5E7EB),
            focusedContainerColor = Color(0xFFFFFFFF),
            unfocusedContainerColor = Color(0xFFF9FAFB),
            unfocusedPlaceholderColor = Color.LightGray,
            focusedPlaceholderColor = Color.LightGray
        ),

        singleLine = true
    )
}
@Composable
private fun UploadButton(
    buttonEnabled: Boolean = true,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        label = ""
    )
    val animatedHeight by animateDpAsState(
        targetValue = if (isPressed) 48.dp else 45.dp,
        label = ""
    )

    Box(
        modifier = Modifier
            .width(52.dp)
            .height(48.dp)
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .border(1.2.dp, Color(0xFFE5E5E5), RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(color = Color(0xFFE5E5E5) )
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(animatedHeight)
            .clip(RoundedCornerShape(16.dp))
            .background(color = Color(0xFFFFFFFF))
            .pointerInput(buttonEnabled) {
                detectTapGestures(
                    onPress = {
                        if (!buttonEnabled) return@detectTapGestures
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = {
                        if (buttonEnabled) {
                            onClick()
                        }
                    }
                )
            }, contentAlignment = Alignment.Center){
                Icon(painter = painterResource(id = R.drawable.ic_upload), contentDescription = "Upload", tint = Color.Gray)
            }


    }
}

@Composable
private fun CustomLanguageSpinner(
    label: String,
    selectedLanguage: Language,
    onLanguageSelected: (Language) -> Unit,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val borderColor = if (expanded || isPressed) Color(0xFF89E219) else Color(0xFFE5E7EB)

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.TopStart)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(if( expanded) 2.dp else 1.5.dp,  borderColor, RoundedCornerShape(16.dp))
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        enabled = enabled
                    ) { expanded = !expanded }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = selectedLanguage.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (enabled) Color.Black else Color.Gray
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = if (expanded) Color(0xFF89E219) else Color.LightGray
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.87f) // Adjust to match width roughly
                    .background(Color.White)
            ) {
                Language.entries.forEach { language ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = language.displayName,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (selectedLanguage == language) Color(0xFF89E219) else Color.Black
                            )
                        },
                        onClick = {
                            onLanguageSelected(language)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TopAppBar(
    onBackClick: () -> Unit = {},
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 3.dp,
        shadowElevation = 4.dp,
        color = Color(0xFFFFFFFF)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically) {
            BackButton(onBackClick)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Thêm video mới",
                style = MaterialTheme.typography.displayMedium,
            )
        }
    }
}

@Composable
private fun BackButton(
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        label = "icon_scale"
    )

    IconButton(
        onClick = onClick,
        interactionSource = interactionSource
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = "Back",
            modifier = Modifier.scale(scale)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddVideoScreenPreview(){
    AddVideoScreen(navController = NavController(LocalContext.current))
}
