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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

// El feed ES la pantalla de Home, por eso su ViewModel es HomeViewModel.
// No se crea un FeedViewModel aparte para no duplicar clases en el merge.
@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val repository: FavoriteUserRepository,
        // Los posts vienen del repositorio, no están escritos acá.
        private val postRepository: PostRepository,
    ) : ViewModel() {
        private val retryRequests = Channel<Unit>(Channel.CONFLATED)

        fun retryFeed() {
            retryRequests.trySend(Unit)
        }

        private var nextPage = 2

        private val _isLoadingMore = MutableStateFlow(false)

        val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

        private val _hasMorePages = MutableStateFlow(true)

        val hasMorePages: StateFlow<Boolean> = _hasMorePages

        private val _additionalPosts =
            MutableStateFlow<List<Post>>(emptyList())

        val additionalPosts: StateFlow<List<Post>> =
            _additionalPosts

        fun loadNextPage() {
            if (_isLoadingMore.value || !_hasMorePages.value) return

            _isLoadingMore.value = true

            viewModelScope.launch {
                try {
                    val nuevosPosts =
                        postRepository.observePosts(page = nextPage).first()

                    if (nuevosPosts.isEmpty()) {
                        _hasMorePages.value = false
                    } else {
                        _additionalPosts.value =
                            _additionalPosts.value + nuevosPosts

                        nextPage++
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e("FeedPagination", "Error al cargar la página", e)
                } finally {
                    _isLoadingMore.value = false
                }
            }
        }

        val feedUiState: StateFlow<FeedUiState> =
            postRepository
                .observePosts()
                .map<List<Post>, FeedUiState> { posts ->
                    FeedUiState.Success(posts)
                } // cuando el repo emite una lista de posts, la transformamos en un estado Success que contiene esa lista
                .onStart {
                    emit(FeedUiState.Loading)
                } // inicialmente emitimos un estado de carga
                .retryWhen { cause, _ ->
                    // capturamos la excepcion
                    if (cause is CancellationException) throw cause

                    emit(
                        FeedUiState.Error(
                            "No se pudieron cargar las publicaciones",
                        ),
                    )

                    retryRequests.receive() // la corrutina queda suspendida esperando señal del usuario para reintentar
                    true // dspues de la señal le indicamos a retryWhen que se emita nuevamente
                }.stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = FeedUiState.Loading, // el valor inicial es una pantalla de carga
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
