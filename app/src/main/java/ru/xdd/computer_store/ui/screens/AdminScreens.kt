package ru.xdd.computer_store.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.xdd.computer_store.model.ProductEntity
import ru.xdd.computer_store.model.UserEntity
import ru.xdd.computer_store.ui.viewmodel.MainViewModel

@Composable
fun AdminScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Админ Панель", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("admin/users") }) {
            Text("Управление пользователями")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("admin/products") }) {
            Text("Управление товарами")
        }
    }
}

@Composable
fun AdminUserManagementScreen(
    viewModel: MainViewModel
) {
    val users by viewModel.allUsers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Управление пользователями", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (error != null) {
            Text("Ошибка: $error", color = MaterialTheme.colorScheme.error)
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(users) { user ->
                    UserItem(
                        user = user,
                        onDelete = { viewModel.deleteUser(user.userId) },
                        onEdit = { /* Навигация на экран редактирования пользователя */ }
                    )
                }
            }
        }
    }
}

@Composable
fun UserItem(user: UserEntity, onDelete: () -> Unit, onEdit: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Имя: ${user.username}", style = MaterialTheme.typography.bodyLarge)
            Text("Email: ${user.email}", style = MaterialTheme.typography.bodySmall)
        }

        Row {
            Button(onClick = onEdit) {
                Text("Редактировать")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onDelete,
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
            ) {
                Text("Удалить")
            }
        }
    }
}

@Composable
fun AdminProductManagementScreen(
    viewModel: MainViewModel
) {
    val products by viewModel.allProducts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Управление товарами", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (error != null) {
            Text("Ошибка: $error", color = MaterialTheme.colorScheme.error)
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(products) { product ->
                    ProductItem(
                        product = product,
                        onDelete = { viewModel.deleteProduct(product.productId) },
                        onEdit = { /* Навигация на экран редактирования товара */ }
                    )
                }
            }
        }
    }
}

@Composable
fun ProductItem(product: ProductEntity, onDelete: () -> Unit, onEdit: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Название: ${product.name}", style = MaterialTheme.typography.bodyLarge)
            Text("Категория: ${product.category}", style = MaterialTheme.typography.bodySmall)
            Text("Цена: ${product.price}", style = MaterialTheme.typography.bodySmall)
        }

        Row {
            Button(onClick = onEdit) {
                Text("Редактировать")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = onDelete,
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
            ) {
                Text("Удалить")
            }
        }
    }
}
