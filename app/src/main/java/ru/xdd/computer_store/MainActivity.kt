// MainActivity.kt
package ru.xdd.computer_store

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.xdd.computer_store.ui.navigation.StoreNavGraph
import ru.xdd.computer_store.ui.theme.ComputerStoreTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComputerStoreTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // Допустим, `userId` получен после авторизации
                    val userId: Long = getUserIdFromPreferencesOrDefault() // Реализуйте этот метод

                    StoreNavGraph(
                        navController = navController,
                        userId = userId
                    )
                }
            }
        }
    }

    private fun getUserIdFromPreferencesOrDefault(): Long {
        // Пример: Получение userId из SharedPreferences
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        return sharedPreferences.getLong("user_id", -1) // -1 как значение по умолчанию
    }
}

