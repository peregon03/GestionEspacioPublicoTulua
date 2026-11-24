package co.gov.tulua.espaciopublico.data.mock

import co.gov.tulua.espaciopublico.domain.model.GeoLocation
import co.gov.tulua.espaciopublico.domain.model.Request
import co.gov.tulua.espaciopublico.domain.model.RequestStatus
import co.gov.tulua.espaciopublico.domain.model.RequestType
import co.gov.tulua.espaciopublico.domain.repository.RequestRepository
import javax.inject.Inject

class MockRequestRepository @Inject constructor() : RequestRepository {

    private val requests = mutableListOf(
        // ── Pendientes (6) ──────────────────────────────────────────
        Request(
            id = 1L, code = "RAD-2024-0512", type = RequestType.PERMISO_OCUPACION,
            title = "Permiso para venta de alimentos en Carrera 25",
            applicant = "María Elena Gómez", applicantId = "31.987.654",
            location = GeoLocation("Cra 25 #25-10", 4.0855, -76.1958, 1),
            createdAt = "2026-11-10T08:30:00", dueAt = "2026-11-25T23:59:59",
            status = RequestStatus.PENDIENTE,
        ),
        Request(
            id = 2L, code = "RAD-2024-0513", type = RequestType.RENOVACION,
            title = "Renovación permiso caseta de jugos",
            applicant = "Carlos Alberto Muñoz", applicantId = "14.325.678",
            location = GeoLocation("Cra 25 #25-32", 4.0857, -76.1960, 1),
            createdAt = "2026-11-12T10:15:00", dueAt = "2026-11-22T23:59:59",
            status = RequestStatus.PENDIENTE,
        ),
        Request(
            id = 3L, code = "RAD-2024-0514", type = RequestType.NUEVO_MODULO,
            title = "Solicitud nuevo módulo de artesanías",
            applicant = "Luz Dary Ospina", applicantId = "66.543.210",
            location = GeoLocation("Parque Boyacá, Cra 26", 4.0840, -76.1945, 3),
            createdAt = "2026-11-14T14:00:00", dueAt = "2026-11-29T23:59:59",
            status = RequestStatus.PENDIENTE,
        ),
        Request(
            id = 4L, code = "RAD-2024-0515", type = RequestType.TRASLADO,
            title = "Traslado de puesto de verduras a nuevo sector",
            applicant = "Gloria Esperanza Vargas", applicantId = "31.234.567",
            location = GeoLocation("Cll 26 #25-05", 4.0848, -76.1952, 2),
            createdAt = "2026-11-15T09:00:00", dueAt = "2026-11-30T23:59:59",
            status = RequestStatus.PENDIENTE,
        ),
        Request(
            id = 5L, code = "RAD-2024-0516", type = RequestType.EVENTO_ESPECIAL,
            title = "Permiso para bazar navideño en Parque Céspedes",
            applicant = "Sandra Patricia Mejía", applicantId = "66.890.123",
            location = GeoLocation("Parque Céspedes, Cll 27", 4.0832, -76.1938, 4),
            createdAt = "2026-11-16T11:30:00", dueAt = "2026-12-01T23:59:59",
            status = RequestStatus.PENDIENTE,
        ),
        Request(
            id = 6L, code = "RAD-2024-0517", type = RequestType.PERMISO_OCUPACION,
            title = "Permiso venta de café en la Plaza de Mercado",
            applicant = "Pedro Nel García", applicantId = "14.678.901",
            location = GeoLocation("Plaza de Mercado Central", 4.0825, -76.1930, 5),
            createdAt = "2026-11-18T08:00:00", dueAt = "2026-12-03T23:59:59",
            status = RequestStatus.PENDIENTE,
        ),

        // ── Aprobadas (4) ───────────────────────────────────────────
        Request(
            id = 7L, code = "RAD-2024-0498", type = RequestType.PERMISO_OCUPACION,
            title = "Permiso puesto de frutas esquina Carrera 25",
            applicant = "Jorge Iván Castaño", applicantId = "16.789.012",
            location = GeoLocation("Cra 25 #26-18", 4.0862, -76.1963, 1),
            createdAt = "2026-10-20T09:00:00", dueAt = "2026-11-05T23:59:59",
            status = RequestStatus.APROBADA,
        ),
        Request(
            id = 8L, code = "RAD-2024-0501", type = RequestType.RENOVACION,
            title = "Renovación permiso granos y abarrotes",
            applicant = "Martha Lucía Henao", applicantId = "31.012.345",
            location = GeoLocation("Cll 26 #25-35", 4.0852, -76.1956, 2),
            createdAt = "2026-10-25T14:30:00", dueAt = "2026-11-10T23:59:59",
            status = RequestStatus.APROBADA,
        ),
        Request(
            id = 9L, code = "RAD-2024-0505", type = RequestType.NUEVO_MODULO,
            title = "Nuevo módulo de heladería en Parque Boyacá",
            applicant = "Patricia Elena Salazar", applicantId = "31.890.123",
            location = GeoLocation("Cra 26 #26-01", 4.0840, -76.1945, 3),
            createdAt = "2026-10-28T10:00:00", dueAt = "2026-11-12T23:59:59",
            status = RequestStatus.APROBADA,
        ),
        Request(
            id = 10L, code = "RAD-2024-0508", type = RequestType.EVENTO_ESPECIAL,
            title = "Feria agropecuaria en la Galería",
            applicant = "Fabio Andrés Quintero", applicantId = "14.234.567",
            location = GeoLocation("Galería, Cll 26 #25-05", 4.0848, -76.1952, 2),
            createdAt = "2026-09-15T16:00:00", dueAt = "2026-09-30T23:59:59",
            status = RequestStatus.APROBADA,
        ),

        // ── Rechazadas (4) ──────────────────────────────────────────
        Request(
            id = 11L, code = "RAD-2024-0490", type = RequestType.PERMISO_OCUPACION,
            title = "Permiso venta ambulante zona escolar",
            applicant = "Ana Milena Ríos", applicantId = "31.456.789",
            location = GeoLocation("Cra 25 #26-40", 4.0864, -76.1965, 1),
            createdAt = "2026-10-05T08:00:00", dueAt = "2026-10-20T23:59:59",
            status = RequestStatus.RECHAZADA,
            rejectionReason = "La ubicación solicitada se encuentra en zona de exclusión escolar según el Decreto 045 de 2024.",
        ),
        Request(
            id = 12L, code = "RAD-2024-0493", type = RequestType.NUEVO_MODULO,
            title = "Módulo de fotografía en Parque Boyacá",
            applicant = "Diego Fernando López", applicantId = "16.012.345",
            location = GeoLocation("Cra 27 #26-20", 4.0837, -76.1942, 3),
            createdAt = "2026-10-10T11:00:00", dueAt = "2026-10-25T23:59:59",
            status = RequestStatus.RECHAZADA,
            rejectionReason = "El sector Parque Boyacá se encuentra en nivel de saturación media. No se autorizan nuevos módulos hasta reducir ocupación al 50%.",
        ),
        Request(
            id = 13L, code = "RAD-2024-0496", type = RequestType.TRASLADO,
            title = "Traslado puesto de mechas a Galería",
            applicant = "Yolanda del Carmen Ortiz", applicantId = "66.456.789",
            location = GeoLocation("Cra 25 #27-08", 4.0824, -76.1929, 5),
            createdAt = "2026-10-12T15:00:00", dueAt = "2026-10-27T23:59:59",
            status = RequestStatus.RECHAZADA,
            rejectionReason = "El sector destino (Galería) tiene saturación crítica del 92%. No se permiten traslados entrantes.",
        ),
        Request(
            id = 14L, code = "RAD-2024-0499", type = RequestType.PERMISO_OCUPACION,
            title = "Permiso venta de licor en espacio público",
            applicant = "Rubén Darío Agudelo", applicantId = "16.678.901",
            location = GeoLocation("Parque Céspedes, Cll 28", 4.0830, -76.1936, 4),
            createdAt = "2026-10-15T09:30:00", dueAt = "2026-10-30T23:59:59",
            status = RequestStatus.RECHAZADA,
            rejectionReason = "No se permite la venta de bebidas alcohólicas en zonas de esparcimiento público según la Resolución 123 de 2023.",
        ),
    )

