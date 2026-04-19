package com.example.potago.presentation.screen.listofcardsscreen

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
        onVolumeClick = { /* Handle TTS or Audio */ },
        onEditClick = { /* Handle Edit Card */ },
        onDeleteClick = { /* Handle Delete Card */ },
        onAddClick = { /* Handle Add Card Navigation */ }
    )
}

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
                        contentDescription = "Back",
                        modifier = Modifier
                            .size(36.dp)
                            .clickable(onClick = onBackClick),
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Danh sách thẻ",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            Surface(
                shape = CircleShape,
                color = Color(0xFF89E219),
                modifier = Modifier
                    .size(64.dp)
                    .padding(bottom = 8.dp),
                shadowElevation = 4.dp,
                onClick = onAddClick
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Thêm phần tử mới",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        },
        containerColor = Color(0xFFFAFAFA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filter Tabs
            FilterTabs(
                selectedType = uiState.filterType,
                onFilterSelect = onFilterChange
            )

            // Search Bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text("Nhập từ tìm kiếm", color = Color(0xFF9CA3AF), fontSize = 16.sp)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(28.dp)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 12.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF3F4F6),
                    unfocusedContainerColor = Color(0xFFF3F4F6),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                singleLine = true
            )

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.Black)
                }
            } else {
                val filteredCards = uiState.cards.filter { word ->
                    val matchTab = when (uiState.filterType) {
                        FilterType.ALL -> true
                        FilterType.LEARNED -> word.status.equals("know", ignoreCase = true)
                        FilterType.LEARNING -> !word.status.equals("know", ignoreCase = true)
                    }
                    val matchSearch = word.term.contains(uiState.searchQuery, ignoreCase = true) ||
                            word.definition.contains(uiState.searchQuery, ignoreCase = true)
                    
                    matchTab && matchSearch
                }

                if (filteredCards.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Không có thẻ nào phù hợp",
                            color = Color.Gray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
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

@Composable
fun FilterTabs(
    selectedType: FilterType,
    onFilterSelect: (FilterType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FilterTabItem("Tất cả", selectedType == FilterType.ALL) { onFilterSelect(FilterType.ALL) }
        FilterTabItem("Chưa thuộc", selectedType == FilterType.LEARNING) { onFilterSelect(FilterType.LEARNING) }
        FilterTabItem("Đã thuộc", selectedType == FilterType.LEARNED) { onFilterSelect(FilterType.LEARNED) }
    }
}

@Composable
fun RowScope.FilterTabItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val borderColor = if (isSelected) Color.Black else Color(0x33000000)
    val bgColor = Color.White
    val textColor = Color.Black

    Box(
        modifier = Modifier
            .weight(1f)
            .height(34.dp)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(24.dp)
            )
            .background(
                color = bgColor,
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            lineHeight = 20.sp,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun CardItemNode(
    word: Word,
    onVolumeClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
            .padding(vertical = 14.dp, horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Speaker icon
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onVolumeClick() },
                    contentAlignment = Alignment.CenterStart
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_earphone), // Use an existing icon or earphone
                        contentDescription = "Speaker",
                        modifier = Modifier.size(24.dp),
                        tint = Color(0xFF3B82F6) // Muted blue
                    )
                }

                Box {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { isMenuExpanded = true },
                        tint = Color(0x80000000)
                    )

                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false },
                        offset = DpOffset(0.dp, (-8).dp)
                    ) {
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    "Chỉnh sửa", 
                                    color = Color.Black, 
                                    fontSize = 14.sp, 
                                    fontWeight = FontWeight.Bold 
                                ) 
                            },
                            onClick = {
                                isMenuExpanded = false
                                onEditClick()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = Color.Black)
                            }
                        )
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    "Xóa", 
                                    color = Color.Red, 
                                    fontSize = 14.sp, 
                                    fontWeight = FontWeight.Bold 
                                ) 
                            },
                            onClick = {
                                isMenuExpanded = false
                                onDeleteClick()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = word.term.ifBlank { "Không có từ" },
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = word.definition.ifBlank { "Không có định nghĩa" },
                fontSize = 14.sp,
                color = Color.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = word.description.orEmpty(),
                fontSize = 12.sp,
                color = Color(0x80000000),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListOfCardsScreenPreview() {
    ListOfCardsScreenContent(
        uiState = ListOfCardsUiState(
            cards = listOf(
                Word(1, "El perro", "The Dog", "Hi", "", "know"),
                Word(2, "El gato", "The Cat", "", "", "learning")
            )
        ),
        wordSetName = "Animales",
        onBackClick = {},
        onFilterChange = {},
        onSearchQueryChange = {},
        onVolumeClick = {},
        onEditClick = {},
        onDeleteClick = {},
        onAddClick = {}
    )
}
