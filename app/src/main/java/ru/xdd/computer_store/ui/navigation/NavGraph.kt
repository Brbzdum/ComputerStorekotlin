package ru.xdd.computer_store.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.xdd.computer_store.ui.screens.*

@Composable
fun StoreNavGraph(
    navController: NavHostController,
    userId: Long
) {
    NavHost(
        navController = navController,
        startDestination = "main_products" // Всегда начинаем с каталога
    ) {
        composable("main_products") {
            MainProductsScreen(
                navController = navController,
                userId = userId
            )
        }
        composable("cart") {
            if (userId == -1L) {
                // Если пользователь не авторизован, отправляем на экран логина
                navController.navigate("login")
            } else {
                CartScreen(
                    userId = userId,
                    navController = navController
                )
            }
        }
        composable("login") {
            LoginScreen(
                navController = navController
            )
        }
        composable("register") {
            RegistrationScreen(
                navController = navController
            )
        }
        composable("admin") {
            AdminProductScreen(
                navController = navController
            )
        }
        composable("addProduct") {
            AddProductScreen(
                navController = navController
            )
        }
        composable("editProduct/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toLongOrNull()
            productId?.let {
                EditProductScreen(
                    productId = it,
                    navController = navController
                )
            }
        }
        composable("reviews/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toLongOrNull()
            productId?.let {
                ReviewsScreen(
                    userId = userId,
                    productId = it,
                    navController = navController
                )
            }
        }
    }
}



