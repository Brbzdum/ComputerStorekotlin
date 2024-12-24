package ru.xdd.computer_store.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ProductWithAccessories(
    @Embedded val product: ProductEntity,
    @Relation(
        parentColumn = "productId",
        entityColumn = "productId",
        associateBy = Junction(ProductAccessoryCrossRef::class)
    )
    val accessories: List<ProductEntity>
)
