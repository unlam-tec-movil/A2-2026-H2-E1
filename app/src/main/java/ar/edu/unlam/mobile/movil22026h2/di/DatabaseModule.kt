package ar.edu.unlam.mobile.movil22026h2.di

import android.content.Context
import androidx.room.Room
import ar.edu.unlam.mobile.movil22026h2.data.datasources.local.AppDatabase
import ar.edu.unlam.mobile.movil22026h2.data.datasources.local.DraftDao
import ar.edu.unlam.mobile.movil22026h2.data.datasources.local.FavoriteUserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    private const val DATABASE_NAME = "tuiterApp.db"

    // @Provides porque la base se crea con un builder de Room.
    // No es una clase nuestra con constructor @Inject.
    // @Singleton: UNA sola base en toda la app.
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).fallbackToDestructiveMigration(true).build()

    @Provides
    fun provideDraftDao(database: AppDatabase): DraftDao = database.draftDao()

    @Provides
    fun provideFavoriteUserDao(database: AppDatabase): FavoriteUserDao = database.favoriteUserDao()
}
