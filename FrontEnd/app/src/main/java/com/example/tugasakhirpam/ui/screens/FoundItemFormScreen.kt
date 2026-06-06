package com.example.tugasakhirpam.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tugasakhirpam.data.model.Category
import com.example.tugasakhirpam.viewmodel.FoundItemFormState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoundItemFormScreen(
    itemName: String,
    description: String,
    foundLocation: String,
    dateFound: String,
    selectedCategoryId: Int?,
    categories: List<Category>,
    formState: FoundItemFormState,
    onItemNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onFoundLocationChange: (String) -> Unit,
    onDateFoundChange: (String) -> Unit,
    onCategorySelected: (Int?) -> Unit,
    onSubmit: (imageBytes: ByteArray?, fileExtension: String) -> Unit,
    onBackClick: () -> Unit,
    onNavigateAfterSuccess: () -> Unit
) {
    val context = LocalContext.current

    // URI gambar yang dipilih dari galeri
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher untuk membuka galeri
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> selectedImageUri = uri }

    // State dropdown kategori
    var dropdownExpanded by remember { mutableStateOf(false) }

    // Snackbar untuk feedback error
    val snackbarHostState = remember { SnackbarHostState() }

    // Navigasi ke list setelah berhasil simpan
    LaunchedEffect(formState) {
        when (formState) {
            is FoundItemFormState.Success -> onNavigateAfterSuccess()
            is FoundItemFormState.Error -> snackbarHostState.showSnackbar(formState.message)
            else -> Unit
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Lapor Barang Ditemukan") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // --- Image Picker ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { imagePicker.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Foto barang",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Ketuk untuk pilih foto",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "(opsional)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // --- Nama Barang ---
            OutlinedTextField(
                value = itemName,
                onValueChange = onItemNameChange,
                label = { Text("Nama Barang *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // --- Deskripsi ---
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text("Deskripsi") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            // --- Lokasi Penemuan ---
            OutlinedTextField(
                value = foundLocation,
                onValueChange = onFoundLocationChange,
                label = { Text("Lokasi Penemuan *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // --- Tanggal Ditemukan ---
            OutlinedTextField(
                value = dateFound,
                onValueChange = onDateFoundChange,
                label = { Text("Tanggal Ditemukan * (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("contoh: 2024-05-18") }
            )

            // --- Dropdown Kategori ---
            if (categories.isNotEmpty()) {
                val selectedCategory = categories.find { it.id == selectedCategoryId }

                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCategory?.name ?: "Pilih Kategori",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Kategori") },
                        trailingIcon = {
                            Icon(
                                imageVector = if (dropdownExpanded)
                                    Icons.Default.KeyboardArrowUp
                                else
                                    Icons.Default.KeyboardArrowDown,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Tanpa Kategori") },
                            onClick = {
                                onCategorySelected(null)
                                dropdownExpanded = false
                            }
                        )
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    onCategorySelected(category.id)
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // --- Tombol Submit ---
            Button(
                onClick = {
                    val imageBytes = selectedImageUri?.let { uri ->
                        context.contentResolver.openInputStream(uri)?.readBytes()
                    }
                    // Ambil ekstensi dari MIME type (image/jpeg -> jpg, image/png -> png)
                    val mimeType = selectedImageUri?.let {
                        context.contentResolver.getType(it)
                    }
                    val extension = when (mimeType) {
                        "image/png" -> "png"
                        "image/webp" -> "webp"
                        else -> "jpg"
                    }
                    onSubmit(imageBytes, extension)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = formState !is FoundItemFormState.Loading &&
                        itemName.isNotBlank() &&
                        foundLocation.isNotBlank() &&
                        dateFound.isNotBlank()
            ) {
                if (formState is FoundItemFormState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Simpan Laporan")
                }
            }
        }
    }
}
