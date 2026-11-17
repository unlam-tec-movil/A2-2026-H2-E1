package ar.edu.unlam.mobile.movil22026h2.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue

// Avatar del autor. Como el proyecto no tiene (todavía) una librería para
// cargar imágenes desde una URL, se dibuja un círculo con la inicial.
// El color se deriva del avatarUrl guardado, así cada autor tiene el suyo.
@Composable
fun AuthorAvatar(
    authorName: String,
    avatarUrl: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
) {
    val palette =
        listOf(
            Color(0xFF2F4A5A),
            Color(0xFF6A4C93),
            Color(0xFF1B7F79),
            Color(0xFF9C4221),
        )
    val avatarColor = remember(avatarUrl) { palette[avatarUrl.hashCode().absoluteValue % palette.size] }
    val initial =
        authorName
            .trim()
            .take(1)
            .uppercase()
            .ifEmpty { "?" }

    Box(
        modifier =
            modifier
                .size(size)
                .clip(CircleShape)
                .background(avatarColor),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initial,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}
