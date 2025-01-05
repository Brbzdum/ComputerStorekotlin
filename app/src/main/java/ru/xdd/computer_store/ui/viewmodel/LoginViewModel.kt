package ru.xdd.computer_store.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt
import ru.xdd.computer_store.data.repository.StoreRepository
import ru.xdd.computer_store.model.UserEntity
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: StoreRepository) : ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _user = MutableStateFlow<UserEntity?>(null)
    val user: StateFlow<UserEntity?> = _user.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                _errorMessage.value = null
                val userFromDb = repository.getUserByUsername(username)
                if (userFromDb != null && checkPassword(password, userFromDb.passwordHash)) {
                    repository.saveUser(userFromDb.userId, userFromDb.role)
                    _user.value = userFromDb
                } else {
                    _errorMessage.value = "Неверное имя пользователя или пароль"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка входа: ${e.message}"
            }
        }
    }


    private fun checkPassword(password: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(password, hashedPassword)
    }
}