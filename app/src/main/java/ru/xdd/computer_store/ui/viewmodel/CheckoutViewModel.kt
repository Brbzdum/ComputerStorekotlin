package ru.xdd.computer_store.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import ru.xdd.computer_store.data.repository.StoreRepository
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val repository: StoreRepository
) : ViewModel() {

    private val currentUser = repository.getUser()
    private val userId: Long = currentUser.first
    private val userRole = currentUser.second

    init {
        Log.d("CheckoutViewModel", "Initialized with userId=$userId, role=$userRole")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val totalAmount: StateFlow<Double> = repository.getCartItemsForUserFlow(userId).flatMapLatest { cartItems ->
        if (cartItems.isEmpty()) return@flatMapLatest flowOf(0.0)

        val productIds = cartItems.map { it.productId }
        val products = repository.getProductsByIds(productIds)

        flow {
            val total = cartItems.sumOf { cartItem ->
                val product = products.find { it.productId == cartItem.productId }
                (product?.price ?: 0.0) * cartItem.quantity
            }
            emit(total)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    suspend fun placeOrder(shippingAddress: String) {
        if (userId == -1L) {
            Log.e("CheckoutViewModel", "Attempting to place order for unauthenticated user.")
            throw IllegalStateException("Пользователь не авторизован.")
        }

        try {
            val orderId = repository.processOrderFromCart(userId, shippingAddress)
            Log.d("CheckoutViewModel", "Order placed successfully with orderId=$orderId")
        } catch (e: IllegalArgumentException) {
            Log.e("CheckoutViewModel", "Error placing order: ${e.message}")
            throw e
        }
    }
}


