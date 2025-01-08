package ru.xdd.computer_store.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.xdd.computer_store.data.repository.StoreRepository
import ru.xdd.computer_store.model.OrderEntity
import ru.xdd.computer_store.model.Role
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: StoreRepository
) : ViewModel() {

    private val _userId = MutableStateFlow<Long>(-1L)
    val userId: StateFlow<Long> = _userId.asStateFlow()

    private val _userRole = MutableStateFlow<Role?>(null)
    val userRole: StateFlow<Role?> = _userRole.asStateFlow()

    private val _orders = MutableStateFlow<List<OrderEntity>>(emptyList())
    val orders: StateFlow<List<OrderEntity>> = _orders.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        loadProfile()
    }

    // Загрузка данных профиля
    fun loadProfile() {
        viewModelScope.launch {
            val (userId, role) = repository.getUser()
            Log.d("UserDebug", "Profile loaded: userId=$userId, role=$role")// Получаем данные из SharedPreferences

            if (userId != -1L && role != null) { // Если пользователь авторизован
                _userId.value = userId
                _userRole.value = role
                _isLoggedIn.value = true

                // Загружаем заказы пользователя
                repository.getOrdersForUserFlow(userId).collect { orders ->
                    _orders.value = orders
                }
            } else {
                clearProfile()
            }
        }
    }

    // Очистка данных профиля
    private fun clearProfile() {
        _userId.value = -1L
        _userRole.value = null
        _orders.value = emptyList()
        _isLoggedIn.value = false
    }

    // Выход из профиля
    fun logout() {
        viewModelScope.launch {
            repository.logoutUser() // Очищаем SharedPreferences
            clearProfile()
        }
    }
}
