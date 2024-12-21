package ru.xdd.computer_store.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.xdd.computer_store.model.UserEntity

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity? // Этот метод оставляем без изменений, так как он используется для авторизации

    @Query("SELECT * FROM users")
    fun getAllUsersFlow(): Flow<List<UserEntity>> // Изменено

    @Query("DELETE FROM users WHERE userId = :id")
    suspend fun deleteUserById(id: Long)

    @Update
    suspend fun updateUser(user: UserEntity)
}

