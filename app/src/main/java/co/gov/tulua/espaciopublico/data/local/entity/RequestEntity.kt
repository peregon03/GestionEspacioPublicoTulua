package co.gov.tulua.espaciopublico.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "requests")
data class RequestEntity(
    @PrimaryKey val id: Long,
    val code: String,
    val type: String,
    val title: String,
    val applicant: String,
    val applicantId: String,
    val address: String?,
    val latitude: Double?,
    val longitude: Double?,
    val locationSectorId: Long?,
    val locationZone: String?,
    val createdAt: String,
    val dueAt: String?,
    val status: String,
    val rejectionReason: String?,
    val attachmentsJson: String,
)
