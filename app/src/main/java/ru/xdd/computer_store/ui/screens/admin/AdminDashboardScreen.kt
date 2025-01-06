package ru.xdd.computer_store.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Административная панель") },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Кнопка для управления пользователями
            Button(
                onClick = { navController.navigate("admin_users") },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text("Управление пользователями")
            }

            // Кнопка для управления продуктами
            Button(
                onClick = { navController.navigate("admin_products") },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text("Управление продуктами")
            }

            // Кнопка для управления заказами
            Button(
                onClick = { navController.navigate("admin_orders") },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text("Управление заказами")
            }

            // Кнопка для управления отзывами
            Button(
                onClick = { navController.navigate("admin_reviews") },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text("Управление отзывами")
            }
        }
    }
}
