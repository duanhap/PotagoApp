package com.example.potago.presentation.screen.managevideo

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.potago.R
import com.example.potago.data.local.ProcessingJob
import com.example.potago.domain.model.Video
import com.example.potago.presentation.screen.UiEvent
import com.example.potago.presentation.screen.UiState
import com.example.potago.presentation.screen.video.EmptyBoxView
import com.example.potago.presentation.ui.theme.Nunito
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageVideoScreen(
    navController: NavController,
    viewModel: ManageVideoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState()
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var videoToDelete by remember { mutableStateOf<Video?>(null) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is UiEvent.Navigate -> {
                    navController.navigate(event.route) {
                        event.popUpTo?.let {
                            popUpTo(it) { inclusive = event.inclusive }
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                onBackClick = {
                    if (!uiState.isCanceling && !uiState.isDeleting) navController.popBackStack()
                }
            )
        }
    ) { innerPadding ->
        // Giữ nguyên theo yêu cầu: Box này dùng để làm đẹp giao diện
        Box(modifier = Modifier.padding(innerPadding))

        ManageVideoContent(
            uiState = uiState,
            onEvent = viewModel::onEvent,
            onDeleteClick = { video ->
                videoToDelete = video
                showDeleteConfirm = true
            }
        )

        if (showDeleteConfirm) {
            ModalBottomSheet(
                onDismissRequest = { showDeleteConfirm = false },
                sheetState = sheetState,
                containerColor = Color.White,
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                DeleteConfirmationContent(
                    onConfirm = {
                        videoToDelete?.id?.let { viewModel.onEvent(ManageVideoEvent.DeleteVideo(it)) }
                        showDeleteConfirm = false
                    },
                    onDismiss = { showDeleteConfirm = false }
                )
            }
        }

        // Lớp phủ chặn tương tác khi đang xử lý
        val isBusy = uiState.isCanceling || uiState.isDeleting || (uiState.uiState is UiState.Loading && uiState.processedVideos.isEmpty())
        if (isBusy) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
                    .pointerInput(Unit) { }
            )
        }
    }
}

@Composable
private fun ManageVideoContent(
    modifier: Modifier = Modifier,
    uiState: ManageVideoUiState,
    onEvent: (ManageVideoEvent) -> Unit,
    onDeleteClick: (Video) -> Unit
) {
    val listState = rememberLazyListState()

    val filteredVideos = remember(uiState.processedVideos, uiState.processingJob) {
        uiState.processedVideos.filter { it.id != uiState.processingJob?.video?.id }
    }

    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItemIndex >= (filteredVideos.size + 1) && !uiState.isLastPage
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            onEvent(ManageVideoEvent.LoadMore)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Giữ nguyên theo yêu cầu: Khoảng cách tạo độ thoáng cho UI
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }

        item {
            Text(
                text = "Video đang xử lý",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            if (uiState.processingJob != null) {
                ProcessingVideoItem(
                    job = uiState.processingJob,
                    isLoading = uiState.isCanceling,
                    onCancel = { onEvent(ManageVideoEvent.CancelJob) }
                )
            } else {
                EmptyProcessingVideosView()
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        item {
            Text(
                text = "Video đã xử lý",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        items(filteredVideos) { video ->
            ProcessedVideoItem(
                video = video,
                onDeleteClick = { onDeleteClick(video) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (uiState.uiState is UiState.Loading) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
        } else if (uiState.uiState is UiState.Success && filteredVideos.isEmpty()) {
            item {
                EmptyBoxView()
            }
        }
    }
}

@Composable
private fun ProcessingVideoItem(
    job: ProcessingJob,
    isLoading: Boolean,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = job.video.thumbnail,
                contentDescription = null,
                modifier = Modifier
                    .size(width = 215.dp, height = 121.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.video_screen_mascot)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = job.video.title ?: "Đang tải tiêu đề...",
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 3
                )

                CancelButton(
                    isLoading = isLoading,
                    onClick = onCancel
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            StripedProgressIndicator(
                progress = (job.progress.toFloat() / 100f).coerceIn(0f, 1f),
                modifier = Modifier
                    .weight(1f)
                    .height(12.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "${job.progress}%",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
private fun ProcessedVideoItem(
    video: Video,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = video.thumbnail,
            contentDescription = null,
            modifier = Modifier
                .size(width = 153.dp, height = 86.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray),
            contentScale = ContentScale.Crop,
            error = painterResource(id = R.drawable.video_screen_mascot)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = video.title ?: "Untitled",
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2
            )

            Text(
                text = formatDate(video.createdAt ?: "Unknown"),
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color(0xFFFFA9A3).copy(alpha = 0.5f), CircleShape)
                .clickable { onDeleteClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_bin),
                contentDescription = "Delete",
                tint = Color(0xFFEF4444),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun EmptyProcessingVideosView() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.horizon_sleep_mascot),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Không có video nào đang xử lý",
            style = MaterialTheme.typography.titleMedium.copy(color = Color.Gray)
        )
    }
}

@Composable
private fun DeleteConfirmationContent(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MascotAndBubbleDelete(text = "Xác nhận xóa chứ !?")

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SmallWhiteButton(
                text = "Từ chối",
                modifier = Modifier.weight(1f),
                onClick = onDismiss
            )
            SmallGreenButton(
                text = "Xác nhận",
                modifier = Modifier.weight(1f),
                onClick = onConfirm
            )
        }
    }
}

@Composable
fun SmallGreenButton(
    text: String,
    modifier: Modifier = Modifier,
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
        modifier = modifier
            .height(48.dp)
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .border(1.2.dp, Color(0xFF46A302), RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(color = Color(0xFF46A302))
    ) {
        Box(modifier = modifier
            .fillMaxWidth()
            .height(animatedHeight)
            .clip(RoundedCornerShape(16.dp))
            .background(color = Color(0xFF58CC02))
            .pointerInput(buttonEnabled) {
                detectTapGestures(
                    onPress = {
                        if (!buttonEnabled) return@detectTapGestures
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { if (buttonEnabled) onClick() }
                )
            }, contentAlignment = Alignment.Center){
            Text(
                text = text,
                style = TextStyle(
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = Color.White
                )
            )
        }
    }
}

@Composable
fun SmallWhiteButton(
    text: String,
    modifier: Modifier = Modifier,
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
        modifier = modifier
            .height(48.dp)
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .border(1.2.dp, Color(0xFFE5E5E5), RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(color = Color(0xFFE5E5E5))
    ) {
        Box(modifier = modifier
            .fillMaxWidth()
            .height(animatedHeight)
            .clip(RoundedCornerShape(16.dp))
            .background(color = Color.White)
            .pointerInput(buttonEnabled) {
                detectTapGestures(
                    onPress = {
                        if (!buttonEnabled) return@detectTapGestures
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { if (buttonEnabled) onClick() }
                )
            }, contentAlignment = Alignment.Center){
            Text(
                text = text,
                style = TextStyle(
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = Color(0xFF4B4B4B)
                )
            )
        }
    }
}

@Composable
private fun MascotAndBubbleDelete(text: String) {
    var start by remember { mutableStateOf(false) }
    var showBubble by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
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
        modifier = Modifier.wrapContentSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.asking_mascot_manage_video_screen),
            contentDescription = "Mascot",
            modifier = Modifier
                .size(160.dp)
                .graphicsLayer {
                    scaleX = mascotScale
                    scaleY = mascotScale
                    alpha = mascotAlpha
                }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
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
                color = Color(0xFF4B5563)
            )
        }
    }
}

