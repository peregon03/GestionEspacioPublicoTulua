package co.gov.tulua.espaciopublico.domain.repository

import co.gov.tulua.espaciopublico.domain.model.Vendor

interface VendorRepository {
    suspend fun getVendorById(id: Long): Result<Vendor>
    suspend fun searchVendors(query: String): Result<List<Vendor>>
}
