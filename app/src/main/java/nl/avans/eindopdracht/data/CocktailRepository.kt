package nl.avans.eindopdracht.data

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import nl.avans.eindopdracht.model.Cocktail
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
}

