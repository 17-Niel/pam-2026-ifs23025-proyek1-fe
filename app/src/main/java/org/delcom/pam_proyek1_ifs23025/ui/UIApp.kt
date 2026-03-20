package org.delcom.pam_proyek1_ifs23025.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.delcom.pam_proyek1_ifs23025.helper.ConstHelper
import org.delcom.pam_proyek1_ifs23025.ui.components.CustomSnackbar
import org.delcom.pam_proyek1_ifs23025.ui.screens.HomeScreen
import org.delcom.pam_proyek1_ifs23025.ui.screens.ProfileScreen
import org.delcom.pam_proyek1_ifs23025.ui.screens.auth.AuthLoginScreen
import org.delcom.pam_proyek1_ifs23025.ui.screens.auth.AuthRegisterScreen
// Pastikan import di bawah ini disesuaikan ke folder 'events' yang baru
import org.delcom.pam_proyek1_ifs23025.ui.screens.technicians.TechniciansAddScreen
import org.delcom.pam_proyek1_ifs23025.ui.screens.technicians.TechniciansDetailScreen
import org.delcom.pam_proyek1_ifs23025.ui.screens.technicians.TechniciansEditScreen
import org.delcom.pam_proyek1_ifs23025.ui.screens.technicians.TechniciansScreen
import org.delcom.pam_proyek1_ifs23025.ui.viewmodels.AuthViewModel
import org.delcom.pam_proyek1_ifs23025.ui.viewmodels.TechnicianViewModel // Ubah import ViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UIApp(
    navController: NavHostController = rememberNavController(),
    technicianViewModel: TechnicianViewModel, // Ubah dari TodoViewModel
    authViewModel: AuthViewModel
) {
    // Inisialisasi SnackbarHostState
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState){ snackbarData ->
            CustomSnackbar(snackbarData, onDismiss = { snackbarHostState.currentSnackbarData?.dismiss() })
        } },
    ) { _ ->
        NavHost(
            navController = navController,
            startDestination = ConstHelper.RouteNames.Home.path,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F8FA))

        ) {
            // Auth Login
            composable(
                route = ConstHelper.RouteNames.AuthLogin.path,
            ) { _ ->
                AuthLoginScreen(
                    navController = navController,
                    snackbarHost = snackbarHostState,
                    authViewModel = authViewModel,
                )
            }

            // Auth Register
            composable(
                route = ConstHelper.RouteNames.AuthRegister.path,
            ) { _ ->
                AuthRegisterScreen(
                    navController = navController,
                    snackbarHost = snackbarHostState,
                    authViewModel = authViewModel,
                )
            }

            // Home
            composable(
                route = ConstHelper.RouteNames.Home.path,
            ) { _ ->
                HomeScreen(
                    navController = navController,
                    authViewModel = authViewModel,
                    technicianViewModel = technicianViewModel // Ubah dari todoViewModel
                )
            }

            // Profile
            composable(
                route = ConstHelper.RouteNames.Profile.path,
            ) { _ ->
                ProfileScreen(
                    navController = navController,
                    authViewModel = authViewModel,
                    technicianViewModel = technicianViewModel // Ubah dari todoViewModel
                )
            }

            // Technicians (Dulu Todos)
            composable(
                route = ConstHelper.RouteNames.Technicians.path,
            ) { _ ->
                TechniciansScreen(
                    navController = navController,
                    authViewModel = authViewModel,
                    technicianViewModel = technicianViewModel // Ubah dari todoViewModel
                )
            }

            // Events Add (Dulu Todos Add)
            composable(
                route = ConstHelper.RouteNames.TechniciansAdd.path,
            ) { _ ->
                TechniciansAddScreen(
                    navController = navController,
                    snackbarHost = snackbarHostState,
                    authViewModel = authViewModel,
                    technicianViewModel = technicianViewModel // Ubah dari todoViewModel
                )
            }

            // Events Detail (Dulu Todos Detail)
            composable(
                route = ConstHelper.RouteNames.TechniciansDetail.path,
                arguments = listOf(
                    navArgument("technicianId") { type = NavType.StringType }, // Ubah argumen ke technicianId
                )
            ) { backStackEntry ->
                val technicianId = backStackEntry.arguments?.getString("technicianId") ?: ""

                TechniciansDetailScreen(
                    navController = navController,
                    snackbarHost = snackbarHostState,
                    authViewModel = authViewModel,
                    technicianViewModel = technicianViewModel, // Ubah dari todoViewModel
                    technicianId = technicianId // Ubah dari todoId
                )
            }

            // Events Edit (Dulu Todos Edit)
            composable(
                route = ConstHelper.RouteNames.TechniciansEdit.path,
                arguments = listOf(
                    navArgument("technicianId") { type = NavType.StringType }, // Ubah argumen ke eventId
                )
            ) { backStackEntry ->
                val technicianId = backStackEntry.arguments?.getString("technicianId") ?: ""

                TechniciansEditScreen(
                    navController = navController,
                    snackbarHost = snackbarHostState,
                    authViewModel = authViewModel,
                    technicianViewModel = technicianViewModel, // Ubah dari todoViewModel
                    technicianId = technicianId // Ubah dari todoId
                )
            }
        }
    }
}