package ru.xdd.computer_store.ui.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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

    var isEditDialogOpen by remember { mutableStateOf(false) }
    var isAddDialogOpen by remember { mutableStateOf(false) }
    var isAddAccessoryDialogOpen by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<ProductEntity?>(null) }

    LaunchedEffect(selectedProduct) {
        selectedProduct?.let { viewModel.loadAccessoriesForProduct(it.productId) }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(it)
                viewModel.resetError()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text("Управление продуктами") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { isAddDialogOpen = true }) {
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
                        onEdit = {
                            selectedProduct = product
                            isEditDialogOpen = true
                        },
                        onDelete = { viewModel.deleteProduct(product.productId) },
                        onAddAccessory = {
                            selectedProduct = product
                            isAddAccessoryDialogOpen = true
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            selectedProduct?.let { product ->
                Text("Аксессуары для: ${product.name}", style = MaterialTheme.typography.titleMedium)
                LazyColumn {
                    items(selectedProductAccessories) { accessory ->
                        AccessoryRow(
                            accessory = accessory,
                            onRemove = { viewModel.removeAccessory(product.productId, accessory.productId) }
                        )
                    }
                }
            }
        }

        // Диалог редактирования продукта
        if (isEditDialogOpen) {
            EditProductDialog(
                product = selectedProduct,
                onDismiss = { isEditDialogOpen = false },
                onSave = { updatedProduct ->
                    viewModel.updateProduct(updatedProduct)
                    isEditDialogOpen = false
                }
            )
        }

        // Диалог добавления нового продукта
        if (isAddDialogOpen) {
            AddProductDialog(
                onDismiss = { isAddDialogOpen = false },
                onSave = { newProduct ->
                    viewModel.addProduct(newProduct)
                    isAddDialogOpen = false
                }
            )
        }

        // Диалог добавления аксессуара
        if (isAddAccessoryDialogOpen) {
            AddAccessoryDialog(
                onDismiss = { isAddAccessoryDialogOpen = false },
                onSave = { accessoryId ->
                    selectedProduct?.let { product ->
                        viewModel.addAccessory(product.productId, accessoryId)
                    }
                    isAddAccessoryDialogOpen = false
                }
            )
        }
    }
}

@Composable
fun ProductRow(
    product: ProductEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAddAccessory: () -> Unit
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
                Button(onClick = onDelete) { Text("Удалить") }
                Button(onClick = onAddAccessory) { Text("Аксессуары") }
            }
        }
    }
}

@Composable
fun AddAccessoryDialog(
    onDismiss: () -> Unit,
    onSave: (Long) -> Unit
) {
    var accessoryId by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить аксессуар") },
        text = {
            OutlinedTextField(
                value = accessoryId,
                onValueChange = { accessoryId = it },
                label = { Text("ID Аксессуара") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = {
                accessoryId.toLongOrNull()?.let(onSave)
            }) {
                Text("Добавить")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onSave: (ProductEntity) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить продукт") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Категория") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Цена") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(
                    ProductEntity(
                        productId = 0, // Новый ID будет сгенерирован в БД
                        name = name,
                        category = category,
                        price = price.toDoubleOrNull() ?: 0.0,
                        stock = 0, // По умолчанию
                        rating = 0.0f,
                        imageUrl = "" // Может быть пустым
                    )
                )
            }) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
@Composable
fun EditProductDialog(
    product: ProductEntity?,
    onDismiss: () -> Unit,
    onSave: (ProductEntity) -> Unit
) {
    var name by remember { mutableStateOf(product?.name ?: "") }
    var category by remember { mutableStateOf(product?.category ?: "") }
    var price by remember { mutableStateOf(product?.price?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Редактировать продукт") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Категория") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Цена") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (product != null) {
                    onSave(product.copy(name = name, category = category, price = price.toDoubleOrNull() ?: 0.0))
                }
            }) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
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


