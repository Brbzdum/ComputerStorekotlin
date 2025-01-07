// AdminOrderScreen.kt
package ru.xdd.computer_store.ui.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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

    var isEditDialogOpen by remember { mutableStateOf(false) }
    var selectedOrder by remember { mutableStateOf<OrderEntity?>(null) }

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
                                selectedOrder = order
                                isEditDialogOpen = true
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }

        // Диалог изменения статуса заказа
        if (isEditDialogOpen && selectedOrder != null) {
            EditOrderStatusDialog(
                order = selectedOrder,
                onDismiss = { isEditDialogOpen = false },
                onSave = { orderId, newStatus ->
                    viewModel.updateOrderStatus(orderId, newStatus) // Передаём orderId и статус
                    isEditDialogOpen = false
                }
            )
        }

    }
}

@Composable
fun OrderRow(order: OrderEntity, onEditStatus: () -> Unit) {
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
        Button(onClick = onEditStatus) {
            Text("Изменить статус")
        }
    }
}

@Composable
fun EditOrderStatusDialog(
    order: OrderEntity?,
    onDismiss: () -> Unit,
    onSave: (Long, OrderStatus) -> Unit // Передаем orderId и новый статус
) {
    var selectedStatus by remember { mutableStateOf(order?.orderStatus ?: OrderStatus.НОВЫЙ) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Изменить статус заказа") },
        text = {
            Column {
                Text("Выберите новый статус:", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Box {
                    Button(onClick = { isDropdownExpanded = true }) {
                        Text(text = selectedStatus.name)
                    }
                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        OrderStatus.entries.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.name) },
                                onClick = {
                                    selectedStatus = status
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (order != null) {
                    onSave(order.orderId, selectedStatus) // Передаём orderId и выбранный статус
                }
            }) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}



