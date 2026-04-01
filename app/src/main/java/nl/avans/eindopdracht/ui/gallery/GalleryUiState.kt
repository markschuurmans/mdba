package nl.avans.eindopdracht.ui.gallery

import nl.avans.eindopdracht.data.CocktailPhotoStore

data class GalleryUiState(
    val photos: List<CocktailPhotoStore.StoredCocktailPhoto> = emptyList()
)

