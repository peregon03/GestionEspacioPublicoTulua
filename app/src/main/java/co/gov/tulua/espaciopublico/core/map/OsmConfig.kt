package co.gov.tulua.espaciopublico.core.map

import android.content.Context
import org.osmdroid.config.Configuration
import java.io.File

/**
 * Configuracion global de osmdroid. Se llama una vez desde [co.gov.tulua.espaciopublico.TuluaApp].
 *
 * - User-Agent: la politica de uso de tiles de OpenStreetMap exige identificar la app.
 * - Cache de tiles en el directorio de cache de la app (no requiere permisos de almacenamiento).
 */
object OsmConfig {

    @Volatile
    private var initialized = false

    fun init(context: Context) {
        if (initialized) return
        val appContext = context.applicationContext
        Configuration.getInstance().apply {
            userAgentValue = appContext.packageName
            val base = File(appContext.cacheDir, "osmdroid")
            osmdroidBasePath = base
            osmdroidTileCache = File(base, "tiles")
            // ~50 MB de cache de tiles para uso offline basico
            tileFileSystemCacheMaxBytes = 50L * 1024 * 1024
            tileFileSystemCacheTrimBytes = 40L * 1024 * 1024
        }
        initialized = true
    }
}
