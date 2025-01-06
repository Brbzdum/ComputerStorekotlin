package ru.xdd.computer_store.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.xdd.computer_store.ui.screens.*

@Composable
fun StoreNavGraph(
    navController: NavHostController,
    userId: Long,
    userRole: String
) {
    NavHost(
        navController = navController,
        startDestination = "main_products"
    ) {
        // Главная страница каталога
        composable("main_products") {
            MainProductsScreen(navController = navController)
        }

        // Карточка товара
        composable("product_detail/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toLongOrNull()
            if (productId != null) {
                ProductDetailScreen(
                    productId = productId,
                    navController = navController
                )
            }
        }

        // Корзина
        composable("cart") {
            CartScreen(navController = navController)
        }

        // Профиль (проверка авторизации)
        composable("profile") {
            if (userId == -1L) {
                navController.navigate("login?redirect=profile")
            } else {
                ProfileScreen(navController = navController)
            }
        }

        // Оформление заказа
        composable("checkout") {
            if (userId == -1L) {
                navController.navigate("login?redirect=checkout")
            } else {
                CheckoutScreen(navController = navController)
            }
        }

        // Авторизация
        composable("login?redirect={redirect}") { backStackEntry ->
            val redirect = backStackEntry.arguments?.getString("redirect")
            LoginScreen(
                navController = navController,
                redirect = redirect
            )
        }

        // Регистрация
        composable("register?redirect={redirect}") { backStackEntry ->
            val redirect = backStackEntry.arguments?.getString("redirect")
            RegistrationScreen(
                navController = navController,
                onRegistrationSuccess = {
                    navController.navigate(redirect ?: "main_products") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }
    }
}
