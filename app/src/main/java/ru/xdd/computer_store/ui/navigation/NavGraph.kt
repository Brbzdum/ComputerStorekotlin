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
            MainProductsScreen(
                navController = navController,
                userId = userId
            )
        }

        // Карточка товара
        composable("product_detail/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toLongOrNull()
            productId?.let {
                ProductDetailScreen(
                    productId = it,
                    userId = userId,
                    navController = navController
                )
            }
        }

        // Корзина доступна всем пользователям
        composable("cart") {
            CartScreen(userId = userId, navController = navController)
        }

        // Оформление заказа
        composable("checkout") {
            if (userId == -1L) {
                // Если пользователь не авторизован, перенаправляем на экран логина с возвратом
                navController.navigate("login?redirect=checkout")
            } else {
                OrdersScreen(
                    navController = navController,
                    userId = userId
                )
            }
        }


        // Страница авторизации
        composable("login?redirect={redirect}") { backStackEntry ->
            val redirect = backStackEntry.arguments?.getString("redirect")
            LoginScreen(navController = navController, redirect = redirect)
        }


        // Страница регистрации с возвратом
        composable("register?redirect={redirect}") { backStackEntry ->
            val redirect = backStackEntry.arguments?.getString("redirect")
            RegistrationScreen(
                navController = navController,
                onRegistrationSuccess = {
                    if (redirect != null) {
                        navController.navigate(redirect) {
                            popUpTo("register") { inclusive = true }
                        }
                    } else {
                        navController.navigate("main_products") {
                            popUpTo("register") { inclusive = true }
                        }
                    }
                }
            )
        }
    }
}
