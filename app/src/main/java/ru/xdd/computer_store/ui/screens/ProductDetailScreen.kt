// ProductDetailScreen.kt
package ru.xdd.computer_store.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
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
    val isGuest = userId == -1L

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
                        if (isGuest) {
                            navController.navigate("login")
                        } else {
                            // Добавить в корзину
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Добавить в корзину")
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Аксессуары:", style = MaterialTheme.typography.titleMedium)
                LazyColumn {
                    items(details.accessories) { accessory ->
                        ProductCard(
                            product = accessory,
                            onClick = { /* Переход к аксессуару */ },
                            onAddToCart = { /* Логика добавления аксессуара */ }
                        )
                    }
                }
            } ?: Text("Загрузка данных...")
        }
    }
}




