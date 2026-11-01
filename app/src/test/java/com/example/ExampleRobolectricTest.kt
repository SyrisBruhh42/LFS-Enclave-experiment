package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ai.PsychometricGuardrailEngine
import com.example.data.local.EnclaveSaveSerializer
import com.example.data.model.Biome3D
import com.example.data.model.CognitiveResonanceAndMilestonesEngine
import com.example.data.model.DoorArchetype
import com.example.data.model.EnclaveSeedData
import com.example.data.model.InfiniteCorridorGenerator
import com.example.data.model.ResonanceTier
import com.example.data.model.Vec3
import com.example.ui.EnclaveSandboxUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Enclave 16", appName)
    }

    @Test
    fun `serialize and restore save document roundtrip preserves timeline and coordinates`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val customState = EnclaveSandboxUiState(
            dayNumber = 4,
            relayStep = 2,
            activePovId = "kaio",
            currentBiome = Biome3D.STORMBREAKER,
            playerPos = Vec3(5.5f, 0f, 11.2f),
            colonyMorale = 92,
            magneticFlux = 64,
            gatesTraversed = 9,
            hasSiblingSynergyBuff = true
        )

        val saveFileResult = EnclaveSaveSerializer.saveToInternalStorage(context, customState)
        assertTrue(saveFileResult.isSuccess)
        assertTrue(EnclaveSaveSerializer.hasInternalSave(context))

        val loadedResult = EnclaveSaveSerializer.loadFromInternalStorage(context)
        assertTrue(loadedResult != null && loadedResult.isSuccess)
        val restored = loadedResult!!.getOrThrow()

        assertEquals(4, restored.dayNumber)
        assertEquals(2, restored.relayStep)
        assertEquals("kaio", restored.activePovId)
        assertEquals(Biome3D.STORMBREAKER, restored.currentBiome)
        assertEquals(92, restored.colonyMorale)
        assertEquals(64, restored.magneticFlux)
        assertEquals(9, restored.gatesTraversed)
        assertTrue(restored.hasSiblingSynergyBuff)
        assertEquals(5.5f, restored.playerPos.x, 0.01f)
        assertEquals(11.2f, restored.playerPos.z, 0.01f)
    }

    @Test
    fun `psychometric guardrail engine locks OCEAN speech cadence and MBTI stack without drift`() {
        val silas = EnclaveSeedData.initialCharacters.first { it.id == "silas" }
        val kaio = EnclaveSeedData.initialCharacters.first { it.id == "kaio" }

        val silasLock = PsychometricGuardrailEngine.buildVoiceLock(silas)
        assertTrue(silasLock.oceanSpeechCadenceRule.contains("ULTRA-INTROVERTED"))
        assertTrue(silasLock.oceanSpeechCadenceRule.contains("HYPER-CONSCIENTIOUS"))
        assertTrue(silasLock.hudBadgeText.contains("ISTJ (Si>Te)"))

        val invertedSilas = silas.copy(isVexInverted = true, stressLevel = 90)
        val invertedLock = PsychometricGuardrailEngine.buildVoiceLock(invertedSilas)
        assertTrue(invertedLock.activeStateDirective.contains("VEX OVERRIDE ACTIVE"))
        assertTrue(invertedLock.hudBadgeText.contains("Pisces / Virgo"))

        val kaioLock = PsychometricGuardrailEngine.buildVoiceLock(kaio)
        assertTrue(kaioLock.oceanSpeechCadenceRule.contains("HIGH-EXTRAVERSION"))
        assertTrue(kaioLock.oceanSpeechCadenceRule.contains("HIGH-OPENNESS"))
    }

    @Test
    fun `infinite corridor generator spawns endless doors ahead without reusing any scenario signature`() {
        val chars = EnclaveSeedData.initialCharacters
        var (doors, usedSigs) = InfiniteCorridorGenerator.buildInitialCorridorDoors(
            characters = chars,
            dayNumber = 1
        )
        assertEquals(6, doors.size)
        assertEquals(6, usedSigs.size)

        // Simulate walking 300 meters down the enclosed corridor
        val result = InfiniteCorridorGenerator.ensureDoorsAhead(
            currentDoors = doors,
            usedSignatures = usedSigs,
            playerZ = 300f,
            characters = chars,
            dayNumber = 1
        )
        doors = result.first
        usedSigs = result.second

        assertTrue(doors.size >= 40)
        // Strictly zero reused scenario signatures
        val distinctSignatures = doors.map { it.scenarioSignature }.toSet()
        assertEquals(doors.size, distinctSignatures.size)
        // Every door has at least 3 distinct choices inside
        assertTrue(doors.all { it.options.size >= 3 })
        // Verify all 4 strategic door archetypes are spawned along the corridor
        val archetypesPresent = doors.map { it.archetype }.toSet()
        assertTrue(archetypesPresent.contains(DoorArchetype.STANDARD_LAB))
        assertTrue(archetypesPresent.contains(DoorArchetype.TICKING_CRISIS))
        assertTrue(archetypesPresent.contains(DoorArchetype.COGNITIVE_VAULT))
        assertTrue(archetypesPresent.contains(DoorArchetype.SANCTUARY_LOUNGE))
    }

    @Test
    fun `cognitive resonance and milestones engine evaluates locks breakthroughs and 50-100 project perks`() {
        val chars = EnclaveSeedData.initialCharacters
        val silas = chars.first { it.id == "silas" } // Si-Te-Fi-Ne
        val jane = chars.first { it.id == "jane" } // Ti-Se-Ni-Fe
        val baseProjects = EnclaveSeedData.initialGrandProjects

        val (doors, _) = InfiniteCorridorGenerator.buildInitialCorridorDoors(chars, 1)
        val vaultDoor = doors.first { it.archetype == DoorArchetype.COGNITIVE_VAULT }

        // Vault requires "Ti" or First Forge >= 75%: locked for Silas (Si > Te) initially, unlocked for Jane (Ti > Se)!
        assertFalse(CognitiveResonanceAndMilestonesEngine.isVaultUnlocked(vaultDoor, silas, baseProjects))
        assertTrue(CognitiveResonanceAndMilestonesEngine.isVaultUnlocked(vaultDoor, jane, baseProjects))

        // Boosting The Sanctuary from 49% to 55% unlocks its 50% Luciferin Spore Empathy milestone
        val upgradedProjects = baseProjects.map {
            if (it.id == "sanctuary") it.copy(progressPercent = 55) else it
        }
        val newlyUnlocked = CognitiveResonanceAndMilestonesEngine.detectNewlyUnlockedMilestones(
            previousProjects = baseProjects,
            updatedProjects = upgradedProjects
        )
        assertEquals(1, newlyUnlocked.size)
        assertEquals("Luciferin Spore Empathy", newlyUnlocked.first().title)

        // Verify all 16 milestones (50% and 100% across all 8 Grand Projects) build cleanly
        val allMilestones = CognitiveResonanceAndMilestonesEngine.buildAllProjectMilestones(upgradedProjects)
        assertEquals(16, allMilestones.size)

        // Evaluate Cognitive Resonance with Sibling Synergy buff
        val labDoor = doors.first { it.archetype == DoorArchetype.STANDARD_LAB }
        val evaluations = CognitiveResonanceAndMilestonesEngine.evaluateDoorOptionsResonance(
            door = labDoor,
            activeCharacter = silas,
            allCharacters = chars,
            projects = upgradedProjects,
            hasSiblingSynergyBuff = true
        )
        assertTrue(evaluations.values.any { it.tier == ResonanceTier.BREAKTHROUGH })
    }

    @Test
    fun `sibling bonds career medals grand convergence blackout and save deletion work end to end`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val baseBonds = com.example.data.model.EngagementAndEndgameEngine.defaultBondXpMap()
        assertEquals(8, baseBonds.size)

        // Gain Sibling Bond XP for Silas & Leo (duo_first_forge) to reach Rank V (Apex Polymath Bond)
        val pairId = com.example.data.model.EngagementAndEndgameEngine.pairIdForCharacter("silas")
        val boostedBonds = baseBonds + (pairId to 105)
        val forgeTrack = com.example.data.model.EngagementAndEndgameEngine.getBondTrackForCharacter("silas", boostedBonds)
        assertEquals(5, forgeTrack.rank)
        assertEquals(14, forgeTrack.resonanceBonusPercent)

        // Complete all 8 Grand Projects to 100%
        val completedProjects = EnclaveSeedData.initialGrandProjects.map { it.copy(progressPercent = 100) }
        assertTrue(com.example.data.model.EngagementAndEndgameEngine.isReadyForGrandConvergence(completedProjects))
        assertTrue(com.example.data.model.EngagementAndEndgameEngine.isEmergencyBlackoutActive(colonyMorale = 11, magneticFlux = 50))
        assertTrue(com.example.data.model.EngagementAndEndgameEngine.isEmergencyBlackoutActive(colonyMorale = 70, magneticFlux = 97))
        assertFalse(com.example.data.model.EngagementAndEndgameEngine.isEmergencyBlackoutActive(colonyMorale = 75, magneticFlux = 45))

        // Evaluate Career & Discovery Medals
        val (unlockedIds, newlyEarned) = com.example.data.model.EngagementAndEndgameEngine.evaluateAndUnlockMedals(
            currentUnlockedIds = emptySet(),
            gatesTraversed = 18,
            dayNumber = 3,
            projects = completedProjects,
            bondXpMap = boostedBonds,
            prestigeLevel = 1,
            resolvedDoorArchetype = DoorArchetype.TICKING_CRISIS,
            achievedBreakthrough = true,
            usedVexGate = true,
            queuedOrDetonatedPrank = true,
            completedGrandConvergence = true
        )
        assertEquals(10, unlockedIds.size)
        assertEquals(10, newlyEarned.size)

        // Verify Save Serializer roundtrip preserves Sibling Bonds, Medals, Prestige, and Audio/Haptics flag
        val prestigeState = EnclaveSandboxUiState(
            siblingBondXp = boostedBonds,
            unlockedMedalIds = unlockedIds,
            prestigeLevel = 2,
            isAudioAndHapticsEnabled = false
        )
        val json = EnclaveSaveSerializer.serializeToJsonString(prestigeState)
        val restored = EnclaveSaveSerializer.deserializeFromJsonString(json).getOrThrow()
        assertEquals(2, restored.prestigeLevel)
        assertFalse(restored.isAudioAndHapticsEnabled)
        assertEquals(10, restored.unlockedMedalIds.size)
        assertEquals(boostedBonds["duo_first_forge"], restored.siblingBondXp["duo_first_forge"])

        // Verify Play Store local save deletion
        EnclaveSaveSerializer.saveToInternalStorage(context, prestigeState)
        assertTrue(EnclaveSaveSerializer.hasInternalSave(context))
        assertTrue(EnclaveSaveSerializer.deleteLocalSaveFile(context))
        assertFalse(EnclaveSaveSerializer.hasInternalSave(context))
    }
}
