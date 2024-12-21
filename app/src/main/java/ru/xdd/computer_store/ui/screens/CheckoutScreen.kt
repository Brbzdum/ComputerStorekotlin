package ru.xdd.computer_store.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.xdd.computer_store.ui.viewmodel.CheckoutViewModel

@Composable
fun CheckoutScreen(
    userId: Long,
    onOrderPlaced: () -> Unit,
    paddingValues: PaddingValues,
    viewModel: CheckoutViewModel = viewModel()
) {
    var address by remember { mutableStateOf("") }
    val placeOrderStatus by viewModel.placeOrderStatus.collectAsState(initial = null)
    val errorMessage by viewModel.errorMessage.collectAsState(initial = null)

    Column(modifier = Modifier.padding(paddingValues)) {
        Text(text = "Оформление заказа", style = MaterialTheme.typography.titleLarge)
        // ...

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Адрес доставки") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage != null) {
            Text(text = "Ошибка: $errorMessage", color = Color.Red)
        }

        Button(onClick = { viewModel.placeOrder(userId, address) },
            enabled = placeOrderStatus != CheckoutViewModel.PlaceOrderStatus.Loading
        ) {
            if (placeOrderStatus == CheckoutViewModel.PlaceOrderStatus.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
            } else {
                Text("Подтвердить заказ")
            }
        }

        if (placeOrderStatus == CheckoutViewModel.PlaceOrderStatus.Success) {
            onOrderPlaced()
        }
    }
}
