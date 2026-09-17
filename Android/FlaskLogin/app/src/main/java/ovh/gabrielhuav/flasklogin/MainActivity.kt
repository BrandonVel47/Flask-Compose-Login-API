package ovh.gabrielhuav.flasklogin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ovh.gabrielhuav.flasklogin.ui.theme.FlaskLoginTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlaskLoginTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    // Variables para controlar en qué pantalla estamos y guardar el token
    var currentScreen by remember { mutableStateOf("login") }
    var userToken by remember { mutableStateOf("") }

    when (currentScreen) {
        "login" -> LoginScreen(
            onLoginSuccess = { token ->
                userToken = token // Guardamos la "llave"
                currentScreen = "home" // Pasamos al CRUD
            },
            onNavigateToRegister = { currentScreen = "register" }
        )
        "register" -> RegisterScreen(
            onRegisterSuccess = { currentScreen = "login" },
            onNavigateToLogin = { currentScreen = "login" }
        )
        "home" -> {
            // Placeholder: Aquí construiremos tu CRUD en el siguiente paso
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                VehiculosScreen(token = userToken)
            }
        }
    }
}