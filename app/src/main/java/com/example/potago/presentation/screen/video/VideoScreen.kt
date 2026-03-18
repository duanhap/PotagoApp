package com.example.potago.presentation.screen.video

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@Composable
fun VideoScreen(
    navController: NavController,
    viewModel: VideoViewModel = hiltViewModel()
) {
    val recommendedVideosState by viewModel.recommendedVideos.collectAsState()
    val myVideosState by viewModel.myVideos.collectAsState()
    val recentVideosState by viewModel.recentVideos.collectAsState()
    val selectedLangIndex by viewModel.selectedLangIndex.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar()
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Image(
                    painter = painterResource(id = R.drawable.video_screen_mascot),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                SectionHeader(
                    title = "Đề xuất",
                    onSeeMoreClick = { navController.navigate(Screen.RecommendVideo.route) },
                    showSeeMore = true
                )
                FilterChips(
                    selectedIndex = selectedLangIndex,
                    onTabSelected = { index ->
                        viewModel.onLanguageTabSelected(index)
                    }
                )
                VideoListHorizontal(
                    uiState = recommendedVideosState,
                    onVideoClick = { videoId ->
                        // Đề xuất thì chưa cần vào detailed video vội hoặc tùy logic
                        // Theo yêu cầu chỉ nhấn vào Gần đây xem và Video của bạn
                    }
                )
            }

            item {
                SectionHeader(title = "Gần đây xem", onSeeMoreClick = {}, showSeeMore = false)
                VideoListHorizontal(
                    uiState = recentVideosState,
                    onVideoClick = { videoId ->
                        navController.navigate(Screen.DetailedVideo(videoId))
                    }
                )
            }

            item {
                SectionHeader(
                    title = "Video của bạn",
                    onSeeMoreClick = { navController.navigate(Screen.MyVideo.route) },
                    showSeeMore = true
                )
                VideoListHorizontal(
                    uiState = myVideosState,
                    onVideoClick = { videoId ->
                        navController.navigate(Screen.DetailedVideo(videoId))
                    }
                )
            }
        }
    }
}

@Composable
private fun TopAppBar() {
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
            Text(
                text = "Video",
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    onSeeMoreClick: () -> Unit,
    showSeeMore: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black
        )
        if (showSeeMore) {
            Text(
                text = "Xem thêm",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = Color(0xFF3B82F6),
                ),
                modifier = Modifier.clickable { onSeeMoreClick() }
            )
        }
    }
}

@Composable
fun FilterChips(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    val filters = listOf("English", "日本語", "汉语")

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        items(filters.size) { index ->
            val isSelected = selectedIndex == index
            Box(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = if (isSelected) Color.Black else Color.LightGray,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .clickable {
                        onTabSelected(index)
                    }
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = filters[index],
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun VideoListHorizontal(
    uiState: UiState<List<Video>>,
    onVideoClick: (Int) -> Unit
) {
    when (uiState) {
        is UiState.Loading -> {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                items(3) {
                    VideoItemShimmer()
                }
            }
        }
        is UiState.Success -> {
            val videos = uiState.data
            if (videos.isNullOrEmpty()) {
                EmptyBoxView()
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    items(videos.size) { index ->
                        VideoItem(
                            video = videos[index],
                            onClick = { onVideoClick(videos[index].id) }
                        )
                    }
                }
            }
        }
        is UiState.Error -> {
            Text(text = uiState.message, color = Color.Red, modifier = Modifier.padding(vertical = 8.dp))
        }
        else -> {}
    }
}

@Composable
fun EmptyBoxView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.empty_box),
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Không có video nào",
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.Gray,
            )
        )
    }
}

@Composable
fun VideoItem(video: Video, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(196.dp)
            .clickable { onClick() }
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
            error = painterResource(id = R.drawable.video_screen_mascot) // Placeholder error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = video.title ?: "Untitled",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
            maxLines = 2
        )
    }
}

@Composable
fun VideoItemShimmer() {
    Column(modifier = Modifier.width(196.dp)) {
        ShimmerItem(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        ShimmerItem(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp),
            shape = RoundedCornerShape(4.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        ShimmerItem(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(16.dp),
            shape = RoundedCornerShape(4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VideoScreenPreview() {
    VideoScreen(navController = NavController(LocalContext.current))
}
