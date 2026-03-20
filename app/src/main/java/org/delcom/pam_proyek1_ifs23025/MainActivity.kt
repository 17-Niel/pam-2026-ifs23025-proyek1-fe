package org.delcom.pam_proyek1_ifs23025

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.delcom.pam_proyek1_ifs23025.ui.UIApp
import org.delcom.pam_proyek1_ifs23025.ui.theme.DelcomTheme
import org.delcom.pam_proyek1_ifs23025.ui.viewmodels.AuthViewModel
import org.delcom.pam_proyek1_ifs23025.ui.viewmodels.TechnicianViewModel // Ganti import ke EventViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Ubah inisiasi ViewModel
    private val technicianViewModel: TechnicianViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DelcomTheme {
                UIApp(
                    technicianViewModel = technicianViewModel, // Ubah parameter yang dikirim
                    authViewModel = authViewModel
                )
            }
        }
    }
}