package beatmaker.edm.musicgames.PianoGa.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import beatmaker.edm.musicgames.PianoGa.audio.SoundManager
import beatmaker.edm.musicgames.PianoGa.model.LevelData
import beatmaker.edm.musicgames.PianoGa.storage.PrefsManager
import beatmaker.edm.musicgames.PianoGa.ui.components.OceanButton
import beatmaker.edm.musicgames.PianoGa.ui.components.OceanButtonStyle
import beatmaker.edm.musicgames.PianoGa.ui.components.UnderwaterBackground
import beatmaker.edm.musicgames.PianoGa.ui.components.pressableWithCooldown
import beatmaker.edm.musicgames.PianoGa.ui.theme.AquaGlow
import beatmaker.edm.musicgames.PianoGa.ui.theme.CoralOrange
import beatmaker.edm.musicgames.PianoGa.ui.theme.DeepOcean
import beatmaker.edm.musicgames.PianoGa.ui.theme.FoamWhite
import beatmaker.edm.musicgames.PianoGa.ui.theme.GameFontFamily
import beatmaker.edm.musicgames.PianoGa.ui.theme.GoldFish
import beatmaker.edm.musicgames.PianoGa.ui.theme.OceanBlue
import beatmaker.edm.musicgames.PianoGa.ui.theme.SeaBlue
import beatmaker.edm.musicgames.PianoGa.ui.theme.SeaGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.random.Random

private fun randomCatchZoneCenter(): Float = Random.nextFloat() * 0.6f + 0.2f

private enum class GamePhase {
    READY, SWIMMING, FEEDBACK, PAUSED, WIN, LOSE
}

private enum class CatchResult {
    PERFECT, GOOD, MISS
}

