package ar.edu.unlam.mobile.movil22026h2.data.repositories

import ar.edu.unlam.mobile.movil22026h2.data.datasources.model.Post
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.interfaces.PostApi
import ar.edu.unlam.mobile.movil22026h2.data.mappers.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class NetworkPostRepositoryImpl
    @Inject
    constructor(
        private val api: PostApi,
    ) : PostRepository {
        override fun observePosts(): Flow<List<Post>> =
            flow {
                val response = api.getFeed(page = 1, onlyParents = true)
                if (!response.isSuccessful) throw HttpException(response)
                //VIEJO Codigo
                // body() puede ser null cuando la página no tiene posts.
               // emit(response.body().orEmpty().map { it.toDomain() })
                val autoresAOcultar = setOf("Walter", "Probe User")
                emit(
                    response.body()
                        .orEmpty()
                        .map { it.toDomain() }
                        .filterNot { post ->
                            autoresAOcultar.any { autor ->
                                autor.equals(post.authorName.trim(), ignoreCase = true)
                            }
                        },
                )
            }

        //HU-09: trae el post completo que se abrió desde el feed.
        override suspend fun getPost(id: Int): Post {
            val response = api.getPost(id)
            if (!response.isSuccessful) throw HttpException(response)
            return response.body()?.toDomain()
                ?: throw IllegalStateException("La API no devolvió el post $id")
        }

        /*
        (HU-09) trae las respuestas.
         Si la API devuelve null, lo convertimos en lista vacía para que
         la pantalla muestre "Todavía no tiene respuestas."*/
        override suspend fun getReplies(id: Int): List<Post> {
            val response = api.getReplies(id)
            if (!response.isSuccessful) throw HttpException(response)
            return response.body().orEmpty().map { it.toDomain() }
        }
    }
