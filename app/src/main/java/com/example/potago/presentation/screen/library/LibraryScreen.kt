package com.example.potago.presentation.screen.library

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.potago.R
import com.example.potago.domain.model.SetencePattern
import com.example.potago.domain.model.WordSet
import com.example.potago.presentation.navigation.Screen
import com.example.potago.presentation.screen.UiState
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun LibraryScreen(
    navController: NavController,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val recentWordSetsState by viewModel.recentWordSets.collectAsState()
    val allWordSetsState by viewModel.allWordSets.collectAsState()

    val recentSentencePatternsState by viewModel.recentSentencePatterns.collectAsState()
    val allSentencePatternsState by viewModel.allSentencePatterns.collectAsState()

    // Hoisted the UI content to a stateless Composable to allow Previews
    LibraryScreenContent(
        recentWordSetsState = recentWordSetsState,
        allWordSetsState = allWordSetsState,
        recentSentencePatternsState = recentSentencePatternsState,
        allSentencePatternsState = allSentencePatternsState,
        onWordSetClick = { wordSet ->
            navController.navigate(Screen.FlashCard(wordSet.id, wordSet.name))
        },
        onSentencePatternClick = { pattern ->
            navController.navigate(Screen.DetailSentencePattern(pattern.id, pattern.name))
        },
        onRetry = { viewModel.refreshLibrary() }
    )
}

@Composable
private fun LibraryScreenContent(
    recentWordSetsState: UiState<List<WordSet>>,
    allWordSetsState: UiState<List<WordSet>>,
    recentSentencePatternsState: UiState<List<SetencePattern>>,
    allSentencePatternsState: UiState<List<SetencePattern>>,
    onWordSetClick: (WordSet) -> Unit,
    onSentencePatternClick: (SetencePattern) -> Unit,
    onRetry: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(LibraryTab.COURSE) }
    var isAddOverlayVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF))
    ) {
        Column {
            LibraryHeader(
                onAddClick = { isAddOverlayVisible = true }
            )
            LibraryTabSection(
                selectedTab = selectedTab,
                onSelectTab = { selectedTab = it }
            )

            if (selectedTab == LibraryTab.COURSE) {
                CourseTabContent(
                    recentState = recentWordSetsState,
                    allState = allWordSetsState,
                    onWordSetClick = onWordSetClick,
                    onRetry = onRetry
                )
            } else {
                SentenceTabContent(
                    recentState = recentSentencePatternsState,
                    allState = allSentencePatternsState,
                    onSentencePatternClick = onSentencePatternClick,
                    onRetry = onRetry
                )
            }
        }

        if (isAddOverlayVisible) {
            AddOverlay(
                selectedTab = selectedTab,
                onDismiss = { isAddOverlayVisible = false },
                onChooseCourse = {
                    isAddOverlayVisible = false
                },
                onChooseSentence = {
                    isAddOverlayVisible = false
                }
            )
        }
    }
}

private enum class LibraryTab {
    COURSE,
    SENTENCE
}

@Composable
private fun LibraryHeader(
    onAddClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(59.dp)
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Thư viện",
                    fontSize = 32.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = "Add",
                    modifier = Modifier
                        .size(36.dp)
                        .clickable(onClick = onAddClick),
                    tint = Color.Unspecified
                )
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = Color(0xFFF3F4F6)
            )
        }
    }
}

@Composable
private fun AddOverlay(
    selectedTab: LibraryTab,
    onDismiss: () -> Unit,
    onChooseCourse: () -> Unit,
    onChooseSentence: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x99000000))
            .clickable(onClick = onDismiss)
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color(0xFFE5E7EB), shape = RoundedCornerShape(999.dp))
            )

            Spacer(modifier = Modifier.height(12.dp))

            AddOverlayButton(
                selected = selectedTab == LibraryTab.COURSE,
                iconRes = R.drawable.ic_folder,
                text = "Học phần",
                onClick = onChooseCourse
            )
            Spacer(modifier = Modifier.height(12.dp))
            AddOverlayButton(
                selected = selectedTab == LibraryTab.SENTENCE,
                iconRes = R.drawable.ic_library,
                text = "Mẫu câu",
                onClick = onChooseSentence
            )
        }
    }
}

