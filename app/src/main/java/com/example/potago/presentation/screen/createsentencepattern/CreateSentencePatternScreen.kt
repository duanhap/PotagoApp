package com.example.potago.presentation.screen.createsentencepattern

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.potago.R
import com.example.potago.presentation.screen.createwordset.SaveButton
import com.example.potago.presentation.screen.setting.BackButton
import com.example.potago.presentation.ui.theme.Nunito
import kotlinx.coroutines.flow.collectLatest

private val GreenLight = Color(0xFFD7FFA4)
private val GreenPrimary = Color(0xFF58CC02)
private val TextHint = Color(0x80000000)
private val TextPrimary = Color(0xFF000000)
private val CardBorder = Color(0x26000000)
private val DeleteBg = Color(0xFFFFD4D4)
private val DeleteIcon = Color(0xFFFF6B6B)

@Composable
fun CreateSentencePatternScreen(
    navController: NavController,
    viewModel: CreateSentencePatternViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is CreateSentencePatternEvent.NavigateBack -> navController.popBackStack()
                is CreateSentencePatternEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopBar(
                isSaving = uiState.isSaving,
                onClose = viewModel::showCancelConfirm,
                onSave = viewModel::showSaveConfirm
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        val listState = rememberLazyListState()
        val isHeaderVisible by remember {
            derivedStateOf { listState.firstVisibleItemIndex == 0 }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // ── Header (green bg) ──────────────────────────────────
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                GreenLight,
                                RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                            )
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        UnderlineField(
                            value = uiState.title,
                            onValueChange = viewModel::onTitleChange,
                            placeholder = "Nhập tiêu đề vô đây",
                            label = "Tiêu đề"
                        )
                        UnderlineField(
                            value = uiState.description,
                            onValueChange = viewModel::onDescriptionChange,
                            placeholder = "Nhập mô tả vô đây",
                            label = "Mô tả"
                        )
                        LanguageDropdown(
                            value = uiState.termLangCode,
                            label = "Ngôn ngữ của câu",
                            onValueChange = viewModel::onTermLangChange
                        )
                        LanguageDropdown(
                            value = uiState.defLangCode,
                            label = "Ngôn ngữ của nghĩa",
                            onValueChange = viewModel::onDefLangChange
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                // ── Sentence cards ─────────────────────────────────────
                items(uiState.sentences, key = { it.id }) { sentence ->
                    SentenceCard(
                        sentence = sentence,
                        onTermChange = { viewModel.onSentenceTermChange(sentence.id, it) },
                        onDefinitionChange = {
                            viewModel.onSentenceDefinitionChange(
                                sentence.id,
                                it
                            )
                        },
                        onDelete = { viewModel.deleteOrClearSentence(sentence.id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // ── FAB ────────────────────────────────────────────────────
            Box(
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 20.dp,
                    color = Color.White
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Divider(
                            thickness = 1.dp,
                            color = Color(0xFFE5E7EB),
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = -10.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(GreenPrimary)
                                .clickable { viewModel.addSentence() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_add),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }

            // ── Collapsed header bar ───────────────────────────────────
            androidx.compose.animation.AnimatedVisibility(
                visible = !isHeaderVisible,
                modifier = Modifier.align(Alignment.TopCenter),
                enter = androidx.compose.animation.slideInVertically() + androidx.compose.animation.fadeIn(),
                exit = androidx.compose.animation.slideOutVertically() + androidx.compose.animation.fadeOut()
            ) {
                CollapsedHeaderBar(
                    title = uiState.title,
                    termLang = uiState.termLangCode,
                    defLang = uiState.defLangCode,
                    sentenceCount = uiState.sentences.size
                )
            }

            // ── Save confirm ───────────────────────────────────────────
            if (uiState.showSaveConfirm) {
                ConfirmBottomSheet(
                    message = "Xác nhận lưu chứ !?",
                    confirmText = "Xác nhận",
                    cancelText = "Tiếp tục",
                    onConfirm = {
                        viewModel.dismissSaveConfirm()
                        viewModel.save()
                    },
                    onCancel = viewModel::dismissSaveConfirm
                )
            }

            // ── Cancel confirm ─────────────────────────────────────────
            if (uiState.showCancelConfirm) {
                ConfirmBottomSheet(
                    message = "Xác nhận hủy chứ !?",
                    confirmText = "Xác nhận",
                    cancelText = "Tiếp tục",
                    onConfirm = { navController.popBackStack() },
                    onCancel = viewModel::dismissCancelConfirm
                )
            }
        }
    }
}

@Composable
private fun TopBar(
    isSaving: Boolean,
    onClose: () -> Unit,
    onSave: () -> Unit
) {
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
                Spacer(modifier = Modifier.width(60.dp)) // chừa chỗ cho back button
                Text(
                    text = "Tạo học phần",
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier.weight(1f),
                )
            }

            Box(
                modifier = Modifier.matchParentSize()
            ) {
                BackButton(
                    onClick = onClose,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .wrapContentSize()
                )
            }
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    color = GreenPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Box(
                    modifier = Modifier.matchParentSize()
                ) {
                    SaveButton(
                        onSave,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .wrapContentSize()
                    )
                }
            }

        }
    }
}

@Composable
private fun UnderlineField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    label: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextPrimary
            ),
            cursorBrush = SolidColor(GreenPrimary),
            decorationBox = { inner ->
                if (value.isEmpty()) Text(
                    text = placeholder,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextHint
                )
                inner()
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(thickness = 2.dp, color = Color.Black)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontFamily = Nunito,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 12.sp,
            color = Color.Black
        )
    }
}

