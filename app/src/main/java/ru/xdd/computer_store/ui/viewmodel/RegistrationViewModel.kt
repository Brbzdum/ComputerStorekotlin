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
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(private val repository: StoreRepository) : ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _registrationSuccess = MutableStateFlow(false)
    val registrationSuccess: StateFlow<Boolean> = _registrationSuccess.asStateFlow()

    fun register(username: String, email: String, password: String, role: String) {
        viewModelScope.launch {
            try {
                _errorMessage.value = null
                val hashedPassword = hashPassword(password)
                repository.createUser(username, email, hashedPassword, role)
                _registrationSuccess.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка регистрации: ${e.message}"
            }
        }
    }

    private fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }
}