package com.example.potago.presentation.screen.listofcardsscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

    LaunchedEffect(wordSetId) {
        viewModel.loadCards(wordSetId)
    }

    ListOfCardsScreenContent(
        uiState = uiState,
        wordSetName = wordSetName,
        onBackClick = { navController.popBackStack() },
        onFilterChange = viewModel::onFilterChange,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onVolumeClick = { /* Handle TTS / Audio */ },
        onEditClick = { /* Handle Edit Card */ },
        onDeleteClick = { /* Handle Delete Card */ },
        onAddClick = { /* Handle Add Card Navigation */ }
    )
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
    onAddClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopBarSection(
                title = wordSetName,
                onBackClick = onBackClick
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            AddCardFab(onClick = onAddClick)
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
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
                        val matchTab = when (uiState.filterType) {
                            FilterType.ALL -> true
                            FilterType.LEARNED -> word.status.equals("know", ignoreCase = true)
                            FilterType.LEARNING -> !word.status.equals("know", ignoreCase = true)
                        }
                        val matchSearch =
                            word.term.contains(uiState.searchQuery, ignoreCase = true) ||
                                    word.definition.contains(uiState.searchQuery, ignoreCase = true)
                        matchTab && matchSearch
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
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(filteredCards, key = { it.id }) { word ->
                                CardItemNode(
                                    word = word,
                                    onVolumeClick = { onVolumeClick(word) },
                                    onEditClick = { onEditClick(word) },
                                    onDeleteClick = { onDeleteClick(word) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ────────────────────────────────────────────────────────────────────────────
// Top Bar
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun TopBarSection(title: String, onBackClick: () -> Unit) {
    Surface(
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Quay lại",
                modifier = Modifier
                    .size(36.dp)
                    .clickable(onClick = onBackClick),
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// ────────────────────────────────────────────────────────────────────────────
// FAB
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun AddCardFab(onClick: () -> Unit) {
    Surface(
        shape = CircleShape,
        color = Color(0xFF89E219),
        modifier = Modifier
            .size(60.dp)
            .padding(bottom = 4.dp),
        shadowElevation = 6.dp,
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Thêm thẻ mới",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
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
        FilterTabItem(
            text = "Tất cả",
            isSelected = selectedType == FilterType.ALL,
            onClick = { onFilterSelect(FilterType.ALL) }
        )
        FilterTabItem(
            text = "Chưa thuộc",
            isSelected = selectedType == FilterType.LEARNING,
            onClick = { onFilterSelect(FilterType.LEARNING) }
        )
        FilterTabItem(
            text = "Đã thuộc",
            isSelected = selectedType == FilterType.LEARNED,
            onClick = { onFilterSelect(FilterType.LEARNED) }
        )
    }
}

@Composable
fun RowScope.FilterTabItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor = if (isSelected) Color.Black else Color.White
    val textColor = if (isSelected) Color.White else Color(0xFF6B7280)
    val borderColor = if (isSelected) Color.Black else Color(0xFFE5E7EB)

    Box(
        modifier = Modifier
            .weight(1f)
            .height(36.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, borderColor, RoundedCornerShape(24.dp))
            .background(bgColor, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = textColor
        )
    }
}

// ────────────────────────────────────────────────────────────────────────────
// Search Bar
// ────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBarField(query: String, onQueryChange: (String) -> Unit) {
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
                tint = Color(0xFFB0B8C1),
                modifier = Modifier.size(22.dp)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 10.dp)
            .height(52.dp),
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
    onVolumeClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    // Status indicator color
    val statusColor = if (word.status.equals("know", ignoreCase = true))
        Color(0xFF22C55E) else Color(0xFFF59E0B)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp,
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
                // Speaker / audio icon
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEFF6FF))
                        .clickable { onVolumeClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_earphone),
                        contentDescription = "Phát âm",
                        modifier = Modifier.size(18.dp),
                        tint = Color(0xFF1D4ED8)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Status chip
                Box(
                    modifier = Modifier
                        .background(
                            color = statusColor.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (word.status.equals("know", ignoreCase = true))
                            "Đã thuộc" else "Chưa thuộc",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // More options menu
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
                        offset = DpOffset(0.dp, (-4).dp)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Chỉnh sửa",
                                    color = Color(0xFF111827),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            onClick = {
                                isMenuExpanded = false
                                onEditClick()
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = Color(0xFF6B7280)
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Xóa",
                                    color = Color(0xFFE53935),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            onClick = {
                                isMenuExpanded = false
                                onDeleteClick()
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = Color(0xFFE53935)
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ── Term ────────────────────────────────────────────────────
            Text(
                text = word.term.ifBlank { "Không có từ" },
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Divider
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
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            // ── Description (optional) ───────────────────────────────────
            if (!word.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = word.description,
                    fontSize = 12.sp,
                    color = Color(0xFFB0B8C1),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
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
