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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.potago.R
import com.example.potago.presentation.screen.setting.BackButton
import com.example.potago.presentation.ui.theme.Nunito

private val GreenPrimary = Color(0xFF46A302)

@Composable
fun AddSentenceScreen(
    navController: NavController,
    patternId: Int = 0,
    viewModel: AddSentenceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var sentenceValue by remember { mutableStateOf("") }
    var meaningValue by remember { mutableStateOf("") }

    LaunchedEffect(uiState.createSuccess) {
        if (uiState.createSuccess) navController.popBackStack()
    }

    Scaffold(
        topBar = { AppTopBar(title = "Thêm câu", onBackClick = { navController.popBackStack() }) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding))
        AddSentenceContent(
            sentenceValue = sentenceValue,
            onSentenceChange = { sentenceValue = it },
            meaningValue = meaningValue,
            onMeaningChange = { meaningValue = it },
            isLoading = uiState.isLoading,
            error = uiState.error,
            onSaveClick = {
                viewModel.createSentence(
                    patternId = patternId,
                    term = sentenceValue.trim(),
                    definition = meaningValue.trim()
                )
            }
        )
    }
}

@Composable
private fun AddSentenceContent(
    sentenceValue: String,
    onSentenceChange: (String) -> Unit,
    meaningValue: String,
    onMeaningChange: (String) -> Unit,
    isLoading: Boolean,
    error: String?,
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
            AppTopBar(title = "Thêm câu", onBackClick = {}, modifier = Modifier.alpha(0f))
            Spacer(modifier = Modifier.height(48.dp))

            if (error != null) {
                Text(
                    text = error,
                    color = Color.Red,
                    fontFamily = Nunito,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            UnderlinedInputField(
                value = sentenceValue,
                onValueChange = onSentenceChange,
                placeholder = "Nhập câu vô đây",
                label = "Câu",
                singleLine = true
            )
            Spacer(modifier = Modifier.height(28.dp))
            UnderlinedInputField(
                value = meaningValue,
                onValueChange = onMeaningChange,
                placeholder = "Nhập nghĩa vô đây",
                label = "Nghĩa",
                singleLine = true
            )
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Bottom Save Button
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(74.dp),
            color = Color.White,
            shadowElevation = 20.dp
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                HorizontalDivider(thickness = 1.dp, color = Color(0xB3E5E7EB), modifier = Modifier.align(Alignment.TopCenter))
                if (isLoading) {
                    CircularProgressIndicator(color = GreenPrimary, modifier = Modifier.size(36.dp))
                } else {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(if (sentenceValue.isNotBlank() && meaningValue.isNotBlank()) GreenPrimary else Color(0xFFABCF7E))
                            .clickable(
                                enabled = !isLoading && sentenceValue.isNotBlank() && meaningValue.isNotBlank(),
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
private fun UnderlinedInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    label: String,
    singleLine: Boolean
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Box(modifier = Modifier.fillMaxWidth()) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    fontFamily = Nunito,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0x80000000)
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = singleLine,
                textStyle = TextStyle(
                    fontFamily = Nunito,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(thickness = 2.dp, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontFamily = Nunito,
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
        color = Color.White
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(60.dp))
                Text(text = title, style = MaterialTheme.typography.displayMedium, modifier = Modifier.weight(1f))
            }
            Box(modifier = Modifier.matchParentSize()) {
                BackButton(onClick = onBackClick, modifier = Modifier.align(Alignment.CenterStart).wrapContentSize())
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddSentenceScreenPreview() {
    AddSentenceContent(
        sentenceValue = "",
        onSentenceChange = {},
        meaningValue = "",
        onMeaningChange = {},
        isLoading = false,
        error = null,
        onSaveClick = {}
    )
}
