package com.example.potago.presentation.screen.auth

import android.annotation.SuppressLint
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.potago.R
import com.example.potago.presentation.navigation.Screen
import com.example.potago.presentation.screen.UiEvent
import com.example.potago.presentation.screen.UiState
import com.example.potago.presentation.ui.theme.Nunito
import kotlinx.coroutines.delay

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by loginViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

//    LaunchedEffect(uiState.errorMessage) {
//        uiState.errorMessage?.let { message ->
//            if (message.isNotBlank()) {
//                loginViewModel.onEvent(LoginEvent.ErrorShown)
//            }
//        }
//    }

    LaunchedEffect(Unit) {
        loginViewModel.uiEvent
            .collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is UiEvent.Navigate -> {
                    navController.navigate(event.route) {
                        event.popUpTo?.let {
                            popUpTo(it) {
                                inclusive = event.inclusive
                            }
                        }
                    }
                }
            }
        }
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) {
        LoginContent(
            uiState = uiState,
            onEvent = loginViewModel::onEvent,
            onForgetPasswordClick = {
                // TODO: Navigate to forget password screen
            },
            onSignUpClick = {
                navController.navigate(Screen.SignUp.route)
            }
        )
    }

}

@Composable
private fun LoginContent(
    uiState: LoginUiState,
    onEvent: (LoginEvent) -> Unit,
    onForgetPasswordClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    val isLoading = uiState.authState is UiState.Loading
    val loginButtonEnabled = uiState.isLoginButtonEnabled && !isLoading
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))
            MascotAndBubble()
            Text(
                text = "Potago",
                style = MaterialTheme.typography.displayMedium
            )


            Spacer(modifier = Modifier.height(40.dp))

            EmailField(
                value = uiState.email,
                onValueChange = { onEvent(LoginEvent.EmailChanged(it)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordField(
                value = uiState.password,
                isPasswordVisible = uiState.isPasswordVisible,
                onValueChange = { onEvent(LoginEvent.PasswordChanged(it)) },
                onTogglePasswordVisibility = { onEvent(LoginEvent.TogglePasswordVisibility) }
            )

            Text(
                text = "FORGET PASSWORD?",
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 8.dp)
                    .clickable { onForgetPasswordClick() },
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF3B82F6)
            )

//            if (uiState.errorMessage != null) {
//                Spacer(modifier = Modifier.height(8.dp))
//                Text(
//                    text = uiState.errorMessage,
//                    color = Color(0xFFDC2626),
//                    style = MaterialTheme.typography.bodySmall,
//                    modifier = Modifier.fillMaxWidth()
//                )
//            }

            Spacer(modifier = Modifier.height(32.dp))
            BigPotagoButton(
                enabled = loginButtonEnabled,
                isLoading = isLoading,
                onClick = { onEvent(LoginEvent.Submit) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            SocialLoginSection()

            Spacer(modifier = Modifier.height(40.dp))

            SignUpSection(onSignUpClick = onSignUpClick)

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun EmailField(
    value: String,
    onValueChange: (String) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "EMAIL",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.ExtraBold
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            textStyle = MaterialTheme.typography.bodyLarge,
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    "hello@example.com",
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
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_outline_email),
                    contentDescription = null,
                    tint = if (isFocused) Color(0xFF89E219) else Color.LightGray
                )
            },
            singleLine = true
        )
    }
}

@Composable
fun PasswordField(
    type: String= "PASSWORD",
    value: String,
    isPasswordVisible: Boolean,
    onValueChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = type,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.ExtraBold
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            textStyle = MaterialTheme.typography.bodyLarge,
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("••••••••", color = Color.LightGray) },
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
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_prime_lock),
                    contentDescription = null,
                    tint = if (isFocused) Color(0xFF89E219) else Color.LightGray
                )
            },
            trailingIcon = {
                val image = if (isPasswordVisible) {
                    R.drawable.ic_eye
                } else {
                    R.drawable.ic_hide_eye
                }

                IconButton(onClick = onTogglePasswordVisibility) {
                    Icon(
                        painter = painterResource(id = image),
                        contentDescription = null,
                        tint = Color.LightGray
                    )
                }
            },
            visualTransformation = if (isPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )
    }
}

@Composable
fun BigPotagoButton(
    text: String = "LOG IN",
    enabled: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        label = ""
    )
    val animatedHeight by animateDpAsState(
        targetValue = if (isPressed) 56.dp else 53.dp,
        label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .height(56.dp)
            .background(
                if (enabled) Color(0xFF46A302) else Color(0xFFABCF7E),
                RoundedCornerShape(16.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(animatedHeight)
                .background(
                    if (enabled) Color(0xFF58CC02) else Color(0xFFB7E37E),
                    RoundedCornerShape(16.dp)
                )
                .pointerInput(enabled) {
                    detectTapGestures(
                        onPress = {
                            if (!enabled) return@detectTapGestures
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                        },
                        onTap = {
                            if (enabled) {
                                onClick()
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun SocialLoginSection() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE5E5E5))
        Text(
            text = "or continue with",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE5E5E5))
    }

    Spacer(modifier = Modifier.height(24.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SocialButton(
            text = "Google",
            modifier = Modifier.weight(1f),
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.logo_google),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(16.dp)
                )
            }
        )
        SocialButton(
            text = "Facebook",
            modifier = Modifier.weight(1f),
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.logo_facebook),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(16.dp)
                )
            }
        )
    }
}

