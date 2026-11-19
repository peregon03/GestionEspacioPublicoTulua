package co.gov.tulua.espaciopublico.domain.repository

import co.gov.tulua.espaciopublico.domain.model.Module
import co.gov.tulua.espaciopublico.domain.model.ModuleStatus
import co.gov.tulua.espaciopublico.domain.model.ModuleType

interface ModuleRepository {
    suspend fun getModules(
        search: String? = null,
        type: ModuleType? = null,
        status: ModuleStatus? = null,
        sectorId: Long? = null,
        page: Int = 1,
        size: Int = 20,
    ): Result<List<Module>>

    suspend fun getModuleById(id: Long): Result<Module>
    suspend fun createModule(module: Module): Result<Module>
    suspend fun updateModule(module: Module): Result<Module>
    suspend fun uploadPhoto(moduleId: Long, photoPath: String): Result<String>

    suspend fun getModulesForMap(
        bbox: String? = null,
        type: ModuleType? = null,
        status: ModuleStatus? = null,
        sectorId: Long? = null,
    ): Result<List<Module>>

    suspend fun exportModules(format: String = "csv"): Result<String>
}
