package com.example.potago.presentation.screen.goal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.foundation.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.potago.R
import com.example.potago.presentation.screen.auth.BigPotagoButton

// ─── Dữ liệu lựa chọn XP ────────────────────────────────────────────────────
private val xpOptions = listOf(15, 30, 60, 120, 240)

// ─────────────────────────────────────────────────────────────────────────────
// Goal Screen
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun GoalScreen(
    navController: NavController
) {
    var selectedXp by remember { mutableStateOf(xpOptions.first()) }
    var isDropdownOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { GoalTopBar(onBackClick = { navController.popBackStack() }) },
        containerColor = Color.White
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            // ── Top: Label + Dropdown (float tự do, mở xuống không đẩy gì) ──
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Điểm kinh nghiệm mỗi ngày",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                XpDropdown(
                    selectedXp = selectedXp,
                    isOpen = isDropdownOpen,
                    options = xpOptions,
                    onToggle = { isDropdownOpen = !isDropdownOpen },
                    onSelect = { xp ->
                        selectedXp = xp
                        isDropdownOpen = false
                    }
                )
            }

            // ── Bottom: Mascot + Button luôn cố định ở dưới ────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                MascotWithTooltip()

                Spacer(modifier = Modifier.height(32.dp))

                BigPotagoButton(
                    text = "LƯU",
                    enabled = true,
                    isLoading = false,
                    onClick = { /* TODO: save goal */ }
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Custom XP Dropdown
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun XpDropdown(
    selectedXp: Int,
    isOpen: Boolean,
    options: List<Int>,
    onToggle: () -> Unit,
    onSelect: (Int) -> Unit
) {
    val arrowRotation by animateFloatAsState(
        targetValue = if (isOpen) 180f else 0f,
        animationSpec = tween(200),
        label = "arrow_rotation"
    )

    val borderColor = if (isOpen) Color(0xFF89E219) else Color(0xFFE5E7EB)
    val topShape = if (isOpen)
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    else
        RoundedCornerShape(16.dp)

    Column(modifier = Modifier.fillMaxWidth()) {
        // ── Trigger row ─────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(53.dp)
                .border(2.dp, borderColor, topShape)
                .clip(topShape)
                .background(Color(0xFFF9FAFB))
                .clickable { onToggle() }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$selectedXp xp",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = if (isOpen) Color(0xCC000000) else Color(0xFFCCCCCC)
                )
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = if (isOpen) "Đóng" else "Mở",
                tint = Color(0xFF9CA3AF),
                modifier = Modifier
                    .size(20.dp)
                    .rotate(arrowRotation)
            )
        }

        // ── Dropdown list ────────────────────────────────────────────────
        AnimatedVisibility(
            visible = isOpen,
            enter = expandVertically(animationSpec = tween(200)),
            exit = shrinkVertically(animationSpec = tween(200))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = Color(0xFF89E219),
                        shape = RoundedCornerShape(
                            bottomStart = 16.dp,
                            bottomEnd = 16.dp
                        )
                    )
                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                    .background(Color.White)
            ) {
                options.forEachIndexed { index, xp ->
                    XpOptionItem(
                        xp = xp,
                        isSelected = xp == options.firstOrNull(),
                        isHighlighted = index % 2 == 0, // highlight rows đan xen như Figma
                        onClick = { onSelect(xp) }
                    )
                }
            }
        }
    }
}

@Composable
private fun XpOptionItem(
    xp: Int,
    isSelected: Boolean,
    isHighlighted: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isHighlighted) Color(0xFFF3F4F6) else Color.White

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$xp xp",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color(0xCC000000)
            )
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Mascot + Tooltip
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun MascotWithTooltip() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        // Mascot
        Image(
            painter = painterResource(id = R.drawable.ic_mascot_happy),
            contentDescription = "Mascot",
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Speech bubble tooltip
        Box(
            modifier = Modifier
                .weight(1f)
                .border(
                    width = 1.dp,
                    color = Color(0xFFE5E7EB),
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomEnd = 16.dp,
                        bottomStart = 0.dp
                    )
                )
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomEnd = 16.dp,
                        bottomStart = 0.dp
                    )
                )
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Text(
                text = "Điểm này được xét để đạt được streak mỗi ngày. Lựa sức mình nha!",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFF4B5563),
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Top App Bar
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun GoalTopBar(onBackClick :() -> Unit) {
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
            GoalBackButton(onClick = onBackClick)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Mục tiêu",
                style = MaterialTheme.typography.displayMedium
            )
        }
    }
}

@Composable
private fun GoalBackButton(onClick: () -> Unit) {
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
                .size(20.dp)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview
// ─────────────────────────────────────────────────────────────────────────────
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GoalScreenPreview() {
    MaterialTheme {
        GoalScreen(navController = NavController(LocalContext.current))
    }
}

//@Preview(showBackground = true, showSystemUi = true, name = "Goal - Dropdown Open")
//@Composable
//fun GoalScreenDropdownOpenPreview() {
//    var selectedXp by remember { mutableStateOf(15) }
//    MaterialTheme {
//        Scaffold(
//            topBar = { GoalTopBar(onBackClick = {navController.popBackStack() }) },
//            containerColor = Color.White
//        ) { padding ->
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(padding)
//            ) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(horizontal = 20.dp)
//                ) {
//                    Spacer(modifier = Modifier.height(24.dp))
//                    Text(
//                        text = "Điểm kinh nghiệm mỗi ngày",
//                        style = MaterialTheme.typography.titleMedium
//                    )
//                    Spacer(modifier = Modifier.height(12.dp))
//                    XpDropdown(
//                        selectedXp = selectedXp,
//                        isOpen = true,
//                        options = xpOptions,
//                        onToggle = {},
//                        onSelect = { selectedXp = it }
//                    )
//                }
//            }
//        }
//    }
//}
