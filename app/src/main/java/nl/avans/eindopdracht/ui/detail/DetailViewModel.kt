package nl.avans.eindopdracht.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.avans.eindopdracht.data.CocktailRepository

class DetailViewModel(
    application: Application,
    private val cocktailId: String
) : AndroidViewModel(application) {

    private val repository = CocktailRepository(application)

    private val _uiState = MutableStateFlow(DetailUiState(isLoading = true))
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
    }

    fun loadDetail() {
        _uiState.update { current ->
            current.copy(isLoading = true, errorMessage = null)
        }

        viewModelScope.launch {
            runCatching { repository.fetchCocktailDetail(cocktailId) }
                .onSuccess { detail ->
                    _uiState.update {
                        it.copy(isLoading = false, cocktailDetail = detail, errorMessage = null)
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

    companion object {
        fun provideFactory(
            application: Application,
            cocktailId: String
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DetailViewModel(application, cocktailId) as T
                }
            }
        }
    }
}


