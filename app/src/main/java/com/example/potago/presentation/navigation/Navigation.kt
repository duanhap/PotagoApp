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
import com.example.potago.presentation.screen.addcartscreen.AddCardScreen
import com.example.potago.presentation.screen.addvideo.AddVideoScreen
import com.example.potago.presentation.screen.editcardscreen.EditCardScreen
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
import com.example.potago.presentation.screen.ranking.RankScreen
import com.example.potago.presentation.screen.setting.SettingScreen
import com.example.potago.presentation.screen.createsentencepattern.CreateSentencePatternScreen
import com.example.potago.presentation.screen.createwordset.CreateWordSetScreen
import com.example.potago.presentation.screen.matchgame.MatchGameScreen
import com.example.potago.presentation.screen.matchgame.MatchResultScreen
import com.example.potago.presentation.screen.detailsentencepatternscreen.DetailSentencePatternScreen
import com.example.potago.presentation.screen.detailsentencepatternscreen.AddSentenceScreen
import com.example.potago.presentation.screen.detailsentencepatternscreen.DeleteDetailScreen
import com.example.potago.presentation.screen.detailsentencepatternscreen.EditDetailScreen
import com.example.potago.presentation.screen.detailsentencepatternscreen.EditSentenceScreen
import com.example.potago.presentation.screen.detailsentencepatternscreen.EditSentenceScreen
import com.example.potago.presentation.screen.wordordering.WordOrderingScreen
import com.example.potago.presentation.screen.wordordering.WordOrderingResultScreen
import com.example.potago.presentation.screen.detailsentencepatternscreen.ListOfDetailScreen
import com.example.potago.presentation.screen.writingpracticescreen.WritingPracticeScreen
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
    object CreateWordSet : Screen("create_word_set")
    object CreateSentencePattern : Screen("create_sentence_pattern")
    object Rank : Screen("rank")
    object DetailSentencePattern :
        Screen("detail_sentence_pattern/{sentencePatternId}/{sentencePatternName}") {
        operator fun invoke(sentencePatternId: Int, sentencePatternName: String): String {
            val encodedName = android.net.Uri.encode(sentencePatternName)
            return "detail_sentence_pattern/$sentencePatternId/$encodedName"
        }
    }

    object DeleteDetail : Screen("delete_detail/{sentencePatternId}") {
        operator fun invoke(sentencePatternId: Int) = "delete_detail/$sentencePatternId"
    }

    object EditDetail : Screen("edit_detail/{sentencePatternId}") {
        operator fun invoke(sentencePatternId: Int) = "edit_detail/$sentencePatternId"
    }

    object WordOrdering : Screen("word_ordering/{patternId}/{patternName}") {
        operator fun invoke(patternId: Int, patternName: String): String {
            val encodedName = android.net.Uri.encode(patternName)
            return "word_ordering/$patternId/$encodedName"
        }
    }
    object WordOrderingResult : Screen("word_ordering_result/{correctCount}/{totalCount}/{completedTime}/{xpEarned}/{diamondEarned}/{hackXp}/{superXp}") {
        operator fun invoke(correctCount: Int, totalCount: Int, completedTime: Double = 0.0, xpEarned: Int = 0, diamondEarned: Int = 0, hackXp: Boolean = false, superXp: Boolean = false): String {
            val timeFloat = completedTime.toFloat()
            return "word_ordering_result/$correctCount/$totalCount/$timeFloat/$xpEarned/$diamondEarned/$hackXp/$superXp"
        }
    }
    object ListOfDetail : Screen("list_of_detail/{patternId}") {
        operator fun invoke(patternId: Int) = "list_of_detail/$patternId"
    }
    object EditSentence : Screen("edit_sentence/{sentenceId}") {
        operator fun invoke(sentenceId: Int) = "edit_sentence/$sentenceId"
    }
    object AddSentence : Screen("add_sentence/{patternId}") {
        operator fun invoke(patternId: Int) = "add_sentence/$patternId"
    }
    object WritingPractice : Screen("writing_practice/{patternId}") {
        operator fun invoke(patternId: Int) = "writing_practice/$patternId"
    }
    object MatchGame : Screen("match_game/{wordSetId}/{wordSetName}") {
        operator fun invoke(wordSetId: Long, wordSetName: String): String {
            val encodedName = android.net.Uri.encode(wordSetName)
            return "match_game/$wordSetId/$encodedName"
        }
    }

    object MatchResult :
        Screen("match_result/{completedTime}/{bestTime}/{bestDate}/{wordSetId}/{wordSetName}/{hackXp}/{superXp}") {
        operator fun invoke(
            completedTime: Double,
            bestTime: Double,
            bestDate: String,
            wordSetId: Long,
            wordSetName: String,
            hackXp: Boolean = false,
            superXp: Boolean = false
        ): String {
            val encodedDate = android.net.Uri.encode(bestDate.ifBlank { "-" })
            val encodedName = android.net.Uri.encode(wordSetName)
            return "match_result/$completedTime/$bestTime/$encodedDate/$wordSetId/$encodedName/$hackXp/$superXp"
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

    object Streak : Screen("streak_screen/{streakCount}?nextRoute={nextRoute}") {
        operator fun invoke(streakCount: Int, nextRoute: String? = null): String {
            val base = "streak_screen/$streakCount"
            return if (nextRoute != null) "$base?nextRoute=${android.net.Uri.encode(nextRoute)}" else base
        }
    }

    object ListOfCards : Screen("list_of_cards/{wordSetId}/{wordSetName}") {
        operator fun invoke(wordSetId: Long, wordSetName: String): String {
            val encodedName = Uri.encode(wordSetName)
            return "list_of_cards/$wordSetId/$encodedName"
        }
    }

    object EditCard : Screen("edit_card/{cardId}") {
        operator fun invoke(cardId: Long) = "edit_card/$cardId"
    }

    object AddCard : Screen("add_card/{wordSetId}") {
        operator fun invoke(wordSetId: Long) = "add_card/$wordSetId"
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
                composable(Screen.CreateWordSet.route) {
                    CreateWordSetScreen(mainNavController)
                }
                composable(Screen.CreateSentencePattern.route) {
                    CreateSentencePatternScreen(mainNavController)
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
                        navArgument("wordSetName") { type = NavType.StringType },
                        navArgument("hackXp") { type = NavType.BoolType; defaultValue = false },
                        navArgument("superXp") { type = NavType.BoolType; defaultValue = false }
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
                    val hackXp = backStackEntry.arguments?.getBoolean("hackXp") ?: false
                    val superXp = backStackEntry.arguments?.getBoolean("superXp") ?: false
                    MatchResultScreen(
                        navController = mainNavController,
                        completedTime = completedTime,
                        bestTime = bestTime,
                        bestDate = bestDate,
                        wordSetId = wordSetId,
                        wordSetName = wordSetName,
                        hackExperience = hackXp,
                        superExperience = superXp
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
                        if (initialState.destination.route == Screen.DetailCourse.route) {
                            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Down)
                        } else {
                            fadeIn(tween(250))
                        }
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
                    arguments = listOf(
                        navArgument("streakCount") { type = NavType.IntType },
                        navArgument("nextRoute") {
                            type = NavType.StringType; nullable = true; defaultValue = null
                        }
                    )
                ) { backStackEntry ->
                    val streakCount = backStackEntry.arguments?.getInt("streakCount") ?: 1
                    val nextRoute = backStackEntry.arguments?.getString("nextRoute")
                        ?.let(Uri::decode)
                    StreakScreen(
                        navController = mainNavController,
                        streakCount = streakCount,
                        onFinished = if (nextRoute != null) {
                            {
                                mainNavController.navigate(nextRoute) {
                                    popUpTo(Screen.Streak.route) { inclusive = true }
                                }
                            }
                        } else null
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
                composable(
                    route = Screen.EditCard.route,
                    arguments = listOf(
                        navArgument("cardId") { type = NavType.LongType }
                    )
                ) { backStackEntry ->
                    val cardId = backStackEntry.arguments?.getLong("cardId") ?: 0L
                    EditCardScreen(
                        navController = mainNavController,
                        cardId = cardId
                    )
                }
                composable(
                    route = Screen.AddCard.route,
                    arguments = listOf(
                        navArgument("wordSetId") { type = NavType.LongType }
                    ),
                    popExitTransition = {
                        slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
                    }
                ) { backStackEntry ->
                    val wordSetId = backStackEntry.arguments?.getLong("wordSetId") ?: 0L
                    AddCardScreen(
                        navController = mainNavController,
                        wordSetId = wordSetId
                    )
                }
                composable(Screen.Rank.route) {
                    RankScreen(mainNavController)
                }
                composable(
                    route = Screen.DetailSentencePattern.route,
                    arguments = listOf(
                        navArgument("sentencePatternId") { type = NavType.IntType },
                        navArgument("sentencePatternName") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val patternId = backStackEntry.arguments?.getInt("sentencePatternId") ?: 0
                    DetailSentencePatternScreen(mainNavController, patternId)
                }
                composable(
                    route = Screen.DeleteDetail.route,
                    arguments = listOf(navArgument("sentencePatternId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val patternId = backStackEntry.arguments?.getInt("sentencePatternId") ?: 0
                    DeleteDetailScreen(mainNavController, patternId)
                }
                composable(
                    route = Screen.EditDetail.route,
                    arguments = listOf(navArgument("sentencePatternId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val patternId = backStackEntry.arguments?.getInt("sentencePatternId") ?: 0
                    EditDetailScreen(mainNavController, patternId)
                }
                composable(
                    route = Screen.ListOfDetail.route,
                    arguments = listOf(navArgument("patternId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val patternId = backStackEntry.arguments?.getInt("patternId") ?: 0
                    ListOfDetailScreen(mainNavController, patternId)
                }
                composable(
                    route = Screen.EditSentence.route,
                    arguments = listOf(navArgument("sentenceId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val sentenceId = backStackEntry.arguments?.getInt("sentenceId") ?: 0
                    EditSentenceScreen(mainNavController, sentenceId)
                }
                composable(
                    route = Screen.AddSentence.route,
                    arguments = listOf(navArgument("patternId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val patternId = backStackEntry.arguments?.getInt("patternId") ?: 0
                    AddSentenceScreen(mainNavController, patternId)
                }
                composable(
                    route = Screen.WritingPractice.route,
                    arguments = listOf(navArgument("patternId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val patternId = backStackEntry.arguments?.getInt("patternId") ?: 0
                    WritingPracticeScreen(mainNavController, patternId)
                }
                composable(
                    route = Screen.WordOrdering.route,
                    arguments = listOf(
                        navArgument("patternId") { type = NavType.IntType },
                        navArgument("patternName") { type = NavType.StringType }
                    ),
                    enterTransition = { fadeIn(tween(250)) },
                    exitTransition = { fadeOut(tween(250)) }
                ) { backStackEntry ->
                    val patternId = backStackEntry.arguments?.getInt("patternId") ?: 0
                    val patternName = backStackEntry.arguments?.getString("patternName")
                        ?.let(Uri::decode) ?: ""
                    WordOrderingScreen(
                        navController = mainNavController,
                        patternId = patternId,
                        patternName = patternName
                    )
                }
                composable(
                    route = Screen.WordOrderingResult.route,
                    arguments = listOf(
                        navArgument("correctCount") { type = NavType.IntType },
                        navArgument("totalCount") { type = NavType.IntType },
                        navArgument("completedTime") { type = NavType.FloatType; defaultValue = 0f },
                        navArgument("xpEarned") { type = NavType.IntType; defaultValue = 0 },
                        navArgument("diamondEarned") { type = NavType.IntType; defaultValue = 0 },
                        navArgument("hackXp") { type = NavType.BoolType; defaultValue = false },
                        navArgument("superXp") { type = NavType.BoolType; defaultValue = false }
                    ),
                    enterTransition = { fadeIn(tween(300)) },
                    exitTransition = { fadeOut(tween(250)) }
                ) { backStackEntry ->
                    val correctCount = backStackEntry.arguments?.getInt("correctCount") ?: 0
                    val totalCount = backStackEntry.arguments?.getInt("totalCount") ?: 0
                    val completedTime = backStackEntry.arguments?.getFloat("completedTime")?.toDouble() ?: 0.0
                    val xpEarned = backStackEntry.arguments?.getInt("xpEarned") ?: 0
                    val diamondEarned = backStackEntry.arguments?.getInt("diamondEarned") ?: 0
                    val hackXp = backStackEntry.arguments?.getBoolean("hackXp") ?: false
                    val superXp = backStackEntry.arguments?.getBoolean("superXp") ?: false
                    WordOrderingResultScreen(
                        navController = mainNavController,
                        correctCount = correctCount,
                        totalCount = totalCount,
                        completedTime = completedTime,
                        xpEarned = xpEarned,
                        diamondEarned = diamondEarned,
                        hackExperience = hackXp,
                        superExperience = superXp
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
