package nl.avans.eindopdracht.ui.common

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.android.volley.toolbox.ImageRequest
import nl.avans.eindopdracht.network.VolleySingleton

@Composable
fun VolleyNetworkImage(
    imageUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val requestQueue = remember(context) { VolleySingleton.getInstance(context) }

    var bitmap by remember(imageUrl) { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember(imageUrl) { mutableStateOf(true) }
    var isError by remember(imageUrl) { mutableStateOf(false) }

    DisposableEffect(imageUrl) {
        val request = ImageRequest(
            imageUrl,
            { loadedBitmap ->
                bitmap = loadedBitmap
                isLoading = false
                isError = false
            },
            0,
            0,
            null,
            Bitmap.Config.ARGB_8888,
            {
                isLoading = false
                isError = true
            }
        )
        requestQueue.add(request)

        onDispose {
            request.cancel()
        }
    }

    val loadedBitmap = bitmap

    when {
        loadedBitmap != null -> {
            Image(
                bitmap = loadedBitmap.asImageBitmap(),
                contentDescription = contentDescription,
                modifier = modifier,
                contentScale = ContentScale.Crop
            )
        }

        isLoading -> {
            Box(
                modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            }
        }

        isError -> {
            Box(
                modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Geen afbeelding")
            }
        }

        else -> {
            Box(
                modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }
    }
}



