// MainProductsScreen.kt
package ru.xdd.computer_store.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import ru.xdd.computer_store.model.ProductEntity
import ru.xdd.computer_store.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainProductsScreen(navController: NavController, userId: Long, viewModel: MainViewModel = hiltViewModel()) {
    val products by viewModel.mainProducts.collectAsState()
    val isGuest = userId == -1L

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Компьютерный Магазин") },
                actions = {
                    if (userId != -1L) {
                        IconButton(onClick = { navController.navigate("admin_products") }) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxSize()
        ) {
            items(products) { product ->
                ProductListItem(
                    product = product,
                    onClick = {
                        if (isGuest) {
                            // Если гость, перенаправляем на экран логина
                            navController.navigate("login")
                        } else {
                            // Если авторизован, переходим к деталям товара
                            navController.navigate("product_detail/${product.productId}")
                        }
                    },
                    onAddToCart = {
                        if (isGuest) {
                            // Если гость, перенаправляем на экран логина
                            navController.navigate("login")
                        } else {
                            // Добавить товар в корзину (здесь можно вызвать соответствующий метод в ViewModel)
                            viewModel.addToCart(userId, product.productId.toInt())
                        }
                    }
                )
                Divider()
            }
        }
    }
}

@Composable
fun ProductListItem(
    product: ProductEntity,
    onClick: () -> Unit,
    onAddToCart: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Image(
            painter = rememberImagePainter(product.imageUrl),
            contentDescription = product.name,
            modifier = Modifier
                .size(80.dp)
                .clickable { onClick() },
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = product.name, style = MaterialTheme.typography.titleMedium)
            Text(text = "${product.price} ₽", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Рейтинг: ${product.rating}", style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(onClick = { onAddToCart() }) {
            Text("В корзину")
        }
    }
}
