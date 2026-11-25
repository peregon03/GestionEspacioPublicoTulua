package co.gov.tulua.espaciopublico.ui.census.wizard

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import co.gov.tulua.espaciopublico.R
import co.gov.tulua.espaciopublico.core.designsystem.TuluaTheme
import co.gov.tulua.espaciopublico.core.designsystem.components.InfoRow
import co.gov.tulua.espaciopublico.core.designsystem.components.TuluaTopBar
import co.gov.tulua.espaciopublico.core.map.MapMarker
import co.gov.tulua.espaciopublico.core.map.OsmMapView
import co.gov.tulua.espaciopublico.core.map.LatLng
import co.gov.tulua.espaciopublico.core.map.TuluaMapDefaults
import co.gov.tulua.espaciopublico.domain.model.ModuleType
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCensusWizardScreen(
    viewModel: NewCensusViewModel = hiltViewModel(),
    onCancel: () -> Unit = {},
    onSuccess: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val currentStep by viewModel.currentStep.collectAsStateWithLifecycle()
    val submitState by viewModel.submitState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { WIZARD_TOTAL_STEPS })
    val scope = rememberCoroutineScope()

    // Sincronizar pager con el paso actual del VM
    LaunchedEffect(currentStep) {
        pagerState.animateScrollToPage(currentStep)
    }

    // Reaccionar al éxito del envío
    LaunchedEffect(submitState) {
        if (submitState is NewCensusSubmitState.Success) onSuccess()
    }

    Scaffold(
        topBar = {
            TuluaTopBar(
                title = stringResource(R.string.census_wizard_title),
                onBack = {
                    if (currentStep == 0) onCancel()
                    else viewModel.previousStep()
                },
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // Indicador de pasos
            WizardStepIndicator(
                totalSteps = WIZARD_TOTAL_STEPS,
                currentStep = currentStep,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            )

            // Pager con los pasos
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false,
                modifier = Modifier.weight(1f),
            ) { page ->
                when (page) {
                    0 -> PasoTitular(viewModel = viewModel)
                    1 -> PasoModulo(viewModel = viewModel)
                    2 -> PasoUbicacion(viewModel = viewModel)
                    3 -> PasoFoto(viewModel = viewModel)
                    4 -> PasoResumen(viewModel = viewModel)
                }
            }

            // Botonera de navegación
            WizardNavigationBar(
                currentStep = currentStep,
                totalSteps = WIZARD_TOTAL_STEPS,
                isStepValid = viewModel.isStepValid(currentStep),
                isSubmitting = submitState is NewCensusSubmitState.Submitting,
                onNext = {
                    if (currentStep < WIZARD_TOTAL_STEPS - 1) {
                        viewModel.nextStep()
                    } else {
                        viewModel.submit()
                    }
                },
                onBack = {
                    if (currentStep > 0) viewModel.previousStep()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            )

            // Error de envío
            if (submitState is NewCensusSubmitState.Error) {
                Text(
                    text = (submitState as NewCensusSubmitState.Error).message,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp),
                )
            }
        }
    }
}

// ─── Indicador de pasos ───────────────────────────────────────────────────────

@Composable
private fun WizardStepIndicator(
    totalSteps: Int,
    currentStep: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(totalSteps) { index ->
            val estado = when {
                index < currentStep  -> EstadoPaso.COMPLETADO
                index == currentStep -> EstadoPaso.ACTUAL
                else                 -> EstadoPaso.FUTURO
            }
            CirculoPaso(numero = index + 1, estado = estado)
            if (index < totalSteps - 1) {
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height(2.dp)
                        .background(
                            if (index < currentStep) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outlineVariant,
                        ),
                )
            }
        }
    }
}

private enum class EstadoPaso { COMPLETADO, ACTUAL, FUTURO }

@Composable
private fun CirculoPaso(
    numero: Int,
    estado: EstadoPaso,
    modifier: Modifier = Modifier,
) {
    val (bg, fg, border) = when (estado) {
        EstadoPaso.COMPLETADO -> Triple(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.onPrimary,
            Color.Transparent,
        )
        EstadoPaso.ACTUAL -> Triple(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primary,
        )
        EstadoPaso.FUTURO -> Triple(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            MaterialTheme.colorScheme.outlineVariant,
        )
    }

    Box(
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(bg)
            .border(1.5.dp, border, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = numero.toString(),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = fg,
        )
    }
}

