// AddAccessoryViewModel.kt
package ru.xdd.computer_store.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.xdd.computer_store.data.repository.StoreRepository
import javax.inject.Inject

@HiltViewModel
class AddAccessoryViewModel @Inject constructor(
    private val repository: StoreRepository
) : ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun addAccessory(productId: Long, accessoryId: Long) {
        viewModelScope.launch {
            try {
                repository.addAccessoryToProduct(productId, accessoryId)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun resetError() {
        _errorMessage.value = null
    }
}
