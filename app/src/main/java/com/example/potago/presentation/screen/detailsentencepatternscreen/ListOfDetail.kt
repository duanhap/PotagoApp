package com.example.potago.presentation.screen.detailsentencepatternscreen

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search


import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
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
import androidx.navigation.compose.rememberNavController
import com.example.potago.R
import com.example.potago.presentation.navigation.Screen
import com.example.potago.presentation.screen.setting.BackButton
import java.util.Locale

private val CheckCircle = Icons.Default.CheckCircle

@Composable
fun ListOfDetailScreen(
    navController: NavController,
    patternId: Int = 0,
    viewModel: ListOfDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var tts: TextToSpeech? by remember { mutableStateOf(null) }

    DisposableEffect(Unit) {
        val instance = TextToSpeech(context) {}
        tts = instance
        onDispose { instance.stop(); instance.shutdown() }
    }

    LaunchedEffect(patternId) {
        if (patternId > 0) viewModel.loadSentences(patternId)
    }
    LaunchedEffect(Unit) {
        if (patternId > 0) viewModel.refreshSentences(patternId)
    }

    Scaffold(
        topBar = {
            TopBarSection(
                title = "Danh sách câu",
                onBackClick = { navController.popBackStack() }
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues))
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Fake TopBar để giữ khoảng cách
                TopBarSection(
                    title = "Danh sách câu",
                    onBackClick = {},
                    modifier = Modifier.alpha(0f)
                )

                // Filter tabs
                FilterTabsRow(
                    selectedFilter = uiState.selectedFilter,
                    onFilterSelect = { viewModel.filterByStatus(it) }
                )

                // Search bar
                var searchQuery by remember { mutableStateOf("") }
                SearchBarField(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it }
                )

                val displayedSentences = remember(uiState.filteredSentences, searchQuery) {
                    if (searchQuery.isBlank()) uiState.filteredSentences
                    else uiState.filteredSentences.filter {
                        it.term.contains(searchQuery, ignoreCase = true) ||
                                it.definition.contains(searchQuery, ignoreCase = true)
                    }
                }

                when {
                    uiState.isLoading -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator(color = Color(0xFF46A302)) }

                    uiState.error != null -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { Text(text = uiState.error ?: "", color = Color.Red) }

                    displayedSentences.isEmpty() -> Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isBlank()) "Không có câu nào" else "Không tìm thấy câu phù hợp",
                            color = Color(0xFF9CA3AF),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    else -> {
                        // Count badge
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .padding(bottom = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFEDFBE8), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${displayedSentences.size} câu",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF46A302)
                                )
                            }
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = 20.dp, end = 20.dp,
                                top = 4.dp, bottom = 88.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            itemsIndexed(
                                displayedSentences,
                                key = { _, s -> s.id }) { _, sentence ->
                                SentenceCardItem(
                                    term = sentence.term,
                                    definition = sentence.definition,
                                    status = sentence.status,
                                    onSpeakClick = {
                                        tts?.let { engine ->
                                            engine.language = Locale.forLanguageTag("en")
                                            engine.speak(
                                                sentence.term,
                                                TextToSpeech.QUEUE_FLUSH,
                                                null,
                                                null
                                            )
                                        }
                                    },
                                    onEditClick = {
                                        navController.navigate(
                                            Screen.EditSentence(
                                                sentence.id
                                            )
                                        )
                                    },
                                    onDeleteClick = { viewModel.deleteSentence(sentence.id) },
                                    onToggleStatus = {
                                        val newStatus =
                                            if (sentence.status == "known") "unknown" else "known"
                                        viewModel.updateSentenceStatus(sentence.id, newStatus)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // FAB bottom
            AddSentenceBottomBar(
                onClick = { navController.navigate(Screen.AddSentence(patternId)) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Sentence Card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SentenceCardItem(
    term: String,
    definition: String,
    status: String,
    onSpeakClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleStatus: () -> Unit
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    val statusColor =
        if (status.equals("known", ignoreCase = true)) Color(0xFF22C55E) else Color(0xFFF59E0B)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0x99DEDEDE), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // Top row: speaker + status + menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEDFBE8))
                        .clickable { onSpeakClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_flashcard_speaker),
                        contentDescription = "Phát âm",
                        modifier = Modifier.size(18.dp),
                        tint = Color(0xFF46A302)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (status.equals(
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
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
                        shadowElevation = 4.dp,
                        tonalElevation = 0.dp
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (status == "unknown") "Đã thuộc" else "Chưa thuộc",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF111827)
                                )
                            },
                            onClick = { isMenuExpanded = false; onToggleStatus() },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = if (status == "unknown")
                                     R.drawable.ic_sentence_da_thuoc else R.drawable.ic_setence_chua_thuoc),
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
                                    painter = painterResource(R.drawable.icon_edit_sentence_partten),
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
                                    painter = painterResource(R.drawable.icon_delete_setence_pattern),
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

            Text(
                text = term.ifBlank { "Không có câu" },
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = Color(0xFFF3F4F6)
            )

            Text(
                text = definition.ifBlank { "Không có nghĩa" },
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF6B7280),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Filter Tabs
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun FilterTabsRow(selectedFilter: String, onFilterSelect: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        listOf(
            "all" to "Tất cả",
            "unknown" to "Chưa thuộc",
            "known" to "Đã thuộc"
        ).forEach { (status, label) ->
            val isSelected = selectedFilter == status
            Surface(
                onClick = { onFilterSelect(status) },
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) Color.Black else Color.LightGray.copy(alpha = 0.5f)
                ),
                color = Color.Transparent
            ) {
                Text(
                    text = label,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) Color.Black else Color.Gray
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Search Bar
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBarField(query: String, onQueryChange: (String) -> Unit) {
    var isFocused by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Nhập câu tìm kiếm...", color = Color(0xFFB0B8C1), fontSize = 15.sp) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = if (isFocused) Color(0xFF46A302) else Color(0xFFB0B8C1),
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
            focusedBorderColor = Color(0xFF46A302),
            unfocusedBorderColor = Color(0xFFE5E7EB),
            focusedTextColor = Color(0xFF111827),
            unfocusedTextColor = Color(0xFF111827)
        ),
        singleLine = true
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// FAB Bottom Bar
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AddSentenceBottomBar(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(74.dp),
        color = Color.White,
        shadowElevation = 20.dp
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Divider(
                thickness = 1.dp,
                color = Color(0xB3E5E7EB),
                modifier = Modifier.align(Alignment.TopCenter)
            )
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF46A302))
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = "Thêm câu",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Top Bar
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TopBarSection(title: String, onBackClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 3.dp,
        shadowElevation = 4.dp,
        color = Color.White
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(60.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier.weight(1f)
                )
            }
            Box(modifier = Modifier.matchParentSize()) {
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
// Preview
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ListOfDetailScreenPreview() {
    ListOfDetailScreen(rememberNavController(), 1)
}
