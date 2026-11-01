package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.EnclaveSaveState
import com.example.firebase.OperationType
import com.example.firebase.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class EnclaveRepository(private val db: FirebaseFirestore) {

    // CRITICAL: Always initialize with R.string.firestore_database_id
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun observeUserSaves(userId: String = requireUserId()): Flow<List<EnclaveSaveState>> = flow {
        val path = "users/$userId/enclave_saves"
        emitAll(
            db.collection("users")
                .document(userId)
                .collection("enclave_saves")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        EnclaveSaveState::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    if (error is Exception) {
                        handleFirestoreError(error, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    suspend fun getSaveById(
        saveId: String = PRIMARY_SAVE_ID,
        targetUserId: String? = null
    ): Result<EnclaveSaveState?> {
        val uid = targetUserId ?: requireUserId()
        val docRef = db.collection("users")
            .document(uid)
            .collection("enclave_saves")
            .document(saveId)
        return try {
            val snapshot = docRef.get().await()
            val model = snapshot.toObject(
                EnclaveSaveState::class.java,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            )
            Result.success(model)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun saveEnclaveState(
        saveId: String = PRIMARY_SAVE_ID,
        dayNumber: Int,
        relayStep: Int,
        activePovId: String,
        currentBiome: String,
        colonyMorale: Int,
        magneticFlux: Int,
        gatesTraversed: Int,
        statePayloadJson: String,
        lastSummary: String
    ): Result<String> {
        val uid = requireUserId()
        val docRef = db.collection("users")
            .document(uid)
            .collection("enclave_saves")
            .document(saveId)

        return try {
            val existingSnap = docRef.get().await()
            if (!existingSnap.exists()) {
                val createPayload = mapOf(
                    "userId" to uid,
                    "dayNumber" to dayNumber.toLong(),
                    "relayStep" to relayStep.toLong(),
                    "activePovId" to activePovId.take(64).ifBlank { "silas" },
                    "currentBiome" to currentBiome.take(64).ifBlank { "arboretum" },
                    "colonyMorale" to colonyMorale.coerceIn(0, 100).toLong(),
                    "magneticFlux" to magneticFlux.coerceIn(0, 100).toLong(),
                    "gatesTraversed" to gatesTraversed.coerceAtLeast(0).toLong(),
                    "statePayloadJson" to statePayloadJson.ifBlank { "{}" },
                    "lastSummary" to lastSummary.take(1900).ifBlank { "Enclave timeline updated." },
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(createPayload).await()
            } else {
                val updatePayload = mapOf(
                    "dayNumber" to dayNumber.toLong(),
                    "relayStep" to relayStep.toLong(),
                    "activePovId" to activePovId.take(64).ifBlank { "silas" },
                    "currentBiome" to currentBiome.take(64).ifBlank { "arboretum" },
                    "colonyMorale" to colonyMorale.coerceIn(0, 100).toLong(),
                    "magneticFlux" to magneticFlux.coerceIn(0, 100).toLong(),
                    "gatesTraversed" to gatesTraversed.coerceAtLeast(0).toLong(),
                    "statePayloadJson" to statePayloadJson.ifBlank { "{}" },
                    "lastSummary" to lastSummary.take(1900).ifBlank { "Enclave timeline updated." },
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.update(updatePayload).await()
            }
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun deleteSave(saveId: String = PRIMARY_SAVE_ID): Result<Unit> {
        val uid = requireUserId()
        val docRef = db.collection("users")
            .document(uid)
            .collection("enclave_saves")
            .document(saveId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    companion object {
        const val PRIMARY_SAVE_ID = "primary_enclave_save"
    }
}
