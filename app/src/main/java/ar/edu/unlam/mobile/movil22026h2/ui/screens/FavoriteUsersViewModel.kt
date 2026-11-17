package ar.edu.unlam.mobile.movil22026h2.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.movil22026h2.data.datasources.model.FavoriteUser
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

@HiltViewModel // Hilt lo crea; la pantalla lo pide con hiltViewModel()
class FavoriteUsersViewModel
    @Inject
    constructor(
        private val repository: FavoriteUserRepository, // depende de la INTERFAZ, no de Room
    ) : ViewModel() {
        // Observer: Room emite -> Flow -> StateFlow -> Compose se redibuja solo.
        val favorites: StateFlow<List<FavoriteUser>> =
            repository
                .observeFavorites()
                .catch { emit(emptyList()) } // si Room falla al leer, no rompe la app
                .stateIn(
                    scope = viewModelScope, // se cancela con el ViewModel
                    started = SharingStarted.WhileSubscribed(5_000), // deja de leer 5 s sin UI
                    initialValue = emptyList(), // valor hasta la primera lectura
                )

        // Set de nombres favoritos. El feed pregunta "post.author in favoriteNames".
        val favoriteNames: StateFlow<Set<String>> =
            favorites
                .map { users -> users.map { it.name }.toSet() }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = emptySet(),
                )

        // Channel = buzón de eventos de un solo uso. Con StateFlow, el mensaje
        // "agregado" se repetiría al rotar la pantalla.
        private val _events = Channel<FavoriteEvent>(Channel.BUFFERED)
        val events: Flow<FavoriteEvent> = _events.receiveAsFlow()

        // Recibe Strings, NO el modelo del post.
        fun addFavorite(
            author: String,
            avatarUrl: String,
        ) {
            // Escenario 2: segunda defensa contra duplicados (la primera es el botón
            // deshabilitado; la tercera, el REPLACE del DAO).
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

        fun removeFavorite(name: String) {
            viewModelScope.launch {
                try {
                    repository.removeFavorite(name)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    _events.send(FavoriteEvent.Failed(e))
                }
            }
        }
    }
