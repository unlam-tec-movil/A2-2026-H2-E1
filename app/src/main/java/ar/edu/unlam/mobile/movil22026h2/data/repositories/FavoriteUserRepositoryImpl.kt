package ar.edu.unlam.mobile.movil22026h2.data.repositories

import android.R
import ar.edu.unlam.mobile.movil22026h2.data.datasources.local.FavoriteUserDao
import ar.edu.unlam.mobile.movil22026h2.data.datasources.model.FavoriteUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// @Inject constructor; Hilt sabe cosntruirlo y le pasa el DAO que provee DatabaseModule.
class FavoriteUserRepositoryImpl
    @Inject
    constructor(
        private val dao: FavoriteUserDao,
    ) : FavoriteUserRepository {
        // map transforma cada lista que emite Room: de Entity a modelo de dominio.
        override fun observeFavorites(): Flow<List<FavoriteUser>> = dao.observeAll().map { list -> list.map { it.toDomain() } }

        override suspend fun addFavorite(user: FavoriteUser) = dao.insert(user.toEntity())

        override suspend fun removeFavorite(name: String) = dao.deleteByName(name)
    }
