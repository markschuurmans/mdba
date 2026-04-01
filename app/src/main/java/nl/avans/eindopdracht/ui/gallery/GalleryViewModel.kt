package nl.avans.eindopdracht.ui.gallery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import nl.avans.eindopdracht.data.CocktailPhotoStore

class GalleryViewModel(application: Application) : AndroidViewModel(application) {

    private val photoStore = CocktailPhotoStore(application)

    private val _uiState = MutableStateFlow(GalleryUiState())
    val uiState: StateFlow<GalleryUiState> = _uiState.asStateFlow()

    fun refreshPhotos() {
        _uiState.update { it.copy(photos = photoStore.getAllPhotos()) }
    }
}

