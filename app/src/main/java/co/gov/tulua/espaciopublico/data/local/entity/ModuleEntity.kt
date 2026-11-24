package co.gov.tulua.espaciopublico.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "modules")
data class ModuleEntity(
    @PrimaryKey val id: Long,
    val code: String,
    val name: String,
    val type: String,
    val category: String,
    val status: String,
    val vendorId: Long,
    val vendorFullName: String,
    val vendorIdNumber: String,
    val vendorPhone: String?,
    val vendorEmail: String?,
    val vendorAssociation: String?,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val sectorId: Long?,
    val zone: String?,
    val areaM2: Double,
    val widthM: Double?,
    val lengthM: Double?,
    val photoUrl: String?,
    val validUntil: String?,
    val lastVerifiedAt: String?,
    val observations: String?,
    val permitsJson: String,
    val documentsJson: String,
    val sanctionsJson: String,
)
