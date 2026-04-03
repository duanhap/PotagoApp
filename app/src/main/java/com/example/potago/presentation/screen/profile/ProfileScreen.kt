package com.example.potago.presentation.screen.profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.potago.R
import com.example.potago.presentation.screen.auth.BigPotagoButton
import com.example.potago.presentation.screen.auth.PasswordField

@Composable
fun ProfileScreen(
    navController: NavController? = null,
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ProfileTopBar(onBackClick =  { navController?.popBackStack() })
        },
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
            AvatarSection(
                onChangeAvatarClick = { /* TODO: open image picker */ }
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
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Dunsensei",
                    leadingIcon = { isFocused ->
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = if (isFocused) Color(0xFF89E219) else Color.LightGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )

                ProfileTextField(
                    label = "Email",
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "hello@example.com",
                    keyboardType = KeyboardType.Email,
                    leadingIcon = { isFocused ->
                        Icon(
                            painter = painterResource(id = R.drawable.ic_outline_email),
                            contentDescription = null,
                            tint = if (isFocused) Color(0xFF89E219) else Color.LightGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                )

                PasswordField(
                    type = "Password",
                    value = password,
                    isPasswordVisible = isPasswordVisible,
                    onValueChange = { password = it },
                    onTogglePasswordVisibility = { isPasswordVisible = !isPasswordVisible }
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(32.dp))

            // ── Save Button ───────────────────────────────────────────────
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                BigPotagoButton(
                    text = "LƯU",
                    enabled = true,
                    isLoading = false,
                    onClick = { /* TODO: save profile */ }
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
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileBackButton(onClick = onBackClick)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Hồ sơ",
                style = MaterialTheme.typography.displayMedium
            )
        }
    }
}

@Composable
private fun ProfileBackButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        label = "back_scale"
    )
    IconButton(
        onClick = onClick,
        interactionSource = interactionSource
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = "Back",
            modifier = Modifier.scale(scale)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Avatar Section
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AvatarSection(onChangeAvatarClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(106.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Black, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.normal_mascot),
                contentDescription = "Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "CHANGE AVATAR",
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFF46A302),
            modifier = Modifier.clickable { onChangeAvatarClick() }
        )
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
    leadingIcon: (@Composable (isFocused: Boolean) -> Unit)? = null
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            textStyle = MaterialTheme.typography.bodyLarge,
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
            singleLine = true
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview
// ─────────────────────────────────────────────────────────────────────────────
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    MaterialTheme {
        ProfileScreen()
    }
}
