package org.delcom.pam_proyek1_ifs23025.ui.screens.technicians

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import org.delcom.pam_proyek1_ifs23025.R
import org.delcom.pam_proyek1_ifs23025.helper.ConstHelper
import org.delcom.pam_proyek1_ifs23025.helper.RouteHelper
import org.delcom.pam_proyek1_ifs23025.helper.ToolsHelper
import org.delcom.pam_proyek1_ifs23025.network.technicians.data.TeknisiEnum
import org.delcom.pam_proyek1_ifs23025.network.technicians.data.ResponseTechnicianData
import org.delcom.pam_proyek1_ifs23025.ui.components.BottomNavComponent
import org.delcom.pam_proyek1_ifs23025.ui.components.LoadingUI
import org.delcom.pam_proyek1_ifs23025.ui.components.TopAppBarComponent
import org.delcom.pam_proyek1_ifs23025.ui.components.TopAppBarMenuItem
import org.delcom.pam_proyek1_ifs23025.ui.viewmodels.AuthLogoutUIState
import org.delcom.pam_proyek1_ifs23025.ui.viewmodels.AuthUIState
import org.delcom.pam_proyek1_ifs23025.ui.viewmodels.AuthViewModel
import org.delcom.pam_proyek1_ifs23025.ui.viewmodels.TechnicianViewModel
import org.delcom.pam_proyek1_ifs23025.ui.viewmodels.TechniciansUIState
import java.util.UUID

// Warna Biru yang Elegan
private val DarkBlue = Color(0xFF0A2F6C)      // Biru tua elegan
private val SkyBlue = Color(0xFF3B82F6)       // Biru langit
private val LightSky = Color(0xFF93C5FD)      // Biru langit muda
private val GradientBlue = listOf(
    DarkBlue,
    SkyBlue,
    LightSky
)

