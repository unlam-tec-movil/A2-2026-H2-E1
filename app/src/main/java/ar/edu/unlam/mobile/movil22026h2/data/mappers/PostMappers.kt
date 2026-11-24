package ar.edu.unlam.mobile.movil22026h2.data.mappers

import ar.edu.unlam.mobile.movil22026h2.data.datasources.model.Post
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.dto.PostDto

fun PostDto.toDomain(): Post {
    // Si la API no manda autor, usamos un nombre genérico.
    val author = author?.takeIf { it.isNotBlank() } ?: "Usuario desconocido"
    // Si no hay avatar, generamos uno con las iniciales del nombre.
    val avatar =
        avatarUrl?.takeIf { it.isNotBlank() }
            ?: "https://ui-avatars.com/api/?name=${author.trim().replace(' ', '+')}"

    return Post(
        id = id ?: 0,
        authorName = author,
        authorAvatarUrl = avatar,
        content = message.orEmpty(),
        parentId = parentId ?: 0,
        likes = likes ?: 0,
        liked = liked ?: false,
        date = date.orEmpty(),
    )
}