@Composable
private fun SignUpSection(
    onSignUpClick: () -> Unit
) {
    val annotatedString = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = Color.Gray,
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold
            )
        ) {
            append("Don't have an account? ")
        }
        withStyle(
            style = SpanStyle(
                color = Color(0xFF58CC02),
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold
            )
        ) {
            append("SIGN UP")
        }
    }

    Text(
        text = annotatedString,
        modifier = Modifier.clickable { onSignUpClick() },
        fontSize = 14.sp
    )
}

@Composable
private fun MascotAndBubble() {

    var start by remember { mutableStateOf(false) }
    var showBubble by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        start = true

        delay(500)
        showBubble = true
    }

    /* ---------------- Mascot Transition ---------------- */

    val mascotTransition = updateTransition(
        targetState = start,
        label = "mascot_transition"
    )

    val mascotScale by mascotTransition.animateFloat(
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        },
        label = "mascot_scale"
    ) { state ->
        if (state) 1f else 0.5f
    }

    val mascotAlpha by mascotTransition.animateFloat(
        transitionSpec = { tween(400) },
        label = "mascot_alpha"
    ) { state ->
        if (state) 1f else 0f
    }

    /* ---------------- Bubble Transition ---------------- */

    val bubbleTransition = updateTransition(
        targetState = showBubble,
        label = "bubble_transition"
    )

    val bubbleScale by bubbleTransition.animateFloat(
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        },
        label = "bubble_scale"
    ) { state ->
        if (state) 1f else 0.4f
    }

    val bubbleAlpha by bubbleTransition.animateFloat(
        transitionSpec = { tween(350) },
        label = "bubble_alpha"
    ) { state ->
        if (state) 1f else 0f
    }

    val bubbleOffsetY by bubbleTransition.animateFloat(
        transitionSpec = { tween(350) },
        label = "bubble_offset"
    ) { state ->
        if (state) 0f else 50f
    }

    /* ---------------- UI ---------------- */

    Box(
        modifier = Modifier.wrapContentSize(),
        contentAlignment = Alignment.TopEnd
    ) {

        // 💬 Bubble
        Box(
            modifier = Modifier
                .offset(x = 60.dp, y = (-30).dp)
                .graphicsLayer {
                    scaleX = bubbleScale
                    scaleY = bubbleScale
                    alpha = bubbleAlpha
                    translationY = bubbleOffsetY
                }
                .background(
                    Color.White,
                    RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomEnd = 12.dp
                    )
                )
                .border(
                    1.2.dp,
                    Color(0xFFE5E7EB),
                    RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomEnd = 12.dp
                    )
                )
                .padding(horizontal = 15.dp, vertical = 6.dp)
        ) {
            Text(
                text = "Welcome back!",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF4B5563)
            )
        }

        // 🐣 Mascot
        Image(
            painter = painterResource(id = R.drawable.thin_smile_mascot),
            contentDescription = "Mascot",
            modifier = Modifier
                .size(160.dp)
                .graphicsLayer {
                    scaleX = mascotScale
                    scaleY = mascotScale
                    alpha = mascotAlpha
                }
        )
    }
}
@Composable
fun SocialButton(
    text: String,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    buttonEnabled: Boolean = true,
) {
    var isPressed by remember { mutableStateOf(false) }

    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        label = ""
    )
    val animatedHeight by animateDpAsState(
        targetValue = if (isPressed) 48.dp else 45.dp,
        label = ""
    )

    Box(
        modifier = modifier
            .height(48.dp)
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .border(1.2.dp, Color(0xFFE5E5E5), RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(color = Color(0xFFE5E5E5) )
    ) {
        Box(modifier = modifier
                .fillMaxWidth()
                .height(animatedHeight)
                .clip(RoundedCornerShape(16.dp))
                .background(color = Color(0xFFFFFFFF))
                .pointerInput(buttonEnabled) {
                    detectTapGestures(
                        onPress = {
                            if (!buttonEnabled) return@detectTapGestures
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                        },
                        onTap = {
                            if (buttonEnabled) {
                                // xử lý
                            }
                        }
                    )
                }, contentAlignment = Alignment.Center){
            Row(verticalAlignment = Alignment.CenterVertically) {
                icon()
                Text(
                    text = text,
                    modifier = Modifier.padding(start = 12.dp),
                    style = TextStyle(
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = Color(0xFF4B4B4B)
                    )
                )
            }

        }

    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginContent(
        uiState = LoginUiState(),
        onEvent = {},
        onForgetPasswordClick = {},
        onSignUpClick = {}
    )
}
