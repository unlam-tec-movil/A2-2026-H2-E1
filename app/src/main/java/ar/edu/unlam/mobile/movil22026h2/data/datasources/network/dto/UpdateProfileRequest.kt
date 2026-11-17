package ar.edu.unlam.mobile.movil22026h2.data.datasources.network.dto

import com.google.gson.annotations.SerializedName

/**
 * Body de PUT /api/v1/me/profile.
 * password es opcional: si es null, Gson no lo envía y la contraseña no se modifica.
 */
data class UpdateProfileRequest(
    @SerializedName("name") val name: String,
    @SerializedName("avatar_url") val avatarUrl: String,
    @SerializedName("password") val password: String? = null,
)
