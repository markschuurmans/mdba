package nl.avans.eindopdracht.ui.detail

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import nl.avans.eindopdracht.model.CocktailDetail
import nl.avans.eindopdracht.ui.common.VolleyNetworkImage

@Composable
fun DetailRoute(
    viewModel: DetailViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

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
        modifier = modifier
    )
}

@Composable
fun DetailScreen(
    uiState: DetailUiState,
    onRetry: () -> Unit,
    onShareRecipe: (CocktailDetail) -> Unit,
    modifier: Modifier = Modifier
) {
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

