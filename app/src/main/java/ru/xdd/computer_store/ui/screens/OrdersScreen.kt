package ru.xdd.computer_store.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.xdd.computer_store.model.OrderEntity
import ru.xdd.computer_store.ui.viewmodel.OrdersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(userId: Long, navController: NavController, viewModel: OrdersViewModel = hiltViewModel()) {
    val orders by viewModel.getOrders(userId).collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ваши Заказы") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (orders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "У вас нет заказов.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                items(orders) { order ->
                    OrderItemRow(order = order)
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun OrderItemRow(order: OrderEntity) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Text(text = "Заказ №${order.orderId}", style = MaterialTheme.typography.titleMedium)
        Text(text = "Дата: ${java.text.SimpleDateFormat("dd.MM.yyyy HH:mm").format(java.util.Date(order.orderDate))}", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Статус: ${order.orderStatus}", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Сумма: ${order.totalAmount} ₽", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Адрес доставки: ${order.shippingAddress}", style = MaterialTheme.typography.bodyMedium)
    }
}
