package com.mycampus.android.data.repository

import com.mycampus.android.data.TokenManager
import com.mycampus.android.data.dto.AuthResponse
import com.mycampus.android.data.dto.LoginRequest
import com.mycampus.android.data.dto.RegisterRequest
import com.mycampus.android.data.network.ApiService
import kotlinx.coroutines.flow.Flow

class AuthRepository(
    private val api: ApiService,
    private val tokenManager: TokenManager
) {
    val roleFlow: Flow<String?> = tokenManager.roleFlow
    val tokenFlow: Flow<String?> = tokenManager.tokenFlow

    suspend fun login(email: String, motDePasse: String): AuthResponse {
        val response = api.login(LoginRequest(email, motDePasse))
        persist(response)
        return response
    }

    suspend fun register(
        nom: String, prenom: String, email: String, motDePasse: String,
        matricule: String, filiere: String?, niveau: String?
    ): AuthResponse {
        val response = api.register(
            RegisterRequest(nom, prenom, email, motDePasse, matricule, filiere, niveau)
        )
        persist(response)
        return response
    }

    suspend fun logout() {
        tokenManager.clearSession()
    }

    suspend fun isLoggedIn(): Boolean = tokenManager.getTokenOnce() != null

    suspend fun currentEtudiantId(): Long? = tokenManager.getEtudiantIdOnce()
    suspend fun currentRole(): String? = tokenManager.getRoleOnce()

    private suspend fun persist(response: AuthResponse) {
        tokenManager.saveSession(
            token = response.token,
            userId = response.userId,
            etudiantId = response.etudiantId,
            nom = response.nom,
            prenom = response.prenom,
            role = response.role
        )
    }
}
