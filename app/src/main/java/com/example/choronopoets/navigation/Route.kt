package com.example.choronopoets.navigation

sealed class Screen(val route: String) {
    data object HOME_SCREEN : Screen("home")
    data object CENTURY_SCREEN: Screen("century_screen")
    data object POETS_SCREEN : Screen("poets_screen/{centuryId}") {
        fun createRoute(centuryId: Int) = "poets_screen/$centuryId"
    }
    data object POET_DETAIL_SCREEN : Screen("poet_details/{poetId}") {
        fun createRoute(poetId: Int) = "poet_details/$poetId"
    }
    data object TAJIK_POET_DETAIL_SCREEN : Screen("tajik_poet_details/{poetKey}") {
        fun createRoute(poetKey: String) = "tajik_poet_details/$poetKey"
    }
    data object POET_CHAT_DB_SCREEN : Screen("poet_chat_db/{poetId}/{mode}") {
        fun createRoute(poetId: Int, mode: String) = "poet_chat_db/$poetId/$mode"
    }
    data object POET_CHAT_TAJIK_SCREEN : Screen("poet_chat_tajik/{poetKey}/{mode}") {
        fun createRoute(poetKey: String, mode: String) = "poet_chat_tajik/$poetKey/$mode"
    }
    data object POEM_GENERATOR_SCREEN : Screen("poem_generator")
    data object POET_POEM_GENERATE_SCREEN : Screen("poet_poem_generate/{poetId}") {
        fun createRoute(poetId: Int) = "poet_poem_generate/$poetId"
    }
    data object POEM_DETAIL_SCREEN : Screen("poem_details/{poemId}") {
        fun createRoute(poemId: Int) = "poem_details/$poemId"
    }
    data object FAVORITES_SCREEN : Screen("favorites")
    data object CHAT_HISTORY_SCREEN : Screen("chat_history")
    data object AI_SESSION_VIEWER_SCREEN : Screen("ai_session/{sessionKey}/{title}") {
        fun createRoute(sessionKey: String, title: String): String {
            val encodedKey   = android.net.Uri.encode(sessionKey)
            val encodedTitle = android.net.Uri.encode(title)
            return "ai_session/$encodedKey/$encodedTitle"
        }
    }
    data object SETTINGS_SCREEN : Screen("settings")
}