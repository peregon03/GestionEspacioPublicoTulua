package co.gov.tulua.espaciopublico.data.mock

import co.gov.tulua.espaciopublico.domain.model.Vendor
import co.gov.tulua.espaciopublico.domain.repository.VendorRepository
import javax.inject.Inject

class MockVendorRepository @Inject constructor() : VendorRepository {

    internal val vendors = listOf(
        Vendor(1L, "María Elena Gómez", "31.987.654", "3156789012", "maria.gomez@email.com", "Asovencedores"),
        Vendor(2L, "Carlos Alberto Muñoz", "14.325.678", "3209876543", null, null),
        Vendor(3L, "Luz Dary Ospina", "66.543.210", "3178901234", null, "Asoventas Tuluá"),
        Vendor(4L, "Jorge Iván Castaño", "16.789.012", "3145678901", "jorge.castano@email.com", null),
        Vendor(5L, "Ana Milena Ríos", "31.456.789", "3167890123", null, "Asovencedores"),
        Vendor(6L, "Pedro Nel García", "14.678.901", "3198765432", null, null),
        Vendor(7L, "Sandra Patricia Mejía", "66.890.123", "3121234567", "sandra.mejia@email.com", "Asoventas Tuluá"),
        Vendor(8L, "Diego Fernando López", "16.012.345", "3132345678", null, null),
        Vendor(9L, "Gloria Esperanza Vargas", "31.234.567", "3143456789", null, "Asovencedores"),
        Vendor(10L, "Héctor Fabio Ramírez", "14.456.789", "3154567890", "hector.ramirez@email.com", null),
        Vendor(11L, "Claudia Marcela Torres", "66.678.901", "3165678901", null, "Asoventas Tuluá"),
        Vendor(12L, "Luis Eduardo Sánchez", "16.890.123", "3176789012", null, null),
        Vendor(13L, "Martha Lucía Henao", "31.012.345", "3187890123", null, null),
        Vendor(14L, "Fabio Andrés Quintero", "14.234.567", "3198901234", "fabio.quintero@email.com", "Asovencedores"),
        Vendor(15L, "Yolanda del Carmen Ortiz", "66.456.789", "3109012345", null, null),
        Vendor(16L, "Rubén Darío Agudelo", "16.678.901", "3120123456", null, "Asoventas Tuluá"),
        Vendor(17L, "Patricia Elena Salazar", "31.890.123", "3131234567", null, null),
        Vendor(18L, "Óscar Javier Montoya", "14.012.345", "3142345678", "oscar.montoya@email.com", null),
        Vendor(19L, "Adriana María Bedoya", "66.234.567", "3153456789", null, "Asovencedores"),
        Vendor(20L, "William de Jesús Cardona", "16.456.789", "3164567890", null, null),
    )

    override suspend fun getVendorById(id: Long): Result<Vendor> {
        val vendor = vendors.find { it.id == id }
            ?: return Result.failure(NoSuchElementException("Comerciante $id no encontrado"))
        return Result.success(vendor)
    }

    override suspend fun searchVendors(query: String): Result<List<Vendor>> {
        val q = query.lowercase()
        val results = vendors.filter {
            it.fullName.lowercase().contains(q) ||
                it.idNumber.contains(q) ||
                it.phone?.contains(q) == true
        }
        return Result.success(results)
    }
}
