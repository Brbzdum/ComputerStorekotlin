package ru.xdd.computer_store.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ru.xdd.computer_store.R
import ru.xdd.computer_store.model.ProductEntity

@Composable
fun ProductDetailScreen(
    product: ProductEntity?,
    onBackClick: () -> Unit,
    onAddReview: (String) -> Unit // Добавлена функция для добавления отзыва
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = product?.name ?: "Детали продукта") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (product == null) {
            // Обработка случая, когда продукт не найден или еще не загружен
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator() // Или Text("Продукт не найден")
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()) // Добавлена вертикальная прокрутка
                    .fillMaxSize()
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.imageUrl)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(R.drawable.ic_launcher_background),
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = product.name, style = MaterialTheme.typography.h5, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = product.description)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Цена: ${product.price} ₽", style = MaterialTheme.typography.subtitle1)
                Spacer(modifier = Modifier.height(16.dp))

                // Поле для ввода отзыва (пример)
                var reviewText = remember { mutableStateOf("") }
                OutlinedTextField(
                    value = reviewText.value,
                    onValueChange = { reviewText.value = it },
                    label = { Text("Ваш отзыв") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = { onAddReview(reviewText.value) }) {
                    Text("Добавить отзыв")
                }
            }
        }
    }
}