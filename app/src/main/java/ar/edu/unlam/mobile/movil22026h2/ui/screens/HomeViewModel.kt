package ar.edu.unlam.mobile.movil22026h2.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.movil22026h2.data.datasources.model.FavoriteUser
import ar.edu.unlam.mobile.movil22026h2.data.datasources.model.Post
import ar.edu.unlam.mobile.movil22026h2.data.repositories.FavoriteUserRepository
import ar.edu.unlam.mobile.movil22026h2.data.repositories.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen

// El feed ES la pantalla de Home, por eso su ViewModel es HomeViewModel.
// No se crea un FeedViewModel aparte para no duplicar clases en el merge.
@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val repository: FavoriteUserRepository,
        // Los posts vienen del repositorio, no están escritos acá.
        postRepository: PostRepository,
    ) : ViewModel() {
        // Cuando el repositorio apunte a la API, esta línea no cambia.
        val posts: StateFlow<List<Post>> =
            postRepository
                .observePosts()
                .catch { emit(emptyList()) } //si hay algun error, emitimos una lista vacia (esto es momentaneo). TODO: implementar un mensaje de error y un boton reintentar.
                .stateIn(
                    scope = viewModelScope, //la recolección compartida del flujo está vinculada al ciclo de vida del ViewModel
                    started = SharingStarted.WhileSubscribed(5_000), //el flujo se mantiene activo mientras tenga suscriptores y deja de recolectar el flujo original después de cinco segundos sin suscriptores
                    initialValue = emptyList(), //inicialmente no tenemos publicaciones para mostrar
                )

    private val retryRequests = Channel<Unit>(Channel.CONFLATED)

    fun retryFeed() {
        retryRequests.trySend(Unit)
    }

    val feedUiState: StateFlow<FeedUiState> =
        postRepository
            .observePosts()
            .map<List<Post>, FeedUiState> { posts ->
                FeedUiState.Success(posts)
            } //cuando el repo emite una lista de posts, la transformamos en un estado Success que contiene esa lista
            .onStart {
                emit(FeedUiState.Loading)
            }//inicialmente emitimos un estado de carga
            .retryWhen { cause, attempt ->//capturamos la excepcion
                if (cause is CancellationException) throw cause

                Log.e(
                    "FeedDebug",
                    "Error al obtener las publicaciones",
                    cause
                )

                emit(
                    FeedUiState.Error(
                        "No se pudieron cargar las publicaciones"
                    )
                )

                retryRequests.receive()//la corrutina queda suspendida esperando señal del usuario para reintentar
                true//dspues de la señal le indicamos a retryWhen que se emita nuevamente
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = FeedUiState.Loading, //el valor inicial es una pantalla de carga
            )


    // Observer: Room emite -> Flow -> StateFlow -> Compose se redibuja solo.
        // El feed usa este set para mostrar "Ya es favorito" (escenario 2).
        val favoriteNames: StateFlow<Set<String>> =
            repository
                .observeFavorites()
                .map { users -> users.map { it.name }.toSet() }
                .catch { emit(emptySet()) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = emptySet(),
                )

        // Channel = evento de un solo uso: la confirmación no se repite al rotar.
        private val _events = Channel<FavoriteEvent>(Channel.BUFFERED)
        val events: Flow<FavoriteEvent> = _events.receiveAsFlow()

        fun addFavorite(
            author: String,
            avatarUrl: String,
        ) {
            // Escenario 2: si ya es favorito, no se vuelve a guardar.
            if (author in favoriteNames.value) return

            viewModelScope.launch {
                try {
                    repository.addFavorite(FavoriteUser(name = author, avatarUrl = avatarUrl))
                    // Escenario 1: confirmación visible para el usuario.
                    _events.send(FavoriteEvent.Added(author))
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    _events.send(FavoriteEvent.Failed(e))
                }
            }
        }
    }
