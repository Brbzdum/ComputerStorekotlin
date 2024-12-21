package ru.xdd.computer_store.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.xdd.computer_store.ui.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = viewModel()
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val errorMessage by viewModel.errorMessage.collectAsState(initial = null)
    val user by viewModel.user.collectAsState(initial = null) // Добавлено для получения пользователя

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Вход", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Имя пользователя") }
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation()
        )

        Button(onClick = {
            viewModel.login(username, password)
        }) {
            Text("Войти")
        }

        errorMessage?.let { Text(it, color = Color.Red) }

        // Навигация теперь внутри LaunchedEffect, зависящего от user
        LaunchedEffect(user) {
            if (user != null) {
                if (user.role == "admin") {
                    navController.navigate("admin") {
                        popUpTo("login") { inclusive = true } // Предотвращаем возврат на экран логина
                    }
                } else {
                    navController.navigate("catalog") {
                        popUpTo("login") { inclusive = true } // Предотвращаем возврат на экран логина
                    }
                }
            }
        }
    }
}