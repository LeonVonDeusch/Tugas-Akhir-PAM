package com.example.tugasakhirpam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tugasakhirpam.data.LostItem
import com.example.tugasakhirpam.repository.LostItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface LostItemUiState {
    object Idle : LostItemUiState
    object Loading : LostItemUiState
    data class Success(val items: List<LostItem>) : LostItemUiState
    data class SuccessDetail(val item: LostItem) : LostItemUiState
    data class Error(val message: String) : LostItemUiState
}

sealed interface ActionState {
    object Idle : ActionState
    object Loading : ActionState
    data class Success(val message: String) : ActionState
    data class Error(val message: String) : ActionState
}

class LostItemViewModel(
    private val repository: LostItemRepository = LostItemRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<LostItemUiState>(LostItemUiState.Idle)
    val uiState: StateFlow<LostItemUiState> = _uiState.asStateFlow()

    private val _actionState = MutableStateFlow<ActionState>(ActionState.Idle)
    val actionState: StateFlow<ActionState> = _actionState.asStateFlow()

    val itemNameInput = MutableStateFlow("")
    val descriptionInput = MutableStateFlow("")
    val locationInput = MutableStateFlow("")
    val dateLostInput = MutableStateFlow("")
    val categoryIdInput = MutableStateFlow("1")
    val imageByteArrayInput = MutableStateFlow<ByteArray?>(null)

    fun loadLostItems() {
        viewModelScope.launch {
            _uiState.value = LostItemUiState.Loading
            try {
                val items = repository.getAllLostItems()
                _uiState.value = LostItemUiState.Success(items)
            } catch (e: Exception) {
                _uiState.value = LostItemUiState.Error(e.message ?: "Gagal memuat dari Supabase")
            }
        }
    }

    fun loadLostItemDetail(id: String) {
        viewModelScope.launch {
            _uiState.value = LostItemUiState.Loading
            try {
                val item = repository.getLostItemById(id)
                _uiState.value = LostItemUiState.SuccessDetail(item)
            } catch (e: Exception) {
                _uiState.value = LostItemUiState.Error("Gagal memuat detail barang")
            }
        }
    }

    fun createLostItemReport(userId: String) {
        if (itemNameInput.value.isBlank() || locationInput.value.isBlank()) {
            _actionState.value = ActionState.Error("Nama barang dan lokasi wajib diisi")
            return
        }

        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            try {
                var finalImageUrl: String? = null

                imageByteArrayInput.value?.let { bytes ->
                    val fileName = "lost_${System.currentTimeMillis()}.jpg"
                    finalImageUrl = repository.uploadItemImage(fileName, bytes)
                }

                val newItem = LostItem(
                    userId = userId,
                    categoryId = categoryIdInput.value.toIntOrNull() ?: 1,
                    itemName = itemNameInput.value,
                    description = descriptionInput.value,
                    lastSeenLocation = locationInput.value,
                    dateLost = dateLostInput.value,
                    imageUrl = finalImageUrl,
                    status = "belum"
                )

                repository.insertLostItem(newItem)
                _actionState.value = ActionState.Success("Laporan berhasil masuk ke Supabase")
                resetForm()
                loadLostItems()
            } catch (e: Exception) {
                _actionState.value = ActionState.Error(e.message ?: "Gagal mengirim laporan")
            }
        }
    }

    fun markAsFound(id: String) {
        viewModelScope.launch {
            try {
                repository.updateItemStatus(id, "sudah ditemukan")
                loadLostItemDetail(id)
            } catch (e: Exception) {
                _uiState.value = LostItemUiState.Error("Gagal update status")
            }
        }
    }

    fun resetActionState() {
        _actionState.value = ActionState.Idle
    }

    private fun resetForm() {
        itemNameInput.value = ""
        descriptionInput.value = ""
        locationInput.value = ""
        dateLostInput.value = ""
        imageByteArrayInput.value = null
    }
}