package ar.edu.unlam.mobile.movil22026h2.ui.screens

// sealed: el compilador conoce todos los casos y oblig a cubrirlos en el "when".
sealed interface FavoriteEvent {
    data class Added(
        val name: String,
    ) : FavoriteEvent // escenario 1: confirmación

    data class Failed(
        val exception: Exception,
    ) : FavoriteEvent // escenario 2: error
}
