package ar.edu.unlam.mobile.movil22026h2.ui.screens

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.movil22026h2.data.repositories.DraftRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CreatePostEvent {
    data object PublishedOk : CreatePostEvent

    data object PublishedError : CreatePostEvent

    data object DraftSaved : CreatePostEvent
}

@Immutable
data class CreatePostUIState(
    val draftText: String = "",
)

@HiltViewModel
class CreatePostViewModel
    @Inject
    constructor(
        private val draftRepository: DraftRepository,
    ) : ViewModel() {
        // Mutable State Flow contiene un objeto de estado mutable. Simplifica la operación de
        // actualización de información y de manejo de estados de una aplicación: Cargando, Error, Éxito
        // (https://developer.android.com/kotlin/flow/stateflow-and-sharedflow)
        private val _uiState = MutableStateFlow(CreatePostUIState())

        // UIState expone el estado anterior como un Flujo de Estado de solo lectura.
        // Esto impide que se pueda modificar el estado desde fuera del ViewModel.
        val uiState: StateFlow<CreatePostUIState> = _uiState.asStateFlow()
        private val _events = Channel<CreatePostEvent>(Channel.BUFFERED)
        val events = _events.receiveAsFlow()

        init {
            viewModelScope.launch {
                val draft = draftRepository.getDraft()
                _uiState.value =
                    _uiState.value.copy(
                        draftText = draft?.text ?: "",
                    )
            }
        }

        fun onDraftChanged(text: String) {
            _uiState.value = _uiState.value.copy(draftText = text)
        }

        fun onSaveDraft() {
            viewModelScope.launch {
                draftRepository.saveDraft(_uiState.value.draftText)
                _events.send(CreatePostEvent.DraftSaved)
            }
        }

        fun onPublish() {
            viewModelScope.launch {
                try {
                    draftRepository.clearDraft()
                    _uiState.value = _uiState.value.copy(draftText = "")
                    _events.send(CreatePostEvent.PublishedOk)
                } catch (e: Exception) {
                    _events.send(CreatePostEvent.PublishedError)
                }
            }
        }
    }
