package org.delcom.pam_proyek1_ifs23025.ui.viewmodels

import androidx.annotation.Keep
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

import org.delcom.pam_proyek1_ifs23025.network.technicians.data.RequestTechnician
import org.delcom.pam_proyek1_ifs23025.network.technicians.data.RequestUserChange
import org.delcom.pam_proyek1_ifs23025.network.technicians.data.RequestUserChangePassword
import org.delcom.pam_proyek1_ifs23025.network.technicians.data.ResponseTechnicianData
import org.delcom.pam_proyek1_ifs23025.network.technicians.data.ResponseTechnicianStatsData
import org.delcom.pam_proyek1_ifs23025.network.technicians.data.ResponseUserData
import org.delcom.pam_proyek1_ifs23025.network.technicians.service.ITechnicianRepository
import javax.inject.Inject

sealed interface ProfileUIState {
    data class Success(val data: ResponseUserData) : ProfileUIState
    data class Error(val message: String) : ProfileUIState
    object Loading : ProfileUIState
}

sealed interface TechniciansUIState {
    data class Success(val data: List<ResponseTechnicianData>) : TechniciansUIState
    data class Error(val message: String) : TechniciansUIState
    object Loading : TechniciansUIState
}

sealed interface TechnicianUIState {
    data class Success(val data: ResponseTechnicianData) : TechnicianUIState
    data class Error(val message: String) : TechnicianUIState
    object Loading : TechnicianUIState
}

sealed interface TechnicianActionUIState {
    data class Success(val message: String) : TechnicianActionUIState
    data class Error(val message: String) : TechnicianActionUIState
    object Loading : TechnicianActionUIState
}

sealed interface StatsUIState {
    data class Success(val data: ResponseTechnicianStatsData) : StatsUIState
    data class Error(val message: String) : StatsUIState
    object Loading : StatsUIState
}

data class UIStateTechnician(
    val profile: ProfileUIState = ProfileUIState.Loading,
    val stats: StatsUIState = StatsUIState.Loading,
    val technicians: TechniciansUIState = TechniciansUIState.Loading,
    var technician: TechnicianUIState = TechnicianUIState.Loading,
    var technicianAdd: TechnicianActionUIState = TechnicianActionUIState.Loading,
    var technicianChange: TechnicianActionUIState = TechnicianActionUIState.Loading,
    var technicianDelete: TechnicianActionUIState = TechnicianActionUIState.Loading,
    var technicianChangeCover: TechnicianActionUIState = TechnicianActionUIState.Loading,
    var profileChange: TechnicianActionUIState = TechnicianActionUIState.Loading,
    var profileChangePassword: TechnicianActionUIState = TechnicianActionUIState.Loading,
    var profileChangePhoto: TechnicianActionUIState = TechnicianActionUIState.Loading
)

