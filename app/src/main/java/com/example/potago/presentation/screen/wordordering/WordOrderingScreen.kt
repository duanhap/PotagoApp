package com.example.potago.presentation.screen.wordordering

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.magnifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
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
import com.example.potago.presentation.screen.setting.BackButton
import com.example.potago.presentation.ui.theme.Nunito

// ── Design tokens ──────────────────────────────────────────────────────────────
private val GreenPrimary = Color(0xFF58CC02)
private val GreenBorder = Color(0xFF46A302)
private val GreenDisabled = Color(0x8058CC02)
private val RedPrimary = Color(0xFFFF6063)
private val RedBorder = Color(0xFFFF383C)
private val DividerColor = Color(0xFFD9D9D9)
private val ChipBorder = Color(0xFFCCCCCC)
private val ChipGrayed = Color(0xFFECECEC)
private val TextGray = Color(0xFF696969)
private val GreenFeedbackBg = Color(0xFFE8F5E9)
private val RedFeedbackBg = Color(0xFFFFEBEE)

@Composable
fun WordOrderingScreen(
    navController: NavController,
    patternId: Int,
    patternName: String,
    viewModel: WordOrderingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showQuitDialog by remember { mutableStateOf(false) }


    // Handle navigation events
    LaunchedEffect(Unit) {
        viewModel.navEvent.collect { event ->
            when (event) {
                is WordOrderingNavEvent.ToStreak -> {
                    val resultRoute = com.example.potago.presentation.navigation.Screen.WordOrderingResult(
                        correctCount = event.correctCount,
                        totalCount = event.totalCount,
                        completedTime = event.completedTime,
                        xpEarned = event.xpEarned,
                        diamondEarned = event.diamondEarned,
                        hackXp = event.hackXp,
                        superXp = event.superXp
                    )
                    navController.navigate(com.example.potago.presentation.navigation.Screen.Streak(event.streakCount, resultRoute)) {
                        popUpTo(com.example.potago.presentation.navigation.Screen.WordOrdering.route) { inclusive = true }
                    }
                }
                is WordOrderingNavEvent.ToResult -> {
                    navController.navigate(
                        com.example.potago.presentation.navigation.Screen.WordOrderingResult(
                            correctCount = event.correctCount,
                            totalCount = event.totalCount,
                            completedTime = event.completedTime,
                            xpEarned = event.xpEarned,
                            diamondEarned = event.diamondEarned,
                            hackXp = event.hackXp,
                            superXp = event.superXp
                        )
                    ) {
                        popUpTo(com.example.potago.presentation.navigation.Screen.WordOrdering.route) { inclusive = true }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Sắp xếp chữ",
                onBackClick = { showQuitDialog = true },
            )
        },
       containerColor = Color.White
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding))
        // Loading state
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GreenPrimary)
            }
            return@Scaffold
        }

        // Error state
        val error = uiState.error
        if (error != null && uiState.sentences.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = error, color = RedPrimary)
            }
            return@Scaffold
        }

        WordOrderingScreenContent(
            uiState = uiState,
            onBack = { navController.popBackStack() },
            onPoolChipTap = viewModel::onPoolChipTap,
            onCheck = viewModel::checkAnswer,
            onNext = viewModel::nextSentence,
            onAnswerChipTap = viewModel::onAnswerChipTap
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

@Composable
internal fun WordOrderingScreenContent(
    uiState: WordOrderingUiState,
    onBack: () -> Unit,
    onCheck: () -> Unit,
    onNext :() -> Unit,
    onPoolChipTap: (Int) -> Unit,
    onAnswerChipTap: (Int) -> Unit
) {
    val current = uiState.sentences.getOrNull(uiState.currentIndex) ?: return

    val images = listOf(
        R.drawable.ic_mascot_happy,
        R.drawable.ic_teaching_mascot,
        R.drawable.ic_phan_van_mascot,
        R.drawable.ic_thinking_mascot_flashcard,


        )
    val randomImage = remember {
        images.random()
    }


    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)) {

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            AppTopBar(title = "Sắp xếp chữ", onBackClick = {}, modifier = Modifier.alpha(0f))
            Spacer(modifier = Modifier.height(20.dp))
            ProgressGame(uiState.progress)
            Spacer(modifier = Modifier.height(15.dp))

            // Instruction
            Text(
                text = "Sắp xếp lại để có câu với nghĩa",
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
                    shape = RoundedCornerShape(
                        topStart = 0.dp, topEnd = 16.dp,
                        bottomEnd = 16.dp, bottomStart = 16.dp
                    ),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
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

            Spacer(modifier = Modifier.height(50.dp))
            // Available chips pool
            ChipsPool(
                poolChips = uiState.poolChips,
                onChipTap = onPoolChipTap,
                enabled = uiState.checkResult == CheckResult.NONE
            )

            Spacer(modifier = Modifier.height(120.dp))
        }


        BottomActionArea(
            modifier = Modifier.align(Alignment.BottomCenter),
            hasAnswer = uiState.answerChipIds.isNotEmpty(),
            checkResult = uiState.checkResult,
            correctAnswer = current.term,
            onCheck = onCheck,
            onNext = onNext
        )
    }
}


