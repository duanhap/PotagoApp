package com.example.potago.presentation.screen.detailedvideoscreen

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.OptIn
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.example.potago.R
import com.example.potago.domain.model.Subtitle
import com.example.potago.domain.model.Video
import com.example.potago.presentation.screen.UiState
import kotlinx.coroutines.delay
import java.util.regex.Pattern

@Composable
fun DetailedVideoScreen(
    navController: NavController,
    viewModel: DetailedVideoViewModel = hiltViewModel()
) {
    val subtitlesState by viewModel.subtitlesState.collectAsState()
    val videoState by viewModel.videoState.collectAsState()
    val selectedTabIndex by viewModel.selectedTabIndex.collectAsState()
    val currentTimeMs by viewModel.currentTimeMs.collectAsState()
    val currentSubtitleIndex by viewModel.currentSubtitleIndex.collectAsState()
    
    // Hiển thị Mic bar khi ở tab "Xem từng câu" (index 1)
    val showMicBottomSheet = selectedTabIndex == 1

    var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    // Đồng bộ index câu với thời gian video
    LaunchedEffect(currentTimeMs) {
        if (subtitlesState is UiState.Success) {
            val subs = (subtitlesState as UiState.Success<List<Subtitle>>).data ?: emptyList()
            val index = subs.indexOfLast { (it.startTime?.toLong() ?: 0L) <= currentTimeMs }
            val finalIndex = if (index == -1) 0 else index
            if (finalIndex != currentSubtitleIndex) {
                viewModel.jumpToSubtitle(finalIndex)
            }
        }
    }

    val onSeek: (Int) -> Unit = { startTimeMs ->
        if (exoPlayer != null) {
            exoPlayer?.seekTo(startTimeMs.toLong())
        } else {
            webViewInstance?.evaluateJavascript("seekTo(${startTimeMs / 1000f});", null)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding))

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                Spacer(modifier = Modifier.height(73.dp))

                // Video Player Section
                when (videoState) {
                    is UiState.Loading -> {
                        Box(modifier = Modifier.fillMaxWidth().aspectRatio(16/9f).background(Color.Black), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                    is UiState.Success -> {
                        val video = (videoState as UiState.Success<Video>).data
                        if (!video?.serverSourceUrl.isNullOrBlank()) {
                            ExoPlayerView(
                                videoUrl = video.serverSourceUrl!!,
                                onPlayerReady = { exoPlayer = it },
                                onTimeUpdate = { viewModel.updateCurrentTime(it) }
                            )
                        } else {
                            val videoId = video?.let { extractYoutubeVideoId(it.sourceUrl) }
                            YoutubeWebView(
                                videoId = videoId ?: "",
                                onPlayerReady = { webViewInstance = it },
                                onTimeUpdate = { viewModel.updateCurrentTime(it) }
                            )
                        }
                    }
                    is UiState.Error -> {
                        Box(modifier = Modifier.fillMaxWidth().aspectRatio(16/9f).background(Color.Black), contentAlignment = Alignment.Center) {
                            Text(text = "Lỗi tải video", color = Color.White)
                        }
                    }
                    else ->{}
                }

                TabRowSection(selectedTabIndex = selectedTabIndex, onTabSelected = { viewModel.onTabSelected(it) })

                when (subtitlesState) {
                    is UiState.Loading -> { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
                    is UiState.Success -> {
                        val subtitles = (subtitlesState as UiState.Success<List<Subtitle>>).data ?: emptyList()
                        if (selectedTabIndex == 0) {
                            SubtitleList(
                                subtitles = subtitles,
                                currentTimeMs = currentTimeMs,
                                onSubtitleClick = { onSeek(it) }
                            )
                        } else {
                            SingleSubtitleView(
                                subtitles = subtitles,
                                currentIndex = currentSubtitleIndex
                            )
                        }
                    }
                    is UiState.Error -> { Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(text = (subtitlesState as UiState.Error).message, color = Color.Red) } }
                    else -> {}
                }
            }

            // Thanh Mic Bar ở dưới cùng
            if (showMicBottomSheet && subtitlesState is UiState.Success) {
                val subtitles = (subtitlesState as UiState.Success<List<Subtitle>>).data ?: emptyList()
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .offset(y= 15.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        tonalElevation = 3.dp,
                        color = Color.Black
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    if (currentSubtitleIndex > 0) {
                                        val prevIndex = currentSubtitleIndex - 1
                                        viewModel.prevSubtitle()
                                        subtitles.getOrNull(prevIndex)?.startTime?.let { onSeek(it) }
                                    }
                                },
                                enabled = currentSubtitleIndex > 0
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_back),
                                    contentDescription = "Previous",
                                    modifier = Modifier.size(32.dp),
                                    tint = if (currentSubtitleIndex > 0) Color(0xFF89E219) else Color.LightGray
                                )
                            }

                            // Placeholder để giữ khoảng trống cho mic
                            Spacer(modifier = Modifier.width(40.dp))

                            // Next
                            // Nút Tới câu tiếp theo
                            IconButton(
                                onClick = {
                                    if (currentSubtitleIndex < subtitles.size - 1) {
                                        val nextIndex = currentSubtitleIndex + 1
                                        viewModel.nextSubtitle(subtitles.size)
                                        subtitles.getOrNull(nextIndex)?.startTime?.let { onSeek(it) }
                                    }
                                },
                                enabled = currentSubtitleIndex < subtitles.size - 1
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_back),
                                    contentDescription = "Next",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .graphicsLayer { rotationY = 180f },
                                    tint = if (currentSubtitleIndex < subtitles.size - 1) Color(0xFF89E219) else Color.LightGray
                                )
                            }
                        }
                    }

                    // Mic nhô lên, nằm trên Surface
                    Image(
                        painter = painterResource(id = R.drawable.ic_micro_green),
                        contentDescription = "Mic",
                        modifier = Modifier
                            .size(80.dp)
                            .offset(y = -25.dp)
                            .align(Alignment.BottomCenter)
                            .clickable { /* TODO */ }
                    )
                }
            }
        }
    }
}

