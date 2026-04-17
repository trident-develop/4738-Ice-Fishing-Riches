package beatmaker.edm.musicgames.PianoGa.di

import androidx.activity.ComponentActivity
import beatmaker.edm.musicgames.PianoGa.logic.SolutionUseCase
import beatmaker.edm.musicgames.PianoGa.logic.StatisticParamsResolver
import beatmaker.edm.musicgames.PianoGa.model.StatisticComposer
import beatmaker.edm.musicgames.PianoGa.screens.privacy.Show3
import beatmaker.edm.musicgames.PianoGa.storage.NotifyPrefs
import beatmaker.edm.musicgames.PianoGa.storage.ScoreDao
import beatmaker.edm.musicgames.PianoGa.storage.ScoreDbHelper
import beatmaker.edm.musicgames.PianoGa.storage.ScoreStorage
import beatmaker.edm.musicgames.PianoGa.ui.components.ShiftCodec
import beatmaker.edm.musicgames.PianoGa.ui.components.ShiftCodec.DM
import beatmaker.edm.musicgames.PianoGa.ui.components.ShiftCodec.PR
import beatmaker.edm.musicgames.PianoGa.viewmodel.LoadingViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    single { ScoreDbHelper(get()) }
    single { ScoreDao(get()) }
    single { ScoreStorage(get()) }
    single { NotifyPrefs(get()) }

    single {
        StatisticParamsResolver(
            context = get()
        )
    }

    single { StatisticComposer(get()) }

    single {
        SolutionUseCase(
            storage = get(),
            statisticComposer = get(),
            baseStatistic = "${ShiftCodec.decode(DM)}/${ShiftCodec.decode(PR)}"
        )
    }

    viewModelOf(::LoadingViewModel)
}