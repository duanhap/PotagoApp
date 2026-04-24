package com.example.potago.presentation.screen.listofcardsscreen

import android.speech.tts.TextToSpeech
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.potago.R
import com.example.potago.domain.model.Word
import com.example.potago.domain.model.WordSet
import com.example.potago.presentation.navigation.Screen
import com.example.potago.presentation.screen.myvideo.AddButton
import com.example.potago.presentation.screen.recommendvideo.FilterTab
import com.example.potago.presentation.screen.setting.BackButton
import com.example.potago.presentation.ui.theme.Nunito
import java.util.Locale

// ────────────────────────────────────────────────────────────────────────────
// Screen Entry Point
// ────────────────────────────────────────────────────────────────────────────

@Composable
fun ListOfCardsScreen(
    navController: NavController,
    wordSetId: Long,
    wordSetName: String,
    viewModel: ListOfCardsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var tts: TextToSpeech? by remember { mutableStateOf(null) }

    DisposableEffect(Unit) {
        val ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Initialized successfully
            }
        }
        tts = ttsInstance
        onDispose {
            ttsInstance.stop()
            ttsInstance.shutdown()
        }
    }

    LaunchedEffect(wordSetId) {
        viewModel.loadCards(wordSetId)
    }

    var wordPendingDelete by remember { mutableStateOf<Word?>(null) }

    ListOfCardsScreenContent(
        uiState = uiState,
        wordSetName = wordSetName,
        onBackClick = { navController.popBackStack() },
        onFilterChange = viewModel::onFilterChange,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onVolumeClick = { word ->
            tts?.let { engine ->
                engine.language = Locale.forLanguageTag(uiState.termLanguageCode.ifBlank { "en" })
                engine.speak(word.term, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        },
        onEditClick = { word -> navController.navigate(Screen.EditCard(word.id)) },
        onDeleteClick = { word -> wordPendingDelete = word },
        onToggleStatus = { word -> viewModel.toggleWordStatus(word.id, word.status) },
        onAddClick = { navController.navigate(Screen.AddCard(wordSetId)) }
    )

    wordPendingDelete?.let { word ->
        DeleteConfirmBottomSheet(
            onDismiss = { wordPendingDelete = null },
            onConfirm = {
                viewModel.deleteWord(word.id)
                wordPendingDelete = null
            }
        )
    }
}

// ────────────────────────────────────────────────────────────────────────────
// Content (stateless, preview-friendly)
// ────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListOfCardsScreenContent(
    uiState: ListOfCardsUiState,
    wordSetName: String,
    onBackClick: () -> Unit,
    onFilterChange: (FilterType) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onVolumeClick: (Word) -> Unit,
    onEditClick: (Word) -> Unit,
    onDeleteClick: (Word) -> Unit,
    onToggleStatus: (Word) -> Unit = {},
    onAddClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopBarSection(
                title = "Danh sách thẻ",
                onBackClick = onBackClick
            )
        },
        containerColor = Color(0xFFFFFFFF)
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues))
        Box()
        {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // ── Appbar fake để giữ khoảng cách cho Appbar thật ──────────────────────────────────────────────
                TopBarSection(
                    title = "Danh sách thẻ",
                    onBackClick = onBackClick,
                    modifier = Modifier.alpha(0f)
                )
                // ── Filter Tabs ──────────────────────────────────────────────
                FilterTabs(
                    selectedType = uiState.filterType,
                    onFilterSelect = onFilterChange
                )

                // ── Search Bar ───────────────────────────────────────────────
                SearchBarField(
                    query = uiState.searchQuery,
                    onQueryChange = onSearchQueryChange
                )

                // ── Content ──────────────────────────────────────────────────
                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF1CB0F6))
                        }
                    }

                    uiState.error != null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = uiState.error,
                                color = Color(0xFFE53935),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    else -> {
                        val filteredCards = uiState.cards.filter { word ->
                            word.term.contains(uiState.searchQuery, ignoreCase = true) ||
                                    word.definition.contains(uiState.searchQuery, ignoreCase = true)
                        }

                        if (filteredCards.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Không có thẻ nào phù hợp",
                                        color = Color(0xFF9CA3AF),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        } else {
                            // Word count badge
                            WordCountBadge(count = filteredCards.size)

                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    start = 20.dp,
                                    end = 20.dp,
                                    top = 4.dp,
                                    bottom = 88.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(14.dp),
                            ) {
                                items(filteredCards, key = { it.id }) { word ->
                                    CardItemNode(
                                        word = word,
                                        onVolumeClick = { onVolumeClick(word) },
                                        onEditClick = { onEditClick(word) },
                                        onDeleteClick = { onDeleteClick(word) },
                                        onToggleStatus = { onToggleStatus(word) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            AddCardBottomBar(
                onClick = onAddClick,
                modifier = Modifier.align(Alignment.BottomCenter)
            )


        }

    }
}

// ────────────────────────────────────────────────────────────────────────────
// Top Bar
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun TopBarSection(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
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


// ────────────────────────────────────────────────────────────────────────────
// FAB
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun AddCardBottomBar(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(74.dp),
        color = Color.White,
        shadowElevation = 20.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Divider(
                thickness = 1.dp,
                color = Color(0xB3E5E7EB),
                modifier = Modifier
                    .align(Alignment.TopCenter)
            )
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF3B82F6))
                    .clickable(onClick = onClick),
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
    }
}

