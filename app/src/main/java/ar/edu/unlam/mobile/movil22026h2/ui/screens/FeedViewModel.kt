package ar.edu.unlam.mobile.movil22026h2.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.movil22026h2.data.datasources.model.FavoriteUser
import ar.edu.unlam.mobile.movil22026h2.data.datasources.model.Post
import ar.edu.unlam.mobile.movil22026h2.data.repositories.FavoriteUserRepository
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

// Posts de ejemplo: no hay backend todavía, así se puede probar el escenario
// "estoy viendo un post en el feed". Ana aparece dos veces para poder verificar
// que un autor ya guardado no se vuelve a guardar.
private val samplePosts =
    listOf(
        Post(
            id = 1,
            authorName = "Ana Gomez",
            authorAvatarUrl = "https://i.pravatar.cc/150?img=1",
            content = "Arrancando la semana con todo.",
        ),
        Post(
            id = 2,
            authorName = "Beto Diaz",
            authorAvatarUrl = "https://i.pravatar.cc/150?img=12",
            content = "Probando la nueva app de la facu.",
        ),
        Post(
            id = 3,
            authorName = "Carla Ruiz",
            authorAvatarUrl = "https://i.pravatar.cc/150?img=32",
            content = "¿Alguien para estudiar para el parcial?",
        ),
        Post(
            id = 4,
            authorName = "Ana Gomez",
            authorAvatarUrl = "https://i.pravatar.cc/150?img=1",
            content = "Otro post de Ana: acá ya debería decir que es favorita.",
        ),
    )

@HiltViewModel
class FeedViewModel
    @Inject
    constructor(
        private val repository: FavoriteUserRepository,
    ) : ViewModel() {
        val posts: List<Post> = samplePosts

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
