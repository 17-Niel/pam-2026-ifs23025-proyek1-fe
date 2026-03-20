package org.delcom.pam_proyek1_ifs23025.network.technicians.data

enum class TeknisiEnum(val fullName: String, val shortName: String) {
    MIKAT("Minat Bakat", "Mikat"),
    HUMAS("Humas", "Humas"),
    KOMINFO("Kominfo", "Kominfo"),
    DANUS("Dana dan Usaha", "Danus"),
    PENDIDIKAN("Pendidikan", "Pendidikan"),
    RISTEK("Ristek", "Ristek");

    companion object {
        // Fungsi pembantu untuk mendapatkan list String untuk dropdown
        fun getAllFullNames(): List<String> {
            return entries.map { it.fullName }
        }
    }
}