// ────────────────────────────────────────────────────────────────────────────
// Filter Tabs
// ────────────────────────────────────────────────────────────────────────────

@Composable
fun FilterTabs(
    selectedType: FilterType,
    onFilterSelect: (FilterType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        FilterTab(
            text = "Tất cả",
            isSelected = selectedType == FilterType.ALL,
            onClick = { onFilterSelect(FilterType.ALL) }
        )
        FilterTab(
            text = "Chưa thuộc",
            isSelected = selectedType == FilterType.LEARNING,
            onClick = { onFilterSelect(FilterType.LEARNING) }
        )
        FilterTab(
            text = "Đã thuộc",
            isSelected = selectedType == FilterType.LEARNED,
            onClick = { onFilterSelect(FilterType.LEARNED) }
        )
    }
}

// ────────────────────────────────────────────────────────────────────────────
// Search Bar
// ────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBarField(query: String, onQueryChange: (String) -> Unit) {
    var isFocused by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text("Tìm kiếm từ...", color = Color(0xFFB0B8C1), fontSize = 15.sp)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Tìm kiếm",
                tint = if (isFocused) Color(0xFF1CB0F6) else Color(0xFFB0B8C1),
                modifier = Modifier.size(22.dp)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 10.dp)
            .height(52.dp)
            .onFocusChanged { isFocused = it.isFocused },

        shape = RoundedCornerShape(26.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Color(0xFF1CB0F6),
            unfocusedBorderColor = Color(0xFFE5E7EB),
            focusedTextColor = Color(0xFF111827),
            unfocusedTextColor = Color(0xFF111827)
        ),
        singleLine = true
    )
}

// ────────────────────────────────────────────────────────────────────────────
// Word count badge
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun WordCountBadge(count: Int) {
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .background(Color(0xFFEFF6FF), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = "$count thuật ngữ",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1D4ED8)
            )
        }
    }
}

// ────────────────────────────────────────────────────────────────────────────
// Card Item
// ────────────────────────────────────────────────────────────────────────────

