package com.example.potago.presentation.screen.createwordset

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.potago.R
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
fun CreateWordSetScreen(
    navController: NavController,
    viewModel: CreateWordSetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is CreateWordSetEvent.NavigateBack -> navController.popBackStack()
                is CreateWordSetEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CreateWordSetTopBar(
                isSaving = uiState.isSaving,
                onClose = viewModel::showCancelConfirm,
                onSave = viewModel::showSaveConfirm
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // ── Header section (green bg) ──────────────────────────
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(GreenLight, RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Title
                        UnderlineField(
                            value = uiState.title,
                            onValueChange = viewModel::onTitleChange,
                            placeholder = "Nhập tiêu đề vô đây",
                            label = "Tiêu đề"
                        )
                        // Description
                        UnderlineField(
                            value = uiState.description,
                            onValueChange = viewModel::onDescriptionChange,
                            placeholder = "Nhập mô tả vô đây",
                            label = "Mô tả"
                        )
                        // Term language
                        LanguageDropdown(
                            value = uiState.termLangCode,
                            label = "Ngôn ngữ thuật ngữ",
                            onValueChange = viewModel::onTermLangChange
                        )
                        // Def language
                        LanguageDropdown(
                            value = uiState.defLangCode,
                            label = "Ngôn ngữ định nghĩa",
                            onValueChange = viewModel::onDefLangChange
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                // ── Cards ──────────────────────────────────────────────
                items(uiState.cards, key = { it.id }) { card ->
                    CardItem(
                        card = card,
                        onTermChange = { viewModel.onCardTermChange(card.id, it) },
                        onDefinitionChange = { viewModel.onCardDefinitionChange(card.id, it) },
                        onDescriptionChange = { viewModel.onCardDescriptionChange(card.id, it) },
                        onDelete = { viewModel.deleteOrClearCard(card.id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // ── FAB add card ───────────────────────────────────────────
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(GreenPrimary)
                        .clickable { viewModel.addCard() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add),
                        contentDescription = "Thêm thẻ",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // ── Save confirm sheet ─────────────────────────────────────
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

            // ── Cancel confirm sheet ───────────────────────────────────
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
private fun CreateWordSetTopBar(
    isSaving: Boolean,
    onClose: () -> Unit,
    onSave: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xE6FFFFFF),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(59.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Close (X) button — rotated + icon
            IconButton(onClick = onClose) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = "Đóng",
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(45f),
                    tint = Color.Black
                )
            }
            Text(
                text = "Tạo học phần",
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp,
                modifier = Modifier.weight(1f)
            )
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    color = GreenPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                IconButton(onClick = onSave) {
                    Icon(
                        painter = painterResource(R.drawable.ic_check_green_circle),
                        contentDescription = "Lưu",
                        modifier = Modifier.size(36.dp),
                        tint = Color.Unspecified
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
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextHint
                    )
                }
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
private fun LanguageDropdown(
    value: String,
    label: String,
    onValueChange: (String) -> Unit
) {
    val languages = listOf(
        "en" to "English",
        "vi" to "Tiếng Việt",
        "ja" to "日本語",
        "zh" to "中文"
    )
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
                    onClick = {
                        onValueChange(code)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun CardItem(
    card: CardInput,
    onTermChange: (String) -> Unit,
    onDefinitionChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
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
            CardField(
                value = card.term,
                onValueChange = onTermChange,
                label = "Thuật ngữ"
            )
            CardField(
                value = card.definition,
                onValueChange = onDefinitionChange,
                label = "Định nghĩa"
            )
            CardField(
                value = card.description,
                onValueChange = onDescriptionChange,
                label = "Mô tả chi tiết"
            )
        }
        // Delete bar
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
private fun CardField(
    value: String,
    onValueChange: (String) -> Unit,
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
                if (value.isEmpty()) {
                    Text(
                        text = label,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextHint
                    )
                }
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

            // Mascot + bubble row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Mascot with ? overlay
                Box(
                    modifier = Modifier.width(130.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_looking_mascot),
                        contentDescription = null,
                        modifier = Modifier
                            .size(width = 112.dp, height = 120.dp)
                            .padding(start = 4.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        text = "?",
                        fontFamily = Nunito,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 52.sp,
                        color = Color(0xFF4B4B4B),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 4.dp)
                    )
                }

                // Speech bubble
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 10.dp, start = 4.dp),
                    shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    shadowElevation = 2.dp
                ) {
                    Text(
                        text = message,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        color = Color(0xFF4B5563),
                        modifier = Modifier.padding(horizontal = 15.dp, vertical = 16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

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
