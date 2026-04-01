package nl.avans.eindopdracht.data

import android.content.Context

class CocktailPhotoStore(context: Context) {

    data class StoredCocktailPhoto(
        val cocktailId: String,
        val cocktailName: String,
        val photoUri: String
    )

    private val preferences = context.getSharedPreferences("cocktail_photo_store", Context.MODE_PRIVATE)

    fun getPhotoUri(cocktailId: String): String? {
        return preferences.getString("photo_uri_$cocktailId", null)
    }

    fun savePhotoUri(cocktailId: String, cocktailName: String, uri: String) {
        preferences.edit()
            .putString("photo_uri_$cocktailId", uri)
            .putString("photo_name_$cocktailId", cocktailName)
            .apply()
    }

    fun getAllPhotos(): List<StoredCocktailPhoto> {
        return preferences.all
            .asSequence()
            .mapNotNull { (key, value) ->
                if (!key.startsWith("photo_uri_")) return@mapNotNull null
                val cocktailId = key.removePrefix("photo_uri_")
                val photoUri = value as? String ?: return@mapNotNull null
                val cocktailName = preferences.getString("photo_name_$cocktailId", null)
                    ?: "Cocktail $cocktailId"
                StoredCocktailPhoto(cocktailId, cocktailName, photoUri)
            }
            .sortedBy { it.cocktailName }
            .toList()
    }
}


