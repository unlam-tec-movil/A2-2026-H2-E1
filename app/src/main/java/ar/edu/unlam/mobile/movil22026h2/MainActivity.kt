package ar.edu.unlam.mobile.movil22026h2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ar.edu.unlam.mobile.movil22026h2.ui.components.BottomBar
import ar.edu.unlam.mobile.movil22026h2.ui.components.SnackbarVisualsWithError
import ar.edu.unlam.mobile.movil22026h2.ui.screens.HOME_SCREEN_ROUTE
import ar.edu.unlam.mobile.movil22026h2.ui.screens.HomeScreen
import ar.edu.unlam.mobile.movil22026h2.ui.screens.login.LoginScreen
import ar.edu.unlam.mobile.movil22026h2.ui.screens.register.RegisterScreen
import ar.edu.unlam.mobile.movil22026h2.ui.theme.Movil22026H2Theme
import dagger.hilt.android.AndroidEntryPoint

const val LOGIN_SCREEN_ROUTE = "login_screen"
const val REGISTER_SCREEN_ROUTE = "register_screen"
const val POST_SCREEN_ROUTE = "post"
const val HOME_SCREEN_ROUTE = "home"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Movil22026H2Theme {
                // A surface container using the 'background' color from the theme
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
    // Controller es el elemento que nos permite navegar entre pantallas. Tiene las acciones
    // para navegar como naviegate y también la información de en dónde se "encuentra" el usuario
    // a través del back stack
    val controller = rememberNavController()
    val snackBarHostState = remember { SnackbarHostState() }
    Scaffold(
        bottomBar = { BottomBar(controller = controller) },
        floatingActionButton = {
            IconButton(onClick = { controller.navigate("home") }) {
                Icon(Icons.Filled.Home, contentDescription = "Home")
            }
        },
        snackbarHost = {
            SnackbarHost(snackBarHostState) { data ->
                // custom snackbar with the custom action button color and border
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
        // NavHost es el componente que funciona como contenedor de los otros componentes que
        // podrán ser destinos de navegación.
        // NavHost es el componente que funciona como contenedor de los otros componentes...
        NavHost(navController = controller, startDestination = LOGIN_SCREEN_ROUTE) {

            // 1. Pantalla de Login (HU-05)
            composable(LOGIN_SCREEN_ROUTE) {
                LoginScreen(
                    modifier = Modifier.padding(paddingValue),
                    onLoginSuccess = {
                        // Cuando loguea bien, va al home y borra el login del historial
                        controller.navigate(HOME_SCREEN_ROUTE) {
                            popUpTo(LOGIN_SCREEN_ROUTE) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        // Va a la pantalla de registro si no tiene cuenta
                        controller.navigate(REGISTER_SCREEN_ROUTE)
                    }
                )
            }

            // 2. Pantalla de Registro (HU-04)
            composable(REGISTER_SCREEN_ROUTE) {
                RegisterScreen(
                    modifier = Modifier.padding(paddingValue),
                    onRegisterSuccess = {
                        // Vuelve al login cuando se registra con éxito
                        controller.popBackStack()
                    }
                )
            }

            composable(HOME_SCREEN_ROUTE) {
                // Home es el componente en sí que es el destino de navegación.
                HomeScreen(modifier = Modifier.padding(paddingValue))
            }
        }
    }
}
