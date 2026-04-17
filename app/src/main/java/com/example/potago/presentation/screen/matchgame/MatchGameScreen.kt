package com.example.potago.presentation.screen.matchgame

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.potago.R
import com.example.potago.presentation.navigation.Screen

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
                title = wordSetName,
                elapsedSeconds = uiState.elapsedSeconds,
                onBack = viewModel::showExitDialog
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
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp, vertical = 12.dp),
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

                        // Mascot + bubble
                        MascotBubble(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchGameTopBar(
    title: String,
    elapsedSeconds: Double,
    onBack: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 4.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = "Back"
                )
            }
            Text(
                text = "Ghép thẻ",
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = String.format("%.1f s", elapsedSeconds),
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF58CC02),
                fontWeight = FontWeight.Bold
            )
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
            .aspectRatio(0.85f)
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
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
private fun MascotBubble(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.normal_mascot),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(8.dp))
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
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_looking_mascot),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 4.dp),
                    shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp, bottomStart = 12.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    shadowElevation = 2.dp
                ) {
                    Text(
                        text = "Thoát bài học giữa chừng sẽ không có điểm. Xác nhận thoát chứ!?",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Từ chối", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF58CC02))
                ) {
                    Text("Xác nhận", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
