package com.example.tugasakhirpam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tugasakhirpam.data.Category
import com.example.tugasakhirpam.data.DashboardStats
import com.example.tugasakhirpam.repository.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface DashboardUiState {
    object Loading : DashboardUiState
    data class Success(
        val stats: DashboardStats,
        val categories: List<Category>
    ) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

sealed interface DashboardActionState {
    object Idle : DashboardActionState
    object Loading : DashboardActionState
    data class Success(val message: String) : DashboardActionState
    data class Error(val message: String) : DashboardActionState
}

data class CategoryEditorState(
    val isVisible: Boolean = false,
    val categoryId: Long? = null,
    val name: String = "",
    val icon: String = DashboardViewModel.DEFAULT_CATEGORY_ICON
) {
    val isEditing: Boolean get() = categoryId != null
}

class DashboardViewModel(
    private val repository: DashboardRepository = DashboardRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _actionState = MutableStateFlow<DashboardActionState>(DashboardActionState.Idle)
    val actionState: StateFlow<DashboardActionState> = _actionState.asStateFlow()

    private val _editorState = MutableStateFlow(CategoryEditorState())
    val editorState: StateFlow<CategoryEditorState> = _editorState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            try {
                val stats = repository.getDashboardStats()
                val categories = repository.getCategories()
                _uiState.value = DashboardUiState.Success(
                    stats = stats,
                    categories = categories
                )
            } catch (e: Exception) {
                _uiState.value = DashboardUiState.Error(
                    e.message ?: "Gagal memuat dashboard"
                )
            }
        }
    }

    fun openCreateCategoryEditor() {
        _editorState.value = CategoryEditorState(isVisible = true)
    }

    fun openEditCategoryEditor(category: Category) {
        val categoryId = category.id

        if (categoryId == null) {
            _actionState.value = DashboardActionState.Error("Kategori tidak memiliki ID yang valid")
            return
        }

        viewModelScope.launch {
            try {
                val detailCategory = repository.getCategoryById(categoryId)
                _editorState.value = CategoryEditorState(
                    isVisible = true,
                    categoryId = detailCategory.id,
                    name = detailCategory.name,
                    icon = detailCategory.icon ?: DEFAULT_CATEGORY_ICON
                )
            } catch (e: Exception) {
                _actionState.value = DashboardActionState.Error(
                    e.message ?: "Gagal memuat detail kategori"
                )
            }
        }
    }

    fun dismissCategoryEditor() {
        _editorState.value = CategoryEditorState()
    }

    fun onCategoryNameChange(value: String) {
        _editorState.value = _editorState.value.copy(name = value)
    }

    fun onCategoryIconChange(value: String) {
        _editorState.value = _editorState.value.copy(icon = value)
    }

    fun submitCategory() {
        val currentEditor = _editorState.value
        val trimmedName = currentEditor.name.trim()

        if (trimmedName.isBlank()) {
            _actionState.value = DashboardActionState.Error("Nama kategori wajib diisi")
            return
        }

        viewModelScope.launch {
            _actionState.value = DashboardActionState.Loading
            try {
                if (currentEditor.categoryId == null) {
                    repository.createCategory(
                        name = trimmedName,
                        icon = currentEditor.icon
                    )
                    _actionState.value = DashboardActionState.Success("Kategori berhasil ditambahkan")
                } else {
                    repository.updateCategory(
                        id = currentEditor.categoryId,
                        name = trimmedName,
                        icon = currentEditor.icon
                    )
                    _actionState.value = DashboardActionState.Success("Kategori berhasil diperbarui")
                }

                dismissCategoryEditor()
                loadDashboard()
            } catch (e: Exception) {
                _actionState.value = DashboardActionState.Error(
                    e.toCategorySaveErrorMessage()
                )
            }
        }
    }

    fun resetActionState() {
        _actionState.value = DashboardActionState.Idle
    }

    companion object {
        const val DEFAULT_CATEGORY_ICON = "category"

        val categoryIconOptions = listOf(
            "category",
            "backpack",
            "book",
            "devices",
            "wallet",
            "key",
            "badge",
            "watch",
            "headphones",
            "more"
        )
    }
}

private fun Exception.toCategorySaveErrorMessage(): String {
    val rawMessage = message.orEmpty()
    val normalizedMessage = rawMessage.lowercase()

    return when {
        "categories_name_unique" in normalizedMessage ||
            "duplicate key value" in normalizedMessage -> {
            "Nama kategori sudah terdaftar. Gunakan nama kategori lain."
        }
        else -> rawMessage.ifBlank { "Gagal menyimpan kategori" }
    }
}
