package com.example.tugasakhirpam.ui.theme

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tugasakhirpam.data.LostItem
import com.example.tugasakhirpam.viewmodel.ActionState
import com.example.tugasakhirpam.viewmodel.LostItemUiState
import com.example.tugasakhirpam.viewmodel.LostItemViewModel
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LostItemListScreen(viewModel: LostItemViewModel, onNavigateToAddReport: () -> Unit, onNavigateToDetail: (String) -> Unit, onNavigateBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadLostItems() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Barang Hilang", fontWeight = FontWeight.Bold) }, navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
            }
        }
        )},
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddReport) { Icon(Icons.Default.Add, "Tambah") }
        }
    ) { padding ->
        when (uiState) {
            is LostItemUiState.Loading -> CircularProgressIndicator(modifier = Modifier.padding(padding))
            is LostItemUiState.Success -> {
                val items = (uiState as LostItemUiState.Success).items
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items) { item ->
                        LostItemRow(item, onClick = { item.id?.let { onNavigateToDetail(it) } })
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
fun LostItemRow(item: LostItem, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onClick() }, elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            item.imageUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = "Foto Barang",
                    modifier = Modifier.fillMaxWidth().height(150.dp).padding(bottom = 8.dp)
                )
            }
            Text(text = item.itemName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = "Lokasi: ${item.lastSeenLocation} | Tgl: ${item.dateLost}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Status: ${item.status.uppercase()}", color = if(item.status.contains("belum", ignoreCase = true)) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LostItemFormScreen(viewModel: LostItemViewModel, onNavigateBack: () -> Unit) {
    val itemName by viewModel.itemNameInput.collectAsState()
    val location by viewModel.locationInput.collectAsState()
    val description by viewModel.descriptionInput.collectAsState()
    val dateLost by viewModel.dateLostInput.collectAsState()
    val categoryId by viewModel.categoryIdInput.collectAsState()
    val imageByteArray by viewModel.imageByteArrayInput.collectAsState()
    val actionState by viewModel.actionState.collectAsState()

    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            viewModel.imageByteArrayInput.value = inputStream?.readBytes()
        }
    }

    LaunchedEffect(actionState) {
        when (actionState) {
            is ActionState.Success -> {
                Toast.makeText(context, (actionState as ActionState.Success).message, Toast.LENGTH_SHORT).show()
                viewModel.resetActionState()
                onNavigateBack()
            }
            is ActionState.Error -> {
                Toast.makeText(context, (actionState as ActionState.Error).message, Toast.LENGTH_LONG).show()
                viewModel.resetActionState()
            }
            else -> {}
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Lapor Barang Hilang", fontWeight = FontWeight.Bold) }, navigationIcon = {
        IconButton(onClick = onNavigateBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
        }
    })
    })
    { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(value = itemName, onValueChange = { viewModel.itemNameInput.value = it }, label = { Text("Nama Barang") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = location, onValueChange = { viewModel.locationInput.value = it }, label = { Text("Lokasi Terakhir") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = dateLost, onValueChange = { viewModel.dateLostInput.value = it }, label = { Text("Tanggal (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = categoryId, onValueChange = { viewModel.categoryIdInput.value = it }, label = { Text("ID Kategori") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = description, onValueChange = { viewModel.descriptionInput.value = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth(), minLines = 3)

            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text(if (imageByteArray != null) "Foto Terpilih (Klik untuk Ganti)" else "Pilih Foto Barang")
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.createLostItemReport(userId = "276b92eb-fed7-4495-85df-16a23815f649")
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = actionState !is ActionState.Loading
            ) {
                if (actionState is ActionState.Loading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                else Text("Kirim Laporan")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LostItemDetailScreen(itemId: String, viewModel: LostItemViewModel, onNavigateBack: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(itemId) { viewModel.loadLostItemDetail(itemId) }

    Scaffold(topBar = { TopAppBar(title = { Text("Detail Laporan") }, navigationIcon = {
        IconButton(onClick = onNavigateBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
        }
    }) }) { padding ->
        when (uiState) {
            is LostItemUiState.Loading -> CircularProgressIndicator(modifier = Modifier.padding(padding))
            is LostItemUiState.SuccessDetail -> {
                val item = (uiState as LostItemUiState.SuccessDetail).item
                Column(
                    modifier = Modifier.padding(padding).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    item.imageUrl?.let { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = "Foto Detail",
                            modifier = Modifier.fillMaxWidth().height(250.dp)
                                .padding(bottom = 16.dp)
                        )
                    }

                    Text(
                        text = item.itemName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Status: ${item.status.uppercase()}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    HorizontalDivider()
                    Text(text = "Lokasi: ${item.lastSeenLocation}")
                    Text(text = "Tanggal Kehilangan: ${item.dateLost}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Deskripsi:", fontWeight = FontWeight.Bold)
                    Text(text = item.description)

                    Spacer(modifier = Modifier.height(24.dp))

                    if (item.status.contains("belum", ignoreCase = true)) {
                        Button(
                            onClick = { item.id?.let { viewModel.markAsFound(it) } },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Tandai Sudah Ditemukan")
                        }
                    } else {
                        Text(
                            text = "Barang ini telah ditemukan.",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            else -> {}
        }
    }
}