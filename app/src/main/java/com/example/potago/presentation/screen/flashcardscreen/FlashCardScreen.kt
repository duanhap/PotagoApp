package com.example.potago.presentation.screen.flashcardscreen

import android.speech.tts.TextToSpeech
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.potago.R
import com.example.potago.domain.model.Word
import com.example.potago.presentation.navigation.Screen
import com.example.potago.presentation.screen.UiState
import com.example.potago.presentation.screen.addvideo.Language
import com.example.potago.presentation.screen.myvideo.AddButton
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashCardScreen(
    navController: NavController,
    wordSetId: Long,
    wordSetName: String,
    viewModel: FlashCardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(wordSetId) {
        viewModel.init(wordSetId)
    }

    Scaffold(
        topBar = {
            FlashCardTopBar(
                title = "Flashcard",
                onBackClick = { navController.popBackStack() },
                onAddClick = {  }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding))
        Box() {
            FlashCardScreenContent(
                uiState = uiState,
                onSlideUpClick = {
                    navController.navigate(Screen.DetailCourse(wordSetId, wordSetName))
                },
                onNext = viewModel::onNext,
                onPrevious = viewModel::onPrevious,
                onToggleMode = viewModel::toggleMode,
                onFilterChanged = viewModel::onFilterChanged
            )

            if (uiState.isRandomSheetVisible) {
                ModalBottomSheet(
                    onDismissRequest = viewModel::dismissRandomSheet,
                    containerColor = Color.White,
                    dragHandle = { BottomSheetDefaults.DragHandle() }
                ) {
                    RandomModeBottomSheet(
                        onConfirm = viewModel::confirmRandomMode,
                        onDismiss = viewModel::dismissRandomSheet
                    )
                }
            }
        }
    }
}

@Composable
private fun FlashCardScreenContent(
    uiState: FlashCardUiState,
    onSlideUpClick: () -> Unit,
    onNext: (String) -> Unit,
    onPrevious: () -> Unit,
    onToggleMode: () -> Unit,
    onFilterChanged: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
    ) {
        Spacer(modifier = Modifier.height(90.dp))

        CustomFilterWordSpinner(
            selectedFilter = uiState.filter,
            onFilterSelected = onFilterChanged
        )
        
        val currentWord = uiState.words.getOrNull(uiState.currentIndex)
        
        // Progress based on Word's flashcardOrder to persist position on re-entry
        val progressText = if (currentWord != null) {
            "${currentWord.flashcardOrder} / ${uiState.totalWords}"
        } else if (uiState.totalWords > 0) {
            "0 / ${uiState.totalWords}"
        } else "0 / 0"

        val progressFactor = if (uiState.totalWords > 0 && currentWord != null) {
            currentWord.flashcardOrder.toFloat() / uiState.totalWords
        } else 0f

        FlashCardProgress(progressText, progressFactor)
        
        Spacer(modifier = Modifier.height(30.dp))
        
        if (uiState.uiState is UiState.Loading && uiState.words.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().height(384.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF58CC02))
            }
        } else if (currentWord != null) {
            FlashCardPanel(word = currentWord, termLangCode = uiState.termLangCode)
        } else {
            Box(modifier = Modifier.fillMaxWidth().height(384.dp), contentAlignment = Alignment.Center) {
                Text(text = "Không có dữ liệu")
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        
        FlashCardBottomActions(
            isRandomMode = uiState.mode == "random",
            onReturn = onPrevious,
            onUnknown = { onNext("unknown") },
            onKnown = { onNext("known") },
            onToggleMode = onToggleMode
        )
        
        Spacer(modifier = Modifier.height(76.dp))
        Image(
            painter = painterResource(id = R.drawable.ic_flashcard_slideup_button),
            contentDescription = "Slide up",
            modifier = Modifier
                .fillMaxWidth()
                .height(59.dp)
                .clickable(onClick = onSlideUpClick)
        )
    }
}