@HiltViewModel
@Keep
class TechnicianViewModel @Inject constructor(
    private val repository: ITechnicianRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UIStateTechnician())
    val uiState = _uiState.asStateFlow()

    private var currentPage = 1
    private var isLastPage = false
    private var currentStatus: String? = null
    private var currentTeknisi: String? = null
    private val currentTechniciansList = mutableListOf<ResponseTechnicianData>()
    private var isFetching = false

    fun getProfile(authToken: String) {
        // PERBAIKAN: Gunakan Dispatchers.IO untuk pemanggilan asinkron yang aman
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(profile = ProfileUIState.Loading) }
            val tmpState = runCatching {
                repository.getUserMe(authToken)
            }.fold(
                onSuccess = { response ->
                    if (response.status == "success") {
                        ProfileUIState.Success(response.data!!.user)
                    } else {
                        ProfileUIState.Error(response.message)
                    }
                },
                onFailure = { error ->
                    ProfileUIState.Error(error.message ?: "Unknown error")
                }
            )
            _uiState.update { it.copy(profile = tmpState) }
        }
    }

    fun resetAndGetAllTechnicians(
        authToken: String,
        search: String? = null,
        status: String? = null,
        teknisi: String? = null
    ) {
        isFetching = false
        currentPage = 1
        isLastPage = false
        currentStatus = status
        currentTeknisi = teknisi
        currentTechniciansList.clear()
        getAllTechnicians(authToken, search, currentStatus, currentTeknisi)
    }

    fun getAllTechnicians(
        authToken: String,
        search: String? = null,
        status: String? = currentStatus,
        teknisi: String? = currentTeknisi
    ) {
        if (isLastPage || isFetching) return

        isFetching = true

        if (currentPage == 1) {
            _uiState.update { it.copy(technicians = TechniciansUIState.Loading) }
        }

        // PERBAIKAN: Gunakan Dispatchers.IO untuk mencegah ANR saat Ktor lag
        viewModelScope.launch(Dispatchers.IO) {
            val tmpState = runCatching {
                repository.getTechnicians(authToken, search, currentPage, 10, status, teknisi)
            }.fold(
                onSuccess = { response ->
                    isFetching = false

                    if (response.status == "success") {
                        val newTechnicians = response.data?.technicians ?: emptyList()
                        if (newTechnicians.size < 10) isLastPage = true

                        // Proses anti-duplikat ini juga bisa berat jika datanya banyak,
                        // karenanya sangat tepat dilakukan di Dispatchers.IO
                        val uniqueTechnicians = newTechnicians.filter { newTechnician ->
                            currentTechniciansList.none { existingTechnician -> existingTechnician.id == newTechnician.id }
                        }

                        currentTechniciansList.addAll(uniqueTechnicians)
                        currentPage++
                        TechniciansUIState.Success(currentTechniciansList.toList())
                    } else {
                        TechniciansUIState.Error(response.message)
                    }
                },
                onFailure = { error ->
                    isFetching = false
                    TechniciansUIState.Error(error.message ?: "Unknown error")
                }
            )

            _uiState.update { state -> state.copy(technicians = tmpState) }
        }
    }

    fun getTechnicianStats(authToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(stats = StatsUIState.Loading) }
            val tmpState = runCatching {
                repository.getTechnicianStats(authToken)
            }.fold(
                onSuccess = { response ->
                    if (response.status == "success") {
                        StatsUIState.Success(response.data!!.stats)
                    } else {
                        StatsUIState.Error(response.message)
                    }
                },
                onFailure = { error ->
                    StatsUIState.Error(error.message ?: "Unknown error")
                }
            )
            _uiState.update { it.copy(stats = tmpState) }
        }
    }

    fun postTechnician(
        authToken: String,
        title: String,
        description: String,
        status: String,
        tanggalDiterima: String,
        namaPemilik: String,
        estimasiBiaya: String,
        teknisi: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(technicianAdd = TechnicianActionUIState.Loading) }
            val tmpState = runCatching {
                repository.postTechnician(
                    authToken = authToken,
                    RequestTechnician(
                        title = title,
                        description = description,
                        status = status,
                        tanggalDiterima = tanggalDiterima,
                        namaPemilik = namaPemilik,
                        estimasiBiaya = estimasiBiaya,
                        teknisi = teknisi
                    )
                )
            }.fold(
                onSuccess = { response ->
                    if (response.status == "success") TechnicianActionUIState.Success(response.message)
                    else TechnicianActionUIState.Error(response.message)
                },
                onFailure = { error -> TechnicianActionUIState.Error(error.message ?: "Unknown error") }
            )
            _uiState.update { it.copy(technicianAdd = tmpState) }
        }
    }

    fun getTechnicianById(authToken: String, technicianId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(technician = TechnicianUIState.Loading) }
            val tmpState = runCatching {
                repository.getTechnicianById(authToken, technicianId)
            }.fold(
                onSuccess = { response ->
                    if (response.status == "success") TechnicianUIState.Success(response.data!!.technician)
                    else TechnicianUIState.Error(response.message)
                },
                onFailure = { error -> TechnicianUIState.Error(error.message ?: "Unknown error") }
            )
            _uiState.update { it.copy(technician = tmpState) }
        }
    }

    fun putTechnician(
        authToken: String,
        technicianId: String,
        title: String,
        description: String,
        status: String,
        tanggalDiterima: String,
        namaPemilik: String,
        estimasiBiaya: String,
        teknisi: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(technicianChange = TechnicianActionUIState.Loading) }
            val tmpState = runCatching {
                repository.putTechnician(
                    authToken = authToken,
                    technicianId = technicianId,
                    RequestTechnician(
                        title = title,
                        description = description,
                        status = status,
                        tanggalDiterima = tanggalDiterima,
                        namaPemilik = namaPemilik,
                        estimasiBiaya = estimasiBiaya,
                        teknisi = teknisi
                    )
                )
            }.fold(
                onSuccess = { response ->
                    if (response.status == "success") TechnicianActionUIState.Success(response.message)
                    else TechnicianActionUIState.Error(response.message)
                },
                onFailure = { error -> TechnicianActionUIState.Error(error.message ?: "Unknown error") }
            )
            _uiState.update { it.copy(technicianChange = tmpState) }
        }
    }

    fun putTechnicianCover(authToken: String, technicianId: String, file: MultipartBody.Part) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(technicianChangeCover = TechnicianActionUIState.Loading) }
            val tmpState = runCatching {
                repository.putTechnicianCover(authToken = authToken, technicianId = technicianId, file = file)
            }.fold(
                onSuccess = { response ->
                    if (response.status == "success") TechnicianActionUIState.Success(response.message)
                    else TechnicianActionUIState.Error(response.message)
                },
                onFailure = { error -> TechnicianActionUIState.Error(error.message ?: "Unknown error") }
            )
            _uiState.update { it.copy(technicianChangeCover = tmpState) }
        }
    }

    fun deleteTechnician(authToken: String, technicianId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(technicianDelete = TechnicianActionUIState.Loading) }
            val tmpState = runCatching {
                repository.deleteTechnician(authToken = authToken, technicianId = technicianId)
            }.fold(
                onSuccess = { response ->
                    if (response.status == "success") TechnicianActionUIState.Success(response.message)
                    else TechnicianActionUIState.Error(response.message)
                },
                onFailure = { error -> TechnicianActionUIState.Error(error.message ?: "Unknown error") }
            )
            _uiState.update { it.copy(technicianDelete = tmpState) }
        }
    }

    fun putUserMe(authToken: String, name: String, username: String, about: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(profileChange = TechnicianActionUIState.Loading) }
            val tmpState = runCatching {
                repository.putUserMe(authToken, RequestUserChange(name, username, about))
            }.fold(
                onSuccess = { response ->
                    if (response.status == "success") TechnicianActionUIState.Success(response.message)
                    else TechnicianActionUIState.Error(response.message)
                },
                onFailure = { error -> TechnicianActionUIState.Error(error.message ?: "Unknown error") }
            )
            _uiState.update { it.copy(profileChange = tmpState) }
        }
    }

    fun putUserMePassword(authToken: String, oldPassword: String, newPassword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(profileChangePassword = TechnicianActionUIState.Loading) }
            val tmpState = runCatching {
                repository.putUserMePassword(authToken, RequestUserChangePassword(oldPassword, newPassword))
            }.fold(
                onSuccess = { response ->
                    if (response.status == "success") TechnicianActionUIState.Success(response.message)
                    else TechnicianActionUIState.Error(response.message)
                },
                onFailure = { error -> TechnicianActionUIState.Error(error.message ?: "Unknown error") }
            )
            _uiState.update { it.copy(profileChangePassword = tmpState) }
        }
    }

    fun putUserMePhoto(authToken: String, file: MultipartBody.Part) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(profileChangePhoto = TechnicianActionUIState.Loading) }
            val tmpState = runCatching {
                repository.putUserMePhoto(authToken, file)
            }.fold(
                onSuccess = { response ->
                    if (response.status == "success") TechnicianActionUIState.Success(response.message)
                    else TechnicianActionUIState.Error(response.message)
                },
                onFailure = { error -> TechnicianActionUIState.Error(error.message ?: "Unknown error") }
            )
            _uiState.update { it.copy(profileChangePhoto = tmpState) }
        }
    }
}