@Composable
private fun AddOverlayButton(
    selected: Boolean,
    iconRes: Int,
    text: String,
    onClick: () -> Unit
) {
    val selectedBg = Color(0xFFD7FFA4)
    val borderColor = Color(0xFF46A302)
    val unselectedBg = Color.White

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                color = if (selected) selectedBg else unselectedBg,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = if (selected) borderColor else Color(0xFFE5E7EB),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = text,
                modifier = Modifier.size(24.dp),
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun LibraryTabSection(
    selectedTab: LibraryTab,
    onSelectTab: (LibraryTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 14.dp, end = 20.dp, bottom = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        LibraryTabChip(
            text = "Học phần",
            selected = selectedTab == LibraryTab.COURSE,
            onClick = { onSelectTab(LibraryTab.COURSE) }
        )
        LibraryTabChip(
            text = "Mẫu câu",
            selected = selectedTab == LibraryTab.SENTENCE,
            onClick = { onSelectTab(LibraryTab.SENTENCE) }
        )
    }
}

@Composable
private fun LibraryTabChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(103.dp)
            .height(34.dp)
            .background(Color.White, RoundedCornerShape(24.dp))
            .border(
                width = 1.dp,
                color = if (selected) Color.Black else Color(0x1A000000),
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
        )
    }
}

@Composable
private fun CourseTabContent(
    recentState: UiState<List<WordSet>>,
    allState: UiState<List<WordSet>>,
    onWordSetClick: (WordSet) -> Unit,
    onRetry: () -> Unit
) {
    val isLoading = allState is UiState.Loading && recentState is UiState.Loading
    val recentWordSets = (recentState as? UiState.Success<List<WordSet>>)?.data ?: emptyList()
    val allWordSets = (allState as? UiState.Success<List<WordSet>>)?.data ?: emptyList()

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF46A302))
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        if (recentWordSets.isNotEmpty()) {
            item {
                SectionHeader(title = "Gần đây")
            }
            items(recentWordSets) { wordSet ->
                LibraryItemCard(
                    title = wordSet.name,
                    subtitle = "${wordSet.amountOfWords ?: 0} thuật ngữ",
                    timeAgo = formatLastOpened(wordSet.lastOpened),
                    onClick = { onWordSetClick(wordSet) }
                )
            }
        }

        item {
            SectionHeader(title = "Tất cả học phần")
        }

        if (allWordSets.isEmpty()) {
            item {
                EmptyState(message = "Bạn chưa có học phần nào")
            }
        } else {
            items(allWordSets) { wordSet ->
                LibraryItemCard(
                    title = wordSet.name,
                    subtitle = "${wordSet.amountOfWords ?: 0} thuật ngữ",
                    timeAgo = formatLastOpened(wordSet.lastOpened),
                    onClick = { onWordSetClick(wordSet) }
                )
            }
        }
    }
}

@Composable
private fun SentenceTabContent(
    recentState: UiState<List<SetencePattern>>,
    allState: UiState<List<SetencePattern>>,
    onSentencePatternClick: (SetencePattern) -> Unit,
    onRetry: () -> Unit
) {
    val isLoading = allState is UiState.Loading && recentState is UiState.Loading
    val recentPatterns = (recentState as? UiState.Success<List<SetencePattern>>)?.data ?: emptyList()
    val allPatterns = (allState as? UiState.Success<List<SetencePattern>>)?.data ?: emptyList()

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF46A302))
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        if (recentPatterns.isNotEmpty()) {
            item {
                SectionHeader(title = "Gần đây")
            }
            items(recentPatterns) { pattern ->
                LibraryItemCard(
                    title = pattern.name,
                    subtitle = pattern.description,
                    timeAgo = formatLastOpened(pattern.lastOpened),
                    onClick = { onSentencePatternClick(pattern) }
                )
            }
        }

        item {
            SectionHeader(title = "Tất cả mẫu câu")
        }

        if (allPatterns.isEmpty()) {
            item {
                EmptyState(message = "Bạn chưa có mẫu câu nào")
            }
        } else {
            items(allPatterns) { pattern ->
                LibraryItemCard(
                    title = pattern.name,
                    subtitle = pattern.description,
                    timeAgo = formatLastOpened(pattern.lastOpened),
                    onClick = { onSentencePatternClick(pattern) }
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF6B7280),
        modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 8.dp)
    )
}

@Composable
private fun LibraryItemCard(
    title: String,
    subtitle: String,
    timeAgo: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = Color(0xFF4B5563)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_solar_calendar),
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color(0xFF9CA3AF)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = timeAgo,
                    fontSize = 12.sp,
                    color = Color(0xFF9CA3AF),
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = message, color = Color.Gray)
    }
}

private fun formatLastOpened(lastOpened: String?): String {
    if (lastOpened.isNullOrBlank()) return "Chưa mở"
    return try {
        val odt = OffsetDateTime.parse(lastOpened)
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        "Mở lần cuối: ${odt.format(formatter)}"
    } catch (e: Exception) {
        "Mở lần cuối: $lastOpened"
    }
}
