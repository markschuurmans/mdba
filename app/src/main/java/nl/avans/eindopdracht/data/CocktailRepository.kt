package nl.avans.eindopdracht.data

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import nl.avans.eindopdracht.model.Cocktail
import nl.avans.eindopdracht.model.CocktailDetail
import nl.avans.eindopdracht.network.ApiConfig
import nl.avans.eindopdracht.network.VolleySingleton

class CocktailRepository(context: Context) {

    private val volley = VolleySingleton.getInstance(context)

    suspend fun fetchCocktails(): List<Cocktail> = suspendCancellableCoroutine { continuation ->
        val request = JsonObjectRequest(
            Request.Method.GET,
            ApiConfig.BASE_URL + ApiConfig.COCKTAIL_LIST_PATH,
            null,
            { response ->
                try {
                    val drinks = response.optJSONArray("drinks")
                    if (drinks == null) {
                        continuation.resume(emptyList())
                        return@JsonObjectRequest
                    }

                    val cocktails = buildList {
                        for (index in 0 until drinks.length()) {
                            val item = drinks.optJSONObject(index) ?: continue
                            add(
                                Cocktail(
                                    id = item.optString("idDrink"),
                                    name = item.optString("strDrink"),
                                    thumbnailUrl = item.optString("strDrinkThumb")
                                )
                            )
                        }
                    }
                    continuation.resume(cocktails)
                } catch (exception: Exception) {
                    continuation.resumeWithException(exception)
                }
            },
            { error ->
                continuation.resumeWithException(error)
            }
        )

        volley.add(request)

        continuation.invokeOnCancellation {
            request.cancel()
        }
    }

    suspend fun fetchCocktailDetail(cocktailId: String): CocktailDetail =
        suspendCancellableCoroutine { continuation ->
            val request = JsonObjectRequest(
                Request.Method.GET,
                ApiConfig.BASE_URL + ApiConfig.cocktailDetailPath(cocktailId),
                null,
                { response ->
                    try {
                        val drink = response.optJSONArray("drinks")?.optJSONObject(0)
                            ?: throw IllegalStateException("Geen cocktaildetails gevonden")

                        continuation.resume(
                            CocktailDetail(
                                id = drink.optString("idDrink"),
                                name = drink.optString("strDrink"),
                                imageUrl = drink.optString("strDrinkThumb"),
                                instructions = drink.optString("strInstructions"),
                                ingredients = parseIngredients(drink)
                            )
                        )
                    } catch (exception: Exception) {
                        continuation.resumeWithException(exception)
                    }
                },
                { error ->
                    continuation.resumeWithException(error)
                }
            )

            volley.add(request)

            continuation.invokeOnCancellation {
                request.cancel()
            }
        }

    private fun parseIngredients(drink: JSONObject): List<String> {
        val ingredients = mutableListOf<String>()
        for (index in 1..15) {
            val ingredient = drink.optString("strIngredient$index").trim()
            if (ingredient.isBlank() || ingredient == "null") continue

            val measure = drink.optString("strMeasure$index").trim()
            val line = if (measure.isBlank() || measure == "null") ingredient else "$measure $ingredient"
            ingredients.add(line)
        }
        return ingredients
    }
}

