package org.delcom.pam_proyek1_ifs23025.ui.screens.technicians

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Save
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
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
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBarComponent(
            navController = navController,
            title = "Ubah Data Kegiatan",
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

    var dataTitle by remember { mutableStateOf(technician.title ?: "") }
    var dataDescription by remember { mutableStateOf(technician.description ?: "") }
    var dataTanggal by remember { mutableStateOf(technician.tanggalDiterima ?: "") }
    var dataNama by remember { mutableStateOf(technician.namaPemilik ?: "") }
    var dataBiaya by remember { mutableStateOf(technician.estimasiBiaya ?: "") }
    var dataTeknisi by remember { mutableStateOf(technician.teknisi ?: "") }
    var dataStatus by remember { mutableStateOf(technician.status ?: "belum terlaksana") }

    var expandedTeknisi by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Informasi Service",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                // Title
                OutlinedTextField(
                    value = dataTitle,
                    onValueChange = { dataTitle = it },
                    label = { Text("Nama Barang") },
                    leadingIcon = { Icon(Icons.Default.Engineering, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                // Tanggal Pelaksanaan
                OutlinedTextField(
                    value = dataTanggal,
                    onValueChange = { dataTanggal = it },
                    label = { Text("Tanggal Diterima") },
                    leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                // Tempat Pelaksanaan
                OutlinedTextField(
                    value = dataNama,
                    onValueChange = { dataNama = it },
                    label = { Text("Nama Pemilik") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                // Estimasi Biaya
                OutlinedTextField(
                    value = dataBiaya,
                    onValueChange = { dataBiaya = it },
                    label = { Text("Estimasi Biaya (Rp)") },
                    leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                )

                // Divisi (Dropdown)
                ExposedDropdownMenuBox(
                    expanded = expandedTeknisi,
                    onExpandedChange = { expandedTeknisi = !expandedTeknisi },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = dataTeknisi,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Teknisi Penanggung Jawab") },
                        leadingIcon = { Icon(Icons.Default.Group, contentDescription = null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTeknisi) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedTeknisi,
                        onDismissRequest = { expandedTeknisi = false }
                    ) {
                        TeknisiEnum.getAllFullNames().forEach { teknisiName ->
                            DropdownMenuItem(
                                text = { Text(text = teknisiName) },
                                onClick = {
                                    dataTeknisi = teknisiName
                                    expandedTeknisi = false
                                }
                            )
                        }
                    }
                }

                // Description
                OutlinedTextField(
                    value = dataDescription,
                    onValueChange = { dataDescription = it },
                    label = { Text("Deskripsi Kegiatan") },
                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    maxLines = 5,
                    minLines = 3
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pilihan Status Kegiatan
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Status Kegiatan",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val statusOptions = listOf("belum terlaksana", "sudah terlaksana", "dibatalkan")
                    statusOptions.forEach { option ->
                        val isSelected = dataStatus == option
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                .clickable { dataStatus = option }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = null
                            )
                            Text(
                                text = option.replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tombol Simpan Besar di Bawah
        Button(
            onClick = {
                if (dataTitle.isBlank() || dataDescription.isBlank() || dataTanggal.isBlank() ||
                    dataNama.isBlank() || dataBiaya.isBlank() || dataTeknisi.isBlank()
                ) {
                    AlertHelper.show(alertState, AlertType.ERROR, "Semua data wajib diisi!")
                    return@Button
                }
                onSave(dataTitle, dataDescription, dataStatus, dataTanggal, dataNama, dataBiaya, dataTeknisi)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(imageVector = Icons.Default.Save, contentDescription = "Simpan")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Simpan Perubahan", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(100.dp))
    }

    if (alertState.value.isVisible) {
        AlertDialog(
            onDismissRequest = { AlertHelper.dismiss(alertState) },
            title = { Text(alertState.value.type.title) },
            text = { Text(alertState.value.message) },
            confirmButton = {
                TextButton(onClick = { AlertHelper.dismiss(alertState) }) { Text("OK") }
            }
        )
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun PreviewTechniciansEditUI() {
}