package nl.avans.eindopdracht.ui.home

import nl.avans.eindopdracht.model.Cocktail

data class HomeUiState(
    val isLoading: Boolean = false,
    val cocktails: List<Cocktail> = emptyList(),
    val errorMessage: String? = null
)

