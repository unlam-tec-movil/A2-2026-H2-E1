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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import ar.edu.unlam.mobile.movil22026h2.data.datasources.model.Post
import ar.edu.unlam.mobile.movil22026h2.ui.components.AuthorAvatar
import ar.edu.unlam.mobile.movil22026h2.ui.components.ErrorView
import ar.edu.unlam.mobile.movil22026h2.ui.components.FavoriteAuthorButton
import kotlinx.coroutines.flow.distinctUntilChanged

const val HOME_SCREEN_ROUTE = "home"

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onPostClick: (Int) -> Unit = {},
) {
    val favoriteNames by viewModel.favoriteNames.collectAsState()
    // Observa los estados del feed. Compose actualiza la interfaz cuando cambia el estado.
    val feedUiState by viewModel.feedUiState.collectAsState()
    val additionalPosts by viewModel.additionalPosts.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    // VIEJO CODIGO
    // val posts by viewModel.posts.collectAsState()
    // Ese viewModel.posts no existe en el HomeViewModel de A2. Lo sacamos del
    // estado del feed, que es donde ya vienen los posts de verdad.
    val posts: List<Post> =
        when (val state = feedUiState) {
            is FeedUiState.Success -> (state.posts + additionalPosts).distinctBy { it.id }
            else -> emptyList()
        }

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

    val hasLoadedPosts = posts.isNotEmpty()

    LaunchedEffect(listState, hasLoadedPosts) {
        if (!hasLoadedPosts) return@LaunchedEffect

        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val lastVisibleItem =
                layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1

            layoutInfo.totalItemsCount >= 2 &&
                    lastVisibleItem >= layoutInfo.totalItemsCount - 2 &&
                    listState.isScrollInProgress
        }.distinctUntilChanged()
            .collect { nearBottom ->
                if (nearBottom) {
                    viewModel.loadNextPage()
                }
            }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        LazyColumn(
            state = listState,
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

            // Una sola lista:evita dibujar los posts dos veces y hace que
            // TODAS las tarjetas reciban el click para ir al detalle.
            items(posts, key = { it.id }) { post ->
                PostCard(
                    post = post,
                    isFavorite = post.authorName in favoriteNames,
                    onAddFavorite = { viewModel.addFavorite(post.authorName, post.authorAvatarUrl) },
                    onPostClick = { onPostClick(post.id) },
                )
            }

            when (val state = feedUiState) {
                FeedUiState.Loading -> {
                    item { CircularProgressIndicator() }
                }

                is FeedUiState.Success -> Unit

                is FeedUiState.Error -> {
                    item {
                        ErrorView(
                            message = state.message,
                            onRetry = { viewModel.retryFeed() },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }

            if (isLoadingMore && posts.isNotEmpty()) {
                item {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Composable
private fun PostCard(
    post: Post,
    isFavorite: Boolean,
    onAddFavorite: () -> Unit,
    onPostClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // VIEJO CODIGO
    // Card(modifier = modifier.fillMaxWidth()) {

    // clickable detecta el toque sobre toda la tarjeta y avisa a MainActivity.
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

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = post.authorName,
                        modifier = Modifier.alignByBaseline(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )

                    post.date?.let { date ->
                        Text(
                            text = date.take(16).replace("T", " · "),
                            modifier = Modifier.alignByBaseline(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier =
                        Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 8.dp,
                        ),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = "${post.likes} Me gusta",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                FavoriteAuthorButton(
                    authorName = post.authorName,
                    isFavorite = isFavorite,
                    onClick = onAddFavorite,
                )
            }
        }
    }
}