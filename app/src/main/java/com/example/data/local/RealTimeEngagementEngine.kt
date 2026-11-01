package com.example.data.local

import com.example.data.model.Squad
import com.example.ui.EnclaveSandboxUiState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class TwinSunRealTimePhase(
    val id: String,
    val title: String,
    val subtitle: String,
    val timeWindowLabel: String,
    val scientificWeatherNote: String,
    val favoredSquad: Squad,
    val skyTintHex: Long
) {
    RED_GIANT_DAWN(
        id = "red_giant_dawn",
        title = "Red Giant Ascent",
        subtitle = "Morning Orbital Window • Thermal Drafts Powering The Forge",
        timeWindowLabel = "05:00 – 11:59 Local",
        scientificWeatherNote = "Long infrared wavelengths from the Red Giant heat the ironwood canopy, driving convective updrafts that boost metallurgical smelting efficiency.",
        favoredSquad = Squad.FORGE,
        skyTintHex = 0xFFFF9E44
    ),
    TWIN_SUN_ZENITH(
        id = "twin_sun_zenith",
        title = "Binary Noon Cross-Polarity",
        subtitle = "Midday Orbital Window • High-UV Bioluminescence Peak",
        timeWindowLabel = "12:00 – 16:59 Local",
        scientificWeatherNote = "Both suns align at zenith. High ultraviolet photon flux triggers rapid luciferin photosynthesis and mycorrhizal signaling across The Wilds.",
        favoredSquad = Squad.WILDS,
        skyTintHex = 0xFF00E5C3
    ),
    BLUE_DWARF_DUSK(
        id = "blue_dwarf_dusk",
        title = "Blue Dwarf Twilight",
        subtitle = "Evening Orbital Window • Ryn's Obsessive Table & Hub Sync",
        timeWindowLabel = "17:00 – 20:59 Local",
        scientificWeatherNote = "As the Red Giant dips below the basalt ridge, Blue Dwarf actinic light settles over the valley while Ryn's cauldrons reach 72°C and Augusta syncs the Living Atlas.",
        favoredSquad = Squad.HUB,
        skyTintHex = 0xFFD4AF37
    ),
    AURORAL_NIGHT_WATCH(
        id = "auroral_night_watch",
        title = "Auroral Night Watch",
        subtitle = "Nocturnal Orbital Window • Schumann 7.83 Hz Dream-Sync",
        timeWindowLabel = "21:00 – 04:59 Local",
        scientificWeatherNote = "Magnetospheric ribbons shimmer overhead. Kenna, Isla, and Orion run nocturnal cymatic and 5D quartz consolidation while the Enclave rests.",
        favoredSquad = Squad.MIND,
        skyTintHex = 0xFFB388FF
    )
}

data class DailyDirective(
    val id: String,
    val title: String,
    val description: String,
    val targetCount: Int,
    val currentCount: Int,
    val rewardProjectBoost: Int,
    val isCompleted: Boolean = currentCount >= targetCount
)

data class OfflineAutonomousReport(
    val elapsedMinutes: Long,
    val leadCharacterName: String,
    val boostedProjectId: String,
    val projectBoostPercent: Int,
    val moraleRecovery: Int,
    val narrativeDispatch: String,
    val rynKitchenNote: String
)

/**
 * Real-Time Kepler-452b Chronometer, Offline "While You Were Away" Autonomous Simulation,
 * and Daily Engagement / Retention Engine.
 */
object RealTimeEngagementEngine {

