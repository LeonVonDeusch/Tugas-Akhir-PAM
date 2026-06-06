package com.example.tugasakhirpam.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.example.tugasakhirpam.ui.theme.ClaimScreen
import androidx.navigation.navArgument
import com.example.tugasakhirpam.ui.screens.FoundItemDetailScreen
import com.example.tugasakhirpam.ui.screens.FoundItemFormScreen
import com.example.tugasakhirpam.ui.screens.FoundItemListScreen
import com.example.tugasakhirpam.ui.theme.DashboardScreen
import com.example.tugasakhirpam.ui.theme.LoginScreen
import com.example.tugasakhirpam.ui.theme.RegisterScreen
import com.example.tugasakhirpam.ui.theme.LostItemDetailScreen
import com.example.tugasakhirpam.ui.theme.LostItemFormScreen
import com.example.tugasakhirpam.ui.theme.LostItemListScreen
import com.example.tugasakhirpam.viewmodel.AuthCheckState
import com.example.tugasakhirpam.viewmodel.AuthUiState
import com.example.tugasakhirpam.viewmodel.AuthViewModel
import com.example.tugasakhirpam.viewmodel.FoundItemViewModel
import com.example.tugasakhirpam.viewmodel.LostItemViewModel

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = viewModel(),
    lostItemViewModel: LostItemViewModel = viewModel()
) {
    val authCheckState = authViewModel.authCheckState.collectAsStateWithLifecycle()

    when (authCheckState.value) {
        is AuthCheckState.Checking -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is AuthCheckState.Authenticated -> {
            MainNavHost(
                authViewModel = authViewModel,
                lostItemViewModel = lostItemViewModel,
                startDestination = Screen.Dashboard.route
            )
        }
        is AuthCheckState.NotAuthenticated -> {
            MainNavHost(
                authViewModel = authViewModel,
                lostItemViewModel = lostItemViewModel,
                startDestination = Screen.Login.route
            )
        }
    }
}

@Composable
fun MainNavHost(
    authViewModel: AuthViewModel,
    lostItemViewModel: LostItemViewModel,
    startDestination: String
) {
    val navController = rememberNavController()

    val email = authViewModel.email.collectAsStateWithLifecycle()
    val password = authViewModel.password.collectAsStateWithLifecycle()
    val uiState = authViewModel.uiState.collectAsStateWithLifecycle()

    /*
     * FoundItemViewModel dibuat di sini (bukan di dalam composable) agar instance-nya
     * sama dan state tidak hilang saat navigasi antar screen found items.
     */
    val foundItemViewModel: FoundItemViewModel = viewModel()

    LaunchedEffect(uiState.value) {
        if (uiState.value is AuthUiState.Success) {
            navController.navigate(Screen.Dashboard.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
            authViewModel.resetState()
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                email = email.value,
                password = password.value,
                uiState = uiState.value,
                onEmailChange = authViewModel::onEmailChange,
                onPasswordChange = authViewModel::onPasswordChange,
                onLoginClick = { authViewModel.login() },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                email = email.value,
                password = password.value,
                uiState = uiState.value,
                onEmailChange = authViewModel::onEmailChange,
                onPasswordChange = authViewModel::onPasswordChange,
                onRegisterClick = { authViewModel.register() },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        // Dashboard diubah agar menerima aksi perpindahan ke halaman klaim
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                },
                onFoundItemsClick = {
                    navController.navigate(Screen.FoundItemList.route)
                },
                onNavigateToLostItems = {
                    navController.navigate(Screen.LostItemList.route)
                }
            )
        }

        // --- Barang Ditemukan (Found Items - Alvin): List ---
        composable(Screen.FoundItemList.route) {
            val listState = foundItemViewModel.listState.collectAsStateWithLifecycle()

            FoundItemListScreen(
                listState = listState.value,
                onItemClick = { id ->
                    foundItemViewModel.loadFoundItemById(id)
                    navController.navigate(Screen.FoundItemDetail.createRoute(id))
                },
                onAddClick = {
                    navController.navigate(Screen.FoundItemForm.route)
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // --- Barang Ditemukan: Detail ---
        composable(
            route = Screen.FoundItemDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) {
            val detailState = foundItemViewModel.detailState.collectAsStateWithLifecycle()
            val updateStatusState = foundItemViewModel.updateStatusState.collectAsStateWithLifecycle()

            FoundItemDetailScreen(
                detailState = detailState.value,
                updateStatusState = updateStatusState.value,
                currentUserId = foundItemViewModel.currentUserId,
                onBackClick = { navController.popBackStack() },
                onUpdateStatus = { id, status -> foundItemViewModel.updateStatus(id, status) },
                onResetUpdateStatus = { foundItemViewModel.resetUpdateStatus() }
            )
        }

        // --- Barang Ditemukan: Form ---
        composable(Screen.FoundItemForm.route) {
            val itemName = foundItemViewModel.itemName.collectAsStateWithLifecycle()
            val description = foundItemViewModel.description.collectAsStateWithLifecycle()
            val foundLocation = foundItemViewModel.foundLocation.collectAsStateWithLifecycle()
            val dateFound = foundItemViewModel.dateFound.collectAsStateWithLifecycle()
            val selectedCategoryId = foundItemViewModel.selectedCategoryId.collectAsStateWithLifecycle()
            val categories = foundItemViewModel.categories.collectAsStateWithLifecycle()
            val formState = foundItemViewModel.formState.collectAsStateWithLifecycle()

            FoundItemFormScreen(
                itemName = itemName.value,
                description = description.value,
                foundLocation = foundLocation.value,
                dateFound = dateFound.value,
                selectedCategoryId = selectedCategoryId.value,
                categories = categories.value,
                formState = formState.value,
                onItemNameChange = foundItemViewModel::onItemNameChange,
                onDescriptionChange = foundItemViewModel::onDescriptionChange,
                onFoundLocationChange = foundItemViewModel::onFoundLocationChange,
                onDateFoundChange = foundItemViewModel::onDateFoundChange,
                onCategorySelected = foundItemViewModel::onCategorySelected,
                onSubmit = { imageBytes, ext -> foundItemViewModel.createFoundItem(imageBytes, ext) },
                onBackClick = { navController.popBackStack() },
                onNavigateAfterSuccess = {
                    navController.navigate(Screen.FoundItemList.route) {
                        popUpTo(Screen.FoundItemForm.route) { inclusive = true }
                    }
                },
                onClaimClick = {
                    navController.navigate(Screen.Claim.route)
                }
            )
        }

        // Daftarkan rute ClaimScreen baru di sini
        composable(Screen.Claim.route) {
            ClaimScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSubmitSuccess = {
                    navController.popBackStack()
                }
            )
        }

        // --- Barang Hilang (Lost Items - Denta): List ---
        composable(Screen.LostItemList.route) {
            LostItemListScreen(
                viewModel = lostItemViewModel,
                onNavigateToAddReport = { navController.navigate(Screen.LostItemForm.route) },
                onNavigateToDetail = { itemId -> navController.navigate(Screen.LostItemDetail.createRoute(itemId)) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.LostItemForm.route) {
            LostItemFormScreen(
                viewModel = lostItemViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.LostItemDetail.route,
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
            LostItemDetailScreen(
                itemId = itemId,
                viewModel = lostItemViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}