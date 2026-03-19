package com.example.choronopoets.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import java.net.URLDecoder
import com.example.choronopoets.presentation.CenturyScreen
import com.example.choronopoets.presentation.PoemDetailScreen
import com.example.choronopoets.viewmodel.PoetryViewModel
import com.example.choronopoets.presentation.PoetDetailsScreen
import com.example.choronopoets.presentation.PoetScreen
import com.example.choronopoets.ui.AiSessionViewerScreen
import com.example.choronopoets.ui.ChatHistoryScreen
import com.example.choronopoets.ui.FavoritesScreen
import com.example.choronopoets.ui.HomeScreen
import com.example.choronopoets.ui.PoetChatScreen
import com.example.choronopoets.ui.PoetDetailScreen
import com.example.choronopoets.ui.PoetGeneratorScreen
import com.example.choronopoets.ui.PoetPoemGenerateScreen
import com.example.choronopoets.ui.SettingsScreen
import com.example.choronopoets.ui.TajikPoetDetailScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun NavigationScreen() {
    val navController = rememberNavController()
    val poetryViewModel: PoetryViewModel = koinViewModel()

    NavHost(navController = navController, startDestination = Screen.HOME_SCREEN.route) {
        composable(Screen.HOME_SCREEN.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.CENTURY_SCREEN.route) {
            CenturyScreen(navController = navController, viewModel = poetryViewModel)
        }
        composable(
            route = Screen.POETS_SCREEN.route,
            arguments = listOf(navArgument("centuryId"){ type = NavType.IntType })
        ) { backStackEntry ->
            val centuryId = backStackEntry.arguments?.getInt("centuryId")?: 0
            PoetScreen(navController = navController, viewModel = poetryViewModel, centuryId)
        }
        composable(
            route = Screen.POET_DETAIL_SCREEN.route,
            arguments = listOf(navArgument("poetId"){ type = NavType.IntType })
        ) { backStackEntry ->
            val poetId = backStackEntry.arguments?.getInt("poetId")?: 0
            PoetDetailScreen(navController = navController, poetId = poetId)
        }
        composable(
            route = Screen.TAJIK_POET_DETAIL_SCREEN.route,
            arguments = listOf(navArgument("poetKey") { type = NavType.StringType })
        ) { backStackEntry ->
            val poetKey = backStackEntry.arguments?.getString("poetKey") ?: "rudaki"
            TajikPoetDetailScreen(navController = navController, poetKey = poetKey)
        }
        composable(
            route = Screen.POET_CHAT_DB_SCREEN.route,
            arguments = listOf(
                navArgument("poetId") { type = NavType.IntType },
                navArgument("mode") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val poetId = backStackEntry.arguments?.getInt("poetId") ?: 0
            val mode = backStackEntry.arguments?.getString("mode") ?: "ask"
            PoetChatScreen(navController = navController, poetSource = "db", poetId = poetId, poetKey = null, mode = mode)
        }
        composable(
            route = Screen.POET_CHAT_TAJIK_SCREEN.route,
            arguments = listOf(
                navArgument("poetKey") { type = NavType.StringType },
                navArgument("mode") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val poetKey = backStackEntry.arguments?.getString("poetKey") ?: "rudaki"
            val mode = backStackEntry.arguments?.getString("mode") ?: "ask"
            PoetChatScreen(navController = navController, poetSource = "tajik", poetId = null, poetKey = poetKey, mode = mode)
        }
        composable(
            route = Screen.POEM_DETAIL_SCREEN.route,
            arguments = listOf(navArgument("poemId") { type = NavType.IntType })
        ) { backStackEntry ->
            val poemId = backStackEntry.arguments?.getInt("poemId") ?: 0
            PoemDetailScreen(navController = navController, poemId = poemId)
        }
        composable(Screen.FAVORITES_SCREEN.route) {
            FavoritesScreen(navController = navController)
        }
        composable(Screen.CHAT_HISTORY_SCREEN.route) {
            ChatHistoryScreen(navController = navController)
        }
        composable(
            route = Screen.AI_SESSION_VIEWER_SCREEN.route,
            arguments = listOf(
                navArgument("sessionKey") { type = NavType.StringType },
                navArgument("title")      { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val sessionKey = URLDecoder.decode(
                backStackEntry.arguments?.getString("sessionKey") ?: "", "UTF-8"
            )
            val title = URLDecoder.decode(
                backStackEntry.arguments?.getString("title") ?: "", "UTF-8"
            )
            AiSessionViewerScreen(
                navController = navController,
                sessionKey = sessionKey,
                title = title,
            )
        }
        composable(Screen.SETTINGS_SCREEN.route) {
            SettingsScreen(navController = navController)
        }
        composable(Screen.POEM_GENERATOR_SCREEN.route) {
            PoetGeneratorScreen(navController = navController)
        }
        composable(
            route = Screen.POET_POEM_GENERATE_SCREEN.route,
            arguments = listOf(navArgument("poetId") { type = NavType.IntType }),
        ) { backStackEntry ->
            val poetId = backStackEntry.arguments?.getInt("poetId") ?: 0
            PoetPoemGenerateScreen(navController = navController, poetId = poetId)
        }
    }
}