@Composable
fun CustomFilterWordSpinner(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val borderColor = if (expanded || isPressed) Color(0xFF89E219) else Color(0xFFE5E7EB)
    
    // Determine the display name based on the current filter string from uiState
    val currentDisplayName = StatusFlashCard.entries.find { it.filter == selectedFilter }?.displayName ?: "Tất cả"

    Box(
        modifier = Modifier.padding(horizontal = 20.dp)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.CenterEnd)

        ) {
            Row(
                modifier = Modifier
                    .height(35.dp)
                    .width(120.dp)
                    .background(
                        color = if(expanded) Color.White    else Color(0xFFF9FAFB),
                        RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 5.dp,
                            bottomStart = 20.dp,
                            bottomEnd = 5.dp
                        )
                    )
                    .border(
                        if (expanded) 2.dp else 1.5.dp,
                        borderColor,
                        RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 5.dp,
                            bottomStart = 20.dp,
                            bottomEnd = 5.dp
                        )
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                    ) { expanded = !expanded }
                    .padding(horizontal = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = currentDisplayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (expanded) Color.Black else   Color.LightGray
                )
                Spacer(modifier = Modifier.width(15.dp))
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
                    .wrapContentSize()
                    .background(Color.White)
            ) {
                StatusFlashCard.entries.forEach { status ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = status.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (selectedFilter == status.filter) Color(
                                    0xFF89E219
                                ) else Color.Black
                            )
                        },
                        onClick = {
                            onFilterSelected(status.filter)
                            expanded = false
                        }
                    )
                }
            }
        }
    }

}



@Composable
private fun FlashCardTopBar(
    title: String,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton(onBackClick)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.weight(1f),
            )
            AddButton(onAddClick)


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

@Composable
private fun FlashCardProgress(progressText: String, progressFactor: Float) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Thẻ",
                fontSize = 12.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0x80000000)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = progressText,
                fontSize = 12.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0x80000000)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Color(0xFFE5E7EB), RoundedCornerShape(999.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progressFactor.coerceIn(0f, 1f))
                    .height(8.dp)
                    .background(Color(0xFF58CC02), RoundedCornerShape(999.dp))
            )
        }
    }
}

@Composable
private fun FlashCardPanel(word: Word, termLangCode: String) {
    val context = LocalContext.current
    var tts: TextToSpeech? by remember { mutableStateOf(null) }

    DisposableEffect(Unit) {
        val ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Initialized successfully
            }
        }
        tts = ttsInstance
        onDispose {
            ttsInstance.stop()
            ttsInstance.shutdown()
        }
    }

    var rotated by remember(word.id) { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (rotated) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(384.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .pointerInput(word.id) {
                detectTapGestures(onTap = { rotated = !rotated })
            }
    ) {
        if (rotation <= 90f || rotation >= 270f) {
            // Front Side
            CardFace(
                borderColor = Color(0xFFE5E7EB),
                backgroundColor = Color.White,
                content = {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(modifier = Modifier.weight(3.5f), contentAlignment = Alignment.BottomCenter) {
                            Text(
                                text = "DỊCH TỪ NÀY",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color(0xFF9CA3AF)
                            )
                        }
                        Box(modifier = Modifier.weight(5f)) {
                            Text(
                                text = word.term,
                                style = MaterialTheme.typography.displayLarge,
                                color = Color(0xFF1F2937),
                                modifier = Modifier.padding(vertical = 20.dp)
                            )
                        }
                        Box(modifier = Modifier.weight(1.5f), contentAlignment = Alignment.TopCenter) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_flashcard_flip_button),
                                contentDescription = "Tap to flip",
                                modifier = Modifier.width(87.dp).height(20.dp)
                            )
                        }
                    }
                }
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_flashcard_speaker),
                contentDescription = "Sound",
                tint = Color(0xFF58CC02),
                modifier = Modifier
                    .size(52.dp)
                    .align(Alignment.TopEnd)
                    .padding(top = 20.dp, end = 20.dp)
                    .graphicsLayer {
                        if (rotation > 90f) rotationY = 180f
                    }
                    .clickable {
                        tts?.let {
                            it.language = Locale.forLanguageTag(termLangCode)
                            it.speak(word.term, TextToSpeech.QUEUE_FLUSH, null, null)
                        }
                    }
            )
        } else {
            // Back Side
            Box(modifier = Modifier.graphicsLayer { rotationY = 180f }.fillMaxSize()) {
                CardFace(
                    borderColor = Color(0xFFBFDBFE),
                    backgroundColor = Color(0xFFEFF6FF),
                    content = {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .align(Alignment.TopStart)
                                .padding(horizontal = 24.dp, vertical = 24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_light_bub),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Hiển thị mô tả",
                                color = Color(0xFFEAB308),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Column(
                            modifier = Modifier.fillMaxSize().padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Spacer(modifier = Modifier.weight(1f))
                            
                            Text(
                                text = word.definition,
                                style = MaterialTheme.typography.displayMedium,
                                color = Color(0xFF1D4ED8),
                                textAlign = TextAlign.Center
                            )
                            
                            if (!word.description.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = word.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF3B82F6),
                                    textAlign = TextAlign.Center,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                )
            }
        }
        

    }
}

