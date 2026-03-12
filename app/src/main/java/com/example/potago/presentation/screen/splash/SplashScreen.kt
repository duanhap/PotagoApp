package com.example.potago.presentation.screen.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.potago.presentation.navigation.Screen
import com.example.potago.R
import com.example.potago.presentation.ui.theme.Green58
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen (
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel(),
){
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        delay(5000)
        when {
            uiState.loggedIn -> {
                navController.navigate(Screen.MainGraph.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
            else -> {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Green58)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedMascot()
            Spacer(modifier = Modifier.height(28.dp))
            AnimatedIntroText()
            Spacer(modifier = Modifier.height(30.dp))
            LoadingDots()
        }
    }
}
@Composable
fun AnimatedMascot() {

    val scale = remember { Animatable(0f) }
    val rotation = remember { Animatable(-180f) }

    LaunchedEffect(Unit) {
        delay(200) // delay 0.2s giống motion

        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    stiffness = 260f,
                    dampingRatio = 0.6f
                )
            )
        }

        launch {
            rotation.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    stiffness = 260f,
                    dampingRatio = 0.6f
                )
            )
        }
    }

    Image(
        painter = painterResource(R.drawable.normal_mascot),
        contentDescription = null,
        modifier = Modifier
            .size(250.dp)
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
                rotationZ = rotation.value
            }
    )
}

@Composable
fun AnimatedIntroText() {

    val density = LocalDensity.current

    val titleAlpha = remember { Animatable(0f) }
    val titleOffset = remember { Animatable(with(density) { 20.dp.toPx() }) }

    val subtitleAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(400)

        // Title animation song song
        launch {
            titleAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 900)
            )
        }

        launch {
            titleOffset.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 900)
            )
        }

        // Subtitle xuất hiện sau
        delay(500)
        subtitleAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500)
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Potago",
            color = Color.White,
            style = MaterialTheme.typography.displayMedium.copy(
                fontSize = 64.sp,
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.2f),
                    offset = Offset(0f, 10f),
                    blurRadius = 10f
                )
            ),
            modifier = Modifier.graphicsLayer {
                alpha = titleAlpha.value
                translationY = titleOffset.value
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Learn language the fun way!",
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFFDCFCE7),
            modifier = Modifier.graphicsLayer {
                alpha = subtitleAlpha.value
            }
        )
    }
}


@Composable
fun LoadingDots() {

    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(1400)
        alpha.animateTo(1f, tween(500))
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .padding(top = 32.dp)
            .graphicsLayer {
                this.alpha = alpha.value
            }
    ) {
        repeat(3) { index ->
            BouncingDot(delayMillis = index * 200L)
        }
    }
}


@Composable
fun BouncingDot(delayMillis: Long) {

    val infiniteTransition = rememberInfiniteTransition(label = "")

    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -30f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 500,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(delayMillis.toInt())
        ),
        label = ""
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 500,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(delayMillis.toInt())
        ),
        label = ""
    )

    Box(
        modifier = Modifier
            .size(12.dp)
            .graphicsLayer {
                translationY = offsetY
                this.alpha = alpha
            }
            .background(Color.White, CircleShape)
    )
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview(){
    SplashScreen(navController = NavController(LocalContext.current))
}