package ar.edu.unlam.mobile.movil22026h2.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import ar.edu.unlam.mobile.movil22026h2.data.datasources.model.Post
import ar.edu.unlam.mobile.movil22026h2.ui.components.AuthorAvatar
import ar.edu.unlam.mobile.movil22026h2.ui.components.FavoriteAuthorButton

const val HOME_SCREEN_ROUTE = "home"

// (HU-09): recibimos onPostClick desde MainActivity
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onPostClick: (Int) -> Unit = {},
) {
    val favoriteNames by viewModel.favoriteNames.collectAsState()
    val posts by viewModel.posts.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Escenario 1: al guardar un autor se muestra la confirmación.
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            val message =
                when (event) {
                    is FavoriteEvent.Added -> "${event.name} se guardó en favoritos"
                    is FavoriteEvent.Failed -> "No se pudo guardar: ${event.exception.message ?: "error"}"
                }
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text(
                    text = "Feed",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }

            /* VIEJO CODIGO
            items(posts, key = { it.id }) { post ->
                PostCard(
                    post = post,
                    // Escenario 2: si el nombre ya está en favoritos, el post lo indica.
                    isFavorite = post.authorName in favoriteNames,
                    onAddFavorite = { viewModel.addFavorite(post.authorName, post.authorAvatarUrl) },
                )
            }

             */

            items(posts, key = { it.id }) { post ->
                PostCard(
                    post = post,
                    // Escenario 2: si el nombre ya está en favoritos, el post lo indica.
                    isFavorite = post.authorName in favoriteNames,
                    onAddFavorite = { viewModel.addFavorite(post.authorName, post.authorAvatarUrl) },
                    onPostClick = { onPostClick(post.id) },
                )
            }
        }
    }
}

// (HU-09): clickable detecta el toque sobre toda la tarjeta.
@Composable
private fun PostCard(
    post: Post,
    isFavorite: Boolean,
    onAddFavorite: () -> Unit,
    onPostClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    /* VIEJO CODIGO
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AuthorAvatar(
                    authorName = post.authorName,
                    avatarUrl = post.authorAvatarUrl,
                )
                Text(
                    text = post.authorName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
            )

            FavoriteAuthorButton(
                authorName = post.authorName,
                isFavorite = isFavorite,
                onClick = onAddFavorite,
                modifier = Modifier.align(Alignment.End),
            )
        }
    }
     */
    Card(modifier = modifier.fillMaxWidth().clickable { onPostClick() }) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AuthorAvatar(
                    authorName = post.authorName,
                    avatarUrl = post.authorAvatarUrl,
                )
                Text(
                    text = post.authorName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
            )

            FavoriteAuthorButton(
                authorName = post.authorName,
                isFavorite = isFavorite,
                onClick = onAddFavorite,
                modifier = Modifier.align(Alignment.End),
            )
        }
    }
}
