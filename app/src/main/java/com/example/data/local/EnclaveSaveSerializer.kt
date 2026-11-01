package com.example.data.local

import android.content.Context
import android.net.Uri
import com.example.data.model.Biome3D
import com.example.data.model.BlackMarketPrank
import com.example.data.model.ChronicleEntry
import com.example.data.model.EnclaveSeedData
import com.example.data.model.Vec3
import com.example.ui.EnclaveSandboxUiState
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object EnclaveSaveSerializer {

    const val GUEST_USER_ID = "guest_local_sandbox"
    private const val LOCAL_SAVE_FILENAME = "enclave_local_save.json"

    /**
     * Serializes the entire [EnclaveSandboxUiState] into a human-readable, portable JSON document
     * that can be saved to disk, exported via SAF, or uploaded to restore a save point.
     */
    fun serializeToJsonString(state: EnclaveSandboxUiState): String {
        val root = JSONObject()
        root.put("schemaVersion", 1)
        root.put("lastSavedEpochMillis", System.currentTimeMillis())
        root.put(
            "exportedAt",
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
        )
        root.put("dayNumber", state.dayNumber)
        root.put("relayStep", state.relayStep)
        root.put("activePovId", state.activePovId)
        root.put("currentBiomeId", state.currentBiome.id)

        // 3D Player & Camera Coordinates
        root.put(
            "playerPos",
            JSONObject().apply {
                put("x", state.playerPos.x.toDouble())
                put("y", state.playerPos.y.toDouble())
                put("z", state.playerPos.z.toDouble())
            }
        )
        root.put("playerYaw", state.playerYaw.toDouble())
        root.put("cameraYaw", state.cameraYaw.toDouble())
        root.put("cameraPitch", state.cameraPitch.toDouble())

        // Narrative & Telemetry State
        root.put("currentSceneTitle", state.currentSceneTitle)
        root.put("currentNarrativeProse", state.currentNarrativeProse)
        root.put("currentScientificNote", state.currentScientificNote)
        root.put("lastBatonPassQuote", state.lastBatonPassQuote)
        root.put("colonyMorale", state.colonyMorale)
        root.put("magneticFlux", state.magneticFlux)
        root.put("gatesTraversed", state.gatesTraversed)
        root.put("isAtGrandFeast", state.isAtGrandFeast)
        root.put("hasSiblingSynergyBuff", state.hasSiblingSynergyBuff)
        root.put("prestigeLevel", state.prestigeLevel)
        root.put("isAudioAndHapticsEnabled", state.isAudioAndHapticsEnabled)
        root.put("feastDishOfTheNight", state.feastDishOfTheNight)

        // Sibling Bond XP Map & Career Achievement Medals
        val bondsObj = JSONObject()
        state.siblingBondXp.forEach { (pairId, xp) -> bondsObj.put(pairId, xp) }
        root.put("siblingBondXp", bondsObj)

        val medalsArr = JSONArray()
        state.unlockedMedalIds.forEach { medalsArr.put(it) }
        root.put("unlockedMedalIds", medalsArr)

        // All 16 Geniuses' Psychometric Stress & Vex Inversion States
        val charsArray = JSONArray()
        state.characters.forEach { ch ->
            charsArray.put(
                JSONObject().apply {
                    put("id", ch.id)
                    put("name", ch.name)
                    put("stress", ch.stressLevel)
                    put("vex", ch.isVexInverted)
                }
            )
        }
        root.put("chars", charsArray)

        // All 8 Grand Projects' Progress
        val projectsArray = JSONArray()
        state.grandProjects.forEach { proj ->
            projectsArray.put(
                JSONObject().apply {
                    put("id", proj.id)
                    put("progressPercent", proj.progressPercent)
                }
            )
        }
        root.put("grandProjects", projectsArray)

        // Black Market (Sibling Grid) Queue
        val pranksArray = JSONArray()
        state.blackMarketQueue.forEach { prank ->
            pranksArray.put(serializePrank(prank))
        }
        root.put("blackMarketQueue", pranksArray)

        val detonatedArray = JSONArray()
        state.detonatedPranksTonight.forEach { prank ->
            detonatedArray.put(serializePrank(prank))
        }
        root.put("detonatedPranksTonight", detonatedArray)

        // Historian Chronicle Entries
        val chronicleArray = JSONArray()
        state.chronicle.forEach { entry ->
            chronicleArray.put(
                JSONObject().apply {
                    put("id", entry.id)
                    put("dayNumber", entry.dayNumber)
                    put("relayStep", entry.relayStep)
                    put("povName", entry.povName)
                    put("povMbti", entry.povMbti)
                    put("gateTitle", entry.gateTitle)
                    put("scientificDomain", entry.scientificDomain)
                    put("summary", entry.summary)
                }
            )
        }
        root.put("chronicle", chronicleArray)

        // Infinite Corridor Used Scenario Signatures & Resolved Door States
        val sigsArray = JSONArray()
        state.usedScenarioSignatures.forEach { sigsArray.put(it) }
        root.put("usedScenarioSignatures", sigsArray)

        val resolvedDoorsArray = JSONArray()
        state.corridorDoors.forEach { door ->
            resolvedDoorsArray.put(
                JSONObject().apply {
                    put("id", door.id)
                    put("doorNumber", door.doorNumber)
                    put("scenarioSignature", door.scenarioSignature)
                    put("status", door.status.name)
                    put("chosenOptionId", door.chosenOptionId ?: "")
                    put("resolvedOutcomeText", door.resolvedOutcomeText ?: "")
                    put("isVaultUnlockedByOverride", door.isVaultUnlockedByOverride)
                    put("crisisTurnsRemaining", door.crisisTurnsRemaining ?: -1)
                }
            )
        }
        root.put("corridorDoorStates", resolvedDoorsArray)

        return root.toString(2)
    }

    /**
     * Deserializes a JSON save document string back into a complete [EnclaveSandboxUiState].
     */
    fun deserializeFromJsonString(
        jsonString: String,
        currentState: EnclaveSandboxUiState = EnclaveSandboxUiState(),
        sourceLabel: String = "Restored from Local Save Document"
    ): Result<EnclaveSandboxUiState> = runCatching {
        val root = JSONObject(jsonString.trim())
        val day = root.optInt("dayNumber", currentState.dayNumber).coerceAtLeast(1)
        val step = root.optInt("relayStep", currentState.relayStep).coerceIn(1, 3)
        val povId = root.optString("activePovId", currentState.activePovId)
        val biomeId = root.optString("currentBiomeId", currentState.currentBiome.id)
        val restoredBiome = Biome3D.fromId(biomeId)

        // Restore 16 Characters
        val charMap = mutableMapOf<String, Pair<Int, Boolean>>()
        val cArr = root.optJSONArray("chars")
        if (cArr != null) {
            for (i in 0 until cArr.length()) {
                val obj = cArr.getJSONObject(i)
                charMap[obj.getString("id")] =
                    obj.optInt("stress", 28).coerceIn(0, 100) to obj.optBoolean("vex", false)
            }
        }
        val restoredCharacters = EnclaveSeedData.initialCharacters.map { ch ->
            val saved = charMap[ch.id]
            if (saved != null) {
                ch.copy(stressLevel = saved.first, isVexInverted = saved.second)
            } else {
                ch
            }
        }

        // Restore 8 Grand Projects
        val projMap = mutableMapOf<String, Int>()
        val pArr = root.optJSONArray("grandProjects")
        if (pArr != null) {
            for (i in 0 until pArr.length()) {
                val obj = pArr.getJSONObject(i)
                projMap[obj.getString("id")] = obj.optInt("progressPercent", 0).coerceIn(0, 100)
            }
        }
        val restoredProjects = EnclaveSeedData.initialGrandProjects.map { proj ->
            val progress = projMap[proj.id]
            if (progress != null) proj.copy(progressPercent = progress) else proj
        }

        // Restore Black Market Pranks
        val restoredQueue = root.optJSONArray("blackMarketQueue")?.let { arr ->
            buildList {
                for (i in 0 until arr.length()) {
                    add(deserializePrank(arr.getJSONObject(i)))
                }
            }
        } ?: currentState.blackMarketQueue

        val restoredDetonated = root.optJSONArray("detonatedPranksTonight")?.let { arr ->
            buildList {
                for (i in 0 until arr.length()) {
                    add(deserializePrank(arr.getJSONObject(i)))
                }
            }
        } ?: currentState.detonatedPranksTonight

        // Restore Chronicle
        val restoredChronicle = root.optJSONArray("chronicle")?.let { arr ->
            buildList {
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    add(
                        ChronicleEntry(
                            id = obj.optString("id", "chron_$i"),
                            dayNumber = obj.optInt("dayNumber", 1),
                            relayStep = obj.optInt("relayStep", 1),
                            povName = obj.optString("povName", "Silas"),
                            povMbti = obj.optString("povMbti", "INTJ"),
                            gateTitle = obj.optString("gateTitle", "Choice Gate"),
                            scientificDomain = obj.optString("scientificDomain", "Materials Science"),
                            summary = obj.optString("summary", "")
                        )
                    )
                }
            }
        } ?: currentState.chronicle

        // Restore 3D Player Position (Unbounded +Z for Infinite Corridor!)
        val posObj = root.optJSONObject("playerPos")
        val restoredPos = if (posObj != null) {
            Vec3(
                x = posObj.optDouble("x", 0.0).toFloat().coerceIn(-18f, 18f),
                y = posObj.optDouble("y", 0.0).toFloat(),
                z = posObj.optDouble("z", 0.0).toFloat().coerceAtLeast(-4f)
            )
        } else {
            Vec3(0f, 0f, 0f)
        }

        // Restore Used Scenario Signatures & Infinite Corridor Doors up to restoredPos.z
        val restoredSigs = mutableSetOf<String>()
        root.optJSONArray("usedScenarioSignatures")?.let { arr ->
            for (i in 0 until arr.length()) {
                restoredSigs.add(arr.getString(i))
            }
        }
        val (ensuredDoors, ensuredSigs) = com.example.data.model.InfiniteCorridorGenerator.ensureDoorsAhead(
            currentDoors = currentState.corridorDoors,
            usedSignatures = if (restoredSigs.isEmpty()) currentState.usedScenarioSignatures else restoredSigs,
            playerZ = restoredPos.z,
            characters = restoredCharacters,
            dayNumber = day
        )

        data class SavedDoorPatch(
            val status: com.example.data.model.DoorStatus,
            val chosenOptionId: String?,
            val resolvedOutcomeText: String?,
            val isVaultUnlockedByOverride: Boolean,
            val crisisTurnsRemaining: Int?
        )

        val doorStateMap = mutableMapOf<String, SavedDoorPatch>()
        root.optJSONArray("corridorDoorStates")?.let { arr ->
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val id = obj.optString("id")
                val status = runCatching {
                    com.example.data.model.DoorStatus.valueOf(obj.optString("status", "UNEXPLORED"))
                }.getOrDefault(com.example.data.model.DoorStatus.UNEXPLORED)
                val chosen = obj.optString("chosenOptionId").takeIf { it.isNotBlank() }
                val outcome = obj.optString("resolvedOutcomeText").takeIf { it.isNotBlank() }
                val vaultOverride = obj.optBoolean("isVaultUnlockedByOverride", false)
                val crisisTurns = obj.optInt("crisisTurnsRemaining", -1).takeIf { it >= 0 }
                doorStateMap[id] = SavedDoorPatch(status, chosen, outcome, vaultOverride, crisisTurns)
            }
        }

        val restoredCorridorDoors = ensuredDoors.map { d ->
            val savedDoor = doorStateMap[d.id]
            if (savedDoor != null) {
                d.copy(
                    status = savedDoor.status,
                    chosenOptionId = savedDoor.chosenOptionId,
                    resolvedOutcomeText = savedDoor.resolvedOutcomeText,
                    isVaultUnlockedByOverride = savedDoor.isVaultUnlockedByOverride,
                    crisisTurnsRemaining = savedDoor.crisisTurnsRemaining ?: d.crisisTurnsRemaining
                )
            } else {
                d
            }
        }

        val activeChar = restoredCharacters.firstOrNull { it.id == povId } ?: restoredCharacters.first()
        val rebuiltGates = EnclaveSeedData.buildGatesForCharacter(activeChar, day, step)
        val savedEpoch = root.optLong("lastSavedEpochMillis", System.currentTimeMillis())

        val restoredBonds = com.example.data.model.EngagementAndEndgameEngine.defaultBondXpMap().toMutableMap()
        root.optJSONObject("siblingBondXp")?.let { bObj ->
            val keys = bObj.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                restoredBonds[k] = bObj.optInt(k, 12).coerceIn(0, 150)
            }
        }

        val restoredMedals = mutableSetOf<String>()
        root.optJSONArray("unlockedMedalIds")?.let { mArr ->
            for (i in 0 until mArr.length()) {
                restoredMedals.add(mArr.getString(i))
            }
        }

        val baseRestored = currentState.copy(
            dayNumber = day,
            relayStep = step,
            characters = restoredCharacters,
            activePovId = activeChar.id,
            currentBiome = restoredBiome,
            playerPos = restoredPos,
            playerYaw = root.optDouble("playerYaw", 0.0).toFloat(),
            cameraYaw = root.optDouble("cameraYaw", 0.0).toFloat(),
            cameraPitch = root.optDouble("cameraPitch", 0.22).toFloat().coerceIn(0.06f, 0.72f),
            currentSceneTitle = root.optString("currentSceneTitle", currentState.currentSceneTitle),
            currentNarrativeProse = root.optString("currentNarrativeProse", currentState.currentNarrativeProse),
            currentScientificNote = root.optString("currentScientificNote", activeChar.scientificSpecialty),
            lastBatonPassQuote = root.optString("lastBatonPassQuote", currentState.lastBatonPassQuote),
            corridorDoors = restoredCorridorDoors,
            usedScenarioSignatures = ensuredSigs,
            inspectedDoorId = null,
            hasSiblingSynergyBuff = root.optBoolean("hasSiblingSynergyBuff", false),
            siblingBondXp = restoredBonds,
            unlockedMedalIds = restoredMedals,
            prestigeLevel = root.optInt("prestigeLevel", currentState.prestigeLevel).coerceAtLeast(0),
            isAudioAndHapticsEnabled = root.optBoolean("isAudioAndHapticsEnabled", currentState.isAudioAndHapticsEnabled),
            latestAftermathReport = null,
            activeGates = rebuiltGates,
            grandProjects = restoredProjects,
            blackMarketQueue = restoredQueue,
            detonatedPranksTonight = restoredDetonated,
            colonyMorale = root.optInt("colonyMorale", currentState.colonyMorale).coerceIn(10, 100),
            magneticFlux = root.optInt("magneticFlux", currentState.magneticFlux).coerceIn(10, 100),
            gatesTraversed = root.optInt("gatesTraversed", currentState.gatesTraversed).coerceAtLeast(0),
            isAtGrandFeast = root.optBoolean("isAtGrandFeast", false),
            feastDishOfTheNight = root.optString("feastDishOfTheNight", currentState.feastDishOfTheNight),
            chronicle = restoredChronicle,
            cloudSyncStatus = sourceLabel,
            lastSavedEpochMillis = savedEpoch,
            realTimePhase = RealTimeEngagementEngine.currentTwinSunPhase()
        )
        val offlineReport = RealTimeEngagementEngine.computeOfflineSimulation(
            lastSavedMillis = savedEpoch,
            nowMillis = System.currentTimeMillis(),
            state = baseRestored
        )
        baseRestored.copy(
            pendingOfflineReport = offlineReport,
            aiStatusBanner = if (offlineReport != null) {
                "⏱ While You Were Away (${offlineReport.elapsedMinutes}m): ${offlineReport.leadCharacterName} advanced ${offlineReport.boostedProjectId}!"
            } else {
                "Save Point Restored: Day $day • Corridor Z=${restoredPos.z.toInt()}m (${activeChar.name})"
            }
        )
    }

    fun saveToInternalStorage(context: Context, state: EnclaveSandboxUiState): Result<File> = runCatching {
        val file = File(context.filesDir, LOCAL_SAVE_FILENAME)
        file.writeText(serializeToJsonString(state), Charsets.UTF_8)
        file
    }

    fun loadFromInternalStorage(
        context: Context,
        currentState: EnclaveSandboxUiState = EnclaveSandboxUiState()
    ): Result<EnclaveSandboxUiState>? {
        val file = File(context.filesDir, LOCAL_SAVE_FILENAME)
        if (!file.exists()) return null
        return runCatching {
            val content = file.readText(Charsets.UTF_8)
            deserializeFromJsonString(
                jsonString = content,
                currentState = currentState,
                sourceLabel = "Local Save Active"
            ).getOrThrow()
        }
    }

    fun hasInternalSave(context: Context): Boolean {
        return File(context.filesDir, LOCAL_SAVE_FILENAME).exists()
    }

    fun deleteLocalSaveFile(context: Context): Boolean {
        val file = File(context.filesDir, LOCAL_SAVE_FILENAME)
        return if (file.exists()) file.delete() else true
    }

    fun exportToUri(context: Context, uri: Uri, state: EnclaveSandboxUiState): Result<Unit> = runCatching {
        val json = serializeToJsonString(state)
        context.contentResolver.openOutputStream(uri)?.use { out ->
            out.write(json.toByteArray(Charsets.UTF_8))
            out.flush()
        } ?: error("Unable to open output stream for selected document.")
    }

    fun importFromUri(
        context: Context,
        uri: Uri,
        currentState: EnclaveSandboxUiState = EnclaveSandboxUiState()
    ): Result<EnclaveSandboxUiState> = runCatching {
        val json = context.contentResolver.openInputStream(uri)?.use { input ->
            input.bufferedReader(Charsets.UTF_8).readText()
        } ?: error("Unable to read selected save document.")
        deserializeFromJsonString(
            jsonString = json,
            currentState = currentState,
            sourceLabel = "Restored from Uploaded Save File"
        ).getOrThrow()
    }

    private fun serializePrank(prank: BlackMarketPrank): JSONObject = JSONObject().apply {
        put("id", prank.id)
        put("instigatorId", prank.instigatorId)
        put("intendedTargetId", prank.intendedTargetId)
        put("accidentalVictimId", prank.accidentalVictimId)
        put("blamedNodeId", prank.blamedNodeId)
        put("title", prank.title)
        put("pettyDescription", prank.pettyDescription)
        put("blindSpotOutcome", prank.blindSpotOutcome)
        put("turnsRemaining", prank.turnsRemaining)
    }

    private fun deserializePrank(obj: JSONObject): BlackMarketPrank = BlackMarketPrank(
        id = obj.optString("id", "bm_restored"),
        instigatorId = obj.optString("instigatorId", "kaio"),
        intendedTargetId = obj.optString("intendedTargetId", "silas"),
        accidentalVictimId = obj.optString("accidentalVictimId", "leo"),
        blamedNodeId = obj.optString("blamedNodeId", "zara"),
        title = obj.optString("title", "Sibling Grid Favor"),
        pettyDescription = obj.optString("pettyDescription", ""),
        blindSpotOutcome = obj.optString("blindSpotOutcome", ""),
        turnsRemaining = obj.optInt("turnsRemaining", 2)
    )
}
