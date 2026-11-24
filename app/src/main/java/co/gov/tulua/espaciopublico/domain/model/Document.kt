package co.gov.tulua.espaciopublico.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Document(
    val id: Long,
    val type: DocumentType,
    val name: String,
    val url: String? = null,
    val uploadedAt: String,
)
