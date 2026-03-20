package org.delcom.pam_proyek1_ifs23025.network.technicians.data

import kotlinx.serialization.Serializable

@Serializable
data class ResponseAuthRegister (
    val userId: String
)

@Serializable
data class ResponseAuthLogin (
    val authToken: String,
    val refreshToken: String
)