@Composable
fun SingleSubtitleView(
    subtitles: List<Subtitle>,
    currentIndex: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hàng nút chức năng bên trên
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            UploadButtonLike(iconRes = R.drawable.ic_question) { /* TODO */ }
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
                    .border(1.2.dp, Color(0xFFE5E5E5), RoundedCornerShape(16.dp))
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Số câu đã luyện nói đúng : 0/3",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontSize = 11.sp)
                    )
                    Text(
                        text = "Số câu đã luyện viết đúng : 0/3",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontSize = 11.sp)
                    )
                }
            }

            UploadButtonLike(iconRes = R.drawable.ic_random) { /* TODO */ }
        }

        Spacer(modifier = Modifier.height(15.dp))

        // Card hiển thị câu với Animation
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = currentIndex,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut())
                    } else {
                        (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> width } + fadeOut())
                    }.using(SizeTransform(clip = false))
                },
                label = "SubtitleSlideAnimation"
            ) { targetIndex ->
                    val scrollState = rememberScrollState()
                    val subtitle = subtitles.getOrNull(targetIndex)
                    if (subtitle != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                                .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(24.dp))
                                .padding(20.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize()
                                    .verticalScroll(scrollState),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFFF3F4F6)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                                    Text(text = formatTime(subtitle.startTime ?: 0), style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray))
                                }
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_save_detailed_video_screen),
                                    contentDescription = "Save",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFF3F4F6)).padding(8.dp).clickable { /* TODO */ }
                                )
                            }

                            Spacer(modifier = Modifier.height(15.dp))

                            Text(
                                text = subtitle.content ?: "",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp, textAlign = TextAlign.Center),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            if (!subtitle.pronunciation.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = subtitle.pronunciation,
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray,  fontSize = 14.sp,textAlign = TextAlign.Center),
                                )
                            }

                            Spacer(modifier = Modifier.height(15.dp))

                            Text(
                                text = subtitle.translation ?: "",
                                style = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center, fontSize = 15.sp),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )


                        }
                    }
                }
            }
        }
        // Đã xóa hàng điều hướng cũ ở đây vì đã đưa ra ngoài Scaffold
    }
}

