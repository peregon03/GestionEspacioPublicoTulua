package co.gov.tulua.espaciopublico.ui.census.wizard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.gov.tulua.espaciopublico.domain.model.Document
import co.gov.tulua.espaciopublico.domain.model.GeoLocation
import co.gov.tulua.espaciopublico.domain.model.Module
import co.gov.tulua.espaciopublico.domain.model.ModuleStatus
import co.gov.tulua.espaciopublico.domain.model.ModuleType
import co.gov.tulua.espaciopublico.domain.model.Vendor
import co.gov.tulua.espaciopublico.domain.repository.ModuleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

const val WIZARD_TOTAL_STEPS = 5

sealed interface NewCensusSubmitState {
    data object Idle : NewCensusSubmitState
    data object Submitting : NewCensusSubmitState
    data class Success(val createdId: Long) : NewCensusSubmitState
    data class Error(val message: String) : NewCensusSubmitState
}

@HiltViewModel
class NewCensusViewModel @Inject constructor(
    private val moduleRepository: ModuleRepository,
) : ViewModel() {

    // ── Estado del wizard ──────────────────────────────────────────────────
    private val _currentStep = MutableStateFlow(0)
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()

    // ── Paso 1: Titular ────────────────────────────────────────────────────
    val titularNombre = MutableStateFlow("")
    val titularCedula = MutableStateFlow("")
    val titularTelefono = MutableStateFlow("")
    val titularEmail = MutableStateFlow("")
    val titularAsociacion = MutableStateFlow("")

    // ── Paso 2: Módulo ─────────────────────────────────────────────────────
    val moduloNombre = MutableStateFlow("")
    val moduloCodigo = MutableStateFlow(generarCodigo())
    val moduloTipo = MutableStateFlow(ModuleType.INFORMAL)
    val moduloCategoria = MutableStateFlow("")
    val moduloArea = MutableStateFlow("")
    val moduloAncho = MutableStateFlow("")
    val moduloLargo = MutableStateFlow("")

    // ── Paso 3: Ubicación ──────────────────────────────────────────────────
    val ubicacionDireccion = MutableStateFlow("")
    val ubicacionLatitud = MutableStateFlow("")
    val ubicacionLongitud = MutableStateFlow("")

    // ── Paso 4: Foto ───────────────────────────────────────────────────────
    val fotoUri = MutableStateFlow<String?>(null)

    // ── Estado de envío ────────────────────────────────────────────────────
    private val _submitState = MutableStateFlow<NewCensusSubmitState>(NewCensusSubmitState.Idle)
    val submitState: StateFlow<NewCensusSubmitState> = _submitState.asStateFlow()

    // ── Validación por paso ────────────────────────────────────────────────

    fun isStepValid(step: Int): Boolean = when (step) {
        0 -> titularNombre.value.isNotBlank() && titularCedula.value.isNotBlank()
        1 -> moduloNombre.value.isNotBlank() && moduloCategoria.value.isNotBlank() && moduloArea.value.toDoubleOrNull() != null
        2 -> ubicacionDireccion.value.isNotBlank()
        3 -> true // La foto es opcional
        4 -> true // Paso de resumen
        else -> false
    }

    fun nextStep() {
        if (_currentStep.value < WIZARD_TOTAL_STEPS - 1) {
            _currentStep.value++
        }
    }

    fun previousStep() {
        if (_currentStep.value > 0) {
            _currentStep.value--
        }
    }

    fun submit() {
        if (_submitState.value is NewCensusSubmitState.Submitting) return
        _submitState.value = NewCensusSubmitState.Submitting

        viewModelScope.launch {
            val vendor = Vendor(
                id = 0L,
                fullName = titularNombre.value.trim(),
                idNumber = titularCedula.value.trim(),
                phone = titularTelefono.value.trim().takeIf { it.isNotBlank() },
                email = titularEmail.value.trim().takeIf { it.isNotBlank() },
                association = titularAsociacion.value.trim().takeIf { it.isNotBlank() },
            )

            val lat = ubicacionLatitud.value.toDoubleOrNull() ?: 4.0847
            val lng = ubicacionLongitud.value.toDoubleOrNull() ?: -76.1954

            val location = GeoLocation(
                address = ubicacionDireccion.value.trim(),
                latitude = lat,
                longitude = lng,
            )

            val module = Module(
                id = 0L,
                code = moduloCodigo.value,
                name = moduloNombre.value.trim(),
                type = moduloTipo.value,
                category = moduloCategoria.value.trim(),
                status = ModuleStatus.EN_REVISION,
                vendor = vendor,
                location = location,
                areaM2 = moduloArea.value.toDoubleOrNull() ?: 0.0,
                widthM = moduloAncho.value.toDoubleOrNull(),
                lengthM = moduloLargo.value.toDoubleOrNull(),
                photoUrl = fotoUri.value,
                documents = emptyList(),
                permits = emptyList(),
                sanctions = emptyList(),
            )

            val result = moduleRepository.createModule(module)
            _submitState.value = if (result.isSuccess) {
                NewCensusSubmitState.Success(result.getOrThrow().id)
            } else {
                NewCensusSubmitState.Error(
                    result.exceptionOrNull()?.message ?: "Error al crear el modulo"
                )
            }
        }
    }

    fun resetSubmitState() {
        _submitState.value = NewCensusSubmitState.Idle
    }

    private companion object {
        fun generarCodigo(): String {
            val year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
            val num = (1000..9999).random()
            return "MOD-$year-$num"
        }
    }
}
