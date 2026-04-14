package beatmaker.edm.musicgames.PianoGa.audio

import android.content.Context
import android.media.MediaPlayer
import beatmaker.edm.musicgames.PianoGa.R

class SoundManager(private val context: Context) {

    private var musicPlayer: MediaPlayer? = null
    private var isMusicEnabled = true
    private var isSoundEnabled = true
    private var isMusicPrepared = false

    fun setMusicEnabled(enabled: Boolean) {
        isMusicEnabled = enabled
        if (enabled) {
            startMusic()
        } else {
            pauseMusic()
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        isSoundEnabled = enabled
    }

    fun startMusic() {
        if (!isMusicEnabled) return
        if (musicPlayer == null) {
            musicPlayer = MediaPlayer.create(context, R.raw.game_music).apply {
                isLooping = true
                setVolume(0.5f, 0.5f)
            }
            isMusicPrepared = true
        }
        if (isMusicPrepared && musicPlayer?.isPlaying == false) {
            musicPlayer?.start()
        }
    }

    fun pauseMusic() {
        if (isMusicPrepared && musicPlayer?.isPlaying == true) {
            musicPlayer?.pause()
        }
    }

    fun resumeMusic() {
        if (isMusicEnabled && isMusicPrepared && musicPlayer?.isPlaying == false) {
            musicPlayer?.start()
        }
    }

    fun stopMusic() {
        musicPlayer?.stop()
        musicPlayer?.release()
        musicPlayer = null
        isMusicPrepared = false
    }

    fun playWinSound() {
        if (!isSoundEnabled) return
        playOneShot(R.raw.level_win)
    }

    fun playLoseSound() {
        if (!isSoundEnabled) return
        playOneShot(R.raw.level_lose)
    }

    private fun playOneShot(resId: Int) {
        val player = MediaPlayer.create(context, resId)
        player?.setOnCompletionListener { it.release() }
        player?.start()
    }

    fun release() {
        stopMusic()
    }
}
