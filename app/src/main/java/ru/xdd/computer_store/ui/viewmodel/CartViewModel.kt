package ru.xdd.computer_store.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.xdd.computer_store.data.repository.StoreRepository
import ru.xdd.computer_store.model.CartItemEntity
import ru.xdd.computer_store.model.ProductEntity
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: StoreRepository
) : ViewModel() {

    private val userId: Long = 1L

    // Поток элементов корзины
    val cartItems: StateFlow<List<CartItemEntity>> = repository.getCartItemsForUserFlow(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Список всех продуктов (чтобы минимизировать частые обращения к репозиторию)
    private val allProducts: StateFlow<List<ProductEntity>> = repository.getAllProductsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Общая стоимость
    val totalAmount = cartItems.map { items ->
        items.sumOf { cartItem ->
            val product = allProducts.value.find { it.productId == cartItem.productId }
            (product?.price ?: 0.0) * cartItem.quantity
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Удаление товара из корзины
    fun removeItemFromCart(cartItemId: Long) {
        viewModelScope.launch {
            repository.removeItemFromCart(cartItemId)
        }
    }

    // Обновление количества товара
    fun updateCartItemQuantity(cartItemId: Long, quantity: Long) {
        viewModelScope.launch {
            repository.updateCartItemQuantity(cartItemId, quantity)
        }
    }

    // Получение продукта по ID
    fun getProductById(productId: Long): ProductEntity? {
        return allProducts.value.find { it.productId == productId }
    }
}

