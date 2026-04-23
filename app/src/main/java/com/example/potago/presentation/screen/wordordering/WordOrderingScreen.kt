package com.example.potago.presentation.screen.wordordering

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.potago.R
import com.example.potago.presentation.ui.theme.Nunito

// ── Design tokens ──────────────────────────────────────────────────────────────
private val GreenPrimary    = Color(0xFF58CC02)
private val GreenBorder     = Color(0xFF46A302)
private val GreenDisabled   = Color(0x8058CC02)
private val RedPrimary      = Color(0xFFFF6063)
private val RedBorder       = Color(0xFFFF383C)
private val DividerColor    = Color(0xFFD9D9D9)
private val ChipBorder      = Color(0xFFCCCCCC)
private val ChipGrayed      = Color(0xFFECECEC)
private val TextGray        = Color(0xFF696969)
private val GreenFeedbackBg = Color(0xFFE8F5E9)
private val RedFeedbackBg   = Color(0xFFFFEBEE)

@Composable
fun WordOrderingScreen(
    navController: NavController,
    patternId: Int,
    patternName: String,
    viewModel: WordOrderingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle navigation events
    LaunchedEffect(Unit) {
        viewModel.navEvent.collect { event ->
            when (event) {
                is WordOrderingNavEvent.ToStreak -> {
                    val resultRoute = com.example.potago.presentation.navigation.Screen.WordOrderingResult(
                        correctCount = event.correctCount,
                        totalCount = event.totalCount,
                        completedTime = event.completedTime
                    )
                    navController.navigate(
                        com.example.potago.presentation.navigation.Screen.Streak(event.streakCount, resultRoute)
                    ) {
                        popUpTo(com.example.potago.presentation.navigation.Screen.WordOrdering.route) { inclusive = true }
                    }
                }
                is WordOrderingNavEvent.ToResult -> {
                    navController.navigate(
                        com.example.potago.presentation.navigation.Screen.WordOrderingResult(
                            correctCount = event.correctCount,
                            totalCount = event.totalCount,
                            completedTime = event.completedTime
                        )
                    ) {
                        popUpTo(com.example.potago.presentation.navigation.Screen.WordOrdering.route) { inclusive = true }
                    }
                }
            }
        }
    }

    // Loading state
    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = GreenPrimary)
        }
        return
    }

    // Error state
    val error = uiState.error
    if (error != null && uiState.sentences.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = error, color = RedPrimary)
        }
        return
    }

    // Navigate to result screen when finished
    if (uiState.isFinished) {
        androidx.compose.runtime.LaunchedEffect(Unit) {
            navController.navigate(
                com.example.potago.presentation.navigation.Screen.WordOrderingResult(
                    correctCount = uiState.correctCount,
                    totalCount = uiState.sentences.size
                )
            ) {
                popUpTo(
                    com.example.potago.presentation.navigation.Screen.WordOrdering.route
                ) { inclusive = true }
            }
        }
        return
    }

    WordOrderingScreenContent(
        uiState = uiState,
        onBack = { navController.popBackStack() },
        onCheck = viewModel::checkAnswer,
        onNext = viewModel::nextSentence,
        onPoolChipTap = viewModel::onPoolChipTap,
        onAnswerChipTap = viewModel::onAnswerChipTap
    )

    // Submitting overlay
    if (uiState.isSubmitting) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = GreenPrimary)
        }
    }
}
@Composable
internal fun WordOrderingScreenContent(
    uiState: WordOrderingUiState,
    onBack: () -> Unit,
    onCheck: () -> Unit,
    onNext: () -> Unit,
    onPoolChipTap: (Int) -> Unit,
    onAnswerChipTap: (Int) -> Unit
) {
    val current = uiState.sentences.getOrNull(uiState.currentIndex) ?: return
    var showQuitDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Scaffold(
            topBar = {
                WordOrderingTopBar(
                    progress = uiState.progress,
                    onBack = { showQuitDialog = true }
                )
            },
            bottomBar = {
                BottomActionArea(
                    hasAnswer = uiState.answerChipIds.isNotEmpty(),
                    checkResult = uiState.checkResult,
                    correctAnswer = current.term,
                    onCheck = onCheck,
                    onNext = onNext
                )
            },
            containerColor = Color.White
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Instruction
                Text(
                    text = "Sắp xếp lại để có câu với nghĩa",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = Color.Black.copy(alpha = 0.8f),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Mascot + definition bubble
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.asking_mascot_manage_video_screen),
                        contentDescription = null,
                        modifier = Modifier.size(90.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 0.dp, topEnd = 16.dp,
                            bottomEnd = 16.dp, bottomStart = 16.dp
                        ),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
                        shadowElevation = 2.dp,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = current.definition,
                            fontFamily = Nunito,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextGray,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Answer slots — 2 rows with dividers
                AnswerSlots(
                    answerWords = uiState.selectedWords,
                    answerChipIds = uiState.answerChipIds,
                    onChipTap = onAnswerChipTap,
                    checkResult = uiState.checkResult
                )

                Spacer(modifier = Modifier.weight(1f))

                // Available chips pool
                ChipsPool(
                    poolChips = uiState.poolChips,
                    onChipTap = onPoolChipTap,
                    enabled = uiState.checkResult == CheckResult.NONE
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Quit dialog (bottom sheet style)
        if (showQuitDialog) {
            QuitDialog(
                onDismiss = { showQuitDialog = false },
                onConfirm = onBack
            )
        }
    }
}

// ── TopBar ─────────────────────────────────────────────────────────────────────
@Composable
private fun WordOrderingTopBar(progress: Float, onBack: () -> Unit) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 600),
        label = "progress"
    )
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White.copy(alpha = 0.9f),
        shadowElevation = 2.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back),
                        contentDescription = "Quay lại",
                        modifier = Modifier.size(22.dp),
                        tint = Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sắp xếp chữ",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = Color.Black
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 16.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .weight(1f)
                        .height(13.dp)
                        .clip(RoundedCornerShape(9999.dp)),
                    color = GreenPrimary,
                    trackColor = Color(0xFFE5E7EB)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_goal_flag),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Color.Unspecified
                )
            }
        }
    }
}

