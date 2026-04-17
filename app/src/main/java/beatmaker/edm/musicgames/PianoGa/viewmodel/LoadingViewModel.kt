package beatmaker.edm.musicgames.PianoGa.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import beatmaker.edm.musicgames.PianoGa.logic.SolutionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoadingViewModel(
    private val solutionUseCase: SolutionUseCase
) : ViewModel() {

    private val _scoreState = MutableStateFlow<String?>(null)
    val scoreState: StateFlow<String?> = _scoreState

    fun loadScore() {

        viewModelScope.launch {
            val score = solutionUseCase()

//            log("ViewModel: final link = $score")

            _scoreState.value = score
        }
    }
}