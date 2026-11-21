package ar.edu.unlam.mobile.movil22026h2.ui.screens

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.movil22026h2.data.repositories.DraftRepository
import ar.edu.unlam.mobile.movil22026h2.data.repositories.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
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
    val savedDraftText: String = "",
    val isLoading: Boolean = false,
) {
    val canSaveDraft: Boolean
        get() = !isLoading && draftText.isNotBlank() && draftText != savedDraftText

    val canPublish: Boolean
        get() = !isLoading && draftText.isNotBlank()
}

@HiltViewModel
class CreatePostViewModel
@Inject
constructor(
    private val draftRepository: DraftRepository,
    private val postRepository: PostRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreatePostUIState())
    val uiState: StateFlow<CreatePostUIState> = _uiState.asStateFlow()

    private val _events = Channel<CreatePostEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            val draft = draftRepository.getDraft()
            _uiState.update {
                it.copy(
                    draftText = draft?.text ?: "",
                    savedDraftText = draft?.text ?: "",
                )
            }
        }
    }

    fun onDraftChanged(text: String) {
        _uiState.update { it.copy(draftText = text) }
    }

    fun onSaveDraft() {
        val state = _uiState.value
        if (!state.canSaveDraft) return
        viewModelScope.launch {
            draftRepository.saveDraft(state.draftText)
            _uiState.update { it.copy(savedDraftText = state.draftText) }
            _events.send(CreatePostEvent.DraftSaved)
        }
    }

    fun onPublish() {
        val state = _uiState.value
        if (!state.canPublish) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                postRepository.createPost(state.draftText)
                draftRepository.clearDraft()
                _uiState.update {
                    it.copy(
                        draftText = "",
                        savedDraftText = "",
                        isLoading = false,
                    )
                }
                _events.send(CreatePostEvent.PublishedOk)
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                _events.send(CreatePostEvent.PublishedError)
            }
        }
    }
}