// ── Answer slots with 2 divider lines ─────────────────────────────────────────
@Composable
private fun AnswerSlots(
    answerWords: List<String>,
    answerChipIds: List<Int>,
    onChipTap: (Int) -> Unit,
    checkResult: CheckResult
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        // Row 1 — first 4
        AnswerSlotRow(
            words = answerWords.take(4),
            ids = answerChipIds.take(4),
            onChipTap = onChipTap,
            checkResult = checkResult
        )
        HorizontalDivider(color = DividerColor, thickness = 2.dp)
        Spacer(modifier = Modifier.height(8.dp))
        // Row 2 — remaining
        AnswerSlotRow(
            words = if (answerWords.size > 4) answerWords.drop(4) else emptyList(),
            ids = if (answerChipIds.size > 4) answerChipIds.drop(4) else emptyList(),
            onChipTap = onChipTap,
            checkResult = checkResult
        )
        HorizontalDivider(color = DividerColor, thickness = 2.dp)
    }
}

@Composable
private fun AnswerSlotRow(
    words: List<String>,
    ids: List<Int>,
    onChipTap: (Int) -> Unit,
    checkResult: CheckResult
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        words.forEachIndexed { i, word ->
            val chipId = ids.getOrNull(i) ?: return@forEachIndexed
            AnswerChip(
                word = word,
                onClick = { onChipTap(chipId) },
                enabled = checkResult == CheckResult.NONE,
                checkResult = checkResult
            )
            if (i < words.lastIndex) Spacer(modifier = Modifier.width(6.dp))
        }
    }
}

@Composable
private fun AnswerChip(
    word: String,
    onClick: () -> Unit,
    enabled: Boolean,
    checkResult: CheckResult
) {
    val bgColor = when (checkResult) {
        CheckResult.CORRECT -> GreenFeedbackBg
        CheckResult.WRONG   -> RedFeedbackBg
        CheckResult.NONE    -> Color.White
    }
    val borderColor = when (checkResult) {
        CheckResult.CORRECT -> GreenPrimary
        CheckResult.WRONG   -> RedPrimary
        CheckResult.NONE    -> ChipBorder
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 20.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = word,
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = TextGray,
            textAlign = TextAlign.Center
        )
    }
}

// ── Chips pool — fixed positions, selected ones show as gray placeholder ───────
@Composable
private fun ChipsPool(
    poolChips: List<PoolChip>,
    onChipTap: (Int) -> Unit,
    enabled: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        poolChips.chunked(3).forEach { rowChips ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                rowChips.forEachIndexed { i, chip ->
                    PoolChipItem(
                        chip = chip,
                        onClick = { onChipTap(chip.id) },
                        enabled = enabled && !chip.isSelected
                    )
                    if (i < rowChips.lastIndex) Spacer(modifier = Modifier.width(10.dp))
                }
            }
        }
    }
}

@Composable
private fun PoolChipItem(
    chip: PoolChip,
    onClick: () -> Unit,
    enabled: Boolean
) {
    val bgColor    = if (chip.isSelected) ChipGrayed else Color.White
    val borderColor = if (chip.isSelected) ChipGrayed else ChipBorder

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 20.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        // Show text only when not selected (selected = invisible placeholder)
        Text(
            text = chip.word,
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = if (chip.isSelected) Color.Transparent else TextGray,
            textAlign = TextAlign.Center
        )
    }
}

