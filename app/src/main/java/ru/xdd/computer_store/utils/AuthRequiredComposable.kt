package ru.xdd.computer_store.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController

@Composable
fun AuthRequiredComposable(
    userId: Long,
    navController: NavController,
    content: @Composable () -> Unit
) {
    if (userId == -1L) {
        // Если пользователь не авторизован, перенаправляем на логин
        LaunchedEffect(Unit) {
            navController.navigate("login") {
                popUpTo("main_products") { inclusive = true }
            }
        }
    } else {
        // Если пользователь авторизован, отображаем содержимое
        content()
    }
}
