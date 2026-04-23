package com.example.potago.presentation.screen.ranking

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
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
    Scaffold(
        topBar = {
            TopAppBar(
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding))
        Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
            Column(modifier = Modifier.fillMaxSize()) {

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
                        item{
                            TopAppBar(
                                onBackClick = { navController.popBackStack() },
                                modifier = Modifier.alpha(0f)
                            )
                        }
                        // Ranking Banner with Gradient Background
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 20.dp)
                            ) {
                                // Top 1000 Banner Image
                                Image(
                                    painter = painterResource(id = R.drawable.top1000),
                                    contentDescription = "Top 1000 Banner",
                                    modifier = Modifier
                                        .align(Alignment.Center),
                                )
                            }
                        }

                        // Ranking List
                        itemsIndexed(uiState.topUsers) { index, user ->
                            val isCurrentUser = user.uid == uiState.currentUser?.uid
                            RankItem(
                                rank = index + 1,
                                user = user,
                                isCurrentUser = isCurrentUser,
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
                        .padding(bottom = 16.dp, start = 9.dp, end = 9.dp)
                        .fillMaxWidth()
                ) {
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
                            .padding(horizontal = 16.dp, vertical = 18.dp)
                    ) {
                        Text(
                            "${uiState.myRank}",
                            color = Color(0xCC050505),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(end = 16.dp)
                        )

                        AsyncImage(
                            model = uiState.currentUser?.avatar ?: R.drawable.avataryellowhair,
                            contentDescription = "My Avatar",
                            modifier = Modifier
                                .size(41.dp)
                                .clip(CircleShape)
                                .border(1.dp, Color.Black, CircleShape),
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
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }


}

@Composable
fun RankItem(
    rank: Int, 
    user: User, 
    isCurrentUser: Boolean = false,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isCurrentUser) Color(0xFFC4FF7A) else Color.White
    val borderColor = if (isCurrentUser) Color(0xFF89E219) else Color(0x1A000000)
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(15.dp)
            )
            .clip(RoundedCornerShape(15.dp))
            .fillMaxWidth()
            .background(backgroundColor)
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
                color = Color(0xCC050505),
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.width(37.dp).padding(end = 12.dp)
            )
        }

        // Avatar
        AsyncImage(
            model = if (user.avatar.isNullOrEmpty()) R.drawable.ic_an_danh else user.avatar,
            contentDescription = "Avatar",
            modifier = Modifier
                .size(41.dp)
                .clip(CircleShape)
                .border(1.dp, Color.Black, CircleShape),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.ic_an_danh)
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
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun TopAppBar(
    onBackClick: () -> Unit = {},
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = "Xếp hạng",
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier.padding(start = 45.dp)
                )

            }

            // 🔥 BackButton overlay
            Box(
                modifier = Modifier.matchParentSize()
            ) {
                BackButtonRankScreen(
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
private fun BackButtonRankScreen(
    onClick: () -> Unit,
    modifier: Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        label = "icon_scale"
    )

    IconButton(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier.offset(x = -10.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_library_add_button),
            contentDescription = "Back",
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .rotate(45f)
        )
    }
}