@Composable
fun GameScreen(
    levelNumber: Int,
    prefsManager: PrefsManager,
    soundManager: SoundManager,
    onBackToLevels: () -> Unit,
    onNextLevel: (Int) -> Unit,
    onRetry: () -> Unit
) {
    val config = remember { LevelData.getLevel(levelNumber) }
    var phase by remember { mutableStateOf(GamePhase.READY) }
    var catches by remember { mutableIntStateOf(0) }
    var misses by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var lastResult by remember { mutableStateOf<CatchResult?>(null) }
    var showResultOverlay by remember { mutableStateOf(false) }
    var catchZoneCenter by remember { mutableStateOf(randomCatchZoneCenter()) }
    var isPaused by remember { mutableStateOf(false) }
    var pausedFishProgress by remember { mutableStateOf(0f) }

    val fishProgress = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val infiniteTransition = rememberInfiniteTransition(label = "game")
    val bobOffset by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bob"
    )
    var isExitingScreen by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    fun startFishSwim(fromProgress: Float = 0f, isNewRound: Boolean = true, randomizeZone: Boolean = true) {
        phase = GamePhase.SWIMMING
        if (isNewRound) {
            lastResult = null
            if (randomizeZone) {
                catchZoneCenter = randomCatchZoneCenter()
            }
        }
        scope.launch {
            fishProgress.snapTo(fromProgress)
            val remaining = 1f - fromProgress
            fishProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = (config.fishSpeedMs * remaining).toInt(),
                    easing = LinearEasing
                )
            )
            // Fish reached the end without being caught
            if (phase == GamePhase.SWIMMING) {
                lastResult = CatchResult.MISS
                misses++
                phase = GamePhase.FEEDBACK

                if (misses >= config.maxMisses) {
                    delay(600)
                    phase = GamePhase.LOSE
                    soundManager.playLoseSound()
                    showResultOverlay = true
                } else {
                    delay(800)
                    startFishSwim()
                }
            }
        }
    }

    fun pauseGame() {
        if (phase == GamePhase.SWIMMING && !isPaused) {
            isPaused = true
            pausedFishProgress = fishProgress.value
            phase = GamePhase.PAUSED
            scope.launch { fishProgress.stop() }
        }
    }

    fun resumeGame() {
        if (isPaused) {
            isPaused = false
            startFishSwim(fromProgress = pausedFishProgress, isNewRound = false)
        }
    }

    fun togglePause() {
        if (isPaused) {
            resumeGame()
        } else {
            pauseGame()
        }
    }

    fun onCatchTap() {
        if (phase != GamePhase.SWIMMING) return
        val pos = fishProgress.value
        val distFromCenter = (pos - catchZoneCenter).absoluteValue

        val result = when {
            distFromCenter <= config.perfectZoneFraction / 2f -> CatchResult.PERFECT
            distFromCenter <= config.goodZoneFraction / 2f -> CatchResult.GOOD
            else -> CatchResult.MISS
        }

        lastResult = result
        phase = GamePhase.FEEDBACK

        when (result) {
            CatchResult.PERFECT -> {
                catches++
                score += config.perfectPoints
            }
            CatchResult.GOOD -> {
                catches++
                score += config.goodPoints
            }
            CatchResult.MISS -> {
                misses++
            }
        }

        scope.launch {
            delay(800)
            if (catches >= config.targetCatches) {
                phase = GamePhase.WIN
                prefsManager.unlockNextLevel(levelNumber)
                prefsManager.recordLevelCompletion(levelNumber, score)
                soundManager.playWinSound()
                showResultOverlay = true
            } else if (misses >= config.maxMisses) {
                phase = GamePhase.LOSE
                soundManager.playLoseSound()
                showResultOverlay = true
            } else {
                startFishSwim()
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val obs = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE && phase != GamePhase.WIN && phase != GamePhase.LOSE && !isExitingScreen)
                pauseGame()
        }
        lifecycleOwner.lifecycle.addObserver(obs)
        onDispose { lifecycleOwner.lifecycle.removeObserver(obs) }
    }

    BackHandler(enabled = true) {
        isExitingScreen = true
        onBackToLevels()
    }

    // Auto-start first swim
    LaunchedEffect(Unit) {
        delay(800)
        startFishSwim(randomizeZone = false)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        UnderwaterBackground(showFish = false)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top HUD
            GameHud(
                levelNumber = levelNumber,
                catches = catches,
                targetCatches = config.targetCatches,
                misses = misses,
                maxMisses = config.maxMisses,
                score = score,
                onPauseClick = { togglePause() },
                onBackClick = {
                    isExitingScreen = true
                    onBackToLevels()
                }
            )

            // Game area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                // Catch zone indicator
                CatchZoneIndicator(
                    centerFraction = catchZoneCenter,
                    perfectFraction = config.perfectZoneFraction,
                    goodFraction = config.goodZoneFraction
                )

                // Fish
                FishCanvas(
                    progress = fishProgress.value,
                    bobOffset = bobOffset,
                    modifier = Modifier.fillMaxSize()
                )

                // Feedback text
                lastResult?.let { result ->
                    if (phase == GamePhase.FEEDBACK || phase == GamePhase.WIN || phase == GamePhase.LOSE) {
                        FeedbackText(result = result)
                    }
                }
            }

            // Catch button
            CatchButton(
                enabled = phase == GamePhase.SWIMMING,
                onClick = { onCatchTap() }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Pause overlay
        if (isPaused) {
            PauseOverlay(
                onResume = { togglePause() },
                onBackToLevels = {
                    isExitingScreen = true
                    onBackToLevels()
                }
            )
        }

        // Win/Lose overlay
        if (showResultOverlay) {
            ResultOverlay(
                isWin = phase == GamePhase.WIN,
                score = score,
                levelNumber = levelNumber,
                onRetry = onRetry,
                onNextLevel = {
                    if (levelNumber < 30) onNextLevel(levelNumber + 1)
                    else onBackToLevels()
                },
                onBackToLevels = {
                    isExitingScreen = true
                    onBackToLevels()
                }
            )
        }
    }
}

@Composable
private fun GameHud(
    levelNumber: Int,
    catches: Int,
    targetCatches: Int,
    misses: Int,
    maxMisses: Int,
    score: Int,
    onPauseClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DeepOcean.copy(alpha = 0.7f))
            .padding(top = 40.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Back button (left)
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(36.dp)
                    .pressableWithCooldown(cooldownMillis = 500L, onClick = onBackClick)
                    .clip(CircleShape)
                    .background(OceanBlue.copy(alpha = 0.8f))
                    .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(16.dp)) {
                    val w = size.width
                    val h = size.height
                    val path = Path().apply {
                        moveTo(w * 0.75f, h * 0.15f)
                        lineTo(w * 0.25f, h * 0.5f)
                        lineTo(w * 0.75f, h * 0.85f)
                    }
                    drawPath(
                        path = path,
                        color = FoamWhite,
                        style = Stroke(
                            width = 2.5f,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }

            Text(
                text = "Level $levelNumber",
                fontFamily = GameFontFamily,
                fontSize = 22.sp,
                color = GoldFish,
                modifier = Modifier.align(Alignment.Center),
                textAlign = TextAlign.Center
            )

            // Pause button (right)
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(36.dp)
                    .pressableWithCooldown(cooldownMillis = 500L, onClick = onPauseClick)
                    .clip(CircleShape)
                    .background(OceanBlue.copy(alpha = 0.8f))
                    .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(14.dp)) {
                    val w = size.width
                    val h = size.height
                    drawLine(FoamWhite, Offset(w * 0.28f, h * 0.15f), Offset(w * 0.28f, h * 0.85f), 2.5f)
                    drawLine(FoamWhite, Offset(w * 0.72f, h * 0.15f), Offset(w * 0.72f, h * 0.85f), 2.5f)
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            HudItem(label = "Catches", value = "$catches/$targetCatches", color = SeaGreen)
            HudItem(label = "Misses", value = "$misses/$maxMisses", color = CoralOrange)
            HudItem(label = "Score", value = "$score", color = GoldFish)
        }
    }
}

