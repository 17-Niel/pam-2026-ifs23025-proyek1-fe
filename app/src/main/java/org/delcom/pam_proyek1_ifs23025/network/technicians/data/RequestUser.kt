package org.delcom.pam_proyek1_ifs23025.network.technicians.data

import kotlinx.serialization.Serializable

@Serializable
data class RequestUserChange (
    val name: String,
    val username: String,
    val about: String? = null
)

@Serializable
data class RequestUserChangePassword (
    val newPassword: String,
    val password: String
)