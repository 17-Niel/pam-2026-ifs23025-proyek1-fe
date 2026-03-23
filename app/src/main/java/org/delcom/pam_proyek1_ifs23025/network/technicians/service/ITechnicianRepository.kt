package org.delcom.pam_proyek1_ifs23025.network.technicians.service

import okhttp3.MultipartBody
import org.delcom.pam_proyek1_ifs23025.network.data.ResponseMessage
// Pastikan semua import mengarah ke package events.data
import org.delcom.pam_proyek1_ifs23025.network.technicians.data.RequestAuthLogin
import org.delcom.pam_proyek1_ifs23025.network.technicians.data.RequestAuthLogout
import org.delcom.pam_proyek1_ifs23025.network.technicians.data.RequestAuthRefreshToken
import org.delcom.pam_proyek1_ifs23025.network.technicians.data.RequestAuthRegister
import org.delcom.pam_proyek1_ifs23025.network.technicians.data.RequestTechnician
import org.delcom.pam_proyek1_ifs23025.network.technicians.data.RequestUserChange
import org.delcom.pam_proyek1_ifs23025.network.technicians.data.RequestUserChangePassword
import org.delcom.pam_proyek1_ifs23025.network.technicians.data.ResponseAuthLogin
import org.delcom.pam_proyek1_ifs23025.network.technicians.data.ResponseAuthRegister
import org.delcom.pam_proyek1_ifs23025.network.technicians.data.ResponseTechnician
import org.delcom.pam_proyek1_ifs23025.network.technicians.data.ResponseTechnicianAdd
import org.delcom.pam_proyek1_ifs23025.network.technicians.data.ResponseTechnicianStats
import org.delcom.pam_proyek1_ifs23025.network.technicians.data.ResponseTechnicians
import org.delcom.pam_proyek1_ifs23025.network.technicians.data.ResponseUser

interface ITechnicianRepository {

    // ----------------------------------
    // Auth
    // ----------------------------------

    suspend fun postRegister(
        request: RequestAuthRegister
    ): ResponseMessage<ResponseAuthRegister?>

    suspend fun postLogin(
        request: RequestAuthLogin
    ): ResponseMessage<ResponseAuthLogin?>

    suspend fun postLogout(
        request: RequestAuthLogout
    ): ResponseMessage<String?>

    suspend fun postRefreshToken(
        request: RequestAuthRefreshToken
    ): ResponseMessage<ResponseAuthLogin?>

    // ----------------------------------
    // Users
    // ----------------------------------

    suspend fun getUserMe(
        authToken: String
    ): ResponseMessage<ResponseUser?>

    suspend fun putUserMe(
        authToken: String,
        request: RequestUserChange
    ): ResponseMessage<String?>

    suspend fun putUserMePassword(
        authToken: String,
        request: RequestUserChangePassword
    ): ResponseMessage<String?>

    suspend fun putUserMePhoto(
        authToken: String,
        file: MultipartBody.Part
    ): ResponseMessage<String?>

    // ----------------------------------

    // ----------------------------------

    // Ubah nama fungsi dan parameter
    suspend fun getTechnicians(
        authToken: String,
        search: String? = null,
        page: Int = 1,
        perPage: Int = 10,
        status: String? = null, // Filter
        teknisi: String? = null  // Filter
    ): ResponseMessage<ResponseTechnicians?>

    suspend fun getTechnicianStats(
        authToken: String
    ): ResponseMessage<ResponseTechnicianStats?>

    suspend fun postTechnician(
        authToken: String,
        request: RequestTechnician
    ): ResponseMessage<ResponseTechnicianAdd?>

    suspend fun getTechnicianById(
        authToken: String,
        technicianId: String
    ): ResponseMessage<ResponseTechnician?>

    suspend fun putTechnician(
        authToken: String,
        technicianId: String,
        request: RequestTechnician
    ): ResponseMessage<String?>

    suspend fun putTechnicianCover(
        authToken: String,
        technicianId: String,
        file: MultipartBody.Part
    ): ResponseMessage<String?>

    suspend fun deleteTechnician(
        authToken: String,
        technicianId: String
    ): ResponseMessage<String?>
}