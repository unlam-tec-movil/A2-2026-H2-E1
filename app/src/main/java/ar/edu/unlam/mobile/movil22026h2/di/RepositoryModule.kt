package ar.edu.unlam.mobile.movil22026h2.di

import ar.edu.unlam.mobile.movil22026h2.data.repositories.DefaultProfileRepository
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
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: DefaultProfileRepository): ProfileRepository
}
