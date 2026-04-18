package com.example.potago.presentation.screen.detailedvideoscreen

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.buildAnnotatedString

import androidx.compose.ui.graphics.Shadow

import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.example.potago.R
import com.example.potago.domain.model.Subtitle
import com.example.potago.domain.model.Video
import com.example.potago.presentation.screen.UiEvent
import com.example.potago.presentation.screen.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@Composable
fun DetailedVideoScreen(
    navController: NavController,
    viewModel: DetailedVideoViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val subtitlesState by viewModel.subtitlesState.collectAsState()
    val videoState by viewModel.videoState.collectAsState()
    val selectedTabIndex by viewModel.selectedTabIndex.collectAsState()
    val currentTimeMs by viewModel.currentTimeMs.collectAsState()
    val currentSubtitleIndex by viewModel.currentSubtitleIndex.collectAsState()
    val isRepeatMode by viewModel.isRepeatMode.collectAsState()
    val isQuestionMode by viewModel.isQuestionMode.collectAsState()
    
    val userInput by viewModel.userInput.collectAsState()
    val checkResult by viewModel.checkResult.collectAsState()
    val showRewardPopup by viewModel.showRewardPopup.collectAsState()
    val isRewardEarned by viewModel.isRewardEarned.collectAsState()
    val writingProgress by viewModel.writingProgress.collectAsState()

    // Record Test Mode States
    val isRecordTestMode by viewModel.isRecordTestMode.collectAsState()
    val speakingScore by viewModel.speakingScore.collectAsState()
    val speakingProgress by viewModel.speakingProgress.collectAsState()
    val spokenWordIndices by viewModel.spokenWordIndices.collectAsState()
    val spokenTranscript by viewModel.spokenTranscript.collectAsState()
    val recordState by viewModel.recordState.collectAsState()

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                viewModel.enterRecordTestMode()
            }
        }
    )

    // Hiển thị Mic bar khi ở tab "Xem từng câu" (index 1)
    val showMicBottomSheet = selectedTabIndex == 1

    var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    val pauseVideo = {
        exoPlayer?.pause()
        webViewInstance?.evaluateJavascript("player.pauseVideo();", null)
    }

    val onSeek: (Int) -> Unit = { startTimeMs ->
        if (exoPlayer != null) {
            exoPlayer?.seekTo(startTimeMs.toLong())
            exoPlayer?.play()
        } else {
            webViewInstance?.evaluateJavascript("seekTo(${startTimeMs / 1000f});", null)
        }
    }

    // Lắng nghe sự kiện từ ViewModel (như yêu cầu pause video khi thu âm)
    LaunchedEffect(Unit) {
        launch {
            viewModel.uiEvent.collect { event ->
                when {
                    event is UiEvent.ShowSnackbar && event.message == "Đang lắng nghe..." -> {
                        pauseVideo()
                    }
                    event is UiEvent.Navigate -> {
                        navController.navigate(event.route)
                    }
                }
            }
        }

        launch {
            while (true) {
                delay(500)
                Log.d("DEBUG", "currentSubtitleIndex: $currentSubtitleIndex, currentTimeMs: $currentTimeMs")
            }
        }
    }

    // Tự động play khi bật repeat mode
    LaunchedEffect(isRepeatMode) {
        if (isRepeatMode) {
            val subs = (subtitlesState as? UiState.Success)?.data
            val currentSub = subs?.getOrNull(currentSubtitleIndex)
            if (currentSub != null) {
                // Nếu đang ở cuối câu hoặc đã dừng, seek về đầu câu và chạy
                onSeek(currentSub.startTime ?: 0)
            } else {
                exoPlayer?.play()
                webViewInstance?.evaluateJavascript("player.playVideo();", null)
            }
        }
    }

    LaunchedEffect(currentTimeMs) {
        if (subtitlesState is UiState.Success) {
            val subs = (subtitlesState as UiState.Success<List<Subtitle>>).data ?: emptyList()
            val currentSub = subs.getOrNull(currentSubtitleIndex)

            // LOGIC DỪNG VIDEO KHI Ở CHẾ ĐỘ QUESTION MODE
            if (isQuestionMode && currentSub != null && !isRepeatMode) {
                if (currentTimeMs >= (currentSub.endTime?.toLong() ?: Long.MAX_VALUE)) {
                    pauseVideo()
                }
            }

            val index = subs.indexOfLast { (it.startTime?.toLong() ?: 0L) <= currentTimeMs }
            val finalIndex = if (index == -1) 0 else index

            if (finalIndex != currentSubtitleIndex) {
                if (isRepeatMode) {
                    if (currentSub != null && (currentTimeMs + 3000)  >= (currentSub.endTime?.toLong() ?: Long.MAX_VALUE)) {
                        onSeek(currentSub.startTime ?: 0)
                    }
                } else if (!isQuestionMode && !isRecordTestMode) {
                    // Chỉ tự động nhảy câu theo video nếu KHÔNG ở chế độ Question Mode/Record Mode
                    viewModel.jumpToSubtitle(finalIndex)
                }
            }
        }
    }

    // Pause video when entering record mode
    LaunchedEffect(isRecordTestMode) {
        if (isRecordTestMode) {
            pauseVideo()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding))

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                Spacer(modifier = Modifier.height(73.dp))

                // Video Player Section
                when (videoState) {
                    is UiState.Loading -> {
                        Box(modifier = Modifier.fillMaxWidth().aspectRatio(16/9f).background(Color.Black), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                    is UiState.Success -> {
                        val video = (videoState as UiState.Success<Video>).data
                        if (!video?.serverSourceUrl.isNullOrBlank()) {
                            ExoPlayerView(
                                videoUrl = video.serverSourceUrl!!,
                                onPlayerReady = { exoPlayer = it },
                                onTimeUpdate = { viewModel.updateCurrentTime(it) }
                            )
                        } else {
                            val videoId = video?.let { extractYoutubeVideoId(it.sourceUrl) }
                            // Capture startPosition 1 lần khi composable được tạo, reset ngay để tránh seek lại
                            val initialPositionMs = remember { viewModel.savedVideoPositionMs.also { viewModel.savedVideoPositionMs = 0L } }
                            YoutubeWebView(
                                videoId = videoId ?: "",
                                startPositionMs = initialPositionMs,
                                onPlayerReady = { webViewInstance = it },
                                onTimeUpdate = { viewModel.updateCurrentTime(it) }
                            )
                        }
                    }
                    is UiState.Error -> {
                        Box(modifier = Modifier.fillMaxWidth().aspectRatio(16/9f).background(Color.Black), contentAlignment = Alignment.Center) {
                            Text(text = "Lỗi tải video", color = Color.White)
                        }
                    }
                    else ->{}
                }

                TabRowSection(selectedTabIndex = selectedTabIndex, onTabSelected = { viewModel.onTabSelected(it) })

                when (subtitlesState) {
                    is UiState.Loading -> { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
                    is UiState.Success -> {
                        val subtitles = (subtitlesState as UiState.Success<List<Subtitle>>).data ?: emptyList()
                        if (selectedTabIndex == 0) {
                            SubtitleList(
                                subtitles = subtitles,
                                currentTimeMs = currentTimeMs,
                                onSubtitleClick = { 
                                    viewModel.disableRepeatMode()
                                    onSeek(it) 
                                }
                            )
                        } else {
                            SingleSubtitleView(
                                subtitles = subtitles,
                                currentIndex = currentSubtitleIndex,
                                isRepeatMode = isRepeatMode,
                                isQuestionMode = isQuestionMode,
                                onToggleRepeat = { viewModel.toggleRepeatMode() },
                                onToggleQuestion = { viewModel.toggleQuestionMode() },
                                userInput = userInput,
                                onUserInputChange = { viewModel.onUserInputChange(it) },
                                writingProgress = writingProgress,
                                speakingProgress = speakingProgress,
                                isRewardEarned = isRewardEarned,
                                onClaimReward = { viewModel.claimReward() }
                            )
                        }
                    }
                    is UiState.Error -> { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(text = (subtitlesState as UiState.Error).message, color = Color.Red) } }
                    else -> {}
                }
            }

            // --- Record Mode Overlay ---
            if (isRecordTestMode && subtitlesState is UiState.Success) {
                val currentSub = (subtitlesState as UiState.Success<List<Subtitle>>).data?.getOrNull(currentSubtitleIndex)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable(enabled = false) {}
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Spacer(modifier = Modifier.weight(2.5f))

                        // White Box Content
                        RecordContentBox(
                            modifier = Modifier.weight(3f),
                            subtitle = currentSub?.content ?: "",
                            score = speakingScore,
                            spokenIndices = spokenWordIndices
                        )

                        Spacer(modifier = Modifier.weight(0.5f))

                        // Mascot and Bubble
                        MascotAndBubbleRecord(
                            modifier = Modifier.weight(2f)
                        )
                        Spacer(modifier = Modifier.weight(2f))
                    }
                }
            }

            // Thanh Mic Bar ở dưới cùng
            if (showMicBottomSheet && subtitlesState is UiState.Success) {
                val subtitles = (subtitlesState as UiState.Success<List<Subtitle>>).data ?: emptyList()

                // ✍️ Hiển thị văn bản đang nói (Transcript)
                if (isRecordTestMode && spokenTranscript.isNotBlank()) {
                    val scrollState = rememberScrollState()
                    LaunchedEffect(spokenTranscript) {
                        scrollState.animateScrollTo(scrollState.maxValue)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 100.dp) // Nằm trên Mic Bar
                            .align(Alignment.BottomCenter)
                            .padding(horizontal = 40.dp)
                            .heightIn(max = 120.dp) // Giới hạn chiều cao tương đương 3-4 dòng
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = spokenTranscript,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color.Yellow,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(scrollState)
                                .padding(8.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier.fillMaxWidth()
                        .height(80.dp)
                        .offset(y= 16.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth().shadow(20.dp),
                        tonalElevation = 0.dp,
                        color = if (isRecordTestMode )Color.Black else Color.White
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 50.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Left Action Button
                            IconButton(
                                onClick = {
                                    if (isRecordTestMode) {
                                        viewModel.exitRecordTestMode()
                                    } else {
                                        if (currentSubtitleIndex > 0) {
                                            val prevIndex = currentSubtitleIndex - 1
                                            viewModel.prevSubtitle()
                                            subtitles.getOrNull(prevIndex)?.startTime?.let { onSeek(it) }
                                        }
                                    }
                                },
                                enabled = isRecordTestMode || currentSubtitleIndex > 0
                            ) {
                                Icon(
                                    painter = painterResource(id = if (isRecordTestMode) R.drawable.ic_cancel_round else R.drawable.ic_back),
                                    contentDescription = "Left Action",
                                    modifier = if (isRecordTestMode) Modifier.size(25.dp) else Modifier.size(32.dp),
                                    tint = if (currentSubtitleIndex > 0 && !isRecordTestMode) Color(0xFF89E219)
                                    else if(isRecordTestMode) Color(0xFFFFFFFF) else Color.LightGray
                                )
                            }

                            // Placeholder để giữ khoảng trống cho mic
                            Spacer(modifier = Modifier.width(40.dp))

                            // Right Action Button
                            IconButton(
                                onClick = {
                                    if (isRecordTestMode) {
                                        viewModel.playBackLastRecord()
                                    } else {
                                        if (currentSubtitleIndex < subtitles.size - 1) {
                                            val nextIndex = currentSubtitleIndex + 1
                                            viewModel.nextSubtitle(subtitles.size)
                                            subtitles.getOrNull(nextIndex)?.startTime?.let { onSeek(it) }
                                        }
                                    }
                                },
                                enabled = isRecordTestMode || currentSubtitleIndex < subtitles.size - 1
                            ) {
                                Icon(
                                    painter = painterResource(id = if (isRecordTestMode) R.drawable.ic_earphone else R.drawable.ic_back),
                                    contentDescription = "Right Action",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .graphicsLayer { if (!isRecordTestMode) rotationY = 180f },
                                    tint = if (currentSubtitleIndex < subtitles.size - 1&& !isRecordTestMode) Color(0xFF89E219)
                                    else if(isRecordTestMode) Color(0xFFFFFFFF) else Color.LightGray
                                )
                            }
                        }
                    }

                    // Mic nhô lên hoặc nút Check
                    val micIcon = when {
                        isQuestionMode -> R.drawable.ic_check_detailed_video_screen
                        !isRecordTestMode -> R.drawable.ic_micro_green
                        recordState == RecordState.RECORDING -> R.drawable.ic_micro_red
                        else -> R.drawable.ic_micro_blue
                    }

                    Image(
                        painter = painterResource(id = micIcon),
                        contentDescription = "Action",
                        modifier = Modifier
                            .size(75.dp)
                            .offset(y = -25.dp)
                            .align(Alignment.BottomCenter)
                            .clickable {
                                when {
                                    isQuestionMode -> viewModel.checkAnswer()
                                    isRecordTestMode -> viewModel.toggleRecord()
                                    else -> {
                                        val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                                        if (hasPermission) {
                                            viewModel.enterRecordTestMode()
                                        } else {
                                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                        }
                                    }
                                }
                            }
                    )
                }
            }
            // Popup kết quả
            if (checkResult != CheckResult.NONE && subtitlesState is UiState.Success) {
                val currentSub = (subtitlesState as UiState.Success<List<Subtitle>>).data?.getOrNull(currentSubtitleIndex)
                ResultPopup(
                    checkResult = checkResult,
                    correctAnswer = currentSub?.content ?: "",
                    onDismiss = { viewModel.dismissResult() }
                )
            }

            // Reward Popup
            if (showRewardPopup) {
                RewardPopup(
                    onDismiss = { viewModel.onRewardDismissed()}
                )
            }
        }
    }
}

