package ar.edu.unlam.mobile.movil22026h2.data.repositories

import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.TuiterApi
import javax.inject.Inject

/** Guarda el like en la API, que es la fuente de verdad (scenario 4 de HU-11). */
class DefaultPostLikeRepository
    @Inject
    constructor(
        private val api: TuiterApi,
    ) : PostLikeRepository {
        override suspend fun setLike(
            postId: Int,
            liked: Boolean,
        ) {
            if (liked) {
                api.like(postId)
            } else {
                api.unlike(postId)
            }
        }
    }
