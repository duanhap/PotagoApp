package com.example.potago.presentation.screen.streak

import android.app.Activity
import android.media.SoundPool
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import com.example.potago.R
import kotlinx.coroutines.delay
import kotlin.random.Random

val Nunito = FontFamily(
    Font(R.font.nunito_extrabold, FontWeight.ExtraBold),
    Font(R.font.nunito_bold, FontWeight.Bold),
    Font(R.font.nunito_semibold, FontWeight.SemiBold),
    Font(R.font.nunito_medium, FontWeight.Medium),
    Font(R.font.nunito_regular, FontWeight.Normal),
    Font(R.font.nunito_light, FontWeight.Light),
    Font(R.font.nunito_extralight, FontWeight.ExtraLight),
    Font(R.font.nunito_black, FontWeight.Black)
)

@OptIn(ExperimentalTextApi::class)
@Composable
fun StreakScreen(
    navController: NavController? = null,
    streakCount: Int = 13,
    isSickTest: Boolean = true
) {
    val view = LocalView.current
    val context = LocalContext.current

    val soundPool = remember {
        SoundPool.Builder()
            .setMaxStreams(3)
            .build()
    }

    val waterdropmp3 = remember {
        soundPool.load(context, R.raw.waterdrop, 1)
    }
    val haoquangmp3 = remember {
        soundPool.load(context, R.raw.haoquang, 3)
    }
    val taonchua = remember {
        soundPool.load(context, R.raw.taonchua, 4)
    }
    DisposableEffect(Unit) {
        onDispose {
            soundPool.autoPause()
        }
    }

    // Logic ẩn cả Status Bar và Navigation Bar
    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window ?: return@DisposableEffect onDispose {}
        val insetsController = WindowCompat.getInsetsController(window, view)

        insetsController.hide(WindowInsetsCompat.Type.systemBars())
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        onDispose {
            insetsController.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    // Tự động back sau 5 giây
    LaunchedEffect(Unit) {
        delay(6800L) // Thay đổi từ 20000L thành 5000L theo yêu cầu
        navController?.popBackStack()
    }

    // --- Animation Logic ---
    var displayedCount by remember { mutableIntStateOf(streakCount - 1) }
    val numberScale = remember { Animatable(1f) }
    var isSick  by remember { mutableStateOf(isSickTest)}
    var isVisible by  remember { mutableStateOf(false)}
    LaunchedEffect(streakCount) {
        delay(500)
        soundPool.play(taonchua, 0.4f, 0.4f, 0, 0, 1f)
        delay(300)
        isVisible = !isVisible
        soundPool.play(waterdropmp3, 0.7f, 0.7f, 0, 0, 1f)
        delay(1000) // Chờ hiệu ứng hào quang xoay một chút
        // Hiệu ứng "Pop" tăng số
        numberScale.animateTo(
            targetValue = 1.9f,
            animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
        )
        isSick = !isSick
        displayedCount = streakCount
        soundPool.play(haoquangmp3, 1f, 1f, 0, 0, 0.5f)
        numberScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )

    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors =
                        if (!isSick) listOf(
                            Color(0xFF6784B3),
                            Color(0xFF303D53),
                            Color(0xFF1E2634),
                            Color(0xFF1E2634)
                        )
                        else listOf(Color(0xFF1E2634), Color(0xFF1E2634))
                )
            )
    ) {

        if (!isSick) {
            // Custom Rotating Lighting Effect
            RotatingLighting(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
                    .align(Alignment.TopCenter)
            )
        } else {

            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() ,
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_sick_mascot_streak_screen),
                    contentDescription = null,
                    modifier = Modifier.size(350.dp)
                )
            }
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .height(320.dp)
                    .fillMaxWidth()
                    .alpha(if (isSick) 0f else 1f),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_streak_mascot),
                    contentDescription = "Streak Mascot",
                    modifier = Modifier.size(250.dp)
                )
            }
            Spacer(modifier = Modifier.height(30.dp))


            // Streak Count Section with "Pop" Animation
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.graphicsLayer {
                    scaleX = numberScale.value
                    scaleY = numberScale.value
                }
            ) {
                // Glow layer
                Text(
                    text = displayedCount.toString(),
                    style = TextStyle(
                        fontSize = 128.sp,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        shadow = Shadow(
                            color = Color(0x33FFFFFF),
                            offset = Offset(0f, 0f),
                            blurRadius = 60f
                        )
                    ),
                    color = Color.Transparent
                )

                // Stroke layer
                Text(
                    text = displayedCount.toString(),
                    style = TextStyle(
                        fontSize = 128.sp,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        drawStyle = Stroke(
                            width = 18f,
                            join = StrokeJoin.Round
                        )
                    ),
                    color = Color(0xFF6FC6FF)
                )

                // Fill layer
                Text(
                    text = displayedCount.toString(),
                    style = TextStyle(
                        fontSize = 128.sp,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF5B99FF), Color(0xFF3B82F6), Color(0xFF234C90))
                        )
                    )
                )

                // Small water icon
                Image(
                    painter = painterResource(id = R.drawable.ic_water_on),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.TopEnd) // 👈 luôn nằm góc trên phải
                        .padding(top = 8.dp, end = 8.dp) // 👈 tinh chỉnh khoảng cách
                        .size(46.dp)
                        .offset(x = 50.dp, y = 7.dp)
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Streak Text Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_dropping_water),
                    contentDescription = null,
                    modifier = Modifier.size(95.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Box(contentAlignment = Alignment.Center) {
                    // Glow layer
                    Text(
                        text = "streak",
                        style = TextStyle(
                            fontSize = 75.sp,
                            fontFamily = Nunito,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            shadow = Shadow(
                                color = Color(0x33FFFFFF),
                                offset = Offset(0f, 0f),
                                blurRadius = 20f
                            )
                        ),
                        color = Color.Transparent
                    )

                    // Stroke layer
                    Text(
                        text = "streak",
                        style = TextStyle(
                            fontSize = 75.sp,
                            fontFamily = Nunito,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            drawStyle = Stroke(
                                width = 12f,
                                join = StrokeJoin.Round
                            )
                        ),
                        color = Color(0xFF6FC6FF)
                    )
                    // Fill layer
                    Text(
                        text = "streak",
                        style = TextStyle(
                            fontSize = 75.sp,
                            fontFamily = Nunito,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF5B99FF),
                                    Color(0xFF3B82F6),
                                    Color(0xFF234C90)
                                )
                            )
                        )
                    )
                }
            }
        }
    }
}
@Composable
fun RotatingLighting(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "lighting")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(45000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Canvas(modifier = modifier.offset(y = 70.dp)) {
        val center = center
        val radius = size.maxDimension * 1.2f
        val rayCount = 16
        val anglePerRay = 360f / rayCount

        rotate(rotation) {
            for (i in 0 until rayCount) {
                if (i % 2 == 0) {
                    val path = Path().apply {
                        moveTo(center.x, center.y)
                        arcTo(
                            rect = androidx.compose.ui.geometry.Rect(
                                center.x - radius,
                                center.y - radius,
                                center.x + radius,
                                center.y + radius
                            ),
                            startAngleDegrees = i * anglePerRay,
                            sweepAngleDegrees = anglePerRay / 1.2f,
                            forceMoveTo = false
                        )
                        close()
                    }
                    drawPath(
                        path = path,
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.22f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = radius * 0.75f
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StreakScreenPreview() {
    StreakScreen(isSickTest = true)
}

@Preview(showBackground = true)
@Composable
fun StreakScreenPreview2() {
    StreakScreen(isSickTest = false)
}

