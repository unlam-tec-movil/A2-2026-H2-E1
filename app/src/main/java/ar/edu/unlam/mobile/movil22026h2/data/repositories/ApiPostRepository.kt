package ar.edu.unlam.mobile.movil22026h2.data.repositories

import ar.edu.unlam.mobile.movil22026h2.data.datasources.model.Post
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.TuiterApi
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.dto.CreatePostRequest
import ar.edu.unlam.mobile.movil22026h2.data.mappers.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

/**
 * Fuente de verdad del feed: la API. Si no hay conexión se mantienen los
 * posts de ejemplo (HU-07) para que la pantalla no quede vacía.
 * Implementa el contrato de develop: observePosts(page) y createPost().
 */
class ApiPostRepository
    @Inject
    constructor(
        private val api: TuiterApi,
        private val fallback: FakePostRepositoryImpl,
    ) : PostRepository {
        // Cada post creado incrementa este contador y provoca un nuevo fetch.
        private val refresh = MutableStateFlow(0)

        private fun fetch(page: Int): Flow<List<Post>> =
            flow {
                emit(
                    api
                        .getFeed(page = page, onlyParents = true)
                        .map { it.toDomain() },
                )
            }.catch { error ->
                if (error is CancellationException) throw error
                emitAll(fallback.observePosts(page))
            }

        override fun observePosts(page: Int): Flow<List<Post>> = refresh.flatMapLatest { fetch(page) }

        override suspend fun createPost(content: String) {
            val trimmedContent = content.trim()
            require(trimmedContent.isNotEmpty()) { "El contenido del post no puede estar vacío" }
            api.createPost(CreatePostRequest(message = trimmedContent))
            refresh.update { it + 1 }
        }
    }
