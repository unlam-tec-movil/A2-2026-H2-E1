package ar.edu.unlam.mobile.movil22026h2.di

import ar.edu.unlam.mobile.movil22026h2.data.repositories.DraftRepository
import ar.edu.unlam.mobile.movil22026h2.data.repositories.DraftRepositoryImpl
import ar.edu.unlam.mobile.movil22026h2.data.repositories.FakePostRepositoryImpl
import ar.edu.unlam.mobile.movil22026h2.data.repositories.FavoriteUserRepository
import ar.edu.unlam.mobile.movil22026h2.data.repositories.FavoriteUserRepositoryImpl
import ar.edu.unlam.mobile.movil22026h2.data.repositories.PostRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule { // @Binds exige clase abstracta, no object

    // "Cuando pidan la interfaz, entregá esta IMPLEMENTACIÓN"
    @Binds
    abstract fun bindDraftRepository(impl: DraftRepositoryImpl): DraftRepository

    @Binds
    abstract fun bindFavoriteUserRepository(impl: FavoriteUserRepositoryImpl): FavoriteUserRepository

    // Los posts todavía son falsos: esta línea es lo único que hay que cambiar
    // cuando exista la implementación real con la API.
    @Binds
    abstract fun bindPostRepository(impl: FakePostRepositoryImpl): PostRepository
}