@Composable
fun UploadButtonLike(
    iconRes: Int,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(targetValue = if (isPressed) 0.96f else 1f, label = "")
    val animatedHeight by animateDpAsState(targetValue = if (isPressed) 52.dp else 48.dp, label = "")

    Box(
        modifier = Modifier
            .width(56.dp)
            .height(52.dp)
            .graphicsLayer { scaleX = animatedScale; scaleY = animatedScale }
            .border(1.2.dp, Color(0xFFE5E5E5), RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(color = Color(0xFFE5E5E5))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(animatedHeight)
                .clip(RoundedCornerShape(16.dp))
                .background(color = Color(0xFFFFFFFF))
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { isPressed = true; tryAwaitRelease(); isPressed = false },
                        onTap = { onClick() }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(painter = painterResource(id = iconRes), contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(24.dp))
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YoutubeWebView(
    videoId: String,
    onPlayerReady: (WebView) -> Unit,
    onTimeUpdate: (Long) -> Unit
) {
    val packageName = LocalContext.current.packageName
    val embedHtml = """
        <!DOCTYPE html>
        <html>
        <head>
            <style>body { margin: 0; padding: 0; background: black; } .container { position: relative; padding-bottom: 56.25%; height: 0; overflow: hidden; } iframe { position: absolute; top: 0; left: 0; width: 100%; height: 100%; border: 0; }</style>
        </head>
        <body>
            <div class="container">
                <div id="player"></div>
            </div>
            <script>
                var tag = document.createElement('script');
                tag.src = "https://www.youtube.com/iframe_api";
                var firstScriptTag = document.getElementsByTagName('script')[0];
                firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

                var player;
                function onYouTubeIframeAPIReady() {
                    player = new YT.Player('player', {
                        height: '100%', width: '100%',
                        videoId: '$videoId',
                        playerVars: { 'autoplay': 1, 'rel': 0, 'controls': 1, 'origin': 'https://$packageName' },
                        events: { 'onReady': onPlayerReady }
                    });
                }

                function onPlayerReady(event) {
                    setInterval(function() {
                        if (player && player.getCurrentTime) {
                            Android.onTimeUpdate(player.getCurrentTime());
                        }
                    }, 200);
                }

                function seekTo(seconds) {
                    if (player) player.seekTo(seconds, true);
                }
            </script>
        </body>
        </html>
    """.trimIndent()

    AndroidView(
        modifier = Modifier.fillMaxWidth().aspectRatio(16 / 9f),
        factory = { context ->
            WebView(context).apply {
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    mediaPlaybackRequiresUserGesture = false
                }
                addJavascriptInterface(object {
                    @JavascriptInterface
                    fun onTimeUpdate(seconds: Float) {
                        onTimeUpdate((seconds * 1000).toLong())
                    }
                }, "Android")
                webViewClient = WebViewClient()
                webChromeClient = WebChromeClient()
                loadDataWithBaseURL("https://$packageName", embedHtml, "text/html", "UTF-8", null)
                onPlayerReady(this)
            }
        }
    )
}

@OptIn(UnstableApi::class)
@Composable
fun ExoPlayerView(videoUrl: String, onPlayerReady: (ExoPlayer) -> Unit, onTimeUpdate: (Long) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val player = remember { ExoPlayer.Builder(context).build().apply { setMediaItem(MediaItem.fromUri(videoUrl)); prepare(); onPlayerReady(this) } }

    LaunchedEffect(player) {
        while (true) { if (player.isPlaying) onTimeUpdate(player.currentPosition); delay(200) }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> player.pause()
                Lifecycle.Event.ON_RESUME -> player.play()
                Lifecycle.Event.ON_DESTROY -> player.release()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer); player.release() }
    }

    AndroidView(modifier = Modifier.fillMaxWidth().aspectRatio(16/9f), factory = { PlayerView(context).apply { this.player = player; useController = true } })
}

