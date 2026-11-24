package co.gov.tulua.espaciopublico.data.remote.dto

import kotlinx.serialization.Serializable

// Todos los DTOs reflejan el contrato de la API definido en PLAN_APP_MOVIL_TULUA.md sección 6.
// Los nombres de campo usan camelCase para coincidir con el formato JSON esperado.

@Serializable
data class LoginRequestDto(
    val identifier: String,
    val password: String,
    val role: String,
)

@Serializable
data class LoginResponseDto(
    val user: UserDto,
    val token: String,
    val refreshToken: String,
)

@Serializable
data class UserDto(
    val id: Long,
    val name: String,
    val email: String,
    val role: String,
)

@Serializable
data class VendorDto(
    val id: Long,
    val fullName: String,
    val idNumber: String,
    val phone: String? = null,
    val email: String? = null,
    val association: String? = null,
)

@Serializable
data class GeoLocationDto(
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val sectorId: Long? = null,
    val zone: String? = null,
)

@Serializable
data class PermitDto(
    val id: Long,
    val type: String,
    val number: String,
    val issuedAt: String,
    val expiresAt: String,
    val isActive: Boolean,
)

@Serializable
data class DocumentDto(
    val id: Long,
    val type: String,
    val name: String,
    val url: String? = null,
    val uploadedAt: String,
)

@Serializable
data class SanctionDto(
    val id: Long,
    val type: String,
    val description: String,
    val issuedAt: String,
    val resolvedAt: String? = null,
    val isActive: Boolean,
)

@Serializable
data class ModuleDto(
    val id: Long,
    val code: String,
    val name: String,
    val type: String,
    val category: String,
    val status: String,
    val vendor: VendorDto,
    val location: GeoLocationDto,
    val areaM2: Double,
    val widthM: Double? = null,
    val lengthM: Double? = null,
    val photoUrl: String? = null,
    val validUntil: String? = null,
    val lastVerifiedAt: String? = null,
    val observations: String? = null,
    val permits: List<PermitDto> = emptyList(),
    val documents: List<DocumentDto> = emptyList(),
    val sanctions: List<SanctionDto> = emptyList(),
)

@Serializable
data class SectorDto(
    val id: Long,
    val name: String,
    val description: String? = null,
    val moduleCount: Int,
    val occupancyPercent: Float,
    val saturationLevel: String,
)

@Serializable
data class EventDto(
    val id: Long,
    val code: String,
    val name: String,
    val status: String,
    val startDate: String,
    val endDate: String,
    val location: GeoLocationDto,
    val organizer: String,
    val comuna: String? = null,
    val totalSpots: Int,
    val assignedSpots: Int,
    val zoneId: Long? = null,
)

@Serializable
data class SpotAssignmentDto(
    val id: Long,
    val eventId: Long,
    val spotLabel: String,
    val vendorId: Long? = null,
    val vendorName: String? = null,
    val moduleId: Long? = null,
)

@Serializable
data class RequestDto(
    val id: Long,
    val code: String,
    val type: String,
    val title: String,
    val applicant: String,
    val applicantId: String,
    val location: GeoLocationDto? = null,
    val createdAt: String,
    val dueAt: String? = null,
    val status: String,
    val rejectionReason: String? = null,
    val attachments: List<String> = emptyList(),
)

@Serializable
data class DashboardMetricsDto(
    val totalModules: Int,
    val weeklyDelta: Int,
    val formalCount: Int,
    val informalCount: Int,
    val occupiedToday: Int,
    val occupancyPercent: Float,
    val pendingRequests: Int,
    val sectors: List<SectorDto>,
    val upcomingEvents: List<EventDto>,
)

@Serializable
data class ReportDataDto(
    val period: String,
    val sectorId: Long? = null,
    val modulesBySector: Map<String, Int> = emptyMap(),
    val formalVsInformalSeries: List<FormalInformalPointDto> = emptyList(),
    val eventOccupancy: List<EventOccupancyPointDto> = emptyList(),
    val requestsByStatus: Map<String, Int> = emptyMap(),
)

@Serializable
data class FormalInformalPointDto(
    val month: String,
    val formal: Int,
    val informal: Int,
)

@Serializable
data class EventOccupancyPointDto(
    val eventName: String,
    val totalSpots: Int,
    val assignedSpots: Int,
)

// Wrapper genérico para respuestas paginadas
@Serializable
data class PaginatedResponseDto<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
)

// Respuesta de error genérica de la API
@Serializable
data class ApiErrorDto(
    val code: String,
    val message: String,
)
