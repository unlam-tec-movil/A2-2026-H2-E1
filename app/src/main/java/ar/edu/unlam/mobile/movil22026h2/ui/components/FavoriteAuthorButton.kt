package ar.edu.unlam.mobile.movil22026h2.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

// Recibe datos y avisa con un callback. No conoce el ViewModel.
// State hoisting: el estado vive arriba, el componente solo dibuja.
@Composable
fun FavoriteAuthorButton(
    authorName: String,
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // enabled = !isFavorite: si ya es favorito, no se puede guardar de nuevo
    // (escenario 2). El texto lo dice explícitamente.
    TextButton(
        onClick = onClick,
        enabled = !isFavorite,
        modifier = modifier,
    ) {
        if (isFavorite) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                // Tint explícito: se sigue viendo activo aunque el botón esté deshabilitado.
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "Ya es favorito",
                color = MaterialTheme.colorScheme.primary,
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
            )
            Text(text = "Agregar a favoritos")
        }
    }
}

// @Preview: Android Studio lo dibuja sin correr la app, para ver los dos estados.
@Preview(showBackground = true)
@Composable
private fun NotFavoritePreview() {
    MaterialTheme { FavoriteAuthorButton("Test5", isFavorite = false, onClick = {}) }
}

@Preview(showBackground = true)
@Composable
private fun FavoritePreview() {
    MaterialTheme { FavoriteAuthorButton("Test5", isFavorite = true, onClick = {}) }
}
