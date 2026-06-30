package com.example.sigaap

/**
 * BAGIAN: VIEW - NAVIGASI UTAMA
 * File ini adalah pengatur lalu lintas (Router) aplikasi.
 * Tugasnya: Menentukan layar mana yang harus muncul berdasarkan status login dan menu yang dipilih.
 */

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sigaap.ui.theme.*

/**
 * Enumeration untuk daftar semua layar yang tersedia di aplikasi.
 */
enum class Screen {
    Login, Dashboard, Input, Riwayat, BukuSaku, Profile
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SigaapApp(viewModel: PelanggaranViewModel) {
    // State untuk menyimpan layar mana yang sedang aktif saat ini
    var currentScreen by remember { mutableStateOf(if (viewModel.isLoggedIn.value) Screen.Dashboard else Screen.Login) }

    // Memantau status login: Jika logout, langsung tendang ke layar Login. Jika login, ke Dashboard.
    LaunchedEffect(viewModel.isLoggedIn.value) {
        currentScreen = if (viewModel.isLoggedIn.value) Screen.Dashboard else Screen.Login
    }

    if (!viewModel.isLoggedIn.value) {
        // MENAMPILKAN VIEW LOGIN jika belum masuk
        LoginScreen(viewModel)
    } else {
        // MENAMPILKAN VIEW UTAMA dengan Bottom Navigation Bar
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = DarkNavyBackground,
                    tonalElevation = 12.dp
                ) {
                    // Item Menu 1: Dashboard / Input
                    NavigationBarItem(
                        selected = currentScreen == Screen.Dashboard || currentScreen == Screen.Input,
                        onClick = { currentScreen = Screen.Dashboard },
                        label = { Text("Pencatatan", fontWeight = FontWeight.Bold) },
                        icon = { 
                            Icon(
                                if (currentScreen == Screen.Dashboard) Icons.Default.Assessment else Icons.Default.EditNote, 
                                null,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaroonPrimary,
                            selectedIconColor = White,
                            unselectedIconColor = White.copy(alpha = 0.5f),
                            selectedTextColor = White,
                            unselectedTextColor = White.copy(alpha = 0.5f)
                        )
                    )
                    // Item Menu 2: Riwayat
                    NavigationBarItem(
                        selected = currentScreen == Screen.Riwayat,
                        onClick = { currentScreen = Screen.Riwayat },
                        label = { Text("Riwayat", fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.History, null) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaroonPrimary,
                            selectedIconColor = White,
                            unselectedIconColor = White.copy(alpha = 0.5f),
                            selectedTextColor = White,
                            unselectedTextColor = White.copy(alpha = 0.5f)
                        )
                    )
                    // Item Menu 3: Buku Saku
                    NavigationBarItem(
                        selected = currentScreen == Screen.BukuSaku,
                        onClick = { currentScreen = Screen.BukuSaku },
                        label = { Text("Buku Saku", fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.AutoStories, null) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaroonPrimary,
                            selectedIconColor = White,
                            unselectedIconColor = White.copy(alpha = 0.5f),
                            selectedTextColor = White,
                            unselectedTextColor = White.copy(alpha = 0.5f)
                        )
                    )
                    // Item Menu 4: Profil
                    NavigationBarItem(
                        selected = currentScreen == Screen.Profile,
                        onClick = { currentScreen = Screen.Profile },
                        label = { Text("Profil", fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.AccountCircle, null) },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaroonPrimary,
                            selectedIconColor = White,
                            unselectedIconColor = White.copy(alpha = 0.5f),
                            selectedTextColor = White,
                            unselectedTextColor = White.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                // LOGIKA PERPINDAHAN SCREEN (Konten Tengah)
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "ScreenTransition"
                ) { targetScreen ->
                    when (targetScreen) {
                        Screen.Dashboard -> GuruDashboardScreen(
                            viewModel = viewModel,
                            onNavigateToInput = { currentScreen = Screen.Input }
                        )
                        Screen.Input -> InputCatatanScreen(
                            viewModel = viewModel,
                            onBack = { currentScreen = Screen.Dashboard }
                        )
                        Screen.Riwayat -> RiwayatCatatanScreen(
                            viewModel = viewModel,
                            onNavigateToInput = { currentScreen = Screen.Input }
                        )
                        Screen.BukuSaku -> BukuSakuScreen(viewModel)
                        Screen.Profile -> ProfileGuruScreen(viewModel)
                        else -> {}
                    }
                }
            }
        }
    }
}

