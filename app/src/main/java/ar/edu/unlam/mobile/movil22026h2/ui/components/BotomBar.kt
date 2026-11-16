package ar.edu.unlam.mobile.movil22026h2.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import ar.edu.unlam.mobile.movil22026h2.ui.screens.HOME_SCREEN_ROUTE
import ar.edu.unlam.mobile.movil22026h2.ui.screens.profile.PROFILE_SCREEN_ROUTE

@Composable
fun BottomBar(controller: NavHostController) {
    val navBackStackEntry by controller.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == HOME_SCREEN_ROUTE,
            onClick = { controller.navigate(HOME_SCREEN_ROUTE) { launchSingleTop = true } },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") },
        )
        NavigationBarItem(
            selected = currentRoute == PROFILE_SCREEN_ROUTE,
            onClick = { controller.navigate(PROFILE_SCREEN_ROUTE) { launchSingleTop = true } },
            icon = { Icon(Icons.Filled.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") },
        )
    }
}
