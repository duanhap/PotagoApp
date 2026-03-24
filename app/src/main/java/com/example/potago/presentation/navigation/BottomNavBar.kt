package com.example.potago.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.potago.R

sealed class BottomNavItem(val route: String, val icon: Int, val label: String) {
    object Home : BottomNavItem("home", R.drawable.ic_home, "Home")
    object Library : BottomNavItem("library", R.drawable.ic_library, "Library")
    object Video : BottomNavItem("video", R.drawable.ic_video, "Video")
    object Potato : BottomNavItem("potato", R.drawable.ic_potato, "Potato")
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Library,
    BottomNavItem.Video,
    BottomNavItem.Potato
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavBar(
    navController: NavController,
    currentRoute: String?,
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp, // Loại bỏ bóng đổ mặc định nếu muốn phẳng hoàn toàn
        modifier = Modifier.shadow(10.dp)
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            CompositionLocalProvider(
                LocalRippleConfiguration provides null
            ) {
                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        if (!selected) {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        Box(
                            modifier = if (selected) {
                                Modifier
                                    .width(68.dp)
                                    .height(56.dp)// Kích thước khung nền xanh
                                    .background(
                                        color = Color(0xFFEEF4FF), // Màu xanh nhạt của background
                                        shape = RoundedCornerShape(16.dp)
                                    )
                            } else {
                                Modifier.size(56.dp)
                            },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = item.label,
                                modifier = Modifier.size(30.dp), // Kích thước icon
                                tint = if (selected) Color(0xFF4A80F0) else Color(0xFF94A3B8) // Màu icon xanh đậm hoặc xám
                            )
                        }
                    },
                    label = null, // Không hiển thị text label
                    alwaysShowLabel = false,
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent // Tắt indicator mặc định để dùng Box tùy chỉnh
                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun BottomNavBarPreview() {
    BottomNavBar(navController = NavController(LocalContext.current), currentRoute = "home")
}