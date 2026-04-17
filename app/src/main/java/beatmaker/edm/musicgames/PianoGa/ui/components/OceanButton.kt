package beatmaker.edm.musicgames.PianoGa.ui.components

import android.Manifest
import android.os.Build
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import beatmaker.edm.musicgames.PianoGa.ui.theme.AquaGlow
import beatmaker.edm.musicgames.PianoGa.ui.theme.CoralOrange
import beatmaker.edm.musicgames.PianoGa.ui.theme.FoamWhite
import beatmaker.edm.musicgames.PianoGa.ui.theme.GameFontFamily
import beatmaker.edm.musicgames.PianoGa.ui.theme.GoldFish
import beatmaker.edm.musicgames.PianoGa.ui.theme.OceanBlue
import beatmaker.edm.musicgames.PianoGa.ui.theme.SeaBlue
import java.net.URLDecoder

@Composable
fun OceanButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: OceanButtonStyle = OceanButtonStyle.Primary,
    padding: PaddingValues = PaddingValues(horizontal = 32.dp, vertical = 14.dp)
) {
    val shape = RoundedCornerShape(16.dp)
    val gradient = when (style) {
        OceanButtonStyle.Primary -> Brush.horizontalGradient(
            listOf(SeaBlue, AquaGlow)
        )
        OceanButtonStyle.Accent -> Brush.horizontalGradient(
            listOf(CoralOrange, GoldFish)
        )
        OceanButtonStyle.Secondary -> Brush.horizontalGradient(
            listOf(OceanBlue, SeaBlue)
        )
    }
    val textColor = when (style) {
        OceanButtonStyle.Accent -> OceanBlue
        else -> FoamWhite
    }
    val alpha = if (enabled) 1f else 0.45f

    Box(
        modifier = modifier
            .pressableWithCooldown(enabled = enabled, onClick = onClick)
            .shadow(8.dp, shape, ambientColor = SeaBlue, spotColor = SeaBlue)
            .clip(shape)
            .background(gradient, alpha = alpha)
            .border(1.dp, Color.White.copy(alpha = 0.2f * alpha), shape)
            .defaultMinSize(minWidth = 200.dp)
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor.copy(alpha = alpha),
            fontFamily = GameFontFamily,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
    }
}

enum class OceanButtonStyle {
    Primary, Accent, Secondary
}

@Composable
fun OceanButtonFullWidth(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: OceanButtonStyle = OceanButtonStyle.Primary
) {
    OceanButton(
        text = text,
        onClick = onClick,
        modifier = modifier.fillMaxWidth(0.75f),
        enabled = enabled,
        style = style
    )
}

fun decodeUtf8(encoded: String?): String =
    URLDecoder.decode(encoded, "UTF-8")

fun requestNotify(registry: ActivityResultRegistry) {
    val launcher = registry.register(
        "requestPermissionKey",
        ActivityResultContracts.RequestPermission()
    ) {  }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}