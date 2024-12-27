// AdminOrderScreen.kt
package ru.xdd.computer_store.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.xdd.computer_store.model.OrderEntity
import ru.xdd.computer_store.model.OrderStatus
import ru.xdd.computer_store.ui.viewmodel.admin.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderScreen(
    viewModel: AdminViewModel = hiltViewModel(),
    navController: NavController
) {
    val orders by viewModel.orders.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(it)
                viewModel.resetError()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text("Управление заказами") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (orders.isEmpty()) {
                Text(
                    text = "Заказы отсутствуют.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(orders) { order ->
                        OrderRow(
                            order = order,
                            onEditStatus = {
                                navController.navigate("editOrderStatus/${order.orderId}")
                            },
                            onViewDetails = {
                                navController.navigate("orderDetails/${order.orderId}")
                            }
                        )
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun OrderRow(order: OrderEntity, onEditStatus: () -> Unit, onViewDetails: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Заказ №${order.orderId}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Пользователь ID: ${order.userId}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Сумма: ${order.totalAmount} ₽", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Статус: ${order.orderStatus}", style = MaterialTheme.typography.bodySmall)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onViewDetails) {
                Icon(Icons.Default.Info, contentDescription = "View Details")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Детали")
            }
            Button(onClick = onEditStatus) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Status")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Статус")
            }
        }
    }
}
