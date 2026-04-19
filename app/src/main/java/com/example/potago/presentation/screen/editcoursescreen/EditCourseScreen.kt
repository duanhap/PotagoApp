package com.example.potago.presentation.screen.editcoursescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.potago.R
import com.example.potago.presentation.navigation.Screen

@Composable
fun EditCourseScreen(
    navController: NavController,
    @Suppress("UNUSED_PARAMETER") wordSetId: Long,
    initialTitle: String,
    viewModel: EditCourseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            navController.navigate(Screen.DetailCourse(wordSetId, uiState.title)) {
                popUpTo(Screen.DetailCourse.route) {
                    inclusive = true
                }
            }
        }
    }

    EditCourseScreenContent(
        title = uiState.title.ifEmpty { initialTitle },
        onTitleChange = viewModel::onTitleChange,
        description = uiState.description,
        onDescriptionChange = viewModel::onDescriptionChange,
        termLanguageLabel = uiState.termLanguageLabel,
        definitionLanguageLabel = uiState.definitionLanguageLabel,
        onBackClick = { navController.popBackStack() },
        onSaveClick = { viewModel.saveChanges() }
    )
}

@Composable
private fun EditCourseScreenContent(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    termLanguageLabel: String,
    definitionLanguageLabel: String,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 74.dp)
                .verticalScroll(rememberScrollState())
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
                    Text(
                        text = "Chỉnh sửa học phần",
                        fontSize = 32.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            UnderlinedTextFieldBlock(
                value = title,
                onValueChange = onTitleChange,
                placeholder = "Nhập tiêu đề vô đây",
                label = "Tiêu đề",
                singleLine = true
            )

            Spacer(modifier = Modifier.height(28.dp))

            UnderlinedTextFieldBlock(
                value = description,
                onValueChange = onDescriptionChange,
                placeholder = "Nhập mô tả vô đây",
                label = "Mô tả",
                singleLine = false
            )

            Spacer(modifier = Modifier.height(28.dp))

            LanguageSelectBlock(
                valueLabel = termLanguageLabel,
                label = "Ngôn ngữ thuật ngữ",
                onClick = { /* TODO: language picker */ }
            )

            Spacer(modifier = Modifier.height(28.dp))

            LanguageSelectBlock(
                valueLabel = definitionLanguageLabel,
                label = "Ngôn ngữ định nghĩa",
                onClick = { /* TODO: language picker */ }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(74.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3B82F6))
                        .clickable(onClick = onSaveClick),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun UnderlinedTextFieldBlock(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    label: String,
    singleLine: Boolean
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            textStyle = TextStyle(
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xCC000000)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (!singleLine) Modifier.heightIn(min = 80.dp) else Modifier
                ),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0x80000000)
                        )
                    }
                    innerTextField()
                }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(thickness = 2.dp, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
        )
    }
}

@Composable
private fun LanguageSelectBlock(
    valueLabel: String,
    label: String,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = valueLabel,
                modifier = Modifier.weight(1f),
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xCC000000)
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer { rotationZ = 270f },
                tint = Color(0xFF000000)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(thickness = 2.dp, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun EditCourseScreenPreview() {
    EditCourseScreenContent(
        title = "Ordering Food",
        onTitleChange = {},
        description = "",
        onDescriptionChange = {},
        termLanguageLabel = "English",
        definitionLanguageLabel = "Tiếng Việt",
        onBackClick = {},
        onSaveClick = {}
    )
}
