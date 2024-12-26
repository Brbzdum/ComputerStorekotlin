package ru.xdd.computer_store.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.xdd.computer_store.data.repository.StoreRepository
import ru.xdd.computer_store.model.UserEntity
import javax.inject.Inject

@HiltViewModel
class AdminUserViewModel @Inject constructor(
    private val repository: StoreRepository
) : ViewModel() {

    private val _users = MutableStateFlow<List<UserEntity>>(emptyList())
    val users: StateFlow<List<UserEntity>> = _users.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            try {
                repository.getAllUsersFlow().collect { userList ->
                    _users.value = userList
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun deleteUserById(userId: Long) {
        viewModelScope.launch {
            try {
                repository.deleteUserById(userId)
                _errorMessage.value = null
                loadUsers() // Обновляем список пользователей после удаления
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
}
