package com.example.sigaap

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * MODEL LAYER: Definisi Data
 * File ini berisi semua data class yang mendefinisikan struktur informasi dalam aplikasi.
 */

/**
 * Tipe Catatan: Pelanggaran (Poin Minus) atau Prestasi (Poin Plus)
 */
enum class TipeCatatan {
    PELANGGARAN, PRESTASI
}

/**
 * ENTITY: CatatanSiswa
 * Merepresentasikan satu entitas catatan aktivitas siswa di database.
 * Digunakan untuk menyimpan riwayat pelanggaran atau prestasi.
 */
@Entity(tableName = "catatan_siswa")
data class CatatanSiswa(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val tipe: TipeCatatan = TipeCatatan.PELANGGARAN,
    val nisn: String = "",
    val namaSiswa: String = "",
    val kelas: String = "",
    val judulCatatan: String = "",
    val poin: Int = 0,
    val tanggalInput: String = "",
    val namaGuruInput: String = "",
    val catatanTambahan: String = ""
)

/**
 * ENTITY: KategoriCatatan
 * Merepresentasikan referensi kategori (misal: "Terlambat", "Juara Lomba") beserta bobot poinnya.
 */
@Entity(tableName = "kategori_catatan")
data class KategoriCatatan(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nama: String,
    val poin: Int,
    val tipe: TipeCatatan, // Membedakan kategori pelanggaran vs prestasi
    val deskripsi: String = ""
) {
    override fun toString(): String = "$nama ($poin Poin)"
}

/**
 * DATA CLASS: GuruProfile
 * Struktur data untuk informasi profil pengguna (Guru) yang sedang aktif.
 */
data class GuruProfile(
    val nama: String = "",
    val nip: String = "",
    val jabatan: String = "",
    val email: String = "",
    val fotoResId: Int? = null 
)

/**
 * DATA STATIS: MasterData
 * Berisi data awal (seed data) untuk mengisi aplikasi saat pertama kali dijalankan.
 */
object MasterData {
    val daftarPelanggaran = listOf(
        KategoriCatatan(nama = "Terlambat", poin = 5, tipe = TipeCatatan.PELANGGARAN, deskripsi = "Datang setelah bel masuk berbunyi"),
        KategoriCatatan(nama = "Atribut Tidak Lengkap", poin = 5, tipe = TipeCatatan.PELANGGARAN, deskripsi = "Tidak memakai dasi, ikat pinggang, atau kaos kaki sesuai ketentuan"),
        KategoriCatatan(nama = "Rambut Tidak Rapi", poin = 10, tipe = TipeCatatan.PELANGGARAN, deskripsi = "Panjang rambut melebihi kerah baju atau menutupi telinga"),
        KategoriCatatan(nama = "Bolos", poin = 15, tipe = TipeCatatan.PELANGGARAN, deskripsi = "Meninggalkan sekolah tanpa izin pada jam KBM"),
        KategoriCatatan(nama = "Merokok", poin = 50, tipe = TipeCatatan.PELANGGARAN, deskripsi = "Membawa atau menggunakan rokok di lingkungan sekolah"),
        KategoriCatatan(nama = "Tindakan Kriminal", poin = 100, tipe = TipeCatatan.PELANGGARAN, deskripsi = "Perjudian, pencurian, atau penganiayaan")
    )

    val daftarPrestasi = listOf(
        KategoriCatatan(nama = "Juara Lomba Nasional", poin = 100, tipe = TipeCatatan.PRESTASI, deskripsi = "Juara 1, 2, atau 3 tingkat Nasional"),
        KategoriCatatan(nama = "Juara Lomba Provinsi", poin = 75, tipe = TipeCatatan.PRESTASI, deskripsi = "Juara 1, 2, atau 3 tingkat Provinsi"),
        KategoriCatatan(nama = "Juara Lomba Kota", poin = 50, tipe = TipeCatatan.PRESTASI, deskripsi = "Juara 1, 2, atau 3 tingkat Kota/Kabupaten"),
        KategoriCatatan(nama = "Pengurus OSIS Aktif", poin = 20, tipe = TipeCatatan.PRESTASI, deskripsi = "Berpartisipasi aktif dalam kegiatan OSIS"),
        KategoriCatatan(nama = "Peringkat 1 Kelas", poin = 30, tipe = TipeCatatan.PRESTASI, deskripsi = "Mendapatkan ranking 1 di akhir semester"),
        KategoriCatatan(nama = "Aksi Sosial/Kemanusiaan", poin = 40, tipe = TipeCatatan.PRESTASI, deskripsi = "Melakukan aksi heroik atau bantuan kemanusiaan")
    )

    val daftarKelas = listOf(
        "X PPLG 1", "X PPLG 2", "X TKJT 1", "X TKJT 2", "X AKL 1", "X AKL 2",
        "XI PPLG 1", "XI PPLG 2", "XI TKJT 1", "XI TKJT 2", "XI AKL 1", "XI AKL 2",
        "XII PPLG 1", "XII PPLG 2", "XII TKJT 1", "XII TKJT 2", "XII AKL 1", "XII AKL 2"
    )

    val tataTertibUmum = listOf(
        "Siswa wajib hadir di sekolah paling lambat 15 menit sebelum bel masuk.",
        "Siswa wajib mengikuti upacara bendera setiap hari Senin.",
        "Siswa dilarang membawa senjata tajam, narkoba, dan barang terlarang lainnya.",
        "Siswa wajib menjaga kebersihan kelas dan lingkungan sekolah.",
        "Penggunaan smartphone hanya diperbolehkan atas izin guru pengajar."
    )
}

