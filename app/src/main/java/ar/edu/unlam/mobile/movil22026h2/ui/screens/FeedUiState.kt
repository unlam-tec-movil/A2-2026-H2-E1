package ar.edu.unlam.mobile.movil22026h2.ui.screens

import ar.edu.unlam.mobile.movil22026h2.data.datasources.model.Post

//armamos el FeedUiState en el que controlamos los posts, asi manejamos el estado de cada post en cada llamado donde los necesitemos.
sealed interface FeedUiState {
    data object Loading : FeedUiState //estado en el que esperamos los posts

    //mostramos un resultado exitoso, mostrando el listado de posts
    data class Success(
        val posts: List<Post>
    ) : FeedUiState

    //manejamos el caso de error con un mensaje
    data class Error(
        val message: String
    ) : FeedUiState
}