fun extractYoutubeVideoId(url: String): String? {
    val patterns = listOf("(?:v=|/v/|/embed/|/shorts/|youtu.be/|/videos/|embed\\?v=)([^#&?\\s]+)", "youtu.be/([^#&?\\s]+)", "youtube.com/watch\\?v=([^#&?\\s]+)")
    for (p in patterns) {
        val matcher = Pattern.compile(p).matcher(url)
        if (matcher.find()) return matcher.group(1)
    }
    return null
}

@Composable
fun TabRowSection(selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)).border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))) {
        TabItem(text = "Xem danh sách", iconRes = R.drawable.ic_list_detailed_video_screen, isSelected = selectedTabIndex == 0, onClick = { onTabSelected(0) }, modifier = Modifier.weight(1f))
        TabItem(text = "Xem từng câu", iconRes = R.drawable.ic_card_detailed_video_screen, isSelected = selectedTabIndex == 1, onClick = { onTabSelected(1) }, modifier = Modifier.weight(1f))
    }
}

@Composable
fun TabItem(text: String, iconRes: Int, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.background(if (isSelected) Color.White else Color(0xFFF3F4F6)).clickable { onClick() }.padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(painter = painterResource(id = iconRes), contentDescription = null, tint = if (isSelected) Color(0xFF3B82F6) else Color.Gray, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, style = MaterialTheme.typography.labelSmall.copy( color = if (isSelected) Color(0xFF3B82F6) else Color.Gray))
        }
    }
}

@Composable
fun SubtitleList(subtitles: List<Subtitle>, currentTimeMs: Long, onSubtitleClick: (Int) -> Unit) {
    val listState = rememberLazyListState()
    val activeIndex = remember(currentTimeMs, subtitles) { subtitles.indexOfLast { (it.startTime?.toLong() ?: 0L) <= currentTimeMs } }
    LaunchedEffect(activeIndex) { if (activeIndex != -1) listState.animateScrollToItem(activeIndex, scrollOffset = -200) }
    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
        itemsIndexed(subtitles) { index, subtitle -> SubtitleItem(subtitle = subtitle, isHighlighted = index == activeIndex, onClick = { onSubtitleClick(subtitle.startTime ?: 0) }) }
    }
}

@Composable
fun SubtitleItem(subtitle: Subtitle, isHighlighted: Boolean, onClick: () -> Unit) {
    val backgroundColor by animateColorAsState(targetValue = if (isHighlighted) Color(0xFFD6FFA3) else Color.White, label = "bgColor")
    Column(modifier = Modifier.fillMaxWidth().background(backgroundColor).clickable { onClick() }.drawBehind { val strokeWidth = 1.dp.toPx(); drawLine(color = Color(0xFFEEEEEE), start = Offset(0f, size.height - strokeWidth / 2), end = Offset(size.width, size.height - strokeWidth / 2), strokeWidth = strokeWidth) }.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFFF3F4F6)).padding(horizontal = 8.dp, vertical = 4.dp)) { Text(text = formatTime(subtitle.startTime ?: 0), style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)) }
            Icon(painter = painterResource(id = R.drawable.ic_save_detailed_video_screen), contentDescription = "Save", tint = Color.Gray, modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFF3F4F6)).padding(6.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = subtitle.content ?: "", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp))
        if (!subtitle.pronunciation.isNullOrBlank()) Text(text = subtitle.pronunciation, style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontSize = 14.sp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = subtitle.translation ?: "", style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp))
    }
}

fun formatTime(ms: Int): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

@Composable
private fun TopAppBar(onBackClick: () -> Unit = {}) {
    Surface(modifier = Modifier.fillMaxWidth(), tonalElevation = 3.dp, shadowElevation = 4.dp, color = Color(0xFFFFFFFF)) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            BackButton(onBackClick)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Chi tiết video", style = MaterialTheme.typography.displayMedium)
        }
    }
}

@Composable
private fun BackButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.85f else 1f, label = "icon_scale")
    IconButton(onClick = onClick, interactionSource = interactionSource) { Icon(painter = painterResource(id = R.drawable.ic_back), contentDescription = "Back", modifier = Modifier.scale(scale)) }
}
