package ru.xdd.computer_store.ui.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ru.xdd.computer_store.R
import ru.xdd.computer_store.model.CartItemEntity
import ru.xdd.computer_store.model.ProductEntity
import java.io.File

@Composable
fun CartItemRow(
    cartItem: CartItemEntity,
    product: ProductEntity
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