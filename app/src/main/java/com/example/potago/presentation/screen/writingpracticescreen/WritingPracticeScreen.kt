package com.example.potago.presentation.screen.writingpracticescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.potago.R

@Composable
fun WritingPracticeScreen(
    navController: NavController,
    patternId: Int = 0,
    viewModel: WritingPracticeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val (userAnswer, setUserAnswer) = remember { mutableStateOf("") }

    // Load sentences khi screen được tạo và reset state khi quay lại
    LaunchedEffect(patternId) {
        if (patternId > 0) {
            viewModel.resetState()
            viewModel.loadSentences(patternId)
        }
    }

    // Reset user answer khi chuyển câu mới
    LaunchedEffect(uiState.currentIndex) {
        setUserAnswer("")
    }

    val currentSentence = uiState.currentSentence
    val progress = viewModel.getProgress()

    // Hiển thị màn hình completion nếu đã hoàn thành
    if (uiState.isCompleted) {
        CompletionScreen(
            navController = navController,
            experienceEarned = uiState.experienceEarned,
            diamondEarned = uiState.diamondEarned,
            timeFormatted = viewModel.getCompletionTimeFormatted()
        )
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF58CC02))
            }
        } else if (currentSentence != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color(0xE3FFFFFF))
                        .shadow(elevation = 2.dp, spotColor = Color(0x1A000000))
                        .padding(vertical = 11.dp)
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
                        "Luyện viết",
                        color = Color(0xFF000000),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(13.dp)
                            .clip(RoundedCornerShape(9999.dp))
                            .background(Color(0xFFE5E7EB))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(progress)
                                .clip(RoundedCornerShape(9999.dp))
                                .background(Color(0xFF58CC02))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(59.dp))

                // Tiêu đề
                Text(
                    "Viết lại câu có nghĩa như sau",
                    color = Color(0xCC000000),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 24.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Mascot và speech bubble
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_teaching_mascot),
                        contentDescription = "Duo mascot",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(86.dp, 121.dp)
                    )

                    Spacer(modifier = Modifier.width(22.dp))

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .shadow(
                                elevation = 2.dp,
                                shape = RoundedCornerShape(
                                    topStart = 0.dp,
                                    topEnd = 16.dp,
                                    bottomEnd = 16.dp,
                                    bottomStart = 16.dp
                                )
                            )
                            .background(
                                Color.White,
                                RoundedCornerShape(
                                    topStart = 0.dp,
                                    topEnd = 16.dp,
                                    bottomEnd = 16.dp,
                                    bottomStart = 16.dp
                                )
                            )
                            .border(
                                width = 1.dp,
                                color = Color(0xFFE5E7EB),
                                shape = RoundedCornerShape(
                                    topStart = 0.dp,
                                    topEnd = 16.dp,
                                    bottomEnd = 16.dp,
                                    bottomStart = 16.dp
                                )
                            )
                            .padding(13.dp)
                    ) {
                        Text(
                            currentSentence.definition,
                            color = Color(0xFF696969),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 24.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(75.dp))

                // Text input area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(246.dp)
                        .border(
                            width = 2.dp,
                            color = Color(0x1A000000),
                            shape = RoundedCornerShape(15.dp)
                        )
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color.White)
                        .padding(13.dp)
                ) {
                    BasicTextField(
                        value = userAnswer,
                        onValueChange = setUserAnswer,
                        textStyle = TextStyle(
                            color = Color(0xFF000000),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 24.sp
                        ),
                        modifier = Modifier.fillMaxSize(),
                        decorationBox = { innerTextField ->
                            Box {
                                if (userAnswer.isEmpty()) {
                                    Text(
                                        "Nhập ở đây ...",
                                        color = Color(0x33000000),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 24.sp
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(230.dp))
            }
        }

        // Hiển thị feedback dựa trên answerResult - positioned at bottom
        when (val result = uiState.answerResult) {
            is AnswerResult.Correct -> {
                Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                    CorrectAnswerFeedback(
                        onContinue = { viewModel.moveToNextSentence() }
                    )
                }
            }
            is AnswerResult.Incorrect -> {
                Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                    IncorrectAnswerFeedback(
                        correctAnswer = result.correctAnswer,
                        onUnderstood = { viewModel.moveToNextSentence() }
                    )
                }
            }
            AnswerResult.None -> {
                // Nút KIỂM TRA ở dưới cùng
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 56.dp)
                        .height(56.dp)
                        .shadow(elevation = 0.dp, shape = RoundedCornerShape(16.dp))
                        .background(
                            if (userAnswer.isBlank()) Color(0x8058CC02) else Color(0xFF58CC02),
                            RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 3.dp,
                            color = if (userAnswer.isBlank()) Color(0x8046A302) else Color(0xFF46A302),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable(enabled = userAnswer.isNotBlank()) {
                            viewModel.checkAnswer(userAnswer)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "KIỂM TRA",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 24.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun CorrectAnswerFeedback(onContinue: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(145.dp)
            .background(Color(0xFFA4E86C))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(33.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check),
                        contentDescription = "Correct",
                        tint = Color(0xFF58CC02),
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(17.dp))
                
                Text(
                    "CHÍNH XÁC !",
                    color = Color(0xFF46A302),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 24.sp
                )
            }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color(0xFF58CC02), RoundedCornerShape(16.dp))
                    .border(
                        width = 3.dp,
                        color = Color(0xFF46A302),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onContinue() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "TIẾP TỤC",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 24.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun IncorrectAnswerFeedback(correctAnswer: String, onUnderstood: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(222.dp)
            .background(Color(0xFFFFD7D8))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color(0xFFFF6063)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_cancel),
                            contentDescription = "Incorrect",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(17.dp))
                    
                    Text(
                        "ĐÁP ÁN ĐÚNG LÀ :",
                        color = Color(0xFFFF383C),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 24.sp
                    )
                }
                
                Text(
                    correctAnswer,
                    color = Color(0xFFFF383C),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 24.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color(0xFFFF6063), RoundedCornerShape(16.dp))
                    .border(
                        width = 3.dp,
                        color = Color(0xFFFF383C),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onUnderstood() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "ĐÃ HIỂU",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 24.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun CompletionScreen(
    navController: NavController,
    experienceEarned: Int = 15,
    diamondEarned: Int = 10,
    timeFormatted: String = "0:00"
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(59.dp)
                .background(Color(0xE3FFFFFF))
                .shadow(elevation = 2.dp, spotColor = Color(0x1A000000))
                .padding(horizontal = 18.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                "Kết quả",
                color = Color(0xFF000000),
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 24.sp
            )
        }

        // Potato in ground illustration
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(329.dp)
                .padding(horizontal = 20.dp)
        ) {
            // Sử dụng mascot có sẵn thay vì illustration phức tạp
            Image(
                painter = painterResource(id = R.drawable.ic_teaching_mascot),
                contentDescription = "Completion mascot",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Phần thưởng title
        Text(
            "Phần thưởng",
            color = Color(0xFF000000),
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 24.sp,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Reward cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // XP Card
            RewardCard(
                title = "Kinh nghiệm",
                value = experienceEarned.toString(),
                unit = "XP",
                backgroundColor = Color(0x80FEF9C3),
                borderColor = Color(0x80FEF08A),
                headerColor = Color(0xFFFFD600),
                textColor = Color(0xFFA16207),
                modifier = Modifier.weight(1f)
            )

            // Diamond Card
            RewardCard(
                title = "Diamond",
                value = diamondEarned.toString(),
                unit = "",
                backgroundColor = Color(0x80FFA9A3),
                borderColor = Color(0x40F44336),
                headerColor = Color(0xFFF44336),
                textColor = Color(0xFFF44336),
                modifier = Modifier.weight(1f)
            )

            // Time Card
            RewardCard(
                title = "Time",
                value = timeFormatted,
                unit = "",
                backgroundColor = Color(0xFFD9E7FF),
                borderColor = Color(0x403B82F6),
                headerColor = Color(0xFF3B82F6),
                textColor = Color(0xFF3B82F6),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(80.dp))

        // Mascot with speech bubble
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_teaching_mascot),
                contentDescription = "Mascot",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(86.dp, 121.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = 16.dp,
                            bottomEnd = 16.dp,
                            bottomStart = 16.dp
                        )
                    )
                    .background(
                        Color.White,
                        RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = 16.dp,
                            bottomEnd = 16.dp,
                            bottomStart = 16.dp
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = Color(0xFFE5E7EB),
                        shape = RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = 16.dp,
                            bottomEnd = 16.dp,
                            bottomStart = 16.dp
                        )
                    )
                    .padding(12.dp)
            ) {
                Text(
                    "Kết quả rất tốt đó <3\nTiếp tục chứ chủ nhân !?",
                    color = Color(0xFF4B5563),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 24.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Từ chối button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .border(
                        width = 1.dp,
                        color = Color(0xFFE5E7EB),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { navController.popBackStack() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Từ chối",
                    color = Color(0xFF374151),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 24.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Học tiếp button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .background(Color(0xFF58CC02), RoundedCornerShape(16.dp))
                    .border(
                        width = 3.dp,
                        color = Color(0xFF46A302),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { navController.popBackStack() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Học tiếp",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 24.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun RewardCard(
    title: String,
    value: String,
    unit: String,
    backgroundColor: Color,
    borderColor: Color,
    headerColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(101.dp)
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .border(
                width = 3.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .background(
                        headerColor,
                        RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 24.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Value
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    value,
                    color = textColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 24.sp
                )
                if (unit.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        unit,
                        color = textColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WritingPracticeScreenPreview() {
    WritingPracticeScreen(rememberNavController())
}
