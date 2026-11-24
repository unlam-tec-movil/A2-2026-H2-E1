package ar.edu.unlam.mobile.movil22026h2.data.repositories

import ar.edu.unlam.mobile.movil22026h2.data.datasources.model.Post
import kotlinx.coroutines.flow.Flow

// El feed pide los posts acá, sin saber de dónde vienen.
// Cuando esté la API, se cambia la implementación que se bindea en
// RepositoryModule y ni la pantalla ni el ViewModel se tocan.
interface PostRepository {
    // Feed (HU-01, soporte para llegar a HU-09).
    fun observePosts(): Flow<List<Post>>

    // (HU-09)Trae un post completo por id
    suspend fun getPost(id: Int): Post

    // (HU-09)Trae las respuestas de un post
    suspend fun getReplies(id: Int): List<Post>
}
