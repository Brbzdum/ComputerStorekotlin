package ru.xdd.computer_store

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.xdd.computer_store.model.AppDatabase
import ru.xdd.computer_store.ui.navigation.StoreNavGraph
import ru.xdd.computer_store.ui.theme.ComputerStoreTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContent {
            ComputerStoreTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    val userId = getUserIdFromPreferencesOrDefault()
                    val userRole = getUserRoleFromPreferencesOrDefault()

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
        return sharedPreferences.getLong("user_id", -1L)
    }

    private fun getUserRoleFromPreferencesOrDefault(): String {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        return sharedPreferences.getString("user_role", "USER") ?: "USER"
    }
}
