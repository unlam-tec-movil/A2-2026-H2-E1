package ar.edu.unlam.mobile.movil22026h2.data.repositories

import ar.edu.unlam.mobile.movil22026h2.data.datasources.model.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

// ÚNICO archivo con datos falsos del feed.
// No hay backend todavía, así que los posts de ejemplo viven acá y en ningún
// otro lado (ni en HomeScreen ni en HomeViewModel). Ana aparece dos veces para
// poder verificar que un autor ya guardado no se vuelve a guardar.
private val samplePosts =
    listOf(
        Post(
            id = 1,
            authorName = "Ana Gomez",
            authorAvatarUrl = "https://i.pravatar.cc/150?img=1",
            content = "Arrancando la semana con todo.",
            date = "2026-11-21T10:30:00Z",
            likes = 15,
            liked = false,
        ),
        Post(
            id = 2,
            authorName = "Beto Diaz",
            authorAvatarUrl = "https://i.pravatar.cc/150?img=12",
            content = "Probando la nueva app de la facu.",
            date = "2026-11-18T10:30:00Z",
            likes = 10,
            liked = false,
        ),
        Post(
            id = 3,
            authorName = "Carla Ruiz",
            authorAvatarUrl = "https://i.pravatar.cc/150?img=32",
            content = "¿Alguien para estudiar para el parcial?",
            date = "2026-11-17T10:30:00Z",
            likes = 2,
            liked = false,
        ),
        Post(
            id = 4,
            authorName = "Ana Gomez",
            authorAvatarUrl = "https://i.pravatar.cc/150?img=1",
            content = "Otro post de Ana: acá ya debería decir que es favorita.",
            date = "2026-11-16T10:30:00Z",
            likes = 1,
            liked = false,
        ),
    )

// Implementación provisoria: devuelve siempre la misma lista.
// Cuando otro compañero conecte la API, se crea otro PostRepository y cambia el
// @Binds de RepositoryModule. Este archivo se puede borrar sin tocar el resto.
class FakePostRepositoryImpl
    @Inject
    constructor() : PostRepository {
        override fun observePosts(): Flow<List<Post>> = flowOf(samplePosts)
    }
