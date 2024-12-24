package ru.xdd.computer_store.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class Role {
    ADMIN,
    USER
}

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val userId: Long = 0,
    val username: String,
    val email: String,
    val passwordHash: String,
    val role: Role
)
