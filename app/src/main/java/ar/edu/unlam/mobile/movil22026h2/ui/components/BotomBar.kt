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
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import ar.edu.unlam.mobile.movil22026h2.ui.screens.FAVORITES_SCREEN_ROUTE
import ar.edu.unlam.mobile.movil22026h2.ui.screens.HOME_SCREEN_ROUTE

@Composable
fun BottomBar(controller: NavHostController) {
    val navBackStackEntry by controller.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        // En el feed (Home) no se muestra "Inicio" porque ya estás ahí.
        // En Favoritos sí, para poder volver al feed.
        if (currentRoute != HOME_SCREEN_ROUTE) {
            NavigationBarItem(
                selected = currentRoute == HOME_SCREEN_ROUTE,
                onClick = {
                    controller.navigate(HOME_SCREEN_ROUTE) {
                        popUpTo(HOME_SCREEN_ROUTE) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Volver al inicio",
                    )
                },
                label = { Text("Inicio") },
            )
        }

        // "Mi perfil" todavía no tiene pantalla en esta rama: la navegación se
        // conecta cuando se integre HU-03, para no mezclar las ramas.
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO(HU-03): navegar a Mi perfil */ },
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Mi perfil",
                )
            },
            label = { Text("Mi perfil") },
        )

        // En Favoritos no se muestra el ítem Favoritos.
        if (currentRoute != FAVORITES_SCREEN_ROUTE) {
            NavigationBarItem(
                selected = currentRoute == FAVORITES_SCREEN_ROUTE,
                onClick = {
                    controller.navigate(FAVORITES_SCREEN_ROUTE) {
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Favoritos",
                    )
                },
                label = { Text("Favoritos") },
            )
        }

        // "Crear post" todavía no tiene pantalla en esta rama: la navegación se
        // conecta cuando se integre HU-03, para no mezclar las ramas.
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO(HU-03): navegar a Crear post */ },
            icon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Crear post",
                )
            },
            label = { Text("Crear") },
        )
    }
}
