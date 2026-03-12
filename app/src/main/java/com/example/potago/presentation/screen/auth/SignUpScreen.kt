package com.example.potago.presentation.screen.auth

import android.annotation.SuppressLint
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
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
fun SignUpScreen(
    navController: NavController,
    signUpViewModel: SignUpViewModel = hiltViewModel()
) {
    val uiState by signUpViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        signUpViewModel.uiEvent.collect { event ->
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
        SignUpContent(
            uiState = uiState,
            onEvent = signUpViewModel::onEvent,
            onLoginClick = {
                navController.navigate(Screen.Login.route){
                    popUpTo(Screen.SignUp.route){
                        inclusive = true
                    }
                }
            }
        )

    }

}
@Composable
fun SignUpContent(
    uiState: SignUpUiState,
    onEvent: (SignUpEvent) -> Unit,
    onLoginClick: () -> Unit
) {
    val isLoading = uiState.authState is UiState.Loading
    val createAccountButtonEnabled = uiState.isCreateAccountButtonEnabled && !isLoading
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
            Spacer(modifier = Modifier.height(60.dp))
            MascotAndBubble()
            Text(
                modifier = Modifier.offset(y= -20.dp),
                text = "Create your potato",
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(10.dp))
            NameField(
                value = uiState.name,
                onValueChange = { onEvent(SignUpEvent.NameChanged(it)) }
            )
            Spacer(modifier = Modifier.height(5.dp))
            EmailField(
                value = uiState.email,
                onValueChange = { onEvent(SignUpEvent.EmailChanged(it)) }
            )
            Spacer(modifier = Modifier.height(5.dp))
            PasswordField(
                value = uiState.password,
                isPasswordVisible = uiState.isPasswordVisible,
                onValueChange = { onEvent(SignUpEvent.PasswordChanged(it)) },
                onTogglePasswordVisibility = { onEvent(SignUpEvent.TogglePasswordVisibility) }
            )
            Spacer(modifier = Modifier.height(5.dp))
            PasswordField(
                type = "CONFIRM PASSWORD",
                value = uiState.confirmPassword,
                isPasswordVisible = uiState.isConfirmPasswordVisible,
                onValueChange = { onEvent(SignUpEvent.ConfirmPasswordChanged(it)) },
                onTogglePasswordVisibility = { onEvent(SignUpEvent.ToggleConfirmPasswordVisibility) }
            )
            Spacer(modifier = Modifier.height(20.dp))
            BigPotagoButton(
                text = "CREATE ACCOUNT",
                enabled = createAccountButtonEnabled,
                isLoading = isLoading,
                onClick = { onEvent(SignUpEvent.Submit) }
            )
            Spacer(modifier = Modifier.height(24.dp))

            SocialLoginSection()

            Spacer(modifier = Modifier.height(40.dp))

            LoginSection (onLoginClick = onLoginClick)

            Spacer(modifier = Modifier.height(40.dp))



        }
    }
}
@Composable
private fun MascotAndBubble(){
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
    val rotation by mascotTransition.animateFloat(
        transitionSpec = { tween(400) },
        label = "mascot_alpha"
    ) { state ->
        if (state) -90f else 0f
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
                .offset(x = (-130).dp, y = (-10).dp)
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
                        bottomStart = 12.dp
                    )
                )
                .border(
                    1.2.dp,
                    Color(0xFFE5E7EB),
                    RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = 12.dp
                    )
                )
                .padding(horizontal = 15.dp, vertical = 6.dp)
        ) {
            Text(
                text = "Join with us!",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF4B5563)
            )
        }

        // 🐣 Mascot
        Image(
            painter = painterResource(id = R.drawable.love_mascot),
            contentDescription = "Mascot",
            modifier = Modifier
                .size(160.dp)
                .graphicsLayer {
                    scaleX = mascotScale
                    scaleY = mascotScale
                    alpha = mascotAlpha
                    rotationZ = rotation
                }
        )
    }
}
@Composable
fun NameField(
    value: String,
    onValueChange: (String) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "NAME",
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
                    "your name",
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
                    painter = painterResource(id = R.drawable.ic_potato),
                    contentDescription = null,
                    tint = if (isFocused) Color(0xFF89E219) else Color.LightGray
                )
            },
            singleLine = true
        )
    }
}
@Composable
private fun LoginSection(
    onLoginClick: () -> Unit
) {
    val annotatedString = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = Color.Gray,
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold
            )
        ) {
            append("Already have an account? ")
        }
        withStyle(
            style = SpanStyle(
                color = Color(0xFF58CC02),
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold
            )
        ) {
            append("LOG IN")
        }
    }

    Text(
        text = annotatedString,
        modifier = Modifier.clickable { onLoginClick() },
        fontSize = 14.sp
    )
}
@Preview(showBackground = true)
@Composable
fun SignUpPreview(){
    SignUpScreen(navController = NavController(LocalContext.current))
}