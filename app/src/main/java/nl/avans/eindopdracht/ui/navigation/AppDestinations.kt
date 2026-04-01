package nl.avans.eindopdracht.ui.navigation

object AppDestinations {
    const val HOME = "home"
    const val GALLERY = "gallery"
    const val DETAIL = "detail"
    const val COCKTAIL_ID_ARG = "cocktailId"
    const val DETAIL_ROUTE = "$DETAIL/{$COCKTAIL_ID_ARG}"

    fun detailRoute(cocktailId: String): String {
        return "$DETAIL/$cocktailId"
    }
}

