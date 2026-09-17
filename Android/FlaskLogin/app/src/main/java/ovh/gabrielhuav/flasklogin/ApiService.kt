package ovh.gabrielhuav.flasklogin

import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Autenticación
    @POST("register")
    suspend fun register(@Body request: AuthRequest): Response<AuthResponse>

    @POST("login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    // Operaciones CRUD protegiendo la ruta con el Token JWT
    @GET("tareas")
    suspend fun getTareas(@Header("Authorization") token: String): Response<List<Tarea>>

    @POST("tareas")
    suspend fun createTarea(@Header("Authorization") token: String, @Body tarea: Tarea): Response<Tarea>

    @PUT("tareas/{id}")
    suspend fun updateTarea(@Header("Authorization") token: String, @Path("id") id: Int, @Body tarea: Tarea): Response<Tarea>

    @DELETE("tareas/{id}")
    suspend fun deleteTarea(@Header("Authorization") token: String, @Path("id") id: Int): Response<Unit>

    @GET("vehiculos")
    suspend fun getVehiculos(@Header("Authorization") token: String): List<Vehiculo>

    @POST("vehiculos")
    suspend fun createVehiculo(@Header("Authorization") token: String, @Body vehiculo: Vehiculo): Map<String, String>

    @PUT("vehiculos/{id}")
    suspend fun updateVehiculo(@Header("Authorization") token: String, @Path("id") id: Int, @Body vehiculo: Vehiculo): Map<String, String>

    @DELETE("vehiculos/{id}")
    suspend fun deleteVehiculo(@Header("Authorization") token: String, @Path("id") id: Int): Map<String, String>
}