package ar.edu.unlam.mobile.movil22026h2.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import ar.edu.unlam.mobile.movil22026h2.data.datasources.model.FavoriteUser
import ar.edu.unlam.mobile.movil22026h2.ui.components.AuthorAvatar

const val FAVORITES_SCREEN_ROUTE = "favorites"

@Composable
fun FavoriteUsersScreen(
    modifier: Modifier = Modifier,
    viewModel: FavoriteUsersViewModel = hiltViewModel(),
) {
    val favorites by viewModel.favorites.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            val message =
                when (event) {
                    is FavoriteEvent.Added -> "${event.name} se guardó en favoritos"
                    is FavoriteEvent.Failed -> "No se pudo completar la operación"
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

            if (favorites.isEmpty()) {
                Text(
                    text = "Todavía no guardaste ningún autor. Agregalos desde el feed.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(favorites, key = { it.name }) { user ->
                        FavoriteRow(
                            user = user,
                            onRemove = { viewModel.removeFavorite(user.name) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteRow(
    user: FavoriteUser,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AuthorAvatar(
                authorName = user.name,
                avatarUrl = user.avatarUrl,
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Guardado en el dispositivo",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            TextButton(onClick = onRemove) {
                Text("Quitar")
            }
        }
    }
}
