package ar.edu.unlam.mobile.movil22026h2.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.movil22026h2.data.datasources.model.FavoriteUser
import ar.edu.unlam.mobile.movil22026h2.data.datasources.model.Post
import ar.edu.unlam.mobile.movil22026h2.data.repositories.FavoriteUserRepository
import ar.edu.unlam.mobile.movil22026h2.data.repositories.PostLikeRepository
import ar.edu.unlam.mobile.movil22026h2.data.repositories.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
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
// Acá conviven el paginado/estados de develop y los likes de HU-11.
@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val repository: FavoriteUserRepository,
        private val postRepository: PostRepository,
        private val likeRepository: PostLikeRepository,
    ) : ViewModel() {
        // --- Paginar y reintentar (develop) ---
        private val retryRequests = Channel<Unit>(Channel.CONFLATED)

        fun retryFeed() {
            retryRequests.trySend(Unit)
        }

        private var nextPage = 2

        private val _isLoadingMore = MutableStateFlow(false)
        val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

        private val _hasMorePages = MutableStateFlow(true)
        val hasMorePages: StateFlow<Boolean> = _hasMorePages

        private val _additionalPosts = MutableStateFlow<List<Post>>(emptyList())
        val additionalPosts: StateFlow<List<Post>> = _additionalPosts

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
                .map<List<Post>, FeedUiState> { posts -> FeedUiState.Success(posts) }
                .onStart { emit(FeedUiState.Loading) }
                .retryWhen { cause, _ ->
                    if (cause is CancellationException) throw cause

                    emit(
                        FeedUiState.Error(
                            "No se pudieron cargar las publicaciones",
                        ),
                    )

                    retryRequests.receive()
                    true
                }.stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = FeedUiState.Loading,
                )

        // --- HU-11: like optimista con rollback ---
        private val _likeStates = MutableStateFlow<Map<Int, LikeState>>(emptyMap())
        val likeStates: StateFlow<Map<Int, LikeState>> = _likeStates.asStateFlow()

        // Feed base + páginas cargadas + likes locales. Si hay estado local para
        // un post, se superpone sobre el que vino de la API; si no, se muestra tal cual.
        val feedPosts: StateFlow<List<Post>> =
            combine(
                postRepository.observePosts().catch { emit(emptyList()) },
                _additionalPosts,
                _likeStates,
            ) { currentPosts, extraPosts, states ->
                (currentPosts + extraPosts)
                    .distinctBy { it.id }
                    .map { post ->
                        states[post.id]?.let { state ->
                            post.copy(likes = state.likes, liked = state.liked)
                        } ?: post
                    }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList(),
            )

        // Observer: Room emite -> Flow -> StateFlow -> Compose se redibuja solo.
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

        // Channel = evento de un solo uso: no se repite al rotar la pantalla.
        private val _events = Channel<FavoriteEvent>(Channel.BUFFERED)
        val events: Flow<FavoriteEvent> = _events.receiveAsFlow()

        private val _likeEvents = Channel<LikeEvent>(Channel.BUFFERED)
        val likeEvents: Flow<LikeEvent> = _likeEvents.receiveAsFlow()

        // Un post con una request en vuelo no acepta otro toque.
        private val pendingLikes = mutableSetOf<Int>()

        fun addFavorite(
            author: String,
            avatarUrl: String,
        ) {
            if (author in favoriteNames.value) return

            viewModelScope.launch {
                try {
                    repository.addFavorite(FavoriteUser(name = author, avatarUrl = avatarUrl))
                    _events.send(FavoriteEvent.Added(author))
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    _events.send(FavoriteEvent.Failed(e))
                }
            }
        }

        fun toggleLike(post: Post) {
            if (!pendingLikes.add(post.id)) return

            val previous = _likeStates.value[post.id] ?: LikeState(post.liked, post.likes)
            val updated = nextLikeState(previous)
            _likeStates.value = _likeStates.value + (post.id to updated)

            viewModelScope.launch {
                try {
                    likeRepository.setLike(post.id, updated.liked)
                    _likeStates.value = _likeStates.value + (post.id to updated.copy(pending = false))
                    _likeEvents.send(LikeEvent.Saved(post.id, updated.liked))
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    // Rollback del post afectado, no de toda la lista.
                    _likeStates.value = _likeStates.value + (post.id to restoredLikeState(previous))
                    _likeEvents.send(LikeEvent.Failed(e))
                } finally {
                    pendingLikes.remove(post.id)
                }
            }
        }
    }
