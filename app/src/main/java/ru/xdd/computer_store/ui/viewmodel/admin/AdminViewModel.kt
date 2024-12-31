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

    val reviews: StateFlow<List<ReviewEntity>> = repository.getAllReviewsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val users: StateFlow<List<UserEntity>> = repository.getAllUsersFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val products: StateFlow<List<ProductEntity>> = repository.getAllProductsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _selectedProductAccessories = MutableStateFlow<List<ProductEntity>>(emptyList())
    val selectedProductAccessories: StateFlow<List<ProductEntity>> = _selectedProductAccessories

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _orders = MutableStateFlow<List<OrderEntity>>(emptyList())
    val orders: StateFlow<List<OrderEntity>> = _orders.asStateFlow()

    private fun setError(message: String) {
        _errorMessage.value = message
    }

    fun resetError() {
        _errorMessage.value = null
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
    fun loadAllOrders() {
        viewModelScope.launch {
            try {
                repository.getAllOrdersFlow().collect {
                    _orders.value = it
                }
            } catch (e: Exception) {
                setError("Ошибка загрузки заказов: ${e.message}")
            }
        }
    }


    fun updateOrderStatus(orderId: Long, newStatus: OrderStatus) {
        viewModelScope.launch {
            try {
                repository.updateOrderStatus(orderId, newStatus)
                loadAllOrders() // Перезагружаем заказы после изменения
            } catch (e: Exception) {
                setError("Ошибка обновления статуса заказа: ${e.message}")
            }
        }
    }

    fun addUser(user: UserEntity) {
        viewModelScope.launch {
            try {
                if (user.username.isBlank() || user.email.isBlank() || user.passwordHash.isBlank()) {
                    setError("Все поля пользователя обязательны для заполнения.")
                    return@launch
                }
                repository.createUser(user.username, user.email, user.passwordHash, user.role.name)
            } catch (e: Exception) {
                setError("Ошибка добавления пользователя: ${e.message}")
            }
        }
    }

    fun updateUser(user: UserEntity) {
        viewModelScope.launch {
            try {
                repository.updateUser(user)
            } catch (e: Exception) {
                setError("Ошибка обновления пользователя: ${e.message}")
            }
        }
    }

    fun deleteUser(userId: Long) {
        viewModelScope.launch {
            try {
                repository.deleteUserById(userId)
            } catch (e: Exception) {
                setError("Ошибка удаления пользователя: ${e.message}")
            }
        }
    }

    fun addProduct(product: ProductEntity) {
        viewModelScope.launch {
            try {
                if (product.name.isBlank() || product.category.isBlank() || product.price <= 0) {
                    setError("Некорректные данные продукта.")
                    return@launch
                }
                repository.insertProduct(product)
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
                val product = repository.getProductByIdBlocking(productId)
                if (product != null) repository.deleteProduct(product)
                else setError("Продукт с ID $productId не найден.")
            } catch (e: Exception) {
                setError("Ошибка удаления продукта: ${e.message}")
            }
        }
    }

    fun loadAccessoriesForProduct(productId: Long) {
        viewModelScope.launch {
            try {
                repository.getAccessoriesForProductFlow(productId).collect {
                    _selectedProductAccessories.value = it
                }
            } catch (e: Exception) {
                setError("Ошибка загрузки аксессуаров: ${e.message}")
            }
        }
    }

    fun addAccessory(productId: Long, accessoryId: Long) {
        viewModelScope.launch {
            try {
                repository.addAccessoryToProduct(productId, accessoryId)
                loadAccessoriesForProduct(productId) // Обновляем список аксессуаров
            } catch (e: Exception) {
                setError("Ошибка добавления аксессуара: ${e.message}")
            }
        }
    }

    fun removeAccessory(productId: Long, accessoryId: Long) {
        viewModelScope.launch {
            try {
                repository.removeAccessoryFromProduct(productId, accessoryId)
                loadAccessoriesForProduct(productId) // Обновляем список аксессуаров
            } catch (e: Exception) {
                setError("Ошибка удаления аксессуара: ${e.message}")
            }
        }
    }
}
