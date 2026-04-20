package com.example.potago.presentation.screen.matchgame

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.potago.R
import com.example.potago.presentation.navigation.Screen
import com.example.potago.presentation.screen.setting.BackButton
import com.example.potago.presentation.ui.theme.Nunito

@Composable
fun MatchGameScreen(
    navController: NavController,
    wordSetId: Long,
    wordSetName: String,
    viewModel: MatchGameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(wordSetId) {
        viewModel.startGame(wordSetId)
    }

    // Navigate to result when finished
    LaunchedEffect(uiState.isFinished) {
        if (uiState.isFinished) {
            navController.navigate(
                Screen.MatchResult(
                    completedTime = uiState.completedTime,
                    bestTime = uiState.bestTime?.bestTime ?: 0.0,
                    bestDate = uiState.bestTime?.date ?: "",
                    wordSetId = wordSetId,
                    wordSetName = wordSetName
                )
            ) {
                popUpTo(Screen.MatchGame.route) { inclusive = true }
            }
        }
    }

    if (uiState.showExitDialog) {
        ExitConfirmDialog(
            onDismiss = viewModel::dismissExitDialog,
            onConfirm = { navController.popBackStack() }
        )
    }

    Scaffold(
        topBar = {
            MatchGameTopBar(
                onBackClick = viewModel::showExitDialog
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF58CC02)
                )
                uiState.error != null -> Text(
                    text = uiState.error!!,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Spacer(modifier = Modifier.weight(1f))
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            modifier = Modifier
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(uiState.cards, key = { it.cardId }) { card ->
                                MatchCardItem(
                                    card = card,
                                    onClick = { viewModel.onCardTap(card) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        // Mascot + bubble
                        MascotBubble(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            elapsedSeconds = uiState.elapsedSeconds,
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun MatchGameTopBar(
    onBackClick: () -> Unit = {},
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
                    text = "Ghép thẻ",
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
@Composable
private fun MatchCardItem(
    card: MatchCard,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = when (card.state) {
            CardState.SELECTED        -> Color(0xFFBFDBFE)
            CardState.MATCHED_VISIBLE -> Color(0xFFD7FFA4)
            CardState.MATCHED         -> Color.Transparent
            CardState.WRONG           -> Color(0xFFFECACA)
            CardState.IDLE            -> Color.White
        },
        animationSpec = tween(300),
        label = "card_bg"
    )
    val borderColor = when (card.state) {
        CardState.SELECTED        -> Color(0xFF3B82F6)
        CardState.MATCHED_VISIBLE -> Color(0xFF89E219)
        CardState.MATCHED         -> Color.Transparent
        CardState.WRONG           -> Color(0xFFEF4444)
        CardState.IDLE            -> Color(0xFFE5E7EB)
    }
    val isVisible = card.state != CardState.MATCHED

    Box(
        modifier = Modifier
            .aspectRatio(0.90f)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isVisible) bgColor else Color.Transparent)
            .then(
                if (isVisible) Modifier.border(2.dp, borderColor, RoundedCornerShape(16.dp))
                else Modifier
            )
            .clickable(
                enabled = isVisible && card.state == CardState.IDLE || card.state == CardState.SELECTED,
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isVisible) {
            Text(
                text = card.content,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
private fun MascotBubble(modifier: Modifier = Modifier, elapsedSeconds: Double) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_teaching_mascot),
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ){
            Surface(
                shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp, bottomStart = 12.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
                shadowElevation = 2.dp
            ) {
                Text(
                    text = "Phá kỷ lục lần trước nèo (*/ω＼*)",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                )

            }
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = String.format("%.1f s", elapsedSeconds),
                style = MaterialTheme.typography.headlineLarge,
                color = Color(0xFF4B5563),
                fontWeight = FontWeight.Bold
            )

        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExitConfirmDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
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
                    .padding(horizontal = 5.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_crying_mascot),
                    contentDescription = null,
                    modifier = Modifier
                        .scale(0.7f)
                )
                Surface(
                    modifier = Modifier
                        .padding(top = 30.dp),
                    shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    shadowElevation = 2.dp
                ) {
                    Text(
                        text = "Thoát bài học giữa chừng sẽ không có điểm. Xác nhận thoát chứ!?",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4B5563),
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(horizontal = 13.dp, vertical = 14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

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
                                    onPress = { confirmPressed = true; tryAwaitRelease(); confirmPressed = false },
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
@Preview(showBackground = true)
@Composable
fun MascotBubblePreview() {
    MascotBubble(elapsedSeconds = 10.0)
}