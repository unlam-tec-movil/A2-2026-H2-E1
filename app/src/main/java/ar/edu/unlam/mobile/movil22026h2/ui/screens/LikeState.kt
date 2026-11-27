package ar.edu.unlam.mobile.movil22026h2.ui.screens

/** Estado de un like en pantalla: optimista, con bloqueo mientras se guarda. */
data class LikeState(
    val liked: Boolean,
    val likes: Int,
    val pending: Boolean = false,
)

/** Escenarios 1 y 2 de HU-11: el icono y el contador cambian al tocar. */
fun nextLikeState(current: LikeState): LikeState =
    current.copy(
        liked = !current.liked,
        likes = (current.likes + if (current.liked) -1 else 1).coerceAtLeast(0),
        pending = true,
    )

/** Escenario 3: si falla la API, volvemos exactamente al estado anterior. */
fun restoredLikeState(previous: LikeState): LikeState = previous.copy(pending = false)
