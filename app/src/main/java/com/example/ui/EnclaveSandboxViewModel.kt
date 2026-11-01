package com.example.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.R
import com.example.data.ai.EnclaveAiDmService
import com.example.data.local.DailyDirective
import com.example.data.local.EnclaveSaveSerializer
import com.example.data.local.OfflineAutonomousReport
import com.example.data.local.RealTimeEngagementEngine
import com.example.data.local.TwinSunRealTimePhase
import com.example.data.model.Biome3D
import com.example.data.model.BlackMarketPrank
import com.example.data.model.ChamberAftermathReport
import com.example.data.model.ChoiceGate3D
import com.example.data.model.ChronicleEntry
import com.example.data.model.CognitiveResonanceAndMilestonesEngine
import com.example.data.model.CorridorDoor3D
import com.example.data.model.DoorArchetype
import com.example.data.model.DoorStatus
import com.example.data.model.EnclaveAchievementMedal
import com.example.data.model.EnclaveCharacter
import com.example.data.model.EngagementAndEndgameEngine
import com.example.data.model.EnclaveSeedData
import com.example.data.model.FeastExchange
import com.example.data.model.GrandProject
import com.example.data.model.HubVetoOption
import com.example.data.model.InfiniteCorridorGenerator
import com.example.data.model.ProjectMilestonePerk
import com.example.data.model.ResonanceTier
import com.example.data.model.SiblingBondTrack
import com.example.data.model.Vec3
import com.example.data.repository.EnclaveRepository
import com.example.ui.audio.EnclaveSoundAndHapticsManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.PI

private val initialCorridorBootstrap by lazy {
    InfiniteCorridorGenerator.buildInitialCorridorDoors(
        characters = EnclaveSeedData.initialCharacters,
        dayNumber = 1
    )
}

data class EnclaveSandboxUiState(
    val dayNumber: Int = 1,
    val relayStep: Int = 1, // 1..3 daytime POV relays, then Nightly Grand Feast available
    val characters: List<EnclaveCharacter> = EnclaveSeedData.initialCharacters,
    val activePovId: String = "silas",
    val currentBiome: Biome3D = Biome3D.ARBORETUM,
    val playerPos: Vec3 = Vec3(0f, 0f, 0f),
    val playerYaw: Float = 0f,
    val cameraYaw: Float = 0f,
    val cameraPitch: Float = 0.22f,
    val currentSceneTitle: String = "Infinite Enclave Corridor • Sector Bay 01+",
    val currentNarrativeProse: String = "You stand inside the Main Enclave Corridor—an endless, enclosed brass-and-bioluminescent hallway stretching across Kepler-452b. Walk forward to approach Standard Lab Bays, Ticking Crisis Bulkheads, Cognitive-Locked Vaults, and Sanctuary Lounge Alcoves along the Left and Right walls.",
    val currentScientificNote: String = "High-Entropy Alloys (HEAs) mix five elements in equal atomic proportions; each sealed Chamber Bay along the corridor isolates a live Grand Project experiment.",
    val lastBatonPassQuote: String = "\"Every door down this corridor holds a live crisis or sanctuary. Check your Cognitive Resonance, or swap POV to crack a locked vault.\"",
    val corridorDoors: List<CorridorDoor3D> = initialCorridorBootstrap.first,
    val usedScenarioSignatures: Set<String> = initialCorridorBootstrap.second,
    val inspectedDoorId: String? = null,
    val hasSiblingSynergyBuff: Boolean = false,
    val siblingBondXp: Map<String, Int> = EngagementAndEndgameEngine.defaultBondXpMap(),
    val unlockedMedalIds: Set<String> = emptySet(),
    val prestigeLevel: Int = 0,
    val isAudioAndHapticsEnabled: Boolean = true,
    val showGrandConvergenceFinale: Boolean = false,
    val breakthroughBurstCount: Int = 0,
    val latestAftermathReport: ChamberAftermathReport? = null,
    val activeGates: List<ChoiceGate3D> = EnclaveSeedData.buildGatesForCharacter(
        EnclaveSeedData.initialCharacters.first(),
        dayNumber = 1,
        relayStep = 1
    ),
    val grandProjects: List<GrandProject> = EnclaveSeedData.initialGrandProjects,
    val blackMarketQueue: List<BlackMarketPrank> = EnclaveSeedData.initialBlackMarketQueue,
    val detonatedPranksTonight: List<BlackMarketPrank> = emptyList(),
    val colonyMorale: Int = 86,
    val magneticFlux: Int = 42,
    val gatesTraversed: Int = 0,
    val isAtGrandFeast: Boolean = false,
    val feastDishOfTheNight: String = "Chitinous Flash-Mushroom & Methional Tuber Stew (Browned via Maillard reaction to taste exactly like buttered theater popcorn; served with grounded copper ankle-spoons).",
    val feastExchanges: List<FeastExchange> = EnclaveSeedData.defaultFeastDialogue,
    val hubVetoOptions: List<HubVetoOption> = EnclaveSeedData.hubVetoOptions,
    val chronicle: List<ChronicleEntry> = emptyList(),
    val isAiGenerating: Boolean = false,
    val aiStatusBanner: String? = null,
    val cloudSyncStatus: String = "Local Save Active",
    val lastSavedEpochMillis: Long = System.currentTimeMillis(),
    val realTimePhase: TwinSunRealTimePhase = RealTimeEngagementEngine.currentTwinSunPhase(),
    val pendingOfflineReport: OfflineAutonomousReport? = null
) {
    val activeCharacter: EnclaveCharacter
        get() = characters.firstOrNull { it.id == activePovId } ?: characters.first()

    val inspectedDoor: CorridorDoor3D?
        get() = corridorDoors.firstOrNull { it.id == inspectedDoorId }

    val dailyDirectives: List<DailyDirective>
        get() = RealTimeEngagementEngine.buildDailyDirectives(this)

    val overallReadinessPercent: Int
        get() = RealTimeEngagementEngine.computeOverallEnclaveReadiness(this)

    val projectMilestones: List<ProjectMilestonePerk>
        get() = CognitiveResonanceAndMilestonesEngine.buildAllProjectMilestones(grandProjects)

    val unlockedMilestonesCount: Int
        get() = projectMilestones.count { it.isUnlocked }

    val hasAtlasRadarPerk: Boolean
        get() = CognitiveResonanceAndMilestonesEngine.isMilestoneUnlocked(grandProjects, "living_atlas", 50)

    val siblingBondTracks: List<SiblingBondTrack>
        get() = EngagementAndEndgameEngine.buildAllSiblingBondTracks(siblingBondXp)

    val achievementMedals: List<EnclaveAchievementMedal>
        get() = EngagementAndEndgameEngine.buildAchievementMedals(unlockedMedalIds)

    val unlockedMedalsCount: Int
        get() = unlockedMedalIds.size

    val isEmergencyBlackoutActive: Boolean
        get() = EngagementAndEndgameEngine.isEmergencyBlackoutActive(colonyMorale, magneticFlux)

    val canTriggerGrandConvergence: Boolean
        get() = EngagementAndEndgameEngine.isReadyForGrandConvergence(grandProjects)
}

