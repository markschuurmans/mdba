package nl.avans.eindopdracht.ui.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import nl.avans.eindopdracht.data.CocktailPhotoStore
import nl.avans.eindopdracht.ui.common.LocalUriImage

@Composable
fun GalleryRoute(
    viewModel: GalleryViewModel,
    onOpenDetails: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshPhotos()
    }

    GalleryScreen(
        uiState = uiState,
        onOpenDetails = onOpenDetails,
        modifier = modifier
    )
}

@Composable
fun GalleryScreen(
    uiState: GalleryUiState,
    onOpenDetails: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (uiState.photos.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Je hebt nog geen foto's geupload.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(uiState.photos, key = { it.cocktailId }) { photo ->
            GalleryPhotoCard(photo = photo, onOpenDetails = onOpenDetails)
        }
    }
}

@Composable
private fun GalleryPhotoCard(
    photo: CocktailPhotoStore.StoredCocktailPhoto,
    onOpenDetails: (String) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LocalUriImage(
                uriString = photo.photoUri,
                contentDescription = "Foto van ${photo.cocktailName}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
            Text(
                text = photo.cocktailName,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Button(onClick = { onOpenDetails(photo.cocktailId) }) {
                Text("Ga naar details")
            }
        }
    }
}

