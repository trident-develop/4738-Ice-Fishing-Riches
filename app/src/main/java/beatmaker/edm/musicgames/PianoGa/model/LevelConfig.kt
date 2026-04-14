package beatmaker.edm.musicgames.PianoGa.model

data class LevelConfig(
    val levelNumber: Int,
    val targetCatches: Int,
    val maxMisses: Int,
    val fishSpeedMs: Long,
    val perfectZoneFraction: Float,
    val goodZoneFraction: Float,
    val perfectPoints: Int,
    val goodPoints: Int
)

object LevelData {
    val levels: List<LevelConfig> = (1..30).map { level ->
        LevelConfig(
            levelNumber = level,
            targetCatches = 3 + (level - 1) / 3,
            maxMisses = maxOf(2, 5 - (level - 1) / 6),
            fishSpeedMs = (3500.0 - 1900.0 * (1.0 - 1.0 / (1.0 + (level - 1) * 0.12))).toLong().coerceIn(1400L, 3500L),
            perfectZoneFraction = maxOf(0.04f, 0.10f - (level - 1) * 0.002f),
            goodZoneFraction = maxOf(0.07f, 0.16f - (level - 1) * 0.003f),
            perfectPoints = 100 + level * 5,
            goodPoints = 50 + level * 2
        )
    }

    fun getLevel(number: Int): LevelConfig = levels[number - 1]
}
