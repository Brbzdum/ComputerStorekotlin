//package ru.xdd.computer_store.ui.adapter
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import ru.xdd.computer_store.databinding.ItemProductBinding
//import ru.xdd.computer_store.model.ProductEntity
//
//class ProductAdapter(
//    private var items: List<ProductEntity>,
//    private val onProductClick: (ProductEntity) -> Unit
//) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
//
//    inner class ProductViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
//        fun bind(product: ProductEntity) {
//            binding.productName.text = product.name
//            binding.productDescription.text = product.description
//            binding.productPrice.text = "Цена: ${product.price}"
//            binding.root.setOnClickListener {
//                onProductClick(product)
//            }
//        }
//    }
//
//    fun updateData(newItems: List<ProductEntity>) {
//        items = newItems
//        notifyDataSetChanged()
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
//        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return ProductViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
//        holder.bind(items[position])
//    }
//
//    override fun getItemCount(): Int = items.size
//}
