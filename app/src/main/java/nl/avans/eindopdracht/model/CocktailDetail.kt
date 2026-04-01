package nl.avans.eindopdracht.model

data class CocktailDetail(
    val id: String,
    val name: String,
    val imageUrl: String,
    val instructions: String,
    val ingredients: List<String>
)

