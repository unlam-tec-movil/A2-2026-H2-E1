package ar.edu.unlam.mobile.movil22026h2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ar.edu.unlam.mobile.movil22026h2.ui.components.BottomBar
import ar.edu.unlam.mobile.movil22026h2.ui.components.SnackbarVisualsWithError
import ar.edu.unlam.mobile.movil22026h2.ui.screens.CREATE_POST_SCREEN_ROUTE
import ar.edu.unlam.mobile.movil22026h2.ui.screens.CreatePostScreen
import ar.edu.unlam.mobile.movil22026h2.ui.screens.FAVORITES_SCREEN_ROUTE
import ar.edu.unlam.mobile.movil22026h2.ui.screens.FavoriteUsersScreen
import ar.edu.unlam.mobile.movil22026h2.ui.screens.HOME_SCREEN_ROUTE
import ar.edu.unlam.mobile.movil22026h2.ui.screens.HomeScreen
import ar.edu.unlam.mobile.movil22026h2.ui.screens.login.LoginScreen
import ar.edu.unlam.mobile.movil22026h2.ui.screens.profile.PROFILE_SCREEN_ROUTE
import ar.edu.unlam.mobile.movil22026h2.ui.screens.profile.ProfileScreen
import ar.edu.unlam.mobile.movil22026h2.ui.screens.register.RegisterScreen
import ar.edu.unlam.mobile.movil22026h2.ui.theme.Movil22026H2Theme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

const val LOGIN_SCREEN_ROUTE = "login_screen"
const val REGISTER_SCREEN_ROUTE = "register_screen"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Movil22026H2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val controller = rememberNavController()
    val currentRoute =
        controller
            .currentBackStackEntryAsState()
            .value
            ?.destination
            ?.route
    val showBottomBar = currentRoute != LOGIN_SCREEN_ROUTE && currentRoute != REGISTER_SCREEN_ROUTE
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Un único punto para mostrar mensajes (errores y confirmaciones) desde cualquier pantalla.
    val showMessage: (String, Boolean) -> Unit = { message, isError ->
        coroutineScope.launch {
            snackBarHostState.showSnackbar(
                SnackbarVisualsWithError(message = message, isError = isError),
            )
        }
    }

    Scaffold(
        // La barra inferior es el único acceso a "Crear": antes había además un
        // botón flotante que hacía exactamente lo mismo.
        bottomBar = { if (showBottomBar) BottomBar(controller = controller) },
        snackbarHost = {
            SnackbarHost(snackBarHostState) { data ->
                val isError = (data.visuals as? SnackbarVisualsWithError)?.isError ?: false
                val buttonColor =
                    if (isError) {
                        ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.error,
                        )
                    } else {
                        ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.inversePrimary,
                        )
                    }

                Snackbar(
                    modifier =
                        Modifier
                            .border(2.dp, MaterialTheme.colorScheme.secondary)
                            .padding(12.dp),
                    action = {
                        TextButton(
                            onClick = { if (isError) data.dismiss() else data.performAction() },
                            colors = buttonColor,
                        ) {
                            Text(data.visuals.actionLabel ?: "")
                        }
                    },
                ) {
                    Text(data.visuals.message)
                }
            }
        },
    ) { paddingValue ->
        NavHost(navController = controller, startDestination = LOGIN_SCREEN_ROUTE) {
            composable(LOGIN_SCREEN_ROUTE) {
                LoginScreen(
                    modifier = Modifier.padding(paddingValue),
                    onLoginSuccess = {
                        controller.navigate(HOME_SCREEN_ROUTE) {
                            popUpTo(LOGIN_SCREEN_ROUTE) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { controller.navigate(REGISTER_SCREEN_ROUTE) },
                )
            }

            composable(REGISTER_SCREEN_ROUTE) {
                RegisterScreen(
                    modifier = Modifier.padding(paddingValue),
                    onRegisterSuccess = { controller.popBackStack() },
                )
            }

            composable(HOME_SCREEN_ROUTE) {
                // Home es el componente en sí que es el destino de navegación.
                HomeScreen(modifier = Modifier.padding(paddingValue))
            }
            composable(PROFILE_SCREEN_ROUTE) {
                ProfileScreen(
                    modifier = Modifier.padding(paddingValue),
                    onError = { error -> showMessage(error.message ?: "Ocurrió un error", true) },
                    onMessage = { message -> showMessage(message, false) },
                )
            }

            composable(CREATE_POST_SCREEN_ROUTE) {
                CreatePostScreen(
                    modifier = Modifier.padding(paddingValue),
                    onBack = {
                        // Al publicar, vuelve a Home en lugar de apilar otra pantalla.
                        controller.popBackStack(HOME_SCREEN_ROUTE, inclusive = false)
                    },
                )
            }

            composable(FAVORITES_SCREEN_ROUTE) {
                FavoriteUsersScreen(modifier = Modifier.padding(paddingValue))
            }
        }
    }
}
