package ar.edu.unlam.mobile.movil22026h2.di

import ar.edu.unlam.mobile.movil22026h2.data.repositories.DefaultProfileRepository
import ar.edu.unlam.mobile.movil22026h2.data.repositories.DraftRepository
import ar.edu.unlam.mobile.movil22026h2.data.repositories.DraftRepositoryImpl
import ar.edu.unlam.mobile.movil22026h2.data.repositories.FavoriteUserRepository
import ar.edu.unlam.mobile.movil22026h2.data.repositories.FavoriteUserRepositoryImpl
import ar.edu.unlam.mobile.movil22026h2.data.repositories.PostRepository
import ar.edu.unlam.mobile.movil22026h2.data.repositories.ProfileRepository
import ar.edu.unlam.mobile.movil22026h2.data.repositories.RemotePostRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Le dice a Hilt qué implementación usar cuando alguien pida la interfaz.
 * Así el ViewModel pide ProfileRepository y recibe DefaultProfileRepository.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule { // @Binds exige clase abstracta, no object
    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: DefaultProfileRepository): ProfileRepository

    // "Cuando pidan la interfaz, entregá esta IMPLEMENTACIÓN"
    @Binds
    abstract fun bindDraftRepository(impl: DraftRepositoryImpl): DraftRepository

    @Binds
    abstract fun bindFavoriteUserRepository(impl: FavoriteUserRepositoryImpl): FavoriteUserRepository

    @Binds
    @Singleton
    abstract fun bindPostRepository(impl: RemotePostRepositoryImpl): PostRepository
}
