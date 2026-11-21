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
        ),
        Post(
            id = 2,
            authorName = "Beto Diaz",
            authorAvatarUrl = "https://i.pravatar.cc/150?img=12",
            content = "Probando la nueva app de la facu.",
        ),
        Post(
            id = 3,
            authorName = "Carla Ruiz",
            authorAvatarUrl = "https://i.pravatar.cc/150?img=32",
            content = "¿Alguien para estudiar para el parcial?",
        ),
        Post(
            id = 4,
            authorName = "Ana Gomez",
            authorAvatarUrl = "https://i.pravatar.cc/150?img=1",
            content = "Otro post de Ana: acá ya debería decir que es favorita.",
        ),
    )

// Implementación provisoria: devuelve siempre la misma lista.
// Cuando otro compañero conecte la API, se crea otro PostRepository y cambia el
// @Binds de RepositoryModule. Este archivo se puede borrar sin tocar el resto.

/*VIEJO CODIGO
class FakePostRepositoryImpl
    @Inject
    constructor() : PostRepository {
        override fun observePosts(): Flow<List<Post>> = flowOf(samplePosts)
    }
*/

class FakePostRepositoryImpl
@Inject
constructor() : PostRepository {
    override fun observePosts(): Flow<List<Post>> = flowOf(samplePosts)

    //  busca el post falso por id entre los ejemplos.
    override suspend fun getPost(id: Int): Post =
        samplePosts.firstOrNull { it.id == id }
            ?: throw IllegalStateException("No hay post falso con id $id")

    // los posts falsos no tienen respuestas (lista vacía).
    override suspend fun getReplies(id: Int): List<Post> = emptyList()
}