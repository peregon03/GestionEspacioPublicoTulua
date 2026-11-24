package co.gov.tulua.espaciopublico.data.mappers

import co.gov.tulua.espaciopublico.data.remote.dto.DashboardMetricsDto
import co.gov.tulua.espaciopublico.data.remote.dto.DocumentDto
import co.gov.tulua.espaciopublico.data.remote.dto.EventDto
import co.gov.tulua.espaciopublico.data.remote.dto.EventOccupancyPointDto
import co.gov.tulua.espaciopublico.data.remote.dto.FormalInformalPointDto
import co.gov.tulua.espaciopublico.data.remote.dto.GeoLocationDto
import co.gov.tulua.espaciopublico.data.remote.dto.LoginRequestDto
import co.gov.tulua.espaciopublico.data.remote.dto.LoginResponseDto
import co.gov.tulua.espaciopublico.data.remote.dto.ModuleDto
import co.gov.tulua.espaciopublico.data.remote.dto.PermitDto
import co.gov.tulua.espaciopublico.data.remote.dto.ReportDataDto
import co.gov.tulua.espaciopublico.data.remote.dto.RequestDto
import co.gov.tulua.espaciopublico.data.remote.dto.SanctionDto
import co.gov.tulua.espaciopublico.data.remote.dto.SectorDto
import co.gov.tulua.espaciopublico.data.remote.dto.SpotAssignmentDto
import co.gov.tulua.espaciopublico.data.remote.dto.UserDto
import co.gov.tulua.espaciopublico.data.remote.dto.VendorDto
import co.gov.tulua.espaciopublico.domain.model.AuthCredentials
import co.gov.tulua.espaciopublico.domain.model.AuthResponse
import co.gov.tulua.espaciopublico.domain.model.DashboardMetrics
import co.gov.tulua.espaciopublico.domain.model.Document
import co.gov.tulua.espaciopublico.domain.model.DocumentType
import co.gov.tulua.espaciopublico.domain.model.Event
import co.gov.tulua.espaciopublico.domain.model.EventOccupancyPoint
import co.gov.tulua.espaciopublico.domain.model.EventStatus
import co.gov.tulua.espaciopublico.domain.model.FormalInformalPoint
import co.gov.tulua.espaciopublico.domain.model.GeoLocation
import co.gov.tulua.espaciopublico.domain.model.Module
import co.gov.tulua.espaciopublico.domain.model.ModuleStatus
import co.gov.tulua.espaciopublico.domain.model.ModuleType
import co.gov.tulua.espaciopublico.domain.model.Permit
import co.gov.tulua.espaciopublico.domain.model.ReportData
import co.gov.tulua.espaciopublico.domain.model.Request
import co.gov.tulua.espaciopublico.domain.model.RequestStatus
import co.gov.tulua.espaciopublico.domain.model.RequestType
import co.gov.tulua.espaciopublico.domain.model.Sanction
import co.gov.tulua.espaciopublico.domain.model.SanctionType
import co.gov.tulua.espaciopublico.domain.model.SaturationLevel
import co.gov.tulua.espaciopublico.domain.model.Sector
import co.gov.tulua.espaciopublico.domain.model.SpotAssignment
import co.gov.tulua.espaciopublico.domain.model.User
import co.gov.tulua.espaciopublico.domain.model.UserRole
import co.gov.tulua.espaciopublico.domain.model.Vendor

// ── Helpers ──────────────────────────────────────────────────────────────────

private inline fun <reified T : Enum<T>> safeEnum(value: String, default: T): T =
    runCatching { enumValueOf<T>(value) }.getOrDefault(default)

// ── GeoLocation ──────────────────────────────────────────────────────────────

fun GeoLocationDto.toDomain(): GeoLocation = GeoLocation(address, latitude, longitude, sectorId, zone)
fun GeoLocation.toDto(): GeoLocationDto = GeoLocationDto(address, latitude, longitude, sectorId, zone)

// ── Vendor ───────────────────────────────────────────────────────────────────

fun VendorDto.toDomain(): Vendor = Vendor(id, fullName, idNumber, phone, email, association)
fun Vendor.toDto(): VendorDto = VendorDto(id, fullName, idNumber, phone, email, association)

// ── Permit ───────────────────────────────────────────────────────────────────

fun PermitDto.toDomain(): Permit = Permit(id, type, number, issuedAt, expiresAt, isActive)
fun Permit.toDto(): PermitDto = PermitDto(id, type, number, issuedAt, expiresAt, isActive)

// ── Document ─────────────────────────────────────────────────────────────────

fun DocumentDto.toDomain(): Document = Document(id, safeEnum(type, DocumentType.OTRO), name, url, uploadedAt)
fun Document.toDto(): DocumentDto = DocumentDto(id, type.name, name, url, uploadedAt)

// ── Sanction ─────────────────────────────────────────────────────────────────

fun SanctionDto.toDomain(): Sanction = Sanction(id, safeEnum(type, SanctionType.AMONESTACION), description, issuedAt, resolvedAt, isActive)
fun Sanction.toDto(): SanctionDto = SanctionDto(id, type.name, description, issuedAt, resolvedAt, isActive)

// ── Module ───────────────────────────────────────────────────────────────────

