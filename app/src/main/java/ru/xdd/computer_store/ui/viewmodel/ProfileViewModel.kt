package ru.xdd.computer_store.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.xdd.computer_store.data.repository.StoreRepository
import ru.xdd.computer_store.model.OrderEntity
import ru.xdd.computer_store.model.UserEntity
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: StoreRepository
) : ViewModel() {

    private val _user = MutableStateFlow<UserEntity?>(null)
    val user: StateFlow<UserEntity?> = _user.asStateFlow()

    private val _orders = MutableStateFlow<List<OrderEntity>>(emptyList())
    val orders: StateFlow<List<OrderEntity>> = _orders.asStateFlow()

    fun loadProfile(userId: Long) {
        viewModelScope.launch {
            val user = repository.getUserById(userId)
            _user.value = user
            _orders.value = repository.getOrdersByUserId(userId)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _user.value = null
            _orders.value = emptyList()
        }
    }
}
