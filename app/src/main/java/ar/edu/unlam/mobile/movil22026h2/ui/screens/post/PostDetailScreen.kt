package ar.edu.unlam.mobile.movil22026h2.ui.screens.post

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import ar.edu.unlam.mobile.movil22026h2.data.datasources.model.Post
import ar.edu.unlam.mobile.movil22026h2.ui.components.AuthorAvatar
import ar.edu.unlam.mobile.movil22026h2.ui.components.EmptyView
import ar.edu.unlam.mobile.movil22026h2.ui.components.ErrorView
import ar.edu.unlam.mobile.movil22026h2.ui.components.LoadingView

const val POST_DETAIL_SCREEN_ROUTE = "post_detail/{postId}"

fun postDetailRoute(postId: Int): String = "post_detail/$postId"

@Composable
fun PostDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: PostViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(modifier = modifier) { padding ->
        val contentModifier = Modifier.padding(padding)
        val post = state.post

        when {
            state.isLoading -> LoadingView(modifier = contentModifier)

            state.error != null ->
                ErrorView(
                    message = state.error ?: "Ocurrió un error",
                    onRetry = viewModel::retry,
                    modifier = contentModifier,
                )

            post == null ->
                EmptyView(
                    message = "No se pudo cargar el post.",
                    modifier = contentModifier,
                )

            else ->
                DetailContent(
                    post = post,
                    replies = state.replies,
                    modifier = contentModifier,
                )
        }
    }
}

@Composable
private fun DetailContent(
    post: Post,
    replies: List<Post>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Detalle del post",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )

        PostCardDetail(post = post)

        Text(
            text = "Respuestas (${replies.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )

        if (replies.isEmpty()) {
            Text(
                text = "Todavía no tiene respuestas.",
                style = MaterialTheme.typography.bodyMedium,
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(replies, key = { it.id }) { reply ->
                    ReplyCard(reply = reply)
                }
            }
        }
    }
}

@Composable
private fun PostCardDetail(
    post: Post,
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
                AuthorAvatar(authorName = post.authorName, avatarUrl = post.authorAvatarUrl)
                Column {
                    Text(
                        text = post.authorName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    // En A2 date es nullable: por eso usamos ?. y no . directo.
                    if (post.date?.isNotBlank() == true) {
                        Text(text = post.date.orEmpty(), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            Text(text = post.content, style = MaterialTheme.typography.bodyMedium)
            Text(text = "Me gusta: ${post.likes}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun ReplyCard(
    reply: Post,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AuthorAvatar(authorName = reply.authorName, avatarUrl = reply.authorAvatarUrl)
            Column {
                Text(
                    text = reply.authorName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(text = reply.content, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}