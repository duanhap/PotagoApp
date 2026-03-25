package com.example.potago.presentation.screen.flashcardscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.potago.R

@Composable
fun FlashCardScreen(
    navController: NavController,
    wordSetName: String
) {
    FlashCardScreenContent(
        title = if (wordSetName.isBlank()) "Học phần" else wordSetName,
        onBackClick = { navController.popBackStack() }
    )
}

@Composable
private fun FlashCardScreenContent(
    title: String,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F4F6))
    ) {
        FlashCardTopBar(
            title = title,
            onBackClick = onBackClick
        )
        FlashCardProgress()
        FlashCardPanel()
        Spacer(modifier = Modifier.weight(1f))
        FlashCardBottomActions()
        Spacer(modifier = Modifier.height(76.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(59.dp)
                .background(
                    color = Color(0xFF3B82F6),
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "^",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun FlashCardTopBar(
    title: String,
    onBackClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(59.dp)
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
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 32.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun FlashCardProgress() {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 22.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Thẻ",
                fontSize = 12.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0x80000000)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "45/200",
                fontSize = 12.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0x80000000)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Color(0xFFE5E7EB), RoundedCornerShape(999.dp))
        ) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(8.dp)
                    .background(Color(0xFF58CC02), RoundedCornerShape(999.dp))
            )
        }
    }
}

@Composable
private fun FlashCardPanel() {
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(384.dp)
            .background(Color(0xFFE5E7EB), RoundedCornerShape(24.dp))
            .padding(bottom = 6.dp)
            .background(Color.White, RoundedCornerShape(24.dp))
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(24.dp))
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_flashcard_speaker),
            contentDescription = "Sound",
            tint = Color(0xFF58CC02),
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.TopEnd)
                .padding(top = 14.dp, end = 14.dp)
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "DỊCH TỪ NÀY",
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9CA3AF)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "El Gato",
                fontSize = 40.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
            Spacer(modifier = Modifier.height(120.dp))
            Text(
                text = "Tap to flip",
                fontSize = 12.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF9CA3AF)
            )
        }
    }
}

@Composable
private fun FlashCardBottomActions() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_flashcard_back_button),
            contentDescription = "Back action",
            tint = Color.Unspecified,
            modifier = Modifier.width(58.dp)
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_flashcard_x_button),
            contentDescription = "Wrong action",
            tint = Color.Unspecified,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_flashcard_v_button),
            contentDescription = "Correct action",
            tint = Color.Unspecified,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_flashcard_shuffle_button),
            contentDescription = "Shuffle action",
            tint = Color.Unspecified,
            modifier = Modifier.width(58.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FlashCardScreenPreview() {
    FlashCardScreenContent(
        title = "Học phần",
        onBackClick = {}
    )
}
