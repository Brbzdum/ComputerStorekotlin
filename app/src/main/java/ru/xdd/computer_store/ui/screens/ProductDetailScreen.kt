package ru.xdd.computer_store.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.xdd.computer_store.ui.screens.components.ProductCard
import ru.xdd.computer_store.ui.viewmodel.ProductDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Long,
    userId: Long,
    navController: NavController,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val productWithAccessories by viewModel.getProductWithAccessories(productId).collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(productWithAccessories?.product?.name ?: "Детали товара") },
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
            productWithAccessories?.let { details ->
                Text(text = details.product.name, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = details.product.description, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (userId == -1L) {
                            navController.navigate("login")
                        } else {
                            viewModel.addToCart(details.product.productId, userId)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Добавить в корзину")
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Рекомендуемые аксессуары:", style = MaterialTheme.typography.titleMedium)
                LazyColumn {
                    items(details.accessories) { accessory ->
                        ProductCard(
                            product = accessory,
                            onClick = {
                                navController.navigate("product_detail/${accessory.productId}")
                            },
                            onAddToCart = {
                                viewModel.addToCart(accessory.productId, userId)
                            }
                        )
                    }
                }
            } ?: Text("Загрузка данных...")
        }
    }
}