@Composable
fun TechniciansScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    technicianViewModel: TechnicianViewModel
) {
    val uiStateAuth by authViewModel.uiState.collectAsState()
    val uiStateTechnician by technicianViewModel.uiState.collectAsState()

    var isLoading by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    var selectedStatus by remember { mutableStateOf<String?>(null) }
    var selectedTeknisi by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()

    var technicians by remember { mutableStateOf<List<ResponseTechnicianData>>(emptyList()) }
    var authToken by remember { mutableStateOf<String?>(null) }

    fun fetchTechniciansData() {
        val authState = uiStateAuth.auth
        if (authState is AuthUIState.Success) {
            isLoading = true
            authToken = authState.data.authToken
            technicianViewModel.resetAndGetAllTechnicians(authToken ?: "", searchQuery.text, selectedStatus, selectedTeknisi)
        }
    }

    LaunchedEffect(uiStateAuth.auth) {
        val authState = uiStateAuth.auth
        if (authState is AuthUIState.Success) {
            fetchTechniciansData()
        } else if (authState is AuthUIState.Error) {
            RouteHelper.to(navController, ConstHelper.RouteNames.AuthLogin.path, true)
        }
    }

    val shouldLoadMore = remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItem >= (totalItems - 2) && totalItems > 0
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && uiStateTechnician.technicians !is TechniciansUIState.Loading) {
            technicianViewModel.getAllTechnicians(authToken ?: "", searchQuery.text, selectedStatus, selectedTeknisi)
        }
    }

    LaunchedEffect(uiStateTechnician.technicians) {
        if (uiStateTechnician.technicians !is TechniciansUIState.Loading) {
            isLoading = false
            technicians = if (uiStateTechnician.technicians is TechniciansUIState.Success) {
                (uiStateTechnician.technicians as TechniciansUIState.Success).data
            } else {
                emptyList()
            }
        }
    }

    fun onLogout(token: String){
        isLoading = true
        authViewModel.logout(token)
    }

    LaunchedEffect(uiStateAuth.authLogout) {
        if (uiStateAuth.authLogout !is AuthLogoutUIState.Loading) {
            RouteHelper.to(navController, ConstHelper.RouteNames.AuthLogin.path, true)
        }
    }

    if (isLoading && technicians.isEmpty()) {
        LoadingUI()
        return
    }

    val menuItems = listOf(
        TopAppBarMenuItem(text = "Profile", icon = Icons.Filled.Person, route = ConstHelper.RouteNames.Profile.path),
        TopAppBarMenuItem(text = "Logout", icon = Icons.AutoMirrored.Filled.Logout, route = null, onClick = { onLogout(authToken ?: "") })
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DarkBlue,
                        SkyBlue,
                        LightSky
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        TopAppBarComponent(
            navController = navController,
            title = "Daftar Service",
            showBackButton = false,
            customMenuItems = menuItems,
            withSearch = true,
            searchQuery = searchQuery,
            onSearchQueryChange = { query -> searchQuery = query },
            onSearchAction = { fetchTechniciansData() }
        )

        // PREMIUM FILTER SECTION
        Column(modifier = Modifier.fillMaxWidth()) {
            // Filter Header dengan Statistik
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter & Kategori",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "${technicians.size} Item",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            // Filter Status dengan Premium Style
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val statuses = listOf(
                    null to "Semua Status",
                    "Kerusakan Ringan" to "Ringan",
                    "Kerusakan Sedang" to "Sedang",
                    "Kerusakan Berat" to "Berat"
                )
                items(statuses) { (key, label) ->
                    val isSelected = selectedStatus == key
                    val statusColor = when (key) {
                        "Kerusakan Ringan" -> Color(0xFF10B981) // Hijau
                        "Kerusakan Sedang" -> Color(0xFFF59E0B) // Oranye
                        "Kerusakan Berat" -> Color(0xFFEF4444)  // Merah
                        else -> Color.Blue
                    }

                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedStatus = key; fetchTechniciansData() },
                        label = {
                            Text(
                                label,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else statusColor
                            )
                        },
                        shape = RoundedCornerShape(40.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = statusColor,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White.copy(alpha = 0.9f),
                            labelColor = statusColor
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = statusColor.copy(alpha = 0.5f),
                            selectedBorderColor = statusColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Filter Teknisi dengan Premium Style
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedTeknisi == null,
                        onClick = { selectedTeknisi = null; fetchTechniciansData() },
                        label = {
                            Text(
                                "Semua Teknisi",
                                fontWeight = if (selectedTeknisi == null) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTeknisi == null) Color.White else SkyBlue
                            )
                        },
                        shape = RoundedCornerShape(40.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SkyBlue,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White.copy(alpha = 0.9f),
                            labelColor = SkyBlue
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedTeknisi == null,
                            borderColor = SkyBlue.copy(alpha = 0.5f),
                            selectedBorderColor = SkyBlue
                        )
                    )
                }
                itemsIndexed(TeknisiEnum.entries) { index, teknisi ->
                    val isSelected = selectedTeknisi == teknisi.fullName
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedTeknisi = teknisi.fullName; fetchTechniciansData() },
                        label = {
                            Text(
                                teknisi.shortName,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else SkyBlue
                            )
                        },
                        shape = RoundedCornerShape(40.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SkyBlue,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White.copy(alpha = 0.9f),
                            labelColor = SkyBlue
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = SkyBlue.copy(alpha = 0.5f),
                            selectedBorderColor = SkyBlue
                        )
                    )
                }
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            PremiumTechniciansUI(
                technicians = technicians,
                onOpen = { id -> RouteHelper.to(navController, "technicians/$id") },
                listState = listState
            )

            // PREMIUM FAB WITH GRADIENT
            Box(modifier = Modifier.fillMaxSize()) {
                FloatingActionButton(
                    onClick = { RouteHelper.to(navController, ConstHelper.RouteNames.TechniciansAdd.path) },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(24.dp)
                        .size(64.dp)
                        .shadow(
                            elevation = 12.dp,
                            shape = CircleShape,
                            spotColor = SkyBlue.copy(alpha = 0.5f),
                            ambientColor = SkyBlue.copy(alpha = 0.3f)
                        ),
                    shape = CircleShape,
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(SkyBlue, DarkBlue)
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah Barang Rusak",
                            modifier = Modifier.size(32.dp),
                            tint = Color.White
                        )
                    }
                }
            }
        }
        BottomNavComponent(navController = navController)
    }
}