    override suspend fun getRequests(
        status: RequestStatus?, search: String?, page: Int, size: Int,
    ): Result<List<Request>> {
        val filtered = requests.filter { r ->
            (status == null || r.status == status) &&
                (search == null || r.code.contains(search, true) || r.applicant.contains(search, true) || r.applicantId.contains(search))
        }
        val start = (page - 1) * size
        return Result.success(filtered.drop(start).take(size))
    }

    override suspend fun getRequestById(id: Long): Result<Request> {
        val request = requests.find { it.id == id }
            ?: return Result.failure(NoSuchElementException("Solicitud $id no encontrada"))
        return Result.success(request)
    }

    override suspend fun approveRequest(requestId: Long): Result<Request> {
        val index = requests.indexOfFirst { it.id == requestId }
        if (index == -1) return Result.failure(NoSuchElementException("Solicitud $requestId no encontrada"))
        val updated = requests[index].copy(status = RequestStatus.APROBADA)
        requests[index] = updated
        return Result.success(updated)
    }

    override suspend fun rejectRequest(requestId: Long, reason: String): Result<Request> {
        val index = requests.indexOfFirst { it.id == requestId }
        if (index == -1) return Result.failure(NoSuchElementException("Solicitud $requestId no encontrada"))
        val updated = requests[index].copy(status = RequestStatus.RECHAZADA, rejectionReason = reason)
        requests[index] = updated
        return Result.success(updated)
    }
}
