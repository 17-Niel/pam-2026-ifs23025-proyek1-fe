package org.delcom.pam_proyek1_ifs23025.network.data

import kotlinx.serialization.Serializable

@Serializable
data class ResponseMessage<T>(
    val status: String,
    val message: String,
    val data: T? = null
)