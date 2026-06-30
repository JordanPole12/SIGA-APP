package com.example.sigaap

/**
 * BAGIAN: VIEW - INPUT DATA
 * File ini menangani tampilan Form untuk menginput data pelanggaran atau prestasi.
 * Layar ini mengirimkan data yang diketik user ke ViewModel untuk diolah.
 */

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sigaap.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputCatatanScreen(
    viewModel: PelanggaranViewModel, // Menghubungkan View dengan Logika (ViewModel)
    onBack: () -> Unit // Menghubungkan View ini kembali ke Navigasi Utama (SigaapUI)
) {
    val context = LocalContext.current
    var expandedKelas by remember { mutableStateOf(false) }
    var expandedKategori by remember { mutableStateOf(false) }
    
    // Mengecek apakah sedang mode Edit atau Tambah Baru
    val isEdit = viewModel.editingId.value != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6F8))
    ) {
        // --- HEADER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .statusBarsPadding()
                .padding(bottom = 20.dp, start = 16.dp, end = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.background(GrayLight, CircleShape)
                ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = DarkNavyBackground) }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    if (isEdit) "Edit Catatan" else "Form Catatan Baru", 
                    fontSize = 20.sp, 
                    fontWeight = FontWeight.Black, 
                    color = DarkNavyBackground
                )
            }
        }

        // --- FORM INPUT ---
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = White),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Pilih Tipe Catatan", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GrayText)
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Menghubungkan pilihan tipe ke State di ViewModel
                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            SegmentedButton(
                                selected = viewModel.tipeCatatan.value == TipeCatatan.PELANGGARAN,
                                onClick = { 
                                    if (!isEdit) {
                                        viewModel.tipeCatatan.value = TipeCatatan.PELANGGARAN
                                        viewModel.judulCatatan.value = "" 
                                    }
                                },
                                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                                enabled = !isEdit
                            ) { Text("Pelanggaran", fontSize = 13.sp) }
                            SegmentedButton(
                                selected = viewModel.tipeCatatan.value == TipeCatatan.PRESTASI,
                                onClick = { 
                                    if (!isEdit) {
                                        viewModel.tipeCatatan.value = TipeCatatan.PRESTASI
                                        viewModel.judulCatatan.value = "" 
                                    }
                                },
                                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                                enabled = !isEdit
                            ) { Text("Prestasi", fontSize = 13.sp) }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // --- INPUT IDENTITAS SISWA ---
                        Text("Informasi Siswa", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GrayText)
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = viewModel.nisn.value,
                            onValueChange = { viewModel.nisn.value = it },
                            label = { Text("NIS / NISN") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            isError = viewModel.nisnError.value != null,
                            supportingText = { viewModel.nisnError.value?.let { Text(it) } }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = viewModel.namaSiswa.value,
                            onValueChange = { viewModel.namaSiswa.value = it },
                            label = { Text("Nama Lengkap Siswa") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            isError = viewModel.namaError.value != null,
                            supportingText = { viewModel.namaError.value?.let { Text(it) } }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Dropdown Kelas: Mengambil data statis dari MasterData
                        ExposedDropdownMenuBox(
                            expanded = expandedKelas,
                            onExpandedChange = { expandedKelas = !expandedKelas }
                        ) {
                            OutlinedTextField(
                                value = viewModel.kelas.value,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Kelas") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedKelas) },
                                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            )
                            ExposedDropdownMenu(expanded = expandedKelas, onDismissRequest = { expandedKelas = false }) {
                                MasterData.daftarKelas.forEach { k ->
                                    DropdownMenuItem(
                                        text = { Text(k) },
                                        onClick = { viewModel.kelas.value = k; expandedKelas = false }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // --- INPUT DETAIL CATATAN ---
                        Text("Detail Catatan", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GrayText)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Mengambil list kategori dari ViewModel (yang sudah diambil dari Database)
                        val kategoriList = if (viewModel.tipeCatatan.value == TipeCatatan.PELANGGARAN) 
                            viewModel.listKategoriPelanggaran 
                        else 
                            viewModel.listKategoriPrestasi

                        ExposedDropdownMenuBox(
                            expanded = expandedKategori,
                            onExpandedChange = { expandedKategori = !expandedKategori }
                        ) {
                            OutlinedTextField(
                                value = viewModel.judulCatatan.value,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Kategori / Jenis Catatan") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedKategori) },
                                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            )
                            ExposedDropdownMenu(expanded = expandedKategori, onDismissRequest = { expandedKategori = false }) {
                                kategoriList.forEach { kat ->
                                    DropdownMenuItem(
                                        text = { 
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text(kat.nama, fontWeight = FontWeight.Medium)
                                                Text("${kat.poin} Poin", color = GrayText, fontSize = 12.sp)
                                            }
                                        },
                                        onClick = {
                                            viewModel.judulCatatan.value = kat.nama
                                            viewModel.poin.value = kat.poin
                                            expandedKategori = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = viewModel.catatan.value,
                            onValueChange = { viewModel.catatan.value = it },
                            label = { Text("Keterangan Tambahan / Kronologi") },
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            shape = RoundedCornerShape(14.dp)
                        )
                    }
                }
            }

            // --- TOMBOL SIMPAN ---
            item {
                Button(
                    onClick = {
                        // Menjalankan Logika Simpan yang ada di ViewModel
                        viewModel.simpanCatatan {
                            Toast.makeText(context, if (isEdit) "Data diperbarui!" else "Data berhasil disimpan!", Toast.LENGTH_SHORT).show()
                            onBack() // Navigasi kembali setelah sukses
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (viewModel.tipeCatatan.value == TipeCatatan.PELANGGARAN) MaroonPrimary else Color(0xFF2E7D32)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(4.dp)
                ) {
                    Icon(if (isEdit) Icons.Default.Update else Icons.Default.Save, null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(if (isEdit) "UPDATE CATATAN" else "SIMPAN CATATAN", fontWeight = FontWeight.Black, fontSize = 16.sp)
                }
            }
            
            if (isEdit) {
                item {
                    OutlinedButton(
                        onClick = { viewModel.clearForm(); onBack() },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GrayText)
                    ) {
                        Text("BATAL")
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }
}

