package nl.avans.eindopdracht.ui.detail

import nl.avans.eindopdracht.model.CocktailDetail

data class DetailUiState(
    val isLoading: Boolean = false,
    val cocktailDetail: CocktailDetail? = null,
    val errorMessage: String? = null
)

