package ru.xdd.computer_store.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.xdd.computer_store.ui.components.BottomNavigationBar
import ru.xdd.computer_store.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val userRole by viewModel.userRole.collectAsState()
    val orders by viewModel.orders.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Профиль") },
                actions = {
                    if (isLoggedIn) {
                        IconButton(onClick = {
                            viewModel.logout()
                            navController.navigate("main_products") {
                                popUpTo("main_products") { inclusive = true }
                            }
                        }) {
                            Text("Выход")
                        }
                    }
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (isLoggedIn) {
                Text("Добро пожаловать, ${userRole?.name ?: "Пользователь"}!", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))

                Text("Ваши заказы:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                if (orders.isEmpty()) {
                    Text("У вас нет заказов.", style = MaterialTheme.typography.bodyLarge)
                } else {
                    LazyColumn {
                        items(orders) { order ->
                            OrderItem(orderId = order.orderId, totalAmount = order.totalAmount)
                        }
                    }
                }
            } else {
                Text("Вы не авторизованы", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navController.navigate("login") },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Авторизоваться")
                }
            }
        }
    }
}

@Composable
fun OrderItem(orderId: Long, totalAmount: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "ID Заказа: $orderId", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Сумма: $totalAmount ₽", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
