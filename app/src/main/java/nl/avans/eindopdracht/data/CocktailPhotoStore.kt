package nl.avans.eindopdracht.data

import android.content.Context
import androidx.core.content.edit

class CocktailPhotoStore(context: Context) {

    data class StoredCocktailPhoto(
        val cocktailId: String,
        val cocktailName: String,
        val photoUri: String
    )

    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getPhotoUri(cocktailId: String): String? {
        return preferences.getString(photoUriKey(cocktailId), null)
    }

    fun savePhotoUri(cocktailId: String, cocktailName: String, uri: String) {
        preferences.edit {
            putString(photoUriKey(cocktailId), uri)
            putString(photoNameKey(cocktailId), cocktailName)
        }
    }

    fun getAllPhotos(): List<StoredCocktailPhoto> {
        return preferences.all
            .asSequence()
            .mapNotNull { (key, value) ->
                if (!key.startsWith(PHOTO_URI_PREFIX)) return@mapNotNull null
                val cocktailId = key.removePrefix(PHOTO_URI_PREFIX)
                val photoUri = value as? String ?: return@mapNotNull null
                val cocktailName = preferences.getString(photoNameKey(cocktailId), null)
                    ?: "Cocktail $cocktailId"
                StoredCocktailPhoto(cocktailId, cocktailName, photoUri)
            }
            .sortedBy { it.cocktailName }
            .toList()
    }

    private fun photoUriKey(cocktailId: String): String = "$PHOTO_URI_PREFIX$cocktailId"

    private fun photoNameKey(cocktailId: String): String = "$PHOTO_NAME_PREFIX$cocktailId"

    private companion object {
        const val PREFS_NAME = "cocktail_photo_store"
        const val PHOTO_URI_PREFIX = "photo_uri_"
        const val PHOTO_NAME_PREFIX = "photo_name_"
    }
}


