package co.gov.tulua.espaciopublico.ui.census

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import co.gov.tulua.espaciopublico.R
import co.gov.tulua.espaciopublico.core.designsystem.CardBorder
import co.gov.tulua.espaciopublico.core.designsystem.TuluaTheme
import co.gov.tulua.espaciopublico.core.designsystem.components.ErrorState
import co.gov.tulua.espaciopublico.core.designsystem.components.InfoRow
import co.gov.tulua.espaciopublico.core.designsystem.components.SkeletonBox
import co.gov.tulua.espaciopublico.core.designsystem.components.StatusBadge
import co.gov.tulua.espaciopublico.core.designsystem.components.StatusType
import co.gov.tulua.espaciopublico.core.designsystem.components.TuluaTopBar
import co.gov.tulua.espaciopublico.core.designsystem.components.etiqueta
import co.gov.tulua.espaciopublico.core.designsystem.components.toStatusType
import co.gov.tulua.espaciopublico.core.map.MapMarker
import co.gov.tulua.espaciopublico.core.map.OsmMapView
import co.gov.tulua.espaciopublico.core.map.LatLng
import co.gov.tulua.espaciopublico.core.map.toLatLng
import co.gov.tulua.espaciopublico.domain.model.Document
import co.gov.tulua.espaciopublico.domain.model.DocumentType
import co.gov.tulua.espaciopublico.domain.model.GeoLocation
import co.gov.tulua.espaciopublico.domain.model.Module
import co.gov.tulua.espaciopublico.domain.model.ModuleStatus
import co.gov.tulua.espaciopublico.domain.model.ModuleType
import co.gov.tulua.espaciopublico.domain.model.Permit
import co.gov.tulua.espaciopublico.domain.model.Sanction
import co.gov.tulua.espaciopublico.domain.model.Vendor

