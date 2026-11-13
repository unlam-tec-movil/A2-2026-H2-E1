package ar.edu.unlam.mobile.movil22026h2.data.datasources.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ar.edu.unlam.mobile.movil22026h2.data.datasources.model.Draft

@Dao
interface DraftDao {
    @Query("SELECT * FROM draft WHERE name = name")
    suspend fun getDraft(): Draft?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDraft(draft: Draft)

    @Query("DELETE FROM draft WHERE name = name")
    suspend fun clear()
}
