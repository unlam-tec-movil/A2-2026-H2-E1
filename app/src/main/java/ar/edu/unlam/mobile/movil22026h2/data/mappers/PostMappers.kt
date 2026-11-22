package ar.edu.unlam.mobile.movil22026h2.data.mappers

import ar.edu.unlam.mobile.movil22026h2.data.datasources.model.Post
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.dto.FeedPostDto

/** Convierte un post de la red al modelo que ya usa el feed. */
fun FeedPostDto.toDomain(): Post =
    Post(
        id = id,
        authorName = author,
        authorAvatarUrl = avatarUrl,
        content = message,
        likes = likes,
        liked = liked,
    )
