package ar.edu.unlam.mobile.movil22026h2.data.datasources.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DraftDao {
    @Query("SELECT * FROM draft WHERE id = 1")
    suspend fun getDraft(): DraftEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDraft(draft: DraftEntity)

    @Query("DELETE FROM draft WHERE id = 1")
    suspend fun clear()
}
