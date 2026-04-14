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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import beatmaker.edm.musicgames.PianoGa.audio.SoundManager
import beatmaker.edm.musicgames.PianoGa.storage.PrefsManager
import beatmaker.edm.musicgames.PianoGa.ui.components.OceanButton
import beatmaker.edm.musicgames.PianoGa.ui.components.OceanButtonFullWidth
import beatmaker.edm.musicgames.PianoGa.ui.components.OceanButtonStyle
import beatmaker.edm.musicgames.PianoGa.ui.components.ScreenTitle
import beatmaker.edm.musicgames.PianoGa.ui.components.UnderwaterBackground
import beatmaker.edm.musicgames.PianoGa.ui.theme.AquaGlow
import beatmaker.edm.musicgames.PianoGa.ui.theme.FoamWhite
import beatmaker.edm.musicgames.PianoGa.ui.theme.GameFontFamily
import beatmaker.edm.musicgames.PianoGa.ui.theme.OceanBlue
import beatmaker.edm.musicgames.PianoGa.ui.theme.SeaBlue

@Composable
fun SettingsScreen(
    prefsManager: PrefsManager,
    soundManager: SoundManager,
    onHowToPlayClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var musicEnabled by remember { mutableStateOf(prefsManager.musicEnabled) }
    var soundEnabled by remember { mutableStateOf(prefsManager.soundEnabled) }

    Box(modifier = Modifier.fillMaxSize()) {
        UnderwaterBackground(showFish = false)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ScreenTitle(text = "Settings")

            Spacer(modifier = Modifier.height(32.dp))

            // Settings card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(OceanBlue.copy(alpha = 0.7f))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                SettingRow(
                    label = "Music",
                    checked = musicEnabled,
                    onCheckedChange = {
                        musicEnabled = it
                        prefsManager.musicEnabled = it
                        soundManager.setMusicEnabled(it)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SettingRow(
                    label = "Sound Effects",
                    checked = soundEnabled,
                    onCheckedChange = {
                        soundEnabled = it
                        prefsManager.soundEnabled = it
                        soundManager.setSoundEnabled(it)
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            OceanButtonFullWidth(
                text = "How To Play",
                onClick = onHowToPlayClick,
                style = OceanButtonStyle.Primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            OceanButtonFullWidth(
                text = "Privacy Policy",
                onClick = onPrivacyPolicyClick,
                style = OceanButtonStyle.Secondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            OceanButton(
                text = "Back",
                onClick = onBackClick,
                style = OceanButtonStyle.Secondary,
                padding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
            )
        }
    }
}

@Composable
private fun SettingRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontFamily = GameFontFamily,
            fontSize = 20.sp,
            color = FoamWhite
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = FoamWhite,
                checkedTrackColor = AquaGlow,
                uncheckedThumbColor = FoamWhite.copy(alpha = 0.5f),
                uncheckedTrackColor = SeaBlue.copy(alpha = 0.3f)
            )
        )
    }
}
