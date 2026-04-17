package beatmaker.edm.musicgames.PianoGa.storage

import beatmaker.edm.musicgames.PianoGa.audio.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScoreStorage(
    private val scoreDao: ScoreDao
) {

    suspend fun getSavedScore(): String? = withContext(Dispatchers.IO) {
        val result = scoreDao.getCachedScore()?.score
        result
    }

    suspend fun saveScore(link: String) = withContext(Dispatchers.IO) {
//        log("Storage: saveLink = $link")
        scoreDao.saveCachedScore(link)
    }

    suspend fun clearScore() = withContext(Dispatchers.IO) {
        scoreDao.clearCachedScore()
    }
}