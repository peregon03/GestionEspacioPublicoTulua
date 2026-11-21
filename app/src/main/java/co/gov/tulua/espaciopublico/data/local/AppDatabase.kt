package co.gov.tulua.espaciopublico.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import co.gov.tulua.espaciopublico.data.local.converter.Converters
import co.gov.tulua.espaciopublico.data.local.dao.EventDao
import co.gov.tulua.espaciopublico.data.local.dao.ModuleDao
import co.gov.tulua.espaciopublico.data.local.dao.RequestDao
import co.gov.tulua.espaciopublico.data.local.dao.SectorDao
import co.gov.tulua.espaciopublico.data.local.dao.SpotAssignmentDao
import co.gov.tulua.espaciopublico.data.local.entity.EventEntity
import co.gov.tulua.espaciopublico.data.local.entity.ModuleEntity
import co.gov.tulua.espaciopublico.data.local.entity.RequestEntity
import co.gov.tulua.espaciopublico.data.local.entity.SectorEntity
import co.gov.tulua.espaciopublico.data.local.entity.SpotAssignmentEntity

@Database(
    entities = [
        ModuleEntity::class,
        SectorEntity::class,
        EventEntity::class,
        RequestEntity::class,
        SpotAssignmentEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun moduleDao(): ModuleDao
    abstract fun sectorDao(): SectorDao
    abstract fun eventDao(): EventDao
    abstract fun requestDao(): RequestDao
    abstract fun spotAssignmentDao(): SpotAssignmentDao
}
