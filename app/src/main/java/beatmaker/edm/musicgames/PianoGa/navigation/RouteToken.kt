package beatmaker.edm.musicgames.PianoGa.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun rememberRouteToken(): RouteToken {
    var route by remember { mutableStateOf(RouteBus.route) }

    DisposableEffect(Unit) {
        val listener: (RouteToken) -> Unit = { route = it }
        RouteBus.addListener(listener, emitCurrent = true)

        onDispose {
            RouteBus.removeListener(listener)
        }
    }

    return route
}