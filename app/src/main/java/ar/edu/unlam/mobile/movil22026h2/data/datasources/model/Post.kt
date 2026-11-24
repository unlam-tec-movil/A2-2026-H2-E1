package ar.edu.unlam.mobile.movil22026h2.data.datasources.model

// Un post del feed. Todavía no hay backend, pero el modelo ya trae lo que
// necesita el favorito: nombre y avatar del autor.
data class Post(
    val id: Int,
    val authorName: String,
    val authorAvatarUrl: String,
    val content: String,
    val parentId: Int = 0, // creamos para saber que se trata de una publicacion y no del valor de padre
    val likes: Int = 0, // iniciamo en cero el valor de los like que tiene el post
    val liked: Boolean = false, // variable de tipo boolean que permite determinar
    // si el usuario indico que le gusta la respuesta
    val date: String = "", // varible que determina la fecha de la publicacion
)
