package co.gov.tulua.espaciopublico.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Request(
    val id: Long,
    val code: String,
    val type: RequestType,
    val title: String,
    val applicant: String,
    val applicantId: String,
    val location: GeoLocation? = null,
    val createdAt: String,
    val dueAt: String? = null,
    val status: RequestStatus,
    val rejectionReason: String? = null,
    val attachments: List<String> = emptyList(),
)
