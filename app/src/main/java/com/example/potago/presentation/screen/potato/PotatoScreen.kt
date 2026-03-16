package com.example.potago.presentation.screen.potato

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.potago.R
import com.example.potago.presentation.navigation.Screen

@Composable
fun PotatoScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                onSettingClick = { navController.navigate(Screen.Setting.route) }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            SectionTitle(text = "Thông tin")
            Spacer(modifier = Modifier.height(12.dp))
            InfoSection()

            Spacer(modifier = Modifier.height(22.dp))
            SectionTitle(text = "Tổng quan")
            Spacer(modifier = Modifier.height(12.dp))
            OverviewSection()

            Spacer(modifier = Modifier.height(22.dp))
            SectionTitle(text = "Tính năng khác")
            Spacer(modifier = Modifier.height(12.dp))
            OtherFeatureSection()

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = Color(0xFF111827)
    )
}

@Composable
private fun InfoSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        DateCard(
            modifier = Modifier.weight(1f),
            label = "Ngày tạo",
            value = "12/12/2025"
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(128.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_potato),
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
                        .background(Color(0xFFDCFCE7)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "C",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF16A34A)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF374151)
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF111827)
            )
        }
    }
}

@Composable
private fun OverviewSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SummaryCard(
            modifier = Modifier.weight(1f),
            iconLabel = "X",
            iconBackground = Color(0xFFFEF3C7),
            iconTextColor = Color(0xFFD97706),
            title = "XP",
            value = "2,450"
        )
        SummaryCard(
            modifier = Modifier.weight(1f),
            iconLabel = "S",
            iconBackground = Color(0xFFDBEAFE),
            iconTextColor = Color(0xFF2563EB),
            title = "Streak",
            value = "12 Days"
        )
    }
}

@Composable
private fun SummaryCard(
    modifier: Modifier = Modifier,
    iconLabel: String,
    iconBackground: Color,
    iconTextColor: Color,
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
                    Text(
                        text = iconLabel,
                        style = MaterialTheme.typography.bodyLarge,
                        color = iconTextColor
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF374151)
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF111827)
            )
        }
    }
}

@Composable
private fun OtherFeatureSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FeatureItem(
            icon = Icons.Default.Person,
            text = "Hồ sơ"
        )
        FeatureItem(
            icon = Icons.Default.Settings,
            text = "Mục tiêu"
        )
    }
}

@Composable
private fun FeatureItem(
    icon: ImageVector,
    text: String
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
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color(0xFF6B7280)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
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
    PotatoScreen(
        navController = rememberNavController()
    )
}