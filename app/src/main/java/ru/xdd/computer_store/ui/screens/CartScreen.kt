package ru.xdd.computer_store.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.xdd.computer_store.ui.components.BottomNavigationBar
import ru.xdd.computer_store.ui.viewmodel.CartViewModel
import ru.xdd.computer_store.ui.screens.components.CartItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    viewModel: CartViewModel = hiltViewModel()
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val totalAmount by viewModel.totalAmount.collectAsState()
    val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Корзина") }
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
            if (cartItems.isEmpty()) {
                Text(
                    text = "Корзина пуста",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                LazyColumn {
                    items(cartItems) { cartItem ->
                        val product = viewModel.getProductById(cartItem.productId)
                        if (product != null) {
                            CartItemCard(
                                cartItem = cartItem,
                                product = product,
                                onRemove = { viewModel.removeItemFromCart(cartItem.cartItemId) },
                                onUpdateQuantity = { cartItemId, quantity ->
                                    viewModel.updateCartItemQuantity(cartItemId, quantity)
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { viewModel.clearCart() },
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        Text("Очистить корзину")
                    }

                    Button(
                        onClick = {
                            if (isUserLoggedIn) {
                                navController.navigate("checkout_screen")
                            } else {
                                navController.navigate("registration_screen")
                            }
                        },
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    ) {
                        Text("Оформить заказ: $totalAmount ₽")
                    }
                }
            }
        }
    }
}




