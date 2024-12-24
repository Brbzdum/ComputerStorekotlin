// AddProductViewModel.kt
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
class AddProductViewModel @Inject constructor(
    private val repository: StoreRepository
) : ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    suspend fun addProduct(product: ProductEntity): Long? {
        return try {
            repository.insertProduct(product)
        } catch (e: Exception) {
            _errorMessage.value = e.message
            null
        }
    }

    fun resetError() {
        _errorMessage.value = null
    }
}
