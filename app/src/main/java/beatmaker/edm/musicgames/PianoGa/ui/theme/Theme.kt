package beatmaker.edm.musicgames.PianoGa.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val OceanColorScheme = darkColorScheme(
    primary = SeaBlue,
    onPrimary = FoamWhite,
    primaryContainer = OceanBlue,
    onPrimaryContainer = AquaGlow,
    secondary = CoralOrange,
    onSecondary = FoamWhite,
    secondaryContainer = CoralPink,
    tertiary = GoldFish,
    background = DeepOcean,
    onBackground = FoamWhite,
    surface = OceanBlue,
    onSurface = FoamWhite,
    surfaceVariant = SeaBlue,
    onSurfaceVariant = AquaGlow
)

@Composable
fun IceFishingRichesTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = OceanColorScheme,
        typography = Typography,
        content = content
    )
}
