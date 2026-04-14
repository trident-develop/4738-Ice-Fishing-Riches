package beatmaker.edm.musicgames.PianoGa.screens

import androidx.activity.compose.BackHandler
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
import beatmaker.edm.musicgames.PianoGa.R
import beatmaker.edm.musicgames.PianoGa.ui.theme.AquaGlow
import beatmaker.edm.musicgames.PianoGa.ui.theme.FoamWhite
import beatmaker.edm.musicgames.PianoGa.ui.theme.GameFontFamily
import beatmaker.edm.musicgames.PianoGa.ui.theme.GoldFish
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LoadingScreen(onLoadingComplete: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000)
        onLoadingComplete()
    }
    BackHandler(enabled = true) {}
    val infiniteTransition = rememberInfiniteTransition(label = "loading")

    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    val bubbleTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bubbles"
    )

    // Fish data
    data class FishInfo(val radius: Float, val speed: Float, val phase: Float, val color: Color, val size: Float)
    val fishList = remember {
        listOf(
            FishInfo(0.28f, 1.0f, 0f, Color(0xFFFF8A65), 55f),
            FishInfo(0.22f, 1.4f, 90f, Color(0xFF4FC3F7), 45f),
            FishInfo(0.33f, 0.7f, 180f, Color(0xFFFFD54F), 50f),
            FishInfo(0.25f, 1.2f, 270f, Color(0xFF81C784), 40f),
            FishInfo(0.18f, 1.6f, 45f, Color(0xFFCE93D8), 35f)
        )
    }

    data class BubbleInfo(val x: Float, val speed: Float, val size: Float, val startY: Float)
    val bubbles = remember {
        List(12) {
            BubbleInfo(
                x = (it * 0.08f + 0.05f) % 1f,
                speed = 0.2f + (it % 5) * 0.08f,
                size = 3f + (it % 4) * 2f,
                startY = (it * 0.15f) % 1f
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.bg_1),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Animated overlay (fish + bubbles)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Swimming fish in a loose circle
            val centerX = w / 2f
            val centerY = h * 0.38f

            fishList.forEach { fish ->
                val angle = Math.toRadians((time * fish.speed + fish.phase).toDouble())
                val fx = centerX + cos(angle).toFloat() * w * fish.radius
                val fy = centerY + sin(angle).toFloat() * h * fish.radius * 0.5f
                val bobY = sin(angle * 2.0).toFloat() * 5f

                val dx = -sin(angle).toFloat()
                val facingRight = dx > 0
                drawGlowingFish(fx, fy + bobY, fish.size, fish.color, facingRight)
            }

            // Bubbles
            bubbles.forEach { bubble ->
                val yProgress = (bubble.startY + bubbleTime * bubble.speed * 0.01f) % 1.3f
                val by = h * (1.2f - yProgress)
                val bx = bubble.x * w + sin(bubbleTime * 0.1 + bubble.x * 20.0).toFloat() * 10f
                val alpha = if (yProgress < 0.1f) yProgress * 10f else if (yProgress > 1.0f) (1.3f - yProgress) / 0.3f else 1f

                drawCircle(
                    color = Color.White.copy(alpha = 0.2f * alpha.coerceIn(0f, 1f)),
                    radius = bubble.size,
                    center = Offset(bx, by)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.35f * alpha.coerceIn(0f, 1f)),
                    radius = bubble.size * 0.3f,
                    center = Offset(bx - bubble.size * 0.3f, by - bubble.size * 0.3f)
                )
            }
        }

        // Title and progress indicator
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = "Ice Fishing Riches",
                fontFamily = GameFontFamily,
                fontSize = 38.sp,
                color = GoldFish,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                color = AquaGlow,
                strokeWidth = 5.dp,
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Loading...",
                fontFamily = GameFontFamily,
                fontSize = 18.sp,
                color = FoamWhite
            )
        }
    }
}

