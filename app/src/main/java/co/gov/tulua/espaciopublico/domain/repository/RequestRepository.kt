package co.gov.tulua.espaciopublico.domain.repository

import co.gov.tulua.espaciopublico.domain.model.Request
import co.gov.tulua.espaciopublico.domain.model.RequestStatus

interface RequestRepository {
    suspend fun getRequests(
        status: RequestStatus? = null,
        search: String? = null,
        page: Int = 1,
        size: Int = 20,
    ): Result<List<Request>>

    suspend fun getRequestById(id: Long): Result<Request>
    suspend fun approveRequest(requestId: Long): Result<Request>
    suspend fun rejectRequest(requestId: Long, reason: String): Result<Request>
}