@Composable
fun RecordContentBox(
    modifier: Modifier,
    subtitle: String,
    score: Int,
    spokenIndices: Set<Int>
) {
    Box(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Highlightable subtitle text
            val words = subtitle.split(Regex("\\s+")).filter { it.isNotBlank() }
            val annotatedString = buildAnnotatedString {
                words.forEachIndexed { index, word ->
                    val color = if (spokenIndices.contains(index)) Color(0xFF3B82F6) else Color.Gray
                    withStyle(style = SpanStyle(color = color)) {
                        append(word)
                    }
                    if (index < words.size - 1) append(" ")
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(3f), // 👈 phần chiếm space
                contentAlignment = Alignment.Center // 👈 căn giữa
            ) {
                Text(
                    text = annotatedString,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            HorizontalDivider(color = Color(0xFFE5E7EB), thickness = 1.dp)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth().weight(1f).padding(end = 20.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = "Độ chính xác: ",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Black
                )
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ){
                    Text(
                        text = "$score",
                        style = MaterialTheme.typography.displayMedium,
                        color = if (score >= 80) Color(0xFF89E219) else Color(0xFFF44336)
                    )
                    Text(
                        text = " /80%",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.Black
                    )

                }

            }
        }
    }
}

@Composable
fun MascotAndBubbleRecord(modifier: Modifier = Modifier) {
    var start by remember { mutableStateOf(false) }
    var showBubble by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        start = true
        delay(500)
        showBubble = true
    }

    val mascotTransition = updateTransition(targetState = start, label = "mascot")
    val mascotScale by mascotTransition.animateFloat(
        transitionSpec = { spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow) },
        label = "scale"
    ) { if (it) 1f else 0.5f }
    val mascotAlpha by mascotTransition.animateFloat(label = "alpha") { if (it) 1f else 0f }

    val bubbleTransition = updateTransition(targetState = showBubble, label = "bubble")
    val bubbleScale by bubbleTransition.animateFloat(
        transitionSpec = { spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow) },
        label = "scale"
    ) { if (it) 1f else 0.4f }
    val bubbleAlpha by bubbleTransition.animateFloat(label = "alpha") { if (it) 1f else 0f }
    val bubbleOffsetY by bubbleTransition.animateFloat(label = "offset") { if (it) 0f else 50f }

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = modifier.fillMaxWidth()
        ) {
            // Mascot
            Image(
                painter = painterResource(id = R.drawable.ic_looking_mascot),
                contentDescription = "Mascot",
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = mascotScale
                        scaleY = mascotScale
                        alpha = mascotAlpha
                    },
            )

            // Bubble
            Box(
                modifier = Modifier
                    .padding(top= 20.dp,start = 5.dp,end =20.dp)
                    .graphicsLayer {
                        scaleX = bubbleScale
                        scaleY = bubbleScale
                        alpha = bubbleAlpha
                        translationY = bubbleOffsetY
                    }
                    .background(Color.White, RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp, bottomStart = 12.dp))
                    .border(1.2.dp, Color(0xFFE5E7EB), RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp, bottomStart = 12.dp))
                    .padding(horizontal = 15.dp, vertical = 20.dp)
                    .widthIn(max = 250.dp)
            ) {
                Text(
                    text = "Nhắm làm được không đó?!\nĐúng trên 80% sẽ có thưởng đó",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF4B5563)
                )
            }
        }
    }
}

