package ru.xdd.computer_store.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ru.xdd.computer_store.ui.screens.*
import ru.xdd.computer_store.ui.viewmodel.LoginViewModel
import ru.xdd.computer_store.ui.viewmodel.MainViewModel
import ru.xdd.computer_store.ui.viewmodel.RegistrationViewModel

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    NavHost(navController = navController, startDestination = "catalog") {
        // Каталог товаров
        composable("catalog") {
            CatalogScreen(
                onProductClick = { product ->
                    navController.navigate("productDetail/${product.productId}")
                },
                onCartClick = {
                    navController.navigate("cart")
                },
                viewModel = mainViewModel
            )
        }

        // Детали товара
        composable(
            route = "productDetail/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.LongType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId")
            productId?.let {
                ProductDetailScreen(
                    productId = it,
                    onAddToCart = { id ->
                        if (!mainViewModel.isLoggedIn.value) {
                            navController.navigate("login")
                        } else {
                            mainViewModel.addToCart(userId = mainViewModel.currentUserId, productId = id, quantity = 1)
                            navController.navigate("cart")
                        }
                    },
                    viewModel = mainViewModel
                )
            }
        }

        // Корзина
        composable("cart") {
            CartScreen(
                onCheckout = {
                    navController.navigate("checkout")
                },
                viewModel = mainViewModel
            )
        }

        // Оформление заказа
        composable("checkout") {
            CheckoutScreen(
                onOrderPlaced = {
                    navController.navigate("catalog") {
                        popUpTo("catalog") { inclusive = true }
                    }
                },
                viewModel = mainViewModel
            )
        }

        // Отзывы
        composable(
            route = "reviews/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.LongType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId")
            productId?.let {
                ReviewsScreen(
                    productId = it,
                    viewModel = mainViewModel
                )
            }
        }

        // Авторизация
        // Авторизация
        composable("login") {
            val loginViewModel: LoginViewModel = viewModel()
            LoginScreen(navController = navController, viewModel = loginViewModel)
        }

        // Регистрация
        composable("registration") {
            val registrationViewModel: RegistrationViewModel = viewModel()
            RegistrationScreen(navController = navController, viewModel = registrationViewModel)
        }
    }
}
