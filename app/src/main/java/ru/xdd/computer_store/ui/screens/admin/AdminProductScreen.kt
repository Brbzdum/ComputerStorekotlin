package ru.xdd.computer_store.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.xdd.computer_store.model.ProductEntity
import ru.xdd.computer_store.ui.viewmodel.admin.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductScreen(

    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val products by viewModel.products.collectAsState()
    val selectedProductAccessories by viewModel.accessories.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var selectedProductId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(selectedProductId) {
        selectedProductId?.let { viewModel.loadAccessoriesForProduct(it) }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(message)
                viewModel.resetError()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Управление продуктами") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("addProduct")
            }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить продукт")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            LazyColumn {
                items(products) { product ->
                    ProductRow(
                        product = product,
                        onEdit = { navController.navigate("editProduct/${product.productId}") },
                        onDelete = { viewModel.deleteProduct(product.productId) },
                        onSelect = {
                            selectedProductId = product.productId
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            selectedProductId?.let { productId ->
                Text("Аксессуары для продукта $productId", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                if (selectedProductAccessories.isEmpty()) {
                    Text("Нет добавленных аксессуаров.", style = MaterialTheme.typography.bodyLarge)
                } else {
                    LazyColumn {
                        items(selectedProductAccessories) { accessory ->
                            AccessoryRow(
                                accessory = accessory,
                                onRemove = {
                                    viewModel.removeAccessory(productId, accessory.productId)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { navController.navigate("addAccessory/$productId") },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Добавить аксессуар")
                }
            }
        }
    }
}

@Composable
fun ProductRow(
    product: ProductEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Название: ${product.name}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Категория: ${product.category}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Цена: ${product.price} ₽", style = MaterialTheme.typography.bodySmall)
            }
            Column(horizontalAlignment = Alignment.End) {
                Button(onClick = onEdit) { Text("Редактировать") }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onDelete) { Text("Удалить") }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onSelect) { Text("Аксессуары") }
            }
        }
    }
}

@Composable
fun AccessoryRow(accessory: ProductEntity, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = accessory.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = "Категория: ${accessory.category}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Цена: ${accessory.price} ₽", style = MaterialTheme.typography.bodySmall)
        }
        Button(onClick = onRemove) {
            Text("Удалить")
        }
    }
}
