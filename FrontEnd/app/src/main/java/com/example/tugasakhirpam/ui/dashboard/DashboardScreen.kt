package com.example.tugasakhirpam.ui.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tugasakhirpam.data.Category
import com.example.tugasakhirpam.ui.theme.TugasAkhirPAMTheme
import com.example.tugasakhirpam.viewmodel.DashboardActionState
import com.example.tugasakhirpam.viewmodel.DashboardUiState
import com.example.tugasakhirpam.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onLogoutClick: () -> Unit,
    onNavigateToLostItems: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val actionState by viewModel.actionState.collectAsStateWithLifecycle()
    val editorState by viewModel.editorState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(actionState) {
        when (val state = actionState) {
            is DashboardActionState.Success -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.resetActionState()
            }

            is DashboardActionState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.resetActionState()
            }

            else -> Unit
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("LACAK", fontWeight = FontWeight.Bold)
                        Text(
                            text = "Layanan Cari dan Kembalikan",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        DashboardContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            uiState = uiState,
            actionState = actionState,
            editorStateVisible = editorState.isVisible,
            onRetry = viewModel::loadDashboard,
            onAddCategory = viewModel::openCreateCategoryEditor,
            onEditCategory = viewModel::openEditCategoryEditor,
            onNavigateToLostItems = onNavigateToLostItems
        )

        if (editorState.isVisible) {
            CategoryEditorDialog(
                title = if (editorState.isEditing) "Edit Kategori" else "Tambah Kategori",
                name = editorState.name,
                selectedIcon = editorState.icon,
                isSaving = actionState is DashboardActionState.Loading,
                onNameChange = viewModel::onCategoryNameChange,
                onIconChange = viewModel::onCategoryIconChange,
                onDismiss = viewModel::dismissCategoryEditor,
                onSubmit = viewModel::submitCategory
            )
        }
    }
}

@Composable
internal fun DashboardContent(
    modifier: Modifier = Modifier,
    uiState: DashboardUiState,
    actionState: DashboardActionState,
    editorStateVisible: Boolean,
    onRetry: () -> Unit,
    onAddCategory: () -> Unit,
    onEditCategory: (Category) -> Unit,
    onNavigateToLostItems: () -> Unit
) {
    when (uiState) {
        DashboardUiState.Loading -> DashboardLoadingState(modifier = modifier)
        is DashboardUiState.Error -> {
            DashboardErrorState(
                message = uiState.message,
                onRetry = onRetry,
                modifier = modifier
            )
        }

        is DashboardUiState.Success -> {
            DashboardSuccessState(
                modifier = modifier,
                stats = uiState.stats,
                categories = uiState.categories,
                actionState = actionState,
                editorStateVisible = editorStateVisible,
                onAddCategory = onAddCategory,
                onEditCategory = onEditCategory,
                onNavigateToLostItems = onNavigateToLostItems
            )
        }
    }
}
