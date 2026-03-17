package com.example.potago.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.potago.presentation.screen.addvideo.AddVideoScreen
import com.example.potago.presentation.screen.auth.LoginScreen
import com.example.potago.presentation.screen.auth.SignUpScreen
import com.example.potago.presentation.screen.home.HomeScreen
import com.example.potago.presentation.screen.library.LibraryScreen
import com.example.potago.presentation.screen.managevideo.ManageVideoScreen
import com.example.potago.presentation.screen.myvideo.MyVideoScreen
import com.example.potago.presentation.screen.potato.PotatoScreen
import com.example.potago.presentation.screen.recommendvideo.RecommendVideoScreen
import com.example.potago.presentation.screen.setting.SettingScreen
import com.example.potago.presentation.screen.splash.SplashScreen
import com.example.potago.presentation.screen.video.VideoScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    
    // Auth Flow
    object AuthGraph : Screen("auth_graph")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    
    // Main Flow
    object MainGraph : Screen("main_graph")
    object Home : Screen("home")
    object Library : Screen("library")
    object Video : Screen("video")
    object Potato : Screen("potato")
    object Setting : Screen("setting")

    object RecommendVideo : Screen("recommend_video")
    object MyVideo : Screen("my_video")
    object ManageVideo : Screen("manage_video")
    object AddVideo : Screen("add_video")
}

@Composable
fun Navigation() {
    val rootNavController = rememberNavController()

    NavHost(
        navController = rootNavController,
        startDestination = Screen.Splash.route,
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left) },
        exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left) }
    ) {
        // Màn hình Splash
        composable(Screen.Splash.route) {
            SplashScreen(rootNavController)
        }

        // Auth Flow: Không chứa trong Scaffold
        navigation(
            route = Screen.AuthGraph.route,
            startDestination = Screen.Login.route
        ) {
            composable(Screen.Login.route) {
                LoginScreen(rootNavController)
            }
            composable(Screen.SignUp.route) {
                SignUpScreen(rootNavController)
            }
        }

        // Main Flow: Chứa trong Scaffold (có BottomNavBar)
        composable(Screen.MainGraph.route) {
            MainFlowContainer(rootNavController)
        }
    }
}

@Composable
fun MainFlowContainer(rootNavController: NavController) {
    val mainNavController = rememberNavController()
    val navBackStackEntry by mainNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarScreens = listOf(
        Screen.Home.route,
        Screen.Library.route,
        Screen.Video.route,
        Screen.Potato.route
    )

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomBarScreens) {
                BottomNavBar(
                    navController = mainNavController,
                    currentRoute = currentRoute
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = mainNavController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left) },
            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left) }
        ) {
            composable(Screen.Home.route) {
                HomeScreen(mainNavController)
            }
            composable(Screen.Library.route) {
                LibraryScreen(mainNavController)
            }
            composable(Screen.Video.route) {
                VideoScreen(mainNavController)
            }
            composable(Screen.Potato.route) {
                PotatoScreen(mainNavController)
            }
            composable(Screen.Setting.route) {
                SettingScreen(rootNavController)
            }
            composable(Screen.RecommendVideo.route) {
                RecommendVideoScreen(mainNavController)
            }
            composable(Screen.MyVideo.route) {
                MyVideoScreen(mainNavController)
            }
            composable(Screen.ManageVideo.route) {
                ManageVideoScreen(mainNavController)
            }
            composable(Screen.AddVideo.route) {
                AddVideoScreen(mainNavController)
            }
        }
    }
}
