package beatmaker.edm.musicgames.PianoGa.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import beatmaker.edm.musicgames.PianoGa.storage.PrefsManager
import beatmaker.edm.musicgames.PianoGa.ui.components.OceanButton
import beatmaker.edm.musicgames.PianoGa.ui.components.OceanButtonStyle
import beatmaker.edm.musicgames.PianoGa.ui.components.ScreenTitle
import beatmaker.edm.musicgames.PianoGa.ui.components.UnderwaterBackground
import beatmaker.edm.musicgames.PianoGa.ui.theme.AquaGlow
import beatmaker.edm.musicgames.PianoGa.ui.theme.FoamWhite
import beatmaker.edm.musicgames.PianoGa.ui.theme.GameFontFamily
import beatmaker.edm.musicgames.PianoGa.ui.theme.GoldFish
import beatmaker.edm.musicgames.PianoGa.ui.theme.OceanBlue
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun LeaderboardScreen(
    prefsManager: PrefsManager,
    onBackClick: () -> Unit
) {
    val bestLevel = remember { prefsManager.bestCompletedLevel }
    val totalScore = remember { prefsManager.totalScore }
    val bestScore = remember { prefsManager.bestLevelScore }
    val isInPreview = LocalInspectionMode.current

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
            ScreenTitle(text = "Leaderboard")

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(OceanBlue.copy(alpha = 0.7f))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatRow(label = "Best Level", value = if (bestLevel > 0) "$bestLevel" else "-")

                HorizontalDivider(color = AquaGlow.copy(alpha = 0.3f))

                StatRow(label = "Best Score", value = if (bestScore > 0) "$bestScore" else "-")

                HorizontalDivider(color = AquaGlow.copy(alpha = 0.3f))

                StatRow(label = "Total Score", value = if (totalScore > 0) "$totalScore" else "-")
            }

            Spacer(modifier = Modifier.height(32.dp))

            OceanButton(
                text = "Back",
                onClick = onBackClick,
                style = OceanButtonStyle.Secondary,
                padding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
            )
        }

        if (!isInPreview) {
            AndroidView(
                factory = {
                    val adView = AdView(it)
                    adView.setAdSize(AdSize.BANNER)
                    adView.adUnitId = "ca-app-pub-3940256099942544/9214589741"
                    adView.loadAd(AdRequest.Builder().build())
                    adView
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontFamily = GameFontFamily,
            fontSize = 20.sp,
            color = FoamWhite
        )
        Text(
            text = value,
            fontFamily = GameFontFamily,
            fontSize = 24.sp,
            color = GoldFish
        )
    }
}
