package ar.edu.unlam.mobile.movil22026h2.data.repositories

import ar.edu.unlam.mobile.movil22026h2.data.datasources.model.FavoriteUser
import kotlinx.coroutines.flow.Flow

interface FavoriteUserRepository {
    fun observeFavorites(): Flow<List<FavoriteUser>>

    suspend fun addFavorite(user: FavoriteUser)

    suspend fun removeFavorite(name: String)
}
