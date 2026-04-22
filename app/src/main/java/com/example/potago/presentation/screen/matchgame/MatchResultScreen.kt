package com.example.potago.presentation.screen.matchgame

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.potago.R
import com.example.potago.presentation.navigation.Screen
import com.example.potago.presentation.screen.setting.BackButton
import com.example.potago.presentation.ui.theme.Nunito
import com.example.potago.presentation.ui.theme.PotagoTheme

@Composable
fun MatchResultScreen(
    navController: NavController,
    completedTime: Double,
    bestTime: Double,
    bestDate: String,
    wordSetId: Long,
    wordSetName: String,
    hackExperience: Boolean = false,
    superExperience: Boolean = false
) {
    val baseXp = when {
        completedTime <= 20 -> 5
        completedTime <= 40 -> 5
        else -> 10
    }
    val multiplier = when {
        hackExperience -> 3
        superExperience -> 2
        else -> 1
    }
    val xpReward = baseXp * multiplier
    val hasBonus = multiplier > 1
    val multiplierLabel = "x$multiplier"

    val diamondReward = when {
        completedTime <= 20 -> 5
        completedTime <= 40 -> 5
        else -> 5
    }

    // Animation states cho bonus tag
    var showTag by remember { mutableStateOf(false) }
    var showFinalXp by remember { mutableStateOf(false) }
    LaunchedEffect(hasBonus) {
        if (hasBonus) {
            kotlinx.coroutines.delay(800)
            showTag = true
            kotlinx.coroutines.delay(300)
            showFinalXp = true
        }
    }
    val tagScale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (showTag) 1f else 0f,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessMedium
        ),
        label = "tagScale"
    )

    Scaffold(
        topBar = {
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
                        Text(
                            text = "Kết quả",
                            style = MaterialTheme.typography.displayMedium,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        },
        containerColor = Color.White
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize())
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth().alpha(0f),
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
                        Text(
                            text = "Kết quả",
                            style = MaterialTheme.typography.displayMedium,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            // ── Hero: mountain + mascot + time ──────────────────────────
            MountainAndMascot(completedTime)

            Spacer(modifier = Modifier.weight(2f))

            // ── Kỷ lục ──────────────────────────────────────────────────
            Text(
                text = "Kỷ lục",
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_result_medal),
                        contentDescription = null,
                        modifier = Modifier.size(38.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = formatBestDate(bestDate),
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = if (bestTime > 0) String.format("%.1f s", bestTime) else "-",
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.weight(2f))

            // ── Phần thưởng ─────────────────────────────────────────────
            Text(
                text = "Phần thưởng",
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            ) {
                // XP card
                RewardCard(
                    modifier = Modifier.weight(1f),
                    headerColor = Color(0xFFFFD600),
                    bodyColor = Color(0xFFFEF9C3),
                    borderColor = Color(0x7FFEF08A),
                    headerText = "Kinh nghiệm",
                    content = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_experience_points),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = Color.Unspecified
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(contentAlignment = Alignment.TopEnd) {
                                AnimatedContent(
                                    targetState = if (hasBonus && showFinalXp) xpReward else baseXp,
                                    transitionSpec = {
                                        (slideInVertically { it } + androidx.compose.animation.fadeIn())
                                            .togetherWith(slideOutVertically { -it } + androidx.compose.animation.fadeOut())
                                    },
                                    label = "xpNumber"
                                ) { xp ->
                                    Text(
                                        text = "$xp",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Color(0xFFA16207)
                                    )
                                }
                                if (hasBonus) {
                                    Box(
                                        modifier = Modifier
                                            .offset(x = 20.dp, y = (-15).dp)
                                            .graphicsLayer { scaleX = tagScale; scaleY = tagScale }
                                            .background(Color(0xFFEF4444), RoundedCornerShape(5.dp))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = multiplierLabel,
                                            fontFamily = Nunito,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 10.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "XP",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = Color(0xFFA16207)
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.width(10.dp))
                // Diamond card
                RewardCard(
                    modifier = Modifier.weight(1f),
                    headerColor = Color(0xFFF44336),
                    bodyColor = Color(0xFFFFE3E0),
                    borderColor = Color(0x40F44336),
                    headerText = "Diamond",
                    content = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_ruby_detailed_video_screen),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(7.dp))
                            Text(
                                text = "$diamondReward",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color(0xFFF44336)
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.width(10.dp))
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.weight(3f))

            // ── Mascot + bubble ─────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.empty_box_1),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(20.dp))
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    shadowElevation = 1.dp
                ) {
                    Text(
                        text = "Kết quả rất tốt đó <3\nTiếp tục chứ chủ nhân !?",
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = Color(0xFF4B5563),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(3f))

            // ── Buttons ─────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ResultButton(
                    text = "Từ chối",
                    bgColor = Color.White,
                    borderColor = Color(0xFFE5E7EB),
                    textColor = Color(0xFF374151),
                    modifier = Modifier.weight(1f),
                    onClick = { navController.popBackStack() }
                )
                ResultButton(
                    text = "Học tiếp",
                    bgColor = Color(0xFF58CC02),
                    borderColor = Color(0xFF46A302),
                    textColor = Color.White,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.navigate(Screen.MatchGame(wordSetId, wordSetName)) {
                            popUpTo(Screen.MatchResult.route) { inclusive = true }
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.weight(2f))
        }
    }
}