@Composable
fun SingleSubtitleView(
    subtitles: List<Subtitle>,
    currentIndex: Int,
    isRepeatMode: Boolean,
    isQuestionMode: Boolean,
    onToggleRepeat: () -> Unit,
    onToggleQuestion: () -> Unit,
    onClaimReward : () -> Unit,
    userInput: String,
    onUserInputChange: (String) -> Unit,
    writingProgress: Int,
    speakingProgress: Int,
    isRewardEarned: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hàng nút chức năng bên trên
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            UploadButtonLike(
                iconRes = R.drawable.ic_question,
                isActiveMode = isQuestionMode,
                onClick = onToggleQuestion
            )
            EarnPointsButton(
                modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                writingProgress = writingProgress,
                speakingProgress = speakingProgress,
                isRewardEarned = isRewardEarned,
                onClaimReward
            )
            UploadButtonLike(
                iconRes = R.drawable.ic_random,
                isActiveMode = isRepeatMode,
                onClick = onToggleRepeat
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Card hiển thị câu hoặc ô nhập
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = currentIndex to isQuestionMode,
                transitionSpec = {
                    fadeIn().togetherWith(fadeOut())
                },
                label = "SubtitleSlideAnimation"
            ) { (targetIndex, questionMode) ->
                    val scrollState = rememberScrollState()
                    val subtitle = subtitles.getOrNull(targetIndex)
                    Log.d("DEBUG", "subtitle: $subtitle")
                    if (subtitle != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                                .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(24.dp))
                                .padding(20.dp)
                        ) {
                            if (questionMode) {
                                // Ô nhập cho chế độ Question
                                TextField(
                                    value = userInput,
                                    onValueChange = onUserInputChange,
                                    placeholder = { Text("Nhập ở đây...", color = Color.LightGray) },
                                    modifier = Modifier.fillMaxSize(),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        disabledContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                    ),
                                    textStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                )
                            } else {
                                Column(
                                    modifier = Modifier.fillMaxSize()
                                        .verticalScroll(scrollState),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFFF3F4F6)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                                            Text(text = formatTime(subtitle.startTime ?: 0), style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray))
                                        }
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_save_detailed_video_screen),
                                            contentDescription = "Save",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFF3F4F6)).padding(8.dp).clickable { /* TODO */ }
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(15.dp))

                                    Text(
                                        text = subtitle.content ?: "",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp, textAlign = TextAlign.Center),
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )

                                    if (!subtitle.pronunciation.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = subtitle.pronunciation,
                                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray,  fontSize = 14.sp,textAlign = TextAlign.Center),
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(15.dp))

                                    Text(
                                        text = subtitle.translation ?: "",
                                        style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center, fontSize = 15.sp),
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
        }
    }
}

