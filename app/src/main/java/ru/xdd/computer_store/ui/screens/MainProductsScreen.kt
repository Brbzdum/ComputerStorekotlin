package ru.xdd.computer_store.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.xdd.computer_store.ui.components.BottomNavigationBar
import ru.xdd.computer_store.ui.screens.components.ProductCard
import ru.xdd.computer_store.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainProductsScreen(
    navController: NavController,
    userId: Long,
    viewModel: MainViewModel = hiltViewModel()
) {
    // Получение данных из ViewModel
    val filteredProducts by viewModel.filteredProducts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.loadProducts()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Компьютерный Магазин") }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp)
        ) {
            // Поле для поиска
            TextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                label = { Text("Поиск товаров") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Выпадающее меню для категорий
            val categories = listOf("Все категории", "Ноутбуки", "Компьютеры", "Аксессуары")
            DropdownMenu(selectedCategory, categories, viewModel::updateSelectedCategory)

            Spacer(modifier = Modifier.height(8.dp))

            // Список товаров
            if (filteredProducts.isEmpty()) {
                Text(
                    text = "Нет доступных товаров",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                LazyColumn {
                    items(filteredProducts) { product ->
                        ProductCard(
                            product = product,
                            onClick = {
                                navController.navigate("product_detail/${product.productId}")
                            },
                            onAddToCart = {
                                viewModel.addToCart(userId, product.productId)
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}


@Composable
fun DropdownMenu(
    selectedCategory: String,
    categories: List<String>,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Button(onClick = { expanded = !expanded }, modifier = Modifier.fillMaxWidth()) {
            Text(text = selectedCategory)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    },
                    text = {
                        Text(text = category)
                    }
                )

            }
        }
    }
}
