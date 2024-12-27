package ru.xdd.computer_store.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
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
    viewModel: AdminViewModel = hiltViewModel()
) {
    val accessories by viewModel.selectedProductAccessories.collectAsState()

    LaunchedEffect(productId) {
        viewModel.selectProduct(productId)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Аксессуары для продукта $productId") })
        }
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            items(accessories) { accessory ->
                AccessoryRow(
                    accessory = accessory,
                    onRemove = { viewModel.deleteProductAccessoryCrossRef(productId, accessory.productId) }
                )
            }
        }
    }
}

@Composable
fun AccessoryRow(accessory: ProductEntity, onRemove: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = accessory.name)
        Button(onClick = onRemove) {
            Text("Удалить")
        }
    }
}


