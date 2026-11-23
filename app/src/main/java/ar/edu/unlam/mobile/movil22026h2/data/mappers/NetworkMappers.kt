package ar.edu.unlam.mobile.movil22026h2.data.mappers

import ar.edu.unlam.mobile.movil22026h2.data.datasources.model.Post
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.dto.PostDto
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.dto.ProfileDto
import ar.edu.unlam.mobile.movil22026h2.data.model.Profile

/** Convierte lo que viene de la red (DTO) al modelo que usa la app (dominio). */
fun ProfileDto.toDomain(): Profile =
    Profile(
        name = name,
        avatarUrl = avatarUrl,
        email = email,
    )

fun PostDto.toDomain(): Post =
    Post(
        id = requireNotNull(id),
        authorName = requireNotNull(author),
        authorAvatarUrl = avatarUrl.orEmpty(),
        content = requireNotNull(message),
        date = date,
        likes = likes ?: 0,
        liked = liked ?: false,
    )
