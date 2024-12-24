// ReviewsViewModel.kt
package ru.xdd.computer_store.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.xdd.computer_store.data.repository.StoreRepository
import ru.xdd.computer_store.model.ReviewEntity
import javax.inject.Inject

@HiltViewModel
class ReviewsViewModel @Inject constructor(
    private val repository: StoreRepository
) : ViewModel() {

    fun getReviewsForProduct(productId: Long): StateFlow<List<ReviewEntity>> =
        repository.getReviewsForProductFlow(productId)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun resetError() {
        _errorMessage.value = null
    }

    suspend fun canLeaveReview(userId: Long, productId: Long): Boolean {
        return repository.hasCompletedOrderForProduct(userId, productId)
    }

    fun addReview(userId: Long, productId: Long, rating: Int, comment: String) {
        viewModelScope.launch {
            try {
                if (canLeaveReview(userId, productId)) {
                    repository.addReview(userId, productId, rating, comment)
                } else {
                    _errorMessage.value = "Вы не можете оставить отзыв, так как товар еще не прибыл."
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
}

