package ar.edu.unlam.mobile.movil22026h2.data.session

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Guarda el token del usuario logueado.
 *
 * El login (R4) debería llamar a [saveToken] con el token que devuelve POST /login.
 */
@Singleton
class SessionManager
    @Inject // indicamos como Hilt puede construir un objeto
    constructor(
        @ApplicationContext context: Context,
    ) {
        private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

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
