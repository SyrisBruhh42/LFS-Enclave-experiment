package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.util.UUID

class EnclaveRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun saveEnclaveState_authenticatedOwner_createsAndUpdatesDocument() = runBlocking {
        signInTestUser(ALICE_EMAIL)
        val repository = EnclaveRepository(firestore)
        val saveId = "save_${UUID.randomUUID()}"

        val createResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveEnclaveState(
                saveId = saveId,
                dayNumber = 1,
                relayStep = 1,
                activePovId = "silas",
                currentBiome = "arboretum",
                colonyMorale = 88,
                magneticFlux = 42,
                gatesTraversed = 1,
                statePayloadJson = "{\"day\":1}",
                lastSummary = "Silas patched the pipe."
            )
        }
        assertTrue("Expected save creation to succeed: ${createResult.exceptionOrNull()}", createResult.isSuccess)

        val updateResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveEnclaveState(
                saveId = saveId,
                dayNumber = 1,
                relayStep = 2,
                activePovId = "kaio",
                currentBiome = "arboretum",
                colonyMorale = 92,
                magneticFlux = 48,
                gatesTraversed = 2,
                statePayloadJson = "{\"day\":1,\"step\":2}",
                lastSummary = "Kaio triggered the flashbang mushroom."
            )
        }
        assertTrue("Expected save update to succeed: ${updateResult.exceptionOrNull()}", updateResult.isSuccess)

        val fetched = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.getSaveById(saveId).getOrThrow()
        }
        assertNotNull(fetched)
        assertEquals("kaio", fetched?.activePovId)
        assertEquals(2L, fetched?.gatesTraversed)
    }

    @Test
    fun observeUserSaves_authenticatedOwner_emitsRealtimeUpdates() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val repository = EnclaveRepository(firestore)
        val saveId = "save_${UUID.randomUUID()}"

        withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveEnclaveState(
                saveId = saveId,
                dayNumber = 2,
                relayStep = 1,
                activePovId = "augusta",
                currentBiome = "atlas_chamber",
                colonyMorale = 85,
                magneticFlux = 60,
                gatesTraversed = 3,
                statePayloadJson = "{\"day\":2}",
                lastSummary = "Augusta mapped Sector 4."
            ).getOrThrow()
        }

        val emitted = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeUserSaves(aliceUid).first { list -> list.any { it.id == saveId } }
        }
        assertTrue(emitted.any { it.id == saveId && it.activePovId == "augusta" })
    }

    @Test
    fun getSaveById_crossUserAccess_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = EnclaveRepository(firestore)
        val saveId = "save_${UUID.randomUUID()}"

        withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.saveEnclaveState(
                saveId = saveId,
                dayNumber = 1,
                relayStep = 1,
                activePovId = "silas",
                currentBiome = "arboretum",
                colonyMorale = 85,
                magneticFlux = 40,
                gatesTraversed = 0,
                statePayloadJson = "{}",
                lastSummary = "Initial state."
            ).getOrThrow()
        }

        signInTestUser(BOB_EMAIL)
        val bobRepo = EnclaveRepository(firestore)
        val crossUserResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            bobRepo.getSaveById(saveId = saveId, targetUserId = aliceUid)
        }
        assertTrue("Expected cross-user read to fail", crossUserResult.isFailure)
        val fsException = crossUserResult.exceptionOrNull()?.unwrapFirestoreException()
        assertNotNull("Expected FirebaseFirestoreException", fsException)
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, fsException?.code)
    }

    @Test
    fun observeUserSaves_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        auth.signOut()
        val repository = EnclaveRepository(firestore)

        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observeUserSaves(aliceUid).first()
            }
            fail("Expected unauthenticated observeUserSaves to throw PERMISSION_DENIED")
        } catch (e: Exception) {
            val fsException = e.unwrapFirestoreException()
            assertNotNull("Expected nested FirebaseFirestoreException, got $e", fsException)
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, fsException?.code)
        }
    }

    private companion object {
        const val ALICE_EMAIL = "alice@enclave16.test"
        const val BOB_EMAIL = "bob@enclave16.test"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 3000L
    }
}
