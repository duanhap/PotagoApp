package com.example.potago.presentation.screen.detailsentencepatternscreen

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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.potago.R
import com.example.potago.presentation.navigation.Screen
import com.example.potago.presentation.screen.setting.BackButton
import com.example.potago.presentation.ui.theme.Nunito
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException

// ─────────────────────────────────────────────────────────────────────────────
// Screen Entry Point
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DetailSentencePatternScreen(
    navController: NavController,
    patternId: Int,
    viewModel: DetailSentencePatternViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(patternId) { viewModel.loadDetail(patternId) }

    LaunchedEffect(uiState.deleteSuccess) {
        if (uiState.deleteSuccess) {
            navController.navigate(Screen.Library.route) {
                popUpTo(Screen.Library.route) { inclusive = false }
            }
        }
    }

    Scaffold(
        topBar = { AppTopBar(title = "Mẫu câu", onBackClick = {navController.popBackStack()}) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding))
        DetailSentencePatternContent(
            patternName = uiState.pattern?.name ?: "",
            sentenceCount = uiState.sentences.size,
            createdAt = uiState.pattern?.createdAt?.let { formatCreatedAt(it) },
            description = uiState.pattern?.description,
            isLoading = uiState.isLoading || uiState.isDeleting,
            onSlideDownClick = { navController.popBackStack() },
            onWritingGameClick = { /* TODO: navigate to writing game */ },
            onListSentencesClick = { navController.navigate(Screen.ListOfDetail.route) },
            onEditPatternClick = { navController.navigate(Screen.EditDetail(patternId)) },
            onConfirmDelete = { viewModel.deletePattern() }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Content
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DetailSentencePatternContent(
    patternName: String,
    sentenceCount: Int,
    createdAt: String? = null,
    description: String? = null,
    isLoading: Boolean = false,
    onSlideDownClick: () -> Unit,
    onWritingGameClick: () -> Unit,
    onListSentencesClick: () -> Unit,
    onEditPatternClick: () -> Unit,
    onConfirmDelete: () -> Unit = {}
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        androidx.compose.foundation.lazy.LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            item {
                AppTopBar("Mẫu câu", {},modifier = Modifier.alpha(0f))



                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = patternName.ifBlank { "Mẫu câu" },
                        fontSize = 24.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = buildString {
                            append("$sentenceCount mẫu câu")
                            if (!createdAt.isNullOrBlank()) append(" - $createdAt")
                        },
                        fontSize = 12.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                    if (!description.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = description,
                            fontSize = 14.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontStyle = FontStyle.Italic,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(26.dp))
                    Text(
                        text = "Chế độ học",
                        fontSize = 18.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    SentenceActionCard(
                        iconRes = R.drawable.ic_writing_game,
                        title = "Luyện viết",
                        onClick = onWritingGameClick
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    SentenceActionCard(
                        iconRes = R.drawable.ic_order_word,
                        title = "Sắp xếp chữ",
                        onClick = onWritingGameClick
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
                    SentenceActionCard(
                        iconRes = R.drawable.ic_list_sentence_card,
                        title = "Xem danh sách câu",
                        onClick = onListSentencesClick
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SentenceActionCard(
                        iconRes = R.drawable.icon_edit_sentence_partten,
                        title = "Chỉnh sửa mẫu câu",
                        onClick = onEditPatternClick
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SentenceActionCard(
                        iconRes = R.drawable.icon_delete_setence_pattern,
                        title = "Xóa mẫu câu",
                        onClick = { showDeleteConfirm = true }
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        // Dim overlay
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

        // Delete confirm bottom sheet
        AnimatedVisibility(
            visible = showDeleteConfirm,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            DeletePatternConfirmBottomSheet(
                isLoading = isLoading,
                onDismiss = { showDeleteConfirm = false },
                onConfirm = {
                    showDeleteConfirm = false
                    onConfirmDelete()
                }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Action Card (green tint)
// ─────────────────────────────────────────────────────────────────────────────

    @Composable
    private fun SentenceActionCard(
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
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
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

// ─────────────────────────────────────────────────────────────────────────────
// Delete Confirm Bottom Sheet
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DeletePatternConfirmBottomSheet(
    isLoading: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFF3F4F6), RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = {})
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
                // Từ chối
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .background(Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (!isLoading) Color.White else Color(0xFFF3F4F6))
                            .then(if (!isLoading) Modifier.clickable(onClick = onDismiss) else Modifier),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Từ chối",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (!isLoading) Color(0xFF374151) else Color(0xFFB0B8C1)
                        )
                    }
                }

                // Xác nhận
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .background(Color(0xFF46A302), RoundedCornerShape(16.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF58CC02))
                            .then(if (!isLoading) Modifier.clickable(onClick = onConfirm) else Modifier),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.5.dp)
                        } else {
                            Text(text = "Xác nhận", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Top App Bar
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AppTopBar(title: String,onBackClick: () -> Unit ,modifier: Modifier = Modifier) {
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
                Spacer(modifier = Modifier.width(60.dp)) // chừa chỗ cho back button
                Text(
                    text = title,
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier.weight(1f),
                )
            }

            // 🔥 BackButton overlay
            Box(
                modifier = Modifier.matchParentSize()
            ) {
                BackButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .wrapContentSize()
                )
            }

        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────

private fun formatCreatedAt(dateStr: String): String {
    if (dateStr.isBlank()) return ""
    return try {
        val date = if (dateStr.contains("T")) OffsetDateTime.parse(dateStr).toLocalDate()
        else java.time.LocalDate.parse(dateStr)
        "Tháng ${date.monthValue} năm ${date.year}"
    } catch (_: DateTimeParseException) { "" }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DetailSentencePatternScreenPreview() {
    DetailSentencePatternContent(
        patternName = "English",
        sentenceCount = 200,
        createdAt = "Tháng 1 năm 2026",
        description = "Learn how to order tacos and ask for the bill.",
        onSlideDownClick = {},
        onWritingGameClick = {},
        onListSentencesClick = {},
        onEditPatternClick = {},
        onConfirmDelete = {}
    )
}
