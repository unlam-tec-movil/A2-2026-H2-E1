package ar.edu.unlam.mobile.movil22026h2.data.datasources.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import ar.edu.unlam.mobile.movil22026h2.data.datasources.model.Draft

@Entity(tableName = "draft")
data class DraftEntity(
    @PrimaryKey val id: Int = 1,
    val message: String = "",
    val updateAtMillis: Long = System.currentTimeMillis(),
)

fun DraftEntity.toDraft(): Draft =
    Draft(
        text = message,
        updateAtMillis = updateAtMillis,
    )

fun Draft.toEntity(): DraftEntity =
    DraftEntity(
        id = 1, // siempre 1, asi pisa el mismo borrador
        message = text,
        updateAtMillis = System.currentTimeMillis(), // actualiza la fecha al guardar
    )
