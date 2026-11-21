package co.gov.tulua.espaciopublico.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sectors")
data class SectorEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val description: String?,
    val moduleCount: Int,
    val occupancyPercent: Float,
    val saturationLevel: String,
)
