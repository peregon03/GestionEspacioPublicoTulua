package co.gov.tulua.espaciopublico.ui.census

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.gov.tulua.espaciopublico.domain.model.Module
import co.gov.tulua.espaciopublico.domain.model.ModuleStatus
import co.gov.tulua.espaciopublico.domain.model.ModuleType
import co.gov.tulua.espaciopublico.domain.repository.ModuleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val PAGE_SIZE = 20
private const val SEARCH_DEBOUNCE_MS = 400L

sealed interface CensusUiState {
    data object Loading : CensusUiState
    data class Success(
        val modules: List<Module>,
        val hasMore: Boolean,
        val totalCount: Int,
    ) : CensusUiState
    data class Error(val message: String) : CensusUiState
}

@HiltViewModel
class CensusViewModel @Inject constructor(
    private val moduleRepository: ModuleRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CensusUiState>(CensusUiState.Loading)
    val uiState: StateFlow<CensusUiState> = _uiState.asStateFlow()

    val searchQuery = MutableStateFlow("")
    val selectedType = MutableStateFlow<ModuleType?>(null)
    val selectedStatus = MutableStateFlow<ModuleStatus?>(null)

    // Acumula todos los módulos cargados (paginación incremental)
    private val _loadedModules = MutableStateFlow<List<Module>>(emptyList())
    private val _currentPage = MutableStateFlow(1)

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private val filterTrigger = combine(
        searchQuery.debounce(SEARCH_DEBOUNCE_MS),
        selectedType,
        selectedStatus,
    ) { query, type, status -> Triple(query, type, status) }

    init {
        // Cuando cambian filtros o búsqueda → resetear y cargar página 1
        filterTrigger
            .onEach { (query, type, status) ->
                _loadedModules.value = emptyList()
                _currentPage.value = 1
                fetchPage(query = query, type = type, status = status, page = 1, replace = true)
            }
            .launchIn(viewModelScope)
    }

    fun loadMore() {
        val current = _uiState.value
        if (current !is CensusUiState.Success || !current.hasMore) return
        val nextPage = _currentPage.value + 1
        _currentPage.value = nextPage
        fetchPage(
            query = searchQuery.value,
            type = selectedType.value,
            status = selectedStatus.value,
            page = nextPage,
            replace = false,
        )
    }

    fun refresh() {
        _loadedModules.value = emptyList()
        _currentPage.value = 1
        _uiState.value = CensusUiState.Loading
        fetchPage(
            query = searchQuery.value,
            type = selectedType.value,
            status = selectedStatus.value,
            page = 1,
            replace = true,
        )
    }

    private fun fetchPage(
        query: String,
        type: ModuleType?,
        status: ModuleStatus?,
        page: Int,
        replace: Boolean,
    ) {
        viewModelScope.launch {
            if (replace) _uiState.value = CensusUiState.Loading

            val result = moduleRepository.getModules(
                search = query.takeIf { it.isNotBlank() },
                type = type,
                status = status,
                page = page,
                size = PAGE_SIZE,
            )

            if (result.isFailure) {
                _uiState.value = CensusUiState.Error(
                    result.exceptionOrNull()?.message ?: "Error al cargar el censo"
                )
                return@launch
            }

            val newItems = result.getOrThrow()
            val accumulated = if (replace) newItems else _loadedModules.value + newItems
            _loadedModules.value = accumulated

            _uiState.value = CensusUiState.Success(
                modules = accumulated,
                hasMore = newItems.size >= PAGE_SIZE,
                totalCount = accumulated.size,
            )
        }
    }
}
