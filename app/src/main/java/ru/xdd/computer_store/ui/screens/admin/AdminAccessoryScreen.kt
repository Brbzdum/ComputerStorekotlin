// AdminAccessoryScreen.kt
package ru.xdd.computer_store.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
fun AdminAccessoryScreen(
    productId: Long,
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val accessories by viewModel.selectedProductAccessories.collectAsState()
    val allProducts by viewModel.products.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Загружаем аксессуары для текущего продукта
    LaunchedEffect(productId) {
        viewModel.loadAccessoriesForProduct(productId)
    }

    // Обработка ошибок через Snackbar
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(message)
                viewModel.resetError() // Сбрасываем ошибку после показа
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Аксессуары для продукта $productId") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("addAccessory/$productId")
            }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить аксессуар")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (accessories.isEmpty()) {
                Text(
                    text = "Аксессуары не добавлены.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn {
                    items(accessories) { accessory ->
                        AccessoryRow(
                            accessory = accessory,
                            onRemove = {
                                viewModel.removeAccessory(productId, accessory.productId)
                            }
                        )
                        Divider()
                    }
                }
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
