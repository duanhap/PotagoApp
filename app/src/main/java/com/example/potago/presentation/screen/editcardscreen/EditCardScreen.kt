package com.example.potago.presentation.screen.editcardscreen

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.potago.R
import com.example.potago.presentation.screen.setting.BackButton
import com.example.potago.presentation.ui.theme.Blue3B
import com.example.potago.presentation.ui.theme.Nunito

@Composable
fun EditCardScreen(
    navController: NavController,
    cardId: Long,
    viewModel: EditCardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(cardId) { viewModel.loadCard(cardId) }
    LaunchedEffect(uiState.isSuccess) { if (uiState.isSuccess) navController.popBackStack() }
    LaunchedEffect(uiState.error) { uiState.error?.let { snackbarHostState.showSnackbar(it) } }

    Scaffold(
        topBar = { AppTopBar(title = "Chỉnh sửa thẻ", onBackClick = { navController.popBackStack() }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding))
        EditCardScreenContent(
            term = uiState.term,
            onTermChange = viewModel::onTermChange,
            definition = uiState.definition,
            onDefinitionChange = viewModel::onDefinitionChange,
            description = uiState.description,
            onDescriptionChange = viewModel::onDescriptionChange,
            isLoading = uiState.isLoading,
            onBackClick = { navController.popBackStack() },
            onSaveClick = { viewModel.saveCard() }
        )
    }
}

@Composable
private fun EditCardScreenContent(
    term: String,
    onTermChange: (String) -> Unit,
    definition: String,
    onDefinitionChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    isLoading: Boolean = false,
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
            // Top Bar Fake
            AppTopBar(
                title = "Chỉnh sửa thẻ",
                onBackClick = {  },
                modifier = Modifier.alpha(0f)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Term Field
            UnderlinedTextFieldBlock(
                value = term,
                onValueChange = onTermChange,
                label = "Thuật ngữ",
                singleLine = true
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Definition Field
            UnderlinedTextFieldBlock(
                value = definition,
                onValueChange = onDefinitionChange,
                label = "Định nghĩa",
                singleLine = true
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Description Field
            UnderlinedTextFieldBlock(
                value = description,
                onValueChange = onDescriptionChange,
                label = "Mô tả chi tiết",
                singleLine = false
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
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Divider(thickness = 1.dp, color = Color(0xB3E5E7EB), modifier = Modifier.align(Alignment.TopCenter))
                if (isLoading) {
                    CircularProgressIndicator(color = Blue3B, modifier = Modifier.size(36.dp))
                } else {
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
}

@Composable
private fun UnderlinedTextFieldBlock(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean,
    placeholder: String = label
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (!singleLine) Modifier.heightIn(min = 120.dp) else Modifier)
        ) {
            if (value.isEmpty()) {
                Text(
                    text = "Nhập $placeholder vô đây",
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
                    color = Color(0xB3000000)
                ),
                modifier = Modifier.fillMaxWidth()
                    .then(if (!singleLine) Modifier.heightIn(min = 120.dp) else Modifier)
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
private fun EditCardScreenPreview() {
    EditCardScreenContent(
        term = "El perro",
        onTermChange = {},
        definition = "The dog",
        onDefinitionChange = {},
        description = "Los gatos que duermen en pantalones cortos suelen ser muy adorables.",
        onDescriptionChange = {},
        isLoading = false,
        onBackClick = {},
        onSaveClick = {}
    )
}
