package com.example.potago.presentation.screen.myvideo

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.example.potago.presentation.screen.UiState
import com.example.potago.presentation.ui.component.ShimmerItem

@Composable
fun MyVideoScreen(
    navController: NavController,
    viewModel: MyVideoViewModel = hiltViewModel()
) {
    val videos by viewModel.videos.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                onBackClick = { navController.popBackStack() },
                onAddClick = { /* Handle Add */ }
            )
        },
        floatingActionButton = {
            Surface(
                shape = CircleShape,
                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                color = Color.Transparent
            ) {
                FloatingActionButton(
                    onClick = { /* Handle Folder */ },
                    containerColor = Color.White,
                    contentColor = Color(0xFF4285F4),
                    shape = CircleShape
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_folder),
                        contentDescription = "Folder",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding))
        MyVideoContent(
            videos = videos,
            uiState = uiState,
            selectedTab = selectedTab,
            onTabSelected = { viewModel.onTabSelected(it) },
            onLoadMore = { viewModel.loadMoreVideos() }
        )
    }
}

@Composable
private fun MyVideoContent(
    modifier: Modifier = Modifier,
    videos: List<Video>,
    uiState: UiState<Unit>,
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    onLoadMore: () -> Unit
) {
    val tabs = listOf("All", "Youtube", "File")
    val listState = rememberLazyListState()

    // Detect when reaching the end of the list
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
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp) // Space for FAB
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
                items(tabs) { tab ->
                    FilterTab(
                        text = tab,
                        isSelected = selectedTab == tab,
                        onClick = { onTabSelected(tab) }
                    )
                }
            }
        }

        // Video List
        items(videos) { video ->
            VideoItem(video = video)
        }

        // Loading/Error states
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
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) Color.Black else Color.Gray
        )
    }
}

@Composable
fun VideoItem(video: Video) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton(onBackClick)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Video của bạn",
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.weight(1f)
            )
            AddButon(onAddClick)
        }
    }
}

@Composable
fun AddButon(
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
            painter = painterResource(id = R.drawable.ic_add),
            contentDescription = "Add",
            modifier = Modifier.scale(scale)
        )
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
fun MyVideoScreenPreview(){
    MyVideoScreen(navController = NavController(LocalContext.current))
}
