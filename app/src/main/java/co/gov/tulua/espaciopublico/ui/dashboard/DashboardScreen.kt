package co.gov.tulua.espaciopublico.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.gov.tulua.espaciopublico.R
import co.gov.tulua.espaciopublico.core.designsystem.Amber500
import co.gov.tulua.espaciopublico.core.designsystem.AmberContainer
import co.gov.tulua.espaciopublico.core.designsystem.CardBorder
import co.gov.tulua.espaciopublico.core.designsystem.Green900
import co.gov.tulua.espaciopublico.core.designsystem.TuluaTheme
import co.gov.tulua.espaciopublico.core.designsystem.components.ErrorState
import co.gov.tulua.espaciopublico.core.designsystem.components.SectionHeader
import co.gov.tulua.espaciopublico.core.designsystem.components.SkeletonBox
import co.gov.tulua.espaciopublico.core.designsystem.components.StatCard
import co.gov.tulua.espaciopublico.core.designsystem.components.TuluaTopBar
import co.gov.tulua.espaciopublico.domain.model.DashboardMetrics
import co.gov.tulua.espaciopublico.domain.model.Event
import co.gov.tulua.espaciopublico.domain.model.EventStatus
import co.gov.tulua.espaciopublico.domain.model.GeoLocation
import co.gov.tulua.espaciopublico.domain.model.SaturationLevel
import co.gov.tulua.espaciopublico.domain.model.Sector
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToRequests: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing = uiState is DashboardUiState.Loading

    Scaffold(
        topBar = {
            TuluaTopBar(title = stringResource(R.string.screen_dashboard))
        },
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (val state = uiState) {
                is DashboardUiState.Loading -> DashboardSkeleton()
                is DashboardUiState.Error   -> ErrorState(
                    message = state.message,
                    onRetry = viewModel::refresh,
                )
                is DashboardUiState.Success -> DashboardContent(
                    metrics = state.metrics,
                    sectors = state.sectors,
                    onNavigateToRequests = onNavigateToRequests,
                )
            }
        }
    }
}

// ─── Contenido principal ──────────────────────────────────────────────────────