@Composable
fun ResultPopup(
    checkResult: CheckResult,
    correctAnswer: String,
    onDismiss: () -> Unit
) {
    val isCorrect = checkResult == CheckResult.CORRECT
    val bgColor = if (isCorrect) Color(0xFFD7FFA4) else Color(0xFFFFDFE0)
    val textColor = if (isCorrect) Color(0xFF46A302) else Color(0xFFEE2D30)
    val buttonColor = if (isCorrect) Color(0xFF58CC02) else Color(0xFFFF6063)
    val buttonShadowColor = if (isCorrect) Color(0xFF46A302) else Color(0xFFFF383C)
    val iconRes = if (isCorrect) R.drawable.ic_check_green_circle else R.drawable.ic_close_red_circle

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(bgColor)
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (isCorrect) "CHÍNH XÁC !" else "ĐÁP ÁN ĐÚNG LÀ :",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = textColor,
                    )
                )
            }

            if (!isCorrect) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = correctAnswer,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = textColor,
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Custom Button like BigPotagoButton
            CustomPopupButton(
                text = if (isCorrect) "TIẾP TỤC" else "ĐÃ HIỂU",
                buttonColor = buttonColor,
                shadowColor = buttonShadowColor,
                onClick = onDismiss
            )
        }
    }
}

