package ar.edu.unlam.mobile.movil22026h2.data.repositories

import ar.edu.unlam.mobile.movil22026h2.data.datasources.model.Post
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.TuiterApi
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.dto.CreatePostRequest
import ar.edu.unlam.mobile.movil22026h2.data.mappers.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PostRepositoryImpl
    @Inject
    constructor(
        private val api: TuiterApi,
    ) : PostRepository {
        override fun observePosts(page: Int): Flow<List<Post>> =
            flow {
                val posts = api.getFeed(page = page).map { it.toDomain() }
                emit(posts)
            }

        override suspend fun createPost(content: String) {
            val trimmedContent = content.trim()
            require(trimmedContent.isNotEmpty()) { "El contenido del post no puede estar vacio" }
            api.createPost(CreatePostRequest(message = trimmedContent))
        }
    }
