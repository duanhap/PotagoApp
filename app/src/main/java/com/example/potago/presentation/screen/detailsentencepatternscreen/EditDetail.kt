package com.example.potago.presentation.screen.detailsentencepatternscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.potago.R

private val LANGUAGE_OPTIONS = listOf(
    "en" to "English",
    "vi" to "Tiếng Việt",
    "ja" to "日本語",
    "ko" to "한국어",
    "zh" to "中文",
    "fr" to "Français",
    "de" to "Deutsch"
)

@Composable
fun EditDetailScreen(
    navController: NavController,
    patternId: Int,
    viewModel: DetailSentencePatternViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pattern = uiState.pattern

    // Pre-fill fields when pattern loads
    var titleValue by remember(pattern) { mutableStateOf(pattern?.name ?: "") }
    var descValue by remember(pattern) { mutableStateOf(pattern?.description ?: "") }
    var termLangCode by remember(pattern) { mutableStateOf(pattern?.termLanguageCode ?: "en") }
    var defLangCode by remember(pattern) { mutableStateOf(pattern?.definitionLanguageCode ?: "vi") }

    var termLangExpanded by remember { mutableStateOf(false) }
    var defLangExpanded by remember { mutableStateOf(false) }

    // Load pattern if not already loaded
    LaunchedEffect(patternId) {
        if (pattern == null || pattern.id != patternId) {
            viewModel.loadDetail(patternId)
        }
    }

    // Navigate back on success
    LaunchedEffect(uiState.updateSuccess) {
        if (uiState.updateSuccess) {
            viewModel.clearUpdateSuccess()
            navController.popBackStack()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(color = Color(0xFFFFFFFF))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(color = Color(0xFFFFFFFF))
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(bottom = 49.dp)
                        .fillMaxWidth()
                        .background(color = Color(0xE3FFFFFF))
                        .shadow(elevation = 2.dp, spotColor = Color(0x1A000000))
                        .padding(vertical = 11.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back",
                        colorFilter = ColorFilter.tint(Color.Black),
                        modifier = Modifier
                            .padding(start = 20.dp)
                            .size(24.dp)
                            .clickable { navController.popBackStack() }
                    )
                    Text(
                        "Chỉnh sửa mẫu câu",
                        color = Color(0xFF000000),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }

                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF58CC02))
                    }
                } else {
                    // Error message
                    if (uiState.actionError != null) {
                        Text(
                            text = uiState.actionError ?: "",
                            color = Color.Red,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 22.dp, vertical = 8.dp)
                        )
                    }

                    // Tiêu đề
                    BasicTextField(
                        value = titleValue,
                        onValueChange = { titleValue = it },
                        textStyle = TextStyle(
                            color = Color(0xFF000000),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .padding(start = 22.dp, end = 22.dp)
                            .fillMaxWidth(),
                        decorationBox = { innerTextField ->
                            Box {
                                if (titleValue.isEmpty()) {
                                    Text(
                                        "Nhập tiêu đề vô đây",
                                        color = Color(0x80000000),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .padding(bottom = 6.dp, start = 20.dp, end = 20.dp)
                            .height(2.dp)
                            .fillMaxWidth()
                            .background(color = Color(0xFF000000))
                    )
                    Text(
                        "Tiêu đề",
                        color = Color(0xFF000000),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 36.dp, start = 20.dp)
                    )

                    // Mô tả
                    BasicTextField(
                        value = descValue,
                        onValueChange = { descValue = it },
                        textStyle = TextStyle(
                            color = Color(0xFF000000),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .padding(start = 22.dp, end = 22.dp)
                            .fillMaxWidth(),
                        decorationBox = { innerTextField ->
                            Box {
                                if (descValue.isEmpty()) {
                                    Text(
                                        "Nhập mô tả vô đây",
                                        color = Color(0x80000000),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(13.dp))
                    Box(
                        modifier = Modifier
                            .padding(bottom = 6.dp, start = 20.dp, end = 20.dp)
                            .height(2.dp)
                            .fillMaxWidth()
                            .background(color = Color(0xFF000000))
                    )
                    Text(
                        "Mô tả",
                        color = Color(0xFF000000),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 44.dp, start = 21.dp)
                    )

                    // Ngôn ngữ của câu (term)
                    Box(modifier = Modifier.padding(start = 23.dp, end = 23.dp, bottom = 4.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { termLangExpanded = true }
                        ) {
                            Text(
                                LANGUAGE_OPTIONS.find { it.first == termLangCode }?.second ?: termLangCode,
                                color = Color(0x80000000),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Image(
                                painter = painterResource(id = R.drawable.ic_dropdown),
                                contentDescription = "Dropdown",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = termLangExpanded,
                            onDismissRequest = { termLangExpanded = false }
                        ) {
                            LANGUAGE_OPTIONS.forEach { (code, label) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        termLangCode = code
                                        termLangExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .padding(bottom = 7.dp, start = 22.dp, end = 22.dp)
                            .height(2.dp)
                            .fillMaxWidth()
                            .background(color = Color(0xFF000000))
                    )
                    Text(
                        "Ngôn ngữ của câu",
                        color = Color(0xFF000000),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 47.dp, start = 22.dp)
                    )

                    // Ngôn ngữ của nghĩa (def)
                    Box(modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 7.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { defLangExpanded = true }
                        ) {
                            Text(
                                LANGUAGE_OPTIONS.find { it.first == defLangCode }?.second ?: defLangCode,
                                color = Color(0x80000000),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Image(
                                painter = painterResource(id = R.drawable.ic_dropdown),
                                contentDescription = "Dropdown",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = defLangExpanded,
                            onDismissRequest = { defLangExpanded = false }
                        ) {
                            LANGUAGE_OPTIONS.forEach { (code, label) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        defLangCode = code
                                        defLangExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .padding(bottom = 5.dp, start = 22.dp, end = 22.dp)
                            .height(2.dp)
                            .fillMaxWidth()
                            .background(color = Color(0xFF000000))
                    )
                    Text(
                        "Ngôn ngữ của nghĩa",
                        color = Color(0xFF000000),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 40.dp, start = 22.dp)
                    )

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // Nút lưu
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    if (uiState.isUpdating) Color(0xFFAAAAAA) else Color(0xFF58CC02)
                )
                .clickable(enabled = !uiState.isUpdating && titleValue.isNotBlank() && descValue.isNotBlank()) {
                    viewModel.updatePattern(
                        name = titleValue.trim(),
                        description = descValue.trim(),
                        termLangCode = termLangCode,
                        defLangCode = defLangCode,
                        isPublic = uiState.pattern?.isPublic ?: false
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isUpdating) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(28.dp),
                    strokeWidth = 3.dp
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_check_detailed_video_screen),
                    contentDescription = "Lưu",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
