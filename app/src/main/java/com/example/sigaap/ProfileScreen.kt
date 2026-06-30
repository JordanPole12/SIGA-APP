package com.example.sigaap

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sigaap.ui.theme.*

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