fun ModuleDto.toDomain(): Module = Module(
    id = id, code = code, name = name,
    type = safeEnum(type, ModuleType.INFORMAL),
    category = category,
    status = safeEnum(status, ModuleStatus.EN_REVISION),
    vendor = vendor.toDomain(),
    location = location.toDomain(),
    areaM2 = areaM2, widthM = widthM, lengthM = lengthM,
    photoUrl = photoUrl, validUntil = validUntil,
    lastVerifiedAt = lastVerifiedAt, observations = observations,
    permits = permits.map { it.toDomain() },
    documents = documents.map { it.toDomain() },
    sanctions = sanctions.map { it.toDomain() },
)

fun Module.toDto(): ModuleDto = ModuleDto(
    id = id, code = code, name = name,
    type = type.name, category = category, status = status.name,
    vendor = vendor.toDto(), location = location.toDto(),
    areaM2 = areaM2, widthM = widthM, lengthM = lengthM,
    photoUrl = photoUrl, validUntil = validUntil,
    lastVerifiedAt = lastVerifiedAt, observations = observations,
    permits = permits.map { it.toDto() },
    documents = documents.map { it.toDto() },
    sanctions = sanctions.map { it.toDto() },
)

// ── Sector ───────────────────────────────────────────────────────────────────

fun SectorDto.toDomain(): Sector = Sector(id, name, description, moduleCount, occupancyPercent, safeEnum(saturationLevel, SaturationLevel.BAJA))
fun Sector.toDto(): SectorDto = SectorDto(id, name, description, moduleCount, occupancyPercent, saturationLevel.name)

// ── Event ────────────────────────────────────────────────────────────────────

fun EventDto.toDomain(): Event = Event(
    id, code, name, safeEnum(status, EventStatus.PROGRAMADA),
    startDate, endDate, location.toDomain(), organizer,
    comuna, totalSpots, assignedSpots, zoneId,
)

fun Event.toDto(): EventDto = EventDto(
    id, code, name, status.name, startDate, endDate,
    location.toDto(), organizer, comuna, totalSpots, assignedSpots, zoneId,
)

// ── SpotAssignment ───────────────────────────────────────────────────────────

fun SpotAssignmentDto.toDomain(): SpotAssignment = SpotAssignment(id, eventId, spotLabel, vendorId, vendorName, moduleId)
fun SpotAssignment.toDto(): SpotAssignmentDto = SpotAssignmentDto(id, eventId, spotLabel, vendorId, vendorName, moduleId)

// ── Request ──────────────────────────────────────────────────────────────────

fun RequestDto.toDomain(): Request = Request(
    id = id, code = code,
    type = safeEnum(type, RequestType.PERMISO_OCUPACION),
    title = title, applicant = applicant, applicantId = applicantId,
    location = location?.toDomain(),
    createdAt = createdAt, dueAt = dueAt,
    status = safeEnum(status, RequestStatus.PENDIENTE),
    rejectionReason = rejectionReason, attachments = attachments,
)

fun Request.toDto(): RequestDto = RequestDto(
    id = id, code = code, type = type.name,
    title = title, applicant = applicant, applicantId = applicantId,
    location = location?.toDto(),
    createdAt = createdAt, dueAt = dueAt,
    status = status.name, rejectionReason = rejectionReason, attachments = attachments,
)

// ── User / Auth ──────────────────────────────────────────────────────────────

fun UserDto.toDomain(): User = User(id, name, email, safeEnum(role, UserRole.COMERCIANTE))
fun User.toDto(): UserDto = UserDto(id, name, email, role.name)

fun LoginResponseDto.toDomain(): AuthResponse = AuthResponse(user.toDomain(), token, refreshToken)
fun AuthCredentials.toDto(): LoginRequestDto = LoginRequestDto(identifier, password, role.name)

// ── DashboardMetrics ─────────────────────────────────────────────────────────

fun DashboardMetricsDto.toDomain(): DashboardMetrics = DashboardMetrics(
    totalModules, weeklyDelta, formalCount, informalCount,
    occupiedToday, occupancyPercent, pendingRequests,
    sectors.map { it.toDomain() }, upcomingEvents.map { it.toDomain() },
)

fun DashboardMetrics.toDto(): DashboardMetricsDto = DashboardMetricsDto(
    totalModules, weeklyDelta, formalCount, informalCount,
    occupiedToday, occupancyPercent, pendingRequests,
    sectors.map { it.toDto() }, upcomingEvents.map { it.toDto() },
)

// ── ReportData ───────────────────────────────────────────────────────────────

fun FormalInformalPointDto.toDomain(): FormalInformalPoint = FormalInformalPoint(month, formal, informal)
fun FormalInformalPoint.toDto(): FormalInformalPointDto = FormalInformalPointDto(month, formal, informal)

fun EventOccupancyPointDto.toDomain(): EventOccupancyPoint = EventOccupancyPoint(eventName, totalSpots, assignedSpots)
fun EventOccupancyPoint.toDto(): EventOccupancyPointDto = EventOccupancyPointDto(eventName, totalSpots, assignedSpots)

fun ReportDataDto.toDomain(): ReportData = ReportData(
    period, sectorId, modulesBySector,
    formalVsInformalSeries.map { it.toDomain() },
    eventOccupancy.map { it.toDomain() },
    requestsByStatus,
)

fun ReportData.toDto(): ReportDataDto = ReportDataDto(
    period, sectorId, modulesBySector,
    formalVsInformalSeries.map { it.toDto() },
    eventOccupancy.map { it.toDto() },
    requestsByStatus,
)
