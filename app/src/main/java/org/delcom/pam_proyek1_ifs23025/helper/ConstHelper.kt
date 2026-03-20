package org.delcom.pam_proyek1_ifs23025.helper

class ConstHelper {
    // Route Names
    enum class RouteNames(val path: String) {
        AuthLogin(path = "auth/login"),
        AuthRegister(path = "auth/register"),

        Home(path = "home"),

        Profile(path = "profile"),

        // Ubah dari Todos menjadi Technicians
        Technicians(path = "technicians"),
        TechniciansAdd(path = "technicians/add"),
        TechniciansDetail(path = "technicians/{technicianId}"), // Ubah parameter ke technicianId
        TechniciansEdit(path = "technicians/{technicianId}/edit"),
    }
}