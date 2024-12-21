package ru.xdd.computer_store.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.xdd.computer_store.model.ProductEntity

@Composable
fun ProductItem(product: ProductEntity, onProductClick: (ProductEntity) -> Unit) { // <- Важный параметр onProductClick
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onProductClick(product) }, // <- Используем onProductClick здесь
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = product.imageUrl,
            contentDescription = null,
            modifier = Modifier.size(80.dp)
        )
        Column(Modifier.padding(start = 16.dp)) {
            Text(text = product.name)
            Text(text = product.price.toString() + " ₽")
        }
    }
}