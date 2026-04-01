package nl.avans.eindopdracht.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.avans.eindopdracht.data.CocktailRepository

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CocktailRepository(application)

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadCocktails()
    }

    fun loadCocktails() {
        _uiState.update { current ->
            current.copy(isLoading = true, errorMessage = null)
        }

        viewModelScope.launch {
            runCatching { repository.fetchCocktails() }
                .onSuccess { cocktails ->
                    _uiState.update {
                        it.copy(isLoading = false, cocktails = cocktails, errorMessage = null)
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.localizedMessage ?: "Onbekende netwerkfout"
                        )
                    }
                }
        }
    }
}

