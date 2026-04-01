package nl.avans.eindopdracht.ui.common

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext

@Composable
fun LocalUriImage(
    uriString: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val bitmap = remember(uriString) {
        runCatching {
            context.contentResolver.openInputStream(Uri.parse(uriString)).use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        }.getOrNull()
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text("Foto niet beschikbaar")
        }
    }
}

