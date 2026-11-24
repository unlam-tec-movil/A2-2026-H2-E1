package ar.edu.unlam.mobile.movil22026h2.data.repositories

import ar.edu.unlam.mobile.movil22026h2.data.datasources.model.Post
import kotlinx.coroutines.flow.Flow

// El feed pide los posts acá, sin saber de dónde vienen.
// Cuando esté la API, se cambia la implementación que se bindea en
// RepositoryModule y ni la pantalla ni el ViewModel se tocan.
interface PostRepository {
   
    fun observePosts(page: Int = 1): Flow<List<Post>>

    suspend fun createPost(content: String)
}
