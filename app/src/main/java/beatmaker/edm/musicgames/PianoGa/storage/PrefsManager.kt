package beatmaker.edm.musicgames.PianoGa.storage

import android.content.Context
import android.content.SharedPreferences

class PrefsManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var highestUnlockedLevel: Int
        get() = prefs.getInt(KEY_HIGHEST_UNLOCKED_LEVEL, 1)
        set(value) = prefs.edit().putInt(KEY_HIGHEST_UNLOCKED_LEVEL, value).apply()

    var musicEnabled: Boolean
        get() = prefs.getBoolean(KEY_MUSIC_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_MUSIC_ENABLED, value).apply()

    var soundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()

    var bestCompletedLevel: Int
        get() = prefs.getInt(KEY_BEST_COMPLETED_LEVEL, 0)
        set(value) = prefs.edit().putInt(KEY_BEST_COMPLETED_LEVEL, value).apply()

    var totalScore: Int
        get() = prefs.getInt(KEY_TOTAL_SCORE, 0)
        set(value) = prefs.edit().putInt(KEY_TOTAL_SCORE, value).apply()

    var bestLevelScore: Int
        get() = prefs.getInt(KEY_BEST_LEVEL_SCORE, 0)
        set(value) = prefs.edit().putInt(KEY_BEST_LEVEL_SCORE, value).apply()

    fun unlockNextLevel(currentLevel: Int) {
        val next = currentLevel + 1
        if (next > highestUnlockedLevel && next <= 30) {
            highestUnlockedLevel = next
        }
    }

    fun recordLevelCompletion(level: Int, score: Int) {
        if (level > bestCompletedLevel) {
            bestCompletedLevel = level
        }
        if (score > bestLevelScore) {
            bestLevelScore = score
        }
        totalScore += score
    }

    companion object {
        private const val PREFS_NAME = "ice_fishing_riches_prefs"
        private const val KEY_HIGHEST_UNLOCKED_LEVEL = "highest_unlocked_level"
        private const val KEY_MUSIC_ENABLED = "music_enabled"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_BEST_COMPLETED_LEVEL = "best_completed_level"
        private const val KEY_TOTAL_SCORE = "total_score"
        private const val KEY_BEST_LEVEL_SCORE = "best_level_score"
    }
}
