package nl.avans.eindopdracht.network

object ApiConfig {
    const val BASE_URL = "https://www.thecocktaildb.com/api/json/v1/1/"
    const val COCKTAIL_LIST_PATH = "filter.php?c=Cocktail"

    fun cocktailDetailPath(cocktailId: String): String = "lookup.php?i=$cocktailId"
}

