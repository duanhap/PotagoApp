package com.example.potago.presentation.screen.addcartscreen

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
import kotlinx.coroutines.flow.collectLatest

// ────────────────────────────────────────────────────────────────────────────
// Screen Entry Point
// ────────────────────────────────────────────────────────────────────────────

@Composable
fun AddCardScreen(
    navController: NavController,
    wordSetId: Long,
    viewModel: AddCardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is AddCardEvent.NavigateBack -> navController.popBackStack()
                is AddCardEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Thêm thẻ",
                onBackClick = { navController.popBackStack() },
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding))
        AddCardScreenContent(
            term = uiState.term,
            onTermChange = viewModel::onTermChange,
            definition = uiState.definition,
            onDefinitionChange = viewModel::onDefinitionChange,
            description = uiState.description,
            onDescriptionChange = viewModel::onDescriptionChange,
            isLoading = uiState.isLoading,
            onBackClick = { navController.popBackStack() },
            onSaveClick = { viewModel.saveCard(wordSetId) }
        )
    }

}

// ────────────────────────────────────────────────────────────────────────────
// Content (stateless, preview-friendly)
// ────────────────────────────────────────────────────────────────────────────

@Composable
fun AddCardScreenContent(
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
        // ── Scrollable body ──────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 74.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Top Bar Fake
            AppTopBar(
                title = "Chỉnh sửa thẻ",
                onBackClick = { },
                modifier = Modifier.alpha(0f)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // ── Term Field ───────────────────────────────────────────────
            UnderlinedInputField(
                value = term,
                onValueChange = onTermChange,
                placeholder = "Nhập thuật ngữ vô đây",
                label = "Thuật ngữ",
                singleLine = true
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ── Definition Field ─────────────────────────────────────────
            UnderlinedInputField(
                value = definition,
                onValueChange = onDefinitionChange,
                placeholder = "Nhập định nghĩa vô đây",
                label = "Định nghĩa",
                singleLine = true
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ── Description Field ────────────────────────────────────────
            UnderlinedInputField(
                value = description,
                onValueChange = onDescriptionChange,
                placeholder = "Nhập mô tả vô đây",
                label = "Mô tả chi tiết",
                singleLine = false
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        // ── Bottom Save Button ───────────────────────────────────────────
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
                Divider(
                    thickness = 1.dp,
                    color = Color(0xB3E5E7EB),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                )
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Blue3B,
                        modifier = Modifier.size(36.dp)
                    )
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
                            contentDescription = "Lưu thẻ",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}

// ────────────────────────────────────────────────────────────────────────────
// Underlined Input Field
// ────────────────────────────────────────────────────────────────────────────

@Composable
private fun UnderlinedInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    label: String,
    singleLine: Boolean
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (!singleLine) Modifier.heightIn(min = 120.dp) else Modifier)
        ) {
            // Placeholder text shown when field is empty
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    fontFamily = Nunito,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0x80000000), // rgba(0,0,0,0.5) — matches Figma
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

// ────────────────────────────────────────────────────────────────────────────
// Preview
// ────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AddCardScreenPreview() {
    AddCardScreenContent(
        term = "",
        onTermChange = {},
        definition = "",
        onDefinitionChange = {},
        description = "",
        onDescriptionChange = {},
        isLoading = false,
        onBackClick = {},
        onSaveClick = {}
    )
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

@Preview(showBackground = true, showSystemUi = true, name = "Filled")
@Composable
private fun AddCardScreenFilledPreview() {
    AddCardScreenContent(
        term = "Apple",
        onTermChange = {},
        definition = "Quả táo",
        onDefinitionChange = {},
        description = "A round fruit that is red or green",
        onDescriptionChange = {},
        isLoading = false,
        onBackClick = {},
        onSaveClick = {}
    )
}
