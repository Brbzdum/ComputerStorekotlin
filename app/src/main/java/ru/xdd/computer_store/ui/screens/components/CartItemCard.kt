package ru.xdd.computer_store.ui.screens.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.xdd.computer_store.model.CartItemEntity
import ru.xdd.computer_store.model.ProductEntity

@Composable
fun CartItemCard(
    cartItem: CartItemEntity,
    product: ProductEntity,
    onRemove: (Long) -> Unit,
    onUpdateQuantity: (Long, Long) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = product.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Цена: ${product.price} ₽", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Количество: ${cartItem.quantity}", style = MaterialTheme.typography.bodyMedium)
                Row {
                    IconButton(onClick = { onUpdateQuantity(cartItem.cartItemId, cartItem.quantity + 1) }) {
                        Text("+")
                    }
                    IconButton(onClick = {
                        if (cartItem.quantity > 1) {
                            onUpdateQuantity(cartItem.cartItemId, cartItem.quantity - 1)
                        }
                    }) {
                        Text("-")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { onRemove(cartItem.cartItemId) }) {
                Text("Удалить")
            }
        }
    }
}

