package ru.xdd.computer_store.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.xdd.computer_store.model.ReviewEntity

@Dao
interface ReviewDao {

    @Insert
    suspend fun insertReview(review: ReviewEntity): Long

    @Query("SELECT * FROM reviews WHERE productId = :productId")
    fun getReviewsForProductFlow(productId: Long): Flow<List<ReviewEntity>> // Изменено

    @Delete
    suspend fun deleteReview(reviewId: Long)

    @Query("SELECT * FROM reviews")
    fun getAllReviewsFlow(): Flow<List<ReviewEntity>>

}