@Composable
private fun MountainAndMascot(completedTime: Double) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xFFBFDBFE),
                RoundedCornerShape(bottomStart =  20.dp, bottomEnd = 20.dp)
            )
            .height(250.dp)
            .padding(horizontal = 16.dp)
    ) {
        // Mountain image — left side
        Image(
            painter = painterResource(R.drawable.ic_result_mountain),
            contentDescription = null,
            modifier = Modifier
                .size(230.dp)
                .align(Alignment.BottomStart)
                .padding(bottom = 15.dp),
            contentScale = ContentScale.Fit
        )
        // Mascot + time badge — bottom right, time above mascot head
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(modifier = Modifier.padding(bottom = 4.dp).offset(y =30.dp)) {
                // Stroke layer
                Text(
                    text = String.format("%.1f s", completedTime),
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontFamily = com.example.potago.presentation.screen.streak.Nunito,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        drawStyle = Stroke(
                            width = 13f,
                            join = StrokeJoin.Round
                        )
                    ),
                    color = Color(0x8046A302)
                )

                // Fill layer
                Text(
                    text = String.format("%.1f s", completedTime),
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        color = Color(0xFFFFFFFF)
                    )
                )
            }
            Image(
                painter = painterResource(R.drawable.ic_mascot_turn_back),
                contentDescription = null,
                modifier = Modifier.size(140.dp).offset(y =30.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun RewardCard(
    modifier: Modifier = Modifier,
    headerColor: Color,
    bodyColor: Color,
    borderColor: Color,
    headerText: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .border(3.dp, borderColor, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(headerColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = headerText,
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
                .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                .background(bodyColor),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Composable
private fun ResultButton(
    text: String,
    bgColor: Color,
    borderColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .height(51.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(borderColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isPressed) 51.dp else 48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(bgColor)
                .border(1.dp, borderColor, RoundedCornerShape(16.dp))
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
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MatchResultScreenPreview() {
    PotagoTheme(dynamicColor = false) {
        MatchResultScreen(
            navController = NavController(LocalContext.current),
            completedTime = 15.5,
            bestTime = 12.0,
            bestDate = "12 tháng 8 năm 2025",
            wordSetId = 1,
            wordSetName = "Test Set"
        )
    }
}

private fun formatBestDate(dateStr: String): String {
    if (dateStr.isBlank()) return ""
    return try {
        val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
        val date = inputFormat.parse(dateStr) ?: return dateStr
        val cal = java.util.Calendar.getInstance().apply { time = date }
        val day = cal.get(java.util.Calendar.DAY_OF_MONTH)
        val month = cal.get(java.util.Calendar.MONTH) + 1
        val year = cal.get(java.util.Calendar.YEAR)
        "$day tháng $month năm $year"
    } catch (e: Exception) {
        dateStr
    }
}
