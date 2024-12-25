// CartViewModel.kt
package ru.xdd.computer_store.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.xdd.computer_store.data.repository.StoreRepository
import ru.xdd.computer_store.model.CartItemEntity
import ru.xdd.computer_store.model.ProductEntity
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: StoreRepository
) : ViewModel() {

    fun getCartItems(userId: Long): StateFlow<List<CartItemEntity>> =
        repository.getCartItemsForUserFlow(userId)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun getProductsMap(userId: Long): StateFlow<Map<Long, ProductEntity>> =
        getCartItems(userId).map { items ->
            items.mapNotNull { item ->
                repository.getProductById(item.productId)?.let { product ->
                    item.productId to product
                }
            }.toMap()
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyMap())



    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun removeCartItem(cartItemId: Long) {
        viewModelScope.launch {
            try {
                repository.removeCartItemById(cartItemId)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun clearCart(userId: Long) {
        viewModelScope.launch {
            try {
                repository.clearCartForUser(userId)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}



