package com.example.potago.presentation.screen.potato

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.annotation.DrawableRes
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.potago.R
import com.example.potago.presentation.navigation.Screen
import com.example.potago.presentation.ui.theme.PotagoTheme
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PotatoScreen(
    navController: NavController,
    viewModel: PotatoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val xpText = NumberFormat.getIntegerInstance(Locale.getDefault()).format(uiState.xp)

    PotatoScreenContent(
        navController = navController,
        xpText = xpText,
        streakText = uiState.streakText,
        createdAtText = uiState.createdAtText
    )
    }


@Composable
private fun PotatoScreenContent(
    navController: NavController,
    xpText: String,
    streakText: String,
    createdAtText: String
) {
    Scaffold(
        topBar = {
            TopAppBar(
                onSettingClick = { navController.navigate(Screen.Setting.route) }
            )
        }
    ) { innerPadding ->
        Box( modifier = Modifier.padding(innerPadding))

        Column(
            modifier = Modifier
                .background(color = Color.White)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Spacer(modifier = Modifier.height(80.dp))
            SectionTitle(text = "Thông tin")
            Spacer(modifier = Modifier.height(12.dp))
            InfoSection(createdAtText = createdAtText)

            Spacer(modifier = Modifier.height(22.dp))
            SectionTitle(text = "Tổng quan")
            Spacer(modifier = Modifier.height(12.dp))
            OverviewSection(
                xpText = xpText,
                streakText = streakText
            )

            Spacer(modifier = Modifier.height(22.dp))
            SectionTitle(text = "Tính năng khác")
            Spacer(modifier = Modifier.height(12.dp))
            OtherFeatureSection(
                onProfileClick = { navController.navigate(Screen.Profile.route) },
                onGoalClick = { navController.navigate(Screen.Goal.route) }

            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = Color(0xFF111827)
    )
}

@Composable
private fun InfoSection(createdAtText: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        DateCard(
            modifier = Modifier.weight(1f),
            label = "Ngày tạo",
            value = createdAtText
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(128.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_garden_potago_screen),
                contentDescription = "Potato illustration",
                modifier = Modifier.size(120.dp)
            )
        }
    }
}

@Composable
private fun DateCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Surface(
        modifier = modifier.height(128.dp),
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFD7FFA4)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_solar_calendar),
                        contentDescription = "Potato illustration",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF374151)
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF111827)
            )
        }
    }
}

@Composable
private fun OverviewSection(
    xpText: String,
    streakText: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SummaryCard(
            modifier = Modifier.weight(1f),
            iconRes = R.drawable.ic_experience_points,
            iconBackground = Color(0xFFFEF3C7),
            title = "XP",
            value = xpText
        )
        SummaryCard(
            modifier = Modifier.weight(1f),
            iconRes = R.drawable.ic_water,
            iconBackground = Color(0xFFDBEAFE),
            title = "Streak",
            value = streakText
        )
    }
}

@Composable
private fun SummaryCard(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    iconBackground: Color,
    title: String,
    value: String
) {
    Surface(
        modifier = modifier.height(128.dp),
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF374151)
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF111827)
            )
        }
    }
}

@Composable
private fun OtherFeatureSection(
    onProfileClick: () -> Unit = {},
    onGoalClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FeatureItem(
            iconRes = R.drawable.ic_iconamoon_profile,
            text = "Hồ sơ",
            onClick = onProfileClick
        )
        FeatureItem(
            iconRes = R.drawable.ic_octicon_goal_16,
            text = "Mục tiêu",
            onClick = onGoalClick
        )
    }
}

@Composable
private fun FeatureItem(
    @DrawableRes iconRes: Int,
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp)
                .clickable { onClick() }, // 👈 chỗ này
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = Color(0xFF4B5563),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF4B5563)
            )
        }
    }
}

@Composable
private fun TopAppBar(
    onSettingClick: () -> Unit = {},
){
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 3.dp,
        shadowElevation = 4.dp,
        color = Color(0xFFFFFFFF)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Potato",
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier.weight(1f)
                )
                SettingButton(onSettingClick)
            }
            HorizontalDivider(color = Color(0xFFE5E7EB))
        }
    }
}
@Composable
private fun SettingButton(
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        label = "icon_scale"
    )

    IconButton(
        onClick = onClick,
        interactionSource = interactionSource
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_setting),
            contentDescription = "Setting",
            modifier = Modifier.scale(scale)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PotatoScreenPreview() {
    PotagoTheme(dynamicColor = false) {
        PotatoScreenContent(
            navController = rememberNavController(),
            xpText = NumberFormat.getIntegerInstance(Locale.getDefault()).format(999),
            streakText = "12 Days",
            createdAtText = "14/03/2026"
        )
    }
}