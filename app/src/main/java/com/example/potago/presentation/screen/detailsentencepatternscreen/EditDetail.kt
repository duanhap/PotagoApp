package com.example.potago.presentation.screen.detailsentencepatternscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.potago.R
import com.example.potago.presentation.screen.setting.BackButton

private val GreenPrimary = Color(0xFF58CC02)
private val GreenShadow = Color(0xFF46A302)

@Composable
fun EditDetailScreen(
    navController: NavController,
    patternId: Int,
    viewModel: DetailSentencePatternViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val pattern = uiState.pattern

    var titleValue by remember(pattern) { mutableStateOf(pattern?.name ?: "") }
    var descValue by remember(pattern) { mutableStateOf(pattern?.description ?: "") }
    var termLangCode by remember(pattern) { mutableStateOf(pattern?.termLanguageCode ?: "en") }
    var defLangCode by remember(pattern) { mutableStateOf(pattern?.definitionLanguageCode ?: "vi") }

    LaunchedEffect(patternId) {
        if (pattern == null || pattern.id != patternId) viewModel.loadDetail(patternId)
    }

    LaunchedEffect(uiState.updateSuccess) {
        if (uiState.updateSuccess) {
            viewModel.clearUpdateSuccess()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Chỉnh sửa",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding))
        Box(modifier = Modifier.fillMaxSize()) {
            EditDetailContent(
                title = titleValue,
                onTitleChange = { titleValue = it },
                description = descValue,
                onDescriptionChange = { descValue = it },
                termLangCode = termLangCode,
                defLangCode = defLangCode,
                onTermLangChange = { termLangCode = it },
                onDefLangChange = { defLangCode = it },
                isLoading = uiState.isLoading,
                isSaving = uiState.isUpdating,
                errorMessage = uiState.actionError,
                onSaveClick = {
                    viewModel.updatePattern(
                        name = titleValue.trim(),
                        description = descValue.trim(),
                        termLangCode = termLangCode,
                        defLangCode = defLangCode,
                        isPublic = uiState.pattern?.isPublic ?: false
                    )
                }
            )
        }
    }
}

@Composable
private fun EditDetailContent(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    termLangCode: String,
    defLangCode: String,
    onTermLangChange: (String) -> Unit,
    onDefLangChange: (String) -> Unit,
    isLoading: Boolean = false,
    isSaving: Boolean = false,
    errorMessage: String? = null,
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
            AppTopBar(title = "Chỉnh sửa mẫu câu", onBackClick = {}, modifier = Modifier.alpha(0f))
            Spacer(modifier = Modifier.height(28.dp))

            if (!errorMessage.isNullOrBlank()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )
            }

            UnderlinedField(
                value = title,
                onValueChange = onTitleChange,
                placeholder = "Nhập tiêu đề vô đây",
                label = "Tiêu đề",
                singleLine = true
            )
            Spacer(modifier = Modifier.height(28.dp))

            UnderlinedField(
                value = description,
                onValueChange = onDescriptionChange,
                placeholder = "Nhập mô tả vô đây",
                label = "Mô tả",
                singleLine = false
            )
            Spacer(modifier = Modifier.height(28.dp))

            LanguageSelectBlock(
                valueCode = termLangCode,
                label = "Ngôn ngữ của câu",
                onValueChange = onTermLangChange
            )
            Spacer(modifier = Modifier.height(28.dp))

            LanguageSelectBlock(
                valueCode = defLangCode,
                label = "Ngôn ngữ của nghĩa",
                onValueChange = onDefLangChange
            )
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Bottom save button
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(74.dp),
            color = Color.White,
            shadowElevation = 20.dp
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Divider(thickness = 1.dp, color = Color(0xB3E5E7EB), modifier = Modifier.align(Alignment.TopCenter))
                if (isSaving || isLoading) {
                    CircularProgressIndicator(color = GreenPrimary, modifier = Modifier.size(36.dp))
                } else {

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(GreenPrimary)
                        .clickable(
                            enabled = title.isNotBlank(),
                            onClick = onSaveClick
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check),
                        contentDescription = "Lưu",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                }
            }
        }
    }
}

@Composable
private fun UnderlinedField(
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
                .then(if (!singleLine) Modifier.heightIn(min = 80.dp) else Modifier),
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
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
    }
}

@Composable
private fun LanguageSelectBlock(
    valueCode: String,
    label: String,
    onValueChange: (String) -> Unit
) {
    val languages = listOf(
        "en" to "English", "vi" to "Tiếng Việt",
        "ja" to "日本語", "zh" to "中文"
    )
    var expanded by remember { mutableStateOf(false) }
    val displayName = languages.find { it.first == valueCode }?.second ?: valueCode

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Box {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { expanded = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = displayName,
                    modifier = Modifier.weight(1f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xCC000000)
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp).graphicsLayer { rotationZ = 270f },
                    tint = Color.Black
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
                                color = if (code == valueCode) GreenPrimary else Color.Black
                            )
                        },
                        onClick = { onValueChange(code); expanded = false }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(thickness = 2.dp, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
    }
}

@Composable
private fun AppTopBar(title: String, onBackClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 3.dp,
        shadowElevation = 4.dp,
        color = Color.White
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(60.dp))
                Text(text = title, style = MaterialTheme.typography.displayMedium, modifier = Modifier.weight(1f))
            }
            Box(modifier = Modifier.matchParentSize()) {
                BackButton(
                    onClick = onBackClick,
                    modifier = Modifier.align(Alignment.CenterStart).wrapContentSize()
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun EditDetailScreenPreview() {
    EditDetailContent(
        title = "",
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
