package com.example.tugasakhirpam.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

/**
 * Form klaim kepemilikan atas sebuah barang hilang.
 * Klaim akan disimpan ke tabel "claims" di Supabase, terhubung ke barang hilang
 * lewat foreign key lost_item_id.
 *
 * @param lostItemId id barang hilang yang sedang diklaim (dari halaman detail).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaimScreen(
    lostItemId: String,
    onBackClick: () -> Unit = {},
    onSubmitSuccess: () -> Unit = {},
    authViewModel: com.example.tugasakhirpam.viewmodel.AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    // State untuk form inputan (sesuai kolom tabel claims)
    var proofDescription by remember { mutableStateOf("") }
    var contactInfo by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    // State untuk proses & hasil
    var isSubmitting by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Form Klaim Kepemilikan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (isSuccess) {
                // Tampilan sukses
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Sukses",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Klaim Berhasil Dikirim!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Pelapor barang akan memeriksa bukti kepemilikanmu. Mohon tunggu kabar selanjutnya melalui kontak yang kamu berikan.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = {
                            isSuccess = false
                            onSubmitSuccess()
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Kembali")
                    }
                }
            } else {
                // Tampilan form
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Kartu info
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Jelaskan bukti kepemilikanmu sedetail mungkin dan cantumkan kontak yang bisa dihubungi agar pelapor dapat memverifikasi klaimmu.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    // Pesan error (jika gagal)
                    errorMessage?.let { msg ->
                        Text(
                            text = "Gagal: $msg",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp
                        )
                    }

                    Text(
                        text = "Detail Klaim",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = proofDescription,
                        onValueChange = { proofDescription = it },
                        label = { Text("Deskripsi Bukti Kepemilikan") },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                        placeholder = { Text("Sebutkan ciri khusus yang tidak terlihat di foto: goresan, isi, gantungan, dsb.") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = contactInfo,
                        onValueChange = { contactInfo = it },
                        label = { Text("Info Kontak (No. HP / Email)") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        placeholder = { Text("Contoh: 08xxxxxxxxxx") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Pesan untuk Pelapor (opsional)") },
                        placeholder = { Text("Pesan tambahan jika ada") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            isSubmitting = true
                            errorMessage = null
                            coroutineScope.launch {
                                try {
                                    authViewModel.repository.klaimBarang(
                                        lostItemId = lostItemId,
                                        proofDescription = proofDescription,
                                        contactInfo = contactInfo,
                                        message = message
                                    )
                                    isSuccess = true
                                } catch (e: Exception) {
                                    // Tampilkan error ke user (tidak lagi disembunyikan)
                                    errorMessage = e.message ?: "Terjadi kesalahan saat mengirim klaim"
                                } finally {
                                    isSubmitting = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = proofDescription.isNotBlank() && contactInfo.isNotBlank() && !isSubmitting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text(
                                text = "Ajukan Klaim Sekarang",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
