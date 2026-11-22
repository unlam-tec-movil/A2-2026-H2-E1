package ar.edu.unlam.mobile.movil22026h2.data.datasources.model

// Un post del feed. Trae lo que necesita el favorito de autor (HU-07) y
// lo que necesita el "me gusta" (HU-11). Los valores por defecto mantienen
// compatibles los posts de ejemplo que no tienen likes.
data class Post(
    val id: Int,
    val authorName: String,
    val authorAvatarUrl: String,
    val content: String,
    val likes: Int = 0,
    val liked: Boolean = false,
)