@Composable
private fun CardFace(
    borderColor: Color,
    backgroundColor: Color,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(borderColor, RoundedCornerShape(24.dp))
            .padding(bottom = 7.dp)
            .background(backgroundColor, RoundedCornerShape(24.dp))
            .border(2.dp, borderColor, RoundedCornerShape(24.dp))
    ) {
        content()
    }
}

@Composable
private fun FlashCardBottomActions(
    isRandomMode: Boolean,
    onReturn: () -> Unit,
    onUnknown: () -> Unit,
    onKnown: () -> Unit,
    onToggleMode: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ReturnButton(
            iconRes = R.drawable.ic_return_flash_card,
            onClick = onReturn
        )
        StatusWordButton(
            colorBorder = Color(0xFFFF383C),
            colorBackground = Color(0xFFFF6063),
            modifier = Modifier.weight(1f),
            iconRes = R.drawable.ic_cancel_round,
            onClick = onUnknown
        )
        StatusWordButton(
            colorBorder = Color(0xFF46A302),
            colorBackground = Color(0xFF58CC02),
            modifier = Modifier.weight(1f),
            iconRes = R.drawable.ic_know_flash_card,
            onClick = onKnown
        )
        RandomButton(
            iconRes = R.drawable.ic_random,
            isActiveMode = isRandomMode,
            onClick = onToggleMode
        )
    }
}

@Composable
private fun StatusWordButton(
    colorBorder: Color,
    colorBackground: Color,
    modifier: Modifier,
    iconRes: Int,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(targetValue = if (isPressed) 0.96f else 1f, label = "")
    val animatedHeight by animateDpAsState(targetValue = if (isPressed) 52.dp else 48.dp, label = "")

    Box(
        modifier = modifier
            .height(52.dp)
            .graphicsLayer { scaleX = animatedScale; scaleY = animatedScale }
            .border(1.2.dp, colorBorder, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(color = colorBorder)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(animatedHeight)
                .clip(RoundedCornerShape(16.dp))
                .background(color = colorBackground)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { isPressed = true; tryAwaitRelease(); isPressed = false },
                        onTap = { onClick() }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = Color.White,
                modifier = if (iconRes == R.drawable.ic_cancel_round) Modifier.size(25.dp) else Modifier.size(43.dp)
            )
        }
    }
}

