package com.example.sigaap

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sigaap.ui.theme.*

/**
 * VIEW LAYER: BukuSakuScreen
 * Menampilkan pedoman tata tertib, daftar kategori pelanggaran, dan prestasi.
 * 
 * Implementasi MVVM:
 * - Mengakses state list (Tata Tertib, Kategori) dari [PelanggaranViewModel].
 * - Menyediakan antarmuka untuk menambah/mengedit/menghapus aturan (CRUD statis di VM).
 * - Menggunakan state internal untuk pengelolaan dialog dan form input sementara.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BukuSakuScreen(viewModel: PelanggaranViewModel) {
    // State lokal untuk navigasi tab dan pencarian
    var selectedSection by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    
    // State lokal untuk pengelolaan dialog tambah/edit
    var showAddDialog by remember { mutableStateOf(false) }
    var editIndex by remember { mutableStateOf<Int?>(null) }
    
    // Form temporary state
    var ruleText by remember { mutableStateOf("") }
    var catNama by remember { mutableStateOf("") }
    var catPoin by remember { mutableStateOf("") }
    var catDesc by remember { mutableStateOf("") }
    
    // Dialog Reusable untuk Tambah/Edit Aturan atau Kategori
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { 
                showAddDialog = false
                editIndex = null
                ruleText = ""; catNama = ""; catPoin = ""; catDesc = ""
            },
            title = { Text(if (editIndex == null) "Tambah Data" else "Edit Data") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Form dinamis tergantung tab mana yang aktif
                    if (selectedSection == 0) {
                        OutlinedTextField(
                            value = ruleText,
                            onValueChange = { ruleText = it },
                            label = { Text("Isi Tata Tertib") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        OutlinedTextField(
                            value = catNama,
                            onValueChange = { catNama = it },
                            label = { Text("Nama Kategori") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = catPoin,
                            onValueChange = { catPoin = it },
                            label = { Text("Poin") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = catDesc,
                            onValueChange = { catDesc = it },
                            label = { Text("Deskripsi (Opsional)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    /**
                     * EVENT DISPATCH:
                     * Mengirim data ke ViewModel untuk disimpan di state aplikasi.
                     */
                    if (selectedSection == 0) {
                        if (ruleText.isNotBlank()) {
                            if (editIndex == null) viewModel.addRule(ruleText)
                            else viewModel.updateRule(editIndex!!, ruleText)
                        }
                    } else {
                        val p = catPoin.toIntOrNull() ?: 0
                        val tipe = if (selectedSection == 1) TipeCatatan.PELANGGARAN else TipeCatatan.PRESTASI
                        val cat = KategoriCatatan(nama = catNama, poin = p, tipe = tipe, deskripsi = catDesc)
                        if (catNama.isNotBlank()) {
                            if (editIndex == null) viewModel.addKategori(cat, tipe)
                            else viewModel.updateKategori(editIndex!!, cat, tipe)
                        }
                    }
                    showAddDialog = false
                    editIndex = null
                    ruleText = ""; catNama = ""; catPoin = ""; catDesc = ""
                }) { Text("Simpan") }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showAddDialog = false
                    editIndex = null
                    ruleText = ""; catNama = ""; catPoin = ""; catDesc = ""
                }) { Text("Batal") }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            // Tombol melayang untuk memicu dialog tambah data
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaroonPrimary,
                contentColor = White
            ) {
                Icon(Icons.Default.Add, "Tambah")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F6F8))
        ) {
            // Header dengan Tab Bar dan Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(White)
                    .statusBarsPadding()
                    .padding(bottom = 10.dp, start = 20.dp, end = 20.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoStories, null, tint = MaroonPrimary, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Buku Saku Digital", fontSize = 24.sp, fontWeight = FontWeight.Black, color = DarkNavyBackground)
                            Text("Pedoman Tata Tertib Siswa", fontSize = 12.sp, color = GrayText)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF0F0F0)
                    ) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Cari informasi tata tertib...", color = GrayText, fontSize = 14.sp) },
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

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Tab navigasi untuk filter konten (Tata Tertib, Pelanggaran, Prestasi)
                    SecondaryScrollableTabRow(
                        selectedTabIndex = selectedSection,
                        containerColor = Color.Transparent,
                        contentColor = MaroonPrimary,
                        edgePadding = 0.dp,
                        divider = {}
                    ) {
                        Tab(selected = selectedSection == 0, onClick = { selectedSection = 0 }) {
                            Text("Tata Tertib", modifier = Modifier.padding(vertical = 12.dp), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Tab(selected = selectedSection == 1, onClick = { selectedSection = 1 }) {
                            Text("Pelanggaran", modifier = Modifier.padding(vertical = 12.dp), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Tab(selected = selectedSection == 2, onClick = { selectedSection = 2 }) {
                            Text("Prestasi", modifier = Modifier.padding(vertical = 12.dp), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            /**
             * LIST CONTENT:
             * Menampilkan data berdasarkan tab yang aktif dan hasil pencarian.
             */
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val q = searchQuery.lowercase()
                when (selectedSection) {
                    0 -> { // Bagian Tata Tertib
                        val rules = viewModel.listTataTertib
                        itemsIndexed(rules) { index, rule ->
                            if (rule.lowercase().contains(q)) {
                                RuleCard(
                                    rule = rule,
                                    onEdit = {
                                        editIndex = index
                                        ruleText = rule
                                        showAddDialog = true
                                    },
                                    onDelete = { viewModel.deleteRule(index) }
                                )
                            }
                        }
                    }
                    1 -> { // Bagian Kategori Pelanggaran
                        val items = viewModel.listKategoriPelanggaran
                        itemsIndexed(items) { index, item ->
                            if (item.nama.lowercase().contains(q) || item.deskripsi.lowercase().contains(q)) {
                                CategoryDetailCard(
                                    nama = item.nama, 
                                    poin = item.poin, 
                                    deskripsi = item.deskripsi, 
                                    isNegative = true,
                                    onEdit = {
                                        editIndex = index
                                        catNama = item.nama
                                        catPoin = item.poin.toString()
                                        catDesc = item.deskripsi
                                        showAddDialog = true
                                    },
                                    onDelete = { viewModel.deleteKategori(index, TipeCatatan.PELANGGARAN) }
                                )
                            }
                        }
                    }
                    2 -> { // Bagian Kategori Prestasi
                        val items = viewModel.listKategoriPrestasi
                        itemsIndexed(items) { index, item ->
                            if (item.nama.lowercase().contains(q) || item.deskripsi.lowercase().contains(q)) {
                                CategoryDetailCard(
                                    nama = item.nama, 
                                    poin = item.poin, 
                                    deskripsi = item.deskripsi, 
                                    isNegative = false,
                                    onEdit = {
                                        editIndex = index
                                        catNama = item.nama
                                        catPoin = item.poin.toString()
                                        catDesc = item.deskripsi
                                        showAddDialog = true
                                    },
                                    onDelete = { viewModel.deleteKategori(index, TipeCatatan.PRESTASI) }
                                )
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) } // Padding bawah agar FAB tidak menutupi item terakhir
            }
        }
    }
}

/**
 * Komponen untuk menampilkan satu butir tata tertib.
 */
@Composable
fun RuleCard(rule: String, onEdit: () -> Unit, onDelete: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                rule,
                modifier = Modifier.weight(1f),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = DarkNavyBackground,
                fontWeight = FontWeight.Medium
            )
            // Menu aksi (Edit/Hapus)
            Box {
                IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.MoreVert, null, tint = GrayText)
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = { showMenu = false; onEdit() },
                        leadingIcon = { Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp)) }
                    )
                    DropdownMenuItem(
                        text = { Text("Hapus", color = Color.Red) },
                        onClick = { showMenu = false; onDelete() },
                        leadingIcon = { Icon(Icons.Default.Delete, null, tint = Color.Red, modifier = Modifier.size(18.dp)) }
                    )
                }
            }
        }
    }
}

