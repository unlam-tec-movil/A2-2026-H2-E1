package ar.edu.unlam.mobile.movil22026h2.data.repositories

/** R8: la UI pide cambiar el like y no sabe si se guarda con POST o DELETE. */
interface PostLikeRepository {
    suspend fun setLike(
        postId: Int,
        liked: Boolean,
    )
}
