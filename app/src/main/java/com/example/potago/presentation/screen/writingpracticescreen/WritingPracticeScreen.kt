package com.example.potago.presentation.screen.writingpracticescreen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.potago.R
import com.example.potago.presentation.navigation.Screen
import com.example.potago.presentation.screen.setting.BackButton
import com.example.potago.presentation.screen.wordordering.GreenButton
import com.example.potago.presentation.screen.wordordering.RedButton
import com.example.potago.presentation.ui.theme.Nunito

@Composable
fun WritingPracticeScreen(
    navController: NavController,
    patternId: Int = 0,
    viewModel: WritingPracticeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showQuitDialog by remember { mutableStateOf(false) }

    LaunchedEffect(patternId) {
        if (patternId > 0) {
            viewModel.resetState()
            viewModel.loadSentences(patternId)
        }
    }

    // Collect navEvent để navigate
    LaunchedEffect(Unit) {
        viewModel.navEvent.collect { event ->
            when (event) {
                is WritingNavEvent.ToStreak -> {
                    val resultRoute = Screen.WordOrderingResult(
                        correctCount = 0,
                        totalCount = 0,
                        completedTime = parseTimeToSeconds(event.timeFormatted),
                        xpEarned = event.xpEarned,
                        diamondEarned = event.diamondEarned,
                        hackXp = event.hackXp,
                        superXp = event.superXp
                    )
                    navController.navigate(Screen.Streak(event.streakCount, resultRoute)) {
                        popUpTo(Screen.WritingPractice.route) { inclusive = true }
                    }
                }
                is WritingNavEvent.ToResult -> {
                    navController.navigate(
                        Screen.WordOrderingResult(
                            correctCount = 0,
                            totalCount = 0,
                            completedTime = parseTimeToSeconds(event.timeFormatted),
                            xpEarned = event.xpEarned,
                            diamondEarned = event.diamondEarned,
                            hackXp = event.hackXp,
                            superXp = event.superXp
                        )
                    ) {
                        popUpTo(Screen.WritingPractice.route) { inclusive = true }
                    }
                }
            }
        }
    }

    // Dialog xác nhận làm tiếp
    if (uiState.showContinueDialog) {
        ContinueDialog(
            onContinue = { viewModel.continueFromSaved() },
            onRestart = { viewModel.startNewSession(patternId) }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Luyện viết",
                onBackClick = { showQuitDialog = true }
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding))

        if (uiState.isLoading) {
            LoadingOverlay()
            return@Scaffold
        }

        val error = uiState.error
        if (error != null && uiState.sentences.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = error, color = Color(0xFFFF6063))
            }
            return@Scaffold
        }

        WritingPracticeContent(
            uiState = uiState,
            onCheck = { answer -> viewModel.checkAnswer(answer) },
            onNext = { viewModel.moveToNextSentence() }
        )

        // Submitting overlay
        if (uiState.isSubmitting) {
            SubmittingOverlay()
        }

        if (showQuitDialog) {
            QuitDialog(
                onDismiss = { showQuitDialog = false },
                onConfirm = { navController.popBackStack() }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Content
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun WritingPracticeContent(
    uiState: WritingPracticeUiState,
    onCheck: (String) -> Unit,
    onNext: () -> Unit
) {
    val currentSentence = uiState.sentences.getOrNull(uiState.currentIndex) ?: return
    var userAnswer by remember(uiState.currentIndex) { mutableStateOf("") }

    val images = listOf(
        R.drawable.ic_mascot_happy,
        R.drawable.ic_teaching_mascot,
        R.drawable.ic_phan_van_mascot,
        R.drawable.ic_thinking_mascot_flashcard
    )
    val randomImage = remember { images.random() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AppTopBar(title = "Luyện viết", onBackClick = {}, modifier = Modifier.alpha(0f))
            Spacer(modifier = Modifier.height(20.dp))

            // Progress bar
            ProgressBar(progress = uiState.progress)

            Spacer(modifier = Modifier.height(15.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // Instruction
                Text(
                    text = "Viết lại câu có nghĩa như sau",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = Color.Black.copy(alpha = 0.8f),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Mascot + definition bubble
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(randomImage),
                        contentDescription = null,
                        modifier = Modifier.size(110.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Surface(
                        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
                    ) {
                        Text(
                            text = currentSentence.definition,
                            fontFamily = Nunito,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF696969),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Text input
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(200.dp)
                        .border(2.dp, Color(0x1A000000), RoundedCornerShape(15.dp))
                        .clip(RoundedCornerShape(15.dp))
                        .background(
                            when (uiState.answerResult) {
                                is AnswerResult.Correct -> Color(0xFFE8F5E9)
                                is AnswerResult.Incorrect -> Color(0xFFFFEBEE)
                                else -> Color.White
                            }
                        )
                        .padding(13.dp)
                ) {
                    BasicTextField(
                        value = userAnswer,
                        onValueChange = { if (uiState.answerResult == AnswerResult.None) userAnswer = it },
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 24.sp,
                            fontFamily = Nunito
                        ),
                        modifier = Modifier.fillMaxSize(),
                        decorationBox = { innerTextField ->
                            Box {
                                if (userAnswer.isEmpty()) {
                                    Text(
                                        "Nhập ở đây ...",
                                        color = Color(0x33000000),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = Nunito
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(120.dp))
            }
        }

        // Bottom action area
        when (val result = uiState.answerResult) {
            is AnswerResult.Correct -> {
                Surface(
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                    color = Color(0xFFC7FF9D)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(top = 16.dp, bottom = 16.dp)
                            .navigationBarsPadding()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.ic_check_green_circle),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CHÍNH XÁC !",
                                fontFamily = Nunito,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = Color(0xFF46A302)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        GreenButton("TIẾP TỤC", enabled = true, isLoading = false, onClick = onNext)
                    }
                }
            }
            is AnswerResult.Incorrect -> {
                Surface(
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                    color = Color(0xFFFFE5E5)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(top = 16.dp, bottom = 16.dp)
                            .navigationBarsPadding()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.ic_close_red_circle),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ĐÁP ÁN ĐÚNG LÀ :",
                                fontFamily = Nunito,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = Color(0xFFFF383C)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = result.correctAnswer,
                            fontFamily = Nunito,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFFFF383C)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        RedButton("ĐÃ HIỂU", enabled = true, isLoading = false, onClick = onNext)
                    }
                }
            }
            AnswerResult.None -> {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 20.dp)
                        .padding(top = 12.dp, bottom = 16.dp)
                        .navigationBarsPadding()
                ) {
                    GreenButton("KIỂM TRA", enabled = userAnswer.isNotBlank(), isLoading = false, onClick = { onCheck(userAnswer) })
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Progress Bar
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ProgressBar(progress: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 16.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(13.dp)
                .clip(RoundedCornerShape(9999.dp))
                .background(Color(0xFFE5E7EB))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(Color(0xFF58CC02))
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Icon(
            painter = painterResource(R.drawable.ic_goal_flag),
            contentDescription = null,
            modifier = Modifier.size(32.dp).offset(y = (-8).dp),
            tint = Color.Unspecified
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Continue Dialog
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ContinueDialog(onContinue: () -> Unit, onRestart: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        containerColor = Color.White,
        title = { Text("Làm tiếp?", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
        text = { Text("Bạn có muốn tiếp tục từ lần trước không?", fontSize = 16.sp) },
        confirmButton = {
            Button(
                onClick = onContinue,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF58CC02))
            ) { Text("Làm tiếp", color = Color.White) }
        },
        dismissButton = {
            TextButton(onClick = onRestart) {
                Text("Làm lại từ đầu", color = Color(0xFF58CC02))
            }
        }
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Quit Dialog
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuitDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 11.dp)
                    .width(48.dp)
                    .height(6.dp)
                    .background(Color(0xFFE5E7EB), RoundedCornerShape(999.dp))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth().height(180.dp).padding(horizontal = 5.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_crying_mascot),
                    contentDescription = null,
                    modifier = Modifier.graphicsLayer { scaleX = 0.7f; scaleY = 0.7f }
                )
                Surface(
                    modifier = Modifier.padding(top = 30.dp),
                    shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    shadowElevation = 2.dp
                ) {
                    Text(
                        text = "Thoát bài học giữa chừng sẽ không có điểm. Xác nhận thoát chứ!?",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4B5563),
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(horizontal = 13.dp, vertical = 14.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(15.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                var cancelPressed by remember { mutableStateOf(false) }
                Box(modifier = Modifier.weight(1f).height(51.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFFE5E7EB))) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(if (cancelPressed) 51.dp else 48.dp)
                            .clip(RoundedCornerShape(16.dp)).background(Color.White)
                            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = { cancelPressed = true; tryAwaitRelease(); cancelPressed = false },
                                    onTap = { onDismiss() }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Từ chối", fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color(0xFF374151))
                    }
                }
                var confirmPressed by remember { mutableStateOf(false) }
                Box(modifier = Modifier.weight(1f).height(51.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFF46A302))) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(if (confirmPressed) 51.dp else 48.dp)
                            .clip(RoundedCornerShape(16.dp)).background(Color(0xFF58CC02))
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = { confirmPressed = true; tryAwaitRelease(); confirmPressed = false },
                                    onTap = { onConfirm() }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Xác nhận", fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Top Bar
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AppTopBar(title: String, onBackClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 3.dp,
        shadowElevation = 4.dp,
        color = Color.White
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(60.dp))
                Text(text = title, style = MaterialTheme.typography.displayMedium, modifier = Modifier.weight(1f))
            }
            Box(modifier = Modifier.matchParentSize()) {
                BackButton(onClick = onBackClick, modifier = Modifier.align(Alignment.CenterStart).wrapContentSize())
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WritingPracticeScreenPreview() {
    WritingPracticeScreen(rememberNavController())
}

// "1:23" → 83.0 seconds
private fun parseTimeToSeconds(timeFormatted: String): Double {
    return try {
        val parts = timeFormatted.split(":")
        if (parts.size == 2) {
            parts[0].toInt() * 60.0 + parts[1].toInt()
        } else {
            timeFormatted.toDoubleOrNull() ?: 0.0
        }
    } catch (_: Exception) { 0.0 }
}

// ─────────────────────────────────────────────────────────────────────────────
// Loading Overlay (initial load)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun LoadingOverlay() {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = tween(1200, easing = LinearEasing), repeatMode = RepeatMode.Restart),
        label = "rot"
    )
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Icon(
            painter = painterResource(R.drawable.ic_experience_points),
            contentDescription = null,
            modifier = Modifier.size(56.dp).graphicsLayer { rotationZ = rotation },
            tint = Color.Unspecified
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Submitting Overlay (after completing all sentences)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SubmittingOverlay() {
    val infiniteTransition = rememberInfiniteTransition(label = "submit_loading")
    val dot1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = keyframes { durationMillis = 900; 0.2f at 0; 1f at 150; 0.2f at 450 }, repeatMode = RepeatMode.Restart), label = "d1"
    )
    val dot2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = keyframes { durationMillis = 900; 0.2f at 150; 1f at 300; 0.2f at 600 }, repeatMode = RepeatMode.Restart), label = "d2"
    )
    val dot3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = keyframes { durationMillis = 900; 0.2f at 300; 1f at 450; 0.2f at 750 }, repeatMode = RepeatMode.Restart), label = "d3"
    )
    val dot1Scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.4f,
        animationSpec = infiniteRepeatable(animation = keyframes { durationMillis = 900; 1f at 0; 1.4f at 150; 1f at 450 }, repeatMode = RepeatMode.Restart), label = "s1"
    )
    val dot2Scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.4f,
        animationSpec = infiniteRepeatable(animation = keyframes { durationMillis = 900; 1f at 150; 1.4f at 300; 1f at 600 }, repeatMode = RepeatMode.Restart), label = "s2"
    )
    val dot3Scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.4f,
        animationSpec = infiniteRepeatable(animation = keyframes { durationMillis = 900; 1f at 300; 1.4f at 450; 1f at 750 }, repeatMode = RepeatMode.Restart), label = "s3"
    )
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = tween(1200, easing = LinearEasing), repeatMode = RepeatMode.Restart), label = "rot"
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_experience_points),
                contentDescription = null,
                modifier = Modifier.size(56.dp).graphicsLayer { rotationZ = rotation },
                tint = Color.Unspecified
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                listOf(dot1Alpha to dot1Scale, dot2Alpha to dot2Scale, dot3Alpha to dot3Scale).forEach { (alpha, scale) ->
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .graphicsLayer { scaleX = scale; scaleY = scale; this.alpha = alpha }
                            .background(Color(0xFF58CC02), androidx.compose.foundation.shape.CircleShape)
                    )
                }
            }
            Text(
                text = "Đang tính điểm...",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.White, fontWeight = FontWeight.Bold)
            )
        }
    }
}
