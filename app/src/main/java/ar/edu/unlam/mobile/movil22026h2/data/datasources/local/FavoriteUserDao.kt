package ar.edu.unlam.mobile.movil22026h2.data.datasources.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteUserDao {
    // Flow sin suspend: Room vuelve a emitir la lista cada vez que la tabla cambia.
    // ESto es el Patrón Observer. El feed lo usa para saber si un autor ya es favorito.
    @Query("SELECT * FROM favorite_users ORDER BY name ASC")
    fun observeAll(): Flow<List<FavoriteUserEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_users WHERE name = :name)")
    suspend fun isFavorite(name: String): Boolean

    @Query("SELECT * FROM favorite_users")
    suspend fun observeAll(): Flow<List<FavoriteUserEntity>>

    // REPLACE: si el autor ya existe, lo actualiza en vez de fallar.
    // suspend: escribir en disco es lento, va fuera del hilo principal.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: FavoriteUserEntity)

    @Query("DELETE FROM favorite_users WHERE name = :name")
    suspend fun deleteByName(name: String)

    @Query("DELETE FROM favorite_users")
    suspend fun deleteAll()
}
