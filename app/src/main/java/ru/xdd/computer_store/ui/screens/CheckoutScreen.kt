package ru.xdd.computer_store.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.xdd.computer_store.ui.viewmodel.CheckoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    userId: Long,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    var address by remember { mutableStateOf("") }
    val totalAmount by viewModel.totalAmount.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Оформление заказа") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            TextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Адрес доставки") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Общая сумма: $totalAmount ₽",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))
            var errorMessage by remember { mutableStateOf<String?>(null) }

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            viewModel.placeOrder(address)
                            navController.navigate("orders_screen") // Переход на экран заказов
                        } catch (e: Exception) {
                            errorMessage = e.message ?: "Ошибка оформления заказа"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Подтвердить заказ")
            }

        }
    }
}
