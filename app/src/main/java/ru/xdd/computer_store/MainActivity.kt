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

                    // Получаем userId и userRole
                    val userId = getUserIdFromPreferencesOrDefault()
                    val userRole = getUserRoleFromPreferencesOrDefault()

                    // Передаем userId и userRole в StoreNavGraph
                    StoreNavGraph(
                        navController = navController,
                        userId = userId,
                        userRole = userRole
                    )
                }
            }
        }
    }

    private fun getUserIdFromPreferencesOrDefault(): Long {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        return sharedPreferences.getLong("user_id", -1L) // -1L означает гостя
    }

    private fun getUserRoleFromPreferencesOrDefault(): String {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        return sharedPreferences.getString("user_role", "USER") ?: "USER" // "USER" по умолчанию
    }
}
