package co.gov.tulua.espaciopublico.data.mock

import co.gov.tulua.espaciopublico.domain.model.Document
import co.gov.tulua.espaciopublico.domain.model.DocumentType
import co.gov.tulua.espaciopublico.domain.model.GeoLocation
import co.gov.tulua.espaciopublico.domain.model.Module
import co.gov.tulua.espaciopublico.domain.model.ModuleStatus
import co.gov.tulua.espaciopublico.domain.model.ModuleType
import co.gov.tulua.espaciopublico.domain.model.Permit
import co.gov.tulua.espaciopublico.domain.model.Sanction
import co.gov.tulua.espaciopublico.domain.model.SanctionType
import co.gov.tulua.espaciopublico.domain.model.Vendor
import co.gov.tulua.espaciopublico.domain.repository.ModuleRepository
import javax.inject.Inject

class MockModuleRepository @Inject constructor(
    private val vendorRepo: MockVendorRepository,
) : ModuleRepository {

    private val modules: List<Module> by lazy { buildModules() }

    private fun v(id: Long): Vendor = vendorRepo.vendors.first { it.id == id }

    private fun buildModules(): List<Module> = listOf(
        // ── Sector 1: Carrera 25 ──────────────────────────────────────
        module(1, "MOD-2025-0001", "Puesto de frutas La Esquina", ModuleType.FORMAL, "Alimentos", ModuleStatus.APROBADO, v(1), loc("Cra 25 #25-10", 4.0855, -76.1958, 1), 4.0, "2026-06-30"),
        module(2, "MOD-2025-0002", "Caseta de jugos naturales", ModuleType.FORMAL, "Bebidas", ModuleStatus.APROBADO, v(2), loc("Cra 25 #25-32", 4.0857, -76.1960, 1), 3.5, "2026-06-30"),
        module(3, "MOD-2025-0003", "Venta de ropa deportiva", ModuleType.INFORMAL, "Ropa", ModuleStatus.EN_REVISION, v(3), loc("Cra 25 #26-05", 4.0860, -76.1962, 1), 5.0, null),
        module(4, "MOD-2025-0004", "Artesanías del Valle", ModuleType.FORMAL, "Artesanías", ModuleStatus.APROBADO, v(4), loc("Cra 25 #26-18", 4.0862, -76.1963, 1), 6.0, "2026-12-31"),
        module(5, "MOD-2025-0005", "Accesorios y bisutería", ModuleType.INFORMAL, "Accesorios", ModuleStatus.RECHAZADO, v(5), loc("Cra 25 #26-40", 4.0864, -76.1965, 1), 2.5, null),
        module(6, "MOD-2025-0006", "Punto de empanadas Doña María", ModuleType.FORMAL, "Alimentos", ModuleStatus.APROBADO, v(6), loc("Cra 25 #27-02", 4.0867, -76.1967, 1), 3.0, "2026-08-15"),
        module(7, "MOD-2025-0007", "Venta de calzado", ModuleType.INFORMAL, "Calzado", ModuleStatus.EN_REVISION, v(7), loc("Cra 25 #27-15", 4.0869, -76.1968, 1), 4.5, null),
        module(8, "MOD-2025-0008", "Dulcería y confitería", ModuleType.FORMAL, "Alimentos", ModuleStatus.APROBADO, v(8), loc("Cra 25 #27-30", 4.0871, -76.1970, 1), 3.0, "2026-09-30"),

        // ── Sector 2: Galería y Plaza de Mercado ──────────────────────
        module(9, "MOD-2025-0009", "Puesto de verduras frescas", ModuleType.FORMAL, "Alimentos", ModuleStatus.APROBADO, v(9), loc("Cll 26 #25-05", 4.0848, -76.1952, 2), 5.5, "2026-06-30"),
        module(10, "MOD-2025-0010", "Carnicería La Esperanza", ModuleType.FORMAL, "Alimentos", ModuleStatus.APROBADO, v(10), loc("Cll 26 #25-12", 4.0849, -76.1953, 2), 8.0, "2026-12-31"),
        module(11, "MOD-2025-0011", "Pescadería del Pacífico", ModuleType.FORMAL, "Alimentos", ModuleStatus.APROBADO, v(11), loc("Cll 26 #25-20", 4.0850, -76.1954, 2), 6.0, "2026-06-30"),
        module(12, "MOD-2025-0012", "Hierbas medicinales", ModuleType.INFORMAL, "Naturismo", ModuleStatus.EN_REVISION, v(12), loc("Cll 26 #25-28", 4.0851, -76.1955, 2), 2.0, null),
        module(13, "MOD-2025-0013", "Granos y abarrotes El Triunfo", ModuleType.FORMAL, "Alimentos", ModuleStatus.APROBADO, v(13), loc("Cll 26 #25-35", 4.0852, -76.1956, 2), 7.0, "2026-10-31"),
        module(14, "MOD-2025-0014", "Flores y plantas ornamentales", ModuleType.INFORMAL, "Flores", ModuleStatus.APROBADO, v(14), loc("Cll 26 #25-42", 4.0853, -76.1957, 2), 4.0, "2026-04-30"),
        module(15, "MOD-2025-0015", "Lácteos y quesos del campo", ModuleType.FORMAL, "Alimentos", ModuleStatus.APROBADO, v(15), loc("Cll 27 #25-05", 4.0846, -76.1950, 2), 5.0, "2026-06-30"),
        module(16, "MOD-2025-0016", "Venta de huevos y aves", ModuleType.FORMAL, "Alimentos", ModuleStatus.SUSPENDIDO, v(16), loc("Cll 27 #25-12", 4.0847, -76.1951, 2), 4.5, "2026-06-30"),

        // ── Sector 3: Parque Boyacá ───────────────────────────────────
        module(17, "MOD-2025-0017", "Heladería ambulante", ModuleType.INFORMAL, "Bebidas", ModuleStatus.APROBADO, v(17), loc("Cra 26 #26-01", 4.0840, -76.1945, 3), 1.5, "2026-05-31"),
        module(18, "MOD-2025-0018", "Venta de minutos y accesorios", ModuleType.INFORMAL, "Telecomunicaciones", ModuleStatus.APROBADO, v(18), loc("Cra 26 #26-10", 4.0841, -76.1946, 3), 2.0, "2026-07-31"),
        module(19, "MOD-2025-0019", "Café y aromáticas", ModuleType.FORMAL, "Bebidas", ModuleStatus.APROBADO, v(19), loc("Cra 26 #26-18", 4.0842, -76.1947, 3), 3.5, "2026-11-30"),
        module(20, "MOD-2025-0020", "Puesto de lotería y chance", ModuleType.FORMAL, "Juegos de azar", ModuleStatus.APROBADO, v(20), loc("Cra 26 #26-25", 4.0843, -76.1948, 3), 2.0, "2026-06-30"),
        module(21, "MOD-2025-0021", "Lustrada de zapatos Don Pedro", ModuleType.INFORMAL, "Servicios", ModuleStatus.EN_REVISION, v(1), loc("Cra 26 #26-30", 4.0844, -76.1949, 3), 1.0, null),
        module(22, "MOD-2025-0022", "Venta de periódicos y revistas", ModuleType.FORMAL, "Medios", ModuleStatus.APROBADO, v(2), loc("Cra 27 #26-05", 4.0838, -76.1943, 3), 2.5, "2026-08-31"),
        module(23, "MOD-2025-0023", "Algodones y crispetas", ModuleType.INFORMAL, "Alimentos", ModuleStatus.APROBADO, v(3), loc("Cra 27 #26-12", 4.0839, -76.1944, 3), 1.5, "2026-06-30"),
        module(24, "MOD-2025-0024", "Fotografía instantánea", ModuleType.INFORMAL, "Servicios", ModuleStatus.RECHAZADO, v(4), loc("Cra 27 #26-20", 4.0837, -76.1942, 3), 2.0, null),

        // ── Sector 4: Parque Céspedes ─────────────────────────────────
        module(25, "MOD-2025-0025", "Chontaduro y cocadas", ModuleType.INFORMAL, "Alimentos", ModuleStatus.APROBADO, v(5), loc("Cll 27 #27-01", 4.0832, -76.1938, 4), 1.5, "2026-06-30"),
        module(26, "MOD-2025-0026", "Manualidades y tejidos", ModuleType.INFORMAL, "Artesanías", ModuleStatus.APROBADO, v(6), loc("Cll 27 #27-10", 4.0833, -76.1939, 4), 3.0, "2026-09-30"),
        module(27, "MOD-2025-0027", "Venta de sombreros y gorras", ModuleType.INFORMAL, "Accesorios", ModuleStatus.EN_REVISION, v(7), loc("Cll 27 #27-18", 4.0834, -76.1940, 4), 2.0, null),
        module(28, "MOD-2025-0028", "Libros usados El Saber", ModuleType.FORMAL, "Cultura", ModuleStatus.APROBADO, v(8), loc("Cll 27 #27-25", 4.0835, -76.1941, 4), 4.0, "2026-12-31"),
        module(29, "MOD-2025-0029", "Punto de agua y gaseosas", ModuleType.INFORMAL, "Bebidas", ModuleStatus.APROBADO, v(9), loc("Cll 28 #27-05", 4.0830, -76.1936, 4), 1.0, "2026-05-31"),
        module(30, "MOD-2025-0030", "Globos y juguetes", ModuleType.INFORMAL, "Entretenimiento", ModuleStatus.APROBADO, v(10), loc("Cll 28 #27-12", 4.0831, -76.1937, 4), 2.0, "2026-07-31"),

        // ── Sector 5: Plaza de Mercado Central ────────────────────────
        module(31, "MOD-2025-0031", "Frutería tropical", ModuleType.FORMAL, "Alimentos", ModuleStatus.APROBADO, v(11), loc("Cra 24 #27-01", 4.0825, -76.1930, 5), 6.0, "2026-06-30"),
        module(32, "MOD-2025-0032", "Especias y condimentos", ModuleType.FORMAL, "Alimentos", ModuleStatus.APROBADO, v(12), loc("Cra 24 #27-08", 4.0826, -76.1931, 5), 3.5, "2026-11-30"),
        module(33, "MOD-2025-0033", "Panadería artesanal", ModuleType.FORMAL, "Alimentos", ModuleStatus.APROBADO, v(13), loc("Cra 24 #27-15", 4.0827, -76.1932, 5), 8.0, "2026-12-31"),
        module(34, "MOD-2025-0034", "Ropa de segunda mano", ModuleType.INFORMAL, "Ropa", ModuleStatus.EN_REVISION, v(14), loc("Cra 24 #27-22", 4.0828, -76.1933, 5), 4.0, null),
        module(35, "MOD-2025-0035", "Utensilios de cocina", ModuleType.FORMAL, "Hogar", ModuleStatus.APROBADO, v(15), loc("Cra 24 #27-30", 4.0829, -76.1934, 5), 5.5, "2026-08-31"),
        module(36, "MOD-2025-0036", "Productos de aseo", ModuleType.FORMAL, "Hogar", ModuleStatus.APROBADO, v(16), loc("Cra 25 #27-01", 4.0823, -76.1928, 5), 4.0, "2026-06-30"),
        module(37, "MOD-2025-0037", "Mechas y extensiones", ModuleType.INFORMAL, "Belleza", ModuleStatus.RECHAZADO, v(17), loc("Cra 25 #27-08", 4.0824, -76.1929, 5), 2.5, null),
        module(38, "MOD-2025-0038", "Ferretería popular", ModuleType.FORMAL, "Ferretería", ModuleStatus.APROBADO, v(18), loc("Cra 25 #27-15", 4.0822, -76.1927, 5), 10.0, "2026-12-31"),
        module(39, "MOD-2025-0039", "Comidas rápidas El Paisa", ModuleType.FORMAL, "Alimentos", ModuleStatus.APROBADO, v(19), loc("Cra 25 #27-22", 4.0821, -76.1926, 5), 6.0, "2026-09-30"),
        module(40, "MOD-2025-0040", "Papelería y miscelánea", ModuleType.FORMAL, "Papelería", ModuleStatus.SUSPENDIDO, v(20), loc("Cra 25 #27-30", 4.0820, -76.1925, 5), 5.0, "2026-06-30"),
    )

    // ── Helpers ─────────────────────────────────────────────────────────

    private fun loc(addr: String, lat: Double, lng: Double, sector: Long) =
        GeoLocation(addr, lat, lng, sector, null)

    private fun module(
        id: Long, code: String, name: String, type: ModuleType, category: String,
        status: ModuleStatus, vendor: Vendor, location: GeoLocation, area: Double,
        validUntil: String?,
    ) = Module(
        id = id, code = code, name = name, type = type, category = category,
        status = status, vendor = vendor, location = location,
        areaM2 = area, validUntil = validUntil,
        lastVerifiedAt = "2025-10-15",
        permits = if (status == ModuleStatus.APROBADO) listOf(
            Permit(id, "Ocupación", "PER-$code", "2025-01-15", validUntil ?: "2026-06-30", true)
        ) else emptyList(),
        documents = listOf(
            Document(id, DocumentType.CEDULA, "Cédula de ${vendor.fullName}", null, "2025-01-10")
        ),
        sanctions = if (status == ModuleStatus.SUSPENDIDO) listOf(
            Sanction(id, SanctionType.SUSPENSION_TEMPORAL, "Incumplimiento de normas sanitarias", "2025-09-01", null, true)
        ) else emptyList(),
    )

    // ── Repository interface ────────────────────────────────────────────

    override suspend fun getModules(
        search: String?, type: ModuleType?, status: ModuleStatus?,
        sectorId: Long?, page: Int, size: Int,
    ): Result<List<Module>> {
        val filtered = modules.filter { m ->
            (search == null || m.name.contains(search, true) || m.code.contains(search, true) || m.vendor.fullName.contains(search, true) || m.vendor.idNumber.contains(search)) &&
                (type == null || m.type == type) &&
                (status == null || m.status == status) &&
                (sectorId == null || m.location.sectorId == sectorId)
        }
        val start = (page - 1) * size
        val paged = filtered.drop(start).take(size)
        return Result.success(paged)
    }

    override suspend fun getModuleById(id: Long): Result<Module> {
        val module = modules.find { it.id == id }
            ?: return Result.failure(NoSuchElementException("Módulo $id no encontrado"))
        return Result.success(module)
    }

    override suspend fun createModule(module: Module): Result<Module> {
        return Result.success(module.copy(id = (modules.maxOfOrNull { it.id } ?: 0) + 1))
    }

    override suspend fun updateModule(module: Module): Result<Module> {
        return Result.success(module)
    }

    override suspend fun uploadPhoto(moduleId: Long, photoPath: String): Result<String> {
        return Result.success("https://mock.tulua.gov.co/photos/module_$moduleId.jpg")
    }

    override suspend fun getModulesForMap(
        bbox: String?, type: ModuleType?, status: ModuleStatus?, sectorId: Long?,
    ): Result<List<Module>> {
        val filtered = modules.filter { m ->
            (type == null || m.type == type) &&
                (status == null || m.status == status) &&
                (sectorId == null || m.location.sectorId == sectorId)
        }
        return Result.success(filtered)
    }

    override suspend fun exportModules(format: String): Result<String> {
        return Result.success("/mock/export/modules.$format")
    }
}
