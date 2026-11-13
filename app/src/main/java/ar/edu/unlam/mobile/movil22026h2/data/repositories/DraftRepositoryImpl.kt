package ar.edu.unlam.mobile.movil22026h2.data.repositories

import ar.edu.unlam.mobile.movil22026h2.data.datasources.model.Draft
import javax.inject.Inject

class DraftRepositoryImpl
    @Inject
    constructor(
        private val draftDao: DraftDao,
    ) : draftRepository {
        // "?" mantiene null si no hay borrador.1
        override suspend fun getDraft(): Draft? = draftDao.getDraft()?.toDomain()

        override suspend fun saveDraft(message: String) = draftDao.upsert(Draft(message).toEntity())

        override suspend fun clearDraft() = draftDao.clear()
    }
