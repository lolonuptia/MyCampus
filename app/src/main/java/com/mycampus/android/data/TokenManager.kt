package com.mycampus.android.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "mycampus_prefs")

/**
 * Stocke le token JWT + les infos de session renvoyées par AuthResponse.
 * Utilisé par AuthInterceptor pour attacher "Authorization: Bearer <token>"
 * à chaque requête, et par l'UI pour savoir si l'utilisateur est connecté.
 */
class TokenManager(private val context: Context) {

    companion object {
        private val KEY_TOKEN = stringPreferencesKey("jwt_token")
        private val KEY_USER_ID = longPreferencesKey("user_id")
        private val KEY_ETUDIANT_ID = longPreferencesKey("etudiant_id")
        private val KEY_NOM = stringPreferencesKey("nom")
        private val KEY_PRENOM = stringPreferencesKey("prenom")
        private val KEY_ROLE = stringPreferencesKey("role")
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[KEY_TOKEN] }
    val roleFlow: Flow<String?> = context.dataStore.data.map { it[KEY_ROLE] }
    val etudiantIdFlow: Flow<Long?> = context.dataStore.data.map { it[KEY_ETUDIANT_ID] }

    suspend fun getTokenOnce(): String? = context.dataStore.data.first()[KEY_TOKEN]
    suspend fun getEtudiantIdOnce(): Long? = context.dataStore.data.first()[KEY_ETUDIANT_ID]
    suspend fun getRoleOnce(): String? = context.dataStore.data.first()[KEY_ROLE]

    suspend fun saveSession(
        token: String,
        userId: Long,
        etudiantId: Long?,
        nom: String,
        prenom: String,
        role: String
    ) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TOKEN] = token
            prefs[KEY_USER_ID] = userId
            etudiantId?.let { prefs[KEY_ETUDIANT_ID] = it }
            prefs[KEY_NOM] = nom
            prefs[KEY_PRENOM] = prenom
            prefs[KEY_ROLE] = role
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}
