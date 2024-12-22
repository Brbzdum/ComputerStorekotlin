package ru.xdd.computer_store.ui.screens
// Complete imports for the AdminViewModel and Composable screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Data models for User and Product
data class User(val id: Int, val name: String, val email: String)
data class Product(val id: Int, val name: String, val price: Double)

class AdminViewModel : ViewModel() {
    // State management for users and products
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    fun addUser(user: User) {
        viewModelScope.launch {
            _users.value = _users.value + user
        }
    }

    fun editUser(user: User) {
        viewModelScope.launch {
            _users.value = _users.value.map { if (it.id == user.id) user else it }
        }
    }

    fun deleteUser(userId: Int) {
        viewModelScope.launch {
            _users.value = _users.value.filter { it.id != userId }
        }
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            _products.value = _products.value + product
        }
    }

    fun editProduct(product: Product) {
        viewModelScope.launch {
            _products.value = _products.value.map { if (it.id == product.id) product else it }
        }
    }

    fun deleteProduct(productId: Int) {
        viewModelScope.launch {
            _products.value = _products.value.filter { it.id != productId }
        }
    }
}

// Composable functions for admin screens
@Composable
fun AdminUserScreen(viewModel: AdminViewModel) {
    val users = viewModel.users.collectAsState().value

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "User Management", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))

        users.forEach { user ->
            Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Text(text = user.name, modifier = Modifier.weight(1f))
                IconButton(onClick = { viewModel.deleteUser(user.id) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete User")
                }
            }
        }

        Button(onClick = { /* Navigate to add/edit user screen */ }) {
            Text(text = "Add User")
        }
    }
}

@Composable
fun AdminProductScreen(viewModel: AdminViewModel) {
    val products = viewModel.products.collectAsState().value

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Product Management", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        products.forEach { product ->
            Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Text(text = product.name, modifier = Modifier.weight(1f))
                IconButton(onClick = { viewModel.deleteProduct(product.id) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Product")
                }
            }
        }

        Button(onClick = { /* Navigate to add/edit product screen */ }) {
            Text(text = "Add Product")
        }
    }
}

