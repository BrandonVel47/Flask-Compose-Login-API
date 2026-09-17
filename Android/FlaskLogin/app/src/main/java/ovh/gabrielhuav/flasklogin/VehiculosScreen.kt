package ovh.gabrielhuav.flasklogin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun VehiculosScreen(token: String) {
    val coroutineScope = rememberCoroutineScope()
    var vehiculos by remember { mutableStateOf(listOf<Vehiculo>()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    // Estados para el formulario flotante (Dialog)
    var showDialog by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var currentId by remember { mutableStateOf(0) }
    var marcaInput by remember { mutableStateOf("") }
    var modeloInput by remember { mutableStateOf("") }
    var estadoInput by remember { mutableStateOf("Activo") }

    // READ: Función para cargar la lista
    fun loadVehiculos() {
        coroutineScope.launch {
            isLoading = true
            try {
                vehiculos = RetrofitClient.instance.getVehiculos("Bearer $token")
                errorMessage = ""
            } catch (e: Exception) {
                errorMessage = "Error de conexión: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // Cargar los datos al abrir la pantalla
    LaunchedEffect(Unit) { loadVehiculos() }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                isEditing = false
                marcaInput = ""
                modeloInput = ""
                estadoInput = "Activo"
                showDialog = true
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar Vehículo")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Text("Gestión de Vehículos", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            // Manejo de estados de la interfaz
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(vehiculos) { vehiculo ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("${vehiculo.marca} ${vehiculo.modelo}", style = MaterialTheme.typography.titleMedium)
                                    Text("Estado: ${vehiculo.estado}", style = MaterialTheme.typography.bodyMedium)
                                }
                                Row {
                                    // UPDATE: Botón para editar
                                    IconButton(onClick = {
                                        isEditing = true
                                        currentId = vehiculo.id
                                        marcaInput = vehiculo.marca
                                        modeloInput = vehiculo.modelo
                                        estadoInput = vehiculo.estado
                                        showDialog = true
                                    }) {
                                        Icon(Icons.Filled.Edit, contentDescription = "Editar")
                                    }
                                    // DELETE: Botón para borrar
                                    IconButton(onClick = {
                                        coroutineScope.launch {
                                            try {
                                                RetrofitClient.instance.deleteVehiculo("Bearer $token", vehiculo.id)
                                                loadVehiculos()
                                            } catch (e: Exception) {
                                                errorMessage = "Error al borrar"
                                            }
                                        }
                                    }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Borrar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // CREATE / UPDATE: Cuadro de diálogo
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(if (isEditing) "Editar Vehículo" else "Nuevo Vehículo") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = marcaInput,
                            onValueChange = { marcaInput = it },
                            label = { Text("Marca") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = modeloInput,
                            onValueChange = { modeloInput = it },
                            label = { Text("Modelo") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        coroutineScope.launch {
                            val nuevoVehiculo = Vehiculo(id = currentId, marca = marcaInput, modelo = modeloInput, estado = estadoInput)
                            try {
                                if (isEditing) {
                                    RetrofitClient.instance.updateVehiculo("Bearer $token", currentId, nuevoVehiculo)
                                } else {
                                    RetrofitClient.instance.createVehiculo("Bearer $token", nuevoVehiculo)
                                }
                                showDialog = false
                                loadVehiculos()
                            } catch (e: Exception) {
                                errorMessage = "Error al guardar"
                            }
                        }
                    }) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
                }
            )
        }
    }
}