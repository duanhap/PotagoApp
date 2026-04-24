package com.example.potago.presentation.screen.detailsentencepatternscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.potago.R

@Composable
fun EditSentenceScreen(
    navController: NavController,
    sentenceId: Int = 0,
    viewModel: EditSentenceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    val (sentenceValue, setSentenceValue) = remember { mutableStateOf("") }
    val (meaningValue, setMeaningValue) = remember { mutableStateOf("") }

    // Load sentence khi screen được tạo
    LaunchedEffect(sentenceId) {
        if (sentenceId > 0) {
            viewModel.loadSentence(sentenceId)
        }
    }

    // Update form khi sentence được load
    LaunchedEffect(uiState.sentence) {
        uiState.sentence?.let {
            setSentenceValue(it.term)
            setMeaningValue(it.definition)
        }
    }

    // Navigate back on success
    LaunchedEffect(uiState.updateSuccess) {
        if (uiState.updateSuccess) {
            navController.popBackStack()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(color = Color(0xFFFFFFFF))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(color = Color(0xFFFFFFFF))
                    .verticalScroll(rememberScrollState())
            ) {
                // Header: nút back + tiêu đề màn hình
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color(0xE3FFFFFF))
                        .shadow(elevation = 2.dp, spotColor = Color(0x1A000000))
                        .padding(vertical = 14.5.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back",
                        colorFilter = ColorFilter.tint(Color.Black),
                        modifier = Modifier
                            .padding(start = 21.dp)
                            .size(36.dp)
                            .clickable { navController.popBackStack() }
                    )
                    Text(
                        "Chỉnh sửa câu",
                        color = Color(0xFF000000),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Loading state
                if (uiState.isLoading && uiState.sentence == null) {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF58CC02))
                    }
                } else if (uiState.error != null) {
                    Text(
                        text = uiState.error ?: "",
                        color = Color.Red,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                } else {
                BasicTextField(
                    value = sentenceValue,
                    onValueChange = setSentenceValue,
                    textStyle = TextStyle(
                        color = Color(0xCC000000), // rgba(0,0,0,0.8)
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp
                    ),
                    modifier = Modifier
                        .padding(start = 19.dp, end = 19.dp)
                        .fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        Box {
                            if (sentenceValue.isEmpty()) {
                                Text(
                                    "Nhập câu tiếng Anh",
                                    color = Color(0xCC000000),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 24.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(18.dp))
                
                // Gạch chân câu
                Box(
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp)
                        .height(2.dp)
                        .fillMaxWidth()
                        .background(color = Color(0xFF000000))
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    "Câu",
                    color = Color(0xFF000000),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(start = 20.dp)
                )

                Spacer(modifier = Modifier.height(77.dp))

                // Trường nghĩa tiếng Việt
                BasicTextField(
                    value = meaningValue,
                    onValueChange = setMeaningValue,
                    textStyle = TextStyle(
                        color = Color(0xCC000000), // rgba(0,0,0,0.8)
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp
                    ),
                    modifier = Modifier
                        .padding(start = 19.dp, end = 19.dp)
                        .fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        Box {
                            if (meaningValue.isEmpty()) {
                                Text(
                                    "Nhập nghĩa tiếng Việt",
                                    color = Color(0xCC000000),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 24.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(17.dp))
                
                // Gạch chân nghĩa
                Box(
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp)
                        .height(2.dp)
                        .fillMaxWidth()
                        .background(color = Color(0xFF000000))
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    "Nghĩa",
                    color = Color(0xFF000000),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(start = 20.dp)
                )

                Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }

        // Bottom container với nút done
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(74.dp)
                .background(color = Color(0xFFFFFFFF))
                .shadow(
                    elevation = 30.dp, 
                    spotColor = Color(0x1A000000),
                    ambientColor = Color(0x1A000000),
                    clip = false
                )
        ) {
            // Background circle
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-10).dp)
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF58CC02))
            )
            
            // Nút done xanh ở giữa
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-10).dp)
                    .size(36.dp)
                    .clickable(
                        enabled = !uiState.isLoading && sentenceValue.isNotBlank() && meaningValue.isNotBlank()
                    ) { 
                        viewModel.updateSentence(
                            sentenceId = sentenceId,
                            term = sentenceValue.trim(),
                            definition = meaningValue.trim(),
                            status = uiState.sentence?.status ?: "unknown",
                            mistakes = uiState.sentence?.numberOfMistakes ?: 0
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_check_detailed_video_screen),
                        contentDescription = "Done",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EditSentenceScreenPreview() {
    EditSentenceScreen(rememberNavController())
}