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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
import java.time.format.DateTimeParseException

@Composable
fun LibraryScreen(
    navController: NavController,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val recentWordSetsState by viewModel.recentWordSets.collectAsState()
    val allWordSetsState by viewModel.allWordSets.collectAsState()

    val recentSentencePatternsState by viewModel.recentSentencePatterns.collectAsState()
    val allSentencePatternsState by viewModel.allSentencePatterns.collectAsState()

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.refreshLibrary()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Hoisted the UI content to a stateless Composable to allow Previews
    LibraryScreenContent(
        recentWordSetsState = recentWordSetsState,
        allWordSetsState = allWordSetsState,
        recentSentencePatternsState = recentSentencePatternsState,
        allSentencePatternsState = allSentencePatternsState,
        onWordSetClick = { wordSet ->
            navController.navigate(Screen.FlashCard(wordSet.id, wordSet.name))
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
                    // TODO: navigate to "create word set" screen if you already have one.
                },
                onChooseSentence = {
                    isAddOverlayVisible = false
                    // TODO: navigate to "create sentence pattern" screen if you already have one.
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
    // Full-screen dim layer
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x99000000))
            .clickable(onClick = onDismiss)
    )

    // Bottom sheet
    Box(
        modifier = Modifier
            .fillMaxSize(),
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
            // Drag handle
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color(0xFFE5E7EB), shape = RoundedCornerShape(999.dp))
            )

            Spacer(modifier = Modifier.height(12.dp))

            val courseSelected = selectedTab == LibraryTab.COURSE
            val sentenceSelected = selectedTab == LibraryTab.SENTENCE

            AddOverlayButton(
                selected = courseSelected,
                iconRes = R.drawable.ic_folder,
                text = "Học phần",
                onClick = onChooseCourse
            )
            Spacer(modifier = Modifier.height(12.dp))
            AddOverlayButton(
                selected = sentenceSelected,
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
    val selectedBg = Color(0xFFD7FFA4) // light green
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
    val recentWordSets = (recentState as? UiState.Success)?.data.orEmpty()
    val allWordSets = (allState as? UiState.Success)?.data.orEmpty()
    val fallbackRecent = if (recentWordSets.isEmpty()) allWordSets.take(3) else recentWordSets
    // Nếu ít nhất một phía (recent/all) có data thì ưu tiên render list, không show error.
    val errorMessage = when {
        fallbackRecent.isNotEmpty() -> null
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
                        WordSetCard(
                            wordSet = wordSet,
                            onClick = { onWordSetClick(wordSet) }
                        )
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
                        WordSetCard(
                            wordSet = wordSet,
                            onClick = { onWordSetClick(wordSet) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SentenceTabContent(
    recentState: UiState<List<SetencePattern>>,
    allState: UiState<List<SetencePattern>>,
    onRetry: () -> Unit
) {
    val isLoading = allState is UiState.Loading && recentState is UiState.Loading
    val recentSentencePatterns = (recentState as? UiState.Success)?.data.orEmpty()
    val allSentencePatterns = (allState as? UiState.Success)?.data.orEmpty()

    val fallbackRecent = if (recentSentencePatterns.isEmpty()) allSentencePatterns.take(3) else recentSentencePatterns
    // Nếu ít nhất một phía (recent/all) có data thì ưu tiên render list, không show error.
    val errorMessage = when {
        fallbackRecent.isNotEmpty() -> null
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

        fallbackRecent.isEmpty() && allSentencePatterns.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                EmptyContentState(message = "Chưa có mẫu câu nào cả")
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
                    items(fallbackRecent, key = { "recent_sentence_${it.id}" }) { pattern ->
                        SentencePatternCard(pattern = pattern)
                    }
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                if (allSentencePatterns.isNotEmpty()) {
                    item {
                        SectionTitle(text = "Tất cả")
                    }
                    items(allSentencePatterns, key = { "all_sentence_${it.id}" }) { pattern ->
                        SentencePatternCard(pattern = pattern)
                    }
                }
            }
        }
    }
}

@Composable
private fun SentencePatternCard(pattern: SetencePattern) {
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
                contentDescription = "Sentence icon",
                modifier = Modifier.size(24.dp),
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.width(20.dp))

        Column {
            Text(
                text = pattern.name.ifBlank { "Không có tên" },
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = buildSentencePatternMeta(pattern),
                fontSize = 14.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0x80000000)
            )
        }
    }
}

private fun buildSentencePatternMeta(pattern: SetencePattern): String {
    val termLang = pattern.termLanguageCode.uppercase()
    val defLang = pattern.definitionLanguageCode.uppercase()
    val monthYearText = formatMonthYear(pattern.createdAt)
    return if (monthYearText.isEmpty()) {
        "$termLang - $defLang"
    } else {
        "$termLang - $defLang - $monthYearText"
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
private fun WordSetCard(
    wordSet: WordSet,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
                lineHeight = 24.sp,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )
            Text(
                text = buildWordSetMeta(wordSet),
                lineHeight = 24.sp,
                style = MaterialTheme.typography.bodyMedium,
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
    // Used the stateless LibraryScreenContent for Preview to avoid hiltViewModel() crash
    LibraryScreenContent(
        recentWordSetsState = UiState.Success(emptyList()),
        allWordSetsState = UiState.Success(emptyList()),
        recentSentencePatternsState = UiState.Success(emptyList()),
        allSentencePatternsState = UiState.Success(emptyList()),
        onWordSetClick = {},
        onRetry = {}
    )
}
