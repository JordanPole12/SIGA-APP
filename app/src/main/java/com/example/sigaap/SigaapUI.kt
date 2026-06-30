package com.example.sigaap

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
 * Navigasi antar halaman
 */
enum class Screen {
    Login, Dashboard, Input, Riwayat, BukuSaku, Profile
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SigaapApp(viewModel: PelanggaranViewModel) {
    var currentScreen by remember { mutableStateOf(if (viewModel.isLoggedIn.value) Screen.Dashboard else Screen.Login) }

    // Memantau status login untuk navigasi otomatis
    LaunchedEffect(viewModel.isLoggedIn.value) {
        currentScreen = if (viewModel.isLoggedIn.value) Screen.Dashboard else Screen.Login
    }

    if (!viewModel.isLoggedIn.value) {
        LoginScreen(viewModel)
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = DarkNavyBackground,
                    tonalElevation = 12.dp
                ) {
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
