package co.gov.tulua.espaciopublico.data.mappers

import co.gov.tulua.espaciopublico.data.local.entity.*
import co.gov.tulua.espaciopublico.domain.model.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

// ── Helpers ─────────────────────────────────────────────────────────────────

private inline fun <reified T : Enum<T>> safeEnum(value: String, default: T): T =
    try { enumValueOf<T>(value) } catch (_: Exception) { default }

private inline fun <reified T> decodeList(raw: String): List<T> =
    try { json.decodeFromString(raw) } catch (_: Exception) { emptyList() }

// ── Module ──────────────────────────────────────────────────────────────────

fun ModuleEntity.toDomain() = Module(
    id = id, code = code, name = name,
    type = safeEnum(type, ModuleType.INFORMAL),
    category = category,
    status = safeEnum(status, ModuleStatus.EN_REVISION),
    vendor = Vendor(vendorId, vendorFullName, vendorIdNumber, vendorPhone, vendorEmail, vendorAssociation),
    location = GeoLocation(address, latitude, longitude, sectorId, zone),
    areaM2 = areaM2, widthM = widthM, lengthM = lengthM,
    photoUrl = photoUrl, validUntil = validUntil,
    lastVerifiedAt = lastVerifiedAt, observations = observations,
    permits = decodeList(permitsJson),
    documents = decodeList(documentsJson),
    sanctions = decodeList(sanctionsJson),
)

fun Module.toEntity() = ModuleEntity(
    id = id, code = code, name = name,
    type = type.name, category = category, status = status.name,
    vendorId = vendor.id, vendorFullName = vendor.fullName, vendorIdNumber = vendor.idNumber,
    vendorPhone = vendor.phone, vendorEmail = vendor.email, vendorAssociation = vendor.association,
    address = location.address, latitude = location.latitude, longitude = location.longitude,
    sectorId = location.sectorId, zone = location.zone,
    areaM2 = areaM2, widthM = widthM, lengthM = lengthM,
    photoUrl = photoUrl, validUntil = validUntil,
    lastVerifiedAt = lastVerifiedAt, observations = observations,
    permitsJson = json.encodeToString(permits),
    documentsJson = json.encodeToString(documents),
    sanctionsJson = json.encodeToString(sanctions),
)

// ── Sector ──────────────────────────────────────────────────────────────────

fun SectorEntity.toDomain() = Sector(
    id, name, description, moduleCount, occupancyPercent,
    safeEnum(saturationLevel, SaturationLevel.BAJA),
)

fun Sector.toEntity() = SectorEntity(
    id, name, description, moduleCount, occupancyPercent, saturationLevel.name,
)

// ── Event ───────────────────────────────────────────────────────────────────

fun EventEntity.toDomain() = Event(
    id = id, code = code, name = name,
    status = safeEnum(status, EventStatus.PROGRAMADA),
    startDate = startDate, endDate = endDate,
    location = GeoLocation(address, latitude, longitude, locationSectorId, locationZone),
    organizer = organizer, comuna = comuna,
    totalSpots = totalSpots, assignedSpots = assignedSpots, zoneId = zoneId,
)

fun Event.toEntity() = EventEntity(
    id = id, code = code, name = name, status = status.name,
    startDate = startDate, endDate = endDate,
    address = location.address, latitude = location.latitude, longitude = location.longitude,
    locationSectorId = location.sectorId, locationZone = location.zone,
    organizer = organizer, comuna = comuna,
    totalSpots = totalSpots, assignedSpots = assignedSpots, zoneId = zoneId,
)

// ── Request ─────────────────────────────────────────────────────────────────

fun RequestEntity.toDomain() = Request(
    id = id, code = code,
    type = safeEnum(type, RequestType.PERMISO_OCUPACION),
    title = title, applicant = applicant, applicantId = applicantId,
    location = if (address != null && latitude != null && longitude != null)
        GeoLocation(address, latitude, longitude, locationSectorId, locationZone) else null,
    createdAt = createdAt, dueAt = dueAt,
    status = safeEnum(status, RequestStatus.PENDIENTE),
    rejectionReason = rejectionReason,
    attachments = decodeList(attachmentsJson),
)

fun Request.toEntity() = RequestEntity(
    id = id, code = code, type = type.name,
    title = title, applicant = applicant, applicantId = applicantId,
    address = location?.address, latitude = location?.latitude, longitude = location?.longitude,
    locationSectorId = location?.sectorId, locationZone = location?.zone,
    createdAt = createdAt, dueAt = dueAt,
    status = status.name, rejectionReason = rejectionReason,
    attachmentsJson = json.encodeToString(attachments),
)

// ── SpotAssignment ──────────────────────────────────────────────────────────

fun SpotAssignmentEntity.toDomain() = SpotAssignment(id, eventId, spotLabel, vendorId, vendorName, moduleId)

fun SpotAssignment.toEntity() = SpotAssignmentEntity(id, eventId, spotLabel, vendorId, vendorName, moduleId)
