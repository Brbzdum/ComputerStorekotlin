//package ru.xdd.computer_store.ui.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.launch
//import ru.xdd.computer_store.data.repository.StoreRepository
//import javax.inject.Inject
//
//@HiltViewModel
//class CheckoutViewModel @Inject constructor(val repository: StoreRepository) : ViewModel() {
//
//    sealed class PlaceOrderStatus {
//        object Loading : PlaceOrderStatus()
//        object Success : PlaceOrderStatus()
//        class Error(val message: String) : PlaceOrderStatus()
//    }
//
//    private val _placeOrderStatus = MutableStateFlow<PlaceOrderStatus?>(null)
//    val placeOrderStatus: StateFlow<PlaceOrderStatus?> = _placeOrderStatus
//
//    private val _errorMessage = MutableStateFlow<String?>(null)
//    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
//
//    fun placeOrder(userId: Long, address: String) {
//        viewModelScope.launch {
//            _placeOrderStatus.value = PlaceOrderStatus.Loading
//            try {
//                val cartItems = repository.getCartItemsForUser(userId)
//                val productsWithQuantity = cartItems.mapNotNull { ci ->
//                    repository.getProductById(ci.productId)?.let { prod -> prod to ci.quantity }
//                }
//                repository.placeOrder(userId, productsWithQuantity, address)
//                _placeOrderStatus.value = PlaceOrderStatus.Success
//            } catch (e: Exception) {
//                _placeOrderStatus.value = PlaceOrderStatus.Error(e.message ?: "Неизвестная ошибка")
//                _errorMessage.value = e.message
//            }
//        }
//    }
//}