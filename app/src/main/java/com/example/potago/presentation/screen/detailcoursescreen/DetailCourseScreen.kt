package com.example.potago.presentation.screen.detailcoursescreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.potago.R
import com.example.potago.presentation.navigation.Screen

@Composable
fun DetailCourseScreen(
    navController: NavController,
    wordSetId: Long,
    wordSetName: String,
    onConfirmDeleteWordSet: () -> Unit = {}
) {
    DetailCourseScreenContent(
        wordSetName = wordSetName,
        onBackClick = { navController.popBackStack() },
        onSlideDownClick = { navController.popBackStack() },
        onMatchGameClick = {
            navController.navigate(Screen.MatchGame(wordSetId, wordSetName))
        },
        onEditCourseClick = {
            navController.navigate(Screen.EditCourse(wordSetId, wordSetName))
        },
        onConfirmDeleteWordSet = onConfirmDeleteWordSet
    )
}

@Composable
private fun DetailCourseScreenContent(
    wordSetName: String,
    onBackClick: () -> Unit,
    onSlideDownClick: () -> Unit,
    onMatchGameClick: () -> Unit,
    onEditCourseClick: () -> Unit,
    onConfirmDeleteWordSet: () -> Unit = {}
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
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
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_back),
//                        contentDescription = "Back",
//                        modifier = Modifier
//                            .size(36.dp)
//                            .clickable(onClick = onBackClick),
//                        tint = Color.Black
//                    )
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
                    iconRes = R.drawable.ic_detail_course_screen_matching_cards,
                    title = "Ghép thẻ",
                    onClick = onMatchGameClick
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
                    iconRes = R.drawable.ic_detail_course_screen_list_cards,
                    title = "Xem danh sách thẻ"
                )
                Spacer(modifier = Modifier.height(12.dp))
                DetailActionCard(
                    iconRes = R.drawable.ic_detail_course_screen_edit,
                    title = "Chỉnh sửa học phần",
                    onClick = onEditCourseClick
                )
                Spacer(modifier = Modifier.height(12.dp))
                DetailActionCard(
                    iconRes = R.drawable.ic_detail_course_screen_delete,
                    title = "Xóa học phần",
                    onClick = { showDeleteConfirm = true }
                )
            }
        }

        if (showDeleteConfirm) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x99000000))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { showDeleteConfirm = false }
                    )
            )
        }
        AnimatedVisibility(
            visible = showDeleteConfirm,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            DeleteWordSetConfirmBottomSheet(
                onDismiss = { showDeleteConfirm = false },
                onConfirm = {
                    showDeleteConfirm = false
                    onConfirmDeleteWordSet()
                }
            )
        }
    }
}

@Composable
private fun DeleteWordSetConfirmBottomSheet(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFF3F4F6), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                )
                .padding(bottom = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 11.dp)
                    .align(Alignment.CenterHorizontally)
                    .width(48.dp)
                    .height(6.dp)
                    .background(Color(0xFFE5E7EB), RoundedCornerShape(999.dp))
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 28.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(130.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_detail_course_screen_potago),
                        contentDescription = null,
                        modifier = Modifier.size(120.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Xác nhận xóa chứ !?",
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4B5563)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OverlayDeclineButton(
                    modifier = Modifier.weight(1f),
                    text = "Từ chối",
                    onClick = onDismiss
                )
                OverlayConfirmButton(
                    modifier = Modifier.weight(1f),
                    text = "Xác nhận",
                    onClick = onConfirm
                )
            }
        }
    }
}

@Composable
private fun OverlayDeclineButton(
    modifier: Modifier,
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .background(Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF374151)
            )
        }
    }
}

@Composable
private fun OverlayConfirmButton(
    modifier: Modifier,
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .background(Color(0xFF46A302), RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFF46A302), RoundedCornerShape(16.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF58CC02))
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun DetailActionCard(
    iconRes: Int,
    title: String,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(Color.White, RoundedCornerShape(15.dp))
            .border(2.dp, Color(0x1A000000), RoundedCornerShape(15.dp))
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            )
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
        onSlideDownClick = {},
        onMatchGameClick = {},
        onEditCourseClick = {},
        onConfirmDeleteWordSet = {}
    )
}
