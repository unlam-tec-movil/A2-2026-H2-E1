package ar.edu.unlam.mobile.movil22026h2.data.datasources.network

import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.dto.FeedPostDto
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.dto.ProfileDto
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.dto.UpdateProfileRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

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

    // R1 · Feed real. LOS IDS de acá son los que usa HU-11 para dar likes.
    // Las rutas son relativas: BASE_URL ya incluye /api/v1/ (convención de develop).
    @GET("me/feed")
    suspend fun getFeed(
        @Query("page") page: Int = 1,
        @Query("only_parents") onlyParents: Boolean = true,
    ): List<FeedPostDto>

    // R8 · Likes
    @POST("me/tuits/{tuitId}/likes")
    suspend fun like(
        @Path("tuitId") tuitId: Int,
    )

    @DELETE("me/tuits/{tuitId}/likes")
    suspend fun unlike(
        @Path("tuitId") tuitId: Int,
    )
}
