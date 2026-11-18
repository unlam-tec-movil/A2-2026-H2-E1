package ar.edu.unlam.mobile.movil22026h2.ui.screens.profile

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.movil22026h2.data.repositories.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

const val PROFILE_SCREEN_ROUTE = "profile"

@Immutable
sealed interface ProfileUIState {
    data object Loading : ProfileUIState

    data class Error(
        val message: String,
    ) : ProfileUIState

    data class Success(
        val name: String,
        val email: String,
        val avatarUrl: String,
        val password: String = "",
        val isSaving: Boolean = false,
    ) : ProfileUIState
}

/** Eventos de una sola vez (snackbar) para no mezclarlos con el estado persistente. */
sealed interface ProfileEvent {
    data class Saved(
        val message: String,
    ) : ProfileEvent

    data class Failed(
        val exception: Throwable,
    ) : ProfileEvent
}

@HiltViewModel
class ProfileViewModel
    @Inject
    constructor(
        private val repository: ProfileRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<ProfileUIState>(ProfileUIState.Loading)
        val uiState = _uiState.asStateFlow()

        private val _events = MutableSharedFlow<ProfileEvent>(extraBufferCapacity = 1)
        val events = _events.asSharedFlow()

        init {
            loadProfile()
        }

        fun loadProfile() {
            _uiState.value = ProfileUIState.Loading
            viewModelScope.launch {
                try {
                    val profile = repository.getProfile()
                    _uiState.value =
                        ProfileUIState.Success(
                            name = profile.name,
                            email = profile.email,
                            avatarUrl = profile.avatarUrl,
                        )
                } catch (e: Exception) {
                    _uiState.value = ProfileUIState.Error(e.message ?: "No se pudo cargar el perfil")
                }
            }
        }

        fun onNameChange(value: String) = updateSuccess { it.copy(name = value) }

        fun onAvatarUrlChange(value: String) = updateSuccess { it.copy(avatarUrl = value) }

        fun onPasswordChange(value: String) = updateSuccess { it.copy(password = value) }

        fun saveProfile() {
            val current = _uiState.value as? ProfileUIState.Success ?: return
            if (current.isSaving) return
            _uiState.value = current.copy(isSaving = true)
            viewModelScope.launch {
                try {
                    val updated = repository.updateProfile(current.name, current.avatarUrl, current.password)
                    _uiState.value =
                        ProfileUIState.Success(
                            name = updated.name,
                            email = updated.email,
                            avatarUrl = updated.avatarUrl,
                        )
                    _events.emit(ProfileEvent.Saved("Perfil actualizado"))
                } catch (e: Exception) {
                    // Conservamos lo que el usuario escribió para que no lo pierda.
                    _uiState.value = current.copy(isSaving = false)
                    _events.emit(ProfileEvent.Failed(e))
                }
            }
        }

        private inline fun updateSuccess(block: (ProfileUIState.Success) -> ProfileUIState.Success) {
            val current = _uiState.value
            if (current is ProfileUIState.Success) {
                _uiState.value = block(current)
            }
        }
    }
