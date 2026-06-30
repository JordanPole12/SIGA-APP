package com.example.sigaap

/**
 * BAGIAN: VIEW - DASHBOARD
 * File ini menangani tampilan utama setelah login, yaitu ringkasan data dan tombol cepat.
 * Menampilkan statistik (Logika dari ViewModel) dan daftar singkat catatan terbaru.
 */

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sigaap.ui.theme.*

@Composable
fun GuruDashboardScreen(
    viewModel: PelanggaranViewModel, // Menghubungkan ke Otak (ViewModel)
    onNavigateToInput: () -> Unit // Menghubungkan ke Navigasi Utama
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val currentTipe = if (selectedTab == 0) TipeCatatan.PELANGGARAN else TipeCatatan.PRESTASI
    
    // LOGIKA: Filter data berdasarkan tab yang dipilih
    val list = viewModel.getFilteredCatatan().filter { it.tipe == currentTipe }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6F8))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // --- HEADER BERGRADASI ---
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp) 
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0xFFD32F2F), Color(0xFF1A1C29))
                                ),
                                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                            )
                    )
                    
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(top = 20.dp, start = 20.dp, end = 20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "SI-GAPP Dashboard",
                                    color = White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    "PANTAU KEDISIPLINAN & PRESTASI SISWA",
                                    color = White.copy(alpha = 0.7f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // --- TAB PEMILIH (PELANGGARAN / PRESTASI) ---
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = White),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(4.dp)
                            ) {
                                TabButton("Pelanggaran", selectedTab == 0, Modifier.weight(1f)) { selectedTab = 0 }
                                TabButton("Prestasi", selectedTab == 1, Modifier.weight(1f)) { selectedTab = 1 }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // --- TOMBOL INPUT CEPAT ---
                        Button(
                            onClick = { 
                                viewModel.clearForm()
                                viewModel.tipeCatatan.value = currentTipe
                                onNavigateToInput() 
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedTab == 0) MaroonPrimary else Color(0xFF2E7D32)
                            ),
                            shape = RoundedCornerShape(14.dp),
                            elevation = ButtonDefaults.buttonElevation(6.dp)
                        ) {
                            Icon(
                                if (selectedTab == 0) Icons.Outlined.Warning else Icons.Default.EmojiEvents, 
                                null, 
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                if (selectedTab == 0) "Catat Pelanggaran" else "Input Prestasi Baru", 
                                fontWeight = FontWeight.Black, 
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // --- KOLOM PENCARIAN ---
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = White,
                            shadowElevation = 2.dp
                        ) {
                            TextField(
                                value = viewModel.searchQuery.value,
                                onValueChange = { viewModel.searchQuery.value = it },
                                placeholder = { Text("Cari nama siswa atau NIS..", color = GrayText, fontSize = 14.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = { Icon(Icons.Default.Search, null, tint = GrayText) },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // --- BAGIAN STATISTIK: Data diambil dari Logika di ViewModel ---
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StatCardImproved("HARI INI", viewModel.getHariIniCount(currentTipe).toString(), Modifier.weight(1f))
                            StatCardImproved("MINGGU INI", viewModel.getMingguIniCount(currentTipe).toString(), Modifier.weight(1f))
                            StatCardImproved("BULAN INI", viewModel.getBulanIniCount(currentTipe).toString(), Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        Text(
                            if (selectedTab == 0) "PELANGGARAN TERBARU" else "PRESTASI TERBARU",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = GrayText,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // --- DAFTAR CATATAN TERBARU ---
            if (list.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                        Text("Belum ada data ${if (selectedTab == 0) "pelanggaran" else "prestasi"}.", color = GrayText, fontWeight = FontWeight.Medium)
                    }
                }
            } else {
                items(list) { item ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        CatatanSiswaCard(
                            item = item,
                            onEdit = {
                                viewModel.prepareEdit(item)
                                onNavigateToInput()
                            },
                            onDelete = { viewModel.hapusCatatan(item.id) }
                        )
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun TabButton(text: String, isSelected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) Color(0xFFF5F5F5) else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            color = if (isSelected) MaroonPrimary else GrayText,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun StatCardImproved(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = DarkNavyBackground
            )
            Text(
                label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = GrayText,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Komponen Card untuk menampilkan detail per siswa.
 * Menghubungkan tombol Edit/Hapus ke Fungsi di ViewModel.
 */
@Composable
fun CatatanSiswaCard(item: CatatanSiswa, onEdit: () -> Unit, onDelete: () -> Unit) {
    val isPelanggaran = item.tipe == TipeCatatan.PELANGGARAN
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        item.namaSiswa,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = DarkNavyBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        item.kelas,
                        fontSize = 13.sp,
                        color = GrayText,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "NISN: ${item.nisn}",
                        fontSize = 11.sp,
                        color = GrayText.copy(alpha = 0.8f)
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Badge status otomatis berdasarkan poin
                    if (isPelanggaran) {
                        val (status, badgeColor, textColor) = when {
                            item.poin >= 50 -> Triple("BERAT", Color(0xFFFFEBEE), Color(0xFFD32F2F))
                            item.poin >= 15 -> Triple("SEDANG", Color(0xFFFFF3E0), Color(0xFFEF6C00))
                            else -> Triple("RINGAN", Color(0xFFFFFDE7), Color(0xFFFBC02D))
                        }
                        Surface(color = badgeColor, shape = RoundedCornerShape(8.dp)) {
                            Text(
                                status,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = textColor
                            )
                        }
                    } else {
                        Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(8.dp)) {
                            Text(
                                "PRESTASI",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                    
                    // Menu Edit & Hapus
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, null, tint = GrayText)
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(
                                text = { Text("Edit") },
                                onClick = { showMenu = false; onEdit() },
                                leadingIcon = { Icon(Icons.Default.Edit, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Hapus", color = Color.Red) },
                                onClick = { showMenu = false; onDelete() },
                                leadingIcon = { Icon(Icons.Default.Delete, null, tint = Color.Red) }
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (isPelanggaran) Icons.Outlined.Warning else Icons.Default.EmojiEvents,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = if (isPelanggaran) MaroonPrimary else Color(0xFF2E7D32)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        item.judulCatatan,
                        fontSize = 15.sp,
                        color = DarkNavyBackground,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Text(
                    "${if (isPelanggaran) "-" else "+"}${item.poin} pt",
                    color = if (isPelanggaran) MaroonPrimary else Color(0xFF2E7D32),
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
            }
            
            if (item.catatanTambahan.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    item.catatanTambahan,
                    fontSize = 12.sp,
                    color = GrayText,
                    lineHeight = 16.sp,
                    maxLines = 2
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    item.tanggalInput,
                    fontSize = 11.sp,
                    color = GrayText.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    "Oleh: ${item.namaGuruInput}",
                    fontSize = 11.sp,
                    color = GrayText.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

