package ar.edu.unlam.mobile.movil22026h2.data.datasources.model

// Un post del feed. Todavía no hay backend, pero el modelo ya trae lo que
// necesita el favorito: nombre y avatar del autor.
data class Post(
    val id: Int,
    val authorName: String,
    val authorAvatarUrl: String,
    val content: String,
)
