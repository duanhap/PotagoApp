package com.example.potago.presentation.screen.detailcoursescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.potago.R

@Composable
fun DetailCourseScreen(
    navController: NavController,
    wordSetName: String
) {
    DetailCourseScreenContent(
        wordSetName = wordSetName,
        onBackClick = { navController.popBackStack() },
        onSlideDownClick = { navController.popBackStack() }
    )
}

@Composable
private fun DetailCourseScreenContent(
    wordSetName: String,
    onBackClick: () -> Unit,
    onSlideDownClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F4F6))
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(59.dp)
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(36.dp)
                        .clickable(onClick = onBackClick),
                    tint = Color.Black
                )
                Text(
                    text = "Học phần",
                    fontSize = 32.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )
            }
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_detail_course_screen_slidedown),
            contentDescription = "Slide down",
            tint = Color.Unspecified,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onSlideDownClick)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = wordSetName.ifBlank { "Ordering Food" },
                fontSize = 24.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "200 thuật ngữ - Tháng 1 năm 2026",
                fontSize = 12.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Learn how to order tacos and ask for the bill.",
                fontSize = 14.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Italic,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(26.dp))
            Text(
                text = "Chế độ học",
                fontSize = 18.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(10.dp))
            DetailActionCard(
                iconRes = R.drawable.ic_card_detailed_video_screen,
                title = "Ghép thẻ"
            )

            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = "Tính năng khác",
                fontSize = 18.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(10.dp))
            DetailActionCard(
                iconRes = R.drawable.ic_list_detailed_video_screen,
                title = "Xem danh sách thẻ"
            )
            Spacer(modifier = Modifier.height(12.dp))
            DetailActionCard(
                iconRes = R.drawable.ic_save_detailed_video_screen,
                title = "Chỉnh sửa học phần"
            )
            Spacer(modifier = Modifier.height(12.dp))
            DetailActionCard(
                iconRes = R.drawable.ic_bin,
                title = "Xóa học phần"
            )
        }
    }
}

@Composable
private fun DetailActionCard(
    iconRes: Int,
    title: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(Color.White, RoundedCornerShape(15.dp))
            .border(2.dp, Color(0x1A000000), RoundedCornerShape(15.dp))
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = title,
            modifier = Modifier.size(32.dp),
            tint = Color.Unspecified
        )
        Text(
            text = title,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DetailCourseScreenPreview() {
    DetailCourseScreenContent(
        wordSetName = "Ordering Food",
        onBackClick = {},
        onSlideDownClick = {}
    )
}
