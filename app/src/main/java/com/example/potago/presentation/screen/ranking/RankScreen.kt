package com.example.potago.presentation.screen.ranking

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.potago.R
import com.example.potago.domain.model.User

@Composable
fun RankScreen(
    navController: NavController,
    viewModel: RankViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .shadow(elevation = 2.dp, spotColor = Color(0x1A000000))
                    .padding(vertical = 12.dp, horizontal = 20.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_cancel),
                    contentDescription = "Back",
                    colorFilter = ColorFilter.tint(Color.Black),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { navController.popBackStack() }
                )
                Text(
                    "Xếp hạng",
                    color = Color.Black,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp)
                )
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF89E219))
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Lỗi: ${uiState.error}", color = Color.Red)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    // Ranking Banner
                    item {
                        Image(
                            painter = painterResource(id = R.drawable.top1000),
                            contentDescription = "Top 1000 Banner",
                            modifier = Modifier
                                .padding(14.dp)
                                .fillMaxWidth(),
                            contentScale = ContentScale.FillWidth
                        )
                    }

                    // Ranking List
                    itemsIndexed(uiState.topUsers) { index, user ->
                        RankItem(
                            rank = index + 1,
                            user = user,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }

        // My Rank Section (Sticky at bottom)
        if (!uiState.isLoading && uiState.myRank != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp, start = 12.dp, end = 12.dp)
                    .fillMaxWidth()
            ) {
                // Background shadow layer if needed or just use the box
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = Color(0xFF89E219),
                            shape = RoundedCornerShape(15.dp)
                        )
                        .clip(RoundedCornerShape(15.dp))
                        .fillMaxWidth()
                        .background(Color(0xFFC4FF7A))
                        .padding(16.dp)
                ) {
                    Text(
                        "${uiState.myRank}",
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                    
                    // Avatar check
                    AsyncImage(
                        model = uiState.currentUser?.avatar ?: R.drawable.avataryellowhair,
                        contentDescription = "My Avatar",
                        modifier = Modifier
                            .size(41.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.avataryellowhair)
                    )
                    
                    Text(
                        uiState.currentUser?.name ?: "Bạn",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Text(
                        "${uiState.currentUser?.experiencePoints ?: 0} xp",
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun RankItem(rank: Int, user: User, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .border(
                width = 1.dp,
                color = Color(0x1A000000),
                shape = RoundedCornerShape(15.dp)
            )
            .clip(RoundedCornerShape(15.dp))
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 10.dp, horizontal = 14.dp)
    ) {
        // Rank Icon or Number
        when (rank) {
            1 -> Image(
                painter = painterResource(id = R.drawable.top1),
                contentDescription = "Rank 1",
                modifier = Modifier.size(37.dp, 39.dp).padding(end = 12.dp)
            )
            2 -> Image(
                painter = painterResource(id = R.drawable.top2),
                contentDescription = "Rank 2",
                modifier = Modifier.size(37.dp, 39.dp).padding(end = 12.dp)
            )
            3 -> Image(
                painter = painterResource(id = R.drawable.top3),
                contentDescription = "Rank 3",
                modifier = Modifier.size(37.dp, 39.dp).padding(end = 12.dp)
            )
            else -> Text(
                "$rank",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(37.dp).padding(end = 12.dp)
            )
        }

        // Avatar
        AsyncImage(
            model = if (user.avatar.isNullOrEmpty()) R.drawable.pinkgirl else user.avatar,
            contentDescription = "Avatar",
            modifier = Modifier
                .size(37.dp, 39.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.pinkgirl)
        )

        // Name
        Text(
            user.name,
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 12.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // XP
        Text(
            "${user.experiencePoints} xp",
            color = Color.Black,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}