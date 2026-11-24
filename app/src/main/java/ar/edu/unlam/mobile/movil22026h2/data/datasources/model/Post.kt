package ar.edu.unlam.mobile.movil22026h2.data.datasources.model

// Un post del feed. Todavía no hay backend, pero el modelo ya trae lo que
// necesita el favorito: nombre y avatar del autor.
// sumamos la fecha, los likes y el booleano para marcar si un post fue likeado o no.
data class Post(
    val id: Int,
    val authorName: String,
    val authorAvatarUrl: String,
    val content: String,
    val date: String? = null,
    val likes: Int = 0,
    val liked: Boolean = false,
)
