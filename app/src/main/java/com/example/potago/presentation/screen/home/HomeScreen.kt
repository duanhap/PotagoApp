package com.example.potago.presentation.screen.home

import android.speech.tts.TextToSpeech
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.potago.R
import com.example.potago.domain.model.Setence
import com.example.potago.domain.model.WordSet
import com.example.potago.presentation.navigation.Screen
import com.example.potago.presentation.screen.UiState
import com.example.potago.presentation.screen.video.VideoListHorizontal
import com.example.potago.presentation.screen.video.VideoViewModel
import com.example.potago.presentation.ui.component.ShimmerItem
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun HomeScreen(
    navController: NavController,
    videoViewModel: VideoViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val videoUiState by videoViewModel.uiState.collectAsState()
    val homeUiState by homeViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar()
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding))
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            item {
                Spacer(modifier = Modifier.height(80.dp))
                MascotAndBubbleHome(modifier = Modifier.padding(horizontal = 20.dp))
                Spacer(modifier = Modifier.height(5.dp))
            }

            // --- Section Học tiếp ---
            item {
                SectionHeader(
                    title = "Học tiếp",
                    onSeeMoreClick = {
                        navController.navigate(Screen.Library.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                RecentWordSetsSection(uiState = homeUiState.recentWordSets, onContinueClick = {navController.navigate(Screen.FlashCard(it.id, it.name))})
                Spacer(modifier = Modifier.height(8.dp))
            }

            // --- Section Câu gần đây ---
            item {
                SectionHeader(
                    title = "Câu gần đây",
                    onSeeMoreClick = {},
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                RecentSentencesSection(uiState = homeUiState.recentSentences)
                Spacer(modifier = Modifier.height(20.dp))
            }

            // --- Section Video gần đây ---
            item {
                SectionHeader(
                    title = "Video gần đây",
                    onSeeMoreClick = {
                        navController.navigate(Screen.Video.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    VideoListHorizontal(
                        uiState = videoUiState.recentVideos,
                        onVideoClick = { videoId ->
                            navController.navigate(Screen.DetailedVideo(videoId))
                        }
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // --- Section Tính năng ---
            item {
                SectionHeader(
                    title = "Tính năng",
                    onSeeMoreClick = {},
                    showSeeMore = false,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FeatureBox(
                        iconRes = R.drawable.ic_shop,
                        title = "Cửa hàng",
                        iconBgColor = Color(0xFFF3E8FF),
                        iconTint = Color(0xFF9333EA),
                        modifier = Modifier.weight(1f),
                        onItemClick = {}
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    FeatureBox(
                        iconRes = R.drawable.ic_add,
                        title = "Thêm thư mục",
                        iconBgColor = Color(0xFFFFEDD5),
                        iconTint = Color(0xFFF97316),
                        modifier = Modifier.weight(1f),
                        onItemClick = {}
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    FeatureBox(
                        iconRes = R.drawable.ic_rank,
                        title = "Xếp hạng",
                        iconBgColor = Color(0xFFFEF9C3),
                        iconTint = Color(0xFFEAB308),
                        modifier = Modifier.weight(1f),
                        onItemClick = {}
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

// ─── Học tiếp Section ─────────────────────────────────────────────────────────

@Composable
fun RecentWordSetsSection(
    uiState: UiState<List<WordSet>>,
    onContinueClick: (WordSet) -> Unit
) {
    when (uiState) {
        is UiState.Loading -> {
            WordSetCardShimmer()
        }
        is UiState.Success -> {
            val wordSets = uiState.data
            if (wordSets.isNullOrEmpty()) {
                EmptyBoxView(
                    text = "Chưa có học phần nào",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            } else {
                WordSetPager(wordSets = wordSets, onContinueClick = onContinueClick)
            }
        }
        is UiState.Error -> {
            Text(
                text = uiState.message,
                color = Color.Red,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }
        else -> {}
    }
}

@Composable
private fun WordSetPager(
    wordSets: List<WordSet>,
    onContinueClick: (WordSet) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { wordSets.size })

    // Auto-scroll every 4 seconds
    LaunchedEffect(pagerState.pageCount) {
        while (true) {
            delay(4000)
            if (pagerState.pageCount > 0) {
                val next = (pagerState.currentPage + 1) % pagerState.pageCount
                pagerState.animateScrollToPage(next)
            }
        }
    }

    Column {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp),
            pageSpacing = 12.dp
        ) { page ->
            WordSetCard(
                wordSet = wordSets[page],
                onContinueClick = { onContinueClick(wordSets[page]) }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Dot indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(wordSets.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (isSelected) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) Color(0xFF58CC02) else Color(0xFFD1FAE5)
                        )
                )
            }
        }
    }
}

@Composable
private fun WordSetCard(
    wordSet: WordSet,
    onContinueClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF5EDC00), Color(0xFF4BB000)),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 0f) // ngang
                )
            )
            .padding(vertical = 24.dp)
    ) {
        Column {
            // Term count + Title
            Column(
                modifier = Modifier
                    .padding(start = 24.dp, end = 26.dp)
                    .padding(bottom = 17.dp)
            ) {
                Text(
                    text = if ((wordSet.amountOfWords ?: 0) > 0)
                        "${wordSet.amountOfWords} thuật ngữ"
                    else
                        "Học phần",
                    color = Color(0xFFDCFCE7),
                    style = MaterialTheme.typography.labelSmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = wordSet.name,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
            }


            // Description
            if (!wordSet.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(13.dp))
                Text(
                    text = wordSet.description?:"Không có mô tả nào...",
                    color = Color(0xFFF0FDF4),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(start = 24.dp, end = 24.dp)
                        .padding(bottom = 24.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Continue button
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .clickable { onContinueClick() }
                        .padding(vertical = 13.dp, horizontal = 100.dp)
                ) {
                    Text(
                        text = "Tiếp tục",
                        color = Color(0xFF58CC02),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

// ─── Câu gần đây Section ──────────────────────────────────────────────────────

@Composable
fun RecentSentencesSection(
    uiState: UiState<List<Setence>>
) {
    when (uiState) {
        is UiState.Loading -> {
            SentenceCardShimmerList()
        }
        is UiState.Success -> {
            val sentences = uiState.data
            if (sentences.isNullOrEmpty()) {
                EmptyBoxView(
                    text = "Chưa có câu nào",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            } else {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(sentences) { sentence ->
                        SentenceCard(sentence = sentence)
                    }
                }
            }
        }
        is UiState.Error -> {
            Text(
                text = uiState.message,
                color = Color.Red,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }
        else -> {}
    }
}

@Composable
private fun SentenceCard(sentence: Setence) {
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

    Box(
        modifier = Modifier
            .wrapContentWidth()
            .widthIn(max = 300.dp)
            .wrapContentHeight()
            .heightIn(max = 150.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.wrapContentSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = sentence.term,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF1F2937),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row() {
                    Text(
                        text = sentence.definition,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF9CA3AF),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        SoundButton(
                            onClick = {
                                tts?.let {
                                    it.language = Locale.forLanguageTag(sentence.termLanguageCode)
                                    it.speak(sentence.term, TextToSpeech.QUEUE_FLUSH, null, null)
                                }
                            }
                        )
                    }

                }

            }


        }
    }
}
@Preview
@Composable
fun SentenceCardShow(){
    SentenceCard(sentence = Setence(
        id = 1,
        term = "Hello",
        definition = "Xin chào",
        termLanguageCode = "en"
    ))
}

@Composable
fun SoundButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .size(44.dp)
            .clip(CircleShape)
            .background(Color(0xFFEFF6FF))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_sound_home_screen),
            contentDescription = "Play sound",
            modifier = Modifier.size(24.dp),
            tint = Color(0xFF3B82F6)
        )
    }
}

@Composable
private fun SentenceCardShimmerList() {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        userScrollEnabled = false
    ) {
        items(3) {
            Box(
                modifier = Modifier
                    .width(260.dp)
                    .height(160.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFF9FAFB))
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        ShimmerItem(
                            modifier = Modifier.fillMaxWidth(0.8f).height(20.dp),
                            shape = RoundedCornerShape(4.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        ShimmerItem(
                            modifier = Modifier.fillMaxWidth(0.6f).height(16.dp),
                            shape = RoundedCornerShape(4.dp)
                        )
                    }
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        ShimmerItem(
                            modifier = Modifier.size(44.dp),
                            shape = CircleShape
                        )
                    }
                }
            }
        }
    }
}

// ─── Shimmer for WordSet card ──────────────────────────────────────────────────

@Composable
private fun WordSetCardShimmer() {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        // simulate the green card shape
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF0FDF4), RoundedCornerShape(24.dp))
                    .padding(vertical = 24.dp, horizontal = 24.dp)
            ) {
                ShimmerItem(
                    modifier = Modifier
                        .width(100.dp)
                        .height(14.dp),
                    shape = RoundedCornerShape(4.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                ShimmerItem(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(22.dp),
                    shape = RoundedCornerShape(6.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
                ShimmerItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp),
                    shape = RoundedCornerShape(4.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                ShimmerItem(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(16.dp),
                    shape = RoundedCornerShape(4.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    ShimmerItem(
                        modifier = Modifier
                            .width(160.dp)
                            .height(48.dp),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Dot indicator shimmer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(3) { index ->
                ShimmerItem(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (index == 0) 10.dp else 8.dp),
                    shape = CircleShape
                )
            }
        }
    }
}

// ─── Shared UI components ─────────────────────────────────────────────────────

@Composable
private fun TopAppBar() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 3.dp,
        shadowElevation = 4.dp,
        color = Color(0xFFFFFFFF)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ){
                Image(
                    painter = painterResource(id = R.drawable.ic_flag_vietnam),
                    contentDescription = "Language",
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = "Home",
                    style = MaterialTheme.typography.displayMedium,
                    color = Color(0x00FFFFFF),
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_water_on),
                    contentDescription = "Streak",
                    modifier = Modifier.size(24.dp).padding(end = 5.dp)
                )
                Text(
                    text = "12",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF0099FF),
                    modifier = Modifier.padding(end = 15.dp)
                )
                Surface(
                    shape = RoundedCornerShape(15.dp),
                    border = BorderStroke(1.dp, Color(0xFFFEF08A)),
                    color = Color(0xFFFEF9C3)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_experience_points),
                            contentDescription = "Experience points",
                            modifier = Modifier.size(32.dp).padding(end = 8.dp)
                        )
                        Text(
                            text = "2450",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontSize = 15.sp
                            ),
                            color = Color(0xFFA16207),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "XP",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontSize = 15.sp
                            ),
                            color = Color(0xFFA16207),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MascotAndBubbleHome(modifier: Modifier = Modifier) {
    var start by remember { mutableStateOf(false) }
    var showBubble by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        start = true
        delay(500)
        showBubble = true
    }

    val mascotTransition = updateTransition(targetState = start, label = "mascot")
    val mascotScale by mascotTransition.animateFloat(
        transitionSpec = { spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow) },
        label = "scale"
    ) { if (it) 1f else 0.5f }
    val mascotAlpha by mascotTransition.animateFloat(label = "alpha") { if (it) 1f else 0f }

    val bubbleTransition = updateTransition(targetState = showBubble, label = "bubble")
    val bubbleScale by bubbleTransition.animateFloat(
        transitionSpec = { spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow) },
        label = "scale"
    ) { if (it) 1f else 0.4f }
    val bubbleAlpha by bubbleTransition.animateFloat(label = "alpha") { if (it) 1f else 0f }
    val bubbleOffsetY by bubbleTransition.animateFloat(label = "offset") { if (it) 0f else 50f }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .offset(x = (-20).dp),
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_thinking_mascot_home_screen),
            contentDescription = "Mascot",
            modifier = Modifier
                .size(130.dp)
                .graphicsLayer {
                    scaleX = mascotScale
                    scaleY = mascotScale
                    alpha = mascotAlpha
                }
        )
        Box(
            modifier = Modifier
                .padding(start = 10.dp, top = 10.dp)
                .graphicsLayer {
                    scaleX = bubbleScale
                    scaleY = bubbleScale
                    alpha = bubbleAlpha
                    translationY = bubbleOffsetY
                }
                .background(Color.White, RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp))
                .border(1.2.dp, Color(0xFFE5E7EB), RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .weight(1f)
        ) {
            Text(
                text = "Xin chào, chủ nhân! Tôi rất ưa tắm, hãy nhớ tắm rửa cho tôi mỗi ngày!",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF4B5563)
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    onSeeMoreClick: () -> Unit,
    showSeeMore: Boolean = true,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black
        )
        if (showSeeMore) {
            Text(
                text = "Xem thêm",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = Color(0xFF3B82F6),
                ),
                modifier = Modifier.clickable { onSeeMoreClick() }
            )
        }
    }
}

@Composable
private fun EmptyBoxView(text: String = "Trống rỗng...", modifier: Modifier = Modifier) {
    val images = listOf(
        R.drawable.empty_box,
        R.drawable.empty_box_1,
        R.drawable.empty_box_2,
        R.drawable.empty_box_3
    )
    val randomImage = remember { images.random() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = randomImage),
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.Gray,
            )
        )
    }
}

@Composable
fun FeatureBox(
    iconRes: Int,
    title: String,
    iconBgColor: Color,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFF3F4F6), RoundedCornerShape(16.dp))
            .clickable { onItemClick() }
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    modifier = Modifier.size(24.dp),
                    tint = iconTint
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF4B5563),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenShow() {
    HomeScreen(navController = NavController(LocalContext.current))
}
