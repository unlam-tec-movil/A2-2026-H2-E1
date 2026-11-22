
package ar.edu.unlam.mobile.movil22026h2.data.repositories

import ar.edu.unlam.mobile.movil22026h2.data.datasources.model.Post
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.TuiterApi
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.dto.PostDto
import ar.edu.unlam.mobile.movil22026h2.data.mappers.toDomain
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PostRepositoryImpl
@Inject
constructor(
    private val api: TuiterApi,
) : PostRepository {

    private val gson = Gson()

    override fun observePosts(): Flow<List<Post>> = flow {

        val response = api.getFeed()

        val posts = when {
            response.isJsonNull -> emptyList()

            response.isJsonArray -> {
                response.asJsonArray.map { element ->
                    require(element.isJsonObject)

                    val dto = gson.fromJson(
                        element,
                        PostDto::class.java
                    )

                    dto.toDomain()
                }
            }

            else -> error("Formato de respuesta inesperado")
        }

        emit(posts)
    }
}