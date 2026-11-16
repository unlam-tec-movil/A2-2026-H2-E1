package ar.edu.unlam.mobile.movil22026h2.data.datasources.network.interceptor

import ar.edu.unlam.mobile.movil22026h2.data.session.SessionManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Agrega el header Authorization con el token del usuario logueado (el de POST /login).
 * Si todavía no hay token, no agrega el header (los endpoints /me responderán 401).
 */
class AuthorizationInterceptor
    @Inject
    constructor(
        private val sessionManager: SessionManager,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val token = sessionManager.getToken()
            val request =
                if (token != null) {
                    chain
                        .request()
                        .newBuilder()
                        .addHeader("Authorization", token)
                        .build()
                } else {
                    chain.request()
                }
            return chain.proceed(request)
        }
    }
