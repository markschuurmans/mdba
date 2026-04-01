package nl.avans.eindopdracht.ui.detail

import android.Manifest
import android.content.res.Configuration
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import nl.avans.eindopdracht.data.CocktailPhotoStore
import nl.avans.eindopdracht.model.CocktailDetail
import nl.avans.eindopdracht.ui.common.LocalUriImage
import nl.avans.eindopdracht.ui.common.VolleyNetworkImage

@Composable
fun DetailRoute(
    viewModel: DetailViewModel,
    cocktailId: String,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val photoStore = remember(context) { CocktailPhotoStore(context) }

    var userPhotoUri by rememberSaveable(cocktailId) { mutableStateOf<String?>(null) }

    LaunchedEffect(cocktailId) {
        userPhotoUri = photoStore.getPhotoUri(cocktailId)
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        runCatching {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }

        val uriText = uri.toString()
        userPhotoUri = uriText
        val cocktailName = uiState.cocktailDetail?.name ?: "Cocktail $cocktailId"
        photoStore.savePhotoUri(cocktailId, cocktailName, uriText)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            pickImageLauncher.launch(arrayOf("image/*"))
        } else {
            Toast.makeText(context, "Toegang tot afbeeldingen geweigerd", Toast.LENGTH_SHORT)
                .show()
        }
    }

    DetailScreen(
        uiState = uiState,
        onRetry = viewModel::loadDetail,
        onShareRecipe = { detail ->
            val ingredientsText = detail.ingredients.joinToString(separator = "\n") { "- $it" }
            val shareText = buildString {
                appendLine(detail.name)
                appendLine()
                appendLine("Ingredienten:")
                appendLine(ingredientsText)
                appendLine()
                appendLine("Instructies:")
                appendLine(detail.instructions)
            }

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Cocktail Recept: ${detail.name}")
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Deel recept via"))
        },
        userPhotoUri = userPhotoUri,
        onPickUserPhoto = {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                pickImageLauncher.launch(arrayOf("image/*"))
                return@DetailScreen
            }

            val permissionStatus = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_IMAGES
            )
            if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                pickImageLauncher.launch(arrayOf("image/*"))
            } else {
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        },
        modifier = modifier
    )
}

@Composable
fun DetailScreen(
    uiState: DetailUiState,
    onRetry: () -> Unit,
    onShareRecipe: (CocktailDetail) -> Unit,
    userPhotoUri: String?,
    onPickUserPhoto: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    when {
        uiState.isLoading -> {
            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Text(text = "Details laden...", modifier = Modifier.padding(top = 12.dp))
            }
        }

        uiState.errorMessage != null -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Fout bij ophalen: ${uiState.errorMessage}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Button(onClick = onRetry, modifier = Modifier.padding(top = 12.dp)) {
                    Text("Opnieuw proberen")
                }
            }
        }

        uiState.cocktailDetail != null -> {
            val detail = uiState.cocktailDetail
            if (isLandscape) {
                Row(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            VolleyNetworkImage(
                                imageUrl = detail.imageUrl,
                                contentDescription = detail.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(260.dp)
                            )
                        }
                        item {
                            Text(text = detail.name, style = MaterialTheme.typography.headlineSmall)
                        }
                        item {
                            Text(text = "Jouw Foto", style = MaterialTheme.typography.titleMedium)
                        }
                        item {
                            if (userPhotoUri != null) {
                                LocalUriImage(
                                    uriString = userPhotoUri,
                                    contentDescription = "Eigen foto voor ${detail.name}",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(220.dp)
                                )
                            } else {
                                Text(text = "Nog geen eigen foto gekozen")
                            }
                        }
                        item {
                            Button(onClick = onPickUserPhoto) {
                                Text(text = "Upload Eigen Foto")
                            }
                        }
                    }

                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(16.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(text = "Ingredienten", style = MaterialTheme.typography.titleMedium)
                        }
                        items(detail.ingredients) { ingredient ->
                            Text(text = "- $ingredient", style = MaterialTheme.typography.bodyLarge)
                        }
                        item {
                            Text(text = "Instructies", style = MaterialTheme.typography.titleMedium)
                        }
                        item {
                            Text(text = detail.instructions, style = MaterialTheme.typography.bodyLarge)
                        }
                        item {
                            Button(onClick = { onShareRecipe(detail) }) {
                                Text(text = "Deel Recept")
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        VolleyNetworkImage(
                            imageUrl = detail.imageUrl,
                            contentDescription = detail.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                        )
                    }
                    item {
                        Text(text = detail.name, style = MaterialTheme.typography.headlineSmall)
                    }
                    item {
                        Text(text = "Ingredienten", style = MaterialTheme.typography.titleMedium)
                    }
                    items(detail.ingredients) { ingredient ->
                        Text(text = "- $ingredient", style = MaterialTheme.typography.bodyLarge)
                    }
                    item {
                        Text(text = "Instructies", style = MaterialTheme.typography.titleMedium)
                    }
                    item {
                        Text(text = detail.instructions, style = MaterialTheme.typography.bodyLarge)
                    }
                    item {
                        Button(onClick = { onShareRecipe(detail) }) {
                            Text(text = "Deel Recept")
                        }
                    }
                    item {
                        Text(text = "Jouw Foto", style = MaterialTheme.typography.titleMedium)
                    }
                    item {
                        if (userPhotoUri != null) {
                            LocalUriImage(
                                uriString = userPhotoUri,
                                contentDescription = "Eigen foto voor ${detail.name}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                            )
                        } else {
                            Text(text = "Nog geen eigen foto gekozen")
                        }
                    }
                    item {
                        Button(onClick = onPickUserPhoto) {
                            Text(text = "Upload Eigen Foto")
                        }
                    }
                }
            }
        }

        else -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Geen detailinformatie beschikbaar")
            }
        }
    }
}

