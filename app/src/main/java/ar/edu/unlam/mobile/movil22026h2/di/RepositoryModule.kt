package ar.edu.unlam.mobile.movil22026h2.di

import ar.edu.unlam.mobile.movil22026h2.data.repositories.ApiPostRepository
import ar.edu.unlam.mobile.movil22026h2.data.repositories.DefaultPostLikeRepository
import ar.edu.unlam.mobile.movil22026h2.data.repositories.DefaultProfileRepository
import ar.edu.unlam.mobile.movil22026h2.data.repositories.DraftRepository
import ar.edu.unlam.mobile.movil22026h2.data.repositories.DraftRepositoryImpl
import ar.edu.unlam.mobile.movil22026h2.data.repositories.FavoriteUserRepository
import ar.edu.unlam.mobile.movil22026h2.data.repositories.FavoriteUserRepositoryImpl
import ar.edu.unlam.mobile.movil22026h2.data.repositories.PostLikeRepository
import ar.edu.unlam.mobile.movil22026h2.data.repositories.PostRepository
import ar.edu.unlam.mobile.movil22026h2.data.repositories.ProfileRepository
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

    // HU-11: el feed usa IDs reales para poder guardar los likes en la API.
    // FakePostRepositoryImpl sigue existiendo como fallback sin conexión.
    @Binds
    abstract fun bindPostRepository(impl: ApiPostRepository): PostRepository

    @Binds
    abstract fun bindPostLikeRepository(impl: DefaultPostLikeRepository): PostLikeRepository
}
