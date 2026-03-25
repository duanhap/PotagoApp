package com.example.potago.presentation.screen.recommendvideo

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.potago.R
import com.example.potago.domain.model.Video
import com.example.potago.presentation.navigation.Screen
import com.example.potago.presentation.screen.UiState
import com.example.potago.presentation.ui.component.ShimmerItem
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RecommendVideoScreen(
    navController: NavController,
    viewModel: RecommendVideoViewModel = hiltViewModel()
) {
    val videos by viewModel.videos.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Xử lý điều hướng
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest { videoId ->
            navController.navigate(Screen.DetailedVideo(videoId))
        }
    }

    // Xử lý lỗi
    LaunchedEffect(Unit) {
        viewModel.errorEvent.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(onBackClick = { navController.popBackStack() })
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding))
        RecommendVideoContent(
            videos = videos,
            uiState = uiState,
            onLanguageSelected = { index ->
                viewModel.onLanguageTabSelected(index)
            },
            onLoadMore = {
                viewModel.loadMoreVideos()
            },
            onVideoClick = { videoId ->
                viewModel.onVideoClick(videoId)
            }
        )
    }
}

@Composable
private fun RecommendVideoContent(
    modifier: Modifier = Modifier,
    videos: List<Video>,
    uiState: UiState<Unit>,
    onLanguageSelected: (Int) -> Unit,
    onLoadMore: () -> Unit,
    onVideoClick: (Int) -> Unit
) {
    val languages = listOf("English", "日本語", "汉语")
    var selectedIndex by remember { mutableStateOf(0) }
    val listState = rememberLazyListState()

    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItemIndex >= videos.size - 2 && videos.isNotEmpty()
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            onLoadMore()
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize()
            .background(color = Color.White),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item{
            Spacer(modifier = Modifier.height(80.dp))
        }
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(languages) { index, lang ->
                    FilterTab(
                        text = lang,
                        isSelected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                            onLanguageSelected(index)
                        }
                    )
                }
            }
        }

        if (uiState is UiState.Success && videos.isEmpty()) {
            item {
                EmptyVideosView()
            }
        } else {
            items(videos) { video ->
                VideoItem(
                    video = video,
                    onClick = { onVideoClick(video.id) }
                )
            }
        }

        when (uiState) {
            is UiState.Loading -> {
                if (videos.isEmpty()) {
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
                        text = uiState.message,
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
            text = video.title ?: "Untitled",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                lineHeight = 20.sp
            ),
            maxLines = 2
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
                .fillMaxWidth(0.8f)
                .height(20.dp),
            shape = RoundedCornerShape(4.dp)
        )
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton(onBackClick)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Đề xuất video",
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
fun RecommendVideoScreenPreview() {
    RecommendVideoScreen(navController = NavController(LocalContext.current))
}
