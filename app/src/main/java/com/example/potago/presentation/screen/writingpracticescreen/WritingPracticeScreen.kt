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
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.potago.R

@Composable
fun WritingPracticeScreen(
    navController: NavController,
    patternId: Int = 0
) {
    // Mock data - sẽ load từ API
    val currentSentence = "Ga gần nhất ở đâu?"
    val progress = 0.32f // 32% progress (1/3 câu)
    
    val (userAnswer, setUserAnswer) = remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
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

            // Progress bar với icon cờ
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                // Progress bar
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

            // Tiêu đề "Viết lại câu có nghĩa như sau"
            Text(
                "Viết lại câu có nghĩa như sau",
                color = Color(0xCC000000), // rgba(0,0,0,0.8)
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
                // Mascot Duo (con chim xanh)
                Image(
                    painter = painterResource(id = R.drawable.ic_teaching_mascot),
                    contentDescription = "Duo mascot",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(86.dp, 121.dp)
                )

                Spacer(modifier = Modifier.width(22.dp))

                // Speech bubble với câu tiếng Việt
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
                        currentSentence,
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
                        color = Color(0x1A000000), // rgba(0,0,0,0.1)
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
                                    color = Color(0x33000000), // rgba(0,0,0,0.2)
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

        // Nút KIỂM TRA ở dưới cùng
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 56.dp)
                .height(56.dp)
                .shadow(
                    elevation = 0.dp,
                    shape = RoundedCornerShape(16.dp)
                )
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
                    // TODO: Kiểm tra câu trả lời
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WritingPracticeScreenPreview() {
    WritingPracticeScreen(rememberNavController())
}