@Composable
private fun ReturnButton(
    iconRes: Int,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(targetValue = if (isPressed) 0.96f else 1f, label = "")
    val animatedHeight by animateDpAsState(targetValue = if (isPressed) 52.dp else 48.dp, label = "")

    Box(
        modifier = Modifier
            .width(56.dp)
            .height(52.dp)
            .graphicsLayer { scaleX = animatedScale; scaleY = animatedScale }
            .border(1.2.dp, Color(0xFF9CA3AF), RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(color = Color(0xFF9CA3AF))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(animatedHeight)
                .clip(RoundedCornerShape(16.dp))
                .background(color = Color(0xFFFFFFFF))
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { isPressed = true; tryAwaitRelease(); isPressed = false },
                        onTap = { onClick() }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun RandomButton(
    iconRes: Int,
    isActiveMode: Boolean,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(targetValue = if (isPressed) 0.96f else 1f, label = "")
    val animatedHeight by animateDpAsState(targetValue = if (isPressed) 52.dp else 48.dp, label = "")
    
    val colorBorder = if (isActiveMode) Color(0xFF46A302) else Color(0xFFE5E7EB)
    val colorBg = if (isActiveMode) Color(0xFFD7FFA4) else Color.White
    val tint = if (isActiveMode) Color(0xFF58CC02) else Color(0xFF9CA3AF)

    Box(
        modifier = Modifier
            .width(56.dp)
            .height(52.dp)
            .graphicsLayer { scaleX = animatedScale; scaleY = animatedScale }
            .border(1.2.dp, colorBorder, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(color = colorBorder)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(animatedHeight)
                .clip(RoundedCornerShape(16.dp))
                .background(color = colorBg)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { isPressed = true; tryAwaitRelease(); isPressed = false },
                        onTap = { onClick() }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun RandomModeBottomSheet(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MascotAndBubble(text = "Học lại đó chịu ko !?")
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Với chế độ ngẫu nhiên, thứ tự các từ sẽ bị xáo trộn không như thứ tự thêm lúc ban đầu. Chủ nhân sẽ phải học lại các thẻ từ thẻ đầu tiên.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = Color(0xFF4B5563),
            lineHeight = 20.sp
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        BigPotagoButton(
            text = "XÁC NHẬN",
            enabled = true,
            onClick = onConfirm
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "ĐỂ SAU",
            modifier = Modifier.clickable { onDismiss() },
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF3B82F6),
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}
@Composable
fun BigPotagoButton(
    text: String = "LOG IN",
    enabled: Boolean,
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
                if (enabled) Color(0xFF46A302) else Color(0xFFABCF7E),
                RoundedCornerShape(16.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(animatedHeight)
                .background(
                    if (enabled) Color(0xFF58CC02) else Color(0xFFB7E37E),
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
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }
    }
}
@Composable
private fun MascotAndBubble(text: String) {
    var start by remember { mutableStateOf(false) }
    var showBubble by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        start = true
        delay(300)
        showBubble = true
    }

    val mascotTransition = updateTransition(targetState = start, label = "mascot")
    val mascotScale by mascotTransition.animateFloat(label = "scale") { if (it) 1f else 0.8f }
    val mascotAlpha by mascotTransition.animateFloat(label = "alpha") { if (it) 1f else 0f }

    val bubbleTransition = updateTransition(targetState = showBubble, label = "bubble")
    val bubbleScale by bubbleTransition.animateFloat(label = "scale") { if (it) 1f else 0.5f }
    val bubbleAlpha by bubbleTransition.animateFloat(label = "alpha") { if (it) 1f else 0f }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_thinking_mascot_flashcard),
            contentDescription = "Mascot",
            modifier = Modifier
                .size(130.dp)
                .graphicsLayer {
                    scaleX = mascotScale
                    scaleY = mascotScale
                    alpha = mascotAlpha
                }
        )
        Spacer(modifier = Modifier.width(20.dp))
        Box(
            modifier = Modifier
                .offset(y = (-20).dp)
                .graphicsLayer {
                    scaleX = bubbleScale
                    scaleY = bubbleScale
                    alpha = bubbleAlpha
                }
                .background(Color.White, RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp, bottomStart = 12.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)

        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF4B5563),
            )
        }
    }
}

@Preview
@Composable
fun FlashCardPanelShow(){
    FlashCardPanel(
        word = Word(
            id = 1,
            term = "term",
            definition = "definition",
            ),
        termLangCode = "en"
    )
}
@Preview
@Composable
fun RandomModeBottomSheetShow() {
    RandomModeBottomSheet(
        onConfirm = {},
        onDismiss = {}
    )
}
