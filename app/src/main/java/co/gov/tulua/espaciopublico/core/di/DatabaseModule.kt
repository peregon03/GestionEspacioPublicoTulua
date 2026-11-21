package co.gov.tulua.espaciopublico.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import co.gov.tulua.espaciopublico.data.local.AppDatabase
import co.gov.tulua.espaciopublico.data.local.dao.EventDao
import co.gov.tulua.espaciopublico.data.local.dao.ModuleDao
import co.gov.tulua.espaciopublico.data.local.dao.RequestDao
import co.gov.tulua.espaciopublico.data.local.dao.SectorDao
import co.gov.tulua.espaciopublico.data.local.dao.SpotAssignmentDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "tulua_espacio_publico.db",
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideModuleDao(db: AppDatabase): ModuleDao = db.moduleDao()

    @Provides
    fun provideSectorDao(db: AppDatabase): SectorDao = db.sectorDao()

    @Provides
    fun provideEventDao(db: AppDatabase): EventDao = db.eventDao()

    @Provides
    fun provideRequestDao(db: AppDatabase): RequestDao = db.requestDao()

    @Provides
    fun provideSpotAssignmentDao(db: AppDatabase): SpotAssignmentDao = db.spotAssignmentDao()

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("tulua_session")
        }
}