@Composable
private fun ProgressGame(animatedProgress: Float) {
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
                    .fillMaxWidth(animatedProgress)
                    .background(GreenPrimary)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Icon(
            painter = painterResource(R.drawable.ic_goal_flag),
            contentDescription = null,
            modifier = Modifier.size(32.dp).offset(y=-8.dp),
            tint = Color.Unspecified
        )
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
        CheckResult.WRONG -> RedFeedbackBg
        CheckResult.NONE -> Color.White
    }
    val borderColor = when (checkResult) {
        CheckResult.CORRECT -> GreenPrimary
        CheckResult.WRONG -> RedPrimary
        CheckResult.NONE -> ChipBorder
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
    val bgColor = if (chip.isSelected) ChipGrayed else Color.White
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
    modifier: Modifier = Modifier,
    hasAnswer: Boolean,
    checkResult: CheckResult,
    correctAnswer: String,
    onCheck: () -> Unit,
    onNext: () -> Unit
) {
    when (checkResult) {
        CheckResult.NONE -> {
            Box(
                modifier = modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 12.dp, bottom = 16.dp)
                    .navigationBarsPadding()
            ) {
                GreenButton("KIỂM TRA", enabled = hasAnswer, isLoading = false, onClick = onCheck)
            }
        }

        CheckResult.CORRECT -> {
            // Green feedback bottom sheet
            Surface(
                modifier = modifier.fillMaxWidth(),
                color = Color(0xFFC7FF9D),
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
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
                            color = GreenBorder
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    GreenButton("TIẾP TỤC", enabled = true, isLoading = false, onClick = onNext)
                }
            }
        }

        CheckResult.WRONG -> {
            // Red feedback bottom sheet
            Surface(
                modifier = modifier.fillMaxWidth(),
                color = Color(0xFFFFE5E5),
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
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
                    RedButton("ĐÃ HIỂU",enabled = true, isLoading = false, onClick = onNext)

                }
            }
        }
    }
}

// ── Quit dialog (bottom sheet style overlay — no Dialog wrapper) ───────────────
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(horizontal = 5.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_crying_mascot),
                    contentDescription = null,
                    modifier = Modifier
                        .scale(0.7f)
                )
                Surface(
                    modifier = Modifier
                        .padding(top = 30.dp),
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

            // Buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Cancel button
                var cancelPressed by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(51.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFE5E7EB))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (cancelPressed) 51.dp else 48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = { cancelPressed = true; tryAwaitRelease(); cancelPressed = false },
                                    onTap = { onDismiss() }
                                )
                            },
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
                }

                // Confirm button
                var confirmPressed by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(51.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF46A302))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (confirmPressed) 51.dp else 48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF58CC02))
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = { confirmPressed = true; tryAwaitRelease(); confirmPressed = false },
                                    onTap = { onConfirm() }
                                )
                            },
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
    PoolChip(0, "Where", isSelected = true),
    PoolChip(1, "is", isSelected = true),
    PoolChip(2, "the", isSelected = false),
    PoolChip(3, "nearest", isSelected = false),
    PoolChip(4, "station", isSelected = false)
)

private val baseState = WordOrderingUiState(
    sentences = listOf(previewSentence),
    currentIndex = 0,
    poolChips = previewPool,
    answerChipIds = listOf(0, 1),
    progress = 0.4f
)

@Composable
private fun AppTopBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
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
                    text = title,
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier.weight(1f),
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
fun RedButton(text: String, enabled: Boolean, isLoading: Boolean, onClick: () -> Unit) {
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
                Text(text = text, style = MaterialTheme.typography.titleMedium, color = Color.White)
            }
        }
    }
}

@Composable
fun GreenButton(
    text: String = "LOG IN",
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
                            .background(GreenPrimary, androidx.compose.foundation.shape.CircleShape)
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
