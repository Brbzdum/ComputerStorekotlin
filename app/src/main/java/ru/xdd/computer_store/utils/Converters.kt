package ru.xdd.computer_store.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.xdd.computer_store.model.OrderStatus
import ru.xdd.computer_store.model.Role

class Converters {

    // Конвертер для Role
    @TypeConverter
    fun fromRole(role: Role): String = role.name

    @TypeConverter
    fun toRole(role: String): Role = Role.valueOf(role)

    // Конвертер для OrderStatus
    @TypeConverter
    fun fromOrderStatus(status: OrderStatus): String = status.name

    @TypeConverter
    fun toOrderStatus(status: String): OrderStatus = OrderStatus.valueOf(status)

    // Конвертер для List<String>
    @TypeConverter
    fun fromStringList(list: List<String>): String = Gson().toJson(list)

    @TypeConverter
    fun toStringList(data: String): List<String> = Gson().fromJson(data, object : TypeToken<List<String>>() {}.type)
}
