package ar.edu.unlam.mobile.movil22026h2.data.datasources.network.dto

import com.google.gson.annotations.SerializedName

/**
 * Perfil tal como lo devuelve GET /api/v1/me/profile.
 * Acá las claves vienen en snake_case (avatar_url).
 */
data class ProfileDto(
    @SerializedName("name") val name: String,
    @SerializedName("avatar_url") val avatarUrl: String,
    @SerializedName("email") val email: String,
)
