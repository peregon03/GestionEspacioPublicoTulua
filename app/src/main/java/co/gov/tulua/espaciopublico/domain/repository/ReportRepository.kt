package co.gov.tulua.espaciopublico.domain.repository

import co.gov.tulua.espaciopublico.domain.model.ReportData

interface ReportRepository {
    suspend fun getReportData(
        from: String,
        to: String,
        sectorId: Long? = null,
    ): Result<ReportData>

    suspend fun exportReport(
        from: String,
        to: String,
        sectorId: Long? = null,
        format: String = "pdf",
    ): Result<String>
}
