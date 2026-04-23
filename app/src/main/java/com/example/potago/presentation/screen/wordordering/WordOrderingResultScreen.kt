package com.example.potago.presentation.screen.wordordering

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    totalCount: Int
) {
    val xp = correctCount * 6
    val wrongCount = totalCount - correctCount

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        // ── Header ────────────────────────────────────────────────────
        Text(
            text = "Kết quả",
            fontFamily = Nunito,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 32.sp,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)
        )

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
            fontFamily = Nunito,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
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
                icon = { // hex coin icon
                    Image(
                        painter = painterResource(R.drawable.ic_experience_points),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                },
                valueText = "$xp",
                unitText = "XP",
                valueColor = Color(0xFFA16207),
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
                valueText = "$wrongCount",
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
                valueText = "3:10",
                unitText = "",
                valueColor = Color(0xFF3B82F6),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // ── Mascot + speech bubble ────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.love_mascot),
                contentDescription = null,
                modifier = Modifier.size(90.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(12.dp))
            Surface(
                shape = RoundedCornerShape(
                    topStart = 0.dp, topEnd = 16.dp,
                    bottomEnd = 16.dp, bottomStart = 16.dp
                ),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
                shadowElevation = 2.dp,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Kết quả rất tốt đó <3\nTiếp tục chứ chủ nhân !?",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF4B5563),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Bottom buttons ────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Từ chối — outline, smaller weight
            Box(
                modifier = Modifier
                    .weight(0.85f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
                    .clickable { navController.popBackStack() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Từ chối",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = Color(0xFF374151),
                    textAlign = TextAlign.Center
                )
            }
            // Học tiếp — green, larger weight
            Box(
                modifier = Modifier
                    .weight(1.15f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF58CC02))
                    .border(3.dp, Color(0xFF46A302), RoundedCornerShape(16.dp))
                    .clickable { navController.popBackStack() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Học tiếp",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
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
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(101.dp)
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
                fontSize = 12.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
        // Icon + value row
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = valueText,
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                color = valueColor
            )
            if (unitText.isNotEmpty()) {
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = unitText,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = valueColor,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
    }
}

// ── Previews ───────────────────────────────────────────────────────────────────
@Preview(showBackground = true, showSystemUi = true, name = "Result - All Correct")
@Composable
private fun PreviewResultAllCorrect() {
    WordOrderingResultScreen(
        navController = rememberNavController(),
        correctCount = 5,
        totalCount = 5
    )
}

@Preview(showBackground = true, showSystemUi = true, name = "Result - Partial")
@Composable
private fun PreviewResultPartial() {
    WordOrderingResultScreen(
        navController = rememberNavController(),
        correctCount = 3,
        totalCount = 5
    )
}

@Preview(showBackground = true, showSystemUi = true, name = "Result - All Wrong")
@Composable
private fun PreviewResultAllWrong() {
    WordOrderingResultScreen(
        navController = rememberNavController(),
        correctCount = 0,
        totalCount = 5
    )
}
