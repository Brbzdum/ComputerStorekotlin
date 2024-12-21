package ru.xdd.computer_store.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.xdd.computer_store.ui.viewmodel.CartViewModel
import ru.xdd.computer_store.model.CartItemEntity
import ru.xdd.computer_store.model.ProductEntity

@Composable
fun CartScreen(
    userId: Long,
    onCheckout: (Long) -> Unit,
    paddingValues: PaddingValues,
    viewModel: CartViewModel = viewModel()
) {
    LaunchedEffect(userId) {
        viewModel.loadCartItems(userId)
    }

    val cartItems by viewModel.cartItems.collectAsState(initial = emptyList())
    val products by viewModel.products.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState(initial = true)
    val errorMessage by viewModel.errorMessage.collectAsState(initial = null)

    Column(modifier = Modifier
        .padding(paddingValues)
        .fillMaxSize()) { // Используем Column и добавляем fillMaxSize()
        Text(text = "Корзина", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally)) // Центрируем индикатор загрузки
        } else if (errorMessage != null) {
            Text(text = "Ошибка: $errorMessage", color = Color.Red)
        } else if (cartItems.isEmpty()) {
            Text(text = "Корзина пуста", style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) { // Добавляем weight для заполнения доступного пространства
                items(cartItems) { cartItem ->
                    val product = products.find { it.productId == cartItem.productId }
                    product?.let { CartItemRow(cartItem = cartItem, product = it) }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isLoading && errorMessage == null && cartItems.isNotEmpty()) {
            Button(modifier = Modifier.fillMaxWidth(), onClick = { onCheckout(userId) }) { // Кнопка на всю ширину
                Text("Оформить заказ")
            }
        }
    }
}