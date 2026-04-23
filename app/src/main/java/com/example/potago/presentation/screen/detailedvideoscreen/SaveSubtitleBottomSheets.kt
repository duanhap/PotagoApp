package com.example.potago.presentation.screen.detailedvideoscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.potago.R
import com.example.potago.domain.model.SetencePattern
import com.example.potago.presentation.ui.theme.Nunito
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException

// ─────────────────────────────────────────────────────────────────────────────
// Bước 1: Bottom sheet chỉnh sửa câu trước khi lưu
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSentenceBottomSheet(
    term: String,
    definition: String,
    onTermChange: (String) -> Unit,
    onDefinitionChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onContinue: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 11.dp)
                    .width(48.dp)
                    .height(6.dp)
                    .background(Color(0xFFE5E7EB), RoundedCornerShape(999.dp))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Chỉnh sửa trước khi lưu",
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Term field
            EditField(
                value = term,
                onValueChange = onTermChange,
                label = "Câu gốc"
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Definition field
            EditField(
                value = definition,
                onValueChange = onDefinitionChange,
                label = "Nghĩa / Dịch"
            )
            Spacer(modifier = Modifier.height(28.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BottomSheetButton(
                    text = "Hủy",
                    bgColor = Color.White,
                    borderColor = Color(0xFFE5E7EB),
                    textColor = Color(0xFF374151),
                    modifier = Modifier.weight(1f),
                    onClick = onDismiss
                )
                BottomSheetButton(
                    text = "Tiếp tục",
                    bgColor = Color(0xFF58CC02),
                    borderColor = Color(0xFF46A302),
                    textColor = Color.White,
                    modifier = Modifier.weight(1f),
                    onClick = onContinue
                )
            }
        }
    }
}

@Composable
private fun EditField(value: String, onValueChange: (String) -> Unit, label: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF111827)

            ),
            cursorBrush = SolidColor(Color(0xFF58CC02)),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(thickness = 2.dp, color = Color.Black)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontFamily = Nunito,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 12.sp,
            color = Color(0xFF6B7280)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Bước 2: Bottom sheet chọn SentencePattern
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatternPickerBottomSheet(
    patterns: List<SetencePattern>,
    isLoading: Boolean,
    isSaving: Boolean,
    videoTermLangCode: String,
    onDismiss: () -> Unit,
    onConfirm: (List<Int>) -> Unit,
    onCreatePattern: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }
    val selectedIds = remember { mutableStateListOf<Int>() }
    var isFocused by remember { mutableStateOf(false) }


    // Lọc theo termLangCode của video
    val filtered = remember(patterns, searchQuery, videoTermLangCode) {
        patterns
            .filter { it.termLanguageCode.equals(videoTermLangCode, ignoreCase = true) }
            .filter { searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true) }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 11.dp)
                    .width(48.dp)
                    .height(6.dp)
                    .background(Color(0xFFE5E7EB), RoundedCornerShape(999.dp))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 28.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Nhập từ tìm kiếm khóa học", color = Color(0xFFB0B8C1), fontSize = 14.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = if (isFocused) Color(0xFF58CC02) else Color(0xFFB0B8C1), modifier = Modifier.size(20.dp))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(48.dp)
                    .onFocusChanged { isFocused = it.isFocused },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF58CC02),
                    unfocusedBorderColor = Color(0xFFE5E7EB)
                ),
                singleLine = true,
                textStyle = MaterialTheme.typography.titleSmall.copy(color = Color(0xFF111827))
            )

            Spacer(modifier = Modifier.height(12.dp))

            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF58CC02))
                    }
                }
                filtered.isEmpty() -> {
                    // Không có pattern phù hợp
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Chưa có mẫu câu phù hợp với ngôn ngữ này",
                            fontFamily = Nunito,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF6B7280)
                        )
                        BottomSheetButton(
                            text = "Thêm mẫu câu mới",
                            bgColor = Color(0xFF58CC02),
                            borderColor = Color(0xFF46A302),
                            textColor = Color.White,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onCreatePattern
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 320.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filtered, key = { it.id }) { pattern ->
                            PatternCheckItem(
                                pattern = pattern,
                                isSelected = selectedIds.contains(pattern.id),
                                onToggle = {
                                    if (selectedIds.contains(pattern.id)) selectedIds.remove(pattern.id)
                                    else selectedIds.add(pattern.id)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        BottomSheetButton(
                            text = "Từ chối",
                            bgColor = Color.White,
                            borderColor = Color(0xFFE5E7EB),
                            textColor = Color(0xFF374151),
                            modifier = Modifier.weight(1f),
                            onClick = onDismiss
                        )
                        BottomSheetButton(
                            text = if (isSaving) "Đang lưu..." else "Xác nhận",
                            bgColor = if (selectedIds.isEmpty()) Color(0xFFE5E7EB) else Color(0xFF58CC02),
                            borderColor = if (selectedIds.isEmpty()) Color(0xFFE5E7EB) else Color(0xFF46A302),
                            textColor = if (selectedIds.isEmpty()) Color(0xFF9CA3AF) else Color.White,
                            modifier = Modifier.weight(1f),
                            onClick = { if (selectedIds.isNotEmpty() && !isSaving) onConfirm(selectedIds.toList()) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PatternCheckItem(
    pattern: SetencePattern,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFFD7FFA4) else Color.White)
            .border(1.dp, if (isSelected) Color(0xFF46A302) else Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
            .clickable(onClick = onToggle)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFF89E219), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_sentence_partten),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = pattern.name,
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.Black
            )
            Text(
                text = formatPatternMeta(pattern),
                fontFamily = Nunito,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = Color(0xFF6B7280)
            )
        }
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF58CC02),
                uncheckedColor = Color(0xFFD1D5DB)
            )
        )
    }
}

private fun formatPatternMeta(pattern: SetencePattern): String {
    val monthYear = try {
        val date = if (pattern.createdAt.contains("T"))
            OffsetDateTime.parse(pattern.createdAt).toLocalDate()
        else java.time.LocalDate.parse(pattern.createdAt)
        "Tháng ${date.monthValue} năm ${date.year}"
    } catch (_: Exception) { "" }
    return listOfNotNull(monthYear.takeIf { it.isNotBlank() }).joinToString(" - ")
}

// ─────────────────────────────────────────────────────────────────────────────
// Shared button component
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun BottomSheetButton(
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
                color = textColor
            )
        }
    }
}
