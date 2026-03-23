package org.delcom.pam_proyek1_ifs23025.network.technicians.data

import kotlinx.serialization.Serializable

@Serializable
data class ResponseTechnicians (
    val technicians: List<ResponseTechnicianData> // Diubah dari todos menjadi technicians
)

@Serializable
data class ResponseTechnician (
    val technician: ResponseTechnicianData // Diubah dari todo menjadi technician
)

@Serializable
data class ResponseTechnicianData(
    val id: String = "",
    val userId: String = "",
    val title: String,
    val description: String,

    // Field baru pengganti isDone dan urgency
    val status: String = "Kerusakan Ringan",
    val tanggalDiterima: String = "",
    val namaPemilik: String = "",
    val estimasiBiaya: String = "",
    val teknisi: String = "",

    val cover: String? = null,
    val createdAt: String = "",
    var updatedAt: String = ""
)

@Serializable
data class ResponseTechnicianAdd (
    val technicianId: String // Diubah dari todoId menjadi technicianId
)

@Serializable
data class ResponseTechnicianStats (
    val stats: ResponseTechnicianStatsData
)

@Serializable
data class ResponseTechnicianStatsData(
    val total: Long = 0,
    val complete: Long = 0,
    val active: Long = 0,
    val canceled: Long = 0 // Tambahan  yang dibatalkan
)