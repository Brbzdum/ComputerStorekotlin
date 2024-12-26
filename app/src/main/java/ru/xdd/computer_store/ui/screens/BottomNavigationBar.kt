package ru.xdd.computer_store.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
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

    BottomAppBar {
        items.forEach { item ->
            IconButton(onClick = {
                navController.navigate(item.route) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }) {
                Icon(imageVector = item.icon, contentDescription = item.label)
                Text(text = item.label)
            }
        }
    }
}