package beatmaker.edm.musicgames.PianoGa.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import beatmaker.edm.musicgames.PianoGa.ui.components.OceanButtonFullWidth
import beatmaker.edm.musicgames.PianoGa.ui.components.OceanButtonStyle
import beatmaker.edm.musicgames.PianoGa.ui.components.UnderwaterBackground

@Composable
fun MenuScreen(
    onPlayClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLeaderboardClick: () -> Unit,
    onExitClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "menu")

    val titleFloat by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "titleFloat"
    )

    val titleScale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "titleScale"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        UnderwaterBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.height(48.dp))

            OceanButtonFullWidth(
                text = "Play",
                onClick = onPlayClick,
                style = OceanButtonStyle.Accent
            )

            Spacer(modifier = Modifier.height(16.dp))

            OceanButtonFullWidth(
                text = "Leaderboard",
                onClick = onLeaderboardClick,
                style = OceanButtonStyle.Primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            OceanButtonFullWidth(
                text = "Settings",
                onClick = onSettingsClick,
                style = OceanButtonStyle.Primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            OceanButtonFullWidth(
                text = "Exit",
                onClick = onExitClick,
                style = OceanButtonStyle.Secondary
            )
        }
    }
}