@Composable
private fun HudItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = FoamWhite.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            fontFamily = GameFontFamily,
            fontSize = 18.sp,
            color = color
        )
    }
}

@Composable
private fun CatchZoneIndicator(
    centerFraction: Float,
    perfectFraction: Float,
    goodFraction: Float
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width * centerFraction
        val goodWidth = size.width * goodFraction
        val perfectWidth = size.width * perfectFraction

        // Good zone
        drawRect(
            color = Color(0x45FFD54F),
            topLeft = Offset(centerX - goodWidth / 2f, 0f),
            size = Size(goodWidth, size.height)
        )

        // Perfect zone
        drawRect(
            color = Color(0x5526A69A),
            topLeft = Offset(centerX - perfectWidth / 2f, 0f),
            size = Size(perfectWidth, size.height)
        )

        // Zone border lines
        val lineColor = Color(0x90FFFFFF)
        drawLine(lineColor, Offset(centerX - goodWidth / 2f, 0f), Offset(centerX - goodWidth / 2f, size.height), 2f)
        drawLine(lineColor, Offset(centerX + goodWidth / 2f, 0f), Offset(centerX + goodWidth / 2f, size.height), 2f)
        drawLine(Color(0x9026A69A), Offset(centerX - perfectWidth / 2f, 0f), Offset(centerX - perfectWidth / 2f, size.height), 2f)
        drawLine(Color(0x9026A69A), Offset(centerX + perfectWidth / 2f, 0f), Offset(centerX + perfectWidth / 2f, size.height), 2f)
    }
}

@Composable
private fun FishCanvas(
    progress: Float,
    bobOffset: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val x = size.width * progress
        val y = size.height * 0.45f + bobOffset
        drawSwimmingFish(x, y, 70f)
    }
}

