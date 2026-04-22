package com.example.potago.presentation.screen.detailsentencepatternscreen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.potago.R
import com.example.potago.presentation.navigation.Screen

@Composable
fun DetailSentencePatternScreen(
    navController: NavController,
    patternId: Int,
    viewModel: DetailSentencePatternViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(patternId) {
        viewModel.loadDetail(patternId)
    }

    val pattern = uiState.pattern
    val sentencesCount = uiState.sentences.size

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier.fillMaxSize()
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
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.tint(Color.Black),
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .size(28.dp)
                        .clickable { navController.popBackStack() }
                )
                Text(
                    "Mẫu câu",
                    color = Color(0xFF000000),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp)
                )
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF89E219))
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = uiState.error ?: "Lỗi tải dữ liệu", color = Color.Red)
                }
            } else if (pattern != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(bottom = 17.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(22.dp))
                    
                    Text(
                        text = pattern.name,
                        color = Color(0xFF000000),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 9.dp, start = 22.dp)
                    )
                    
                    Text(
                        text = "$sentencesCount mẫu câu",
                        color = Color(0xFF000000),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 35.dp, start = 21.dp)
                    )
                    
                    Text(
                        text = pattern.description,
                        color = Color(0xFF000000),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 44.dp, start = 22.dp)
                    )

                    Text(
                        "Chế độ học",
                        color = Color(0xFF000000),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp, start = 21.dp)
                    )

                    // Study Modes
                    StudyModeItem(iconResId = R.drawable.luyenviet, label = "Luyện viết")
                    StudyModeItem(iconResId = R.drawable.sapxepchu, label = "Sắp xếp chữ")

                    Text(
                        "Tính năng khác",
                        color = Color(0xFF000000),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 15.dp, start = 21.dp)
                    )

                    // Other features
                    ActionItem(
                        iconResId = R.drawable.danhsachcau,
                        onClick = { navController.navigate(Screen.ListOfDetail.route) }
                    )
                    ActionItem(
                        iconResId = R.drawable.suacau,
                        onClick = { navController.navigate(Screen.EditDetail(patternId)) }
                    )
                    ActionItem(
                        iconResId = R.drawable.xoacau,
                        onClick = { navController.navigate(Screen.DeleteDetail(patternId)) }
                    )
                }
            }
        }
    }
}

@Composable
fun StudyModeItem(iconResId: Int, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(bottom = 14.dp, start = 19.dp, end = 19.dp)
            .border(width = 2.dp, color = Color(0x1A000000), shape = RoundedCornerShape(15.dp))
            .clip(shape = RoundedCornerShape(15.dp))
            .fillMaxWidth()
            .background(color = Color(0xFFFFFFFF))
            .clickable { /* Handle study mode click */ }
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = label,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxWidth().height(76.dp)
        )
    }
}

@Composable
fun ActionItem(iconResId: Int, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(bottom = 16.dp, start = 21.dp, end = 21.dp)
            .border(width = 2.dp, color = Color(0x1A000000), shape = RoundedCornerShape(15.dp))
            .clip(shape = RoundedCornerShape(15.dp))
            .fillMaxWidth()
            .background(color = Color(0xFFFFFFFF))
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxWidth().height(76.dp)
        )
    }
}

@Composable
fun DetailSentenceTextFieldView(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    textStyle: TextStyle = TextStyle.Default
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle,
        modifier = modifier,
        decorationBox = { innerTextField: @Composable () -> Unit ->
            Box {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = textStyle,
                        color = Color(0xB3050505)
                    )
                }
                innerTextField()
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DetailSentencePatternScreenPreview() {
    DetailSentencePatternScreen(rememberNavController(), patternId = 1)
}
