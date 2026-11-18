package ar.edu.unlam.mobile.movil22026h2.ui.screens.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.movil22026h2.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class RegisterViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(RegisterUiState())
        val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

        fun onNameChanged(newName: String) {
            _uiState.update { it.copy(name = newName, errorMessage = null) }
        }

        fun onEmailChanged(newEmail: String) {
            val isValidEmail = newEmail.isBlank() || Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()
            val error = if (!isValidEmail && newEmail.isNotEmpty()) "Formato de email inválido" else null
            _uiState.update { it.copy(email = newEmail, emailError = error, errorMessage = null) }
        }

        fun onPasswordChanged(newPassword: String) {
            _uiState.update { it.copy(password = newPassword, errorMessage = null) }
        }

        fun register() {
            val currentState = _uiState.value
            if (currentState.name.isBlank() || currentState.email.isBlank() || currentState.password.isBlank()) return

            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                try {
                    authRepository.register(currentState.name, currentState.email, currentState.password)
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "No se pudo crear la cuenta. El email ya podría estar registrado.",
                        )
                    }
                }
            }
        }
    }
