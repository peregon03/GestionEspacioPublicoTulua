package co.gov.tulua.espaciopublico.ui.census

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.gov.tulua.espaciopublico.R
import co.gov.tulua.espaciopublico.core.designsystem.TuluaTheme
import co.gov.tulua.espaciopublico.core.designsystem.components.EmptyState
import co.gov.tulua.espaciopublico.core.designsystem.components.ErrorState
import co.gov.tulua.espaciopublico.core.designsystem.components.FilterChipRow
import co.gov.tulua.espaciopublico.core.designsystem.components.ModuleCard
import co.gov.tulua.espaciopublico.core.designsystem.components.SearchField
import co.gov.tulua.espaciopublico.core.designsystem.components.SkeletonBox
import co.gov.tulua.espaciopublico.core.designsystem.components.TuluaTopBar
import co.gov.tulua.espaciopublico.domain.model.ModuleStatus
import co.gov.tulua.espaciopublico.domain.model.ModuleType

// Umbral de items antes del final para disparar loadMore
private const val LOAD_MORE_THRESHOLD = 4

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CensusScreen(
    viewModel: CensusViewModel = hiltViewModel(),
    onModuleClick: (Long) -> Unit = {},
    onNewCensus: () -> Unit = {},
    onExportCsv: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedType by viewModel.selectedType.collectAsStateWithLifecycle()
    val selectedStatus by viewModel.selectedStatus.collectAsStateWithLifecycle()

    val isRefreshing = uiState is CensusUiState.Loading
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TuluaTopBar(
                title = stringResource(R.string.screen_censo),
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.cd_menu),
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(R.string.census_export_csv),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onExportCsv()
                            },
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewCensus,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.census_nuevo_registro),
                )
            }
        },
        modifier = modifier,
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Barra de búsqueda
                SearchField(
                    query = searchQuery,
                    onQueryChange = { viewModel.searchQuery.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                )

                // Chips de tipo
                FilterChipRow(
                    options = listOf(
                        stringResource(R.string.all),
                        stringResource(R.string.census_filter_formales),
                        stringResource(R.string.census_filter_informales),
                        stringResource(R.string.census_filter_feria),
                    ),
                    selectedIndex = selectedType.toChipIndex(),
                    onSelect = { index ->
                        viewModel.selectedType.value = index.toModuleType()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp),
                )

                Spacer(Modifier.height(4.dp))

                // Chips de estado
                FilterChipRow(
                    options = listOf(
                        stringResource(R.string.all),
                        stringResource(R.string.status_approved),
                        stringResource(R.string.status_in_review),
                        stringResource(R.string.status_rejected),
                        stringResource(R.string.census_filter_suspendido),
                    ),
                    selectedIndex = selectedStatus.toChipIndex(),
                    onSelect = { index ->
                        viewModel.selectedStatus.value = index.toModuleStatus()
                    },
                    modifier = Modifier.padding(horizontal = 12.dp),
                )

                Spacer(Modifier.height(4.dp))

                // Contenido según estado
                when (val state = uiState) {
                    is CensusUiState.Loading -> CensusSkeleton()
                    is CensusUiState.Error   -> ErrorState(
                        message = state.message,
                        onRetry = viewModel::refresh,
                    )
                    is CensusUiState.Success -> {
                        if (state.modules.isEmpty()) {
                            EmptyState(
                                title = stringResource(R.string.empty_state_title),
                                subtitle = stringResource(R.string.census_empty_subtitle),
                            )
                        } else {
                            CensusModuleList(
                                state = state,
                                onModuleClick = onModuleClick,
                                onLoadMore = viewModel::loadMore,
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── Lista con paginación infinita ────────────────────────────────────────────

@Composable
private fun CensusModuleList(
    state: CensusUiState.Success,
    onModuleClick: (Long) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    // Detectar cuando el usuario se acerca al final
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            state.hasMore && lastVisibleIndex >= totalItems - LOAD_MORE_THRESHOLD
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) onLoadMore()
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Text(
                text = stringResource(R.string.census_total_count, state.totalCount),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }

        itemsIndexed(
            items = state.modules,
            key = { _, module -> module.id },
        ) { _, module ->
            ModuleCard(
                module = module,
                onClick = { onModuleClick(module.id) },
            )
        }

        if (state.hasMore) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    SkeletonBox(height = 72.dp)
                }
            }
        }

        item { Spacer(Modifier.height(72.dp)) } // espacio para el FAB
    }
}

// ─── Skeleton de carga ────────────────────────────────────────────────────────

@Composable
private fun CensusSkeleton(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(count = 6) {
            SkeletonBox(height = 88.dp)
        }
    }
}

// ─── Helpers de mapeo chips ───────────────────────────────────────────────────

private fun ModuleType?.toChipIndex(): Int = when (this) {
    null                -> 0
    ModuleType.FORMAL   -> 1
    ModuleType.INFORMAL -> 2
    ModuleType.FERIA    -> 3
}

private fun Int.toModuleType(): ModuleType? = when (this) {
    1    -> ModuleType.FORMAL
    2    -> ModuleType.INFORMAL
    3    -> ModuleType.FERIA
    else -> null
}

private fun ModuleStatus?.toChipIndex(): Int = when (this) {
    null                    -> 0
    ModuleStatus.APROBADO   -> 1
    ModuleStatus.EN_REVISION -> 2
    ModuleStatus.RECHAZADO  -> 3
    ModuleStatus.SUSPENDIDO -> 4
}

private fun Int.toModuleStatus(): ModuleStatus? = when (this) {
    1    -> ModuleStatus.APROBADO
    2    -> ModuleStatus.EN_REVISION
    3    -> ModuleStatus.RECHAZADO
    4    -> ModuleStatus.SUSPENDIDO
    else -> null
}

// ─── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun CensusScreenLoadingPreview() {
    TuluaTheme {
        Scaffold(
            topBar = {
                TuluaTopBar(title = stringResource(R.string.screen_censo))
            },
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                CensusSkeleton()
            }
        }
    }
}
