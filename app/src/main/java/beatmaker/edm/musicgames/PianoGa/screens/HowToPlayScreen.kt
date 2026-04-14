package beatmaker.edm.musicgames.PianoGa.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import beatmaker.edm.musicgames.PianoGa.ui.components.OceanButton
import beatmaker.edm.musicgames.PianoGa.ui.components.OceanButtonStyle
import beatmaker.edm.musicgames.PianoGa.ui.components.ScreenTitle
import beatmaker.edm.musicgames.PianoGa.ui.components.UnderwaterBackground
import beatmaker.edm.musicgames.PianoGa.ui.theme.AquaGlow
import beatmaker.edm.musicgames.PianoGa.ui.theme.FoamWhite
import beatmaker.edm.musicgames.PianoGa.ui.theme.GameFontFamily
import beatmaker.edm.musicgames.PianoGa.ui.theme.OceanBlue

@Composable
fun HowToPlayScreen(
    onBackClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        UnderwaterBackground(showFish = false, showOverlay = false)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ScreenTitle(text = "How To Play", fontSize = 32.sp)

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, false)
                    .clip(RoundedCornerShape(16.dp))
                    .background(OceanBlue.copy(alpha = 0.7f))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                RuleItem(
                    icon = "\uD83D\uDC1F",
                    title = "Watch the Fish",
                    description = "A fish swims across the screen from left to right. Watch its movement carefully!"
                )
                RuleItem(
                    icon = "\uD83C\uDFAF",
                    title = "Hit the Catch Zone",
                    description = "There is a glowing catch zone in the center of the screen. Tap the CATCH button when the fish enters this zone."
                )
                RuleItem(
                    icon = "\u2B50",
                    title = "Perfect Timing",
                    description = "Perfect timing gives the most points! Good timing still earns points, but missing gives you nothing."
                )
                RuleItem(
                    icon = "\uD83C\uDFC6",
                    title = "Reach the Goal",
                    description = "Each level has a target number of successful catches. Reach the goal to complete the level!"
                )
                RuleItem(
                    icon = "\u274C",
                    title = "Don't Miss Too Much",
                    description = "You have a limited number of allowed misses. Too many misses and the level is failed."
                )
                RuleItem(
                    icon = "\uD83D\uDE80",
                    title = "Increasing Difficulty",
                    description = "Higher levels have faster fish, smaller catch zones, and more catches required. Good luck!"
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            OceanButton(
                text = "Got It!",
                onClick = onBackClick,
                style = OceanButtonStyle.Accent,
                padding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun RuleItem(icon: String, title: String, description: String) {
    Column {
        Text(
            text = "$icon  $title",
            fontFamily = GameFontFamily,
            fontSize = 18.sp,
            color = AquaGlow
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            fontSize = 14.sp,
            color = FoamWhite.copy(alpha = 0.8f),
            lineHeight = 20.sp
        )
    }
}
