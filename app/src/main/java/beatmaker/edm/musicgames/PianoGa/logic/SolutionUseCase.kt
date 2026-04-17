package beatmaker.edm.musicgames.PianoGa.logic

import beatmaker.edm.musicgames.PianoGa.audio.log
import beatmaker.edm.musicgames.PianoGa.model.StatisticComposer
import beatmaker.edm.musicgames.PianoGa.storage.ScoreStorage

class SolutionUseCase(
    private val storage: ScoreStorage,
    private val statisticComposer: StatisticComposer,
    private val baseStatistic: String
) {

    suspend operator fun invoke(): String {

        val score = storage.getSavedScore()

        if (!score.isNullOrBlank()) {
//            log("UseCase: link from DB = $score")
            return score
        }

        val finalStatistic = statisticComposer.compose(baseStatistic)

//        log("UseCase: final link built = $finalStatistic")

        return finalStatistic
    }
}