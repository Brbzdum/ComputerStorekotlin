package ru.xdd.computer_store.ui.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.xdd.computer_store.data.repository.StoreRepository
import ru.xdd.computer_store.model.OrderEntity
import ru.xdd.computer_store.model.OrderStatus
import ru.xdd.computer_store.model.ProductEntity
import ru.xdd.computer_store.model.ReviewEntity
import ru.xdd.computer_store.model.UserEntity
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repository: StoreRepository
) : ViewModel() {

    val products: StateFlow<List<ProductEntity>> = repository.getAllProductsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val orders: StateFlow<List<OrderEntity>> = repository.getOrdersForAdminFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _accessories = MutableStateFlow<List<ProductEntity>>(emptyList())
    val accessories: StateFlow<List<ProductEntity>> = _accessories.asStateFlow()

    private fun setError(message: String) {
        _errorMessage.value = message
    }

    fun resetError() {
        _errorMessage.value = null
    }

    fun updateOrderStatus(orderId: Long, newStatus: OrderStatus) {
        viewModelScope.launch {
            try {
                repository.updateOrderStatus(orderId, newStatus)
            } catch (e: Exception) {
                setError("Ошибка обновления статуса заказа: ${e.message}")
            }
        }
    }

    fun addProduct(product: ProductEntity) {
        viewModelScope.launch {
            try {
                repository.addProduct(product)
            } catch (e: Exception) {
                setError("Ошибка добавления продукта: ${e.message}")
            }
        }
    }

    fun updateProduct(product: ProductEntity) {
        viewModelScope.launch {
            try {
                repository.updateProduct(product)
            } catch (e: Exception) {
                setError("Ошибка обновления продукта: ${e.message}")
            }
        }
    }

    fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            try {
                repository.deleteProduct(productId)
            } catch (e: Exception) {
                setError("Ошибка удаления продукта: ${e.message}")
            }
        }
    }

    fun deleteReview(reviewId: Long) {
        viewModelScope.launch {
            try {
                repository.deleteReview(reviewId)
            } catch (e: Exception) {
                setError("Ошибка удаления отзыва: ${e.message}")
            }
        }
    }

    fun loadAccessoriesForProduct(productId: Long) {
        viewModelScope.launch {
            try {
                repository.getAccessoriesForProductFlow(productId).collect {
                    _accessories.value = it
                }
            } catch (e: Exception) {
                setError("Ошибка загрузки аксессуаров: ${e.message}")
            }
        }
    }

    fun addAccessory(productId: Long, accessoryId: Long) {
        viewModelScope.launch {
            try {
                repository.updateProductAccessories(productId, accessoryId, isAdding = true)
                loadAccessoriesForProduct(productId) // Обновляем аксессуары
            } catch (e: Exception) {
                setError("Ошибка добавления аксессуара: ${e.message}")
            }
        }
    }

    fun removeAccessory(productId: Long, accessoryId: Long) {
        viewModelScope.launch {
            try {
                repository.updateProductAccessories(productId, accessoryId, isAdding = false)
                loadAccessoriesForProduct(productId) // Обновляем аксессуары
            } catch (e: Exception) {
                setError("Ошибка удаления аксессуара: ${e.message}")
            }
        }
    }
}

