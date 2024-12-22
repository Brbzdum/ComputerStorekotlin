package ru.xdd.computer_store.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.xdd.computer_store.data.repository.StoreRepository
import ru.xdd.computer_store.model.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: StoreRepository) : ViewModel() {

    // Global state
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // Product list as a Flow
    private val _products = repository.getAllProductsFlow().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    val products: StateFlow<List<ProductEntity>> = _products

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow("Все категории")
    val selectedCategory: StateFlow<String> = _selectedCategory

    val filteredProducts = combine(
        _products, _searchQuery, _selectedCategory
    ) { products, query, category ->
        products.filter { product ->
            (category == "Все категории" || product.category == category) &&
                    product.name.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    // Cart state
    private val _cartItems = MutableStateFlow<List<CartItemEntity>>(emptyList())
    val cartItems: StateFlow<List<CartItemEntity>> = _cartItems

    private val _cartIsLoading = MutableStateFlow(false)
    val cartIsLoading: StateFlow<Boolean> = _cartIsLoading

    private val _cartErrorMessage = MutableStateFlow<String?>(null)
    val cartErrorMessage: StateFlow<String?> = _cartErrorMessage

    val totalAmount = cartItems.map { items ->
        items.sumOf { cartItem ->
            val product = products.value.find { it.productId == cartItem.productId }
            (product?.price ?: 0.0) * cartItem.quantity
        }
    }


    fun addToCart(productId: Long, quantity: Int = 1) {
        _cartIsLoading.value = true
        viewModelScope.launch {
            try {
                repository.addToCart(1, productId, quantity) // userId = 1
                refreshCart()
                _cartIsLoading.value = false
            } catch (e: Exception) {
                _cartErrorMessage.value = "Ошибка добавления в корзину: ${e.message}"
                _cartIsLoading.value = false
            }
        }
    }

    fun removeFromCart(cartItemId: Long) {
        _cartIsLoading.value = true
        viewModelScope.launch {
            try {
                repository.removeCartItemById(cartItemId)
                refreshCart()
                _cartIsLoading.value = false
            } catch (e: Exception) {
                _cartErrorMessage.value = "Ошибка удаления из корзины: ${e.message}"
                _cartIsLoading.value = false
            }
        }
    }

    fun loadCartItems() {
        _cartIsLoading.value = true
        viewModelScope.launch {
            try {
                _cartItems.value = repository.getCartItemsForUserFlow(1).first()
                _cartIsLoading.value = false
            } catch (e: Exception) {
                _cartErrorMessage.value = "Ошибка загрузки корзины: ${e.message}"
                _cartIsLoading.value = false
            }
        }
    }

    private fun refreshCart() {
        viewModelScope.launch {
            _cartItems.value = repository.getCartItemsForUserFlow(1).first()
        }
    }

    // Reviews
    private val _reviews = MutableStateFlow<List<ReviewEntity>>(emptyList())
    val reviews: StateFlow<List<ReviewEntity>> = _reviews

    private val _reviewIsLoading = MutableStateFlow(false)
    val reviewIsLoading: StateFlow<Boolean> = _reviewIsLoading

    private val _reviewErrorMessage = MutableStateFlow<String?>(null)
    val reviewErrorMessage: StateFlow<String?> = _reviewErrorMessage

    fun loadReviewsForProduct(productId: Long) {
        _reviewIsLoading.value = true
        viewModelScope.launch {
            try {
                _reviews.value = repository.getReviewsForProductFlow(productId).first()
                _reviewIsLoading.value = false
            } catch (e: Exception) {
                _reviewErrorMessage.value = "Ошибка загрузки отзывов: ${e.message}"
                _reviewIsLoading.value = false
            }
        }
    }

    fun addReview(productId: Long, comment: String, rating: Int) {
        _reviewIsLoading.value = true
        viewModelScope.launch {
            try {
                repository.addReview(1, productId, rating, comment)
                loadReviewsForProduct(productId)
                _reviewIsLoading.value = false
            } catch (e: Exception) {
                _reviewErrorMessage.value = "Ошибка добавления отзыва: ${e.message}"
                _reviewIsLoading.value = false
            }
        }
    }

    // Checkout state
    private val _checkoutStatus = MutableStateFlow<CheckoutStatus?>(null)
    val checkoutStatus: StateFlow<CheckoutStatus?> = _checkoutStatus

    fun placeOrder(items: List<Pair<ProductEntity, Int>>, address: String) {
        _checkoutStatus.value = CheckoutStatus.Loading
        viewModelScope.launch {
            try {
                repository.placeOrder(1, items, address)
                _checkoutStatus.value = CheckoutStatus.Success
                refreshCart()
            } catch (e: Exception) {
                _checkoutStatus.value = CheckoutStatus.Error(e.message ?: "Unknown error")
            }
        }
    }

    sealed class CheckoutStatus {
        object Loading : CheckoutStatus()
        object Success : CheckoutStatus()
        data class Error(val message: String) : CheckoutStatus()
    }

    // Global error handling
    private val _globalErrorMessage = MutableStateFlow<String?>(null)
    val globalErrorMessage: StateFlow<String?> = _globalErrorMessage

    fun resetError() {
        _globalErrorMessage.value = null
    }
}
