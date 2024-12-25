package ru.xdd.computer_store.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.xdd.computer_store.ui.screens.*
import ru.xdd.computer_store.ui.utils.AuthRequiredComposable

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

        composable("checkout") {
            OrdersScreen(navController = navController, userId = userId)
        }


        // Корзина (только для авторизованных пользователей)
        composable("cart") {
            AuthRequiredComposable(userId = userId, navController = navController) {
                CartScreen(
                    userId = userId,
                    navController = navController
                )
            }
        }

        // Страница авторизации
        composable("login") {
            LoginScreen(navController = navController)
        }

        // Страница регистрации
        composable("register") {
            RegistrationScreen(navController = navController)
        }

        // Управление пользователями (админ)
//        composable("admin_users") {
//            if (userRole == "ADMIN") {
//                AuthRequiredComposable(userId = userId, navController = navController) {
//                    AdminUserScreen(navController = navController)
//                }
//            } else {
//                navController.navigate("main_products") {
//                    popUpTo("main_products") { inclusive = true }
//                }
//            }
//        }

        // Управление товарами (админ)
        composable("admin_products") {
            if (userRole == "ADMIN") {
                AuthRequiredComposable(userId = userId, navController = navController) {
                    AdminProductScreen(navController = navController)
                }
            } else {
                navController.navigate("main_products") {
                    popUpTo("main_products") { inclusive = true }
                }
            }
        }

//        // Экран аксессуаров
//        composable("accessories") {
//            AccessoriesScreen(navController = navController)
//        }
    }
}
