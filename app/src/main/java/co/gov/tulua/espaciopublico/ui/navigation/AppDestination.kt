package co.gov.tulua.espaciopublico.ui.navigation

import kotlinx.serialization.Serializable

/**
 * Destinos de navegación type-safe con Navigation Compose + serialización.
 *
 * Jerarquía sellada que agrupa destinos por área.
 * Cada objeto / data class lleva @Serializable para que Navigation Compose
 * pueda codificar/decodificar argumentos automáticamente.
 */
sealed interface AppDestination {

    // ─── Destino de inicio ────────────────────────────────────────────────────
    @Serializable
    data object Splash : AppDestination

    // ─── Destinos del menú inferior ───────────────────────────────────────────
    @Serializable
    data object Dashboard : AppDestination

    @Serializable
    data object Map : AppDestination

    @Serializable
    data object Census : AppDestination

    @Serializable
    data object Events : AppDestination

    @Serializable
    data object Requests : AppDestination

    // ─── Destinos de autenticación ────────────────────────────────────────────
    @Serializable
    data object Login : AppDestination

    @Serializable
    data object RecoverPassword : AppDestination

    // ─── Destinos de detalle ──────────────────────────────────────────────────
    @Serializable
    data class VendorDetail(val vendorId: String) : AppDestination

    @Serializable
    data class EventDetail(val eventId: String) : AppDestination

    @Serializable
    data class RequestDetail(val requestId: String) : AppDestination

    @Serializable
    data class ModuleDetail(val moduleId: Long) : AppDestination

    // ─── Destinos del censo ───────────────────────────────────────────────────
    @Serializable
    data object NewCensus : AppDestination
}
