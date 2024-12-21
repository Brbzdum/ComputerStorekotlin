package ru.xdd.computer_store

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import ru.xdd.computer_store.ui.screens.SetupNavGraph
import ru.xdd.computer_store.ui.theme.Computer_storeTheme
import ru.xdd.computer_store.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called")
        enableEdgeToEdge()
        setContent {
            Log.d("MainActivity", "Setting content")
            Computer_storeTheme {
                val navController = rememberNavController()
                Log.d("MainActivity", "NavController initialized")
                val viewModel: MainViewModel = hiltViewModel()
                Log.d("MainActivity", "ViewModel initialized")

                Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                    Log.d("MainActivity", "Scaffold paddingValues: $paddingValues")
                    SetupNavGraph(navController = navController, viewModel = viewModel, paddingValues = paddingValues)
                }
            }
        }
    }
}