private fun DrawScope.drawGlowingFish(x: Float, y: Float, s: Float, color: Color, facingRight: Boolean) {
    val dir = if (facingRight) 1f else -1f
    val darker = color.copy(
        red = (color.red * 0.6f).coerceIn(0f, 1f),
        green = (color.green * 0.6f).coerceIn(0f, 1f),
        blue = (color.blue * 0.6f).coerceIn(0f, 1f)
    )
    val lighter = color.copy(
        red = (color.red * 1.3f).coerceIn(0f, 1f),
        green = (color.green * 1.3f).coerceIn(0f, 1f),
        blue = (color.blue * 1.3f).coerceIn(0f, 1f)
    )

    // Outer glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(color.copy(alpha = 0.25f), Color.Transparent),
            center = Offset(x, y),
            radius = s * 2.5f
        ),
        radius = s * 2.5f,
        center = Offset(x, y)
    )

    // Tail (behind body)
    val tailPath = Path().apply {
        moveTo(x - s * 0.9f * dir, y)
        cubicTo(
            x - s * 1.2f * dir, y - s * 0.15f,
            x - s * 1.45f * dir, y - s * 0.5f,
            x - s * 1.6f * dir, y - s * 0.45f
        )
        cubicTo(
            x - s * 1.5f * dir, y - s * 0.1f,
            x - s * 1.5f * dir, y + s * 0.1f,
            x - s * 1.6f * dir, y + s * 0.45f
        )
        cubicTo(
            x - s * 1.45f * dir, y + s * 0.5f,
            x - s * 1.2f * dir, y + s * 0.15f,
            x - s * 0.9f * dir, y
        )
        close()
    }
    drawPath(path = tailPath, brush = Brush.verticalGradient(
        listOf(lighter, darker),
        startY = y - s * 0.5f,
        endY = y + s * 0.5f
    ))

    // Bottom fin
    val bottomFin = Path().apply {
        moveTo(x - s * 0.05f * dir, y + s * 0.32f)
        quadraticTo(x - s * 0.1f * dir, y + s * 0.6f, x - s * 0.35f * dir, y + s * 0.55f)
        quadraticTo(x - s * 0.15f * dir, y + s * 0.4f, x - s * 0.05f * dir, y + s * 0.32f)
        close()
    }
    drawPath(path = bottomFin, color = darker.copy(alpha = 0.7f))

    // Body with gradient
    val body = Path().apply {
        moveTo(x - s * dir, y)
        cubicTo(x - s * 0.55f * dir, y - s * 0.58f, x + s * 0.5f * dir, y - s * 0.5f, x + s * dir, y - s * 0.02f)
        cubicTo(x + s * 0.5f * dir, y + s * 0.48f, x - s * 0.55f * dir, y + s * 0.58f, x - s * dir, y)
        close()
    }
    drawPath(
        path = body,
        brush = Brush.verticalGradient(
            listOf(lighter, color, darker),
            startY = y - s * 0.58f,
            endY = y + s * 0.58f
        )
    )

    // Scale pattern
    for (row in 0..2) {
        for (col in 0..3) {
            val sx = x - s * 0.5f * dir + col * s * 0.25f * dir + (row % 2) * s * 0.12f * dir
            val sy = y - s * 0.15f + row * s * 0.18f
            drawArc(
                color = Color.White.copy(alpha = 0.12f),
                startAngle = if (facingRight) 200f else -20f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = Offset(sx - s * 0.06f, sy - s * 0.05f),
                size = Size(s * 0.12f, s * 0.1f),
                style = Stroke(width = 0.8f)
            )
        }
    }

    // Belly highlight
    val belly = Path().apply {
        moveTo(x - s * 0.5f * dir, y + s * 0.08f)
        cubicTo(x - s * 0.2f * dir, y + s * 0.38f, x + s * 0.25f * dir, y + s * 0.35f, x + s * 0.65f * dir, y + s * 0.06f)
        cubicTo(x + s * 0.25f * dir, y + s * 0.2f, x - s * 0.2f * dir, y + s * 0.22f, x - s * 0.5f * dir, y + s * 0.08f)
        close()
    }
    drawPath(path = belly, color = Color.White.copy(alpha = 0.15f))

    // Top shine
    val shine = Path().apply {
        moveTo(x - s * 0.4f * dir, y - s * 0.33f)
        cubicTo(x - s * 0.15f * dir, y - s * 0.46f, x + s * 0.2f * dir, y - s * 0.43f, x + s * 0.55f * dir, y - s * 0.22f)
        cubicTo(x + s * 0.2f * dir, y - s * 0.33f, x - s * 0.15f * dir, y - s * 0.36f, x - s * 0.4f * dir, y - s * 0.23f)
        close()
    }
    drawPath(path = shine, color = Color.White.copy(alpha = 0.2f))

    // Dorsal fin
    val dorsal = Path().apply {
        moveTo(x - s * 0.1f * dir, y - s * 0.42f)
        cubicTo(x * 1f, y - s * 0.85f, x + s * 0.2f * dir, y - s * 0.78f, x + s * 0.35f * dir, y - s * 0.38f)
        cubicTo(x + s * 0.15f * dir, y - s * 0.48f, x - s * 0.0f * dir, y - s * 0.52f, x - s * 0.1f * dir, y - s * 0.42f)
        close()
    }
    drawPath(path = dorsal, brush = Brush.verticalGradient(
        listOf(darker, color),
        startY = y - s * 0.85f,
        endY = y - s * 0.38f
    ))

    // Pectoral fin
    val pectoral = Path().apply {
        moveTo(x + s * 0.12f * dir, y + s * 0.08f)
        quadraticTo(x + s * 0.3f * dir, y + s * 0.4f, x + s * 0.02f * dir, y + s * 0.42f)
        quadraticTo(x + s * 0.08f * dir, y + s * 0.2f, x + s * 0.12f * dir, y + s * 0.08f)
        close()
    }
    drawPath(path = pectoral, color = color.copy(alpha = 0.6f))

    // Eye
    drawCircle(Color.White, s * 0.14f, Offset(x + s * 0.5f * dir, y - s * 0.1f))
    drawCircle(Color(0xFF1B5E20), s * 0.095f, Offset(x + s * 0.52f * dir, y - s * 0.1f))
    drawCircle(Color(0xFF0A0A1A), s * 0.055f, Offset(x + s * 0.53f * dir, y - s * 0.1f))
    drawCircle(Color.White.copy(alpha = 0.85f), s * 0.035f, Offset(x + s * 0.49f * dir, y - s * 0.13f))

    // Gill line
    val gill = Path().apply {
        moveTo(x + s * 0.32f * dir, y - s * 0.22f)
        quadraticTo(x + s * 0.36f * dir, y, x + s * 0.32f * dir, y + s * 0.22f)
    }
    drawPath(
        path = gill,
        color = darker.copy(alpha = 0.4f),
        style = Stroke(width = 1.2f)
    )

    // Mouth
    val mouth = Path().apply {
        moveTo(x + s * 0.88f * dir, y + s * 0.02f)
        quadraticTo(x + s * 0.78f * dir, y + s * 0.1f, x + s * 0.68f * dir, y + s * 0.06f)
    }
    drawPath(
        path = mouth,
        color = darker.copy(alpha = 0.5f),
        style = Stroke(width = 1.5f)
    )
}
