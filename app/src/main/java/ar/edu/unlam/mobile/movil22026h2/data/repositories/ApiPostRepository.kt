package ar.edu.unlam.mobile.movil22026h2.data.repositories

import ar.edu.unlam.mobile.movil22026h2.data.datasources.model.Post
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.TuiterApi
import ar.edu.unlam.mobile.movil22026h2.data.mappers.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

/**
 * Fuente de verdad del feed: la API. Si no hay conexión se mantienen los
 * posts de ejemplo (HU-07) para que la pantalla no quede vacía.
 */
class ApiPostRepository
    @Inject
    constructor(
        private val api: TuiterApi,
        private val fallback: FakePostRepositoryImpl,
    ) : PostRepository {
        override fun observePosts(): Flow<List<Post>> =
            flow {
                emit(
                    api
                        .getFeed(page = 1, onlyParents = true)
                        .map { it.toDomain() },
                )
            }.catch { error ->
                if (error is CancellationException) throw error
                emitAll(fallback.observePosts())
            }
    }
