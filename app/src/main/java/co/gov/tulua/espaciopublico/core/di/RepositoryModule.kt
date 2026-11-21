package co.gov.tulua.espaciopublico.core.di

import co.gov.tulua.espaciopublico.data.mock.MockModuleRepository
import co.gov.tulua.espaciopublico.data.mock.MockSectorRepository
import co.gov.tulua.espaciopublico.data.mock.MockVendorRepository
import co.gov.tulua.espaciopublico.domain.repository.ModuleRepository
import co.gov.tulua.espaciopublico.domain.repository.SectorRepository
import co.gov.tulua.espaciopublico.domain.repository.VendorRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSectorRepository(impl: MockSectorRepository): SectorRepository

    @Binds
    @Singleton
    abstract fun bindVendorRepository(impl: MockVendorRepository): VendorRepository

    @Binds
    @Singleton
    abstract fun bindModuleRepository(impl: MockModuleRepository): ModuleRepository
}
