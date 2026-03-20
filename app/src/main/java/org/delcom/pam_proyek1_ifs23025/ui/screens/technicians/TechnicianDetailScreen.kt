package org.delcom.pam_proyek1_ifs23025.ui.screens.technicians

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import org.delcom.pam_proyek1_ifs23025.R
import org.delcom.pam_proyek1_ifs23025.helper.ConstHelper
import org.delcom.pam_proyek1_ifs23025.helper.RouteHelper
import org.delcom.pam_proyek1_ifs23025.helper.SuspendHelper
import org.delcom.pam_proyek1_ifs23025.helper.SuspendHelper.SnackBarType
import org.delcom.pam_proyek1_ifs23025.helper.ToolsHelper
import org.delcom.pam_proyek1_ifs23025.helper.ToolsHelper.uriToMultipart
import org.delcom.pam_proyek1_ifs23025.network.technicians.data.ResponseTechnicianData
import org.delcom.pam_proyek1_ifs23025.ui.components.BottomDialog
import org.delcom.pam_proyek1_ifs23025.ui.components.BottomDialogType
import org.delcom.pam_proyek1_ifs23025.ui.components.BottomNavComponent
import org.delcom.pam_proyek1_ifs23025.ui.components.LoadingUI
import org.delcom.pam_proyek1_ifs23025.ui.components.TopAppBarComponent
import org.delcom.pam_proyek1_ifs23025.ui.components.TopAppBarMenuItem
import org.delcom.pam_proyek1_ifs23025.ui.theme.DelcomTheme
import org.delcom.pam_proyek1_ifs23025.ui.viewmodels.AuthUIState
import org.delcom.pam_proyek1_ifs23025.ui.viewmodels.AuthViewModel
import org.delcom.pam_proyek1_ifs23025.ui.viewmodels.TechnicianActionUIState
import org.delcom.pam_proyek1_ifs23025.ui.viewmodels.TechnicianUIState
import org.delcom.pam_proyek1_ifs23025.ui.viewmodels.TechnicianViewModel

