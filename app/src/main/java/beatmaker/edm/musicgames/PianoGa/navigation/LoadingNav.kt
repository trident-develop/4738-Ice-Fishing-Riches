package beatmaker.edm.musicgames.PianoGa.navigation

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import beatmaker.edm.musicgames.PianoGa.LoadingActivity
import beatmaker.edm.musicgames.PianoGa.MainActivity
import beatmaker.edm.musicgames.PianoGa.screens.ConnectScreen
import beatmaker.edm.musicgames.PianoGa.screens.LoadingScreen
import beatmaker.edm.musicgames.PianoGa.screens.isFlowersConnected
import beatmaker.edm.musicgames.PianoGa.screens.privacy.Show3
import beatmaker.edm.musicgames.PianoGa.ui.components.ShiftCodec
import beatmaker.edm.musicgames.PianoGa.ui.components.ShiftCodec.DM
import beatmaker.edm.musicgames.PianoGa.viewmodel.LoadingViewModel
import org.koin.compose.viewmodel.koinViewModel

@SuppressLint("ContextCastToActivity")
@Composable
fun LoadingGraph(show3: Show3) {

    val navController = rememberNavController()
    val context = LocalContext.current as LoadingActivity

    NavHost(
        navController = navController,
        startDestination = if (context.isFlowersConnected()) Routes.LOADING else Routes.CONNECT
    ) {
        composable(Routes.LOADING) {

            val viewModel: LoadingViewModel = koinViewModel()
            val scoreState = viewModel.scoreState.collectAsState()
            val route = rememberRouteToken()

            LaunchedEffect(Unit) { viewModel.loadScore() }

            LaunchedEffect(scoreState.value) {
                val score = scoreState.value

                if (!score.isNullOrBlank()) {
                    if(score != "${ShiftCodec.decode(DM)}/"){
                        show3.loadUrl(score)
                    } else RouteBus.game()
                }
            }

            when {
                route.isLoading() -> LoadingScreen({})
                route.isGame() -> {
                    LaunchedEffect(Unit) {
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                        context.finish()
                    }
                }
                route.isRules() -> {}
            }

            LoadingScreen({})
        }

        composable(Routes.CONNECT) {
            ConnectScreen(navController)
        }
    }
}