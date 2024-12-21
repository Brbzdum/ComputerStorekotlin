package ru.xdd.computer_store.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.xdd.computer_store.model.ProductEntity

import ru.xdd.computer_store.ui.viewmodel.CatalogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    onProductClick: (ProductEntity) -> Unit,
    onCartClick: () -> Unit,
    paddingValues: PaddingValues,
    viewModel: CatalogViewModel = viewModel()
) {
    val products by viewModel.products.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState(initial = true)
    val errorMessage by viewModel.errorMessage.collectAsState(initial = null)
    val categories = listOf("Все категории", "Компьютеры", "Ноутбуки", "Мониторы", "Клавиатуры", "Мыши")

    Column(modifier = Modifier
        .padding(paddingValues)
        .padding(16.dp)) {

        Text(text = "Каталог товаров", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Поиск") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Поиск") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { viewModel.filterByCategory(category) },
                    label = { Text(category) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (errorMessage != null) {
            Text(text = "Ошибка: $errorMessage", color = Color.Red)
        } else if (products.isEmpty()) {
            Text(text = "Товары не найдены", modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn {
                items(products) { product ->
                    ProductItem(product = product, onClick = { onProductClick(product) })
                    Divider()
                }
            }
        }
    }
}


