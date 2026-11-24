package ar.edu.unlam.mobile.movil22026h2.data.datasources.network

import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.dto.CreatePostRequest
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.dto.PostDto
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.dto.ProfileDto
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.dto.UpdateProfileRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @POST("me/tuits")
    suspend fun createPost(
        @Body body: CreatePostRequest,
    )

    @GET("me/feed")
    suspend fun getFeed(
        @Query("page") page: Int = 1,
        @Query("only_parents") onlyParents: Boolean = true,
    ): List<PostDto>
}
