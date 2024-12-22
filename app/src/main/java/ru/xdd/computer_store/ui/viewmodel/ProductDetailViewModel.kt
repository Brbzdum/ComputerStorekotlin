//package ru.xdd.computer_store.ui.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.launch
//import ru.xdd.computer_store.data.repository.StoreRepository
//import ru.xdd.computer_store.model.ProductEntity
//import ru.xdd.computer_store.model.ReviewEntity
//import javax.inject.Inject
//
//@HiltViewModel
//class ProductDetailViewModel @Inject constructor(private val repository: StoreRepository) : ViewModel() {
//
//    private val _product = MutableStateFlow<ProductEntity?>(null)
//    val product: StateFlow<ProductEntity?> = _product.asStateFlow()
//
//    private val _reviews = MutableStateFlow<List<ReviewEntity>>(emptyList())
//    val reviews: StateFlow<List<ReviewEntity>> = _reviews.asStateFlow()
//
//    private val _isLoading = MutableStateFlow(true)
//    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
//
//    private val _errorMessage = MutableStateFlow<String?>(null)
//    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
//
//    fun loadProduct(productId: Long) {
//        viewModelScope.launch {
//            try {
//                _product.value = repository.getProductById(productId)
//                _isLoading.value = false
//            } catch (e: Exception) {
//                _errorMessage.value = "Ошибка загрузки товара: ${e.message}"
//                _isLoading.value = false
//            }
//        }
//    }
//
//    fun loadReviews(productId: Long) {
//        viewModelScope.launch {
//            try {
//                repository.getReviewsForProductFlow(productId).collect { reviews ->
//                    _reviews.value = reviews
//                    _isLoading.value = false
//                }
//            } catch (e: Exception) {
//                _errorMessage.value = "Ошибка загрузки отзывов: ${e.message}"
//                _isLoading.value = false
//            }
//        }
//    }
//
//    fun addReview(userId: Long, productId: Long, rating: Int, comment: String) {
//        viewModelScope.launch {
//            try {
//                repository.addReview(userId, productId, rating, comment)
//                loadReviews(productId) // Обновляем список отзывов после добавления
//            } catch (e: Exception) {
//                _errorMessage.value = "Ошибка добавления отзыва: ${e.message}"
//            }
//        }
//    }
//}