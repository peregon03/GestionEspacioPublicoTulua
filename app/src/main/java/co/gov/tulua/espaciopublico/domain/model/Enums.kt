package co.gov.tulua.espaciopublico.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class ModuleType { FORMAL, INFORMAL, FERIA }

@Serializable
enum class ModuleStatus { APROBADO, EN_REVISION, RECHAZADO, SUSPENDIDO }

@Serializable
enum class EventStatus { PROGRAMADA, EN_CURSO, FINALIZADA }

@Serializable
enum class RequestStatus { PENDIENTE, APROBADA, RECHAZADA }

@Serializable
enum class RequestType {
    PERMISO_OCUPACION,
    RENOVACION,
    TRASLADO,
    NUEVO_MODULO,
    EVENTO_ESPECIAL
}

@Serializable
enum class SaturationLevel { BAJA, MEDIA, ALTA, CRITICA }

@Serializable
enum class UserRole { ADMINISTRADOR, INSPECTOR, COMERCIANTE }

@Serializable
enum class DocumentType {
    CEDULA,
    RUT,
    CAMARA_COMERCIO,
    PERMISO,
    CONCEPTO_USO_SUELO,
    OTRO
}

@Serializable
enum class SanctionType {
    AMONESTACION,
    MULTA,
    SUSPENSION_TEMPORAL,
    RETIRO_DEFINITIVO
}