@Composable
fun TechniciansDetailScreen(
    navController: NavHostController,
    snackbarHost: SnackbarHostState,
    authViewModel: AuthViewModel,
    technicianViewModel: TechnicianViewModel,
    technicianId: String
) {
    val uiStateTechnician by technicianViewModel.uiState.collectAsState()
    val uiStateAuth by authViewModel.uiState.collectAsState()

    var isLoading by remember { mutableStateOf(false) }
    var isConfirmDelete by remember { mutableStateOf(false) }

    var technician by remember { mutableStateOf<ResponseTechnicianData?>(null) }
    val authToken = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        isLoading = true

        if (uiStateAuth.auth !is AuthUIState.Success) {
            RouteHelper.to(navController, ConstHelper.RouteNames.Home.path, true)
            return@LaunchedEffect
        }

        authToken.value = (uiStateAuth.auth as AuthUIState.Success).data.authToken

        uiStateTechnician.technicianDelete = TechnicianActionUIState.Loading
        uiStateTechnician.technicianChangeCover = TechnicianActionUIState.Loading
        uiStateTechnician.technician = TechnicianUIState.Loading

        technicianViewModel.getTechnicianById(authToken.value!!, technicianId)
    }

    LaunchedEffect(uiStateTechnician.technician) {
        if (uiStateTechnician.technician !is TechnicianUIState.Loading) {
            if (uiStateTechnician.technician is TechnicianUIState.Success) {
                technician = (uiStateTechnician.technician as TechnicianUIState.Success).data
                isLoading = false
            } else {
                RouteHelper.back(navController)
            }
        }
    }

    fun onDelete() {
        if (authToken.value == null) return
        uiStateTechnician.technicianDelete = TechnicianActionUIState.Loading
        isLoading = true
        technicianViewModel.deleteTechnician(authToken.value!!, technicianId)
    }

    LaunchedEffect(uiStateTechnician.technicianDelete) {
        when (val state = uiStateTechnician.technicianDelete) {
            is TechnicianActionUIState.Success -> {
                SuspendHelper.showSnackBar(snackbarHost, SnackBarType.SUCCESS, state.message)
                RouteHelper.to(navController, ConstHelper.RouteNames.Technicians.path, true)
                uiStateTechnician.technician = TechnicianUIState.Loading
                isLoading = false
            }
            is TechnicianActionUIState.Error -> {
                SuspendHelper.showSnackBar(snackbarHost, SnackBarType.ERROR, state.message)
                isLoading = false
            }
            else -> {}
        }
    }

    fun onChangeCover(context: Context, file: Uri) {
        if (authToken.value == null) return
        uiStateTechnician.technicianChangeCover = TechnicianActionUIState.Loading
        isLoading = true
        val filePart = uriToMultipart(context, file, "file")
        technicianViewModel.putTechnicianCover(authToken.value!!, technicianId, filePart!!)
    }

    LaunchedEffect(uiStateTechnician.technicianChangeCover) {
        when (val state = uiStateTechnician.technicianChangeCover) {
            is TechnicianActionUIState.Success -> {
                technician?.updatedAt = System.currentTimeMillis().toString()
                SuspendHelper.showSnackBar(snackbarHost, SnackBarType.SUCCESS, state.message)
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

    val safeTechnicianId = technician?.id ?: ""

    val detailMenuItems = listOf(
        TopAppBarMenuItem(
            text = "Ubah Data",
            icon = Icons.Filled.Edit,
            route = null,
            onClick = {
                RouteHelper.to(navController, ConstHelper.RouteNames.TechniciansEdit.path.replace("{technicianId}", safeTechnicianId))
            }
        ),
        TopAppBarMenuItem(
            text = "Hapus Data",
            icon = Icons.Filled.Delete,
            route = null,
            onClick = { isConfirmDelete = true }
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBarComponent(
            navController = navController,
            title = "Detail Kegiatan",
            showBackButton = true,
            customMenuItems = detailMenuItems
        )
        Box(modifier = Modifier.weight(1f)) {
            TechniciansDetailUI(
                technician = technician!!,
                onChangeCover = ::onChangeCover,
            )
            BottomDialog(
                type = BottomDialogType.ERROR,
                show = isConfirmDelete,
                onDismiss = { isConfirmDelete = false },
                title = "Konfirmasi Hapus Data",
                message = "Apakah Anda yakin ingin menghapus data ini?",
                confirmText = "Ya, Hapus",
                onConfirm = { onDelete() },
                cancelText = "Batal",
                destructiveAction = true
            )
        }
        BottomNavComponent(navController = navController)
    }
}

@Composable
fun TechniciansDetailUI(
    technician: ResponseTechnicianData,
    onChangeCover: (context: Context, file: Uri) -> Unit,
) {
    var dataFile by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val safeId = technician.id ?: ""
    val safeTitle = technician.title ?: "Tanpa Judul"
    val safeStatus = technician.status ?: "belum terlaksana"
    val safeTeknisi = technician.teknisi ?: "-"
    val safeTanggal = technician.tanggalDiterima ?: "-"
    val safeNama = technician.namaPemilik ?: "-"
    val safeEstimasi = technician.estimasiBiaya ?: "0"
    val safeDeskripsi = technician.description ?: "-"
    val safeUpdatedAt = technician.updatedAt ?: "0"

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        dataFile = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. HEADER MELENGKUNG DAN COVER BANNER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            // Latar Belakang Biru Melengkung
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )

            // Banner Cover Mengambang
            Card(
                modifier = Modifier
                    .padding(top = 64.dp)
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .height(180.dp)
                    .clickable {
                        imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = dataFile ?: ToolsHelper.getTechnicianImage(safeId, safeUpdatedAt),
                        contentDescription = "Cover Kegiatan",
                        placeholder = painterResource(R.drawable.img_placeholder),
                        error = painterResource(R.drawable.img_placeholder),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Ikon Kamera di Pojok Bawah
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .size(36.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Ganti Cover",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Tombol Simpan (Hanya Muncul Jika Gambar Baru Dipilih)
        if (dataFile != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onChangeCover(context, dataFile!!) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth(0.6f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Text("Simpan Cover Baru", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. JUDUL DAN STATUS
        Text(
            text = safeTitle,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        val statusColor = when (safeStatus.lowercase()) {
            "sudah terlaksana" -> MaterialTheme.colorScheme.secondaryContainer
            "dibatalkan" -> MaterialTheme.colorScheme.errorContainer
            else -> MaterialTheme.colorScheme.tertiaryContainer
        }

        val statusTextColor = when (safeStatus.lowercase()) {
            "sudah terlaksana" -> MaterialTheme.colorScheme.onSecondaryContainer
            "dibatalkan" -> MaterialTheme.colorScheme.onErrorContainer
            else -> MaterialTheme.colorScheme.onTertiaryContainer
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(statusColor)
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                text = safeStatus.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = statusTextColor
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. CARD DETAIL INFORMASI
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                DetailItem(label = "Teknisi Penanggung Jawab", value = safeTeknisi)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))

                DetailItem(label = "Tanggal Diterima", value = safeTanggal.ifEmpty { "-" })
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))

                DetailItem(label = "Nama Pemilik", value = safeNama.ifEmpty { "-" })
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))

                DetailItem(label = "Estimasi Biaya", value = "Rp ${safeEstimasi.ifEmpty { "0" }}")
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))

                DetailItem(label = "Deskripsi Kegiatan", value = safeDeskripsi.ifEmpty { "-" })
            }
        }

        Spacer(modifier = Modifier.height(80.dp)) // Ruang untuk Bottom Nav
    }
}

// Fungsi Bantuan untuk Menampilkan Item Detail yang Rapi
@Composable
fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun PreviewTechniciansDetailUI() {
    DelcomTheme {
    }
}