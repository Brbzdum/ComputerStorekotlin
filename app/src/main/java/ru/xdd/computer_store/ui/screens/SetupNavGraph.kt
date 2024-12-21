package ru.xdd.computer_store.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ru.xdd.computer_store.ui.viewmodel.LoginViewModel
import ru.xdd.computer_store.ui.viewmodel.MainViewModel
import ru.xdd.computer_store.ui.viewmodel.RegistrationViewModel

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    viewModel: MainViewModel,
    paddingValues: PaddingValues
) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            val loginViewModel: LoginViewModel = viewModel()
            LoginScreen(navController = navController, viewModel = loginViewModel)
        }
        composable("registration") {
            val registrationViewModel: RegistrationViewModel = viewModel()
            RegistrationScreen(navController = navController, viewModel = registrationViewModel)
        }
        composable("catalog") {
            val isLoggedIn by viewModel.isLoggedIn.collectAsState(initial = false)

            if (!isLoggedIn) {
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo("catalog") { inclusive = true }
                    }
                }
                return@composable // Важно!
            }

            CatalogScreen(
                viewModel = viewModel,
                onProductClick = { product ->
                    navController.navigate("productDetail/${product.productId}")
                },
                onCartClick = {
                    navController.navigate("cart")
                },
                paddingValues = paddingValues
            )
        }
        composable(
            route = "productDetail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: -1
            val product = viewModel.getProductById(productId)

            ProductDetailScreen(
                product = product,
                onBackClick = { navController.popBackStack() },
                onAddReview = { review ->
                    viewModel.addReviewToProduct(productId, review)
                }
            )
        }

        composable("cart") {
            val isLoggedIn by viewModel.isLoggedIn.collectAsState(initial = false)

            if (!isLoggedIn) {
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo("cart") { inclusive = true }
                    }
                }
                return@composable // Важно!
            }
            CartScreen(
                userId = 1, // Или получайте ID пользователя из ViewModel
                repository = viewModel.repository,
                onCheckout = { userId ->
                    navController.navigate("checkout/$userId")
                },
                paddingValues = paddingValues
            )
        }
        composable("checkout/{userId}") { backStackEntry ->
            val isLoggedIn by viewModel.isLoggedIn.collectAsState(initial = false)

            if (!isLoggedIn) {
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo("checkout/{userId}") { inclusive = true }
                    }
                }
                return@composable // Важно!
            }
            val userId = backStackEntry.arguments?.getString("userId")?.toLong()
            userId?.let {
                CheckoutScreen(
                    userId = it,
                    viewModel = viewModel,
                    onOrderPlaced = {
                        navController.navigateUp()
                    },
                    paddingValues = paddingValues
                )
            }
        }
        composable("reviews/{productId}") { backStackEntry ->
            val isLoggedIn by viewModel.isLoggedIn.collectAsState(initial = false)

            if (!isLoggedIn) {
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo("reviews/{productId}") { inclusive = true }
                    }
                }
                return@composable // Важно!
            }
            val productId = backStackEntry.arguments?.getString("productId")?.toLong()
            productId?.let {
                ReviewsScreen(
                    productId = it,
                    userId = 1, // Или получайте ID пользователя из ViewModel
                    repository = viewModel.repository,
                    paddingValues = paddingValues
                )
            }
        }
    }
}