@Composable
private fun CancelButton(
    buttonEnabled: Boolean = true,
    isLoading: Boolean = false,
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
            .fillMaxWidth()
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
            .background(color = Color.White)
            .pointerInput(buttonEnabled && !isLoading) {
                detectTapGestures(
                    onPress = {
                        if (!buttonEnabled || isLoading) return@detectTapGestures
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { if (buttonEnabled && !isLoading) onClick() }
                )
            }, contentAlignment = Alignment.Center){
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.Gray,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(painter = painterResource(id = R.drawable.ic_cancel), contentDescription = "Cancel", tint = Color.Gray)
            }
        }
    }
}

@Composable
fun StripedProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "striped_progress")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset"
    )

    Canvas(modifier = modifier.clip(RoundedCornerShape(6.dp))) {
        val width = size.width
        val height = size.height

        drawRoundRect(
            color = Color(0xFFEEEEEE),
            size = size,
            cornerRadius = CornerRadius(height / 2, height / 2)
        )

        val progressWidth = width * progress
        if (progressWidth > 0) {
            clipRect(right = progressWidth) {
                drawRoundRect(
                    color = Color(0xFF4B4B4B),
                    size = size,
                    cornerRadius = CornerRadius(height / 2, height / 2)
                )

                val stripeWidth = 15.dp.toPx()
                val gap = 15.dp.toPx()
                val skewWidth = 10.dp.toPx()

                var x = -stripeWidth * 2 + offset.dp.toPx()
                while (x < width + stripeWidth) {
                    val path = Path().apply {
                        moveTo(x + skewWidth, 0f)
                        lineTo(x + stripeWidth + skewWidth, 0f)
                        lineTo(x + stripeWidth, height)
                        lineTo(x, height)
                        close()
                    }
                    drawPath(path, color = Color(0xFF9CA3AF))
                    x += stripeWidth + gap
                }
            }
        }
    }
}

@Composable
private fun TopAppBar(onBackClick: () -> Unit = {}) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 3.dp,
        shadowElevation = 4.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton(onBackClick)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Quản lý video",
                style = MaterialTheme.typography.displayMedium,
            )
        }
    }
}

@Composable
private fun BackButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.85f else 1f, label = "")
    IconButton(onClick = onClick, interactionSource = interactionSource) {
        Icon(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = "Back",
            modifier = Modifier.scale(scale)
        )
    }
}

fun formatDate(dateStr: String): String {
    return try {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
        val outputFormatter = DateTimeFormatter.ofPattern("'Tháng' M 'năm' yyyy", Locale("vi"))
        val date = LocalDate.parse(dateStr, inputFormatter)
        date.format(outputFormatter)
    } catch (e: Exception) {
        dateStr
    }
}