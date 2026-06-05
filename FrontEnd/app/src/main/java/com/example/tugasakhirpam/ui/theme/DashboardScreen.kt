package com.example.tugasakhirpam.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(
    onLogoutClick: () -> Unit,
    onClaimClick: () -> Unit // Kita tambahkan parameter aksi untuk klik tombol klaim
) {
    /*
     * Dashboard setelah user berhasil login.
     */
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Anda berhasil login ke aplikasi."
        )

        Spacer(modifier = Modifier.height(32.dp))

        // TOMBOL BARU: Untuk menuju ke halaman Klaim Barang
        Button(
            onClick = onClaimClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Ajukan Klaim Barang")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tombol Logout bawaan
        OutlinedButton(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}