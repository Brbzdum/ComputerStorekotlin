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

    private val _userId = MutableStateFlow<Long>(-1L)
    val userId: StateFlow<Long> = _userId.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            val (userId, _) = repository.getUser()
            if (userId != -1L) {
                _userId.value = userId
                _isLoggedIn.value = true
            } else {
                _userId.value = -1L
                _isLoggedIn.value = false
            }
        }
    }

    fun addReview(userId: Long, productId: Long, rating: Int, comment: String) {
        viewModelScope.launch {
            try {
                repository.addReview(userId, productId, rating, comment)
            } catch (e: IllegalArgumentException) {
                _errorMessage.value = e.message
            } catch (e: Exception) {
                _errorMessage.value = "Произошла ошибка при добавлении отзыва."
            }
        }
    }
}
