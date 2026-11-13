package ar.edu.unlam.mobile.movil22026h2.data.datasources.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "draft")
data class Draft(
    @PrimaryKey val name: String,
    val message: String,
)
