package com.example.tugasakhirpam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tugasakhirpam.data.model.Category
import com.example.tugasakhirpam.data.model.FoundItem
import com.example.tugasakhirpam.data.model.FoundItemInsert
import com.example.tugasakhirpam.repository.FoundItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ---------------------------------------------------------------------------
// State classes
// ---------------------------------------------------------------------------

/*
 * State untuk halaman list semua barang ditemukan.
 */
sealed class FoundItemListState {
    object Loading : FoundItemListState()
    data class Success(val items: List<FoundItem>) : FoundItemListState()
    data class Error(val message: String) : FoundItemListState()
}

/*
 * State untuk halaman detail satu barang.
 */
sealed class FoundItemDetailState {
    object Idle : FoundItemDetailState()
    object Loading : FoundItemDetailState()
    data class Success(val item: FoundItem) : FoundItemDetailState()
    data class Error(val message: String) : FoundItemDetailState()
}

/*
 * State untuk form tambah laporan & upload gambar.
 */
sealed class FoundItemFormState {
    object Idle : FoundItemFormState()
    object Loading : FoundItemFormState()
    object Success : FoundItemFormState()
    data class Error(val message: String) : FoundItemFormState()
}

// ---------------------------------------------------------------------------
// ViewModel
// ---------------------------------------------------------------------------

class FoundItemViewModel : ViewModel() {

    private val repository = FoundItemRepository()

    // --- List state ---
    private val _listState = MutableStateFlow<FoundItemListState>(FoundItemListState.Loading)
    val listState: StateFlow<FoundItemListState> = _listState

    // --- Detail state ---
    private val _detailState = MutableStateFlow<FoundItemDetailState>(FoundItemDetailState.Idle)
    val detailState: StateFlow<FoundItemDetailState> = _detailState

    // --- Form state ---
    private val _formState = MutableStateFlow<FoundItemFormState>(FoundItemFormState.Idle)
    val formState: StateFlow<FoundItemFormState> = _formState

    // --- Form input state ---
    private val _itemName = MutableStateFlow("")
    val itemName: StateFlow<String> = _itemName

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description

    private val _foundLocation = MutableStateFlow("")
    val foundLocation: StateFlow<String> = _foundLocation

    private val _dateFound = MutableStateFlow("")
    val dateFound: StateFlow<String> = _dateFound

    private val _selectedCategoryId = MutableStateFlow<Int?>(null)
    val selectedCategoryId: StateFlow<Int?> = _selectedCategoryId

    // --- Kategori untuk dropdown ---
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    // --- Status update state ---
    private val _updateStatusState = MutableStateFlow<FoundItemFormState>(FoundItemFormState.Idle)
    val updateStatusState: StateFlow<FoundItemFormState> = _updateStatusState

    /*
     * ID user yang sedang login, digunakan saat membuat laporan baru.
     */
    val currentUserId: String get() = repository.getCurrentUserId()

    init {
        loadFoundItems()
        loadCategories()
    }

    // ---------------------------------------------------------------------------
    // Input handlers
    // ---------------------------------------------------------------------------

    fun onItemNameChange(value: String) { _itemName.value = value }
    fun onDescriptionChange(value: String) { _description.value = value }
    fun onFoundLocationChange(value: String) { _foundLocation.value = value }
    fun onDateFoundChange(value: String) { _dateFound.value = value }
    fun onCategorySelected(id: Int?) { _selectedCategoryId.value = id }

    // ---------------------------------------------------------------------------
    // Load list
    // ---------------------------------------------------------------------------

    /*
     * Mengambil semua laporan barang ditemukan dari Supabase.
     * Dipanggil saat ViewModel dibuat dan bisa dipanggil ulang untuk refresh.
     */
    fun loadFoundItems() {
        viewModelScope.launch {
            _listState.value = FoundItemListState.Loading
            try {
                val items = repository.getAllFoundItems()
                _listState.value = FoundItemListState.Success(items)
            } catch (e: Exception) {
                _listState.value = FoundItemListState.Error(e.message ?: "Gagal memuat data")
            }
        }
    }

    // ---------------------------------------------------------------------------
    // Load detail
    // ---------------------------------------------------------------------------

    /*
     * Mengambil detail satu laporan berdasarkan ID.
     * Dipanggil saat user membuka halaman detail.
     */
    fun loadFoundItemById(id: String) {
        viewModelScope.launch {
            _detailState.value = FoundItemDetailState.Loading
            try {
                val item = repository.getFoundItemById(id)
                _detailState.value = FoundItemDetailState.Success(item)
            } catch (e: Exception) {
                _detailState.value = FoundItemDetailState.Error(e.message ?: "Gagal memuat detail")
            }
        }
    }

    // ---------------------------------------------------------------------------
    // Create laporan
    // ---------------------------------------------------------------------------

    /*
     * Membuat laporan barang ditemukan baru.
     * Jika ada gambar, upload dulu ke Storage baru simpan URL-nya ke database.
     *
     * imageBytes: isi file gambar (null jika tidak ada foto)
     * fileExtension: ekstensi file gambar, contoh "jpg"
     */
    fun createFoundItem(imageBytes: ByteArray? = null, fileExtension: String = "jpg") {
        viewModelScope.launch {
            _formState.value = FoundItemFormState.Loading
            try {
                // Upload gambar jika ada, dapatkan URL-nya
                val imageUrl = if (imageBytes != null) {
                    repository.uploadImage(imageBytes, fileExtension)
                } else null

                val newItem = FoundItemInsert(
                    userId = currentUserId,
                    categoryId = _selectedCategoryId.value,
                    itemName = _itemName.value,
                    description = _description.value,
                    foundLocation = _foundLocation.value,
                    dateFound = _dateFound.value,
                    imageUrl = imageUrl
                )

                repository.createFoundItem(newItem)
                _formState.value = FoundItemFormState.Success

                // Reset form setelah berhasil
                resetForm()
                // Refresh list agar laporan baru langsung muncul
                loadFoundItems()

            } catch (e: Exception) {
                _formState.value = FoundItemFormState.Error(e.message ?: "Gagal membuat laporan")
            }
        }
    }

    // ---------------------------------------------------------------------------
    // Update status
    // ---------------------------------------------------------------------------

    /*
     * Mengubah status laporan (misal dari "belum" menjadi "sudah diklaim").
     * Setelah berhasil, detail laporan di-refresh otomatis.
     */
    fun updateStatus(id: String, status: String) {
        viewModelScope.launch {
            _updateStatusState.value = FoundItemFormState.Loading
            try {
                repository.updateStatus(id, status)
                _updateStatusState.value = FoundItemFormState.Success
                loadFoundItemById(id)
            } catch (e: Exception) {
                _updateStatusState.value = FoundItemFormState.Error(e.message ?: "Gagal update status")
            }
        }
    }

    // ---------------------------------------------------------------------------
    // Load categories
    // ---------------------------------------------------------------------------

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                _categories.value = repository.getCategories()
            } catch (e: Exception) {
                // Tidak perlu tampilkan error khusus, form tetap bisa dipakai tanpa kategori
            }
        }
    }

    // ---------------------------------------------------------------------------
    // Reset
    // ---------------------------------------------------------------------------

    fun resetForm() {
        _itemName.value = ""
        _description.value = ""
        _foundLocation.value = ""
        _dateFound.value = ""
        _selectedCategoryId.value = null
        _formState.value = FoundItemFormState.Idle
    }

    fun resetUpdateStatus() {
        _updateStatusState.value = FoundItemFormState.Idle
    }
}
