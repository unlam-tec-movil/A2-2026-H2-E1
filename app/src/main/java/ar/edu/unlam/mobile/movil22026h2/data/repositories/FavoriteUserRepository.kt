package ar.edu.unlam.mobile.movil22026h2.data.repositories

interface FavoriteUserRepository {
    fun observeFavorites(): Flow<List<FavoriteUser>>

    suspend fun addFavorite(user: FavoriteUser)

    suspend fun removeFavorite(name: String)
}
