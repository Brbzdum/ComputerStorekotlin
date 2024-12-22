//package ru.xdd.computer_store.ui.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.launch
//import ru.xdd.computer_store.data.repository.StoreRepository
//import ru.xdd.computer_store.model.ProductEntity
//import javax.inject.Inject
//
//@HiltViewModel
//class CatalogViewModel @Inject constructor(private val repository: StoreRepository) : ViewModel() {
//
//    private val _searchQuery = MutableStateFlow("")
//    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
//
//    private val _selectedCategory = MutableStateFlow("Все категории")
//    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()
//
//    private val _products = MutableStateFlow<List<ProductEntity>>(emptyList())
//    val products: StateFlow<List<ProductEntity>> = _products.asStateFlow()
//
//    private val _isLoading = MutableStateFlow(true)
//    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
//
//    private val _errorMessage = MutableStateFlow<String?>(null)
//    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
//
//
//    init {
//        loadProducts()
//    }
//
//    fun updateSearchQuery(query: String) {
//        _searchQuery.value = query
//        loadProducts()
//    }
//
//    fun filterByCategory(category: String) {
//        _selectedCategory.value = category
//        loadProducts()
//    }
//
//    private fun loadProducts() {
//        viewModelScope.launch {
//            try {
//                repository.getAllProductsFlow().collect { allProducts ->
//                    _products.value = allProducts.filter { product ->
//                        val categoryFilter = _selectedCategory.value == "Все категории" || product.category == _selectedCategory.value
//                        val searchFilter = product.name.contains(_searchQuery.value, ignoreCase = true)
//                        categoryFilter && searchFilter
//                    }
//                    _isLoading.value = false
//                }
//            } catch (e: Exception) {
//                _errorMessage.value = "Ошибка загрузки товаров: ${e.message}"
//                _isLoading.value = false
//            }
//        }
//    }
//}