// ─── Botonera de navegación ────────────────────────────────────────────────────

@Composable
private fun WizardNavigationBar(
    currentStep: Int,
    totalSteps: Int,
    isStepValid: Boolean,
    isSubmitting: Boolean,
    onNext: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (currentStep > 0) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f),
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text(text = stringResource(R.string.wizard_back))
            }
        }

        Button(
            onClick = onNext,
            enabled = isStepValid && !isSubmitting,
            modifier = Modifier.weight(1f),
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                )
            } else {
                val label = if (currentStep == totalSteps - 1) {
                    stringResource(R.string.confirm)
                } else {
                    stringResource(R.string.wizard_next)
                }
                Text(text = label)
                if (currentStep < totalSteps - 1) {
                    Spacer(Modifier.width(4.dp))
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}

// ─── Paso 1: Titular ──────────────────────────────────────────────────────────

@Composable
private fun PasoTitular(
    viewModel: NewCensusViewModel,
    modifier: Modifier = Modifier,
) {
    val nombre by viewModel.titularNombre.collectAsStateWithLifecycle()
    val cedula by viewModel.titularCedula.collectAsStateWithLifecycle()
    val telefono by viewModel.titularTelefono.collectAsStateWithLifecycle()
    val email by viewModel.titularEmail.collectAsStateWithLifecycle()
    val asociacion by viewModel.titularAsociacion.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        PasoTitulo(stringResource(R.string.wizard_step1_title))

        CampoTexto(
            label = stringResource(R.string.census_field_fullname),
            value = nombre,
            onValueChange = { viewModel.titularNombre.value = it },
            requerido = true,
        )
        CampoTexto(
            label = stringResource(R.string.census_field_idnumber),
            value = cedula,
            onValueChange = { viewModel.titularCedula.value = it },
            requerido = true,
            keyboardType = KeyboardType.Number,
        )
        CampoTexto(
            label = stringResource(R.string.census_field_phone),
            value = telefono,
            onValueChange = { viewModel.titularTelefono.value = it },
            keyboardType = KeyboardType.Phone,
        )
        CampoTexto(
            label = stringResource(R.string.census_field_email),
            value = email,
            onValueChange = { viewModel.titularEmail.value = it },
            keyboardType = KeyboardType.Email,
        )
        CampoTexto(
            label = stringResource(R.string.census_field_association),
            value = asociacion,
            onValueChange = { viewModel.titularAsociacion.value = it },
        )
    }
}