private fun DrawScope.drawSwimmingFish(x: Float, y: Float, fishSize: Float) {
    val s = fishSize

    // -- Outer glow --
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x30FFB74D), Color(0x00FFB74D)),
            center = Offset(x, y),
            radius = s * 2.5f
        ),
        radius = s * 2.5f,
        center = Offset(x, y)
    )

    // -- Tail (behind body) --
    val tailColor1 = Color(0xFFE65100)
    val tailColor2 = Color(0xFFFF8F00)
    val tailPath = Path().apply {
        moveTo(x - s * 0.95f, y)
        cubicTo(x - s * 1.3f, y - s * 0.2f, x - s * 1.5f, y - s * 0.55f, x - s * 1.7f, y - s * 0.5f)
        cubicTo(x - s * 1.55f, y - s * 0.15f, x - s * 1.55f, y + s * 0.15f, x - s * 1.7f, y + s * 0.5f)
        cubicTo(x - s * 1.5f, y + s * 0.55f, x - s * 1.3f, y + s * 0.2f, x - s * 0.95f, y)
        close()
    }
    drawPath(path = tailPath, brush = Brush.verticalGradient(
        listOf(tailColor2, tailColor1),
        startY = y - s * 0.55f,
        endY = y + s * 0.55f
    ))

    // -- Bottom fin --
    val bottomFinPath = Path().apply {
        moveTo(x - s * 0.1f, y + s * 0.35f)
        quadraticTo(x - s * 0.15f, y + s * 0.7f, x - s * 0.4f, y + s * 0.65f)
        quadraticTo(x - s * 0.2f, y + s * 0.45f, x - s * 0.1f, y + s * 0.35f)
        close()
    }
    drawPath(path = bottomFinPath, color = Color(0xCCFF6D00))

    // -- Body --
    val bodyPath = Path().apply {
        moveTo(x - s, y)
        cubicTo(x - s * 0.6f, y - s * 0.6f, x + s * 0.5f, y - s * 0.52f, x + s, y - s * 0.02f)
        cubicTo(x + s * 0.5f, y + s * 0.5f, x - s * 0.6f, y + s * 0.6f, x - s, y)
        close()
    }
    drawPath(
        path = bodyPath,
        brush = Brush.verticalGradient(
            listOf(Color(0xFFFFAB40), Color(0xFFFF7043), Color(0xFFE64A19)),
            startY = y - s * 0.6f,
            endY = y + s * 0.6f
        )
    )

    // -- Scales pattern --
    for (row in 0..2) {
        for (col in 0..4) {
            val sx = x - s * 0.6f + col * s * 0.3f + (row % 2) * s * 0.15f
            val sy = y - s * 0.2f + row * s * 0.2f
            drawArc(
                color = Color(0x20FFFFFF),
                startAngle = 200f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = Offset(sx - s * 0.08f, sy - s * 0.06f),
                size = Size(s * 0.16f, s * 0.12f),
                style = Stroke(width = 1f)
            )
        }
    }

    // -- Belly highlight --
    val bellyPath = Path().apply {
        moveTo(x - s * 0.55f, y + s * 0.1f)
        cubicTo(x - s * 0.25f, y + s * 0.42f, x + s * 0.3f, y + s * 0.38f, x + s * 0.7f, y + s * 0.08f)
        cubicTo(x + s * 0.3f, y + s * 0.22f, x - s * 0.25f, y + s * 0.26f, x - s * 0.55f, y + s * 0.1f)
        close()
    }
    drawPath(path = bellyPath, color = Color(0x50FFECB3))

    // -- Top body shine --
    val shinePath = Path().apply {
        moveTo(x - s * 0.5f, y - s * 0.35f)
        cubicTo(x - s * 0.2f, y - s * 0.48f, x + s * 0.2f, y - s * 0.45f, x + s * 0.6f, y - s * 0.25f)
        cubicTo(x + s * 0.2f, y - s * 0.35f, x - s * 0.2f, y - s * 0.38f, x - s * 0.5f, y - s * 0.25f)
        close()
    }
    drawPath(path = shinePath, color = Color(0x35FFFFFF))

    // -- Dorsal fin (top) --
    val dorsalPath = Path().apply {
        moveTo(x - s * 0.15f, y - s * 0.45f)
        cubicTo(x - s * 0.05f, y - s * 0.95f, x + s * 0.25f, y - s * 0.85f, x + s * 0.4f, y - s * 0.4f)
        cubicTo(x + s * 0.2f, y - s * 0.5f, x + s * 0.0f, y - s * 0.55f, x - s * 0.15f, y - s * 0.45f)
        close()
    }
    drawPath(
        path = dorsalPath,
        brush = Brush.verticalGradient(
            listOf(Color(0xFFFF6D00), Color(0xFFFF8F00)),
            startY = y - s * 0.95f,
            endY = y - s * 0.4f
        )
    )
    // Fin rays
    for (i in 0..3) {
        val t = i / 4f
        val rxStart = x - s * 0.1f + t * s * 0.45f
        val ryStart = y - s * 0.48f - (1f - t * t) * s * 0.35f
        val rxEnd = x - s * 0.05f + t * s * 0.35f
        val ryEnd = y - s * 0.42f
        drawLine(Color(0x30FFFFFF), Offset(rxStart, ryStart), Offset(rxEnd, ryEnd), 0.8f)
    }

    // -- Pectoral fin (side) --
    val pectoralPath = Path().apply {
        moveTo(x + s * 0.15f, y + s * 0.1f)
        quadraticTo(x + s * 0.35f, y + s * 0.45f, x + s * 0.05f, y + s * 0.5f)
        quadraticTo(x + s * 0.1f, y + s * 0.25f, x + s * 0.15f, y + s * 0.1f)
        close()
    }
    drawPath(path = pectoralPath, color = Color(0xAAFF8F00))

    // -- Eye --
    // Eye white
    drawCircle(Color.White, s * 0.16f, Offset(x + s * 0.55f, y - s * 0.12f))
    // Iris
    drawCircle(Color(0xFF1B5E20), s * 0.11f, Offset(x + s * 0.57f, y - s * 0.12f))
    // Pupil
    drawCircle(Color(0xFF0A0A1A), s * 0.065f, Offset(x + s * 0.58f, y - s * 0.12f))
    // Eye highlight
    drawCircle(Color(0xCCFFFFFF), s * 0.04f, Offset(x + s * 0.54f, y - s * 0.15f))

    // -- Mouth --
    val mouthPath = Path().apply {
        moveTo(x + s * 0.92f, y + s * 0.02f)
        quadraticTo(x + s * 0.82f, y + s * 0.12f, x + s * 0.72f, y + s * 0.08f)
    }
    drawPath(
        path = mouthPath,
        color = Color(0xFF8B4513),
        style = Stroke(width = 2f)
    )

    // -- Gill line --
    val gillPath = Path().apply {
        moveTo(x + s * 0.35f, y - s * 0.25f)
        quadraticTo(x + s * 0.4f, y, x + s * 0.35f, y + s * 0.25f)
    }
    drawPath(
        path = gillPath,
        color = Color(0x40BF360C),
        style = Stroke(width = 1.5f)
    )

    // -- Lateral line --
    drawLine(
        color = Color(0x25FFFFFF),
        start = Offset(x - s * 0.7f, y),
        end = Offset(x + s * 0.3f, y - s * 0.03f),
        strokeWidth = 1f
    )
}

