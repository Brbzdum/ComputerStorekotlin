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
import ru.xdd.computer_store.ui.viewmodel.AccessoriesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessoriesScreen(
    navController: NavController,
    parentId: Long,
    viewModel: AccessoriesViewModel = hiltViewModel()
) {
    val accessories by viewModel.getAccessories(parentId).collectAsState(initial = emptyList())
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Аксессуары") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (accessories.isEmpty()) {
                Text(text = "Нет доступных аксессуаров.", style = MaterialTheme.typography.bodyLarge)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(accessories) { accessory ->
                        AccessoryRow(accessory = accessory) {
                            // Добавьте логику по клику на аксессуар, например переход к деталям
                            navController.navigate("product_detail/${accessory.productId}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AccessoryRow(
    accessory: ProductEntity,
    onClick: () -> Unit
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
            Column {
                Text(text = "Название: ${accessory.name}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Цена: ${accessory.price} ₽", style = MaterialTheme.typography.bodyMedium)
            }
            Button(onClick = onClick) {
                Text("Подробнее")
            }
        }
    }
}
