package com.example.potago.presentation.screen.wordordering

import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.potago.R
import com.example.potago.presentation.ui.theme.Nunito

@Composable
fun WordOrderingResultScreen(
    navController: NavController,
    correctCount: Int,
    totalCount: Int,
    completedTime: Double = 0.0,
    xpEarned: Int = 0,
    diamondEarned: Int = 0,
    hackExperience: Boolean = false,
    superExperience: Boolean = false
) {
    val multiplier = when {
        hackExperience -> 3
        superExperience -> 2
        else -> 1
    }
    val hasBonus = multiplier > 1
    val baseXp = if (hasBonus) xpEarned / multiplier else xpEarned
    val multiplierLabel = "x$multiplier"

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

    val wrongCount = totalCount - correctCount
    Scaffold(
        topBar = {
            TopAppBar()
        },
        containerColor = Color.White
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .statusBarsPadding()
        ) {
            //TopAppBar(modifier = Modifier.alpha(0f))
            Spacer(modifier = Modifier.height(45.dp))

            // ── Illustration ──────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Transparent), // green ground bg
                contentAlignment = Alignment.Center
            ) {
                // Ground scene — use ic_garden or normal_mascot as placeholder
                Image(
                    painter = painterResource(R.drawable.potato_in_ground),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── "Phần thưởng" ─────────────────────────────────────────────
            Text(
                text = "Phần thưởng",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── 3 reward cards ────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                RewardCard(
                    headerColor = Color(0xFFFFD600),
                    bgColor = Color(0x80FEF9C3),
                    borderColor = Color(0x80FEF08A),
                    headerText = "Kinh nghiệm",
                    icon = {
                        Image(
                            painter = painterResource(R.drawable.ic_experience_points),
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    valueText = if (hasBonus && showFinalXp) "$xpEarned" else "$baseXp",
                    unitText = "XP",
                    valueColor = Color(0xFFA16207),
                    bonusTag = if (hasBonus) multiplierLabel else null,
                    bonusTagScale = tagScale,
                    modifier = Modifier.weight(1f)
                )
                RewardCard(
                    headerColor = Color(0xFFF44336),
                    bgColor = Color(0x80FFA9A3),
                    borderColor = Color(0x40F44336),
                    headerText = "Diamond",
                    icon = {
                        Image(
                            painter = painterResource(R.drawable.ic_diamon),
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    valueText = "$diamondEarned",
                    unitText = "",
                    valueColor = Color(0xFFF44336),
                    modifier = Modifier.weight(1f)
                )
                RewardCard(
                    headerColor = Color(0xFF3B82F6),
                    bgColor = Color(0xFFD9E7FF),
                    borderColor = Color(0x403B82F6),
                    headerText = "Time",
                    icon = null,
                    valueText = String.format("%.1f", completedTime),
                    unitText = "s",
                    valueColor = Color(0xFF3B82F6),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.weight(2f))

            // ── Mascot + speech bubble ────────────────────────────────────
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

            Spacer(modifier = Modifier.weight(1f))

            // ── Bottom buttons ────────────────────────────────────────────
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
                        navController.popBackStack()
                    }
                )
            }
            Spacer(modifier = Modifier.weight(0.5f))

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
@Composable
private fun TopAppBar(modifier: Modifier= Modifier) {
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
}

@Composable
private fun RewardCard(
    headerColor: Color,
    bgColor: Color,
    borderColor: Color,
    headerText: String,
    icon: (@Composable () -> Unit)?,
    valueText: String,
    unitText: String,
    valueColor: Color,
    bonusTag: String? = null,
    bonusTagScale: Float = 0f,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(bgColor)
                .border(3.dp, borderColor, RoundedCornerShape(12.dp))
        ) {
            // Header bar
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
                    textAlign = TextAlign.Center,
                )
            }

            // Body: icon + value
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 55.dp, bottom = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (icon != null) {
                        icon()
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    // Số XP với AnimatedContent
                    androidx.compose.animation.AnimatedContent(
                        targetState = valueText,
                        transitionSpec = {
                            (androidx.compose.animation.slideInVertically { it } + androidx.compose.animation.fadeIn())
                                .togetherWith(androidx.compose.animation.slideOutVertically { -it } + androidx.compose.animation.fadeOut())
                        },
                        label = "xpValue"
                    ) { v ->
                        Text(
                            text = v,
                            fontFamily = Nunito,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            color = valueColor
                        )
                    }
                    if (unitText.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = unitText,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = valueColor,
                            modifier = Modifier.offset(y=2.dp)
                        )
                    }
                }

                // Tag x2/x3 — đặt góc trên phải bên trong body
                if (bonusTag != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 6.dp)
                            .offset(x=-20.dp, y =-10.dp)
                            .graphicsLayer { scaleX = bonusTagScale; scaleY = bonusTagScale }
                            .background(Color(0xFFEF4444), RoundedCornerShape(5.dp))
                            .padding(horizontal = 5.dp, vertical = 2.dp)

                    ) {
                        Text(
                            text = bonusTag,
                            fontFamily = Nunito,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.sp,
                            color = Color.White,

                        )
                    }
                }
            }
        }
    }
}

// ── Previews ───────────────────────────────────────────────────────────────────
@Preview(showBackground = true, showSystemUi = true, name = "Result - All Correct")
@Composable
private fun PreviewResultAllCorrect() {
    WordOrderingResultScreen(navController = rememberNavController(), correctCount = 5, totalCount = 5, completedTime = 42.3, xpEarned = 30, diamondEarned = 5)
}

@Preview(showBackground = true, showSystemUi = true, name = "Result - Partial")
@Composable
private fun PreviewResultPartial() {
    WordOrderingResultScreen(navController = rememberNavController(), correctCount = 3, totalCount = 5, completedTime = 67.8, xpEarned = 18, diamondEarned = 5)
}

@Preview(showBackground = true, showSystemUi = true, name = "Result - All Wrong")
@Composable
private fun PreviewResultAllWrong() {
    WordOrderingResultScreen(navController = rememberNavController(), correctCount = 0, totalCount = 5, completedTime = 120.0, xpEarned = 0, diamondEarned = 5)
}