class EnclaveSandboxViewModel(
    private val repository: EnclaveRepository,
    private val aiDmService: EnclaveAiDmService = EnclaveAiDmService(),
    private val currentUserId: String,
    private val appContext: Context? = null,
    initialImportedJson: String? = null
) : ViewModel() {

    val isGuestMode: Boolean = currentUserId == EnclaveSaveSerializer.GUEST_USER_ID

    private val _uiState = MutableStateFlow(
        EnclaveSandboxUiState(
            cloudSyncStatus = if (isGuestMode) "Guest Sandbox • Local Save" else "Synced to Enclave Cloud"
        )
    )
    val uiState: StateFlow<EnclaveSandboxUiState> = _uiState.asStateFlow()

    private var gateTraversalCooldown = false

    init {
        if (!initialImportedJson.isNullOrBlank()) {
            restoreFromJsonString(initialImportedJson)
        } else {
            restoreInitialSaveState()
        }
    }

    private fun restoreInitialSaveState() {
        val ctx = appContext
        if (ctx != null) {
            val localLoaded = EnclaveSaveSerializer.loadFromInternalStorage(ctx, _uiState.value)
            if (localLoaded != null && localLoaded.isSuccess) {
                val restored = localLoaded.getOrNull()
                if (restored != null && (restored.gatesTraversed > 0 || restored.dayNumber > 1)) {
                    _uiState.value = restored.copy(
                        cloudSyncStatus = if (isGuestMode) {
                            "Guest Local Save • Day ${restored.dayNumber}.${restored.relayStep}"
                        } else {
                            "Local + Cloud • Day ${restored.dayNumber}.${restored.relayStep}"
                        }
                    )
                }
            }
        }

        if (!isGuestMode) {
            observeCloudSave()
        }
    }

    private fun observeCloudSave() {
        viewModelScope.launch {
            repository.observeUserSaves(currentUserId)
                .catch {
                    _uiState.update { state ->
                        state.copy(cloudSyncStatus = "Local Sandbox Active")
                    }
                }
                .collect { saves ->
                    val primary = saves.firstOrNull { it.id == EnclaveRepository.PRIMARY_SAVE_ID }
                        ?: saves.firstOrNull()
                    if (primary != null && primary.gatesTraversed > _uiState.value.gatesTraversed) {
                        val jsonRes = EnclaveSaveSerializer.deserializeFromJsonString(
                            jsonString = primary.statePayloadJson,
                            currentState = _uiState.value,
                            sourceLabel = "Restored Day ${primary.dayNumber} from Cloud"
                        )
                        if (jsonRes.isSuccess) {
                            _uiState.value = jsonRes.getOrThrow()
                        } else {
                            val activeChar = _uiState.value.characters.firstOrNull { it.id == primary.activePovId }
                                ?: _uiState.value.characters.first()
                            _uiState.update {
                                it.copy(
                                    dayNumber = primary.dayNumber.toInt(),
                                    relayStep = primary.relayStep.toInt().coerceIn(1, 3),
                                    activePovId = activeChar.id,
                                    currentBiome = Biome3D.fromId(primary.currentBiome),
                                    colonyMorale = primary.colonyMorale.toInt(),
                                    magneticFlux = primary.magneticFlux.toInt(),
                                    gatesTraversed = primary.gatesTraversed.toInt(),
                                    currentNarrativeProse = primary.lastSummary,
                                    activeGates = EnclaveSeedData.buildGatesForCharacter(
                                        activeChar,
                                        primary.dayNumber.toInt(),
                                        primary.relayStep.toInt().coerceIn(1, 3)
                                    ),
                                    cloudSyncStatus = "Restored Day ${primary.dayNumber} from Cloud"
                                )
                            }
                        }
                    }
                }
        }
    }

    fun saveLocalCheckpoint() {
        val ctx = appContext ?: return
        val state = _uiState.value
        val result = EnclaveSaveSerializer.saveToInternalStorage(ctx, state)
        _uiState.update {
            it.copy(
                cloudSyncStatus = if (result.isSuccess) {
                    "Local Saved • Day ${state.dayNumber}.${state.relayStep}"
                } else {
                    "Local Save Error"
                },
                aiStatusBanner = if (result.isSuccess) {
                    "Local checkpoint saved (Day ${state.dayNumber} • Relay ${state.relayStep})."
                } else {
                    "Failed to write local checkpoint."
                }
            )
        }
    }

    fun exportSaveToUri(uri: Uri) {
        val ctx = appContext ?: return
        val state = _uiState.value
        viewModelScope.launch {
            val res = EnclaveSaveSerializer.exportToUri(ctx, uri, state)
            _uiState.update {
                it.copy(
                    aiStatusBanner = if (res.isSuccess) {
                        "Exported Save Document (.json) for Day ${state.dayNumber} • Relay ${state.relayStep}!"
                    } else {
                        "Export failed: ${res.exceptionOrNull()?.localizedMessage ?: "Unknown error"}"
                    }
                )
            }
        }
    }

    fun importSaveFromUri(uri: Uri) {
        val ctx = appContext ?: return
        viewModelScope.launch {
            val res = EnclaveSaveSerializer.importFromUri(ctx, uri, _uiState.value)
            res.onSuccess { restoredState ->
                _uiState.value = restoredState
                EnclaveSaveSerializer.saveToInternalStorage(ctx, restoredState)
                if (!isGuestMode) {
                    persistCurrentState(summary = "Restored save point from uploaded .json document.")
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        aiStatusBanner = "Restore failed: ${err.localizedMessage ?: "Invalid save document"}"
                    )
                }
            }
        }
    }

    fun restoreFromJsonString(jsonString: String): Boolean {
        val res = EnclaveSaveSerializer.deserializeFromJsonString(
            jsonString = jsonString,
            currentState = _uiState.value,
            sourceLabel = "Restored from JSON Snapshot"
        )
        return if (res.isSuccess) {
            val restored = res.getOrThrow()
            _uiState.value = restored
            appContext?.let { EnclaveSaveSerializer.saveToInternalStorage(it, restored) }
            if (!isGuestMode) {
                persistCurrentState(summary = "Restored save point from JSON snapshot.")
            }
            true
        } else {
            false
        }
    }

    fun resetTimelineToDayOne() {
        val fresh = EnclaveSandboxUiState(
            cloudSyncStatus = if (isGuestMode) "Guest Sandbox • Reset Day 1" else "Reset Day 1",
            aiStatusBanner = "Timeline reset to Day 1 • Relay 1 (Silas in Bioluminescent Arboretum)."
        )
        _uiState.value = fresh
        appContext?.let { EnclaveSaveSerializer.saveToInternalStorage(it, fresh) }
        if (!isGuestMode) {
            persistCurrentState(summary = "Reset timeline to Day 1.")
        }
    }

    fun onMoveStep(dx: Float, dz: Float, newFacingYaw: Float?) {
        val state = _uiState.value
        if (state.isAtGrandFeast || gateTraversalCooldown) return

        val nextX = (state.playerPos.x + dx).coerceIn(-3.15f, 3.15f)
        val nextZ = (state.playerPos.z + dz).coerceAtLeast(-2.0f)
        onUpdateCorridorPosition(Vec3(nextX, 0f, nextZ), newFacingYaw ?: state.playerYaw)
    }

    /**
     * Updates the player's position along the Infinite Enclosed Corridor (+Z) and
     * automatically generates new, non-repeating Chamber Doors ahead of the player.
     */
    fun onUpdateCorridorPosition(newPos: Vec3, newYaw: Float) {
        val state = _uiState.value
        val (expandedDoors, expandedSignatures) = InfiniteCorridorGenerator.ensureDoorsAhead(
            currentDoors = state.corridorDoors,
            usedSignatures = state.usedScenarioSignatures,
            playerZ = newPos.z,
            characters = state.characters,
            dayNumber = state.dayNumber
        )
        _uiState.update {
            it.copy(
                playerPos = newPos,
                playerYaw = newYaw,
                corridorDoors = expandedDoors,
                usedScenarioSignatures = expandedSignatures
            )
        }
    }

    fun toggleAudioAndHaptics() {
        val nextEnabled = !_uiState.value.isAudioAndHapticsEnabled
        EnclaveSoundAndHapticsManager.isAudioEnabled = nextEnabled
        if (nextEnabled) {
            EnclaveSoundAndHapticsManager.playResonantSfx()
        }
        _uiState.update {
            it.copy(
                isAudioAndHapticsEnabled = nextEnabled,
                aiStatusBanner = if (nextEnabled) {
                    "🔊 Sci-Fi Synth Audio & Tactile Haptics Enabled"
                } else {
                    "🔇 Audio & Haptics Muted"
                }
            )
        }
    }

    /**
     * Opens a Corridor Door's interactive multi-option decision popup without forcing a choice
     * or resetting the player's hallway coordinates.
     */
    fun onOpenCorridorDoor(door: CorridorDoor3D) {
        when (door.archetype) {
            DoorArchetype.TICKING_CRISIS -> EnclaveSoundAndHapticsManager.playCrisisAlertSfx()
            DoorArchetype.SANCTUARY_LOUNGE -> EnclaveSoundAndHapticsManager.playSanctuarySfx()
            else -> EnclaveSoundAndHapticsManager.playDoorOpenSfx()
        }
        _uiState.update { state ->
            val updatedDoors = state.corridorDoors.map { d ->
                if (d.id == door.id && d.status == DoorStatus.UNEXPLORED) {
                    d.copy(status = DoorStatus.INSPECTED)
                } else {
                    d
                }
            }
            state.copy(
                corridorDoors = updatedDoors,
                inspectedDoorId = door.id,
                latestAftermathReport = if (door.status == DoorStatus.RESOLVED && state.latestAftermathReport?.doorId == door.id) {
                    state.latestAftermathReport
                } else {
                    null
                },
                currentSceneTitle = door.chamberTitle,
                currentNarrativeProse = door.roomDilemmaProse,
                currentScientificNote = door.scientificCheeseNote
            )
        }
    }

    /**
     * Unlocks a [DoorArchetype.COGNITIVE_VAULT] door either via the First Forge 50% Perk (free)
     * or via Force Breach (+18% Vex Stress).
     */
    fun onUnlockVaultDoor(door: CorridorDoor3D, forceBreachWithStress: Boolean) {
        EnclaveSoundAndHapticsManager.playVaultUnlockSfx()
        val state = _uiState.value
        val activeChar = state.activeCharacter
        val updatedChars = if (forceBreachWithStress) {
            state.characters.map { ch ->
                if (ch.id == activeChar.id) {
                    val nextStress = (ch.stressLevel + 18).coerceIn(5, 100)
                    ch.copy(stressLevel = nextStress, isVexInverted = ch.isVexInverted || nextStress >= 80)
                } else {
                    ch
                }
            }
        } else {
            state.characters
        }

        val updatedDoors = state.corridorDoors.map { d ->
            if (d.id == door.id) {
                d.copy(isVaultUnlockedByOverride = true, status = DoorStatus.INSPECTED)
            } else {
                d
            }
        }

        _uiState.update {
            it.copy(
                characters = updatedChars,
                corridorDoors = updatedDoors,
                aiStatusBanner = if (forceBreachWithStress) {
                    "⚡ Force-breached ${door.chamberTitle} (+18% Vex Stress on ${activeChar.name})!"
                } else {
                    "🔥 Used First Forge 50% Bulkhead Override Torch to unlock ${door.chamberTitle} for free!"
                }
            )
        }
    }

    /**
     * Leaves the currently open Chamber Door popup and returns the player right to their
     * current hallway position so they can keep exploring down the infinite corridor.
     */
    fun onLeaveCorridorDoor() {
        _uiState.update { state ->
            state.copy(
                inspectedDoorId = null,
                aiStatusBanner = "Stepped back into the Corridor at Z=${state.playerPos.z.toInt()}m. Keep walking to explore more doors!"
            )
        }
    }

    /**
     * Executes one of the multiple choices inside a [CorridorDoor3D], evaluating Cognitive Resonance,
     * applying Grand Project Milestone Perks, leveling Sibling Bonds, unlocking Career Medals,
     * ticking Crisis timers, and transitioning the modal directly into the Stage 2 Dramatic Aftermath Screen!
     */
    fun onCommitDoorDecision(
        door: CorridorDoor3D,
        option: ChoiceGate3D,
        switchPov: Boolean
    ) {
        val state = _uiState.value
        if (door.status == DoorStatus.RESOLVED) return

        val activeChar = state.activeCharacter
        val newGatesTraversed = state.gatesTraversed + 1

        // 1. Evaluate Cognitive Resonance Skill Check (with Sibling Bond & Prestige Bonus)
        val evaluation = CognitiveResonanceAndMilestonesEngine.evaluateSingleChoiceResonance(
            door = door,
            option = option,
            activeCharacter = activeChar,
            allCharacters = state.characters,
            projects = state.grandProjects,
            hasSiblingSynergyBuff = state.hasSiblingSynergyBuff,
            bondXpMap = state.siblingBondXp,
            prestigeLevel = state.prestigeLevel
        )

        when {
            evaluation.tier == ResonanceTier.BREAKTHROUGH -> EnclaveSoundAndHapticsManager.playBreakthroughSfx()
            door.archetype == DoorArchetype.SANCTUARY_LOUNGE -> EnclaveSoundAndHapticsManager.playSanctuarySfx()
            else -> EnclaveSoundAndHapticsManager.playResonantSfx()
        }

        val livePhase = RealTimeEngagementEngine.currentTwinSunPhase()
        val realTimeBonus = RealTimeEngagementEngine.bonusForGate(livePhase, option.squadAlignment)
        val totalProjDelta = option.projectDelta + evaluation.bonusProjectDelta + realTimeBonus
        val totalStressDelta = option.stressDelta + evaluation.bonusStressDelta
        val sanctuary100Bonus = if (CognitiveResonanceAndMilestonesEngine.isMilestoneUnlocked(state.grandProjects, "sanctuary", 100)) 3 else 0
        val table100Bonus = if (switchPov && CognitiveResonanceAndMilestonesEngine.isMilestoneUnlocked(state.grandProjects, "mycelial_concordance", 100)) 4 else 0
        val totalMoraleDelta = option.moraleDelta + evaluation.bonusMoraleDelta + sanctuary100Bonus + table100Bonus

        // 2. Update characters' stress & Vex Override status (+ Genesis Garden 100% passive -4% stress bloom)
        val garden100Bloom = if (CognitiveResonanceAndMilestonesEngine.isMilestoneUnlocked(state.grandProjects, "genesis_garden", 100)) -4 else 0
        val updatedCharacters = state.characters.map { ch ->
            if (ch.id == activeChar.id) {
                val nextStress = (ch.stressLevel + totalStressDelta + garden100Bloom).coerceIn(5, 100)
                val nextVex = if (option.isVexGate || door.archetype == DoorArchetype.SANCTUARY_LOUNGE) {
                    false
                } else {
                    nextStress >= 80
                }
                ch.copy(stressLevel = nextStress, isVexInverted = nextVex)
            } else if (garden100Bloom < 0) {
                ch.copy(stressLevel = (ch.stressLevel + garden100Bloom).coerceIn(5, 100))
            } else {
                ch
            }
        }

        // 3. Level up Sibling Bond XP for the active character's Mentor-Prodigy duo
        val activeDuoPairId = EngagementAndEndgameEngine.pairIdForCharacter(activeChar.id)
        val bondGain = when {
            door.archetype == DoorArchetype.SANCTUARY_LOUNGE -> 16
            evaluation.tier == ResonanceTier.BREAKTHROUGH -> 12
            switchPov -> 10
            else -> 7
        }
        val updatedBondXp = state.siblingBondXp.toMutableMap().apply {
            val currentXp = this[activeDuoPairId] ?: 12
            this[activeDuoPairId] = (currentXp + bondGain).coerceAtMost(120)
        }
        val updatedDuoTrack = EngagementAndEndgameEngine.getBondTrackForCharacter(activeChar.id, updatedBondXp)

        // 4. Update Grand Project progress (+ Simulacrum 100% bonus to lowest project on Breakthrough!)
        val simulacrum100 = CognitiveResonanceAndMilestonesEngine.isMilestoneUnlocked(state.grandProjects, "simulacrum", 100)
        val lowestProjectId = if (simulacrum100 && evaluation.tier == ResonanceTier.BREAKTHROUGH) {
            state.grandProjects.minByOrNull { it.progressPercent }?.id
        } else null

        val updatedProjects = state.grandProjects.map { proj ->
            var nextPct = proj.progressPercent
            if (proj.id == option.projectId) {
                nextPct += totalProjDelta
            }
            if (proj.id == lowestProjectId && proj.id != option.projectId) {
                nextPct += 4
            }
            proj.copy(progressPercent = nextPct.coerceIn(0, 100))
        }

        val newlyUnlockedMilestones = CognitiveResonanceAndMilestonesEngine.detectNewlyUnlockedMilestones(
            previousProjects = state.grandProjects,
            updatedProjects = updatedProjects
        )

        // 5. Evaluate Career & Discovery Medals
        val (updatedMedalIds, newlyUnlockedMedals) = EngagementAndEndgameEngine.evaluateAndUnlockMedals(
            currentUnlockedIds = state.unlockedMedalIds,
            gatesTraversed = newGatesTraversed,
            dayNumber = state.dayNumber,
            projects = updatedProjects,
            bondXpMap = updatedBondXp,
            prestigeLevel = state.prestigeLevel,
            resolvedDoorArchetype = door.archetype,
            achievedBreakthrough = evaluation.tier == ResonanceTier.BREAKTHROUGH,
            usedVexGate = option.isVexGate
        )

        // 6. Decrement Black Market 3-Turn fuses
        val tickedPranks = state.blackMarketQueue.map { it.copy(turnsRemaining = it.turnsRemaining - 1) }
        val newlyDetonated = tickedPranks.filter { it.turnsRemaining <= 0 }
        val remainingPranks = tickedPranks.filter { it.turnsRemaining > 0 }

        // 7. Tick down unvisited Ticking Crisis doors near/behind the player
        var crisisPenaltyMorale = 0
        var expiredCrisisTitle: String? = null
        val resolvedDoors = state.corridorDoors.map { d ->
            if (d.id == door.id) {
                d.copy(
                    status = DoorStatus.RESOLVED,
                    chosenOptionId = option.id,
                    resolvedOutcomeText = option.outcomeNarrative,
                    crisisTurnsRemaining = null
                )
            } else if (
                d.archetype == DoorArchetype.TICKING_CRISIS &&
                d.status != DoorStatus.RESOLVED &&
                d.position.z <= state.playerPos.z + 18f
            ) {
                val nextTurns = (d.crisisTurnsRemaining ?: 3) - 1
                if (nextTurns <= 0) {
                    crisisPenaltyMorale -= 8
                    expiredCrisisTitle = d.chamberTitle
                    d.copy(crisisTurnsRemaining = 1)
                } else {
                    d.copy(crisisTurnsRemaining = nextTurns)
                }
            } else {
                d
            }
        }

        val (expandedDoors, expandedSignatures) = InfiniteCorridorGenerator.ensureDoorsAhead(
            currentDoors = resolvedDoors,
            usedSignatures = state.usedScenarioSignatures,
            playerZ = state.playerPos.z + 10f,
            characters = updatedCharacters,
            dayNumber = state.dayNumber
        )

        val nextChar = if (switchPov) {
            updatedCharacters.firstOrNull { it.id == option.nextPovId } ?: activeChar
        } else {
            updatedCharacters.first { it.id == activeChar.id }
        }
        val nextBiome = if (switchPov) {
            Biome3D.fromId(option.nextBiomeId)
        } else {
            door.biomeTheme
        }

        val targetProjectName = updatedProjects.firstOrNull { it.id == option.projectId }?.name ?: "Grand Project"
        val aftermathReport = CognitiveResonanceAndMilestonesEngine.buildAftermathReport(
            door = door,
            chosenOption = option,
            evaluation = evaluation,
            executedBy = activeChar,
            relayedTo = if (switchPov) nextChar else null,
            allCharacters = updatedCharacters,
            targetProjectName = targetProjectName,
            totalProjectDelta = totalProjDelta,
            totalStressDelta = totalStressDelta,
            totalMoraleDelta = totalMoraleDelta,
            newlyUnlockedMilestones = newlyUnlockedMilestones,
            bondXpGained = bondGain,
            duoBondTitle = "${updatedDuoTrack.mentorName} & ${updatedDuoTrack.prodigyName} • ${updatedDuoTrack.rankTitle}",
            newlyUnlockedMedals = newlyUnlockedMedals.map { "${it.badgeIconText} ${it.title}" }
        )

        // 8. Record Historian Chronicle entry
        val newEntry = ChronicleEntry(
            id = "chron_${UUID.randomUUID()}",
            dayNumber = state.dayNumber,
            relayStep = state.relayStep,
            povName = activeChar.name,
            povMbti = activeChar.mbti,
            gateTitle = "${door.chamberTitle} → ${option.title} (${evaluation.resonancePercent}%)",
            scientificDomain = option.scientificDomain,
            summary = aftermathReport.outcomeNarrative
        )

        val nextMorale = (state.colonyMorale + totalMoraleDelta + crisisPenaltyMorale).coerceIn(10, 100)
        val stormbreaker100 = CognitiveResonanceAndMilestonesEngine.isMilestoneUnlocked(updatedProjects, "stormbreaker", 100)
        val maxFluxCap = if (stormbreaker100) 65 else 98
        val nextFlux = (state.magneticFlux + (if (option.isVexGate) -18 else 6)).coerceIn(15, maxFluxCap)
        val nextRelayStep = (state.relayStep % 3) + 1
        val nextSiblingSynergy = door.archetype == DoorArchetype.SANCTUARY_LOUNGE
        val allProjectsMaxedJustNow = updatedProjects.all { it.progressPercent >= 100 } &&
            !state.grandProjects.all { it.progressPercent >= 100 }

        val bannerMsg = buildString {
            append("${evaluation.tier.label} (${evaluation.resonancePercent}%) in ${door.chamberTitle}!")
            if (newlyUnlockedMilestones.isNotEmpty()) {
                append(" 🏆 Unlocked: ${newlyUnlockedMilestones.joinToString { it.title }}!")
            }
            if (newlyUnlockedMedals.isNotEmpty()) {
                append(" 🎖 Medal: ${newlyUnlockedMedals.joinToString { it.title }}!")
            }
            if (expiredCrisisTitle != null) {
                append(" ⚠ Ignored Crisis at $expiredCrisisTitle caused -8% Morale!")
            }
        }

        _uiState.update {
            it.copy(
                relayStep = nextRelayStep,
                characters = updatedCharacters,
                activePovId = nextChar.id,
                currentBiome = nextBiome,
                corridorDoors = expandedDoors,
                usedScenarioSignatures = expandedSignatures,
                inspectedDoorId = door.id,
                hasSiblingSynergyBuff = nextSiblingSynergy,
                siblingBondXp = updatedBondXp,
                unlockedMedalIds = updatedMedalIds,
                showGrandConvergenceFinale = it.showGrandConvergenceFinale || allProjectsMaxedJustNow,
                breakthroughBurstCount = if (evaluation.tier == ResonanceTier.BREAKTHROUGH) {
                    it.breakthroughBurstCount + 1
                } else {
                    it.breakthroughBurstCount
                },
                latestAftermathReport = aftermathReport,
                grandProjects = updatedProjects,
                blackMarketQueue = remainingPranks,
                detonatedPranksTonight = it.detonatedPranksTonight + newlyDetonated,
                colonyMorale = nextMorale,
                magneticFlux = nextFlux,
                gatesTraversed = newGatesTraversed,
                chronicle = listOf(newEntry) + it.chronicle,
                currentSceneTitle = "${door.chamberTitle} (${evaluation.tier.label})",
                currentNarrativeProse = aftermathReport.outcomeNarrative,
                currentScientificNote = door.scientificCheeseNote,
                lastBatonPassQuote = option.batonPassQuote,
                aiStatusBanner = bannerMsg
            )
        }

        persistCurrentState(summary = "${activeChar.name} resolved ${door.chamberTitle} (${evaluation.resonancePercent}% Resonance).")
    }

    /**
     * Synthesizes fresh AI DM options specifically for the currently open [CorridorDoor3D].
     */
    fun requestAiCustomOptionsForDoor(door: CorridorDoor3D) {
        val state = _uiState.value
        val activeChar = state.activeCharacter
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isAiGenerating = true,
                    aiStatusBanner = "AI DM synthesizing fresh choices inside ${door.chamberTitle}..."
                )
            }
            val result = aiDmService.generateProceduralScene(
                activeChar = activeChar,
                biome = door.biomeTheme,
                dayNumber = state.dayNumber,
                relayStep = state.relayStep,
                magneticFlux = state.magneticFlux,
                previousOutcomeSummary = door.roomDilemmaProse,
                allCharacters = state.characters
            )
            result.onSuccess { scene ->
                _uiState.update { current ->
                    val updatedDoors = current.corridorDoors.map { d ->
                        if (d.id == door.id) {
                            d.copy(
                                chamberSubtitle = scene.sceneTitle,
                                roomDilemmaProse = scene.narrativeProse,
                                scientificCheeseNote = scene.scientificCheeseNote,
                                options = scene.gates.mapIndexed { idx, g ->
                                    g.copy(
                                        id = "${door.id}_ai_opt_${idx}_${UUID.randomUUID()}",
                                        position = door.position
                                    )
                                }
                            )
                        } else {
                            d
                        }
                    }
                    current.copy(
                        corridorDoors = updatedDoors,
                        isAiGenerating = false,
                        aiStatusBanner = "🧠 AI Synthesized ${scene.gates.size} new choices for ${door.chamberTitle}!"
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isAiGenerating = false,
                        aiStatusBanner = "AI Notice: ${err.localizedMessage ?: "Kept canonical chamber options"}"
                    )
                }
            }
        }
    }

    fun openGrandFeastFromCorridor() {
        EnclaveSoundAndHapticsManager.playSanctuarySfx()
        _uiState.update {
            it.copy(
                inspectedDoorId = null,
                isAtGrandFeast = true
            )
        }
    }

    fun onRotateCamera(deltaYaw: Float, deltaPitch: Float) {
        _uiState.update { state ->
            var nextYaw = state.cameraYaw + deltaYaw
            while (nextYaw > PI.toFloat()) nextYaw -= (2.0 * PI).toFloat()
            while (nextYaw < -PI.toFloat()) nextYaw += (2.0 * PI).toFloat()
            val nextPitch = (state.cameraPitch + deltaPitch).coerceIn(0.06f, 0.72f)
            state.copy(
                cameraYaw = nextYaw,
                cameraPitch = nextPitch
            )
        }
    }

    fun resetCameraBehindPlayer() {
        _uiState.update {
            it.copy(
                cameraYaw = 0f,
                cameraPitch = 0.22f
            )
        }
    }

    fun onAutoWalkToGate(gate: ChoiceGate3D) {
        val state = _uiState.value
        val matchingDoor = state.corridorDoors.firstOrNull { d ->
            d.options.any { it.id == gate.id }
        } ?: state.corridorDoors.firstOrNull { it.status != DoorStatus.RESOLVED }
        if (matchingDoor != null) {
            onOpenCorridorDoor(matchingDoor)
        }
    }

    fun onWalkThroughGate(gate: ChoiceGate3D) {
        val state = _uiState.value
        val matchingDoor = state.corridorDoors.firstOrNull { d ->
            d.options.any { it.id == gate.id }
        } ?: state.corridorDoors.firstOrNull { it.status != DoorStatus.RESOLVED }
            ?: state.corridorDoors.first()
        onCommitDoorDecision(matchingDoor, gate, switchPov = true)
    }

    fun triggerAiDmProceduralScene(
        targetChar: EnclaveCharacter = _uiState.value.activeCharacter,
        biome: Biome3D = _uiState.value.currentBiome,
        dayNumber: Int = _uiState.value.dayNumber,
        relayStep: Int = _uiState.value.relayStep,
        magneticFlux: Int = _uiState.value.magneticFlux,
        previousOutcomeSummary: String = _uiState.value.currentNarrativeProse
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isAiGenerating = true,
                    aiStatusBanner = "AI DM generating a new Chamber Door ahead in the Corridor..."
                )
            }
            val result = aiDmService.generateProceduralScene(
                activeChar = targetChar,
                biome = biome,
                dayNumber = dayNumber,
                relayStep = relayStep,
                magneticFlux = magneticFlux,
                previousOutcomeSummary = previousOutcomeSummary,
                allCharacters = _uiState.value.characters
            )
            result.onSuccess { scene ->
                _uiState.update { current ->
                    val targetDoor = current.corridorDoors
                        .filter { it.status != DoorStatus.RESOLVED && it.position.z >= current.playerPos.z - 2f }
                        .minByOrNull { it.position.z }
                        ?: current.corridorDoors.last()

                    val updatedDoors = current.corridorDoors.map { d ->
                        if (d.id == targetDoor.id) {
                            d.copy(
                                chamberTitle = "Bay %02d • %s".format(d.doorNumber, scene.sceneTitle.take(28)),
                                chamberSubtitle = "AI Procedural Chamber • ${targetChar.name} (${targetChar.mbti})",
                                roomDilemmaProse = scene.narrativeProse,
                                scientificCheeseNote = scene.scientificCheeseNote,
                                options = scene.gates.mapIndexed { idx, g ->
                                    g.copy(
                                        id = "${d.id}_ai_${idx}_${UUID.randomUUID()}",
                                        position = d.position
                                    )
                                }
                            )
                        } else {
                            d
                        }
                    }
                    current.copy(
                        currentSceneTitle = scene.sceneTitle,
                        currentNarrativeProse = scene.narrativeProse,
                        currentScientificNote = scene.scientificCheeseNote,
                        corridorDoors = updatedDoors,
                        activeGates = scene.gates,
                        isAiGenerating = false,
                        aiStatusBanner = "🧠 AI Chamber Ready in Bay %02d: %s".format(
                            targetDoor.doorNumber,
                            scene.psychometricAuditNote
                        )
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isAiGenerating = false,
                        aiStatusBanner = "AI DM Notice: ${err.localizedMessage ?: "Using procedural corridor chambers"}"
                    )
                }
            }
        }
    }

    fun toggleVexOverrideForActiveCharacter() {
        val state = _uiState.value
        val active = state.activeCharacter
        val newInverted = !active.isVexInverted
        val newStress = if (newInverted) 88 else 32
        val updatedChars = state.characters.map { ch ->
            if (ch.id == active.id) ch.copy(isVexInverted = newInverted, stressLevel = newStress) else ch
        }
        val updatedActive = updatedChars.first { it.id == active.id }
        val updatedGates = EnclaveSeedData.buildGatesForCharacter(updatedActive, state.dayNumber, state.relayStep)

        val updatedDoors = state.corridorDoors.map { door ->
            if (door.status == DoorStatus.RESOLVED) {
                door
            } else {
                val baseThree = door.options.filter { !it.isVexGate }
                val vexGate = updatedGates.firstOrNull { it.isVexGate }?.copy(
                    id = "${door.id}_vex_override",
                    position = door.position
                )
                door.copy(
                    options = if (newInverted && vexGate != null) baseThree + vexGate else baseThree
                )
            }
        }

        _uiState.update {
            it.copy(
                characters = updatedChars,
                corridorDoors = updatedDoors,
                activeGates = updatedGates,
                aiStatusBanner = if (newInverted) {
                    "⚡ VEX OVERRIDE TRIGGERED: ${active.name} inverted to ${active.vexStarSign} (${active.vexOverrideTitle})! 4th Choice unlocked inside every Door!"
                } else {
                    "Vex Override cleared via Kenna's Schumann resonance recoupling."
                }
            )
        }
        persistCurrentState(summary = "Vex Override toggled for ${active.name}.")
    }

    fun switchPovCharacter(newChar: EnclaveCharacter) {
        val state = _uiState.value
        val nextBiome = when (newChar.squad.id) {
            "forge" -> Biome3D.FORGE_HALL
            "wilds" -> Biome3D.ARBORETUM
            "mind" -> Biome3D.ATLAS_CHAMBER
            else -> Biome3D.STORMBREAKER
        }
        val gates = EnclaveSeedData.buildGatesForCharacter(newChar, state.dayNumber, state.relayStep)
        _uiState.update {
            it.copy(
                activePovId = newChar.id,
                currentBiome = nextBiome,
                currentSceneTitle = "Rashomon Lens: ${newChar.name} (${newChar.mbti} • ${newChar.cognitiveStack})",
                currentNarrativeProse = "${newChar.name} (${newChar.roleTitle}) takes point in the ${nextBiome.title} Corridor at Z=${state.playerPos.z.toInt()}m. Cognitive stack ${newChar.cognitiveStack} active. ${newChar.geniusVsMundane}",
                currentScientificNote = newChar.scientificSpecialty,
                activeGates = gates,
                aiStatusBanner = "Switched POV to ${newChar.name} (${newChar.mbti} • ${newChar.cognitiveStack})!"
            )
        }
        persistCurrentState(summary = "Switched Rashomon POV lens to ${newChar.name}.")
    }

    fun queueBlackMarketPrank(
        instigatorId: String,
        targetId: String,
        title: String,
        detail: String
    ) {
        val state = _uiState.value
        val instigator = state.characters.firstOrNull { it.id == instigatorId } ?: state.activeCharacter
        val target = state.characters.firstOrNull { it.id == targetId } ?: state.characters.random()
        val victims = state.characters.filter { it.id != instigator.id && it.id != target.id }
        val accidentalVictim = victims.firstOrNull() ?: target
        val blamedNode = victims.lastOrNull() ?: instigator

        val newPrank = BlackMarketPrank(
            id = "bm_${UUID.randomUUID()}",
            instigatorId = instigator.id,
            intendedTargetId = target.id,
            accidentalVictimId = accidentalVictim.id,
            blamedNodeId = blamedNode.id,
            title = title,
            pettyDescription = "${instigator.name} targeted ${target.name}: $detail",
            blindSpotOutcome = "${target.name} didn't even notice, accidentally inconveniencing ${accidentalVictim.name}, who angrily blamed ${blamedNode.name} at the Feast!",
            turnsRemaining = 3
        )
        val (updatedMedals, _) = EngagementAndEndgameEngine.evaluateAndUnlockMedals(
            currentUnlockedIds = state.unlockedMedalIds,
            gatesTraversed = state.gatesTraversed,
            dayNumber = state.dayNumber,
            projects = state.grandProjects,
            bondXpMap = state.siblingBondXp,
            prestigeLevel = state.prestigeLevel,
            queuedOrDetonatedPrank = true
        )
        EnclaveSoundAndHapticsManager.playVaultUnlockSfx()
        _uiState.update {
            it.copy(
                blackMarketQueue = it.blackMarketQueue + newPrank,
                unlockedMedalIds = updatedMedals,
                aiStatusBanner = "Queued '${title}' on the Sibling Grid (3-Door Delay)."
            )
        }
        persistCurrentState(summary = "Queued Black Market prank: $title.")
    }

    fun requestAiGrandFeastBanter() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isAiGenerating = true) }
            val recentSummary = state.chronicle.take(3).joinToString(" | ") { "${it.povName}: ${it.gateTitle}" }
                .ifBlank { "Survived Sector 4 magnetic storm and patched the arboretum pipe." }
            val prankSummary = state.detonatedPranksTonight.firstOrNull()?.blindSpotOutcome

            val result = aiDmService.generateGrandFeastBanter(
                dayNumber = state.dayNumber,
                recentChronicleSummary = recentSummary,
                detonatedPrankSummary = prankSummary,
                allCharacters = state.characters
            )
            result.onSuccess { (dish, exchanges) ->
                _uiState.update {
                    it.copy(
                        feastDishOfTheNight = dish,
                        feastExchanges = exchanges.ifEmpty { EnclaveSeedData.defaultFeastDialogue },
                        isAiGenerating = false
                    )
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isAiGenerating = false,
                        aiStatusBanner = err.localizedMessage ?: "Grand Feast AI generation failed"
                    )
                }
            }
        }
    }

    fun executeHubVetoAndStartNextDay(veto: HubVetoOption) {
        val state = _uiState.value
        val nextDay = state.dayNumber + 1
        val updatedProjects = state.grandProjects.map { proj ->
            if (proj.id == veto.projectId) {
                proj.copy(progressPercent = (proj.progressPercent + veto.projectBoost).coerceIn(0, 100))
            } else {
                proj
            }
        }
        val hasTable50 = CognitiveResonanceAndMilestonesEngine.isMilestoneUnlocked(updatedProjects, "mycelial_concordance", 50)
        val stressRelief = if (hasTable50) 30 else 18

        // Reset Vex Overrides after a warm night at Ryn's Feast and Kenna's tea
        val restedCharacters = state.characters.map { ch ->
            ch.copy(
                stressLevel = (ch.stressLevel - stressRelief).coerceIn(10, 60),
                isVexInverted = false
            )
        }
        val nextLeadChar = restedCharacters[(nextDay - 1) % restedCharacters.size]
        val nextBiome = Biome3D.entries[(nextDay - 1) % Biome3D.entries.size]
        val nextGates = EnclaveSeedData.buildGatesForCharacter(nextLeadChar, nextDay, 1)

        val (expandedDoors, expandedSignatures) = InfiniteCorridorGenerator.ensureDoorsAhead(
            currentDoors = state.corridorDoors,
            usedSignatures = state.usedScenarioSignatures,
            playerZ = state.playerPos.z + 16f,
            characters = restedCharacters,
            dayNumber = nextDay
        )

        val (updatedMedals, _) = EngagementAndEndgameEngine.evaluateAndUnlockMedals(
            currentUnlockedIds = state.unlockedMedalIds,
            gatesTraversed = state.gatesTraversed,
            dayNumber = nextDay,
            projects = updatedProjects,
            bondXpMap = state.siblingBondXp,
            prestigeLevel = state.prestigeLevel,
            completedGrandConvergence = EngagementAndEndgameEngine.isReadyForGrandConvergence(updatedProjects)
        )
        EnclaveSoundAndHapticsManager.playResonantSfx()

        _uiState.update {
            it.copy(
                dayNumber = nextDay,
                relayStep = 1,
                characters = restedCharacters,
                activePovId = nextLeadChar.id,
                currentBiome = nextBiome,
                corridorDoors = expandedDoors,
                usedScenarioSignatures = expandedSignatures,
                inspectedDoorId = null,
                hasSiblingSynergyBuff = hasTable50,
                latestAftermathReport = null,
                grandProjects = updatedProjects,
                unlockedMedalIds = updatedMedals,
                detonatedPranksTonight = emptyList(),
                colonyMorale = (state.colonyMorale + veto.moraleBoost).coerceIn(10, 100),
                magneticFlux = (state.magneticFlux - veto.fluxReduction).coerceIn(15, 95),
                isAtGrandFeast = false,
                currentSceneTitle = "Day $nextDay • Infinite Corridor (${nextLeadChar.name})",
                currentNarrativeProse = "Following The Hub's Veto ('${veto.title}'), dawn breaks under the Red Giant and Blue Dwarf. ${nextLeadChar.name} (${nextLeadChar.roleTitle}) continues down the ${nextBiome.title} Corridor as fresh, never-before-seen Chamber Doors unlock ahead.",
                currentScientificNote = nextLeadChar.scientificSpecialty,
                activeGates = nextGates
            )
        }

        persistCurrentState(summary = "Day $nextDay begun after Hub Veto: ${veto.title}.")
        triggerAiDmProceduralScene(
            targetChar = nextLeadChar,
            biome = nextBiome,
            dayNumber = nextDay,
            relayStep = 1,
            magneticFlux = _uiState.value.magneticFlux,
            previousOutcomeSummary = "Hub Veto approved: ${veto.scientificArgument}"
        )
    }

    fun triggerGrandConvergenceFinale() {
        EnclaveSoundAndHapticsManager.playBreakthroughSfx()
        val state = _uiState.value
        val (updatedMedals, _) = EngagementAndEndgameEngine.evaluateAndUnlockMedals(
            currentUnlockedIds = state.unlockedMedalIds,
            gatesTraversed = state.gatesTraversed,
            dayNumber = state.dayNumber,
            projects = state.grandProjects,
            bondXpMap = state.siblingBondXp,
            prestigeLevel = state.prestigeLevel,
            completedGrandConvergence = true
        )
        _uiState.update {
            it.copy(
                unlockedMedalIds = updatedMedals,
                showGrandConvergenceFinale = true
            )
        }
    }

    fun dismissGrandConvergenceFinale() {
        _uiState.update {
            it.copy(showGrandConvergenceFinale = false)
        }
    }

    fun startNewGamePlusPrestige() {
        EnclaveSoundAndHapticsManager.playBreakthroughSfx()
        val state = _uiState.value
        val nextPrestige = state.prestigeLevel + 1
        val (updatedMedals, _) = EngagementAndEndgameEngine.evaluateAndUnlockMedals(
            currentUnlockedIds = state.unlockedMedalIds,
            gatesTraversed = state.gatesTraversed,
            dayNumber = state.dayNumber,
            projects = state.grandProjects,
            bondXpMap = state.siblingBondXp,
            prestigeLevel = nextPrestige,
            completedGrandConvergence = true
        )
        val freshPrestigeState = EnclaveSandboxUiState(
            siblingBondXp = state.siblingBondXp,
            unlockedMedalIds = updatedMedals,
            prestigeLevel = nextPrestige,
            isAudioAndHapticsEnabled = state.isAudioAndHapticsEnabled,
            hasSiblingSynergyBuff = true,
            cloudSyncStatus = if (isGuestMode) {
                "Guest Prestige $nextPrestige • Day 1"
            } else {
                "Cloud Prestige $nextPrestige • Day 1"
            },
            aiStatusBanner = "☀ Ascended to New Game+ (Prestige $nextPrestige)! Permanent +${nextPrestige * 5}% Resonance & all Bonds/Medals retained!"
        )
        _uiState.value = freshPrestigeState
        persistCurrentState(summary = "Ascended to New Game+ Prestige $nextPrestige.")
    }

    fun resolveEmergencyBlackout(ventCapacitors: Boolean) {
        EnclaveSoundAndHapticsManager.playVaultUnlockSfx()
        val state = _uiState.value
        if (ventCapacitors) {
            val highestProjId = state.grandProjects.maxByOrNull { it.progressPercent }?.id
            val updatedProjects = state.grandProjects.map { proj ->
                if (proj.id == highestProjId) {
                    proj.copy(progressPercent = (proj.progressPercent - 6).coerceAtLeast(10))
                } else {
                    proj
                }
            }
            _uiState.update {
                it.copy(
                    grandProjects = updatedProjects,
                    colonyMorale = 62,
                    magneticFlux = 44,
                    aiStatusBanner = "⚡ Shunted Stormbreaker Geodes! Colony Morale restored to 62% & Flux dropped to 44%."
                )
            }
        } else {
            val calmedChars = state.characters.map { ch ->
                ch.copy(stressLevel = 32, isVexInverted = false)
            }
            _uiState.update {
                it.copy(
                    characters = calmedChars,
                    colonyMorale = 54,
                    magneticFlux = 55,
                    aiStatusBanner = "🍵 Executed 7.83 Hz Enclave Recoupling! All 16 Geniuses stabilized at 32% Stress."
                )
            }
        }
        persistCurrentState(summary = "Resolved Magnetospheric Blackout Protocol.")
    }

    fun deleteAccountAndCloudData(onComplete: () -> Unit) {
        viewModelScope.launch {
            appContext?.let { ctx ->
                EnclaveSaveSerializer.deleteLocalSaveFile(ctx)
            }
            if (!isGuestMode) {
                runCatching {
                    repository.deleteSave(EnclaveRepository.PRIMARY_SAVE_ID)
                }
                runCatching {
                    com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.delete()
                }
            }
            onComplete()
        }
    }

    fun claimOfflineAutonomousBoost() {
        val state = _uiState.value
        val report = state.pendingOfflineReport ?: return
        val updatedProjects = state.grandProjects.map { proj ->
            if (proj.id == report.boostedProjectId) {
                proj.copy(progressPercent = (proj.progressPercent + report.projectBoostPercent).coerceIn(0, 100))
            } else {
                proj
            }
        }
        _uiState.update {
            it.copy(
                grandProjects = updatedProjects,
                colonyMorale = (it.colonyMorale + report.moraleRecovery).coerceIn(10, 100),
                pendingOfflineReport = null,
                lastSavedEpochMillis = System.currentTimeMillis(),
                realTimePhase = RealTimeEngagementEngine.currentTwinSunPhase(),
                aiStatusBanner = "Claimed +${report.projectBoostPercent}% offline research from ${report.leadCharacterName}!"
            )
        }
        persistCurrentState(summary = "Claimed offline autonomous research boost (+${report.projectBoostPercent}%).")
    }

    private fun persistCurrentState(summary: String) {
        val now = System.currentTimeMillis()
        _uiState.update {
            it.copy(
                lastSavedEpochMillis = now,
                realTimePhase = RealTimeEngagementEngine.currentTwinSunPhase()
            )
        }
        val state = _uiState.value
        val payload = EnclaveSaveSerializer.serializeToJsonString(state)

        appContext?.let { ctx ->
            EnclaveSaveSerializer.saveToInternalStorage(ctx, state)
        }

        if (isGuestMode) {
            _uiState.update {
                it.copy(cloudSyncStatus = "Local Saved • Day ${state.dayNumber}.${state.relayStep}")
            }
            return
        }

        viewModelScope.launch {
            val saveRes = repository.saveEnclaveState(
                dayNumber = state.dayNumber,
                relayStep = state.relayStep,
                activePovId = state.activePovId,
                currentBiome = state.currentBiome.id,
                colonyMorale = state.colonyMorale,
                magneticFlux = state.magneticFlux,
                gatesTraversed = state.gatesTraversed,
                statePayloadJson = payload,
                lastSummary = summary
            )
            _uiState.update {
                it.copy(
                    cloudSyncStatus = if (saveRes.isSuccess) {
                        "Cloud + Local • Day ${state.dayNumber}.${state.relayStep}"
                    } else {
                        "Local Saved • Day ${state.dayNumber}.${state.relayStep}"
                    }
                )
            }
        }
    }

    companion object {
        fun provideFactory(
            currentUserId: String,
            initialImportedJson: String? = null
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                EnclaveSandboxViewModel(
                    repository = EnclaveRepository(db),
                    currentUserId = currentUserId,
                    appContext = app.applicationContext,
                    initialImportedJson = initialImportedJson
                )
            }
        }
    }
}