@Composable
fun PremiumTechniciansUI(
    technicians: List<ResponseTechnicianData>,
    onOpen: (String) -> Unit,
    listState: LazyListState
) {
    if (technicians.isEmpty()) {
        // Premium Empty State
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Inbox,
                    contentDescription = "Kosong",
                    modifier = Modifier.size(72.dp),
                    tint = Color.White.copy(alpha = 0.8f)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Belum Ada Data Service",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Klik tombol + untuk menambahkan\nlayanan service baru",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(
                items = technicians,
                key = { it.id ?: UUID.randomUUID().toString() }
            ) { technician ->
                PremiumTechnicianItemUI(technician, onOpen)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun PremiumTechnicianItemUI(
    technician: ResponseTechnicianData,
    onOpen: (String) -> Unit
) {
    val scaleAnim = remember { Animatable(0.95f) }
    val safeId = technician.id ?: ""
    val safeTitle = technician.title ?: "Tanpa Judul"
    val safeTeknisi = technician.teknisi ?: "-"
    val safeTanggal = technician.tanggalDiterima ?: "-"
    val safeUpdatedAt = technician.updatedAt ?: "0"
    val safeStatus = technician.status ?: "Kerusakan Ringan"

    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 300)
        )
        delay(50)
    }

    val statusColor = when (safeStatus.lowercase()) {
        "Kerusakan Sedang" -> Color(0xFFF59E0B) // Oranye
        "Kerusakan Berat" -> Color(0xFFEF4444)  // Merah
        else -> Color(0xFF10B981)               // Hijau
    }

    val statusInitial = when (safeStatus.lowercase()) {
        "Kerusakan Sedang" -> "S"
        "Kerusakan Berat" -> "B"
        else -> "R"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scaleAnim.value)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = Color.Black.copy(alpha = 0.12f),
                ambientColor = Color.Black.copy(alpha = 0.08f)
            )
            .clickable { onOpen(safeId) },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Premium Image with Gradient Overlay
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .shadow(4.dp, RoundedCornerShape(20.dp))
            ) {
                AsyncImage(
                    model = ToolsHelper.getTechnicianImage(safeId, safeUpdatedAt),
                    contentDescription = safeTitle,
                    placeholder = painterResource(R.drawable.img_placeholder),
                    error = painterResource(R.drawable.img_placeholder),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Gradient Overlay untuk efek premium
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.3f)
                                )
                            )
                        )
                )

                // Status Badge di pojok
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(statusColor, statusColor.copy(alpha = 0.8f))
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = statusInitial,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Info Teks Premium
            Column(modifier = Modifier.weight(1f)) {
                // Title Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = safeTitle,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = DarkBlue,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Teknisi Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                color = SkyBlue.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = safeTeknisi,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = SkyBlue,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tanggal dengan Icon
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📅",
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = safeTanggal,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Status dengan Progress Bar Style
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(
                            color = statusColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(3.dp)
                        )
                ) {
                    val progress = when (safeStatus.lowercase()) {
                        "Kerusakan Ringan" -> 0.33f
                        "Kerusakan Sedang" -> 0.66f
                        "Kerusakan Berat" -> 1f
                        else -> 0.33f
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxSize()
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(statusColor, statusColor.copy(alpha = 0.7f))
                                ),
                                shape = RoundedCornerShape(3.dp)
                            )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun PreviewTechniciansUI() {
    MaterialTheme {
        // Preview implementation
    }
}