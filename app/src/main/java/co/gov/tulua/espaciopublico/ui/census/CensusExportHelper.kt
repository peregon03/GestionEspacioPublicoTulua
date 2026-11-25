package co.gov.tulua.espaciopublico.ui.census

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import co.gov.tulua.espaciopublico.domain.model.Module
import java.io.File
import java.io.FileWriter

/**
 * Genera un archivo CSV con los módulos del censo y devuelve su URI compartible vía FileProvider.
 *
 * @param context  Contexto de Android.
 * @param modules  Lista de módulos a exportar.
 * @return URI del archivo listo para compartir con Intent.ACTION_SEND.
 */
fun exportModulesToCsv(context: Context, modules: List<Module>): Uri {
    val archivo = File(context.cacheDir, "censo_modulos_${System.currentTimeMillis()}.csv")

    FileWriter(archivo, Charsets.UTF_8).use { writer ->
        // Cabecera
        writer.write(CSV_HEADER)
        writer.write("\n")

        // Filas
        modules.forEach { m ->
            val fila = listOf(
                m.code.escaparCsv(),
                m.name.escaparCsv(),
                m.type.name.escaparCsv(),
                m.category.escaparCsv(),
                m.status.name.escaparCsv(),
                m.vendor.fullName.escaparCsv(),
                m.vendor.idNumber.escaparCsv(),
                (m.vendor.phone ?: "").escaparCsv(),
                m.location.address.escaparCsv(),
                (m.location.sectorId?.toString() ?: "").escaparCsv(),
                m.areaM2.toString().escaparCsv(),
                (m.validUntil ?: "").escaparCsv(),
            ).joinToString(",")
            writer.write(fila)
            writer.write("\n")
        }
    }

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        archivo,
    )
}

private const val CSV_HEADER =
    "Codigo,Nombre,Tipo,Categoria,Estado,Titular,Cedula,Telefono,Direccion,Sector,Area m2,Vigencia"

/** Envuelve el valor entre comillas y escapa las comillas internas (RFC 4180). */
private fun String.escaparCsv(): String {
    val escapado = replace("\"", "\"\"")
    return "\"$escapado\""
}
