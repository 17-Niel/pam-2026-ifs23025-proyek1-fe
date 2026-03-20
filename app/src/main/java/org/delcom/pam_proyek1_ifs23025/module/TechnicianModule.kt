package org.delcom.pam_proyek1_ifs23025.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
// Pastikan import mengarah ke package events.service yang baru
import org.delcom.pam_proyek1_ifs23025.network.technicians.service.TechnicianAppContainer
import org.delcom.pam_proyek1_ifs23025.network.technicians.service.ITechnicianAppContainer
import org.delcom.pam_proyek1_ifs23025.network.technicians.service.ITechnicianRepository

@Module
@InstallIn(SingletonComponent::class)
object TechnicianModule { // Ubah nama dari TodoModule menjadi EventModule

    @Provides
    // Ubah nama fungsi dari providePlantContainer menjadi provideTechnicianContainer
    fun provideTechnicianContainer(): ITechnicianAppContainer {
        return TechnicianAppContainer()
    }

    @Provides
    // Ubah nama fungsi dari providePlantRepository menjadi provideEventRepository
    fun provideTechnicianRepository(container: ITechnicianAppContainer): ITechnicianRepository {
        return container.repository
    }
}