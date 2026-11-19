package co.gov.tulua.espaciopublico.ui.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation destinations using Navigation Compose serializable routes.
 *
 * Sealed hierarchy groups destinations by area.
 * Each object / data class is annotated with @Serializable so Navigation Compose
 * can encode / decode route arguments automatically.
 */
sealed interface AppDestination {

    // ─── Bottom-nav root destinations ─────────────────────────────────────────
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

    // ─── Auth destinations ────────────────────────────────────────────────────
    @Serializable
    data object Login : AppDestination

    @Serializable
    data object RecoverPassword : AppDestination

    // ─── Detail destinations (future phases) ─────────────────────────────────
    @Serializable
    data class VendorDetail(val vendorId: String) : AppDestination

    @Serializable
    data class EventDetail(val eventId: String) : AppDestination

    @Serializable
    data class RequestDetail(val requestId: String) : AppDestination
}