// ─── Paso 2: Módulo ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PasoModulo(
    viewModel: NewCensusViewModel,
    modifier: Modifier = Modifier,
) {
    val nombre by viewModel.moduloNombre.collectAsStateWithLifecycle()
    val codigo by viewModel.moduloCodigo.collectAsStateWithLifecycle()
    val tipo by viewModel.moduloTipo.collectAsStateWithLifecycle()
    val categoria by viewModel.moduloCategoria.collectAsStateWithLifecycle()
    val area by viewModel.moduloArea.collectAsStateWithLifecycle()
    val ancho by viewModel.moduloAncho.collectAsStateWithLifecycle()
    val largo by viewModel.moduloLargo.collectAsStateWithLifecycle()

    var tipoExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        PasoTitulo(stringResource(R.string.wizard_step2_title))

        CampoTexto(
            label = stringResource(R.string.census_field_name),
            value = nombre,
            onValueChange = { viewModel.moduloNombre.value = it },
            requerido = true,
        )
        CampoTexto(
            label = stringResource(R.string.census_field_code),
            value = codigo,
            onValueChange = { viewModel.moduloCodigo.value = it },
            requerido = true,
        )

        // Dropdown de tipo
        ExposedDropdownMenuBox(
            expanded = tipoExpanded,
            onExpandedChange = { tipoExpanded = it },
        ) {
            OutlinedTextField(
                value = tipo.name,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.census_wizard_type)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(tipoExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
            )
            ExposedDropdownMenu(
                expanded = tipoExpanded,
                onDismissRequest = { tipoExpanded = false },
            ) {
                ModuleType.entries.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion.name) },
                        onClick = {
                            viewModel.moduloTipo.value = opcion
                            tipoExpanded = false
                        },
                    )
                }
            }
        }

        CampoTexto(
            label = stringResource(R.string.census_vendor_category),
            value = categoria,
            onValueChange = { viewModel.moduloCategoria.value = it },
            requerido = true,
        )
        CampoTexto(
            label = stringResource(R.string.census_field_area),
            value = area,
            onValueChange = { viewModel.moduloArea.value = it },
            requerido = true,
            keyboardType = KeyboardType.Decimal,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CampoTexto(
                label = stringResource(R.string.census_wizard_width),
                value = ancho,
                onValueChange = { viewModel.moduloAncho.value = it },
                keyboardType = KeyboardType.Decimal,
                modifier = Modifier.weight(1f),
            )
            CampoTexto(
                label = stringResource(R.string.census_wizard_length),
                value = largo,
                onValueChange = { viewModel.moduloLargo.value = it },
                keyboardType = KeyboardType.Decimal,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

// ─── Paso 3: Ubicación ────────────────────────────────────────────────────────

@Composable
private fun PasoUbicacion(
    viewModel: NewCensusViewModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val direccion by viewModel.ubicacionDireccion.collectAsStateWithLifecycle()
    val latitud by viewModel.ubicacionLatitud.collectAsStateWithLifecycle()
    val longitud by viewModel.ubicacionLongitud.collectAsStateWithLifecycle()

    val lat = latitud.toDoubleOrNull() ?: TuluaMapDefaults.Center.latitude
    val lng = longitud.toDoubleOrNull() ?: TuluaMapDefaults.Center.longitude

    // Lanzador de permiso de ubicación
    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            obtenerUbicacion(context) { lt, ln ->
                viewModel.ubicacionLatitud.value = lt.toString()
                viewModel.ubicacionLongitud.value = ln.toString()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        PasoTitulo(stringResource(R.string.wizard_step3_title))

        Button(
            onClick = { locationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(R.string.wizard_get_location))
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CampoTexto(
                label = stringResource(R.string.wizard_latitude),
                value = latitud,
                onValueChange = { viewModel.ubicacionLatitud.value = it },
                keyboardType = KeyboardType.Decimal,
                modifier = Modifier.weight(1f),
            )
            CampoTexto(
                label = stringResource(R.string.wizard_longitude),
                value = longitud,
                onValueChange = { viewModel.ubicacionLongitud.value = it },
                keyboardType = KeyboardType.Decimal,
                modifier = Modifier.weight(1f),
            )
        }

        CampoTexto(
            label = stringResource(R.string.census_field_address),
            value = direccion,
            onValueChange = { viewModel.ubicacionDireccion.value = it },
            requerido = true,
        )

        // Mini mapa
        OsmMapView(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            center = LatLng(lat, lng),
            zoom = 16.0,
            markers = if (latitud.isNotBlank() && longitud.isNotBlank()) {
                listOf(MapMarker(id = "nuevo", position = LatLng(lat, lng), title = "Ubicacion seleccionada"))
            } else {
                emptyList()
            },
        )
    }
}

// ─── Paso 4: Foto ─────────────────────────────────────────────────────────────

@Composable
private fun PasoFoto(
    viewModel: NewCensusViewModel,
    modifier: Modifier = Modifier,
) {
    val fotoUri by viewModel.fotoUri.collectAsStateWithLifecycle()

    // Lanzador de permiso de cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { /* permiso solicitado; CameraPreview internamente verifica */ }

    var mostrarCamara by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PasoTitulo(stringResource(R.string.wizard_step4_title))

        if (fotoUri != null) {
            // Vista previa de la foto capturada
            AsyncImage(
                model = fotoUri,
                contentDescription = stringResource(R.string.census_photo_cd),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(MaterialTheme.shapes.large),
            )
            Button(
                onClick = {
                    mostrarCamara = true
                    viewModel.fotoUri.value = null
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.wizard_retake_photo))
            }
        } else if (mostrarCamara) {
            CameraPreview(
                onPhotoTaken = { path ->
                    viewModel.fotoUri.value = path
                    mostrarCamara = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
            )
        } else {
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = {
                    cameraLauncher.launch(Manifest.permission.CAMERA)
                    mostrarCamara = true
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.wizard_take_photo))
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.wizard_photo_optional),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// ─── Paso 5: Resumen ──────────────────────────────────────────────────────────

@Composable
private fun PasoResumen(
    viewModel: NewCensusViewModel,
    modifier: Modifier = Modifier,
) {
    val nombre by viewModel.titularNombre.collectAsStateWithLifecycle()
    val cedula by viewModel.titularCedula.collectAsStateWithLifecycle()
    val telefono by viewModel.titularTelefono.collectAsStateWithLifecycle()
    val modNombre by viewModel.moduloNombre.collectAsStateWithLifecycle()
    val modCodigo by viewModel.moduloCodigo.collectAsStateWithLifecycle()
    val modTipo by viewModel.moduloTipo.collectAsStateWithLifecycle()
    val modCategoria by viewModel.moduloCategoria.collectAsStateWithLifecycle()
    val modArea by viewModel.moduloArea.collectAsStateWithLifecycle()
    val direccion by viewModel.ubicacionDireccion.collectAsStateWithLifecycle()
    val latitud by viewModel.ubicacionLatitud.collectAsStateWithLifecycle()
    val longitud by viewModel.ubicacionLongitud.collectAsStateWithLifecycle()
    val fotoUri by viewModel.fotoUri.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PasoTitulo(stringResource(R.string.wizard_step5_title))

        ResumenSeccion(titulo = stringResource(R.string.wizard_step1_title)) {
            InfoRow(label = stringResource(R.string.census_field_fullname), value = nombre)
            InfoRow(label = stringResource(R.string.census_field_idnumber), value = cedula)
            if (telefono.isNotBlank()) InfoRow(label = stringResource(R.string.census_field_phone), value = telefono)
        }

        ResumenSeccion(titulo = stringResource(R.string.wizard_step2_title)) {
            InfoRow(label = stringResource(R.string.census_field_name), value = modNombre)
            InfoRow(label = stringResource(R.string.census_field_code), value = modCodigo)
            InfoRow(label = stringResource(R.string.census_wizard_type), value = modTipo.name)
            InfoRow(label = stringResource(R.string.census_vendor_category), value = modCategoria)
            InfoRow(label = stringResource(R.string.census_field_area), value = "$modArea m²")
        }

        ResumenSeccion(titulo = stringResource(R.string.wizard_step3_title)) {
            InfoRow(label = stringResource(R.string.census_field_address), value = direccion)
            if (latitud.isNotBlank()) InfoRow(label = stringResource(R.string.wizard_latitude), value = latitud)
            if (longitud.isNotBlank()) InfoRow(label = stringResource(R.string.wizard_longitude), value = longitud)
        }

        if (fotoUri != null) {
            Text(
                text = stringResource(R.string.wizard_step4_title),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            AsyncImage(
                model = fotoUri,
                contentDescription = stringResource(R.string.census_photo_cd),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(MaterialTheme.shapes.medium),
            )
        }

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun ResumenSeccion(
    titulo: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = MaterialTheme.shapes.medium,
            )
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        content()
    }
}

// ─── Helpers comunes ──────────────────────────────────────────────────────────

@Composable
private fun PasoTitulo(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(bottom = 4.dp),
    )
}

@Composable
private fun CampoTexto(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    requerido: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(text = if (requerido) "$label *" else label)
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
    )
}

@Suppress("MissingPermission")
private fun obtenerUbicacion(
    context: android.content.Context,
    onResult: (Double, Double) -> Unit,
) {
    val client = LocationServices.getFusedLocationProviderClient(context)
    client.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            onResult(location.latitude, location.longitude)
        }
    }
}

// ─── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun WizardStepIndicatorPreview() {
    TuluaTheme {
        WizardStepIndicator(
            totalSteps = 5,
            currentStep = 2,
            modifier = Modifier.padding(16.dp),
        )
    }
}
