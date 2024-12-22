package ru.xdd.computer_store.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.xdd.computer_store.model.ProductEntity
import ru.xdd.computer_store.ui.components.ProductItem
import ru.xdd.computer_store.ui.viewmodel.MainViewModel

@Composable
fun CatalogScreen(
    onProductClick: (ProductEntity) -> Unit,
    onCartClick: () -> Unit,
    viewModel: MainViewModel
) {
    val products by viewModel.filteredProducts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val isLoading by viewModel.cartIsLoading.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Каталог", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            label = { Text("Поиск товаров") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(products) { product ->
                ProductItem(product = product, onClick = { onProductClick(product) })
                Divider()
            }
        }

        FloatingActionButton(onClick = onCartClick, modifier = Modifier.align(Alignment.End)) {
            Icon(Icons.Default.ShoppingCart, contentDescription = "Корзина")
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

@Composable
fun ProductDetailScreen(
    productId: Long,
    onAddToCart: (Long) -> Unit,
    viewModel: MainViewModel
) {
    val product = viewModel.products.value.find { it.productId == productId }

    Column(modifier = Modifier.padding(16.dp)) {
        product?.let {
            Text(it.name, style = MaterialTheme.typography.headlineMedium)
            Text("Цена: ${it.price} ₽")
            Text("Описание: ${it.description}")

            Spacer(modifier = Modifier.height(8.dp))

            Text("Аксессуары:", style = MaterialTheme.typography.headlineSmall)
            LazyColumn {
                items(it.accessories) { accessory ->
                    Text("- ${accessory.name}")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { onAddToCart(productId) }) {
                Text("Добавить в корзину")
            }
        } ?: Text("Товар не найден")
    }
}

@Composable
fun CartScreen(
    onCheckout: () -> Unit,
    viewModel: MainViewModel
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val totalAmount by viewModel.totalAmount.collectAsState()
    val isLoading by viewModel.cartIsLoading.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Корзина", style = MaterialTheme.typography.headlineMedium)

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (cartItems.isEmpty()) {
            Text("Ваша корзина пуста", style = MaterialTheme.typography.bodyLarge)
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(cartItems) { cartItem ->
                    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                        Text(cartItem.productName, modifier = Modifier.weight(1f))
                        Text("${cartItem.price} ₽")
                        IconButton(onClick = { viewModel.removeFromCart(cartItem.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Удалить")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Итого: ${totalAmount} ₽", style = MaterialTheme.typography.bodyLarge)

            Button(onClick = onCheckout, modifier = Modifier.fillMaxWidth()) {
                Text("Оформить заказ")
            }
        }
    }
}

@Composable
fun ReviewsScreen(
    productId: Long,
    viewModel: MainViewModel
) {
    val reviews by viewModel.reviews.collectAsState()
    var reviewText by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(5) }
    val isLoading by viewModel.reviewIsLoading.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Отзывы", style = MaterialTheme.typography.headlineMedium)

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(reviews) { review ->
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Оценка: ${review.rating}")
                        Text("Комментарий: ${review.comment}")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Оставить отзыв", style = MaterialTheme.typography.headlineSmall)

            OutlinedTextField(
                value = reviewText,
                onValueChange = { reviewText = it },
                label = { Text("Комментарий") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = rating.toString(),
                onValueChange = { rating = it.toIntOrNull() ?: 5 },
                label = { Text("Оценка (1-5)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = {
                viewModel.addReview(productId, reviewText, rating)
                reviewText = ""
                rating = 5
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Добавить отзыв")
            }
        }
    }
}

@Composable
fun CheckoutScreen(
    onOrderPlaced: () -> Unit,
    viewModel: MainViewModel
) {
    var address by remember { mutableStateOf("") }
    val isLoading by viewModel.cartIsLoading.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Оформление заказа", style = MaterialTheme.typography.headlineMedium)

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Адрес доставки") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                viewModel.placeOrder(viewModel.cartItems.value.map { it.productId to it.quantity }, address)
                onOrderPlaced()
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Завершить заказ")
            }
        }
    }
}
