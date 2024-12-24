package ru.xdd.computer_store.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.xdd.computer_store.ui.viewmodel.RegistrationViewModel

@Composable
fun RegistrationScreen(
    navController: NavController,
    viewModel: RegistrationViewModel = viewModel()
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val errorMessage by viewModel.errorMessage.collectAsState(initial = null)
    val registrationSuccess by viewModel.registrationSuccess.collectAsState(initial = false)

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Регистрация", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Имя пользователя") }
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                viewModel.register(username, email, password)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Зарегистрироваться")
        }

        errorMessage?.let { Text(it, color = Color.Red, modifier = Modifier.padding(top = 8.dp)) }

        // Переход на экран логина после успешной регистрации
        LaunchedEffect(registrationSuccess) {
            if (registrationSuccess) {
                navController.navigate("login") {
                    popUpTo("register") { inclusive = true }
                }
            }
        }
    }
}
