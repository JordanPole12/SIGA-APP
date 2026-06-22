package com.example.sigaap

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class PelanggaranViewModel(application: Application) : AndroidViewModel(application) {

    private val db = SigaapDatabase.getDatabase(application)
    private val catatanDao = db.catatanDao()
    private val kategoriDao = db.kategoriDao()

    // --- LOGIN STATE ---
    var isLoggedIn = mutableStateOf(false)
    var username = mutableStateOf("")
    var password = mutableStateOf("")
    var loginError = mutableStateOf<String?>(null)
    var namaGuruAktif = mutableStateOf("")
    var profileGuru = mutableStateOf(GuruProfile())

    fun login(onSuccess: () -> Unit) {
        if (username.value == "guru" && password.value == "1234") {
            isLoggedIn.value = true
            namaGuruAktif.value = "Bpk. Eka Prasetya, S.Kom"
            profileGuru.value = GuruProfile(
                nama = "Bpk. Eka Prasetya, S.Kom",
                nip = "19850312 201001 1 002",
                jabatan = "Pembina Kesiswaan / Guru Produktif PPLG",
                email = "ekaprasetya@smkn1sukabumi.sch.id"
            )
            loginError.value = null
            onSuccess()
        } else {
            loginError.value = "Username atau Password salah!"
        }
    }

    fun logout() {
        isLoggedIn.value = false
        username.value = ""
        password.value = ""
    }

    // --- DATA STATE (Room) ---
    private val _listCatatan = mutableStateListOf<CatatanSiswa>()
    val listCatatan: List<CatatanSiswa> = _listCatatan

    val listKategoriPelanggaran = mutableStateListOf<KategoriCatatan>()
    val listKategoriPrestasi = mutableStateListOf<KategoriCatatan>()
    val listTataTertib = mutableStateListOf<String>()

    init {
        // Load data from Room
        viewModelScope.launch {
            // Load Catatan
            catatanDao.getAllCatatan().collect { list ->
                _listCatatan.clear()
                _listCatatan.addAll(list)
                
                // If empty, add mock data for first time
                if (list.isEmpty()) {
                    val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
                    val tgl = sdf.format(Date())
                    val mockData = listOf(
                        CatatanSiswa(tipe = TipeCatatan.PELANGGARAN, nisn = "123", namaSiswa = "Ahmad Fauzi", kelas = "XII RPL 1", judulCatatan = "Terlambat", poin = 5, tanggalInput = tgl, namaGuruInput = "Guru"),
                        CatatanSiswa(tipe = TipeCatatan.PELANGGARAN, nisn = "124", namaSiswa = "Siti Nurhaliza", kelas = "XI RPL 2", judulCatatan = "Tidak Memakai Seragam Lengkap", poin = 5, tanggalInput = tgl, namaGuruInput = "Guru"),
                        CatatanSiswa(tipe = TipeCatatan.PELANGGARAN, nisn = "125", namaSiswa = "Budi Santoso", kelas = "X RPL 1", judulCatatan = "Rambut Panjang", poin = 10, tanggalInput = tgl, namaGuruInput = "Guru")
                    )
                    mockData.forEach { catatanDao.insertCatatan(it) }
                }
            }
        }

        viewModelScope.launch {
            // Load Kategori
            kategoriDao.getAllKategori().collect { list ->
                listKategoriPelanggaran.clear()
                listKategoriPrestasi.clear()
                
                if (list.isEmpty()) {
                    // Populate from MasterData
                    MasterData.daftarPelanggaran.forEach { kategoriDao.insertKategori(it) }
                    MasterData.daftarPrestasi.forEach { kategoriDao.insertKategori(it) }
                } else {
                    listKategoriPelanggaran.addAll(list.filter { it.tipe == TipeCatatan.PELANGGARAN })
                    listKategoriPrestasi.addAll(list.filter { it.tipe == TipeCatatan.PRESTASI })
                }
            }
        }

        // Tata tertib still static for simplicity or can be moved to DB too
        listTataTertib.addAll(MasterData.tataTertibUmum)
    }

    // Form States
    var tipeCatatan = mutableStateOf(TipeCatatan.PELANGGARAN)
    var nisn = mutableStateOf("")
    var namaSiswa = mutableStateOf("")
    var kelas = mutableStateOf("")
    var judulCatatan = mutableStateOf("") 
    var poin = mutableStateOf(0)
    var catatan = mutableStateOf("")

    // Error States
    var nisnError = mutableStateOf<String?>(null)
    var namaError = mutableStateOf<String?>(null)
    var searchQuery = mutableStateOf("")

    // --- CRUD OPERATIONS ---
    var editingId = mutableStateOf<String?>(null)

    fun simpanCatatan(onSuccess: () -> Unit) {
        var isValid = true
        if (nisn.value.isBlank()) { nisnError.value = "NISN wajib diisi"; isValid = false } else { nisnError.value = null }
        if (namaSiswa.value.isBlank()) { namaError.value = "Nama wajib diisi"; isValid = false } else { namaError.value = null }

        if (isValid) {
            viewModelScope.launch {
                val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
                val currentTgl = sdf.format(Date())
                
                if (editingId.value == null) {
                    val dataBaru = CatatanSiswa(
                        tipe = tipeCatatan.value,
                        nisn = nisn.value,
                        namaSiswa = namaSiswa.value,
                        kelas = kelas.value,
                        judulCatatan = judulCatatan.value,
                        poin = poin.value,
                        tanggalInput = currentTgl,
                        namaGuruInput = namaGuruAktif.value,
                        catatanTambahan = catatan.value
                    )
                    catatanDao.insertCatatan(dataBaru)
                } else {
                    val existing = _listCatatan.find { it.id == editingId.value }
                    if (existing != null) {
                        val updated = existing.copy(
                            tipe = tipeCatatan.value,
                            nisn = nisn.value,
                            namaSiswa = namaSiswa.value,
                            kelas = kelas.value,
                            judulCatatan = judulCatatan.value,
                            poin = poin.value,
                            catatanTambahan = catatan.value
                        )
                        catatanDao.updateCatatan(updated)
                    }
                }
                clearForm()
                onSuccess()
            }
        }
    }

    fun hapusCatatan(id: String) {
        viewModelScope.launch {
            catatanDao.deleteCatatanById(id)
        }
    }

    fun prepareEdit(catatanSiswa: CatatanSiswa) {
        editingId.value = catatanSiswa.id
        tipeCatatan.value = catatanSiswa.tipe
        nisn.value = catatanSiswa.nisn
        namaSiswa.value = catatanSiswa.namaSiswa
        kelas.value = catatanSiswa.kelas
        judulCatatan.value = catatanSiswa.judulCatatan
        poin.value = catatanSiswa.poin
        catatan.value = catatanSiswa.catatanTambahan
    }

    fun clearForm() {
        editingId.value = null
        nisn.value = ""
        namaSiswa.value = ""
        kelas.value = ""
        judulCatatan.value = ""
        poin.value = 0
        catatan.value = ""
    }

    fun getFilteredCatatan(): List<CatatanSiswa> {
        val query = searchQuery.value.lowercase()
        return if (query.isEmpty()) _listCatatan else _listCatatan.filter {
            it.namaSiswa.lowercase().contains(query) || it.kelas.lowercase().contains(query) || it.nisn.contains(query)
        }
    }

    // --- CRUD BUKU SAKU ---
    fun addRule(rule: String) {
        listTataTertib.add(0, rule)
    }
    fun updateRule(index: Int, rule: String) {
        if (index in listTataTertib.indices) listTataTertib[index] = rule
    }
    fun deleteRule(index: Int) {
        if (index in listTataTertib.indices) listTataTertib.removeAt(index)
    }

    fun addKategori(kategori: KategoriCatatan, tipe: TipeCatatan) {
        viewModelScope.launch {
            kategoriDao.insertKategori(kategori.copy(tipe = tipe))
        }
    }
    fun updateKategori(index: Int, kategori: KategoriCatatan, tipe: TipeCatatan) {
        viewModelScope.launch {
            val list = if (tipe == TipeCatatan.PELANGGARAN) listKategoriPelanggaran else listKategoriPrestasi
            if (index in list.indices) {
                val oldKategori = list[index]
                kategoriDao.updateKategori(kategori.copy(id = oldKategori.id, tipe = tipe))

                // SINKRONISASI OTOMATIS
                _listCatatan.forEach { catatan ->
                    if (catatan.tipe == tipe && catatan.judulCatatan == oldKategori.nama) {
                        catatanDao.updateCatatan(catatan.copy(
                            judulCatatan = kategori.nama,
                            poin = kategori.poin
                        ))
                    }
                }
            }
        }
    }
    fun deleteKategori(index: Int, tipe: TipeCatatan) {
        viewModelScope.launch {
            val list = if (tipe == TipeCatatan.PELANGGARAN) listKategoriPelanggaran else listKategoriPrestasi
            if (index in list.indices) {
                kategoriDao.deleteKategori(list[index])
            }
        }
    }

    // --- STATISTIK (DASHBOARD) ---
    private fun getCountInDateRange(calendarField: Int, amount: Int, tipe: TipeCatatan): Int {
        val sdfDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val sdfFull = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
        val cal = Calendar.getInstance()
        val todayStr = sdfDate.format(cal.time)
        
        if (amount == 0) {
            return _listCatatan.count { it.tanggalInput.startsWith(todayStr) && it.tipe == tipe }
        }

        cal.add(calendarField, -amount)
        val startDate = cal.time
        
        return _listCatatan.count { 
            try {
                val itemDate = sdfFull.parse(it.tanggalInput)
                itemDate != null && itemDate.after(startDate) && it.tipe == tipe
            } catch (e: Exception) { false }
        }
    }

    fun getHariIniCount(tipe: TipeCatatan) = getCountInDateRange(Calendar.DAY_OF_YEAR, 0, tipe)
    fun getMingguIniCount(tipe: TipeCatatan) = getCountInDateRange(Calendar.WEEK_OF_YEAR, 1, tipe)
    fun getBulanIniCount(tipe: TipeCatatan) = getCountInDateRange(Calendar.MONTH, 1, tipe)

    fun getSiswaPoinMinusTertinggi(): String {
        val top = _listCatatan.filter { it.tipe == TipeCatatan.PELANGGARAN }
            .groupBy { it.namaSiswa }
            .mapValues { it.value.sumOf { c -> c.poin } }
            .maxByOrNull { it.value }
        return top?.let { "${it.key} (-${it.value})" } ?: "-"
    }
}
