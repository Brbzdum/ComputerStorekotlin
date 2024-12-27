package ru.xdd.computer_store.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object MainProducts : BottomNavItem("main_products", Icons.Default.Home, "Каталог")
    object Cart : BottomNavItem("cart", Icons.Default.ShoppingCart, "Корзина")
    object Profile : BottomNavItem("profile", Icons.Default.Person, "Профиль")
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.MainProducts,
        BottomNavItem.Cart,
        BottomNavItem.Profile
    )

    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primary, // Цвет фона панели
        contentColor = MaterialTheme.colorScheme.onPrimary, // Цвет иконок и текста
        tonalElevation = 4.dp // Легкий эффект поднятия
    ) {
        items.forEach { item ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(28.dp) // Увеличиваем размер иконки
                    )
                }
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onPrimary // Цвет текста
                )
            }
        }
    }
}
