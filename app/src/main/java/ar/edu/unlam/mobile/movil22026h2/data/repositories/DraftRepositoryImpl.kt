package ar.edu.unlam.mobile.movil22026h2.data.repositories

import ar.edu.unlam.mobile.movil22026h2.data.datasources.local.DraftDao
import ar.edu.unlam.mobile.movil22026h2.data.datasources.local.toDraft
import ar.edu.unlam.mobile.movil22026h2.data.datasources.local.toEntity
import ar.edu.unlam.mobile.movil22026h2.data.datasources.model.Draft
import javax.inject.Inject

class DraftRepositoryImpl
    @Inject
    constructor(
        private val draftDao: DraftDao,
    ) : DraftRepository {
        // "?" mantiene null si no hay borrador.1
        override suspend fun getDraft(): Draft? = draftDao.getDraft()?.toDraft()

        override suspend fun saveDraft(message: String) = draftDao.saveDraft(Draft(message).toEntity())

        override suspend fun clearDraft() = draftDao.clear()
    }