@Composable
fun CustomPopupButton(
    text: String,
    buttonColor: Color,
    shadowColor: Color,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val animatedHeight by animateDpAsState(targetValue = if (isPressed) 52.dp else 48.dp, label = "")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(shadowColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(animatedHeight)
                .clip(RoundedCornerShape(16.dp))
                .background(buttonColor)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { isPressed = true; tryAwaitRelease(); isPressed = false },
                        onTap = { onClick() }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun RewardPopup(
    onDismiss: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()

    val rotation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 800,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            contentAlignment = Alignment.BottomCenter
        ) {
            // Background Image
            Image(
                painter = painterResource(id = R.drawable.ic_record_test_mode_reward),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )

            Column(
                modifier = Modifier
                    .matchParentSize()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.width(10.dp))
                // White Box Content
                Box(
                    modifier = Modifier
                        .fillMaxWidth().padding(vertical = 50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Experience Points
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "15",
                                style = MaterialTheme.typography.displayLarge.copy(
                                    color = Color(0xFFA16207),
                                    fontSize = 42.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Image(
                                painter = painterResource(id = R.drawable.ic_experience_points),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(50.dp)
                                    .graphicsLayer { rotationZ = -45f +rotation}
                            )
                        }

                        // Diamond Points
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "20",
                                style = MaterialTheme.typography.displayLarge.copy(
                                    color = Color(0xFFF44336),
                                    fontSize = 42.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Image(
                                painter = painterResource(id = R.drawable.ic_ruby_detailed_video_screen),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(50.dp)
                                    .graphicsLayer { rotationZ = 21f +rotation}
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(60.dp))

                // Action Button
                CustomPopupButton(
                    text = "NHẬN",
                    buttonColor = Color(0xFF58CC02),
                    shadowColor = Color(0xFF46A302),
                    onClick = onDismiss
                )
            }
        }
    }
}

@Composable
fun UploadButtonLike(
    iconRes: Int,
    isActiveMode : Boolean = false,
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
            .border(1.2.dp, if (isActiveMode) Color(0xFF46A302)else Color(0xFFE5E5E5), RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(color =if (isActiveMode) Color(0xFF46A302)else Color(0xFFE5E5E5))
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
                tint = if (isActiveMode) Color(0xFF58CC02) else Color.LightGray,
                modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
private fun EarnPointsButton(
    modifier: Modifier,
    writingProgress: Int,
    speakingProgress: Int,
    isRewardEarned: Boolean,
    onClick: () -> Unit
) {
    val isAllDone = writingProgress >= 1 && speakingProgress >= 0
    val isClickable = isAllDone && !isRewardEarned
    var isPressed by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(targetValue = if (isPressed) 0.96f else 1f, label = "")
    val animatedHeight by animateDpAsState(targetValue = if (isPressed) 52.dp else 48.dp, label = "")

    val borderColor = if (isRewardEarned) Color(0xFF4B5563) else if (isAllDone) Color(0xFF46A302) else Color(0xFFE5E5E5)
    val bgColor = if (isRewardEarned) Color(0xFFF3F4F6) else Color.White
    val textColor = if (isRewardEarned) Color.Gray else if (isAllDone) Color(0xFF58CC02) else Color.LightGray

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .graphicsLayer { scaleX = animatedScale; scaleY = animatedScale }
            .border(1.2.dp, borderColor, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(color = if (isRewardEarned) Color(0xFF4B5563) else if (isAllDone) Color(0xFF46A302) else Color(0xFFE5E5E5))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(animatedHeight)
                .clip(RoundedCornerShape(16.dp))
                .background(color = bgColor)
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .pointerInput(isClickable) {
                    if (isClickable) {
                        detectTapGestures(
                            onPress = { isPressed = true; tryAwaitRelease(); isPressed = false },
                            onTap = { onClick() }
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isRewardEarned) "Đã nhận thưởng" else "Số câu đã luyện nói đúng : $speakingProgress/3",
                    style = MaterialTheme.typography.labelSmall.copy(color = textColor, fontSize = 12.sp)
                )
                if (!isRewardEarned) {
                    Text(
                        text = "Số câu đã luyện viết đúng : $writingProgress/3",
                        style = MaterialTheme.typography.labelSmall.copy(color = textColor, fontSize = 12.sp)
                    )
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YoutubeWebView(
    videoId: String,
    startPositionMs: Long = 0L,
    onPlayerReady: (WebView) -> Unit,
    onTimeUpdate: (Long) -> Unit
) {
    val context = LocalContext.current
    val packageName = context.packageName
    val startSeconds = startPositionMs / 1000f
    val embedHtml = """
        <!DOCTYPE html>
        <html>
        <head>
            <style>body { margin: 0; padding: 0; background: black; } .container { position: relative; padding-bottom: 56.25%; height: 0; overflow: hidden; } iframe { position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: 0; }</style>
        </head>
        <body>
            <div class="container">
                <div id="player"></div>
            </div>
            <script>
                var tag = document.createElement('script');
                tag.src = "https://www.youtube.com/iframe_api";
                var firstScriptTag = document.getElementsByTagName('script')[0];
                firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

                var player;
                var timeInterval = null;

                function onYouTubeIframeAPIReady() {
                    player = new YT.Player('player', {
                        height: '100%', width: '100%',
                        videoId: '$videoId',
                        playerVars: { 'autoplay': 1, 'rel': 0, 'controls': 1, 'fs': 0, 'origin': 'https://$packageName' },
                        events: { 'onReady': onPlayerReady }
                    });
                }

                function onPlayerReady(event) {
                    var startPos = $startSeconds;
                    if (startPos > 0) {
                        event.target.seekTo(startPos, true);
                    }
                    event.target.playVideo();
                    if (timeInterval) clearInterval(timeInterval);
                    timeInterval = setInterval(function() {
                        if (player && player.getCurrentTime) {
                            Android.onTimeUpdate(player.getCurrentTime());
                        }
                    }, 500);
                }

                function seekTo(seconds) {
                    if (player) {
                        player.seekTo(seconds, true);
                        player.playVideo();
                    }
                }

                function destroyPlayer() {
                    if (timeInterval) {
                        clearInterval(timeInterval);
                        timeInterval = null;
                    }
                    if (player) {
                        player.stopVideo();
                        player.destroy();
                        player = null;
                    }
                }
            </script>
        </body>
        </html>
    """.trimIndent()

    AndroidView(
        modifier = Modifier.fillMaxWidth().aspectRatio(16 / 9f),
        factory = { ctx ->
            WebView(ctx).apply {
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    mediaPlaybackRequiresUserGesture = false
                }
                addJavascriptInterface(object {
                    @JavascriptInterface
                    fun onTimeUpdate(seconds: Float) {
                        onTimeUpdate((seconds * 1000).toLong())
                    }
                }, "Android")
                webViewClient = WebViewClient()
                webChromeClient = WebChromeClient()
                loadDataWithBaseURL("https://$packageName", embedHtml, "text/html", "UTF-8", null)
                onPlayerReady(this)
            }
        },
        onRelease = { webView ->
            webView.evaluateJavascript("destroyPlayer();", null)
            webView.destroy()
        }
    )
}

@OptIn(UnstableApi::class)
@Composable
fun ExoPlayerView(videoUrl: String, onPlayerReady: (ExoPlayer) -> Unit, onTimeUpdate: (Long) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val player = remember { ExoPlayer.Builder(context).build().apply { setMediaItem(MediaItem.fromUri(videoUrl)); prepare(); onPlayerReady(this) } }

    LaunchedEffect(player) {
        while (true) { if (player.isPlaying) onTimeUpdate(player.currentPosition); delay(200) }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> player.pause()
                Lifecycle.Event.ON_RESUME -> player.play()
                Lifecycle.Event.ON_DESTROY -> player.release()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer); player.release() }
    }

    AndroidView(modifier = Modifier.fillMaxWidth().aspectRatio(16/9f), factory = { PlayerView(context).apply { this.player = player; useController = true } })
}

fun extractYoutubeVideoId(url: String): String? {
    val patterns = listOf("(?:v=|/v/|/embed/|/shorts/|youtu.be/|/videos/|embed\\?v=)([^#&?\\s]+)", "youtu.be/([^#&?\\s]+)", "youtube.com/watch\\?v=([^#&?\\s]+)")
    for (p in patterns) {
        val matcher = Pattern.compile(p).matcher(url)
        if (matcher.find()) return matcher.group(1)
    }
    return null
}

@Composable
fun TabRowSection(selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)).border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))) {
        TabItem(text = "Xem danh sách", iconRes = R.drawable.ic_list_detailed_video_screen, isSelected = selectedTabIndex == 0, onClick = { onTabSelected(0) }, modifier = Modifier.weight(1f))
        TabItem(text = "Xem từng câu", iconRes = R.drawable.ic_card_detailed_video_screen, isSelected = selectedTabIndex == 1, onClick = { onTabSelected(1) }, modifier = Modifier.weight(1f))
    }
}

