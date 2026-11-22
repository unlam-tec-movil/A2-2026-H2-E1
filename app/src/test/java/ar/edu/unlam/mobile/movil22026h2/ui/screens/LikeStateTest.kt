package ar.edu.unlam.mobile.movil22026h2.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Test

class LikeStateTest {
    @Test
    fun `dar like sube el contador y bloquea el boton`() {
        val result = nextLikeState(LikeState(liked = false, likes = 12))

        assertEquals(LikeState(liked = true, likes = 13, pending = true), result)
    }

    @Test
    fun `quitar like baja el contador`() {
        val result = nextLikeState(LikeState(liked = true, likes = 13))

        assertEquals(LikeState(liked = false, likes = 12, pending = true), result)
    }

    @Test
    fun `el contador nunca baja de cero`() {
        val result = nextLikeState(LikeState(liked = true, likes = 0))

        assertEquals(0, result.likes)
    }

    @Test
    fun `un error restaura el estado anterior`() {
        val previous = LikeState(liked = false, likes = 5)
        // El ViewModel guarda el estado original y lo pasa al restaurar.
        val restored = restoredLikeState(previous)

        assertEquals(previous, restored)
    }
}
