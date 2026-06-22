package com.example.sigaap

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sigaap.ui.theme.*
import java.util.*

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

// --- 1. LOGIN SCREEN ---
@Composable
fun LoginScreen(viewModel: PelanggaranViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(MaroonPrimary, DarkNavyBackground)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Security,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "SI-GAPP",
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                color = White,
                letterSpacing = 2.sp
            )
            Text(
                "Sistem Garda Penegakan Peraturan",
                fontSize = 14.sp,
                color = White.copy(alpha = 0.9f),
                fontWeight = FontWeight.Bold
            )
            Text(
                "SMK NEGERI 1 KOTA SUKABUMI",
                fontSize = 12.sp,
                color = White.copy(alpha = 0.7f),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Selamat Datang, Guru",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkNavyBackground
                    )
                    Text(
                        "Silahkan login untuk mengelola data",
                        fontSize = 12.sp,
                        color = GrayText
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))

                    OutlinedTextField(
                        value = viewModel.username.value,
                        onValueChange = { viewModel.username.value = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = MaroonPrimary) },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = viewModel.password.value,
                        onValueChange = { viewModel.password.value = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = MaroonPrimary) },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )

                    if (viewModel.loginError.value != null) {
                        Text(
                            viewModel.loginError.value!!,
                            color = Color.Red,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { viewModel.login {} },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaroonPrimary),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("MASUK SEKARANG", fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

// --- 2. DASHBOARD SCREEN ---
@Composable
fun GuruDashboardScreen(viewModel: PelanggaranViewModel, onNavigateToInput: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val currentTipe = if (selectedTab == 0) TipeCatatan.PELANGGARAN else TipeCatatan.PRESTASI
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

// --- 3. INPUT SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputCatatanScreen(viewModel: PelanggaranViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    var expandedKelas by remember { mutableStateOf(false) }
    var expandedKategori by remember { mutableStateOf(false) }
    val isEdit = viewModel.editingId.value != null

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

                        Text("Detail Catatan", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GrayText)
                        Spacer(modifier = Modifier.height(12.dp))

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

            item {
                Button(
                    onClick = {
                        viewModel.simpanCatatan {
                            Toast.makeText(context, if (isEdit) "Data diperbarui!" else "Data berhasil disimpan!", Toast.LENGTH_SHORT).show()
                            onBack()
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

// --- 4. RIWAYAT SCREEN ---
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

// --- 5. BUKU SAKU SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BukuSakuScreen(viewModel: PelanggaranViewModel) {
    var selectedSection by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    
    var showAddDialog by remember { mutableStateOf(false) }
    var editIndex by remember { mutableStateOf<Int?>(null) }
    
    var ruleText by remember { mutableStateOf("") }
    var catNama by remember { mutableStateOf("") }
    var catPoin by remember { mutableStateOf("") }
    var catDesc by remember { mutableStateOf("") }
    
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

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val q = searchQuery.lowercase()
                when (selectedSection) {
                    0 -> {
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
                    1 -> {
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
                    2 -> {
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
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

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

// --- 6. PROFILE SCREEN ---
@Composable
fun ProfileGuruScreen(viewModel: PelanggaranViewModel) {
    val profile = viewModel.profileGuru.value
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6F8))
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(MaroonPrimary, DarkNavyBackground)
                        )
                    )
                    .statusBarsPadding()
            )
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 130.dp, bottom = 16.dp, start = 24.dp, end = 24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(GrayLight)
                            .padding(4.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = White,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                tint = GrayText
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = profile.nama, 
                        fontSize = 20.sp, 
                        fontWeight = FontWeight.Black, 
                        color = DarkNavyBackground,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = profile.jabatan, 
                        fontSize = 12.sp, 
                        color = GrayText, 
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(GrayLight.copy(alpha = 0.5f))
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Aktivitas", fontSize = 10.sp, color = GrayText, fontWeight = FontWeight.Bold)
                            Text(
                                viewModel.listCatatan.count { it.namaGuruInput == profile.nama }.toString(), 
                                fontSize = 18.sp, 
                                fontWeight = FontWeight.Black, 
                                color = MaroonPrimary
                            )
                        }
                        Box(modifier = Modifier.width(1.dp).height(32.dp).background(GrayText.copy(alpha = 0.2f)))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Status", fontSize = 10.sp, color = GrayText, fontWeight = FontWeight.Bold)
                            Text("Aktif", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF4CAF50))
                        }
                    }
                }
            }
        }
        
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                "INFORMASI AKUN", 
                fontSize = 12.sp, 
                fontWeight = FontWeight.Black, 
                color = GrayText, 
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            ProfileItem(Icons.Default.Badge, "NIP / ID", profile.nip)
            ProfileItem(Icons.Default.Email, "Email Instansi", profile.email)
            ProfileItem(Icons.Default.Settings, "Versi Aplikasi", "1.0.4-stable")
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { viewModel.logout() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF1F0)),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, null, tint = Color.Red, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("KELUAR APLIKASI", color = Color.Red, fontWeight = FontWeight.Black, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun ProfileItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = MaroonPrimary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 10.sp, color = GrayText, fontWeight = FontWeight.Bold)
            Text(value, fontSize = 15.sp, color = DarkNavyBackground, fontWeight = FontWeight.SemiBold)
        }
    }
}
