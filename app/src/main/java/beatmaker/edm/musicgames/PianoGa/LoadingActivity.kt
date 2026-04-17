package beatmaker.edm.musicgames.PianoGa

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import beatmaker.edm.musicgames.PianoGa.navigation.LoadingGraph
import beatmaker.edm.musicgames.PianoGa.screens.LoadingScreen
import beatmaker.edm.musicgames.PianoGa.screens.privacy.Show3
import beatmaker.edm.musicgames.PianoGa.ui.theme.AquaGlow
import beatmaker.edm.musicgames.PianoGa.ui.theme.FoamWhite
import beatmaker.edm.musicgames.PianoGa.ui.theme.GameFontFamily
import beatmaker.edm.musicgames.PianoGa.ui.theme.GoldFish
import beatmaker.edm.musicgames.PianoGa.ui.theme.IceFishingRichesTheme
import kotlinx.coroutines.delay
import org.koin.android.ext.android.get
import kotlin.math.cos
import kotlin.math.sin

class LoadingActivity : ComponentActivity() {
    private var controller: WindowInsetsControllerCompat? = null
    private lateinit var show3: Show3

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        controller = WindowInsetsControllerCompat(window, window.decorView)
        controller?.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller?.hide(WindowInsetsCompat.Type.systemBars())
        show3 = Show3(this, get(), get())
        setContent {
            LoadingGraph(show3)
        }
    }

    override fun onResume() {
        super.onResume()
        controller?.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller?.hide(WindowInsetsCompat.Type.systemBars())
    }

    override fun onDestroy() {
        show3.destroy()
        super.onDestroy()
    }
}