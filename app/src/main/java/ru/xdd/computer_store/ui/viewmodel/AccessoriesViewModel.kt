package ru.xdd.computer_store.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.xdd.computer_store.data.repository.StoreRepository
import ru.xdd.computer_store.model.ProductEntity
import javax.inject.Inject

@HiltViewModel
class AccessoriesViewModel @Inject constructor(
    private val repository: StoreRepository
) : ViewModel() {

    fun getAccessoriesForProduct(productId: Long): StateFlow<List<ProductEntity>> =
        repository.getAccessoriesForProductFlow(productId)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addAccessory(productId: Long, accessoryId: Long) {
        viewModelScope.launch {
            repository.updateProductAccessories(productId, accessoryId, isAdding = true)
        }
    }

    fun removeAccessory(productId: Long, accessoryId: Long) {
        viewModelScope.launch {
            repository.updateProductAccessories(productId, accessoryId, isAdding = false)
        }
    }
}

