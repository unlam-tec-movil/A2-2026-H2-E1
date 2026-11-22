package ar.edu.unlam.mobile.movil22026h2.ui.screens

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
import androidx.compose.material3.CircularProgressIndicator
import ar.edu.unlam.mobile.movil22026h2.ui.components.ErrorView
import androidx.compose.material3.Button
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged

const val HOME_SCREEN_ROUTE = "home"

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val favoriteNames by viewModel.favoriteNames.collectAsState()
    val feedUiState by viewModel.feedUiState.collectAsState() //HomeScreen observa el StateFlow que expone el ViewModel, cuando el estado cambia, compose detecta el nuevo valor y puede recomponer las partes de la interfaz que lo utilizan
    val additionalPosts by viewModel.additionalPosts.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val hasMorePages by viewModel.hasMorePages.collectAsState()
    val listState = rememberLazyListState()//recordar el desplazamiento
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


    val hasLoadedPosts =
        (feedUiState as? FeedUiState.Success)
            ?.posts?.isNotEmpty() == true

    LaunchedEffect(listState, hasLoadedPosts) {
        if (!hasLoadedPosts) return@LaunchedEffect

        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val lastVisibleItem =
                layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1

            layoutInfo.totalItemsCount >= 2 &&
                    lastVisibleItem >= layoutInfo.totalItemsCount - 2 &&
                    listState.isScrollInProgress
        }
            .distinctUntilChanged()
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

            when (val state = feedUiState) {//sumamos un case para manejar lo que mostramos, dependiendo del estado que llega por parametro desde el state

                FeedUiState.Loading -> {
                    item {
                        CircularProgressIndicator()
                    }
                }

                is FeedUiState.Success -> {
                    val allPosts =
                        (state.posts + additionalPosts).distinctBy { it.id }
                    if (state.posts.isEmpty()) {
                        item {
                            Text("Todavía no hay publicaciones")
                        }
                    } else {
                        items(allPosts, key = { it.id }) { post ->
                            PostCard(
                                post = post,
                                // Escenario 2: si el nombre ya está en favoritos, el post lo indica.
                                isFavorite = post.authorName in favoriteNames,
                                onAddFavorite = {
                                    viewModel.addFavorite(
                                        post.authorName,
                                        post.authorAvatarUrl
                                    )
                                },
                            )
                        }
                        if (isLoadingMore) {
                            item {
                                CircularProgressIndicator(
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }

                is FeedUiState.Error -> {
                    item {
                        ErrorView(
                            message = state.message,
                            onRetry = { viewModel.retryFeed() },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
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
    modifier: Modifier = Modifier,
) {
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
                Text(
                    text = "❤️ ${post.likes} Me gusta",
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
            )

            post.date?.let { date ->
                Text(
                    text = "Publicado: $date",
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            FavoriteAuthorButton(
                authorName = post.authorName,
                isFavorite = isFavorite,
                onClick = onAddFavorite,
                modifier = Modifier.align(Alignment.End),
            )
        }
    }
}
