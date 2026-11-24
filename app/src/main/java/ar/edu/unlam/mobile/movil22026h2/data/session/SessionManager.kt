package ar.edu.unlam.mobile.movil22026h2.data.session

import android.content.Context
import ar.edu.unlam.mobile.movil22026h2.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Guarda el token del usuario logueado.
 *
 * El login (R4) debería llamar a [saveToken] con el token que devuelve POST /login.
 * Mientras R4 no exista, se puede precargar un token de prueba desde local.properties
 * (TUITER_USER_TOKEN) para poder probar R5 y R8.
 */
@Singleton
class SessionManager
    @Inject // indicamos como Hilt puede construir un objeto
    constructor(
        @ApplicationContext context: Context,
    ) {
        private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        init {
            if (BuildConfig.DEBUG && getToken() == null && BuildConfig.DEBUG_USER_TOKEN.isNotBlank()) {
                saveToken(BuildConfig.DEBUG_USER_TOKEN)
            }
        }

        fun getToken(): String? = prefs.getString(KEY_TOKEN, null)?.takeIf { it.isNotBlank() }

        fun saveToken(token: String) {
            prefs.edit().putString(KEY_TOKEN, token).apply()
        }

        fun clear() {
            prefs.edit().remove(KEY_TOKEN).apply()
        }

        private companion object {
            const val PREFS_NAME = "session"
            const val KEY_TOKEN = "user_token"
        }
    }
