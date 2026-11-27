package ar.edu.unlam.mobile.movil22026h2.data.datasources.model

// Un post del feed: nombre y avatar del autor (HU-07), fecha (R1/HU-01) y
// lo que necesita el "me gusta" (HU-11). Los valores por defecto mantienen
// compatibles los posts de ejemplo que no traen esos datos.
data class Post(
    val id: Int,
    val authorName: String,
    val authorAvatarUrl: String,
    val content: String,
    val date: String? = null,
    val likes: Int = 0,
    val liked: Boolean = false,
)
