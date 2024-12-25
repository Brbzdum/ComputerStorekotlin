package ru.xdd.computer_store.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import ru.xdd.computer_store.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainProductsScreen(
    navController: NavController,
    userId: Long,
    viewModel: MainViewModel = hiltViewModel()
) {
    val products by viewModel.mainProducts.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Компьютерный Магазин", style = MaterialTheme.typography.titleLarge) },
                actions = {
                    // Кнопка для админских функций (доступна только для авторизованных администраторов)
                    if (userId != -1L) {
                        IconButton(onClick = { navController.navigate("admin_products") }) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            items(products) { product ->
                ProductCard(
                    product = product,
                    onClick = {
                        navController.navigate("product_detail/${product.productId}")
                    },
                    onAddToCart = {
                        if (userId == -1L) {
                            navController.navigate("login")
                        } else {
                            viewModel.addToCart(userId, product.productId.toInt())
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}


//@Composable
//fun ProductCard(
//    product: ProductEntity,
//    onClick: () -> Unit,
//    onAddToCart: () -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable { onClick() },
//        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//        shape = MaterialTheme.shapes.medium
//    ) {
//        Row(
//            modifier = Modifier
//                .padding(16.dp)
//                .fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Image(
//                painter = rememberAsyncImagePainter(product.imageUrl),
//                contentDescription = product.name,
//                modifier = Modifier
//                    .size(80.dp)
//                    .padding(8.dp),
//                contentScale = ContentScale.Crop
//            )
//            Spacer(modifier = Modifier.width(16.dp))
//            Column(modifier = Modifier.weight(1f)) {
//                Text(
//                    text = product.name,
//                    style = MaterialTheme.typography.titleMedium,
//                    maxLines = 1
//                )
//                Text(
//                    text = "${product.price} ₽",
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.primary
//                )
//                Text(
//                    text = "Рейтинг: ${product.rating}",
//                    style = MaterialTheme.typography.bodySmall,
//                    color = MaterialTheme.colorScheme.secondary
//                )
//            }
//            Spacer(modifier = Modifier.width(16.dp))
//            Button(onClick = { onAddToCart() }, modifier = Modifier.height(40.dp)) {
//                Text("В корзину")
//            }
//        }
//    }
//}
