package ovh.gabrielhuav.flasklogin

// Datos que enviamos para el Login y Registro
data class AuthRequest(val username: String, val password: String)

// Respuesta que recibimos al iniciar sesión
data class AuthResponse(val status: String?, val message: String?, val access_token: String?)

// Modelo de tu recurso CRUD
data class Tarea(
    val id: Int = 0,
    val titulo: String,
    val descripcion: String
)

data class Vehiculo(
    val id: Int = 0,
    val marca: String,
    val modelo: String,
    val estado: String = "Activo"
)