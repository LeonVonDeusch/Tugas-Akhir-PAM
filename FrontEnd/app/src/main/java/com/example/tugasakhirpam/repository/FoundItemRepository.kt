package com.example.tugasakhirpam.repository

import com.example.tugasakhirpam.data.SupabaseClientProvider
import com.example.tugasakhirpam.data.model.Category
import com.example.tugasakhirpam.data.model.FoundItem
import com.example.tugasakhirpam.data.model.FoundItemInsert
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import java.util.UUID

class FoundItemRepository {

    private val supabase = SupabaseClientProvider.client

    /*
     * Mengambil ID user yang sedang login.
     * Digunakan saat membuat laporan baru agar laporan terhubung ke user yang benar.
     */
    fun getCurrentUserId(): String {
        return supabase.auth.currentUserOrNull()?.id ?: ""
    }

    /*
     * Mengambil semua laporan barang ditemukan dari database.
     * Diurutkan dari yang terbaru.
     */
    suspend fun getAllFoundItems(): List<FoundItem> {
        return supabase.from("found_items")
            .select()
            .decodeList<FoundItem>()
    }

    /*
     * Mengambil satu laporan berdasarkan ID-nya.
     * Digunakan untuk halaman detail laporan.
     */
    suspend fun getFoundItemById(id: String): FoundItem {
        return supabase.from("found_items")
            .select {
                filter { eq("id", id) }
            }
            .decodeSingle<FoundItem>()
    }

    /*
     * Membuat laporan barang ditemukan baru ke database.
     * Mengembalikan data yang baru dibuat (termasuk id yang di-generate Supabase).
     */
    suspend fun createFoundItem(item: FoundItemInsert): FoundItem {
        return supabase.from("found_items")
            .insert(item) {
                select()
            }
            .decodeSingle<FoundItem>()
    }

    /*
     * Mengubah status laporan menjadi "sudah" (barang sudah diklaim/dikembalikan).
     * Dipanggil dari halaman detail oleh pelapor.
     */
    suspend fun updateStatus(id: String, status: String) {
        supabase.from("found_items")
            .update({ set("status", status) }) {
                filter { eq("id", id) }
            }
    }

    /*
     * Mengupload foto barang ke Supabase Storage bucket "lacak-images".
     * Mengembalikan URL publik foto yang kemudian disimpan di kolom image_url.
     *
     * imageBytes: isi file gambar dalam bentuk ByteArray
     * fileExtension: ekstensi file, contoh "jpg" atau "png"
     */
    suspend fun uploadImage(imageBytes: ByteArray, fileExtension: String): String {
        // Buat nama file unik agar tidak bentrok antar laporan
        val fileName = "found_items/${UUID.randomUUID()}.$fileExtension"

        supabase.storage.from("lacak-images")
            .upload(fileName, imageBytes)

        return supabase.storage.from("lacak-images")
            .publicUrl(fileName)
    }

    /*
     * Mengambil semua kategori barang.
     * Digunakan untuk dropdown pilihan kategori di form laporan.
     */
    suspend fun getCategories(): List<Category> {
        return supabase.from("categories")
            .select()
            .decodeList<Category>()
    }
}