@Composable
private fun FeedbackText(result: CatchResult) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val (text, color) = when (result) {
            CatchResult.PERFECT -> "PERFECT!" to SeaGreen
            CatchResult.GOOD -> "GOOD!" to GoldFish
            CatchResult.MISS -> "MISS!" to CoralOrange
        }

        AnimatedVisibility(
            visible = true,
            enter = scaleIn(initialScale = 0.5f) + fadeIn()
        ) {
            Text(
                text = text,
                fontFamily = GameFontFamily,
                fontSize = 36.sp,
                color = color,
                modifier = Modifier
                    .background(
                        DeepOcean.copy(alpha = 0.6f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun CatchButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    val shape = CircleShape
    val alpha = if (enabled) 1f else 0.4f

    Box(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .size(110.dp)
            .pressableWithCooldown(cooldownMillis = 300L, enabled = enabled, onClick = onClick)
            .shadow(14.dp, shape, ambientColor = SeaBlue, spotColor = SeaBlue)
            .clip(shape)
            .background(
                Brush.radialGradient(
                    colors = listOf(CoralOrange, Color(0xFFD84315))
                ),
                alpha = alpha
            )
            .border(2.5f.dp, Color.White.copy(alpha = 0.3f * alpha), shape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "CATCH!",
            fontFamily = GameFontFamily,
            fontSize = 20.sp,
            color = FoamWhite.copy(alpha = alpha),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ResultOverlay(
    isWin: Boolean,
    score: Int,
    levelNumber: Int,
    onRetry: () -> Unit,
    onNextLevel: () -> Unit,
    onBackToLevels: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures { /* consume */ } }
            .background(DeepOcean.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        if (isWin) listOf(OceanBlue, SeaBlue.copy(alpha = 0.9f))
                        else listOf(Color(0xFF3E2723), Color(0xFF4E342E))
                    )
                )
                .border(
                    2.dp,
                    if (isWin) GoldFish.copy(alpha = 0.5f) else CoralOrange.copy(alpha = 0.5f),
                    RoundedCornerShape(20.dp)
                )
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isWin) "Level Complete!" else "Level Failed",
                fontFamily = GameFontFamily,
                fontSize = 28.sp,
                color = if (isWin) GoldFish else CoralOrange
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (isWin) {
                Text(
                    text = "Score: $score",
                    fontFamily = GameFontFamily,
                    fontSize = 22.sp,
                    color = FoamWhite
                )
            } else {
                Text(
                    text = "Better luck next time!",
                    fontSize = 16.sp,
                    color = FoamWhite.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            OceanButton(
                text = "Retry",
                onClick = onRetry,
                style = OceanButtonStyle.Primary,
                padding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
            )

            if (isWin) {
                Spacer(modifier = Modifier.height(12.dp))
                OceanButton(
                    text = if (levelNumber < 30) "Next Level" else "Finish",
                    onClick = onNextLevel,
                    style = OceanButtonStyle.Accent,
                    padding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OceanButton(
                text = "Levels",
                onClick = onBackToLevels,
                style = OceanButtonStyle.Secondary,
                padding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
            )
        }
    }
}

@Composable
private fun PauseOverlay(
    onResume: () -> Unit,
    onBackToLevels: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures { /* consume */ } }
            .background(DeepOcean.copy(alpha = 0.80f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .clip(RoundedCornerShape(20.dp))
                .background(OceanBlue.copy(alpha = 0.9f))
                .border(2.dp, AquaGlow.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Paused",
                fontFamily = GameFontFamily,
                fontSize = 32.sp,
                color = GoldFish
            )

            Spacer(modifier = Modifier.height(24.dp))

            OceanButton(
                text = "Resume",
                onClick = onResume,
                style = OceanButtonStyle.Accent,
                padding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OceanButton(
                text = "Back to Levels",
                onClick = onBackToLevels,
                style = OceanButtonStyle.Secondary,
                padding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
            )
        }
    }
}
