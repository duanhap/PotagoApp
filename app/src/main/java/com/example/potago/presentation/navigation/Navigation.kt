package com.example.potago.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import android.net.Uri
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.potago.presentation.screen.addvideo.AddVideoScreen
import com.example.potago.presentation.screen.auth.LoginScreen
import com.example.potago.presentation.screen.auth.SignUpScreen
import com.example.potago.presentation.screen.detailcoursescreen.DetailCourseScreen
import com.example.potago.presentation.screen.editcoursescreen.EditCourseScreen
import com.example.potago.presentation.screen.goal.GoalScreen
import com.example.potago.presentation.screen.detailedvideoscreen.DetailedVideoScreen
import com.example.potago.presentation.screen.flashcardscreen.FlashCardScreen
import com.example.potago.presentation.screen.home.HomeScreen
import com.example.potago.presentation.screen.library.LibraryScreen
import com.example.potago.presentation.screen.managevideo.ManageVideoScreen
import com.example.potago.presentation.screen.myvideo.MyVideoScreen
import com.example.potago.presentation.screen.potato.PotatoScreen
import com.example.potago.presentation.screen.profile.ProfileScreen
import com.example.potago.presentation.screen.recommendvideo.RecommendVideoScreen
import com.example.potago.presentation.screen.shop.ActiveItemBadge
import com.example.potago.presentation.screen.setting.SettingScreen
import com.example.potago.presentation.screen.matchgame.MatchGameScreen
import com.example.potago.presentation.screen.matchgame.MatchResultScreen
import com.example.potago.presentation.screen.shop.ShopScreen
import com.example.potago.presentation.screen.splash.SplashScreen
import com.example.potago.presentation.screen.streak.StreakScreen
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
    object Goal : Screen("goal")

    object RecommendVideo : Screen("recommend_video")
    object MyVideo : Screen("my_video")
    object ManageVideo : Screen("manage_video")
    object AddVideo : Screen("add_video")
    object Profile : Screen("profile")
    object Shop : Screen("shop")
    object MatchGame : Screen("match_game/{wordSetId}/{wordSetName}") {
        operator fun invoke(wordSetId: Long, wordSetName: String): String {
            val encodedName = android.net.Uri.encode(wordSetName)
            return "match_game/$wordSetId/$encodedName"
        }
    }

    object MatchResult :
        Screen("match_result/{completedTime}/{bestTime}/{bestDate}/{wordSetId}/{wordSetName}") {
        operator fun invoke(
            completedTime: Double,
            bestTime: Double,
            bestDate: String,
            wordSetId: Long,
            wordSetName: String
        ): String {
            val encodedDate = android.net.Uri.encode(bestDate.ifBlank { "-" })
            val encodedName = android.net.Uri.encode(wordSetName)
            return "match_result/$completedTime/$bestTime/$encodedDate/$wordSetId/$encodedName"
        }
    }

    object DetailedVideo : Screen("detailed_video/{videoId}") {
        operator fun invoke(videoId: Int) = "detailed_video/$videoId"
    }

    object FlashCard : Screen("flash_card/{wordSetId}/{wordSetName}") {
        operator fun invoke(wordSetId: Long, wordSetName: String): String {
            val encodedName = Uri.encode(wordSetName)
            return "flash_card/$wordSetId/$encodedName"
        }
    }

    object DetailCourse : Screen("detail_course/{wordSetId}/{wordSetName}") {
        operator fun invoke(wordSetId: Long, wordSetName: String): String {
            val encodedName = Uri.encode(wordSetName)
            return "detail_course/$wordSetId/$encodedName"
        }
    }

    object EditCourse : Screen("edit_course/{wordSetId}/{wordSetName}") {
        operator fun invoke(wordSetId: Long, wordSetName: String): String {
            val encodedName = Uri.encode(wordSetName)
            return "edit_course/$wordSetId/$encodedName"
        }
    }

    object Streak : Screen("streak_screen/{streakCount}") {
        operator fun invoke(streakCount: Int) = "streak_screen/$streakCount"
    }

    object ListOfCards : Screen("list_of_cards/{wordSetId}/{wordSetName}") {
        operator fun invoke(wordSetId: Long, wordSetName: String): String {
            val encodedName = Uri.encode(wordSetName)
            return "list_of_cards/$wordSetId/$encodedName"
        }
    }
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
    val activeItemViewModel: ActiveItemViewModel = hiltViewModel()
    val activeSession by activeItemViewModel.activeSession.collectAsState()
    var remainingMs by remember { mutableStateOf(0L) }
    var badgeOffset by remember { mutableStateOf(androidx.compose.ui.geometry.Offset(-16f, 200f)) }
    var badgeExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(activeSession) {
        while (true) {
            remainingMs = activeSession?.remainingMs ?: 0L
            kotlinx.coroutines.delay(1000L)
        }
    }

    val bottomBarScreens = listOf(
        Screen.Home.route,
        Screen.Library.route,
        Screen.Video.route,
        Screen.Potato.route
    )

    Box(modifier = Modifier.fillMaxSize()) {
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
                composable(
                    route = Screen.Library.route,
                    popEnterTransition = { fadeIn(tween(250)) }) {
                    LibraryScreen(mainNavController)
                }
                composable(Screen.Video.route) {
                    VideoScreen(mainNavController)
                }
                composable(Screen.Potato.route) {
                    PotatoScreen(mainNavController)
                }
                composable(Screen.Setting.route) {
                    SettingScreen(
                        navController = mainNavController,
                        rootNavController = rootNavController // Truyền rootNavController vào đây
                    )
                }
                composable(Screen.Goal.route) {
                    GoalScreen(mainNavController)
                }
                composable(Screen.Profile.route) {
                    ProfileScreen(mainNavController)
                }
                composable(Screen.Shop.route) {
                    ShopScreen(mainNavController)
                }
                composable(
                    route = Screen.MatchGame.route,
                    arguments = listOf(
                        navArgument("wordSetId") { type = NavType.LongType },
                        navArgument("wordSetName") { type = NavType.StringType }
                    ),
                    enterTransition = { fadeIn(tween(250)) },
                    exitTransition = { fadeOut(tween(250)) }
                ) { backStackEntry ->
                    val wordSetId = backStackEntry.arguments?.getLong("wordSetId") ?: 0L
                    val wordSetName = backStackEntry.arguments?.getString("wordSetName")
                        ?.let(Uri::decode) ?: ""
                    MatchGameScreen(
                        navController = mainNavController,
                        wordSetId = wordSetId,
                        wordSetName = wordSetName
                    )
                }
                composable(
                    route = Screen.MatchResult.route,
                    arguments = listOf(
                        navArgument("completedTime") { type = NavType.FloatType },
                        navArgument("bestTime") { type = NavType.FloatType },
                        navArgument("bestDate") { type = NavType.StringType },
                        navArgument("wordSetId") { type = NavType.LongType },
                        navArgument("wordSetName") { type = NavType.StringType }
                    ),
                    enterTransition = { fadeIn(tween(300)) }
                ) { backStackEntry ->
                    val completedTime =
                        backStackEntry.arguments?.getFloat("completedTime")?.toDouble() ?: 0.0
                    val bestTime = backStackEntry.arguments?.getFloat("bestTime")?.toDouble() ?: 0.0
                    val bestDate = backStackEntry.arguments?.getString("bestDate")
                        ?.let(Uri::decode)?.takeIf { it != "-" } ?: ""
                    val wordSetId = backStackEntry.arguments?.getLong("wordSetId") ?: 0L
                    val wordSetName = backStackEntry.arguments?.getString("wordSetName")
                        ?.let(Uri::decode) ?: ""
                    MatchResultScreen(
                        navController = mainNavController,
                        completedTime = completedTime,
                        bestTime = bestTime,
                        bestDate = bestDate,
                        wordSetId = wordSetId,
                        wordSetName = wordSetName
                    )
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
                composable(
                    route = Screen.DetailedVideo.route,
                    arguments = listOf(navArgument("videoId") { type = NavType.IntType })
                ) {
                    DetailedVideoScreen(mainNavController)
                }
                composable(
                    route = Screen.FlashCard.route,
                    arguments = listOf(
                        navArgument("wordSetId") { type = NavType.LongType },
                        navArgument("wordSetName") { type = NavType.StringType }
                    ),
                    enterTransition = {
                        fadeIn(tween(250))
                    },
                    exitTransition = {
                        fadeOut(tween(250))
                    },
                    popEnterTransition = {
                        slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Down)
                    },
                    popExitTransition = {
                        slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
                    }
                ) { backStackEntry ->
                    val wordSetName = backStackEntry.arguments
                        ?.getString("wordSetName")
                        ?.let(Uri::decode)
                        ?.takeIf { it.isNotBlank() }
                        ?: "Học phần"
                    val wordSetId = backStackEntry.arguments?.getLong("wordSetId") ?: 0L
                    FlashCardScreen(
                        navController = mainNavController,
                        wordSetId = wordSetId,
                        wordSetName = wordSetName
                    )
                }
                composable(
                    route = Screen.DetailCourse.route,
                    arguments = listOf(
                        navArgument("wordSetId") { type = NavType.LongType },
                        navArgument("wordSetName") { type = NavType.StringType }
                    ),
                    enterTransition = {
                        if (initialState.destination.route == Screen.FlashCard.route) {
                            fadeIn(tween(250))
                        } else {
                            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right)
                        }
                    },
                    exitTransition = {
                        if (targetState.destination.route == Screen.FlashCard.route) {
                            //slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down)
                            fadeOut(tween(250))
                        } else {
                            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
                        }
                    },
                    popEnterTransition = {
                        fadeIn(tween(250))
                    },
                    popExitTransition = {
                        slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down) + fadeOut(
                            tween(250)
                        )
                    }
                ) { backStackEntry ->
                    val wordSetName = backStackEntry.arguments
                        ?.getString("wordSetName")
                        ?.let(Uri::decode)
                        ?.takeIf { it.isNotBlank() }
                        ?: "Học phần"
                    val wordSetId = backStackEntry.arguments?.getLong("wordSetId") ?: 0L
                    DetailCourseScreen(
                        navController = mainNavController,
                        wordSetId = wordSetId,
                        wordSetName = wordSetName
                    )
                }
                composable(
                    route = Screen.EditCourse.route,
                    arguments = listOf(
                        navArgument("wordSetId") { type = NavType.LongType },
                        navArgument("wordSetName") { type = NavType.StringType }
                    ),
                    popExitTransition = {
                        slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
                    }
                ) { backStackEntry ->
                    val wordSetName = backStackEntry.arguments
                        ?.getString("wordSetName")
                        ?.let(Uri::decode)
                        ?.takeIf { it.isNotBlank() }
                        ?: ""
                    val wordSetId = backStackEntry.arguments?.getLong("wordSetId") ?: 0L
                    EditCourseScreen(
                        navController = mainNavController,
                        wordSetId = wordSetId,
                        initialTitle = wordSetName
                    )
                }
                composable(
                    route = Screen.Streak.route,
                    arguments = listOf(navArgument("streakCount") { type = NavType.IntType })
                ) { backStackEntry ->
                    val streakCount = backStackEntry.arguments?.getInt("streakCount") ?: 1
                    StreakScreen(
                        navController = mainNavController,
                        streakCount = streakCount
                    )
                }
                composable(
                    route = Screen.ListOfCards.route,
                    arguments = listOf(
                        navArgument("wordSetId") { type = NavType.LongType },
                        navArgument("wordSetName") { type = NavType.StringType }
                    ),
                    enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up) },
                    exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down) }
                ) { backStackEntry ->
                    val wordSetName = backStackEntry.arguments
                        ?.getString("wordSetName")
                        ?.let(Uri::decode)
                        ?.takeIf { it.isNotBlank() }
                        ?: ""
                    val wordSetId = backStackEntry.arguments?.getLong("wordSetId") ?: 0L
                    com.example.potago.presentation.screen.listofcardsscreen.ListOfCardsScreen(
                        navController = mainNavController,
                        wordSetId = wordSetId,
                        wordSetName = wordSetName
                    )
                }
            }
        }

        // Active item badge — float trên toàn bộ MainGraph, draggable
        if (activeSession != null && remainingMs > 0L) {
            ActiveItemBadge(
                session = activeSession!!,
                remainingMs = remainingMs,
                expanded = badgeExpanded,
                onClick = { badgeExpanded = !badgeExpanded },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset {
                        IntOffset(
                            badgeOffset.x.toInt(),
                            badgeOffset.y.toInt()
                        )
                    }
                    .pointerInput(Unit) {
                        detectDragGestures { _, dragAmount ->
                            badgeOffset = Offset(
                                badgeOffset.x + dragAmount.x,
                                badgeOffset.y + dragAmount.y
                            )
                        }
                    }
                    .padding(top = 40.dp)
            )
        }
    }
}