/**
 * Komponen untuk menampilkan detail kategori (Nama, Poin, Deskripsi).
 */
@Composable
fun CategoryDetailCard(
    nama: String, 
    poin: Int, 
    deskripsi: String, 
    isNegative: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(nama, fontWeight = FontWeight.Black, fontSize = 16.sp, color = DarkNavyBackground)
                if (deskripsi.isNotBlank()) {
                    Text(deskripsi, fontSize = 12.sp, color = GrayText, lineHeight = 16.sp)
                }
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Indikator Poin (Merah untuk pelanggaran, Hijau untuk prestasi)
                Surface(
                    color = if (isNegative) Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "${if (isNegative) "-" else "+"}$poin",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Black,
                        color = if (isNegative) Color(0xFFD32F2F) else Color(0xFF2E7D32)
                    )
                }
                
                // Menu aksi (Edit/Hapus)
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, null, tint = GrayText)
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = { showMenu = false; onEdit() },
                            leadingIcon = { Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp)) }
                        )
                        DropdownMenuItem(
                            text = { Text("Hapus", color = Color.Red) },
                            onClick = { showMenu = false; onDelete() },
                            leadingIcon = { Icon(Icons.Default.Delete, null, tint = Color.Red, modifier = Modifier.size(18.dp)) }
                        )
                    }
                }
            }
        }
    }
}

