// AdminUserScreen.kt
package ru.xdd.computer_store.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.xdd.computer_store.model.UserEntity
import ru.xdd.computer_store.ui.viewmodel.admin.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val users by viewModel.users.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Переменные для управления состоянием диалога
    var isEditDialogOpen by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<UserEntity?>(null) }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(it)
                viewModel.resetError()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text("Управление пользователями") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Логика добавления пользователя */ }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить пользователя")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (users.isEmpty()) {
                Text(
                    text = "Пользователи отсутствуют.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(users) { user ->
                        UserRow(
                            user = user,
                            onEdit = {
                                selectedUser = user
                                isEditDialogOpen = true
                            },
                            onDelete = { viewModel.deleteUser(user.userId) }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }

        // Диалог редактирования пользователя
        if (isEditDialogOpen) {
            EditUserDialog(
                user = selectedUser,
                onDismiss = { isEditDialogOpen = false },
                onSave = { updatedUser ->
                    viewModel.updateUser(updatedUser)
                    isEditDialogOpen = false
                }
            )
        }
    }
}

@Composable
fun EditUserDialog(
    user: UserEntity?,
    onDismiss: () -> Unit,
    onSave: (UserEntity) -> Unit
) {
    var username by remember { mutableStateOf(user?.username ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Редактировать пользователя") },
        text = {
            Column {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Имя пользователя") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (user != null) {
                    onSave(user.copy(username = username, email = email))
                }
            }) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@Composable
fun UserRow(user: UserEntity, onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Имя: ${user.username}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Email: ${user.email}", style = MaterialTheme.typography.bodyMedium)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onEdit) {
                Text("Редактировать")
            }
            Button(
                onClick = onDelete,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Удалить")
            }
        }
    }
}

