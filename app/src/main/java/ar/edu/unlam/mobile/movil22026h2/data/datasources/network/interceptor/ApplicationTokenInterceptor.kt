package ar.edu.unlam.mobile.movil22026h2.data.datasources.network.interceptor

import ar.edu.unlam.mobile.movil22026h2.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Agrega el header Application-Token a TODAS las requests.
 * El valor viene de BuildConfig (leído de local.properties), nunca hardcodeado.
 */
class ApplicationTokenInterceptor
    @Inject
    constructor() : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request =
                chain
                    .request()
                    .newBuilder()
                    .addHeader("Application-Token", BuildConfig.APPLICATION_TOKEN)
                    .build()
            return chain.proceed(request)
        }
    }
