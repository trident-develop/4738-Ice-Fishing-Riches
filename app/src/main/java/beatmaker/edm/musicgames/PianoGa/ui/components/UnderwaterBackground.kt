package beatmaker.edm.musicgames.PianoGa.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import beatmaker.edm.musicgames.PianoGa.R
import kotlin.math.sin
import kotlin.random.Random

data class BubbleData(
    val x: Float,
    val baseY: Float,
    val radius: Float,
    val speed: Float,
    val wobble: Float
)

data class BackgroundFishData(
    val baseY: Float,
    val size: Float,
    val speed: Float,
    val color: Color,
    val direction: Float
)

@Composable
fun UnderwaterBackground(
    modifier: Modifier = Modifier,
    showFish: Boolean = true,
    showOverlay: Boolean = true
) {
    val bubbles = remember {
        List(12) {
            BubbleData(
                x = Random.nextFloat(),
                baseY = Random.nextFloat(),
                radius = Random.nextFloat() * 6f + 3f,
                speed = Random.nextFloat() * 0.3f + 0.15f,
                wobble = Random.nextFloat() * 15f + 5f
            )
        }
    }

    val fish = remember {
        if (showFish) List(4) {
            BackgroundFishData(
                baseY = Random.nextFloat() * 0.5f + 0.3f,
                size = Random.nextFloat() * 16f + 10f,
                speed = Random.nextFloat() * 0.12f + 0.04f,
                color = listOf(
                    Color(0x50FF6F3C),
                    Color(0x50FFD54F),
                    Color(0x504DA8DA),
                    Color(0x5026A69A)
                ).random(),
                direction = if (Random.nextBoolean()) 1f else -1f
            )
        } else emptyList()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "underwater")

    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    Box(modifier = modifier.fillMaxSize()) {
        // Background image from resources
        Image(
            painter = painterResource(id = R.drawable.bg_1),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Animated overlay elements (bubbles, fish)
        if (showOverlay) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Background fish
                fish.forEach { f ->
                    drawBackgroundFish(f, w, h, time)
                }

                // Bubbles
                bubbles.forEach { bubble ->
                    drawBubble(bubble, w, h, time)
                }
            }
        }
    }
}

private fun DrawScope.drawBubble(bubble: BubbleData, w: Float, h: Float, time: Float) {
    val yProgress = (bubble.baseY + time * bubble.speed * 0.01f) % 1.2f
    val y = h * (1.1f - yProgress)
    val x = bubble.x * w + sin(time * 0.05f + bubble.wobble) * bubble.wobble

    drawCircle(
        color = Color(0x35FFFFFF),
        radius = bubble.radius,
        center = Offset(x, y)
    )
    drawCircle(
        color = Color(0x18FFFFFF),
        radius = bubble.radius + 2f,
        center = Offset(x, y)
    )
    // Small highlight
    drawCircle(
        color = Color(0x40FFFFFF),
        radius = bubble.radius * 0.3f,
        center = Offset(x - bubble.radius * 0.3f, y - bubble.radius * 0.3f)
    )
}

private fun DrawScope.drawBackgroundFish(fish: BackgroundFishData, w: Float, h: Float, time: Float) {
    val xProgress = (time * fish.speed * 0.01f * fish.direction) % 1.4f
    val x = if (fish.direction > 0) {
        -w * 0.1f + xProgress * w * 1.2f
    } else {
        w * 1.1f - xProgress * w * 1.2f
    }
    val y = fish.baseY * h + sin(time * 0.03f + fish.baseY * 10f) * 12f
    val s = fish.size

    val bodyPath = Path().apply {
        moveTo(x - s * fish.direction, y)
        cubicTo(
            x - s * 0.5f * fish.direction, y - s * 0.5f,
            x + s * 0.5f * fish.direction, y - s * 0.4f,
            x + s * fish.direction, y
        )
        cubicTo(
            x + s * 0.5f * fish.direction, y + s * 0.4f,
            x - s * 0.5f * fish.direction, y + s * 0.5f,
            x - s * fish.direction, y
        )
        close()
    }
    drawPath(path = bodyPath, color = fish.color)

    // Tail
    val tailPath = Path().apply {
        moveTo(x - s * fish.direction, y)
        lineTo(x - s * 1.5f * fish.direction, y - s * 0.4f)
        lineTo(x - s * 1.5f * fish.direction, y + s * 0.4f)
        close()
    }
    drawPath(path = tailPath, color = fish.color.copy(alpha = fish.color.alpha * 0.7f))
}
