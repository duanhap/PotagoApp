package com.example.potago.presentation.screen.library

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.potago.R
import com.example.potago.domain.model.WordSet
import com.example.potago.presentation.screen.UiState
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Composable
fun LibraryScreen(
    navController: NavController,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf(LibraryTab.COURSE) }
    val recentWordSetsState by viewModel.recentWordSets.collectAsState()
    val allWordSetsState by viewModel.allWordSets.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F4F6))
    ) {
        LibraryHeader()
        LibraryTabSection(
            selectedTab = selectedTab,
            onSelectTab = { selectedTab = it }
        )

        if (selectedTab == LibraryTab.COURSE) {
            CourseTabContent(
                recentState = recentWordSetsState,
                allState = allWordSetsState,
                onRetry = viewModel::refreshLibrary
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                EmptyContentState(message = "Chưa có mẫu câu nào cả")
            }
        }
    }
}

private enum class LibraryTab {
    COURSE,
    SENTENCE
}

@Composable
private fun LibraryHeader() {
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
                    modifier = Modifier.size(36.dp),
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
    onRetry: () -> Unit
) {
    val isLoading = allState is UiState.Loading && recentState is UiState.Loading
    val recentWordSets = (recentState as? UiState.Success)?.data.orEmpty()
    val allWordSets = (allState as? UiState.Success)?.data.orEmpty()
    val fallbackRecent = if (recentWordSets.isEmpty()) allWordSets.take(3) else recentWordSets
    val errorMessage = when {
        allWordSets.isNotEmpty() -> null
        allState is UiState.Error -> allState.message
        recentState is UiState.Error -> recentState.message
        else -> null
    }

    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Black)
            }
        }

        errorMessage != null -> {
            ErrorContentState(
                message = errorMessage,
                onRetry = onRetry
            )
        }

        fallbackRecent.isEmpty() && allWordSets.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                EmptyContentState(message = "Chưa có học phần nào cả")
            }
        }

        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (fallbackRecent.isNotEmpty()) {
                    item {
                        SectionTitle(text = "Gần đây")
                    }
                    items(fallbackRecent, key = { "recent_${it.id}" }) { wordSet ->
                        WordSetCard(wordSet = wordSet)
                    }
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                if (allWordSets.isNotEmpty()) {
                    item {
                        SectionTitle(text = "Tất cả")
                    }
                    items(allWordSets, key = { "all_${it.id}" }) { wordSet ->
                        WordSetCard(wordSet = wordSet)
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color.Black
    )
}

@Composable
private fun WordSetCard(wordSet: WordSet) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .background(Color(0xFF89E219), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_folder),
                contentDescription = "Word set icon",
                modifier = Modifier.size(24.dp),
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Column {
            Text(
                text = wordSet.name.ifBlank { "Không có tên" },
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = buildWordSetMeta(wordSet),
                fontSize = 14.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0x80000000)
            )
        }
    }
}

private fun buildWordSetMeta(wordSet: WordSet): String {
    val termLang = wordSet.termLanguageCode.uppercase()
    val defLang = wordSet.definitionLanguageCode.uppercase()
    val monthYearText = formatMonthYear(wordSet.createdAt)
    return if (monthYearText.isEmpty()) {
        "$termLang - $defLang"
    } else {
        "$termLang - $defLang - $monthYearText"
    }
}

private fun formatMonthYear(dateText: String): String {
    if (dateText.isBlank()) return ""
    return try {
        val parsedDate = when {
            dateText.contains("T") -> OffsetDateTime.parse(dateText).toLocalDate()
            else -> DateTimeFormatter.ISO_LOCAL_DATE.parse(dateText).let { java.time.LocalDate.from(it) }
        }
        "Tháng ${parsedDate.monthValue} năm ${parsedDate.year}"
    } catch (_: DateTimeParseException) {
        ""
    }
}

@Composable
private fun ErrorContentState(
    message: String,
    onRetry: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = message,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0x99000000)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Thử lại",
            modifier = Modifier.clickable(onClick = onRetry),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            color = Color.Black
        )
    }
}

@Composable
private fun EmptyContentState(message: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_library_sleeping_potago),
            contentDescription = "Sleeping potato",
            modifier = Modifier
                .size(120.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = message,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0x80000000)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LibraryScreenPreview() {
    LibraryScreen(navController = NavController(LocalContext.current))
}
