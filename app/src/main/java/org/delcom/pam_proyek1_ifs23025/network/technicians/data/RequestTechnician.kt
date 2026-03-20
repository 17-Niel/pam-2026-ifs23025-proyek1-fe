package org.delcom.pam_proyek1_ifs23025.network.technicians.data

import kotlinx.serialization.Serializable

@Serializable
data class RequestTechnician (
    val title: String,
    val description: String,

    // Properti baru disesuaikan dengan Backend
    val status: String = "belum terlaksana",
    val tanggalDiterima: String,
    val namaPemilik: String,
    val estimasiBiaya: String,
    val teknisi: String
)