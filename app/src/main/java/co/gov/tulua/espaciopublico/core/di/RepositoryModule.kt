package co.gov.tulua.espaciopublico.core.di

import co.gov.tulua.espaciopublico.data.mock.MockAuthRepository
import co.gov.tulua.espaciopublico.data.mock.MockDashboardRepository
import co.gov.tulua.espaciopublico.data.mock.MockEventRepository
import co.gov.tulua.espaciopublico.data.mock.MockModuleRepository
import co.gov.tulua.espaciopublico.data.mock.MockRequestRepository
import co.gov.tulua.espaciopublico.data.mock.MockSectorRepository
import co.gov.tulua.espaciopublico.data.mock.MockVendorRepository
import co.gov.tulua.espaciopublico.domain.repository.AuthRepository
import co.gov.tulua.espaciopublico.domain.repository.DashboardRepository
import co.gov.tulua.espaciopublico.domain.repository.EventRepository
import co.gov.tulua.espaciopublico.domain.repository.ModuleRepository
import co.gov.tulua.espaciopublico.domain.repository.RequestRepository
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

    @Binds
    @Singleton
    abstract fun bindEventRepository(impl: MockEventRepository): EventRepository

    @Binds
    @Singleton
    abstract fun bindRequestRepository(impl: MockRequestRepository): RequestRepository

    @Binds
    @Singleton
    abstract fun bindDashboardRepository(impl: MockDashboardRepository): DashboardRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: MockAuthRepository): AuthRepository
}