@Composable
fun ModuleDetailScreen(
    viewModel: ModuleDetailViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TuluaTopBar(
                title = stringResource(R.string.census_detail_title),
                onBack = onBack,
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        when (val state = uiState) {
            is ModuleDetailUiState.Loading -> ModuleDetailSkeleton(
                modifier = Modifier.padding(innerPadding),
            )
            is ModuleDetailUiState.Error   -> ErrorState(
                message = state.message,
                onRetry = viewModel::retry,
                modifier = Modifier.padding(innerPadding),
            )
            is ModuleDetailUiState.Success -> ModuleDetailContent(
                module = state.module,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

// ─── Contenido principal ──────────────────────────────────────────────────────

@Composable
private fun ModuleDetailContent(
    module: Module,
    modifier: Modifier = Modifier,
) {
    var photoFullScreen by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            // Foto principal (si existe)
            if (module.photoUrl != null) {
                item {
                    AsyncImage(
                        model = module.photoUrl,
                        contentDescription = stringResource(R.string.census_photo_cd),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clickable { photoFullScreen = true },
                    )
                }
            }

            // ── Sección 1: Datos generales ────────────────────────────────
            item {
                SeccionDesplegable(
                    titulo = stringResource(R.string.census_section_datos_generales),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            StatusBadge(
                                status = module.type.toStatusType(),
                                label = module.type.etiqueta(),
                            )
                            StatusBadge(
                                status = module.status.toStatusType(),
                                label = module.status.etiqueta(),
                            )
                        }
                        HorizontalDivider()
                        InfoRow(label = stringResource(R.string.census_field_code), value = module.code)
                        HorizontalDivider()
                        InfoRow(label = stringResource(R.string.census_field_name), value = module.name)
                        HorizontalDivider()
                        InfoRow(label = stringResource(R.string.census_field_category), value = module.category)
                        HorizontalDivider()
                        InfoRow(label = stringResource(R.string.census_field_area), value = "${module.areaM2} m²")
                        if (module.widthM != null && module.lengthM != null) {
                            HorizontalDivider()
                            InfoRow(
                                label = stringResource(R.string.census_field_dimensions),
                                value = "${module.widthM} m × ${module.lengthM} m",
                            )
                        }
                        if (module.validUntil != null) {
                            HorizontalDivider()
                            InfoRow(label = stringResource(R.string.census_field_valid_until), value = module.validUntil)
                        }
                        if (module.observations != null) {
                            HorizontalDivider()
                            InfoRow(label = stringResource(R.string.census_field_observations), value = module.observations)
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }

            // ── Sección 2: Titular ────────────────────────────────────────
            item {
                SeccionDesplegable(
                    titulo = stringResource(R.string.census_section_titular),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    ) {
                        InfoRow(label = stringResource(R.string.census_field_fullname), value = module.vendor.fullName)
                        HorizontalDivider()
                        InfoRow(label = stringResource(R.string.census_field_idnumber), value = module.vendor.idNumber)
                        if (module.vendor.phone != null) {
                            HorizontalDivider()
                            InfoRow(label = stringResource(R.string.census_field_phone), value = module.vendor.phone)
                        }
                        if (module.vendor.email != null) {
                            HorizontalDivider()
                            InfoRow(label = stringResource(R.string.census_field_email), value = module.vendor.email)
                        }
                        if (module.vendor.association != null) {
                            HorizontalDivider()
                            InfoRow(label = stringResource(R.string.census_field_association), value = module.vendor.association)
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }

            // ── Sección 3: Ubicación ──────────────────────────────────────
            item {
                SeccionDesplegable(
                    titulo = stringResource(R.string.census_section_ubicacion),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    ) {
                        InfoRow(label = stringResource(R.string.census_field_address), value = module.location.address)
                        if (module.location.zone != null) {
                            HorizontalDivider()
                            InfoRow(label = stringResource(R.string.census_field_zone), value = module.location.zone)
                        }
                        if (module.location.sectorId != null) {
                            HorizontalDivider()
                            InfoRow(label = stringResource(R.string.census_field_sector), value = "Sector ${module.location.sectorId}")
                        }
                        HorizontalDivider()
                        InfoRow(
                            label = stringResource(R.string.census_field_coords),
                            value = "${module.location.latitude}, ${module.location.longitude}",
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    OsmMapView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        center = module.location.toLatLng(),
                        zoom = 17.0,
                        markers = listOf(
                            MapMarker(
                                id = module.id.toString(),
                                position = module.location.toLatLng(),
                                title = module.name,
                            ),
                        ),
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

            // ── Sección 4: Permisos ───────────────────────────────────────
            if (module.permits.isNotEmpty()) {
                item {
                    SeccionDesplegable(
                        titulo = stringResource(R.string.census_section_permisos),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            module.permits.forEach { permit ->
                                PermisoCard(permit = permit)
                            }
                        }
                    }
                }
            }

            // ── Sección 5: Documentos ─────────────────────────────────────
            if (module.documents.isNotEmpty()) {
                item {
                    SeccionDesplegable(
                        titulo = stringResource(R.string.census_section_documentos),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            module.documents.forEach { doc ->
                                DocumentoCard(document = doc)
                            }
                        }
                    }
                }
            }

            // ── Sección 6: Sanciones ──────────────────────────────────────
            if (module.sanctions.isNotEmpty()) {
                item {
                    SeccionDesplegable(
                        titulo = stringResource(R.string.census_section_sanciones),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            module.sanctions.forEach { sancion ->
                                SancionCard(sanction = sancion)
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(24.dp)) }
        }

        // Pantalla completa para la foto
        if (photoFullScreen && module.photoUrl != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center,
            ) {
                AsyncImage(
                    model = module.photoUrl,
                    contentDescription = stringResource(R.string.census_photo_cd),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize(),
                )
                IconButton(
                    onClick = { photoFullScreen = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.close),
                        tint = Color.White,
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
        }
    }
}

// ─── Sección desplegable ──────────────────────────────────────────────────────

@Composable
private fun SeccionDesplegable(
    titulo: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(true) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        HorizontalDivider()
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically(),
        ) {
            content()
        }
    }
}

// ─── Tarjeta de permiso ───────────────────────────────────────────────────────

@Composable
private fun PermisoCard(
    permit: Permit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = permit.type,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                StatusBadge(
                    status = if (permit.isActive) StatusType.ACTIVE else StatusType.INACTIVE,
                    label = if (permit.isActive) "Vigente" else "Vencido",
                )
            }
            InfoRow(label = "Numero", value = permit.number)
            HorizontalDivider()
            InfoRow(label = "Expedido", value = permit.issuedAt)
            HorizontalDivider()
            InfoRow(label = "Vence", value = permit.expiresAt)
        }
    }
}

// ─── Tarjeta de documento ─────────────────────────────────────────────────────

@Composable
private fun DocumentoCard(
    document: Document,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = document.name,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            InfoRow(label = "Tipo", value = document.type.name)
            HorizontalDivider()
            InfoRow(label = "Cargado", value = document.uploadedAt)
        }
    }
}

// ─── Tarjeta de sanción ───────────────────────────────────────────────────────

@Composable
private fun SancionCard(
    sanction: Sanction,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = sanction.type.name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                StatusBadge(
                    status = if (sanction.isActive) StatusType.DANGER else StatusType.INACTIVE,
                    label = if (sanction.isActive) "Activa" else "Resuelta",
                )
            }
            Text(
                text = sanction.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            HorizontalDivider()
            InfoRow(label = "Emitida", value = sanction.issuedAt)
            if (sanction.resolvedAt != null) {
                HorizontalDivider()
                InfoRow(label = "Resuelta", value = sanction.resolvedAt)
            }
        }
    }
}

// ─── Skeleton ─────────────────────────────────────────────────────────────────

@Composable
private fun ModuleDetailSkeleton(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
    ) {
        item { SkeletonBox(height = 220.dp) }
        items(count = 4) {
            SkeletonBox(height = 72.dp)
        }
    }
}

// ─── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun ModuleDetailContentPreview() {
    TuluaTheme {
        Scaffold(
            topBar = {
                TuluaTopBar(
                    title = "Detalle de modulo",
                    onBack = {},
                )
            },
        ) { innerPadding ->
            ModuleDetailContent(
                module = previewModuleDetail(),
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

private fun previewModuleDetail() = Module(
    id = 1L,
    code = "MOD-2025-0001",
    name = "Puesto de frutas La Esquina",
    type = ModuleType.FORMAL,
    category = "Alimentos",
    status = ModuleStatus.APROBADO,
    vendor = Vendor(
        id = 1L,
        fullName = "Carlos Andres Perez",
        idNumber = "1234567890",
        phone = "318 456 7890",
        email = "carlos@ejemplo.com",
        association = "Asociacion de Vendedores",
    ),
    location = GeoLocation(
        address = "Cra 25 #25-10",
        latitude = 4.0855,
        longitude = -76.1958,
        sectorId = 1L,
        zone = "Centro",
    ),
    areaM2 = 4.0,
    widthM = 2.0,
    lengthM = 2.0,
    validUntil = "2026-06-30",
    permits = listOf(
        Permit(1L, "Ocupacion", "PER-001", "2025-01-15", "2026-06-30", true),
    ),
    documents = listOf(
        Document(1L, DocumentType.CEDULA, "Cedula de Carlos", null, "2025-01-10"),
    ),
    sanctions = emptyList(),
)
