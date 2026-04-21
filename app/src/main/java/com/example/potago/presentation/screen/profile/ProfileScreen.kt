package com.example.potago.presentation.screen.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.potago.R
import com.example.potago.presentation.screen.UiState
import com.example.potago.presentation.screen.auth.BigPotagoButton
import com.example.potago.presentation.screen.auth.PasswordField
import com.example.potago.presentation.screen.myvideo.AddButton
import com.example.potago.presentation.screen.setting.BackButton
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProfileScreen(
    navController: NavController? = null,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { viewModel.onAvatarSelected(it) } }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is ProfileEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        topBar = {
            ProfileTopBar(onBackClick = { navController?.popBackStack() })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // ── Avatar ────────────────────────────────────────────────────
            val avatarUrl = (uiState.user as? UiState.Success)?.data?.avatar
            AvatarSection(
                avatarUrl = avatarUrl,
                isUploading = uiState.isUploadingAvatar,
                onChangeAvatarClick = { imagePickerLauncher.launch("image/*") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Form ──────────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProfileTextField(
                    label = "Name",
                    value = uiState.name,
                    onValueChange = viewModel::onNameChange,
                    placeholder = "Dunsensei",
                    leadingIcon = { isFocused ->
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = if (isFocused) Color(0xFF89E219) else Color.LightGray,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    isReadOnly = false
                )

                ProfileTextField(
                    label = "Email",
                    value = uiState.email,
                    onValueChange = viewModel::onEmailChange,
                    placeholder = "hello@example.com",
                    keyboardType = KeyboardType.Email,
                    leadingIcon = { isFocused ->
                        Icon(
                            painter = painterResource(id = R.drawable.ic_outline_email),
                            contentDescription = null,
                            tint = if (isFocused) Color(0xFF89E219) else Color.LightGray,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    isReadOnly = true
                )

                PasswordField(
                    type = "Password",
                    value = uiState.password,
                    isPasswordVisible = isPasswordVisible,
                    onValueChange = viewModel::onPasswordChange,
                    onTogglePasswordVisibility = { isPasswordVisible = !isPasswordVisible }
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(32.dp))

            // ── Save Button ───────────────────────────────────────────────
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                BigPotagoButton(
                    text = "LƯU",
                    enabled = uiState.isSaveButtonEnabled && !uiState.isSaving,
                    isLoading = uiState.isSaving,
                    onClick = viewModel::saveProfile
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Top App Bar
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ProfileTopBar(onBackClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
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
                    text = "Hồ sơ",
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


// ─────────────────────────────────────────────────────────────────────────────
// Avatar Section
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AvatarSection(
    avatarUrl: String?,
    isUploading: Boolean = false,
    onChangeAvatarClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(106.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Black, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (!avatarUrl.isNullOrBlank()) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    error = painterResource(id = R.drawable.normal_mascot),
                    placeholder = painterResource(id = R.drawable.normal_mascot)
                )
            } else {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.normal_mascot),
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            if (isUploading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = Color(0xFF46A302),
                    strokeWidth = 3.dp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isUploading) {
            Text(
                text = "Đang tải lên...",
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFF46A302)
            )
        } else {
            Text(
                text = "CHANGE AVATAR",
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFF46A302),
                modifier = Modifier.clickable { onChangeAvatarClick() }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Reusable Profile Text Field
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: (@Composable (isFocused: Boolean) -> Unit)? = null,
    isReadOnly : Boolean = true
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = if (isFocused && !isReadOnly) Color.Black else Color(0xFFCCCCCC)),
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color.LightGray,
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(53.dp)
                .onFocusChanged { isFocused = it.isFocused },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF89E219),
                unfocusedBorderColor = Color(0xFFE5E7EB),
                focusedContainerColor = Color(0xFFFFFFFF),
                unfocusedContainerColor = Color(0xFFF9FAFB),
                unfocusedPlaceholderColor = Color.LightGray,
                focusedPlaceholderColor = Color.LightGray
            ),
            leadingIcon = leadingIcon?.let { icon -> { icon(isFocused) } },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            readOnly = isReadOnly
        )
    }
}
