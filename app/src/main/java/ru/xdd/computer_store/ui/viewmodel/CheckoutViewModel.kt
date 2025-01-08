package ru.xdd.computer_store.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.xdd.computer_store.data.repository.StoreRepository
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val repository: StoreRepository
) : ViewModel() {

    val totalAmount: StateFlow<Double> =
        repository.getCartItemsForUserFlow(repository.getUser().first)
            .map { items ->
                items.sumOf { cartItem ->
                    val product = repository.getAllProductsFlow().firstOrNull()
                        ?.find { it.productId == cartItem.productId }
                    (product?.price ?: 0.0) * cartItem.quantity
                }
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)


    suspend fun placeOrder(userId: Long, shippingAddress: String) {
        try {
            val orderId = repository.processOrderFromCart(userId, shippingAddress)
            Log.d("CheckoutViewModel", "Order placed successfully with orderId=$orderId")
        } catch (e: IllegalArgumentException) {
            Log.e("CheckoutViewModel", "Error placing order: ${e.message}")
            throw e
        }
    }

}

