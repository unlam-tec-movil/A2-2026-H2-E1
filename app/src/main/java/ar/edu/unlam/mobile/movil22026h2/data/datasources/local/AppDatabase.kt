package ar.edu.unlam.mobile.movil22026h2.data.datasources.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [DraftEntity::class, FavoriteUserEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    // Room genera la implementación de cada DAO.
    abstract fun draftDao(): DraftDao

    abstract fun favoriteUserDao(): FavoriteUserDao
}
