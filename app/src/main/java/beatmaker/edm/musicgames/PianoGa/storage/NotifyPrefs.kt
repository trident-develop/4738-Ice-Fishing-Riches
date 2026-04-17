package beatmaker.edm.musicgames.PianoGa.storage

import android.content.Context
import androidx.core.content.edit

class NotifyPrefs(context: Context) {

    private val prefs = context.getSharedPreferences("notify_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_NOTIFY_SHOWN = "notify_shown"
    }

    fun isNotifyShown(): Boolean {
        return prefs.getBoolean(KEY_NOTIFY_SHOWN, false)
    }

    fun markNotifyShown() {
        prefs.edit { putBoolean(KEY_NOTIFY_SHOWN, true) }
    }
}