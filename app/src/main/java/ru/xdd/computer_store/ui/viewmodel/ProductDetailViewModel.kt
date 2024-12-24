// ProductDetailViewModel.kt
package ru.xdd.computer_store.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import ru.xdd.computer_store.data.repository.StoreRepository
import ru.xdd.computer_store.model.ProductWithAccessories
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val repository: StoreRepository
) : ViewModel() {

    fun getProductWithAccessories(productId: Long): StateFlow<ProductWithAccessories?> =
        repository.getProductWithAccessoriesFlow(productId)
            .stateIn(viewModelScope, SharingStarted.Lazily, null)

    // Методы для добавления/удаления аксессуаров, если необходимо
}
