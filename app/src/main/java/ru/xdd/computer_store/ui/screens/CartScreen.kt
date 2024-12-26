package ru.xdd.computer_store.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import ru.xdd.computer_store.R
import ru.xdd.computer_store.model.CartItemEntity
import ru.xdd.computer_store.model.ProductEntity
import ru.xdd.computer_store.ui.components.BottomNavigationBar
import ru.xdd.computer_store.ui.viewmodel.CartViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(userId: Long, navController: NavController, viewModel: CartViewModel = hiltViewModel()) {
    val cartItems by viewModel.getCartItems(userId).collectAsState()
    val productsMap by viewModel.getProductsMap(userId).collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(it)
                viewModel.clearErrorMessage() // Сбрасываем ошибку после показа
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Корзина") }
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (cartItems.isEmpty()) {
                Text(text = "Ваша корзина пуста.", style = MaterialTheme.typography.bodyLarge)
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(cartItems) { cartItem ->
                        val product = productsMap[cartItem.productId]
                        if (product != null) {
                            CartItemRow(cartItem = cartItem, product = product) {
                                viewModel.removeCartItem(cartItem.cartItemId)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (cartItems.isNotEmpty()) {
                            navController.navigate("checkout")
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Корзина пуста. Добавьте товары перед оформлением заказа.")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Оформить Заказ")
                }

                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.clearCart(userId) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Очистить Корзину")
                }
            }
        }
    }
}



@Composable
fun CartItemRow(
    cartItem: CartItemEntity,
    product: ProductEntity,
    onRemove: () -> Unit
) {
    val context = LocalContext.current
    val imageFile = remember(product.imageUrl) {
        if (product.imageUrl.isNotBlank()) File(context.filesDir, product.imageUrl) else null
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imageFile ?: painterResource(id = R.drawable.ic_placeholder),
            contentDescription = product.name,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.ic_placeholder),
            error = painterResource(id = R.drawable.ic_error)
        )
        Column(Modifier.padding(start = 16.dp)) {
            Text(text = product.name)
            Text(text = stringResource(R.string.quantity_label, cartItem.quantity))
        }
    }
}
