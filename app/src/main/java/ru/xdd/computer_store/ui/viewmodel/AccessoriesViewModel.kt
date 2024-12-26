package ru.xdd.computer_store.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.xdd.computer_store.data.repository.StoreRepository
import ru.xdd.computer_store.model.ProductEntity
import javax.inject.Inject

@HiltViewModel
class AccessoriesViewModel @Inject constructor(
    private val repository: StoreRepository
) : ViewModel() {

    fun getAccessories(parentId: Long): Flow<List<ProductEntity>> {
        return repository.getAccessoriesForProductFlow(parentId)
            .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.Lazily, emptyList())
    }

    fun addAccessory(parentId: Long, accessory: ProductEntity) {
        viewModelScope.launch {
            try {
                repository.addAccessoryToProduct(parentId, accessory.productId)
            } catch (e: Exception) {
                throw RuntimeException("Ошибка при добавлении аксессуара: ${e.message}")
            }
        }
    }

    fun removeAccessory(parentId: Long, accessoryId: Long) {
        viewModelScope.launch {
            try {
                repository.removeAccessoryFromProduct(parentId, accessoryId)
            } catch (e: Exception) {
                throw RuntimeException("Ошибка при удалении аксессуара: ${e.message}")
            }
        }
    }
}
