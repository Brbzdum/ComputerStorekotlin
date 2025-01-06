package ru.xdd.computer_store.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.xdd.computer_store.data.repository.StoreRepository
import ru.xdd.computer_store.model.ProductEntity
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val repository: StoreRepository
) : ViewModel() {

    val totalAmount: StateFlow<Double> = repository.getCartItemsForUserFlow(repository.getUser().first)
        .map { items ->
            items.sumOf { cartItem ->
                val product = repository.getAllProductsFlow().first().find { it.productId == cartItem.productId }
                (product?.price ?: 0.0) * cartItem.quantity
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    suspend fun placeOrder(userId: Long, shippingAddress: String) {
        val cartItems = repository.getCartItemsForUserFlow(userId).first()
        val products = repository.getAllProductsFlow().first()

        val items = cartItems.map { cartItem ->
            val product = products.find { it.productId == cartItem.productId }
                ?: throw IllegalArgumentException("Товар не найден")
            product to cartItem.quantity.toInt()
        }

        repository.createOrder(userId, items, shippingAddress)
    }
}
