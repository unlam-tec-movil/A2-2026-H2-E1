package ar.edu.unlam.mobile.movil22026h2.data.repositories

import ar.edu.unlam.mobile.movil22026h2.data.model.Profile

/**
 * Contrato de acceso a datos del perfil. La UI depende de esta interfaz,
 * no de la implementación concreta (Retrofit, Room, etc.).
 */
interface ProfileRepository {
    suspend fun getProfile(): Profile

    suspend fun updateProfile(
        name: String,
        avatarUrl: String,
        password: String?,
    ): Profile
}
