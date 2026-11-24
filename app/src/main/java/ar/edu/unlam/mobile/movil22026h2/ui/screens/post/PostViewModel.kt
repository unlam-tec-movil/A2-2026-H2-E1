package ar.edu.unlam.mobile.movil22026h2.ui.screens.post

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.movil22026h2.data.datasources.model.Post
import ar.edu.unlam.mobile.movil22026h2.data.repositories.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// (HU-09) Estado que consume PostDetailScreen:

data class PostDetailUiState(
    val isLoading: Boolean = false,
    val post: Post? = null,
    val replies: List<Post> = emptyList(),
    val error: String? = null,
)

@HiltViewModel
class PostViewModel
    @Inject
    constructor(
        private val savedStateHandle: SavedStateHandle,
        private val repository: PostRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(PostDetailUiState())

        val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()

        init {
            load()
        }

        private fun load() {
// SavedStateHandle es la forma en que el ViewModel lee el postId de la ruta.
            // Navigation lo guarda ahí automáticamente
            val postId = savedStateHandle.get<Int>("postId") ?: 0

            viewModelScope.launch {
                _uiState.value = PostDetailUiState(isLoading = true)

                try {
                    val post = repository.getPost(postId)

                    val replies = repository.getReplies(postId)

                    _uiState.value = PostDetailUiState(post = post, replies = replies)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    _uiState.value = PostDetailUiState(error = e.message ?: "No se pudo cargar el post")
                }
            }
        }

        /** Acción agregada: el botón "Reintentar" vuelve a ejecutar la carga. */
        fun retry() {
            load()
        }
    }
