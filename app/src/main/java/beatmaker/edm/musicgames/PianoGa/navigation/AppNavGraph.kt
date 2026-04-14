package beatmaker.edm.musicgames.PianoGa.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import beatmaker.edm.musicgames.PianoGa.audio.SoundManager
import beatmaker.edm.musicgames.PianoGa.screens.GameScreen
import beatmaker.edm.musicgames.PianoGa.screens.HowToPlayScreen
import beatmaker.edm.musicgames.PianoGa.screens.LeaderboardScreen
import beatmaker.edm.musicgames.PianoGa.screens.LevelsScreen
import beatmaker.edm.musicgames.PianoGa.screens.MenuScreen
import beatmaker.edm.musicgames.PianoGa.screens.PrivacyPolicyScreen
import beatmaker.edm.musicgames.PianoGa.screens.SettingsScreen
import beatmaker.edm.musicgames.PianoGa.storage.PrefsManager

object Routes {
    const val MENU = "menu"
    const val LEVELS = "levels"
    const val GAME = "game/{level}"
    const val SETTINGS = "settings"
    const val LEADERBOARD = "leaderboard"
    const val HOW_TO_PLAY = "how_to_play"
    const val PRIVACY_POLICY = "privacy_policy"
    const val LOADING = "loading"
    const val CONNECT = "connect"

    fun game(level: Int) = "game/$level"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    prefsManager: PrefsManager,
    soundManager: SoundManager,
    onExitApp: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Routes.MENU
    ) {
        composable(Routes.MENU) {
            MenuScreen(
                onPlayClick = { navController.navigate(Routes.LEVELS) },
                onSettingsClick = { navController.navigate(Routes.SETTINGS) },
                onLeaderboardClick = { navController.navigate(Routes.LEADERBOARD) },
                onExitClick = onExitApp
            )
        }

        composable(Routes.LEVELS) {
            LevelsScreen(
                prefsManager = prefsManager,
                onLevelClick = { level -> navController.navigate(Routes.game(level)) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.GAME,
            arguments = listOf(navArgument("level") { type = NavType.IntType })
        ) { backStackEntry ->
            val level = backStackEntry.arguments?.getInt("level") ?: 1
            GameScreen(
                levelNumber = level,
                prefsManager = prefsManager,
                soundManager = soundManager,
                onBackToLevels = {
                    navController.popBackStack(Routes.LEVELS, false)
                },
                onNextLevel = { nextLevel ->
                    navController.popBackStack()
                    navController.navigate(Routes.game(nextLevel))
                },
                onRetry = {
                    navController.popBackStack()
                    navController.navigate(Routes.game(level))
                }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                prefsManager = prefsManager,
                soundManager = soundManager,
                onHowToPlayClick = { navController.navigate(Routes.HOW_TO_PLAY) },
                onPrivacyPolicyClick = { navController.navigate(Routes.PRIVACY_POLICY) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.LEADERBOARD) {
            LeaderboardScreen(
                prefsManager = prefsManager,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.HOW_TO_PLAY) {
            HowToPlayScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Routes.PRIVACY_POLICY) {
            PrivacyPolicyScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
