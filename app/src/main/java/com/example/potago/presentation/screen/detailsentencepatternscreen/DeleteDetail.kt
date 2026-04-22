package com.example.potago.presentation.screen.detailsentencepatternscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.potago.R
import com.example.potago.presentation.navigation.Screen

@Composable
fun DeleteDetailScreen(
    navController: NavController,
    patternId: Int,
    viewModel: DetailSentencePatternViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Load pattern if not already loaded
    LaunchedEffect(patternId) {
        if (uiState.pattern == null || uiState.pattern?.id != patternId) {
            viewModel.loadDetail(patternId)
        }
    }

    // Navigate to Library on delete success
    LaunchedEffect(uiState.deleteSuccess) {
        if (uiState.deleteSuccess) {
            navController.navigate(Screen.Library.route) {
                popUpTo(Screen.Library.route) { inclusive = true }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Dimmed background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x66000000))
                .clickable(enabled = !uiState.isDeleting) { navController.popBackStack() }
        )

        // Bottom sheet
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(
                    color = Color(0xFFFFFFFF),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
                .padding(top = 12.dp)
                .align(Alignment.BottomCenter)
        ) {
            // Drag handle
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 28.dp)
                    .clip(shape = RoundedCornerShape(9999.dp))
                    .width(47.dp)
                    .height(5.dp)
                    .background(
                        color = Color(0xFFE5E7EB),
                        shape = RoundedCornerShape(9999.dp)
                    )
            )

            // Mascot + speech bubble
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 32.dp, start = 24.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.mascot_xoacau),
                    contentDescription = "Mascot",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.height(76.dp)
                )

                Column(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = Color(0xFFE5E7EB),
                            shape = RoundedCornerShape(topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
                        )
                        .clip(shape = RoundedCornerShape(topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
                        .background(
                            color = Color(0xFFFFFFFF),
                            shape = RoundedCornerShape(topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Text(
                        "Xác nhận xóa chứ!?",
                        color = Color(0xFF000000),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Error message
            if (uiState.actionError != null) {
                Text(
                    text = uiState.actionError ?: "",
                    color = Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 21.dp, vertical = 4.dp)
                )
            }

            // Buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(bottom = 34.dp, start = 21.dp, end = 21.dp)
                    .fillMaxWidth()
            ) {
                // Từ chối
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .clip(shape = RoundedCornerShape(16.dp))
                        .weight(1f)
                        .background(
                            color = Color(0xFFFFFFFF),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = Color(0xFFE5E7EB),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable(enabled = !uiState.isDeleting) { navController.popBackStack() }
                        .padding(vertical = 18.dp)
                ) {
                    Text(
                        "Từ chối",
                        color = Color(0xFF374151),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Xác nhận
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(16.dp))
                        .weight(1f)
                        .background(
                            color = if (uiState.isDeleting) Color(0xFFAAAAAA) else Color(0xFF58CC02),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable(enabled = !uiState.isDeleting) {
                            viewModel.deletePattern()
                        }
                        .padding(vertical = 18.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    if (uiState.isDeleting) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Xác nhận",
                            color = Color(0xFFFFFFFF),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
