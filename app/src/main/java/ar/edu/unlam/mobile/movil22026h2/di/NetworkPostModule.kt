package ar.edu.unlam.mobile.movil22026h2.di

import ar.edu.unlam.mobile.movil22026h2.data.repositories.NetworkPostRepositoryImpl
import ar.edu.unlam.mobile.movil22026h2.data.repositories.PostRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkPostModule {
    @Binds
    @NetworkPosts
    @Singleton
    abstract fun bindNetworkPostRepository(impl: NetworkPostRepositoryImpl): PostRepository
}
