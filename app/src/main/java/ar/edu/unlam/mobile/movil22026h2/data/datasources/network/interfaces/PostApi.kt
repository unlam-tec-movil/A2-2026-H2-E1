package ar.edu.unlam.mobile.movil22026h2.data.datasources.network.interfaces

import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.dto.PostDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PostApi {
// HU-01 (soporte): trae el feed del usuario logueado.

    @GET("me/feed")
    suspend fun getFeed(
        @Query("page") page: Int, // permite agregar parámetros adicionales a la URL de la solicitud.
        @Query("only_parents") onlyParents: Boolean,
    ): Response<List<PostDto>> // Permite aceder a las repustas ATTP, incluyendo su codigo de estado.

    @GET("me/tuits/{id}")
    suspend fun getPost(
        @Path("id") id: Int, // permite definir la ruta del endpoint.
    ): Response<PostDto>

    @GET("me/tuits/{id}/replies")
    suspend fun getReplies(
        @Path("id") id: Int,
    ): Response<List<PostDto>>
}