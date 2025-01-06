package ru.xdd.computer_store.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.xdd.computer_store.data.repository.StoreRepository
import ru.xdd.computer_store.model.CartItemEntity
import ru.xdd.computer_store.model.ProductEntity
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: StoreRepository
) : ViewModel() {

    // Получаем текущего пользователя
    private val currentUser = repository.getUser()
    private val userId: Long = currentUser.first // ID текущего пользователя

    // Проверка авторизации
    val isUserLoggedIn: StateFlow<Boolean> = flow {
        emit(userId != -1L && currentUser.second != null)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )

    // Поток элементов корзины
    val cartItems: StateFlow<List<CartItemEntity>> = flow {
        if (isUserLoggedIn.value) {
            // Корзина авторизованного пользователя
            emitAll(repository.getCartItemsForUserFlow(userId))
        } else {
            // Корзина гостя
            val guestCart = repository.getGuestCartItems()
            val allProducts = repository.getAllProductsFlow().first()
            val guestCartItems = guestCart.mapNotNull { (productId, quantity) ->
                allProducts.find { it.productId == productId }?.let {
                    CartItemEntity(
                        cartItemId = productId,
                        userId = -1L, // ID гостя
                        productId = productId,
                        quantity = quantity
                    )
                }
            }
            emit(guestCartItems)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Общая стоимость корзины
    val totalAmount: StateFlow<Double> = cartItems.map { items ->
        items.sumOf { cartItem ->
            repository.getAllProductsFlow().first().find { it.productId == cartItem.productId }?.price?.times(cartItem.quantity)
                ?: 0.0
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Удаление товара из корзины
    fun removeItemFromCart(cartItemId: Long) {
        if (isUserLoggedIn.value) {
            viewModelScope.launch {
                repository.removeItemFromCart(cartItemId)
            }
        } else {
            repository.removeGuestCartItem(cartItemId)
        }
    }

    // Очистка корзины
    fun clearCart() {
        if (isUserLoggedIn.value) {
            viewModelScope.launch {
                repository.clearUserCart(userId)
            }
        } else {
            repository.clearGuestCart()
        }
    }

    // Удаление товара по ID (используется в UI)
    fun removeFromCart(cartItemId: Long, productId: Long) {
        if (isUserLoggedIn.value) {
            viewModelScope.launch {
                repository.removeItemFromCart(cartItemId)
            }
        } else {
            repository.removeGuestCartItem(productId)
        }
    }

    // Получение продукта по ID
    fun getProductById(productId: Long): ProductEntity? {
        return runBlocking {
            repository.getAllProductsFlow().first().find { it.productId == productId }
        }
    }

    // Обновление количества товара в корзине
    fun updateCartItemQuantity(cartItemId: Long, quantity: Long) {
        if (isUserLoggedIn.value) {
            viewModelScope.launch {
                repository.updateCartItemQuantity(cartItemId, quantity)
            }
        } else {
            val guestCart = repository.getGuestCartItems().toMutableMap()
            if (quantity > 0) {
                guestCart[cartItemId] = quantity
            } else {
                guestCart.remove(cartItemId)
            }
            repository.updateGuestCartItems(guestCart)
        }
    }
}
