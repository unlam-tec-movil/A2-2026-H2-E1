package ar.edu.unlam.mobile.movil22026h2.di

import ar.edu.unlam.mobile.movil22026h2.data.repositories.FavoriteUserRepository
import ar.edu.unlam.mobile.movil22026h2.data.repositories.FavoriteUserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule { // @Binds exige clase abstracta, no object

    // "Cuando pidan interfaz, entregá esta IMPLEMENTACIÓN"
    @Binds
    abstract fun bindFavoriteUserRepository(impl: FavoriteUserRepositoryImpl): FavoriteUserRepository
}
