package com.example.sigaap

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.coroutines.flow.Flow

class Converters {
    @TypeConverter
    fun fromTipeCatatan(value: TipeCatatan): String {
        return value.name
    }

    @TypeConverter
    fun toTipeCatatan(value: String): TipeCatatan {
        return try {
            TipeCatatan.valueOf(value)
        } catch (e: Exception) {
            TipeCatatan.PELANGGARAN
        }
    }
}

@Dao
interface CatatanDao {
    @Query("SELECT * FROM catatan_siswa ORDER BY tanggalInput DESC")
    fun getAllCatatan(): Flow<List<CatatanSiswa>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCatatan(catatan: CatatanSiswa)

    @Update
    suspend fun updateCatatan(catatan: CatatanSiswa)

    @Query("DELETE FROM catatan_siswa WHERE id = :id")
    suspend fun deleteCatatanById(id: String)
}

@Dao
interface KategoriDao {
    @Query("SELECT * FROM kategori_catatan")
    fun getAllKategori(): Flow<List<KategoriCatatan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKategori(kategori: KategoriCatatan)

    @Update
    suspend fun updateKategori(kategori: KategoriCatatan)

    @Delete
    suspend fun deleteKategori(kategori: KategoriCatatan)

    @Query("SELECT COUNT(*) FROM kategori_catatan")
    suspend fun getKategoriCount(): Int
}

@Database(entities = [CatatanSiswa::class, KategoriCatatan::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class SigaapDatabase : RoomDatabase() {
    abstract fun catatanDao(): CatatanDao
    abstract fun kategoriDao(): KategoriDao

    companion object {
        @Volatile
        private var INSTANCE: SigaapDatabase? = null

        fun getDatabase(context: Context): SigaapDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SigaapDatabase::class.java,
                    "sigaap_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
