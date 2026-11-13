package ar.edu.unlam.mobile.movil22026h2.data.datasources.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FavoriteUserEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    // Room genera la implementación de cada DAO.
    abstract fun favoriteUserDao(): FavoriteUserDao
}
