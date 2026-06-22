package com.example.sigaap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.sigaap.ui.theme.SigaapTheme

/**
 * Activity utama untuk aplikasi SI-GAPP (Sistem Garda Penegakan Peraturan).
 * Menggunakan Jetpack Compose sebagai UI toolkit dan MVVM sebagai arsitektur.
 */
class MainActivity : ComponentActivity() {
    
    // Inisialisasi ViewModel
    private val viewModel: PelanggaranViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SigaapTheme {
                // Memanggil Composable utama aplikasi
                SigaapApp(viewModel = viewModel)
            }
        }
    }
}
