package com.mycampus.android.data.network

import com.google.gson.Gson
import com.mycampus.android.BuildConfig
import com.mycampus.android.data.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private var apiService: ApiService? = null

    fun getInstance(tokenManager: TokenManager): ApiService {
        return apiService ?: synchronized(this) {
            apiService ?: build(tokenManager).also { apiService = it }
        }
    }

    private fun build(tokenManager: TokenManager): ApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager))
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

/**
 * Body JSON renvoyé par GlobalExceptionHandler.java :
 * { "timestamp": ..., "status": 404, "message": "..." }
 * ou pour les erreurs de validation : { "timestamp", "status", "erreurs": {champ: message} }
 */
data class ApiErrorBody(
    val status: Int? = null,
    val message: String? = null,
    val erreurs: Map<String, String>? = null
)

/**
 * Transforme une HttpException Retrofit en message lisible pour l'UI,
 * en parsant le corps d'erreur produit par GlobalExceptionHandler.
 */
fun Throwable.toUserMessage(): String {
    if (this is HttpException) {
        val errorBody = this.response()?.errorBody()?.string()
        if (!errorBody.isNullOrBlank()) {
            return try {
                val parsed = Gson().fromJson(errorBody, ApiErrorBody::class.java)
                parsed.message
                    ?: parsed.erreurs?.values?.joinToString("\n")
                    ?: "Erreur ${this.code()}"
            } catch (e: Exception) {
                "Erreur ${this.code()}"
            }
        }
        return when (this.code()) {
            401 -> "Email ou mot de passe incorrect"
            403 -> "Accès refusé"
            404 -> "Ressource introuvable"
            else -> "Erreur serveur (${this.code()})"
        }
    }
    return this.localizedMessage ?: "Une erreur réseau est survenue"
}
