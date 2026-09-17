package ovh.gabrielhuav.flasklogin

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // IMPORTANTE: Cambia las "X" por tu Dirección IPv4 real (ej. 192.168.1.75)
    private const val BASE_URL = "http://192.168.1.41:5000/"

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}