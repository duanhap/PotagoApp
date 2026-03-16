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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
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
import com.example.potago.R
import com.example.potago.presentation.screen.UiState
import com.example.potago.presentation.ui.component.ShimmerItem
import com.example.potago.presentation.ui.theme.Green58

@Composable
fun VideoScreen(
    navController: NavController,
    viewModel: VideoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLoading = uiState is UiState.Loading

    Scaffold(
        topBar = {
            TopAppBar()
        },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding))
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(90.dp))
               Image(painter = painterResource(id = R.drawable.video_screen_mascot), contentDescription = null)
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                SectionHeader(title = "Đề xuất", onSeeMoreClick = {}, showSeeMore = true)
                FilterChips(isLoading)
                RecommendedVideosList(isLoading)
            }

            item {
                SectionHeader(title = "Gần đây xem", onSeeMoreClick = {}, showSeeMore = false)
                RecentVideosList(isLoading)
            }

            item {
                SectionHeader(title = "Video của bạn", onSeeMoreClick = {}, showSeeMore = true)
                YourVideosList(isLoading)
            }
        }
    }
}

@Composable
private fun TopAppBar(
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
fun FilterChips(isLoading: Boolean) {
    val filters = listOf("English", "日本語", "汉语")
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        if (isLoading) {
            items(3) {
                ShimmerItem(
                    modifier = Modifier
                        .width(100.dp)
                        .height(40.dp),
                    shape = RoundedCornerShape(20.dp)
                )
            }
        } else {
            items(filters.size) { index ->
                Box(
                    modifier = Modifier
                        .border(1.dp, Color.LightGray, RoundedCornerShape(20.dp))
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
}

@Composable
fun RecommendedVideosList(isLoading: Boolean) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        if (isLoading) {
            items(3) {
                Column(modifier = Modifier.width(196.dp)) {
                    ShimmerItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f/9f),
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
        } else {
            items(3) {
                Column(modifier = Modifier.width(196.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16 / 9f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.LightGray)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "忍者 🥷 - aisongmaker 🎶",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun RecentVideosList(isLoading: Boolean) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        if (isLoading) {
            items(3) {
                Column(modifier = Modifier.width(196.dp)) {
                    ShimmerItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f/9f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ShimmerItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp),
                        shape = RoundedCornerShape(4.dp)
                    )
                }
            }
        } else {
            items(3) {
                Column(modifier = Modifier.width(196.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16 / 9f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.LightGray)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "忍者 🥷 - aisongmaker 🎶",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun YourVideosList(isLoading: Boolean) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        if (isLoading) {
            items(3) {
                ShimmerItem(
                    modifier = Modifier
                        .width(196.dp)
                        .aspectRatio(16 / 9f),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        } else {
            items(3) {
                Box(
                    modifier = Modifier
                        .width(196.dp)
                        .aspectRatio(16 / 9f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray)
                )
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun VideoScreenPreview(){
    VideoScreen(navController = NavController(LocalContext.current))
}