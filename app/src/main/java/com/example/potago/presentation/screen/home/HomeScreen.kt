package com.example.potago.presentation.screen.home

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.potago.R
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar()
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                MascotAndBubbleHome()
                Spacer(modifier = Modifier.height(10.dp))
            }

            // --- Section Học tiếp ---
            item {
                SectionHeader(title = "Học tiếp", onSeeMoreClick = {})
                // Placeholder content since no data
                EmptyBoxView(text = "Chưa có học phần nào")
                Spacer(modifier = Modifier.height(20.dp))
            }

            // --- Section Câu gần đây ---
            item {
                SectionHeader(title = "Câu gần đây", onSeeMoreClick = {})
                // Placeholder content since no data
                EmptyBoxView(text = "Chưa có câu nào")
                Spacer(modifier = Modifier.height(20.dp))
            }

            // --- Section Video gần đây ---
            item {
                SectionHeader(title = "Chưa có video nào", onSeeMoreClick = {})
                // Placeholder content since no data
                EmptyBoxView(text = "Chưa có video nào")
                Spacer(modifier = Modifier.height(20.dp))
            }

            // --- Section Tính năng ---
            item {
                SectionHeader(title = "Tính năng", onSeeMoreClick = {}, showSeeMore = false)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FeatureBox(
                        iconRes = R.drawable.ic_shop,
                        title = "Cửa hàng",
                        iconBgColor = Color(0xFFF3E8FF), // Light purple
                        iconTint = Color(0xFF9333EA), // Purple
                        modifier = Modifier.weight(1f),
                        onItemClick = {
    //                        navController.navigate(Screen.ShopScreen.route)
                        }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    FeatureBox(
                        iconRes = R.drawable.ic_add,
                        title = "Thêm thư mục",
                        iconBgColor = Color(0xFFFFEDD5), // Light orange
                        iconTint = Color(0xFFF97316), // Orange
                        modifier = Modifier.weight(1f),
                        onItemClick = {
//                        navController.navigate(Screen.ShopScreen.route)
                        }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    FeatureBox(
                        iconRes = R.drawable.ic_rank,
                        title = "Xếp hạng",
                        iconBgColor = Color(0xFFFEF9C3), // Light yellow
                        iconTint = Color(0xFFEAB308), // Yellow
                        modifier = Modifier.weight(1f),
                        onItemClick = {
//                        navController.navigate(Screen.RankScreen.route)
                        }
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ){
                Image(
                    painter = painterResource(id = R.drawable.ic_flag_vietnam),
                    contentDescription = "Language",
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = "Home",
                    style = MaterialTheme.typography.displayMedium,
                    color = Color(0x00FFFFFF),
                )

            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_water_on),
                    contentDescription = "Streak",
                    modifier = Modifier.size(24.dp).padding(end = 5.dp)
                )
                Text(
                    text = "12",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF0099FF),
                    modifier = Modifier.padding(end = 15.dp)
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFFEF08A)),
                    color = Color(0xFFFEF9C3)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_experience_points),
                            contentDescription = "Experience points",
                            modifier = Modifier.size(32.dp).padding(end = 8.dp)
                        )
                        Text(
                            text = "2450",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontSize = 15.sp
                            ),
                            color = Color(0xFFA16207),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "XP",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontSize = 15.sp
                            ),
                            color = Color(0xFFA16207),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MascotAndBubbleHome() {
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .offset(x = (-20).dp),
    ) {
        // 🐣 Mascot
        Image(
            painter = painterResource(id = R.drawable.ic_thinking_mascot_home_screen),
            contentDescription = "Mascot",
            modifier = Modifier
                .size(130.dp)
                .graphicsLayer {
                    scaleX = mascotScale
                    scaleY = mascotScale
                    alpha = mascotAlpha
                }
        )
        
        // 💬 Bubble
        Box(
            modifier = Modifier
                .padding(start = 10.dp,top = 10.dp)
                .graphicsLayer {
                    scaleX = bubbleScale
                    scaleY = bubbleScale
                    alpha = bubbleAlpha
                    translationY = bubbleOffsetY
                }
                .background(Color.White, RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp))
                .border(1.2.dp, Color(0xFFE5E7EB), RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .weight(1f)
        ) {
            Text(
                text = "Xin chào, chủ nhân! Tôi rất ưa tắm, hãy nhớ tắm rửa cho tôi mỗi ngày!",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF4B5563)
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
            .padding(vertical = 12.dp),
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
private fun EmptyBoxView(text : String = "Trống rỗng...") {
    val images = listOf(
        R.drawable.empty_box,
        R.drawable.empty_box_1,
        R.drawable.empty_box_2,
        R.drawable.empty_box_3
    )
    val randomImage = remember { images.random() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = randomImage),
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.Gray,
            )
        )
    }
}

@Composable
fun FeatureBox(
    iconRes: Int,
    title: String,
    iconBgColor: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFF3F4F6), RoundedCornerShape(16.dp))
            .clickable { onItemClick }
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    modifier = Modifier.size(24.dp),
                    tint = iconTint
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF4B5563),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenShow() {
    HomeScreen(navController = NavController(LocalContext.current))
}
