package ru.xdd.computer_store.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.runBlocking
import ru.xdd.computer_store.data.repository.StoreRepository
import ru.xdd.computer_store.model.ReviewEntity

@Composable
fun ReviewsScreen(
    productId: Long,
    userId: Long,
    repository: StoreRepository,
    paddingValues: PaddingValues // Принимаем paddingValues
) {
    var reviews by remember { mutableStateOf<List<ReviewEntity>>(emptyList()) }
    var comment by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("5") }

    LaunchedEffect(productId) {
        reviews = repository.getReviewsForProduct(productId)
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .padding(paddingValues) // Используем paddingValues
    ) {
        Text(text = "Отзывы о товаре", style = MaterialTheme.typography.titleLarge)
        LazyColumn {
            items(reviews) { review ->
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("Рейтинг: ${review.rating}")
                    Text("Комментарий: ${review.comment}")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Оставить отзыв:", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = rating,
            onValueChange = { rating = it },
            label = { Text("Оценка (1-5)") },
            modifier = Modifier.fillMaxSize()
        )
        OutlinedTextField(
            value = comment,
            onValueChange = { comment = it },
            label = { Text("Комментарий") },
            modifier = Modifier.fillMaxSize()
        )

        Button(onClick = {
            val r = rating.toIntOrNull() ?: 5
            runBlocking { repository.addReview(userId, productId, r, comment) }
            runBlocking { reviews = repository.getReviewsForProduct(productId) }
            comment = ""
            rating = "5"
        }) {
            Text("Добавить отзыв")
        }
    }
}
