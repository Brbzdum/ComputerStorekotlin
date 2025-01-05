package ru.xdd.computer_store.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.xdd.computer_store.model.ProductEntity
import ru.xdd.computer_store.ui.components.BottomNavigationBar
import ru.xdd.computer_store.ui.screens.components.ProductCard
import ru.xdd.computer_store.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainProductsScreen(
    navController: NavController,
    viewModel: MainViewModel = hiltViewModel()
) {
    val products by viewModel.products.collectAsState() // Заменили filteredProducts на products
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

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
            SearchBar(
                query = searchQuery,
                onQueryChange = viewModel::updateSearchQuery
            )

            Spacer(modifier = Modifier.height(8.dp))

            CategoryDropdownMenu(
                selectedCategory = selectedCategory,
                categories = listOf("Все категории", "Ноутбуки", "Компьютеры", "Аксессуары"),
                onCategorySelected = viewModel::updateSelectedCategory
            )

            Spacer(modifier = Modifier.height(8.dp))

            ProductList(
                products = products,
                onProductClick = { productId ->
                    navController.navigate("product_detail/$productId")
                },
                onAddToCart = { productId ->
                    viewModel.addToCart(productId) // Заменили addProductToCart на addToCart
                }
            )
        }
    }
}


@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text("Поиск товаров") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun CategoryDropdownMenu(
    selectedCategory: String,
    categories: List<String>,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
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
                    text = { Text(text = category) }
                )
            }
        }
    }
}

@Composable
fun ProductList(
    products: List<ProductEntity>,
    onProductClick: (Long) -> Unit,
    onAddToCart: (Long) -> Unit
) {
    if (products.isEmpty()) {
        Text(
            text = "Нет доступных товаров",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )
    } else {
        LazyColumn {
            items(products) { product ->
                ProductCard(
                    product = product,
                    onClick = { onProductClick(product.productId) },
                    onAddToCart = { onAddToCart(product.productId) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
