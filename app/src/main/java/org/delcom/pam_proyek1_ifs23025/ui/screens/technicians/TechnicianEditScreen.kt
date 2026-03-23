package org.delcom.pam_proyek1_ifs23025.ui.screens.technicians

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import org.delcom.pam_proyek1_ifs23025.helper.AlertHelper
import org.delcom.pam_proyek1_ifs23025.helper.AlertState
import org.delcom.pam_proyek1_ifs23025.helper.AlertType
import org.delcom.pam_proyek1_ifs23025.helper.ConstHelper
import org.delcom.pam_proyek1_ifs23025.helper.RouteHelper
import org.delcom.pam_proyek1_ifs23025.helper.SuspendHelper
import org.delcom.pam_proyek1_ifs23025.helper.SuspendHelper.SnackBarType
import org.delcom.pam_proyek1_ifs23025.network.technicians.data.TeknisiEnum
import org.delcom.pam_proyek1_ifs23025.network.technicians.data.ResponseTechnicianData
import org.delcom.pam_proyek1_ifs23025.ui.components.BottomNavComponent
import org.delcom.pam_proyek1_ifs23025.ui.components.LoadingUI
import org.delcom.pam_proyek1_ifs23025.ui.components.TopAppBarComponent
import org.delcom.pam_proyek1_ifs23025.ui.viewmodels.AuthUIState
import org.delcom.pam_proyek1_ifs23025.ui.viewmodels.AuthViewModel
import org.delcom.pam_proyek1_ifs23025.ui.viewmodels.TechnicianActionUIState
import org.delcom.pam_proyek1_ifs23025.ui.viewmodels.TechnicianUIState
import org.delcom.pam_proyek1_ifs23025.ui.viewmodels.TechnicianViewModel

