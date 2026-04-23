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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.example.potago.presentation.screen.setting.BackButton
import com.example.potago.presentation.ui.theme.Blue3B

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
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Chỉnh sửa",
                onBackClick = { navController.popBackStack() },
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding))
        Box(modifier = Modifier.fillMaxSize()) {
            EditCourseScreenContent(
                title = uiState.title.ifEmpty { initialTitle },
                onTitleChange = viewModel::onTitleChange,
                description = uiState.description,
                onDescriptionChange = viewModel::onDescriptionChange,
                termLangCode = uiState.termLangCode,
                defLangCode = uiState.defLangCode,
                onTermLangChange = viewModel::onTermLangChange,
                onDefLangChange = viewModel::onDefLangChange,
                onSaveClick = { viewModel.saveChanges() }
            )
        }
    }

}

@Composable
private fun EditCourseScreenContent(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    termLangCode: String,
    defLangCode: String,
    onTermLangChange: (String) -> Unit,
    onDefLangChange: (String) -> Unit,
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
            AppTopBar(
                title = "Chỉnh sửa học phần",
                onBackClick = {  },
                modifier = Modifier.alpha(0f)
            )
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
                valueCode = termLangCode,
                label = "Ngôn ngữ thuật ngữ",
                onValueChange = onTermLangChange
            )

            Spacer(modifier = Modifier.height(28.dp))

            LanguageSelectBlock(
                valueCode = defLangCode,
                label = "Ngôn ngữ định nghĩa",
                onValueChange = onDefLangChange
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(74.dp),
            color = Color.White,
            shadowElevation = 20.dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Divider(
                    thickness = 1.dp,
                    color = Color(0xB3E5E7EB),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                )

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Blue3B)
                        .clickable(onClick = onSaveClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check),
                        contentDescription = "Save",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
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
    valueCode: String,
    label: String,
    onValueChange: (String) -> Unit
) {
    val languages = listOf(
        "en" to "English",
        "vi" to "Tiếng Việt",
        "ja" to "日本語",
        "zh" to "中文"
    )
    var expanded by remember { mutableStateOf(false) }
    val displayName = languages.find { it.first == valueCode }?.second ?: valueCode

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = displayName,
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

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                languages.forEach { (code, name) ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = name,
                                fontWeight = if (code == valueCode) FontWeight.ExtraBold else FontWeight.Normal,
                                color = if (code == valueCode) Color(0xFF58CC02) else Color.Black
                            )
                        },
                        onClick = {
                            onValueChange(code)
                            expanded = false
                        }
                    )
                }
            }
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

@Composable
private fun AppTopBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
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
                    text = title,
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


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun EditCourseScreenPreview() {
    EditCourseScreenContent(
        title = "Ordering Food",
        onTitleChange = {},
        description = "",
        onDescriptionChange = {},
        termLangCode = "en",
        defLangCode = "vi",
        onTermLangChange = {},
        onDefLangChange = {},
        onSaveClick = {}
    )
}
