// EditProductViewModel.kt
package ru.xdd.computer_store.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.xdd.computer_store.data.repository.StoreRepository
import ru.xdd.computer_store.model.ProductEntity
import javax.inject.Inject

@HiltViewModel
class EditProductViewModel @Inject constructor(
    private val repository: StoreRepository
) : ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun getProduct(productId: Long): StateFlow<ProductEntity?> =
        repository.getAllProductsFlow()
            .map { it.find { product -> product.productId == productId } }
            .stateIn(viewModelScope, SharingStarted.Lazily, null)

    suspend fun updateProduct(product: ProductEntity) {
        try {
            repository.updateProduct(product)
        } catch (e: Exception) {
            _errorMessage.value = e.message
        }
    }

    fun resetError() {
        _errorMessage.value = null
    }
}
