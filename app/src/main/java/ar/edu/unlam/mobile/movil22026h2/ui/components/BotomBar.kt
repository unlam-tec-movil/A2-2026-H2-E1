package ar.edu.unlam.mobile.movil22026h2.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import ar.edu.unlam.mobile.movil22026h2.ui.screens.CREATE_POST_SCREEN_ROUTE
import ar.edu.unlam.mobile.movil22026h2.ui.screens.HOME_SCREEN_ROUTE

@Composable
fun BottomBar(controller: NavHostController) {
    val navBackStackEntry by controller.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == HOME_SCREEN_ROUTE,
            onClick = {
                controller.navigate(HOME_SCREEN_ROUTE) {
                    // Vuelve a Home sin apilar copias de la pantalla.
                    popUpTo(HOME_SCREEN_ROUTE) { inclusive = false }
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                )
            },
            label = { Text("Home") },
        )

        NavigationBarItem(
            selected = currentRoute == CREATE_POST_SCREEN_ROUTE,
            onClick = {
                controller.navigate(CREATE_POST_SCREEN_ROUTE) {
                    launchSingleTop = true
                }
            },
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
