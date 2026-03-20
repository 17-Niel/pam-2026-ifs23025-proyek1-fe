package org.delcom.pam_proyek1_ifs23025.network.technicians.service

import okhttp3.MultipartBody
import org.delcom.pam_proyek1_ifs23025.helper.SuspendHelper
import org.delcom.pam_proyek1_ifs23025.network.data.ResponseMessage
// Import disesuaikan ke package Technicians/data
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
import org.delcom.pam_proyek1_ifs23025.network.technicians.data.ResponseTechnicianStats
import org.delcom.pam_proyek1_ifs23025.network.technicians.data.ResponseTechnicianAdd
import org.delcom.pam_proyek1_ifs23025.network.technicians.data.ResponseTechnicians
import org.delcom.pam_proyek1_ifs23025.network.technicians.data.ResponseUser

class TechnicianRepository(
    private val apiService: TechnicianApiService
) : ITechnicianRepository {

    // ----------------------------------
    // Auth
    // ----------------------------------

    override suspend fun postRegister(
        request: RequestAuthRegister
    ): ResponseMessage<ResponseAuthRegister?> {
        return SuspendHelper.safeApiCall {
            apiService.postRegister(request)
        }
    }

    override suspend fun postLogin(
        request: RequestAuthLogin
    ): ResponseMessage<ResponseAuthLogin?> {
        return SuspendHelper.safeApiCall {
            apiService.postLogin(request)
        }
    }

    override suspend fun postLogout(
        request: RequestAuthLogout
    ): ResponseMessage<String?> {
        return SuspendHelper.safeApiCall {
            apiService.postLogout(request)
        }
    }

    override suspend fun postRefreshToken(
        request: RequestAuthRefreshToken
    ): ResponseMessage<ResponseAuthLogin?> {
        return SuspendHelper.safeApiCall {
            apiService.postRefreshToken(request)
        }
    }

    // ----------------------------------
    // Users
    // ----------------------------------

    override suspend fun getUserMe(
        authToken: String
    ): ResponseMessage<ResponseUser?> {
        return SuspendHelper.safeApiCall {
            apiService.getUserMe("Bearer $authToken")
        }
    }

    override suspend fun putUserMe(
        authToken: String,
        request: RequestUserChange
    ): ResponseMessage<String?> {
        return SuspendHelper.safeApiCall {
            apiService.putUserMe("Bearer $authToken", request)
        }
    }

    override suspend fun putUserMePassword(
        authToken: String,
        request: RequestUserChangePassword
    ): ResponseMessage<String?> {
        return SuspendHelper.safeApiCall {
            apiService.putUserMePassword("Bearer $authToken", request)
        }
    }

    override suspend fun putUserMePhoto(
        authToken: String,
        file: MultipartBody.Part
    ): ResponseMessage<String?> {
        return SuspendHelper.safeApiCall {
            apiService.putUserMePhoto("Bearer $authToken", file)
        }
    }

    // ----------------------------------
    // Technician                                                                                                         s (Kegiatan Himpunan)
    // ----------------------------------

    override suspend fun getTechnicians(
        authToken: String,
        search: String?,
        page: Int,
        perPage: Int,
        status: String?,
        teknisi: String?
    ): ResponseMessage<ResponseTechnicians?> {
        return SuspendHelper.safeApiCall {
            // Memanggil getEvents dari apiService dengan parameter baru
            apiService.getTechnicians("Bearer $authToken", search, page, perPage, status, teknisi)
        }
    }

    override suspend fun getTechnicianStats(
        authToken: String
    ): ResponseMessage<ResponseTechnicianStats?> {
        return SuspendHelper.safeApiCall {
            apiService.getTechnicianStats("Bearer $authToken")
        }
    }

    override suspend fun postTechnician(
        authToken: String,
        request: RequestTechnician
    ): ResponseMessage<ResponseTechnicianAdd?> {
        return SuspendHelper.safeApiCall {
            apiService.postTechnician("Bearer $authToken", request)
        }
    }

    override suspend fun getTechnicianById(
        authToken: String,
        technicianId: String
    ): ResponseMessage<ResponseTechnician?> {
        return SuspendHelper.safeApiCall {
            apiService.getTechnicianById("Bearer $authToken", technicianId)
        }
    }

    override suspend fun putTechnician(
        authToken: String,
        technicianId: String,
        request: RequestTechnician
    ): ResponseMessage<String?> {
        return SuspendHelper.safeApiCall {
            apiService.putTechnician("Bearer $authToken", technicianId, request)
        }
    }

    override suspend fun putTechnicianCover(
        authToken: String,
        technicianId: String,
        file: MultipartBody.Part
    ): ResponseMessage<String?> {
        return SuspendHelper.safeApiCall {
            apiService.putTechnicianCover("Bearer $authToken", technicianId, file)
        }
    }

    override suspend fun deleteTechnician(
        authToken: String,
        technicianId: String
    ): ResponseMessage<String?> {
        return SuspendHelper.safeApiCall {
            apiService.deleteTechnician("Bearer $authToken", technicianId)
        }
    }
}