@Composable
private fun LanguageDropdown(value: String, label: String, onValueChange: (String) -> Unit) {
    val languages =
        listOf("en" to "English", "vi" to "Tiếng Việt", "ja" to "日本語", "zh" to "中文")
    var expanded by remember { mutableStateOf(false) }
    val displayName = languages.find { it.first == value }?.second ?: value

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = displayName,
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xCC000000),
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .rotate(-90f),
                tint = Color.Black
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(thickness = 2.dp, color = Color.Black)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontFamily = Nunito,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 12.sp,
            color = Color.Black
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            languages.forEach { (code, name) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = name,
                            fontFamily = Nunito,
                            fontWeight = if (code == value) FontWeight.ExtraBold else FontWeight.Normal,
                            color = if (code == value) GreenPrimary else Color.Black
                        )
                    },
                    onClick = { onValueChange(code); expanded = false }
                )
            }
        }
    }
}

@Composable
private fun SentenceCard(
    sentence: SentenceInput,
    onTermChange: (String) -> Unit,
    onDefinitionChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 17.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CardField(value = sentence.term, onValueChange = onTermChange, label = "Câu")
            CardField(
                value = sentence.definition,
                onValueChange = onDefinitionChange,
                label = "Nghĩa"
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(31.dp)
                .background(DeleteBg)
                .clickable { onDelete() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_bin),
                contentDescription = "Xóa",
                tint = DeleteIcon,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun CardField(value: String, onValueChange: (String) -> Unit, label: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextPrimary
            ),
            cursorBrush = SolidColor(GreenPrimary),
            decorationBox = { inner ->
//                if (value.isEmpty()) Text(
//                    text = label,
//                    fontFamily = Nunito,
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 16.sp,
//                    color = TextHint
//                )
                inner()
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(thickness = 2.dp, color = Color.Black)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontFamily = Nunito,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 12.sp,
            color = Color.Black
        )
    }
}

@Composable
private fun CollapsedHeaderBar(
    title: String,
    termLang: String,
    defLang: String,
    sentenceCount: Int
) {
    val langMap = mapOf("en" to "EN", "vi" to "VI", "ja" to "JA", "zh" to "ZH")
    Surface(modifier = Modifier.fillMaxWidth(), color = GreenLight, shadowElevation = 4.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title.ifBlank { "Chưa có tiêu đề" },
                fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp,
                color = if (title.isBlank()) TextHint else TextPrimary,
                maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f)
            )
            Surface(shape = RoundedCornerShape(8.dp), color = Color.White.copy(alpha = 0.7f)) {
                Text(
                    text = "${langMap[termLang] ?: termLang.uppercase()} → ${langMap[defLang] ?: defLang.uppercase()}",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF46A302),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Surface(shape = RoundedCornerShape(8.dp), color = GreenPrimary) {
                Text(
                    text = "$sentenceCount câu",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConfirmBottomSheet(
    message: String,
    confirmText: String,
    cancelText: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onCancel,
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
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(horizontal = 10.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.asking_mascot_manage_video_screen),
                    contentDescription = null,
                    modifier = Modifier
                        .scale(0.8f)
                )
                Surface(
                    modifier = Modifier
                        .padding(top = 70.dp),
                    shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    shadowElevation = 2.dp
                ) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4B5563),
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(horizontal = 13.dp, vertical = 14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Cancel button
                var cancelPressed by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(51.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFE5E7EB))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (cancelPressed) 51.dp else 48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = { cancelPressed = true; tryAwaitRelease(); cancelPressed = false },
                                    onTap = { onCancel() }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cancelText,
                            fontFamily = Nunito,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = Color(0xFF374151)
                        )
                    }
                }

                // Confirm button
                var confirmPressed by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(51.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF46A302))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (confirmPressed) 51.dp else 48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF58CC02))
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = { confirmPressed = true; tryAwaitRelease(); confirmPressed = false },
                                    onTap = { onConfirm() }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = confirmText,
                            fontFamily = Nunito,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}