    fun currentTwinSunPhase(nowMillis: Long = System.currentTimeMillis()): TwinSunRealTimePhase {
        val cal = Calendar.getInstance().apply { timeInMillis = nowMillis }
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> TwinSunRealTimePhase.RED_GIANT_DAWN
            in 12..16 -> TwinSunRealTimePhase.TWIN_SUN_ZENITH
            in 17..20 -> TwinSunRealTimePhase.BLUE_DWARF_DUSK
            else -> TwinSunRealTimePhase.AURORAL_NIGHT_WATCH
        }
    }

    fun bonusForGate(phase: TwinSunRealTimePhase, gateSquad: Squad): Int {
        return if (phase.favoredSquad == gateSquad) 3 else 0
    }

    fun getCurrentDateKey(nowMillis: Long = System.currentTimeMillis()): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(nowMillis))
    }

    /**
     * Computes what the 16 geniuses autonomously accomplished while the player was away
     * whenever 5+ real-world minutes have elapsed since [lastSavedMillis].
     */
    fun computeOfflineSimulation(
        lastSavedMillis: Long,
        nowMillis: Long = System.currentTimeMillis(),
        state: EnclaveSandboxUiState
    ): OfflineAutonomousReport? {
        if (lastSavedMillis <= 0L) return null
        val elapsedMinutes = (nowMillis - lastSavedMillis) / 60_000L
        if (elapsedMinutes < 5L) return null

        val phase = currentTwinSunPhase(nowMillis)
        val projectBoost = when {
            elapsedMinutes >= 360 -> 8
            elapsedMinutes >= 120 -> 6
            elapsedMinutes >= 30 -> 4
            else -> 2
        }
        val moraleRecovery = when {
            elapsedMinutes >= 120 -> 6
            elapsedMinutes >= 30 -> 4
            else -> 2
        }

        val targetProject = state.grandProjects.firstOrNull {
            it.squad == phase.favoredSquad && it.progressPercent < 100
        } ?: state.grandProjects.minByOrNull { it.progressPercent }
            ?: state.grandProjects.first()

        val leadChar = state.characters.firstOrNull { it.grandProjectId == targetProject.id }
            ?: state.activeCharacter
        val partnerChar = state.characters.firstOrNull { it.id == leadChar.partnerNodeId }
            ?: state.characters.last()

        return OfflineAutonomousReport(
            elapsedMinutes = elapsedMinutes,
            leadCharacterName = "${leadChar.name} & ${partnerChar.name}",
            boostedProjectId = targetProject.id,
            projectBoostPercent = projectBoost,
            moraleRecovery = moraleRecovery,
            narrativeDispatch = "During ${phase.title} (${elapsedMinutes}m real-time away), ${leadChar.name} (${leadChar.mbti}) and ${partnerChar.name} (${partnerChar.mbti}) ran an autonomous shift on '${targetProject.name}', preparing a +$projectBoost% breakthrough.",
            rynKitchenNote = "Ryn kept the methionalmushroom stock simmering at 72°C (40 RPM) and grounded every copper ladle before you returned."
        )
    }

    /**
     * Generates the 3 Dynamic Daily Enclave Directives based on current day and progress to drive daily engagement.
     */
    fun buildDailyDirectives(state: EnclaveSandboxUiState): List<DailyDirective> {
        val completedFeasts = (state.dayNumber - 1).coerceAtLeast(0)
        val unlockedMilestonesCount = state.unlockedMilestonesCount
        val resolvedVaultsOrCrises = state.corridorDoors.count {
            it.status == com.example.data.model.DoorStatus.RESOLVED &&
                (it.archetype == com.example.data.model.DoorArchetype.COGNITIVE_VAULT ||
                    it.archetype == com.example.data.model.DoorArchetype.TICKING_CRISIS)
        }
        return listOf(
            DailyDirective(
                id = "dir_gates",
                title = "Corridor Expedition (3 Chamber Bays)",
                description = "Open and resolve at least 3 Chamber Doors along the Infinite Corridor.",
                targetCount = 3,
                currentCount = state.gatesTraversed.coerceAtMost(3),
                rewardProjectBoost = 5
            ),
            DailyDirective(
                id = "dir_vault_or_crisis",
                title = "Defuse a Crisis or Crack a Cognitive Vault",
                description = "Resolve any ⚠ Ticking Crisis Bulkhead or 🔒 Cognitive-Locked Vault along the hallway.",
                targetCount = 1,
                currentCount = resolvedVaultsOrCrises.coerceAtMost(1),
                rewardProjectBoost = 8
            ),
            DailyDirective(
                id = "dir_milestone",
                title = "Unlock 6+ Grand Project Milestone Perks",
                description = "Advance Grand Projects across 50% and 100% thresholds or dock at Ryn's Feast.",
                targetCount = 6,
                currentCount = (unlockedMilestonesCount + completedFeasts).coerceAtMost(6),
                rewardProjectBoost = 10
            )
        )
    }

    /**
     * Calculates overall Enclave Readiness (0..100%) across all 8 Grand Projects toward
     * surviving the Season 1 Finale (The Ediacaran Bifurcation Megastorm).
     */
    fun computeOverallEnclaveReadiness(state: EnclaveSandboxUiState): Int {
        if (state.grandProjects.isEmpty()) return 0
        return (state.grandProjects.sumOf { it.progressPercent } / state.grandProjects.size).coerceIn(0, 100)
    }
}
