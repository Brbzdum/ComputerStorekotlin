// OrdersViewModel.kt
package ru.xdd.computer_store.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.xdd.computer_store.data.repository.StoreRepository
import ru.xdd.computer_store.model.OrderEntity
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val repository: StoreRepository
) : ViewModel() {

    fun getOrders(userId: Long): StateFlow<List<OrderEntity>> =
        repository.getOrdersForUserFlow(userId)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


}