@Composable
fun TechniciansEditScreen(
    navController: NavHostController,
    snackbarHost: SnackbarHostState,
    authViewModel: AuthViewModel,
    technicianViewModel: TechnicianViewModel,
    technicianId: String
) {
    val uiStateAuth by authViewModel.uiState.collectAsState()
    val uiStateTechnician by technicianViewModel.uiState.collectAsState()

    var isLoading by remember { mutableStateOf(false) }
    var technician by remember { mutableStateOf<ResponseTechnicianData?>(null) }
    val authToken = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        isLoading = true

        if(uiStateAuth.auth !is AuthUIState.Success){
            RouteHelper.to(navController, ConstHelper.RouteNames.Home.path, true)
            return@LaunchedEffect
        }

        authToken.value = (uiStateAuth.auth as AuthUIState.Success).data.authToken
        uiStateTechnician.technician = TechnicianUIState.Loading
        uiStateTechnician.technicianChange = TechnicianActionUIState.Loading

        technicianViewModel.getTechnicianById(authToken.value!!, technicianId)
    }

    LaunchedEffect(uiStateTechnician.technician) {
        if (uiStateTechnician.technician !is TechnicianUIState.Loading) {
            if (uiStateTechnician.technician is TechnicianUIState.Success) {
                technician = (uiStateTechnician.technician as TechnicianUIState.Success).data
                isLoading = false
            } else {
                RouteHelper.back(navController)
                isLoading = false
            }
        }
    }

    fun onSave(
        title: String,
        description: String,
        status: String,
        tanggalDiterima: String,
        namaPemilik: String,
        estimasiBiaya: String,
        teknisi: String
    ) {
        isLoading = true

        technicianViewModel.putTechnician(
            authToken = authToken.value!!,
            technicianId = technicianId,
            title = title,
            description = description,
            status = status,
            tanggalDiterima = tanggalDiterima,
            namaPemilik = namaPemilik,
            estimasiBiaya = estimasiBiaya,
            teknisi = teknisi
        )
    }

    LaunchedEffect(uiStateTechnician.technicianChange) {
        when (val state = uiStateTechnician.technicianChange) {
            is TechnicianActionUIState.Success -> {
                SuspendHelper.showSnackBar(snackbarHost, SnackBarType.SUCCESS, state.message)
                RouteHelper.to(
                    navController = navController,
                    destination = ConstHelper.RouteNames.TechniciansDetail.path.replace("{technicianId}", technicianId),
                    popUpTo = ConstHelper.RouteNames.TechniciansDetail.path.replace("{technicianId}", technicianId),
                    removeBackStack = true
                )
                isLoading = false
            }
            is TechnicianActionUIState.Error -> {
                SuspendHelper.showSnackBar(snackbarHost, SnackBarType.ERROR, state.message)
                isLoading = false
            }
            else -> {}
        }
    }

    if (isLoading || technician == null) {
        LoadingUI()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.03f),
                        MaterialTheme.colorScheme.background
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        TopAppBarComponent(
            navController = navController,
            title = "Ubah Data Service",
            showBackButton = true,
        )
        Box(modifier = Modifier.weight(1f)) {
            TechniciansEditUI(technician = technician!!, onSave = ::onSave)
        }
        BottomNavComponent(navController = navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechniciansEditUI(
    technician: ResponseTechnicianData,
    onSave: (String, String, String, String, String, String, String) -> Unit
) {
    val alertState = remember { mutableStateOf(AlertState()) }
    val headerScale = remember { Animatable(0.9f) }

    var dataTitle by remember { mutableStateOf(technician.title ?: "") }
    var dataDescription by remember { mutableStateOf(technician.description ?: "") }
    var dataTanggal by remember { mutableStateOf(technician.tanggalDiterima ?: "") }
    var dataNama by remember { mutableStateOf(technician.namaPemilik ?: "") }
    var dataBiaya by remember { mutableStateOf(technician.estimasiBiaya ?: "") }
    var dataTeknisi by remember { mutableStateOf(technician.teknisi ?: "") }
    var dataStatus by remember { mutableStateOf(technician.status ?: "Kerusakan Ringan") }

    var expandedTeknisi by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        headerScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500)
        )
        delay(100)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // LUXURY HEADER SECTION with EDIT INDICATOR
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .scale(headerScale.value)
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(32.dp),
                    clip = false,
                    spotColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                    ambientColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                )
                .clip(RoundedCornerShape(32.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f),
                            MaterialTheme.colorScheme.secondary
                        )
                    ),
                    shape = RoundedCornerShape(32.dp)
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Decorative Elements
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .shadow(15.dp, CircleShape, clip = true)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.3f),
                                    Color.White.copy(alpha = 0.1f)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Valvoline Service",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 12.sp,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color.White.copy(alpha = 0.8f)
                )

                Text(
                    text = "Perbarui Data Kerusakan Barang",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.2f),
                            offset = Offset(2f, 2f),
                            blurRadius = 4f
                        )
                    ),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Ubah informasi dengan teliti untuk akurasi data",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // FORM CARD with PREMIUM DESIGN
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(28.dp),
                    spotColor = Color.Black.copy(alpha = 0.15f),
                    ambientColor = Color.Black.copy(alpha = 0.1f)
                ),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(0.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Section Header dengan Icon Premium
                LuxuryEditSectionHeader(
                    title = "Informasi Barang",
                    icon = Icons.Outlined.Verified,
                    color = MaterialTheme.colorScheme.secondary
                )

                // Nama Barang - PREMIUM TEXTFIELD
                LuxuryEditTextField(
                    value = dataTitle,
                    onValueChange = { dataTitle = it },
                    label = "Nama Barang / Alat",
                    leadingIcon = Icons.Default.Engineering,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth()
                )

                // Deskripsi Kerusakan
                LuxuryEditTextField(
                    value = dataDescription,
                    onValueChange = { dataDescription = it },
                    label = "Deskripsi Kerusakan",
                    leadingIcon = Icons.Default.Info,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 5,
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Section Header
                LuxuryEditSectionHeader(
                    title = "Detail Pemilik & Teknisi",
                    icon = Icons.Default.Person,
                    color = MaterialTheme.colorScheme.secondary
                )

                // Nama Pemilik
                LuxuryEditTextField(
                    value = dataNama,
                    onValueChange = { dataNama = it },
                    label = "Nama Pemilik",
                    leadingIcon = Icons.Default.Person,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth()
                )

                // Tanggal Diterima
                LuxuryEditTextField(
                    value = dataTanggal,
                    onValueChange = { dataTanggal = it },
                    label = "Tanggal Diterima",
                    placeholder = "Contoh: 9 February 2026",
                    leadingIcon = Icons.Default.DateRange,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth()
                )

                // Estimasi Biaya
                LuxuryEditTextField(
                    value = dataBiaya,
                    onValueChange = { dataBiaya = it },
                    label = "Estimasi Biaya",
                    placeholder = "Contoh: 250000",
                    leadingIcon = Icons.Default.AttachMoney,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Teknisi Dropdown - PREMIUM
                LuxuryEditDropdownField(
                    value = dataTeknisi,
                    onValueChange = { dataTeknisi = it },
                    expanded = expandedTeknisi,
                    onExpandedChange = { expandedTeknisi = it },
                    label = "Teknisi Penanggung Jawab",
                    leadingIcon = Icons.Default.Group,
                    options = TeknisiEnum.getAllFullNames(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // STATUS CARD with GRADIENT and PREMIUM STYLING
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(28.dp),
                    spotColor = Color.Black.copy(alpha = 0.15f)
                ),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(0.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                LuxuryEditSectionHeader(
                    title = "Tingkat Kerusakan",
                    icon = Icons.Default.Warning,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(12.dp))

                // PREMIUM STATUS OPTIONS
                val statusOptions = listOf(
                    StatusEditOption("Kerusakan Ringan", MaterialTheme.colorScheme.tertiary, "R"),
                    StatusEditOption("Kerusakan Sedang", MaterialTheme.colorScheme.secondary, "S"),
                    StatusEditOption("Kerusakan Berat", MaterialTheme.colorScheme.error, "B")
                )

                statusOptions.forEachIndexed { index, option ->
                    val isSelected = dataStatus == option.label

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                color = if (isSelected)
                                    option.color.copy(alpha = 0.15f)
                                else Color.Transparent,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { dataStatus = option.label }
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Premium Radio Button
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(
                                        color = if (isSelected) option.color
                                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(
                                                color = Color.White,
                                                shape = CircleShape
                                            )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Status Badge
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                option.color.copy(alpha = 0.2f),
                                                option.color.copy(alpha = 0.1f)
                                            )
                                        ),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = option.initial,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = option.color
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = option.label,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) option.color else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )

                            if (isSelected) {
                                Icon(
                                    Icons.Outlined.Verified,
                                    contentDescription = null,
                                    tint = option.color,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    if (index < statusOptions.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // PREMIUM UPDATE BUTTON with GRADIENT
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(30.dp),
                    spotColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                )
                .clip(RoundedCornerShape(30.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.secondary,
                            MaterialTheme.colorScheme.primary
                        )
                    ),
                    shape = RoundedCornerShape(30.dp)
                )
                .clickable {
                    if (dataTitle.isBlank() || dataDescription.isBlank() || dataTanggal.isBlank() ||
                        dataNama.isBlank() || dataBiaya.isBlank() || dataTeknisi.isBlank()
                    ) {
                        AlertHelper.show(alertState, AlertType.ERROR, "Semua data wajib diisi!")
                        return@clickable
                    }
                    onSave(
                        dataTitle,
                        dataDescription,
                        dataStatus,
                        dataTanggal,
                        dataNama,
                        dataBiaya,
                        dataTeknisi
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = "Simpan",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "UPDATE DATA SERVICE",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }

    // PREMIUM ALERT DIALOG
    if (alertState.value.isVisible) {
        AlertDialog(
            onDismissRequest = { AlertHelper.dismiss(alertState) },
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                color = when (alertState.value.type) {
                                    AlertType.ERROR -> MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                                    AlertType.SUCCESS -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                                    else -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                                },
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (alertState.value.type) {
                                AlertType.ERROR -> Icons.Default.Warning
                                AlertType.SUCCESS -> Icons.Outlined.Verified
                                else -> Icons.Default.Edit
                            },
                            contentDescription = null,
                            tint = when (alertState.value.type) {
                                AlertType.ERROR -> MaterialTheme.colorScheme.error
                                AlertType.SUCCESS -> MaterialTheme.colorScheme.tertiary
                                else -> MaterialTheme.colorScheme.secondary
                            },
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = alertState.value.type.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Text(
                    text = alertState.value.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = { AlertHelper.dismiss(alertState) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when (alertState.value.type) {
                            AlertType.ERROR -> MaterialTheme.colorScheme.error
                            AlertType.SUCCESS -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.secondary
                        }
                    )
                ) {
                    Text("Mengerti", fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(28.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp
        )
    }
}

// PREMIUM COMPONENTS for EDIT

data class StatusEditOption(
    val label: String,
    val color: Color,
    val initial: String
)

@Composable
fun LuxuryEditSectionHeader(
    title: String,
    icon: ImageVector,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    color = color.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.3.sp
            ),
            color = color
        )
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(1.dp)
                .background(color = color.copy(alpha = 0.3f))
        )
    }
}

@Composable
fun LuxuryEditTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier,
    maxLines: Int = 1,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        },
        placeholder = if (placeholder.isNotEmpty()) {
            { Text(placeholder, style = MaterialTheme.typography.bodyMedium) }
        } else null,
        leadingIcon = {
            Icon(
                leadingIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
        },
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.secondary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            focusedLabelColor = MaterialTheme.colorScheme.secondary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.secondary,
            focusedLeadingIconColor = MaterialTheme.colorScheme.secondary,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        ),
        keyboardOptions = keyboardOptions,
        modifier = modifier,
        maxLines = maxLines,
        minLines = minLines,
        singleLine = maxLines == 1
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LuxuryEditDropdownField(
    value: String,
    onValueChange: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    options: List<String>,
    modifier: Modifier = Modifier
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = {
                Text(
                    label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            },
            leadingIcon = {
                Icon(
                    leadingIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.secondary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                focusedLabelColor = MaterialTheme.colorScheme.secondary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                cursorColor = MaterialTheme.colorScheme.secondary
            ),
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            singleLine = true
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .shadow(8.dp)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (value == option) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onValueChange(option)
                        onExpandedChange(false)
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun PreviewTechniciansEditUI() {
    MaterialTheme {
        TechniciansEditUI(
            technician = ResponseTechnicianData(
                id = "1",
                userId = "user1",
                title = "AC Rusak",
                description = "AC tidak mengeluarkan udara dingin",
                status = "Kerusakan Sedang",
                tanggalDiterima = "15 March 2026",
                namaPemilik = "John Doe",
                estimasiBiaya = "350000",
                teknisi = "Teknisi AC",
                createdAt = "",
                updatedAt = ""
            ),
            onSave = { _, _, _, _, _, _, _ -> }
        )
    }
}