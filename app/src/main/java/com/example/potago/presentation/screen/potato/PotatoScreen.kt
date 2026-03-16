package com.example.potago.presentation.screen.potato

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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

        Box(
            modifier = Modifier.padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Potato Screen",
                style = MaterialTheme.typography.headlineMedium
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
            SettingButton (onSettingClick)
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