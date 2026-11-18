package ar.edu.unlam.mobile.movil22026h2.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.login.LoginRequest
import ar.edu.unlam.mobile.movil22026h2.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccessful: Boolean = false,
)

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(LoginUiState())
        val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

        // Actualiza el email cuando el usuario escribe en el TextField
        fun onEmailChanged(email: String) {
            _uiState.update { it.copy(email = email, errorMessage = null) }
        }

        // Actualiza la contraseña cuando el usuario escribe en el TextField
        fun onPasswordChanged(password: String) {
            _uiState.update { it.copy(password = password, errorMessage = null) }
        }

        // Acción de iniciar sesión conectada al repositorio
        fun login() {
            val currentState = _uiState.value

            if (currentState.email.isBlank() || currentState.password.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Por favor, completa todos los campos") }
                return
            }

            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }

                val request =
                    LoginRequest(
                        email = currentState.email,
                        password = currentState.password,
                    )

                val result = authRepository.login(request)

                result
                    .onSuccess { response ->
                        _uiState.update {
                            it.copy(isLoading = false, isLoginSuccessful = true)
                        }
                    }.onFailure { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = error.localizedMessage ?: "Error al iniciar sesión",
                            )
                        }
                    }
            }
        }
    }
