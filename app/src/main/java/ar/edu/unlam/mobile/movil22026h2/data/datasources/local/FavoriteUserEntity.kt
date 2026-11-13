package ar.edu.unlam.mobile.movil22026h2.data.datasources.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_users")
data class FavoriteUserEntity(
    // Clave primaria = nombre del autor. Si guardás dos veces al mismo autor,
    // hay una sola fila: así se cumple "no se guarda dos veces".
    @PrimaryKey val name: String,
    // Se guarda la Url (texto), no la imagen.
    val avatarUrl: String,
)
