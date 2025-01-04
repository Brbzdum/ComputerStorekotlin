package ru.xdd.computer_store.ui.viewmodel

import android.util.Log
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

    // Products
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

    // Cart
    private val _cartItems = repository.getCartItemsForUserFlow(1L).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    val cartItems: StateFlow<List<CartItemEntity>> = _cartItems

    val totalAmount = cartItems.map { items ->
        items.sumOf { cartItem ->
            val product = products.value.find { it.productId == cartItem.productId.toLong() }
            (product?.price ?: 0.0) * cartItem.quantity
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun addProductToCart(productId: Long, quantity: Long = 1) {
        viewModelScope.launch {
            try {
                repository.addProductToCart(1L, productId, quantity)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error adding to cart: ${e.message}")
            }
        }
    }

    fun removeItemFromCart(cartItemId: Long) {
        viewModelScope.launch {
            try {
                repository.removeItemFromCart(cartItemId)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error removing from cart: ${e.message}")
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            try {
                repository.clearUserCart(1L)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error clearing cart: ${e.message}")
            }
        }
    }

    // Reviews
    private val _reviews = MutableStateFlow<List<ReviewEntity>>(emptyList())
    val reviews: StateFlow<List<ReviewEntity>> = _reviews

    fun loadReviews(productId: Long) {
        viewModelScope.launch {
            try {
                _reviews.value = repository.getReviewsForProductFlow(productId).first()
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error loading reviews: ${e.message}")
            }
        }
    }

    fun addReview(productId: Long, rating: Int, comment: String) {
        viewModelScope.launch {
            try {
                repository.addReview(1L, productId, rating, comment)
                loadReviews(productId)
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error adding review: ${e.message}")
            }
        }
    }

    // Orders
    fun createOrder(items: List<Pair<ProductEntity, Int>>, address: String) {
        viewModelScope.launch {
            try {
                repository.createOrder(1L, items, address)
                clearCart()
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error creating order: ${e.message}")
            }
        }
    }
}
