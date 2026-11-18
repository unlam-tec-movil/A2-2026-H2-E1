package ar.edu.unlam.mobile.movil22026h2.data.datasources.network

import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.dto.ProfileDto
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.dto.UpdateProfileRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

/**
 * Contrato HTTP con la API de UNLaM Tuiter.
 * Retrofit genera la implementación real en tiempo de ejecución.
 */
interface TuiterApi {
    // R5 · Perfil
    @GET("me/profile")
    suspend fun getProfile(): ProfileDto

    @PUT("me/profile")
    suspend fun updateProfile(
        @Body body: UpdateProfileRequest,
    ): ProfileDto
}
