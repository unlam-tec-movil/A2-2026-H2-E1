package ar.edu.unlam.mobile.movil22026h2.data.repositories

import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.TuiterApi
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.dto.UpdateProfileRequest
import ar.edu.unlam.mobile.movil22026h2.data.mappers.toDomain
import ar.edu.unlam.mobile.movil22026h2.data.model.Profile
import javax.inject.Inject

/** Implementación del repositorio de perfil contra la API. */
class DefaultProfileRepository
    @Inject
    constructor(
        private val api: TuiterApi,
    ) : ProfileRepository {
        override suspend fun getProfile(): Profile = api.getProfile().toDomain()

        override suspend fun updateProfile(
            name: String,
            avatarUrl: String,
            password: String?,
        ): Profile =
            api
                .updateProfile(
                    UpdateProfileRequest(
                        name = name,
                        avatarUrl = avatarUrl,
                        password = password?.takeIf { it.isNotBlank() },
                    ),
                ).toDomain()
    }
