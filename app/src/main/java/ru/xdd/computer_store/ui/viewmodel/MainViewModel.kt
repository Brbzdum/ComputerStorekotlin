package ru.xdd.computer_store.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.xdd.computer_store.data.repository.StoreRepository
import ru.xdd.computer_store.model.CartItemEntity
import ru.xdd.computer_store.model.ProductEntity
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: StoreRepository
) : ViewModel() {

    // Продукты
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow("Все категории")
    val selectedCategory: StateFlow<String> = _selectedCategory

    val products: StateFlow<List<ProductEntity>> = combine(
        repository.getAllProductsFlow(), _searchQuery, _selectedCategory
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

    // Корзина
    val cartItems: StateFlow<List<CartItemEntity>> = repository.getCartItemsForUserFlow(1L)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalAmount: StateFlow<Double> = cartItems.map { items ->
        items.sumOf { cartItem ->
            val product = repository.getAllProductsFlow().first().find { it.productId == cartItem.productId }
            (product?.price ?: 0.0) * cartItem.quantity
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun addToCart(productId: Long, quantity: Long = 1) {
        viewModelScope.launch {
            Log.d("CartDebug", "Adding productId=$productId to cart")
            repository.addProductToCart(productId, quantity)
        }
    }





}

