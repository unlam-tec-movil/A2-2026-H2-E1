package ar.edu.unlam.mobile.movil22026h2.data.mappers

import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.dto.ProfileDto
import ar.edu.unlam.mobile.movil22026h2.data.model.Profile

/** Convierte lo que viene de la red (DTO) al modelo que usa la app (dominio). */
fun ProfileDto.toDomain(): Profile =
    Profile(
        name = name,
        avatarUrl = avatarUrl,
        email = email,
    )