@Composable
fun CardItemNode(
    word: Word,
    modifier: Modifier = Modifier,
    onVolumeClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleStatus: () -> Unit = {}
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }

    val statusColor = if (word.status.equals("known", ignoreCase = true))
        Color(0xFF22C55E) else Color(0xFFF59E0B)

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.85f else 1f, label = "scale")

    // Rotation animation cho mũi tên
    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 0f else 180f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "arrow"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0x99DEDEDE), RoundedCornerShape(16.dp))
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // ── Top row: speaker + status chip + menu ────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEFF6FF))
                        .clickable { onVolumeClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_flashcard_speaker),
                        contentDescription = "Phát âm",
                        modifier = Modifier.size(18.dp),
                        tint = Color(0xFF1D4ED8)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .background(
                            color = statusColor.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (word.status.equals(
                                "known",
                                ignoreCase = true
                            )
                        ) "Đã thuộc" else "Chưa thuộc",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Tùy chọn",
                        modifier = Modifier
                            .size(22.dp)
                            .clickable { isMenuExpanded = true },
                        tint = Color(0xFFB0B8C1)
                    )
                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false },
                        offset = DpOffset(0.dp, (-4).dp),
                        containerColor = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                        shadowElevation = 4.dp,
                        tonalElevation = 0.dp
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (word.status.equals("known", ignoreCase = true)) "Chưa thuộc" else "Đã thuộc",
                                    color = Color(0xFF111827),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            onClick = { isMenuExpanded = false; onToggleStatus() },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = if (word.status.equals(
                                            "known",
                                            ignoreCase = true
                                        )
                                    ) R.drawable.ic_word_chua_thuoc else R.drawable.ic_word_da_thuoc),
                                    contentDescription = null,
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        )
                        HorizontalDivider(color = Color(0xFFE5E7EB))
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Chỉnh sửa",
                                    color = Color(0xFF111827),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            onClick = { isMenuExpanded = false; onEditClick() },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_detail_course_screen_edit),
                                    contentDescription = null,
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(20.dp)


                                )
                            }
                        )
                        HorizontalDivider(color = Color(0xFFE5E7EB))
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Xóa",
                                    color = Color(0xFF111827),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            onClick = { isMenuExpanded = false; onDeleteClick() },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_detail_course_screen_delete),
                                    contentDescription = null,
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(20.dp)

                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ── Term (luôn hiện, không giới hạn khi expanded) ────────
            Text(
                text = word.term.ifBlank { "Không có từ" },
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827),
                maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = Color(0xFFF3F4F6)
            )

            // ── Definition ──────────────────────────────────────────────
            Text(
                text = word.definition.ifBlank { "Không có định nghĩa" },
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF6B7280),
                maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis
            )

            // ── Description — chỉ hiện khi expanded ─────────────────
            AnimatedVisibility(
                visible = isExpanded && !word.description.isNullOrBlank(),
                enter = fadeIn() + expandVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = word.description ?: "",
                        fontSize = 12.sp,
                        color = Color(0xFFB0B8C1),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            // ── Nút thu/mở ───────────────────────────────────────────
            if (!word.description.isNullOrBlank() && isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        interactionSource = interactionSource,
                    ) {

                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = if (isExpanded) "Thu lại" else "Mở rộng",
                            tint = Color(0xFFB0B8C1),
                            modifier = Modifier
                                .size(20.dp)
                                .rotate(90f)
                                .graphicsLayer {
                                    rotationZ = arrowRotation; scaleX = scale; scaleY = scale
                                }


                        )

                    }
                }
            }
        }
    }
}

// ────────────────────────────────────────────────────────────────────────────
// Delete Confirm Bottom Sheet
// ────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteConfirmBottomSheet(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
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
                    shape = RoundedCornerShape(
                        topEnd = 16.dp,
                        bottomEnd = 16.dp,
                        bottomStart = 16.dp
                    ),
                    color = Color.White,
                    border =BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    shadowElevation = 2.dp
                ) {
                    Text(
                        text = "Xác nhận xóa chứ!?",
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
                                    onPress = {
                                        cancelPressed = true; tryAwaitRelease(); cancelPressed =
                                        false
                                    },
                                    onTap = { onDismiss() }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Từ chối",
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
                                    onPress = {
                                        confirmPressed = true; tryAwaitRelease(); confirmPressed =
                                        false
                                    },
                                    onTap = { onConfirm() }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Xác nhận",
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

// ────────────────────────────────────────────────────────────────────────────
// Preview
// ────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun ListOfCardsScreenPreview() {
    ListOfCardsScreenContent(
        uiState = ListOfCardsUiState(
            cards = listOf(
                Word(1, "Apple", "Quả táo", "A round fruit", "", "know"),
                Word(2, "Banana", "Quả chuối", "A yellow fruit", "", "learning"),
                Word(3, "Cherry", "Quả anh đào", null, "", "know")
            )
        ),
        wordSetName = "Fruits",
        onBackClick = {},
        onFilterChange = {},
        onSearchQueryChange = {},
        onVolumeClick = {},
        onEditClick = {},
        onDeleteClick = {},
        onAddClick = {}
    )
}
