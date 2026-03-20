package org.delcom.pam_proyek1_ifs23025.network.technicians.service

import okhttp3.MultipartBody
import org.delcom.pam_proyek1_ifs23025.network.data.ResponseMessage
// Pastikan semua import data mengarah ke folder 'events/data' yang baru
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
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface TechnicianApiService {
    // ----------------------------------
    // Auth
    // ----------------------------------

    // Register
    @POST("auth/register")
    suspend fun postRegister(
        @Body request: RequestAuthRegister
    ): ResponseMessage<ResponseAuthRegister?>

    // Login
    @POST("auth/login")
    suspend fun postLogin(
        @Body request: RequestAuthLogin
    ): ResponseMessage<ResponseAuthLogin?>

    // Logout
    @POST("auth/logout")
    suspend fun postLogout(
        @Body request: RequestAuthLogout
    ): ResponseMessage<String?>

    // RefreshToken
    @POST("auth/refresh-token")
    suspend fun postRefreshToken(
        @Body request: RequestAuthRefreshToken
    ): ResponseMessage<ResponseAuthLogin?>

    // ----------------------------------
    // Users
    // ----------------------------------

    // Ambil informasi profile
    @GET("users/me")
    suspend fun getUserMe(
        @Header("Authorization") authToken: String
    ): ResponseMessage<ResponseUser?>

    // Ubah data profile
    @PUT("users/me")
    suspend fun putUserMe(
        @Header("Authorization") authToken: String,
        @Body request: RequestUserChange,
    ): ResponseMessage<String?>

    // Ubah data kata sandi
    @PUT("users/me/password")
    suspend fun putUserMePassword(
        @Header("Authorization") authToken: String,
        @Body request: RequestUserChangePassword,
    ): ResponseMessage<String?>

    // Ubah photo profile
    @Multipart
    @PUT("users/me/photo")
    suspend fun putUserMePhoto(
        @Header("Authorization") authToken: String,
        @Part file: MultipartBody.Part
    ): ResponseMessage<String?>

    // ----------------------------------
    // Events (Kegiatan Himpunan)
    // ----------------------------------

    // Ambil semua data kegiatan
    @GET("technicians")
    suspend fun getTechnicians(
        @Header("Authorization") authToken: String,
        @Query("search") search: String? = null,
        @Query("page") page: Int = 1,
        @Query("perPage") perPage: Int = 10,
        @Query("status") status: String? = null, // Filter baru
        @Query("teknisi") teknisi: String? = null  // Filter baru
    ): ResponseMessage<ResponseTechnicians?>

    // Ambil data statistik kegiatan
    @GET("technicians/stats")
    suspend fun getTechnicianStats(
        @Header("Authorization") authToken: String
    ): ResponseMessage<ResponseTechnicianStats?>

    // Menambahkan data kegiatan
    @POST("technicians")
    suspend fun postTechnician(
        @Header("Authorization") authToken: String,
        @Body request: RequestTechnician
    ): ResponseMessage<ResponseTechnicianAdd?>

    // Ambil data kegiatan berdasarkan id
    @GET("technicians/{id}")
    suspend fun getTechnicianById(
        @Header("Authorization") authToken: String,
        @Path("id") technicianId: String // Path parameter disesuaikan
    ): ResponseMessage<ResponseTechnician?>

    // Mengubah data kegiatan
    @PUT("technicians/{id}")
    suspend fun putTechnician(
        @Header("Authorization") authToken: String,
        @Path("id") technicianId: String,
        @Body request: RequestTechnician
    ): ResponseMessage<String?>

    // Ubah cover kegiatan
    @Multipart
    @PUT("technicians/{id}/cover")
    suspend fun putTechnicianCover(
        @Header("Authorization") authToken: String,
        @Path("id") technicianId: String,
        @Part file: MultipartBody.Part
    ): ResponseMessage<String?>

    // Hapus data kegiatan
    @DELETE("technicians/{id}")
    suspend fun deleteTechnician(
        @Header("Authorization") authToken: String,
        @Path("id") technicianId: String
    ): ResponseMessage<String?>
}