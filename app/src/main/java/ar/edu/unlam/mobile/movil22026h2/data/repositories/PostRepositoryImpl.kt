package ar.edu.unlam.mobile.movil22026h2.data.repositories

import ar.edu.unlam.mobile.movil22026h2.data.datasources.model.Post
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.TuiterApi
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.dto.CreatePostRequest
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.interfaces.PostApi
import ar.edu.unlam.mobile.movil22026h2.data.mappers.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class PostRepositoryImpl
    @Inject
    constructor(
        private val api: TuiterApi,
        private val postApi: PostApi,
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

        // HU-09: trae el post completo que se abrió desde el feed.
        override suspend fun getPost(id: Int): Post {
            val response = postApi.getPost(id)
            if (!response.isSuccessful) throw HttpException(response)
            return response.body()?.toDomain()
                ?: throw IllegalStateException("La API no devolvió el post $id")
        }

        // HU-09: trae las respuestas del post.
        override suspend fun getReplies(id: Int): List<Post> {
            val response = postApi.getReplies(id)
            if (!response.isSuccessful) throw HttpException(response)
            return response.body().orEmpty().map { it.toDomain() }
        }
    }
