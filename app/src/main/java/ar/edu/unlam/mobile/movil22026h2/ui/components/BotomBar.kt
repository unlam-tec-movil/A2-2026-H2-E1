package ar.edu.unlam.mobile.movil22026h2.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import ar.edu.unlam.mobile.movil22026h2.ui.screens.CREATE_POST_SCREEN_ROUTE
import ar.edu.unlam.mobile.movil22026h2.ui.screens.FAVORITES_SCREEN_ROUTE
import ar.edu.unlam.mobile.movil22026h2.ui.screens.HOME_SCREEN_ROUTE
import ar.edu.unlam.mobile.movil22026h2.ui.screens.PROFILE_SCREEN_ROUTE

// Un ítem de la barra inferior. "hiddenOn" es la ruta donde NO debe mostrarse:
// no tiene sentido ofrecer la pantalla en la que ya estás parado.
private data class BottomBarItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val hiddenOn: String,
)

private val bottomBarItems =
    listOf(
        BottomBarItem(HOME_SCREEN_ROUTE, "Volver al inicio", Icons.Default.Home, HOME_SCREEN_ROUTE),
        BottomBarItem(PROFILE_SCREEN_ROUTE, "Mi perfil", Icons.Default.Person, PROFILE_SCREEN_ROUTE),
        BottomBarItem(FAVORITES_SCREEN_ROUTE, "Favoritos", Icons.Default.Star, FAVORITES_SCREEN_ROUTE),
        BottomBarItem(CREATE_POST_SCREEN_ROUTE, "Crear", Icons.Default.Add, CREATE_POST_SCREEN_ROUTE),
    )

@Composable
fun BottomBar(controller: NavHostController) {
    val navBackStackEntry by controller.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        // En Home se ven Mi perfil / Favoritos / Crear.
        // En Nuevo post se ven Volver al inicio / Mi perfil / Favoritos (sin Crear).
        bottomBarItems
            .filter { it.hiddenOn != currentRoute }
            .forEach { item ->
                NavigationBarItem(
                    selected = currentRoute == item.route,
                    onClick = { controller.navigateTo(item.route) },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                        )
                    },
                    label = { Text(item.label) },
                )
            }
    }
}

private fun NavHostController.navigateTo(route: String) {
    navigate(route) {
        // Al volver a Inicio se saca la pantalla actual del back stack,
        // así el botón "atrás" del sistema no vuelve a Nuevo post.
        if (route == HOME_SCREEN_ROUTE) {
            popUpTo(HOME_SCREEN_ROUTE) { inclusive = false }
        }
        launchSingleTop = true
    }
}
