// AdminViewModel.kt
package ru.xdd.computer_store.ui.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.xdd.computer_store.data.repository.StoreRepository
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



    fun deleteReview(reviewId: Long) {
        viewModelScope.launch {
            repository.deleteReview(reviewId)
        }
    }

    fun addUser(user: UserEntity) {
        viewModelScope.launch {
            repository.createUser(user.username, user.email, user.passwordHash, user.role.name)
        }
    }

    fun updateUser(user: UserEntity) {
        viewModelScope.launch {
            repository.updateUser(user)
        }
    }

    fun deleteUser(userId: Long) {
        viewModelScope.launch {
            repository.deleteUserById(userId)
        }
    }

    fun addProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.insertProduct(product)
        }
    }

    fun updateProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }

    fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            val product = repository.getProductByIdBlocking(productId)
            if (product != null) repository.deleteProduct(product)
        }
    }
    fun loadAccessoriesForProduct(productId: Long) {
        viewModelScope.launch {
            repository.getAccessoriesForProductFlow(productId).collect {
                _selectedProductAccessories.value = it
            }
        }
    }

    fun addAccessory(productId: Long, accessoryId: Long) {
        viewModelScope.launch {
            repository.addAccessoryToProduct(productId, accessoryId)
            loadAccessoriesForProduct(productId) // Обновляем список аксессуаров
        }
    }

    fun removeAccessory(productId: Long, accessoryId: Long) {
        viewModelScope.launch {
            repository.removeAccessoryFromProduct(productId, accessoryId)
            loadAccessoriesForProduct(productId) // Обновляем список аксессуаров
        }
    }
}
}


