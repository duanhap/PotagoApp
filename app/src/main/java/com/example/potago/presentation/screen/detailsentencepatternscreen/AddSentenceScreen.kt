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
fun AddSentenceScreen(
    navController: NavController,
    patternId: Int = 0,
    viewModel: AddSentenceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    val (sentenceValue, setSentenceValue) = remember { mutableStateOf("") }
    val (meaningValue, setMeaningValue) = remember { mutableStateOf("") }

    // Navigate back on success
    LaunchedEffect(uiState.createSuccess) {
        if (uiState.createSuccess) {
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
                        "Thêm câu",
                        color = Color(0xFF000000),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(72.dp))

                // Error message
                if (uiState.error != null) {
                    Text(
                        text = uiState.error ?: "",
                        color = Color.Red,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                }

                // Trường câu
                BasicTextField(
                    value = sentenceValue,
                    onValueChange = setSentenceValue,
                    textStyle = TextStyle(
                        color = Color(0x80000000), // rgba(0,0,0,0.5)
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp
                    ),
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp)
                        .fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        Box {
                            if (sentenceValue.isEmpty()) {
                                Text(
                                    "Nhập câu vô đây",
                                    color = Color(0x80000000), // rgba(0,0,0,0.5)
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 24.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(13.dp))
                
                // Gạch chân câu
                Box(
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp)
                        .height(2.dp)
                        .fillMaxWidth()
                        .background(color = Color(0xFF000000))
                )
                
                Spacer(modifier = Modifier.height(11.dp))
                
                Text(
                    "Câu",
                    color = Color(0xFF000000),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(start = 20.dp)
                )

                Spacer(modifier = Modifier.height(82.dp))

                // Trường nghĩa
                BasicTextField(
                    value = meaningValue,
                    onValueChange = setMeaningValue,
                    textStyle = TextStyle(
                        color = Color(0x80000000), // rgba(0,0,0,0.5)
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp
                    ),
                    modifier = Modifier
                        .padding(start = 21.dp, end = 21.dp)
                        .fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        Box {
                            if (meaningValue.isEmpty()) {
                                Text(
                                    "Nhập nghĩa vô đây",
                                    color = Color(0x80000000), // rgba(0,0,0,0.5)
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 24.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(14.dp))
                
                // Gạch chân nghĩa
                Box(
                    modifier = Modifier
                        .padding(start = 21.dp, end = 21.dp)
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
                    modifier = Modifier.padding(start = 21.dp)
                )

                Spacer(modifier = Modifier.height(80.dp))
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
                    .background(
                        if (uiState.isLoading || sentenceValue.isBlank() || meaningValue.isBlank()) 
                            Color(0xFFAAAAAA) 
                        else 
                            Color(0xFF58CC02)
                    )
            )
            
            // Nút done
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = (-10).dp)
                    .size(36.dp)
                    .clickable(
                        enabled = !uiState.isLoading && sentenceValue.isNotBlank() && meaningValue.isNotBlank()
                    ) { 
                        viewModel.createSentence(
                            patternId = patternId,
                            term = sentenceValue.trim(),
                            definition = meaningValue.trim()
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
fun AddSentenceScreenPreview() {
    AddSentenceScreen(rememberNavController())
}