package ru.xdd.computer_store.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.xdd.computer_store.ui.screens.*
import ru.xdd.computer_store.ui.screens.admin.AdminDashboardScreen
import ru.xdd.computer_store.ui.screens.admin.AdminOrderScreen
import ru.xdd.computer_store.ui.screens.admin.AdminProductScreen
import ru.xdd.computer_store.ui.screens.admin.AdminReviewScreen
import ru.xdd.computer_store.ui.screens.admin.AdminUserScreen

@Composable
fun StoreNavGraph(
    navController: NavHostController,
    userId: Long,
    userRole: String
) {
    NavHost(
        navController = navController,
        startDestination = if (userRole == "ADMIN") "admin_dashboard" else "main_products"
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
                    navController = navController,
                    userId = userId // Передаем userId
                )
            } else {
                // Обработка ошибки
                navController.navigate("main_products")
            }
        }

        // Корзина
        composable("cart") {
            CartScreen(navController = navController)
        }

        // Профиль
        composable("profile") {
            if (userId == -1L) {
                redirectToLogin(navController, "profile")
            } else {
                ProfileScreen(navController = navController)
            }
        }

        // Оформление заказа
        composable("checkout") {
            if (userId == -1L) {
                redirectToLogin(navController, "checkout")
            } else {
                CheckoutScreen(
                    navController = navController,
                    userId = userId // Передаем userId
                )
            }
        }

        // Авторизация
        composable("login?redirect={redirect}") { backStackEntry ->
            val redirect = backStackEntry.arguments?.getString("redirect") ?: "main_products"
            LoginScreen(navController = navController, redirect = redirect)
        }

        // Регистрация
        composable("register?redirect={redirect}") { backStackEntry ->
            val redirect = backStackEntry.arguments?.getString("redirect") ?: "main_products"
            RegistrationScreen(
                navController = navController,
                onRegistrationSuccess = {
                    navController.navigate(redirect) {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        // Админ: Главная панель
        if (userRole == "ADMIN") {
            composable("admin_dashboard") {
                AdminDashboardScreen(navController = navController)
            }

            // Админ: Управление заказами
            composable("admin_orders") {
                AdminOrderScreen(navController = navController)
            }

            // Админ: Управление пользователями
            composable("admin_users") {
                AdminUserScreen(navController = navController)
            }

            // Админ: Управление продуктами
            composable("admin_products") {
                AdminProductScreen(navController = navController)
            }

            // Админ: Управление отзывами
            composable("admin_reviews") {
                AdminReviewScreen()
            }
        }
    }
}

// Вспомогательная функция для перенаправления на логин
private fun redirectToLogin(navController: NavHostController, redirect: String) {
    navController.navigate("login?redirect=$redirect")
}

