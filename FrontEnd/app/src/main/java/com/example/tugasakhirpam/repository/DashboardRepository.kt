package com.example.tugasakhirpam.repository

import com.example.tugasakhirpam.data.Category
import com.example.tugasakhirpam.data.DashboardStats
import com.example.tugasakhirpam.data.SupabaseClientProvider.client
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

class DashboardRepository {

    suspend fun getCategories(): List<Category> {
        return client.postgrest["categories"]
            .select()
            .decodeList<Category>()
            .sortedBy { it.name.lowercase() }
    }

    suspend fun getCategoryById(id: Long): Category {
        return client.postgrest["categories"]
            .select {
                filter { eq("id", id) }
            }
            .decodeSingle<Category>()
    }

    suspend fun createCategory(name: String, icon: String?) {
        client.postgrest["categories"].insert(
            Category(
                name = name.trim(),
                icon = icon?.takeIf { it.isNotBlank() }
            )
        )
    }

    suspend fun updateCategory(id: Long, name: String, icon: String?) {
        client.postgrest["categories"].update(
            {
                set("name", name.trim())
                set("icon", icon?.takeIf { it.isNotBlank() })
            }
        ) {
            filter { eq("id", id) }
        }
    }

    suspend fun getDashboardStats(): DashboardStats {
        return DashboardStats(
            lostItemCount = countRows("lost_items"),
            foundItemCount = countRows("found_items"),
            claimCount = countRows("claims")
        )
    }

    private suspend fun countRows(tableName: String): Int {
        return client.postgrest[tableName]
            .select()
            .decodeList<RowIdentity>()
            .size
    }
}

@Serializable
private data class RowIdentity(
    @SerialName("id") val id: JsonElement? = null
)
