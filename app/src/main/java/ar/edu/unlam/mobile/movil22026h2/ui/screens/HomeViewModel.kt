package ar.edu.unlam.mobile.movil22026h2.ui.screens

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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
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
        postRepository: PostRepository,
        private val likeRepository: PostLikeRepository,
    ) : ViewModel() {
        // Cuando el repositorio apunte a la API, esta línea no cambia.
        val posts: StateFlow<List<Post>> =
            postRepository
                .observePosts()
                .catch { emit(emptyList()) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = emptyList(),
                )

        // HU-11: el like cambia al instante y se revierte solo si falla la API.
        private val _likeStates = MutableStateFlow<Map<Int, LikeState>>(emptyMap())
        val likeStates: StateFlow<Map<Int, LikeState>> = _likeStates.asStateFlow()

        val feedPosts: StateFlow<List<Post>> =
            combine(posts, _likeStates) { currentPosts, states ->
                currentPosts.map { post ->
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

        private val _likeEvents = Channel<LikeEvent>(Channel.BUFFERED)
        val likeEvents: Flow<LikeEvent> = _likeEvents.receiveAsFlow()

        // Un post con una request en vuelo no acepta otro toque.
        private val pendingLikes = mutableSetOf<Int>()

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
