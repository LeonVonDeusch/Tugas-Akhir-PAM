package com.example.tugasakhirpam.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.tugasakhirpam.data.Category
import com.example.tugasakhirpam.data.DashboardStats
import com.example.tugasakhirpam.ui.theme.BlueAccent
import com.example.tugasakhirpam.ui.theme.SuccessGreen
import com.example.tugasakhirpam.ui.theme.WarningAmber
import com.example.tugasakhirpam.viewmodel.DashboardActionState

@Composable
internal fun DashboardLoadingState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
internal fun DashboardSuccessState(
    modifier: Modifier = Modifier,
    stats: DashboardStats,
    categories: List<Category>,
    actionState: DashboardActionState,
    editorStateVisible: Boolean,
    onAddCategory: () -> Unit,
    onEditCategory: (Category) -> Unit,
    onNavigateToLostItems: () -> Unit
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        HeroSection(
            onPrimaryAction = onNavigateToLostItems
        )

        StatsSection(stats = stats)

        CategorySection(
            categories = categories,
            isSubmitting = actionState is DashboardActionState.Loading && editorStateVisible,
            onAddCategory = onAddCategory,
            onEditCategory = onEditCategory
        )
    }
}

@Composable
internal fun HeroSection(
    onPrimaryAction: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.12f)
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Text(
                text = "Pusat laporan barang kampus",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSecondary
            )
            Text(
                text = "Pantau statistik, atur kategori, dan arahkan pengguna ke laporan yang paling relevan.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.88f)
            )

            Button(
                onClick = onPrimaryAction,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Lihat laporan barang hilang")
            }
        }
    }
}

@Composable
internal fun StatsSection(stats: DashboardStats) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader(
            title = "Ringkasan Aktivitas",
            subtitle = "Dashboard utama untuk memantau volume laporan yang aktif."
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                value = stats.lostItemCount.toString(),
                label = "Hilang",
                description = "Laporan kehilangan",
                accent = WarningAmber
            )
            StatCard(
                modifier = Modifier.weight(1f),
                value = stats.foundItemCount.toString(),
                label = "Ditemukan",
                description = "Laporan temuan",
                accent = SuccessGreen
            )
            StatCard(
                modifier = Modifier.weight(1f),
                value = stats.claimCount.toString(),
                label = "Klaim",
                description = "Pengajuan klaim",
                accent = BlueAccent
            )
        }
    }
}

@Composable
internal fun StatCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    description: String,
    accent: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = accent,
                    maxLines = 1
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
internal fun DashboardErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(20.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Dashboard gagal dimuat",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Button(onClick = onRetry) {
                    Text("Coba Lagi")
                }
            }
        }
    }
}

@Composable
internal fun SectionHeader(
    title: String,
    subtitle: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleLarge)
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
