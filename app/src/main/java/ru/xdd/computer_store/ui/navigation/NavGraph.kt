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
    userRole: String // Передаем роль пользователя (например, "ADMIN" или "USER")
) {
    NavHost(
        navController = navController,
        startDestination = "main_products"
    ) {
        // Главная страница для всех пользователей
        composable("main_products") {
            MainProductsScreen(
                navController = navController,
                userId = userId
            )
        }

        // Страница корзины, доступная только авторизованным пользователям
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
            LoginScreen(
                navController = navController
            )
        }

        // Страница регистрации
        composable("register") {
            RegistrationScreen(
                navController = navController
            )
        }

        // Страница управления продуктами для администраторов
        composable("admin") {
            if (userRole == "ADMIN") {
                AuthRequiredComposable(userId = userId, navController = navController) {
                    AdminProductScreen(
                        navController = navController
                    )
                }
            } else {
                // Перенаправление на главную страницу для неадминистраторов
                navController.navigate("main_products") {
                    popUpTo("main_products") { inclusive = true }
                }
            }
        }

        // Экран добавления продукта для администраторов
        composable("addProduct") {
            if (userRole == "ADMIN") {
                AuthRequiredComposable(userId = userId, navController = navController) {
                    AddProductScreen(
                        navController = navController
                    )
                }
            } else {
                navController.navigate("main_products") {
                    popUpTo("main_products") { inclusive = true }
                }
            }
        }

        // Экран редактирования продукта для администраторов
        composable("editProduct/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toLongOrNull()
            if (userRole == "ADMIN") {
                AuthRequiredComposable(userId = userId, navController = navController) {
                    productId?.let {
                        EditProductScreen(
                            productId = it,
                            navController = navController
                        )
                    }
                }
            } else {
                navController.navigate("main_products") {
                    popUpTo("main_products") { inclusive = true }
                }
            }
        }

        // Экран отзывов, доступный авторизованным пользователям
        composable("reviews/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toLongOrNull()
            AuthRequiredComposable(userId = userId, navController = navController) {
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
}
