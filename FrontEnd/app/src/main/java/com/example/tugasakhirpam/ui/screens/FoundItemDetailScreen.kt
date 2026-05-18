package com.example.tugasakhirpam.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tugasakhirpam.viewmodel.FoundItemDetailState
import com.example.tugasakhirpam.viewmodel.FoundItemFormState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoundItemDetailScreen(
    detailState: FoundItemDetailState,
    updateStatusState: FoundItemFormState,
    currentUserId: String,
    onBackClick: () -> Unit,
    onUpdateStatus: (id: String, status: String) -> Unit,
    onResetUpdateStatus: () -> Unit
) {
    // Tampilkan snackbar saat update status berhasil
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(updateStatusState) {
        if (updateStatusState is FoundItemFormState.Success) {
            snackbarHostState.showSnackbar("Status berhasil diperbarui")
            onResetUpdateStatus()
        } else if (updateStatusState is FoundItemFormState.Error) {
            snackbarHostState.showSnackbar(updateStatusState.message)
            onResetUpdateStatus()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Detail Barang Ditemukan") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (detailState) {
                is FoundItemDetailState.Idle,
                is FoundItemDetailState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is FoundItemDetailState.Error -> {
                    Text(
                        text = detailState.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is FoundItemDetailState.Success -> {
                    val item = detailState.item

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Foto barang
                        if (!item.imageUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = item.imageUrl,
                                contentDescription = item.itemName,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // Badge status
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (item.status == "belum")
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = if (item.status == "belum") "Belum Diklaim" else "Sudah Diklaim",
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Text(
                            text = item.itemName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        HorizontalDivider()

                        DetailRow(label = "Deskripsi", value = item.description.ifBlank { "-" })
                        DetailRow(label = "Lokasi Penemuan", value = item.foundLocation)
                        DetailRow(label = "Tanggal Ditemukan", value = item.dateFound)

                        // Tombol update status hanya tampil untuk pelapor & status masih "belum"
                        if (item.userId == currentUserId && item.status == "belum") {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { onUpdateStatus(item.id, "sudah diklaim") },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = updateStatusState !is FoundItemFormState.Loading
                            ) {
                                if (updateStatusState is FoundItemFormState.Loading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                } else {
                                    Text("Tandai Sudah Diklaim")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