@Composable
fun TabItem(text: String, iconRes: Int, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.background(if (isSelected) Color.White else Color(0xFFF3F4F6)).clickable { onClick() }.padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(painter = painterResource(id = iconRes), contentDescription = null, tint = if (isSelected) Color(0xFF3B82F6) else Color.Gray, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, style = MaterialTheme.typography.labelSmall.copy( color = if (isSelected) Color(0xFF3B82F6) else Color.Gray))
        }
    }
}

@Composable
fun SubtitleList(subtitles: List<Subtitle>, currentTimeMs: Long, onSubtitleClick: (Int) -> Unit) {
    val listState = rememberLazyListState()
    val activeIndex = remember(currentTimeMs, subtitles) { subtitles.indexOfLast { (it.startTime?.toLong() ?: 0L) <= currentTimeMs } }
    LaunchedEffect(activeIndex) { if (activeIndex != -1) listState.animateScrollToItem(activeIndex, scrollOffset = -200) }
    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
        itemsIndexed(subtitles) { index, subtitle -> SubtitleItem(subtitle = subtitle, isHighlighted = index == activeIndex, onClick = { onSubtitleClick(subtitle.startTime ?: 0) }) }
    }
}

@Composable
fun SubtitleItem(subtitle: Subtitle, isHighlighted: Boolean, onClick: () -> Unit) {
    val backgroundColor by animateColorAsState(targetValue = if (isHighlighted) Color(0xFFD6FFA3) else Color.White, label = "bgColor")
    Column(modifier = Modifier.fillMaxWidth().background(backgroundColor).clickable { onClick() }.drawBehind { val strokeWidth = 1.dp.toPx(); drawLine(color = Color(0xFFEEEEEE), start = Offset(0f, size.height - strokeWidth / 2), end = Offset(size.width, size.height - strokeWidth / 2), strokeWidth = strokeWidth) }.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFFF3F4F6)).padding(horizontal = 8.dp, vertical = 4.dp)) { Text(text = formatTime(subtitle.startTime ?: 0), style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)) }
            Icon(painter = painterResource(id = R.drawable.ic_save_detailed_video_screen), contentDescription = "Save", tint = Color.Gray, modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFF3F4F6)).padding(6.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = subtitle.content ?: "", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp))
        if (!subtitle.pronunciation.isNullOrBlank()) Text(text = subtitle.pronunciation, style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontSize = 14.sp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = subtitle.translation ?: "", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp))
    }
}

fun formatTime(ms: Int): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

@Composable
private fun TopAppBar(onBackClick: () -> Unit = {}) {
    Surface(modifier = Modifier.fillMaxWidth(), tonalElevation = 3.dp, shadowElevation = 4.dp, color = Color(0xFFFFFFFF)) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            BackButton(onBackClick)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Chi tiết video", style = MaterialTheme.typography.displayMedium)
        }
    }
}

@Composable
private fun BackButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.85f else 1f, label = "icon_scale")
    IconButton(onClick = onClick, interactionSource = interactionSource) { Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Back", modifier = Modifier.scale(scale)) }
}

@Preview (showBackground = true)
@Composable
fun RewardPopupShow(){
    RewardPopup(onDismiss = {})
}