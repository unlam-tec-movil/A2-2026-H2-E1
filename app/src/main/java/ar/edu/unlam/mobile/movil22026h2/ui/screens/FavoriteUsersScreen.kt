package ar.edu.unlam.mobile.movil22026h2.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import ar.edu.unlam.mobile.movil22026h2.ui.components.FavoriteAuthorButton

const val FAVORITES_SCREEN_ROUTE = "favorites"

// Pantalla de prueba de HU-07: permite agregar, ver y quitar autores favoritos.
private val suggestedAuthors =
    listOf(
        "Ana Gomez",
        "Beto Diaz",
        "Carla Ruiz",
    )

@Composable
fun FavoriteUsersScreen(
    modifier: Modifier = Modifier,
    viewModel: FavoriteUsersViewModel = hiltViewModel(),
) {
    val favorites by viewModel.favorites.collectAsState()
    val favoriteNames by viewModel.favoriteNames.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var authorName by rememberSaveable { mutableStateOf("") }
    var avatarUrl by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            val message =
                when (event) {
                    is FavoriteEvent.Added -> "${event.name} agregado a favoritos"
                    is FavoriteEvent.Failed -> "No se pudo guardar: ${event.exception.message ?: "error"}"
                }
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Favoritos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            OutlinedTextField(
                value = authorName,
                onValueChange = { authorName = it },
                label = { Text("Nombre del autor") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            OutlinedTextField(
                value = avatarUrl,
                onValueChange = { avatarUrl = it },
                label = { Text("URL del avatar (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Button(
                onClick = {
                    viewModel.addFavorite(authorName.trim(), avatarUrl.trim())
                    authorName = ""
                    avatarUrl = ""
                },
                enabled = authorName.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Agregar a favoritos")
            }

            Text(
                text = "Autores sugeridos",
                style = MaterialTheme.typography.titleMedium,
            )

            suggestedAuthors.forEach { author ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = author, modifier = Modifier.weight(1f))
                    FavoriteAuthorButton(
                        authorName = author,
                        isFavorite = author in favoriteNames,
                        onClick = { viewModel.addFavorite(author, "") },
                    )
                }
            }

            Text(
                text = "Tus favoritos (${favorites.size})",
                style = MaterialTheme.typography.titleMedium,
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(favorites, key = { it.name }) { user ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = user.name, modifier = Modifier.weight(1f))
                        TextButton(onClick = { viewModel.removeFavorite(user.name) }) {
                            Text("Quitar")
                        }
                    }
                }
            }
        }
    }
}
