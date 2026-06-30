package com.example.sigaap

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sigaap.ui.theme.*

@Composable
fun RiwayatCatatanScreen(viewModel: PelanggaranViewModel, onNavigateToInput: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6F8))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .statusBarsPadding()
                .padding(bottom = 20.dp, start = 20.dp, end = 20.dp)
        ) {
            Column {
                Text("Riwayat Lengkap", fontSize = 24.sp, fontWeight = FontWeight.Black, color = DarkNavyBackground)
                Text("Daftar seluruh aktivitas siswa", fontSize = 12.sp, color = GrayText)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF0F0F0)
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Cari riwayat...", color = GrayText, fontSize = 14.sp) },
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
            }
        }

        val list = viewModel.getFilteredCatatan().filter {
            val q = searchQuery.lowercase()
            it.namaSiswa.lowercase().contains(q) || 
            it.kelas.lowercase().contains(q) || 
            it.judulCatatan.lowercase().contains(q) ||
            it.nisn.contains(q)
        }
        if (list.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Tidak ada riwayat untuk ditampilkan.", color = GrayText)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(list) { item ->
                    HistoryCard(
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
    }
}

@Composable
fun HistoryCard(item: CatatanSiswa, onEdit: () -> Unit, onDelete: () -> Unit) {
    val isPelanggaran = item.tipe == TipeCatatan.PELANGGARAN
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Apakah Anda yakin ingin menghapus catatan untuk ${item.namaSiswa}? Data yang dihapus tidak dapat dikembalikan.") },
            confirmButton = {
                TextButton(onClick = { 
                    onDelete()
                    showDeleteConfirm = false 
                }) {
                    Text("HAPUS", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("BATAL")
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        if (isPelanggaran) MaroonPrimary.copy(0.1f) else Color(0xFFE8F5E9),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isPelanggaran) Icons.Default.WarningAmber else Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = if (isPelanggaran) MaroonPrimary else Color(0xFF2E7D32),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.namaSiswa, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkNavyBackground, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${item.kelas} • ${item.judulCatatan}", fontSize = 12.sp, color = GrayText, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(item.tanggalInput, fontSize = 10.sp, color = GrayText.copy(alpha = 0.6f))
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "${if (isPelanggaran) "-" else "+"}${item.poin}",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = if (isPelanggaran) MaroonPrimary else Color(0xFF2E7D32)
                )
                
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, null, tint = GrayText, modifier = Modifier.size(20.dp))
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = { showMenu = false; onEdit() },
                            leadingIcon = { Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp)) }
                        )
                        DropdownMenuItem(
                            text = { Text("Hapus", color = Color.Red) },
                            onClick = { showMenu = false; showDeleteConfirm = true },
                            leadingIcon = { Icon(Icons.Default.Delete, null, tint = Color.Red, modifier = Modifier.size(18.dp)) }
                        )
                    }
                }
            }
        }
    }
}
