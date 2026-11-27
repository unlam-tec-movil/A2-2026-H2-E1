package ar.edu.unlam.mobile.movil22026h2.ui.screens

/** Eventos de un solo uso del like: no se repiten al rotar la pantalla. */
sealed interface LikeEvent {
    data class Saved(
        val postId: Int,
        val liked: Boolean,
    ) : LikeEvent

    data class Failed(
        val exception: Throwable,
    ) : LikeEvent
}
