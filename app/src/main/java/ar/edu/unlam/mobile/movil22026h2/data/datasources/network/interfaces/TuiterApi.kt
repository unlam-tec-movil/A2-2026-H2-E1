package ar.edu.unlam.mobile.movil22026h2.data.datasources.network.interfaces

import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.login.LoginRequest
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.login.LoginResponse
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.register.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TuiterApi {
    @POST("api/v1/users")
    suspend fun registerUser(
        @Body request: RegisterRequest,
    )

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest,
    ): Response<LoginResponse>
}
