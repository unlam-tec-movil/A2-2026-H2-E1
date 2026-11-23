package ar.edu.unlam.mobile.movil22026h2.data.repositories

import ar.edu.unlam.mobile.movil22026h2.data.datasources.model.Post
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.TuiterApi
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
                val response = api.getFeed(page = page)

                val posts =
                    response.map { dto ->
                        dto.toDomain()
                    }

                emit(posts)
            }
    }
