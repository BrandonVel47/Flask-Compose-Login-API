package ovh.gabrielhuav.flasklogin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Iniciar Sesión", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage.isNotEmpty()) {
            Text(errorMessage, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    isLoading = true
                    errorMessage = ""
                    coroutineScope.launch {
                        try {
                            val response = RetrofitClient.instance.login(AuthRequest(username, password))
                            if (response.isSuccessful && response.body()?.status == "success") {
                                response.body()?.access_token?.let { token ->
                                    onLoginSuccess(token) // Pasamos el token hacia arriba
                                }
                            } else {
                                errorMessage = "Credenciales incorrectas"
                            }
                        } catch (e: Exception) {
                            errorMessage = "Error de red. Revisa tu IP y conexión."
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Entrar")
            }
            TextButton(onClick = onNavigateToRegister) {
                Text("¿No tienes cuenta? Regístrate")
            }
        }
    }
}

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Crear Cuenta", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nuevo Usuario") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Nueva Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage.isNotEmpty()) {
            Text(errorMessage, color = MaterialTheme.colorScheme.error)
        }
        if (successMessage.isNotEmpty()) {
            Text(successMessage, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    isLoading = true
                    errorMessage = ""
                    successMessage = ""
                    coroutineScope.launch {
                        try {
                            val response = RetrofitClient.instance.register(AuthRequest(username, password))
                            if (response.isSuccessful) {
                                successMessage = "Cuenta creada exitosamente"
                                kotlinx.coroutines.delay(1000) // Pausa de 1 segundo para leer el mensaje
                                onRegisterSuccess() // Mandamos al usuario al login
                            } else {
                                errorMessage = "Error: El usuario ya existe"
                            }
                        } catch (e: Exception) {
                            errorMessage = "Error de conexión"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse")
            }
            TextButton(onClick = onNavigateToLogin) {
                Text("Volver al Inicio de Sesión")
            }
        }
    }
}