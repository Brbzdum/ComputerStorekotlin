//package ru.xdd.computer_store.ui.fragments
//
//import android.os.Bundle
//import android.view.View
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.lifecycle.lifecycleScope
//import androidx.recyclerview.widget.LinearLayoutManager
//import kotlinx.coroutines.flow.collectLatest
//import kotlinx.coroutines.launch
//import ru.xdd.computer_store.R
//import ru.xdd.computer_store.data.repository.StoreRepository
//import ru.xdd.computer_store.databinding.FragmentCatalogBinding
//import ru.xdd.computer_store.ui.adapter.ProductAdapter
//import ru.xdd.computer_store.ui.viewmodel.MainViewModel
//import ru.xdd.computer_store.model.AppDatabase
//import androidx.room.Room
//import ru.xdd.computer_store.data.dao.*
//
//class CatalogFragment : Fragment(R.layout.fragment_catalog) {
//
//    private var binding: FragmentCatalogBinding? = null
//
//    // Здесь мы создаем ViewModel "на лету". В реальном проекте лучше использовать DI (Hilt/Koin)
//    private val viewModel: MainViewModel by viewModels {
//        val db = Room.databaseBuilder(
//            requireContext(),
//            AppDatabase::class.java,
//            "store.db"
//        ).build()
//
//        val repository = StoreRepository(
//            userDao = db.userDao(),
//            productDao = db.productDao(),
//            reviewDao = db.reviewDao(),
//            cartDao = db.cartDao(),
//            orderDao = db.orderDao()
//        )
//
//        object : androidx.lifecycle.ViewModelProvider.Factory {
//            @Suppress("UNCHECKED_CAST")
//            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
//                return MainViewModel(repository) as T
//            }
//        }
//    }
//
//    private lateinit var adapter: ProductAdapter
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        val bind = FragmentCatalogBinding.bind(view)
//        binding = bind
//
//        adapter = ProductAdapter(emptyList()) { product ->
//            // Обработка нажатия на товар, например переход к экрану деталей
//        }
//
//        bind.productsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
//        bind.productsRecyclerView.adapter = adapter
//
//        // Подписываемся на изменения списка отфильтрованных продуктов из ViewModel
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewModel.products.collectLatest { products ->
//                adapter.updateData(products)
//            }
//        }
//
//        // Отслеживаем изменения текста в поле поиска
//        bind.searchEditText.addTextChangedListener { text ->
//            viewModel.updateSearchQuery(text?.toString().orEmpty())
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        binding = null
//    }
//}
