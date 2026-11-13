package ar.edu.unlam.mobile.movil22026h2.data.repositories

import ar.edu.unlam.mobile.movil22026h2.data.datasources.model.Draft

interface DraftRepository {
    suspend fun getDraft(): Draft?

    suspend fun saveDraft(message: String)

    suspend fun clearDraft()
}
