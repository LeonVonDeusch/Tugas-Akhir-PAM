package com.example.tugasakhirpam.repository

import com.example.tugasakhirpam.data.LostItem
import com.example.tugasakhirpam.data.SupabaseClientProvider.client
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage

class LostItemRepository {

    suspend fun getAllLostItems(): List<LostItem> {
        return client.postgrest["lost_items"]
            .select()
            .decodeList<LostItem>()
    }

    suspend fun getLostItemById(id: String): LostItem {
        return client.postgrest["lost_items"]
            .select {
                filter { eq("id", id) }
            }.decodeSingle<LostItem>()
    }

    suspend fun insertLostItem(item: LostItem) {
        client.postgrest["lost_items"].insert(item)
    }

    suspend fun updateItemStatus(id: String, newStatus: String) {
        client.postgrest["lost_items"].update(
            { set("status", newStatus) }
        ) {
            filter { eq("id", id) }
        }
    }

    suspend fun uploadItemImage(fileName: String, byteArray: ByteArray): String {
        val bucket = client.storage["lacak-images"]
        bucket.upload(fileName, byteArray)
        return bucket.publicUrl(fileName)
    }
}