package ar.edu.unlam.mobile.movil22026h2.data.repositories

import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.interfaces.TuiterApi
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.login.LoginRequest
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.login.LoginResponse
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.register.RegisterRequest
import ar.edu.unlam.mobile.movil22026h2.data.session.SessionManager
import javax.inject.Inject

class AuthRepository
    @Inject
    constructor(
        private val api: TuiterApi,
        private val sessionManager: SessionManager,
    ) {
        suspend fun register(
            name: String,
            email: String,
            password: String,
        ) {
            val request = RegisterRequest(name = name, email = email, password = password)
            api.registerUser(request)
        }

        suspend fun login(request: LoginRequest): Result<LoginResponse> =
            try {
                val response = api.login(request)
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    sessionManager.saveToken(body.token)
                    Result.success(body)
                } else {
                    Result.failure(Exception("Credenciales inválidas o error en el servidor"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
    }