@Composable
private fun DashboardContent(
    metrics: DashboardMetrics,
    sectors: List<Sector>,
    onNavigateToRequests: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Banner de pico de ocupación, descartable
    var bannerVisible by remember { mutableStateOf(true) }
    val mostrarBanner = bannerVisible && metrics.occupancyPercent > 80f

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item { Spacer(Modifier.height(8.dp)) }

        // Banner de alerta de ocupación alta
        if (mostrarBanner) {
            item {
                OcupacionBanner(
                    percent = metrics.occupancyPercent,
                    onDismiss = { bannerVisible = false },
                )
            }
        }

        // Grid 2×2 de estadísticas
        item {
            SectionHeader(
                title = stringResource(R.string.dashboard_stats_title),
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        item {
            StatsGrid(
                metrics = metrics,
                onNavigateToRequests = onNavigateToRequests,
            )
        }

        // Sección de módulos por sector
        item {
            SectionHeader(
                title = stringResource(R.string.dashboard_section_sectores),
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        item {
            SectorBarChart(
                sectors = sectors,
            )
        }

        // Sección de próximos eventos
        item {
            SectionHeader(
                title = stringResource(R.string.dashboard_section_eventos),
                onVerTodos = {},
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        val proximosEventos = metrics.upcomingEvents.take(3)
        if (proximosEventos.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.dashboard_no_eventos),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
        } else {
            proximosEventos.forEach { event ->
                item(key = event.id) {
                    EventoCard(event = event)
                }
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

// ─── Grid 2×2 de estadísticas ─────────────────────────────────────────────────

@Composable
private fun StatsGrid(
    metrics: DashboardMetrics,
    onNavigateToRequests: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Total censados con delta semanal
            StatCard(
                label = stringResource(R.string.dashboard_stat_total_censados),
                value = metrics.totalModules.toString(),
                delta = "+${metrics.weeklyDelta} ${stringResource(R.string.dashboard_stat_esta_semana)}",
                modifier = Modifier.weight(1f),
            )
            // Ocupación hoy
            StatCard(
                label = stringResource(R.string.dashboard_stat_ocupacion_hoy),
                value = "${metrics.occupancyPercent.roundToInt()}%",
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Formales vs Informales
            FormalesInformalesCard(
                formalCount = metrics.formalCount,
                informalCount = metrics.informalCount,
                modifier = Modifier.weight(1f),
            )
            // Solicitudes pendientes (navegable)
            StatCard(
                label = stringResource(R.string.dashboard_stat_solicitudes),
                value = metrics.pendingRequests.toString(),
                modifier = Modifier
                    .weight(1f),
            )
        }
    }
}

// ─── Card Formales vs Informales con barra segmentada ─────────────────────────

@Composable
private fun FormalesInformalesCard(
    formalCount: Int,
    informalCount: Int,
    modifier: Modifier = Modifier,
) {
    val total = (formalCount + informalCount).coerceAtLeast(1)
    val fractionFormal = formalCount.toFloat() / total.toFloat()

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = stringResource(R.string.dashboard_stat_formales_informales),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            // Barra segmentada usando Canvas directamente
            BarraSegmentada(fractionFormal = fractionFormal)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.dashboard_formales, formalCount),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = stringResource(R.string.dashboard_informales, informalCount),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                )
            }
        }
    }
}

@Composable
private fun BarraSegmentada(
    fractionFormal: Float,
    modifier: Modifier = Modifier,
) {
    // Captura los colores fuera del lambda de Canvas (contexto Composable requerido)
    val colorFormal = Green900
    val colorInformal = Amber500

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(10.dp),
    ) {
        val radius = size.height / 2f

        // Segmento formal (verde)
        val anchoFormal = size.width * fractionFormal
        if (anchoFormal > 0f) {
            drawRoundRect(
                color = colorFormal,
                topLeft = Offset.Zero,
                size = Size(anchoFormal, size.height),
                cornerRadius = CornerRadius(radius, radius),
            )
        }

        // Segmento informal (ámbar)
        val anchoInformal = size.width * (1f - fractionFormal)
        if (anchoInformal > 0f) {
            drawRoundRect(
                color = colorInformal,
                topLeft = Offset(anchoFormal, 0f),
                size = Size(anchoInformal, size.height),
                cornerRadius = CornerRadius(radius, radius),
            )
        }
    }
}

// ─── Tarjeta de evento ────────────────────────────────────────────────────────

@Composable
private fun EventoCard(
    event: Event,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = event.name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = event.status.etiqueta(),
                    style = MaterialTheme.typography.labelSmall,
                    color = event.status.color(),
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
            Text(
                text = "${stringResource(R.string.dashboard_evento_fecha)}: ${event.startDate}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "${stringResource(R.string.dashboard_evento_organizador)}: ${event.organizer}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun EventStatus.etiqueta(): String = when (this) {
    EventStatus.PROGRAMADA -> "Programada"
    EventStatus.EN_CURSO   -> "En curso"
    EventStatus.FINALIZADA -> "Finalizada"
}

@Composable
private fun EventStatus.color(): androidx.compose.ui.graphics.Color = when (this) {
    EventStatus.PROGRAMADA -> MaterialTheme.colorScheme.primary
    EventStatus.EN_CURSO   -> Amber500
    EventStatus.FINALIZADA -> MaterialTheme.colorScheme.onSurfaceVariant
}

// ─── Banner de pico de ocupación ─────────────────────────────────────────────

@Composable
private fun OcupacionBanner(
    percent: Float,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = AmberContainer),
        border = BorderStroke(1.dp, Amber500.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f),
            ) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = null,
                    tint = Amber500,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = stringResource(
                        R.string.dashboard_banner_ocupacion,
                        percent.roundToInt(),
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.close),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

// ─── Esqueleto de carga ───────────────────────────────────────────────────────

@Composable
private fun DashboardSkeleton(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item { Spacer(Modifier.height(8.dp)) }

        // Título de sección skeleton
        item {
            SkeletonBox(height = 20.dp, modifier = Modifier.fillMaxWidth(0.45f))
        }

        // Grid 2×2 skeleton
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SkeletonBox(height = 80.dp, modifier = Modifier.weight(1f))
                    SkeletonBox(height = 80.dp, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SkeletonBox(height = 80.dp, modifier = Modifier.weight(1f))
                    SkeletonBox(height = 80.dp, modifier = Modifier.weight(1f))
                }
            }
        }

        // Título de sección skeleton
        item {
            SkeletonBox(
                height = 20.dp,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(top = 8.dp),
            )
        }

        // Área del gráfico skeleton
        item {
            SkeletonBox(height = 160.dp)
        }

        // Título de sección skeleton
        item {
            SkeletonBox(
                height = 20.dp,
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .padding(top = 8.dp),
            )
        }

        // Tarjetas de eventos skeleton
        items(count = 3) {
            SkeletonBox(height = 72.dp)
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

// ─── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun DashboardScreenPreview() {
    TuluaTheme {
        // Preview del estado de carga (skeleton)
        Scaffold(
            topBar = {
                TuluaTopBar(title = "Inicio")
            },
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                DashboardSkeleton()
            }
        }
    }
}

@Preview(showBackground = true, name = "Dashboard Content")
@Composable
private fun DashboardContentPreview() {
    TuluaTheme {
        val metricasEjemplo = DashboardMetrics(
            totalModules = 208,
            weeklyDelta = 3,
            formalCount = 24,
            informalCount = 16,
            occupiedToday = 35,
            occupancyPercent = 85f,
            pendingRequests = 6,
            sectors = emptyList(),
            upcomingEvents = listOf(
                Event(
                    id = 1L,
                    code = "EVT-001",
                    name = "Feria de Artesanías",
                    status = EventStatus.PROGRAMADA,
                    startDate = "2026-12-01",
                    endDate = "2026-12-05",
                    location = GeoLocation(address = "Centro, Tuluá", latitude = 4.08, longitude = -76.19),
                    organizer = "Alcaldía de Tuluá",
                    totalSpots = 50,
                    assignedSpots = 30,
                ),
            ),
        )
        val sectoresEjemplo = listOf(
            Sector(1L, "Carrera 25", moduleCount = 48, occupancyPercent = 85f, saturationLevel = SaturationLevel.ALTA),
            Sector(2L, "Galería", moduleCount = 62, occupancyPercent = 92f, saturationLevel = SaturationLevel.CRITICA),
            Sector(3L, "Parque Boyacá", moduleCount = 25, occupancyPercent = 60f, saturationLevel = SaturationLevel.MEDIA),
        )
        DashboardContent(
            metrics = metricasEjemplo,
            sectors = sectoresEjemplo,
            onNavigateToRequests = {},
        )
    }
}
