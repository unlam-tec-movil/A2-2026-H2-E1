package ar.edu.unlam.mobile.movil22026h2.di

import ar.edu.unlam.mobile.movil22026h2.BuildConfig
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.TuiterApi
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.interceptor.ApplicationTokenInterceptor
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.interceptor.AuthorizationInterceptor
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.interfaces.PostApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import ar.edu.unlam.mobile.movil22026h2.data.datasources.network.interfaces.TuiterApi as AuthTuiterApi

/**
 * Provee las dependencias de red. Hilt las crea una sola vez (@Singleton)
 * y las inyecta donde se pidan.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(
        applicationTokenInterceptor: ApplicationTokenInterceptor,
        authorizationInterceptor: AuthorizationInterceptor,
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(applicationTokenInterceptor)
            .addInterceptor(authorizationInterceptor)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
                },
            ).build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideTuiterApi(retrofit: Retrofit): TuiterApi = retrofit.create(TuiterApi::class.java)

    @Provides
    @Singleton
    fun provideAuthTuiterApi(retrofit: Retrofit): AuthTuiterApi = retrofit.create(AuthTuiterApi::class.java)

    @Provides
    @Singleton
    fun providePostApi(retrofit: Retrofit): PostApi = retrofit.create(PostApi::class.java)
}
