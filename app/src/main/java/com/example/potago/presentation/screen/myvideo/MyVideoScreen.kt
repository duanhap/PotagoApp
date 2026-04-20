package com.example.potago.presentation.screen.myvideo

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.potago.R
import com.example.potago.domain.model.Video
import com.example.potago.presentation.screen.UiEvent
import com.example.potago.presentation.screen.UiState
import com.example.potago.presentation.screen.setting.BackButton
import com.example.potago.presentation.ui.component.ShimmerItem

@Composable
fun MyVideoScreen(
    navController: NavController,
    viewModel: MyVideoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onEvent(MyVideoEvent.Refresh)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is UiEvent.Navigate -> {
                    navController.navigate(event.route) {
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
        topBar = {
            TopAppBar(
                onBackClick = { navController.popBackStack() },
                onAddClick = { viewModel.onEvent(MyVideoEvent.NavigateToAddVideo) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ProcessingFab(
                processingJob = uiState.processingJob,
                onClick = { viewModel.onEvent(MyVideoEvent.NavigateToManageVideo) }
            )
        }
    ) { innerPadding ->
        // Giữ nguyên theo yêu cầu của user: Box này có vẻ thừa nhưng user muốn giữ để làm đẹp
        Box(modifier = Modifier.padding(innerPadding))
        
        MyVideoContent(
            uiState = uiState,
            onEvent = viewModel::onEvent
        )
    }
}

@Composable
private fun MyVideoContent(
    modifier: Modifier = Modifier,
    uiState: MyVideoUiState,
    onEvent: (MyVideoEvent) -> Unit
) {
    val tabs = listOf("All", "Youtube", "File")
    val listState = rememberLazyListState()

    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItemIndex >= uiState.videos.size - 2 && uiState.videos.isNotEmpty()
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            onEvent(MyVideoEvent.LoadMore)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize()
            .background(color = Color.White),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Giữ nguyên theo yêu cầu của user: Spacer này để tạo khoảng trống phía trên
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }

        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tabs) { tab ->
                    FilterTab(
                        text = tab,
                        isSelected = uiState.selectedTab == tab,
                        onClick = { onEvent(MyVideoEvent.TabSelected(tab)) }
                    )
                }
            }
        }

        if (uiState.videosUiState is UiState.Success && uiState.videos.isEmpty()) {
            item {
                EmptyVideosView()
            }
        } else {
            items(uiState.videos) { video ->
                VideoItem(
                    video = video,
                    onClick = { onEvent(MyVideoEvent.VideoClicked(video.id)) }
                )
            }
        }

        when (val state = uiState.videosUiState) {
            is UiState.Loading -> {
                if (uiState.videos.isEmpty()) {
                    items(5) {
                        VideoItemShimmer()
                    }
                } else {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }
            is UiState.Error -> {
                item {
                    Text(
                        text = state.message,
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            else -> {}
        }
    }
}

@Composable
fun ProcessingFab(
    processingJob: com.example.potago.data.local.ProcessingJob?,
    onClick: () -> Unit
) {
    Box(contentAlignment = Alignment.Center) {
        if (processingJob != null) {
            CircularProgressIndicator(
                progress = { (processingJob.progress.toFloat()) / 100f },
                modifier = Modifier.size(64.dp),
                color = Color(0xFF58CC02),
                strokeWidth = 3.dp,
                trackColor = Color(0xFFEEEEEE)
            )
        }
        
        Surface(
            shape = CircleShape,
            border = if (processingJob == null) BorderStroke(1.dp, Color(0x4D3B82F6)) else null,
            color = Color.Transparent
        ) {
            FloatingActionButton(
                onClick = onClick,
                containerColor = Color.White,
                contentColor = Color(0xFF4285F4),
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_folder),
                    contentDescription = "Folder",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyVideosView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(120.dp))
        Image(
            painter = painterResource(id = R.drawable.horizon_sleep_mascot),
            contentDescription = null,
            modifier = Modifier.size(150.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Chưa có video nào cả",
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.Gray,
            )
        )
    }
}

@Composable
fun FilterTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) Color.Black else Color.LightGray.copy(alpha = 0.5f)
        ),
        color = Color.Transparent
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) Color.Black else Color.Gray
        )
    }
}

@Composable
fun VideoItem(video: Video, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        AsyncImage(
            model = video.thumbnail,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray),
            contentScale = ContentScale.Crop,
            error = painterResource(id = R.drawable.video_screen_mascot)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = video.title ?: "",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        )
    }
}

@Composable
fun VideoItemShimmer() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        ShimmerItem(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        ShimmerItem(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(20.dp),
            shape = RoundedCornerShape(4.dp)
        )
    }
}

@Composable
private fun TopAppBar(
    onBackClick: () -> Unit = {},
    onAddClick: () -> Unit = {}
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
                    text = "Video của bạn",
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier.weight(1f)
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
            // 🔥 trick ở đây
            Box(
                modifier = Modifier.matchParentSize()
            ) {
                AddButton(
                    onAddClick,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .wrapContentSize()
                )
            }
        }
    }
}

@Composable
fun AddButton(
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
            painter = painterResource(id = R.drawable.ic_add),
            contentDescription = "Add",
            modifier = Modifier.scale(scale)
        )
    }
}



@Preview(showBackground = true)
@Composable
fun MyVideoScreenPreview() {
    MyVideoScreen(navController = NavController(LocalContext.current))
}