// ── Bottom action area ─────────────────────────────────────────────────────────
@Composable
private fun BottomActionArea(
    hasAnswer: Boolean,
    checkResult: CheckResult,
    correctAnswer: String,
    onCheck: () -> Unit,
    onNext: () -> Unit
) {
    when (checkResult) {
        CheckResult.NONE -> {
            Box(modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp, bottom = 24.dp)
                .navigationBarsPadding()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (hasAnswer) GreenPrimary else GreenDisabled)
                        .clickable(enabled = hasAnswer) { onCheck() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "KIỂM TRA",
                        fontFamily = Nunito,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
            }
        }

        CheckResult.CORRECT -> {
            // Green feedback bottom sheet
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFC7FF9D),
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
            ) {
                Column(modifier = Modifier
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
                            color = GreenBorder
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(GreenPrimary)
                            .border(3.dp, GreenBorder, RoundedCornerShape(16.dp))
                            .clickable { onNext() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "TIẾP TỤC",
                            fontFamily = Nunito,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        CheckResult.WRONG -> {
            // Red feedback bottom sheet
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFFFE5E5),
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
            ) {
                Column(modifier = Modifier
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
                            color = RedBorder
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = correctAnswer,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = RedBorder
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(RedPrimary)
                            .border(3.dp, RedBorder, RoundedCornerShape(16.dp))
                            .clickable { onNext() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ĐÃ HIỂU",
                            fontFamily = Nunito,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

// ── Quit dialog (bottom sheet style overlay — no Dialog wrapper) ───────────────
@Composable
private fun QuitDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            // Scrim: tap outside sheet → dismiss
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                // Consume clicks so tapping the sheet itself doesn't dismiss
                .clickable(enabled = false, onClick = {}),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = Color.White,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp, bottom = 20.dp)
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Drag handle
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(9999.dp))
                        .background(Color(0xFFE5E7EB))
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Mascot + bubble
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Image(
                        painter = painterResource(R.drawable.asking_mascot_manage_video_screen),
                        contentDescription = null,
                        modifier = Modifier.size(120.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 0.dp, topEnd = 16.dp,
                            bottomEnd = 16.dp, bottomStart = 16.dp
                        ),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
                        shadowElevation = 2.dp,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Thoát bài học giữa chừng sẽ không có điểm. Xác nhận thoát chứ!?",
                            fontFamily = Nunito,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF4B5563),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Từ chối",
                            fontFamily = Nunito,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = Color(0xFF374151)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(GreenPrimary)
                            .border(3.dp, GreenBorder, RoundedCornerShape(16.dp))
                            .clickable { onConfirm() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Xác nhận",
                            fontFamily = Nunito,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}




// ── Previews ───────────────────────────────────────────────────────────────────
private val previewSentence = OrderingSentence(
    id = 1,
    term = "Where is the nearest station?",
    definition = "Ga gần nhất ở đâu?"
)

private val previewPool = listOf(
    PoolChip(0, "Where",   isSelected = true),
    PoolChip(1, "is",      isSelected = true),
    PoolChip(2, "the",     isSelected = false),
    PoolChip(3, "nearest", isSelected = false),
    PoolChip(4, "station", isSelected = false)
)

private val baseState = WordOrderingUiState(
    sentences    = listOf(previewSentence),
    currentIndex = 0,
    poolChips    = previewPool,
    answerChipIds = listOf(0, 1),
    progress     = 0.4f
)

@Preview(name = "Đang làm bài", showBackground = true, showSystemUi = true)
@Composable
private fun PreviewWordOrdering_Idle() {
    com.example.potago.presentation.ui.theme.PotagoTheme {
        WordOrderingScreenContent(
            uiState = baseState.copy(checkResult = CheckResult.NONE),
            onBack = {},
            onCheck = {},
            onNext = {},
            onPoolChipTap = {},
            onAnswerChipTap = {}
        )
    }
}

@Preview(name = "Đúng", showBackground = true, showSystemUi = true)
@Composable
private fun PreviewWordOrdering_Correct() {
    com.example.potago.presentation.ui.theme.PotagoTheme {
        WordOrderingScreenContent(
            uiState = baseState.copy(checkResult = CheckResult.CORRECT),
            onBack = {},
            onCheck = {},
            onNext = {},
            onPoolChipTap = {},
            onAnswerChipTap = {}
        )
    }
}

@Preview(name = "Sai", showBackground = true, showSystemUi = true)
@Composable
private fun PreviewWordOrdering_Wrong() {
    com.example.potago.presentation.ui.theme.PotagoTheme {
        WordOrderingScreenContent(
            uiState = baseState.copy(checkResult = CheckResult.WRONG),
            onBack = {},
            onCheck = {},
            onNext = {},
            onPoolChipTap = {},
            onAnswerChipTap = {}
        )
    }
}
