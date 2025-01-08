package ru.xdd.computer_store.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.xdd.computer_store.ui.screens.*
import ru.xdd.computer_store.ui.screens.admin.*

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
                    userId = userId
                )
            } else {
                navController.navigate("main_products")
            }
        }
        composable("reviews/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toLongOrNull()
            if (productId != null) {
                ReviewsScreen(
                    productId = productId,
                    navController = navController
                )
            } else {
                // В случае ошибки возвращаемся на главную
                navController.navigate("main_products") {
                    popUpTo("main_products") { inclusive = true }
                }
            }
        }


        // Корзина
        composable("cart") {
            CartScreen(navController = navController)
        }

        // Профиль
        composable("profile") {
            ProfileScreen(navController = navController)
        }

        // Оформление заказа
        composable("checkout_screen") {
            CheckoutScreen(
                navController = navController,
                userId = userId
            )
        }

        // Авторизация
        composable("login") {
            LoginScreen(navController = navController)
        }

        // Регистрация
        composable("registration_screen") {
            RegistrationScreen(
                navController = navController,
                onRegistrationSuccess = {
                    navController.navigate("profile") {
                        popUpTo("registration_screen") { inclusive = true }
                    }
                }
            )
        }
        composable("orders_screen") {
            OrdersScreen(navController = navController, userId = userId)
        }

        // Админ: Главная панель
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
