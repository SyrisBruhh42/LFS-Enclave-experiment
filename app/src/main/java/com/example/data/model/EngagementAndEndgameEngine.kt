package com.example.data.model

data class SiblingBondTrack(
    val pairId: String,
    val mentorId: String,
    val mentorName: String,
    val prodigyId: String,
    val prodigyName: String,
    val squad: Squad,
    val grandProjectId: String,
    val pairTitle: String,
    val bondXp: Int,
    val rank: Int, // 1..5
    val rankTitle: String,
    val resonanceBonusPercent: Int,
    val nextRankXp: Int,
    val liveRelationshipQuote: String
)

data class EnclaveAchievementMedal(
    val id: String,
    val title: String,
    val description: String,
    val badgeIconText: String,
    val accentColorHex: Long,
    val isUnlocked: Boolean
)

/**
 * Engine governing:
 * 1. Sibling Bond & Mentor-Prodigy Affinity Levels (Ranks I–V) + Duo Resonance Perks + Live Quotes.
 * 2. 12 Career Achievement & Discovery Medals persisted across New Game+ Prestige runs.
 * 3. Endgame Climax (The Grand Convergence) & Emergency Blackout Protocol evaluation.
 */
object EngagementAndEndgameEngine {

    private data class CanonicalDuoDefinition(
        val pairId: String,
        val mentorId: String,
        val mentorName: String,
        val prodigyId: String,
        val prodigyName: String,
        val squad: Squad,
        val grandProjectId: String,
        val pairTitle: String,
        val rankQuotes: List<String>
    )

    private val canonicalDuos: List<CanonicalDuoDefinition> = listOf(
        CanonicalDuoDefinition(
            pairId = "duo_first_forge",
            mentorId = "silas",
            mentorName = "Silas",
            prodigyId = "leo",
            prodigyName = "Leo",
            squad = Squad.FORGE,
            grandProjectId = "first_forge",
            pairTitle = "The Anvil & The Spark",
            rankQuotes = listOf(
                "\"Stop improvising with my torque wrenches, Leo—Wait. Why did that actually seal the pipe?\"",
                "\"Silas measures ten times; I weld once while the iron is singing. Together, nothing leaks.\"",
                "\"Don't tell the kid, but his ferrofluid collar saved Sector 2's main manifold today.\"",
                "\"When Silas calls out a tolerance and I hit it blindfolded, the whole Forge hums in phase.\"",
                "\"Father-and-son of the First Forge: five-element alloys and zero-latency trust.\""
            )
        ),
        CanonicalDuoDefinition(
            pairId = "duo_stormbreaker",
            mentorId = "hector",
            mentorName = "Hector",
            prodigyId = "jane",
            prodigyName = "Jane",
            squad = Squad.FORGE,
            grandProjectId = "stormbreaker",
            pairTitle = "Thunder & Chaos Compiler",
            rankQuotes = listOf(
                "\"Jane, stay behind the Faraday line! ...Did you just rewrite my targeting loop in twelve lines?\"",
                "\"Hector brings the terawatt courage; I bring the Lorenz strange attractors.\"",
                "\"She's eleven years old and already predicts step-leader bifurcations faster than my scopes.\"",
                "\"When the purple lightning hits the spire, Hector doesn't flinch because he knows my code holds.\"",
                "\"Twin conductors of the Stormbreaker: turning planetary megastorms into pure Enclave power.\""
            )
        ),
        CanonicalDuoDefinition(
            pairId = "duo_sanctuary",
            mentorId = "kaio",
            mentorName = "Kaio",
            prodigyId = "zara",
            prodigyName = "Zara",
            squad = Squad.WILDS,
            grandProjectId = "sanctuary",
            pairTitle = "Bio-Photon & Perimeter Blade",
            rankQuotes = listOf(
                "\"Zara wants to tranquilize everything that moves; I'm trying to translate its prime-number light!\"",
                "\"Kaio talks to the glowing canopy; I make sure nothing with chitin sneaks up behind him.\"",
                "\"She held her fire for six minutes while I finished the luciferin handshake. That took real trust.\"",
                "\"We move through the Ediacaran gorge like a single organism—sight and reflex in lockstep.\"",
                "\"Guardians of The Sanctuary: human and Keplerian biology living in unbreakable symbiosis.\""
            )
        ),
        CanonicalDuoDefinition(
            pairId = "duo_genesis_garden",
            mentorId = "willow",
            mentorName = "Willow",
            prodigyId = "terra",
            prodigyName = "Terra",
            squad = Squad.WILDS,
            grandProjectId = "genesis_garden",
            pairTitle = "Root Whisperer & Canopy Climber",
            rankQuotes = listOf(
                "\"Terra climbs sixty feet up the ironwood vines before I've even finished testing the soil pH.\"",
                "\"Willow hears the fungal network's warning an hour before the barometer drops!\"",
                "\"Her vulcanizing sap and my mycorrhizal cultures just healed three bays in a single afternoon.\"",
                "\"Every root and leaf in the Genesis Garden knows our footsteps by heart.\"",
                "\"Heart of the Wilds: breathing nitrogen, nectar, and calm into every lung in Enclave 16.\""
            )
        ),
        CanonicalDuoDefinition(
            pairId = "duo_lyceum_mind",
            mentorId = "kenna",
            mentorName = "Kenna",
            prodigyId = "elias",
            prodigyName = "Elias",
            squad = Squad.MIND,
            grandProjectId = "lyceum_mind",
            pairTitle = "Schumann Healer & Bio-Linguist",
            rankQuotes = listOf(
                "\"Elias speaks twelve languages and still forgets to sleep unless I brew valerian-moss tea.\"",
                "\"Kenna maps human neuro-harmonics the exact way I map the forest's calcium-ion syntax trees.\"",
                "\"Together we caught a 0300h Vex inversion before a single breaker tripped.\"",
                "\"Our 7.83 Hz Helmholtz coils and bio-semiotic lexicons have become the Enclave's quiet shield.\"",
                "\"Architects of the Lyceum: sixteen brilliant, volatile minds held together in lucid harmony.\""
            )
        ),
        CanonicalDuoDefinition(
            pairId = "duo_simulacrum",
            mentorId = "isla",
            mentorName = "Isla",
            prodigyId = "orion",
            prodigyName = "Orion",
            squad = Squad.MIND,
            grandProjectId = "simulacrum",
            pairTitle = "5D Quartz & Cymatic Bow",
            rankQuotes = listOf(
                "\"Orion's acoustic bass notes vibrate my femtosecond laser table—until we tuned them into phase!\"",
                "\"Isla etches light for a billion years; I sculpt standing waves for the present moment.\"",
                "\"Using Orion's Chladni nodes to damp thermal jitter doubled our 5D quartz write speed.\"",
                "\"Light and sound woven into fused silica: the Simulacrum remembers every triumph we share.\"",
                "\"Keepers of Eternity: an indestructible archive of human and Keplerian genius.\""
            )
        ),
        CanonicalDuoDefinition(
            pairId = "duo_living_atlas",
            mentorId = "augusta",
            mentorName = "Augusta",
            prodigyId = "cassia",
            prodigyName = "Cassia",
            squad = Squad.HUB,
            grandProjectId = "living_atlas",
            pairTitle = "Grand Cartographer & Seismic Ear",
            rankQuotes = listOf(
                "\"Cassia comes back from the deep aquifer covered in basalt mud, handing me flawless P-wave maps.\"",
                "\"Augusta sees the whole planet from orbit; I feel its tectonic heartbeat through the stone.\"",
                "\"When my seismic thumper feeds directly into her holo-table, Sector 4 has nowhere to hide.\"",
                "\"Top-down strategy meets bottom-up crystallography—our Living Atlas predicts every magnetic shift.\"",
                "\"Eyes and Ears of Kepler-452b: the entire valley mapped down to the last quartz vein.\""
            )
        ),
        CanonicalDuoDefinition(
            pairId = "duo_hearth_concordance",
            mentorId = "ryn",
            mentorName = "Ryn",
            prodigyId = "vex",
            prodigyName = "Vex",
            squad = Squad.HUB,
            grandProjectId = "mycelial_concordance",
            pairTitle = "72°C Hearth & Twin-Sun Maverick",
            rankQuotes = listOf(
                "\"Vex keeps trying to stir my 40-RPM broth counterclockwise just to test his chaos horoscopes!\"",
                "\"Ryn pretends my binary-star charts are nonsense, yet he always plates dinner right at solar zenith.\"",
                "\"A grounded copper spoon and a wink across the feast table—nobody diffuses a PhD feud faster.\"",
                "\"Between Ryn's Maillard alchemy and Vex's cross-polarity intuition, the Hub never breaks.\"",
                "\"Soul of the Enclave Table: turning sixteen stubborn geniuses into an unbreakable family.\""
            )
        )
    )

    fun defaultBondXpMap(): Map<String, Int> = canonicalDuos.associate { it.pairId to 12 }

    fun pairIdForCharacter(charId: String): String {
        return canonicalDuos.firstOrNull {
            it.mentorId == charId || it.prodigyId == charId
        }?.pairId ?: "duo_first_forge"
    }

    fun buildAllSiblingBondTracks(bondXpMap: Map<String, Int>): List<SiblingBondTrack> {
        return canonicalDuos.map { def ->
            val xp = (bondXpMap[def.pairId] ?: 12).coerceAtLeast(0)
            val (rank, rankTitle, bonusPct, nextXp) = when {
                xp >= 100 -> Quadruple(5, "Rank V • Apex Polymath Bond", 14, 100)
                xp >= 70 -> Quadruple(4, "Rank IV • Twin-Star Entanglement", 10, 100)
                xp >= 45 -> Quadruple(3, "Rank III • Found-Family Sync", 7, 70)
                xp >= 25 -> Quadruple(2, "Rank II • Calibrated Duo", 4, 45)
                else -> Quadruple(1, "Rank I • Guarded Colleagues", 2, 25)
            }
            val quote = def.rankQuotes.getOrElse(rank - 1) { def.rankQuotes.last() }
            SiblingBondTrack(
                pairId = def.pairId,
                mentorId = def.mentorId,
                mentorName = def.mentorName,
                prodigyId = def.prodigyId,
                prodigyName = def.prodigyName,
                squad = def.squad,
                grandProjectId = def.grandProjectId,
                pairTitle = def.pairTitle,
                bondXp = xp.coerceAtMost(120),
                rank = rank,
                rankTitle = rankTitle,
                resonanceBonusPercent = bonusPct,
                nextRankXp = nextXp,
                liveRelationshipQuote = quote
            )
        }
    }

    fun getBondTrackForCharacter(charId: String, bondXpMap: Map<String, Int>): SiblingBondTrack {
        val tracks = buildAllSiblingBondTracks(bondXpMap)
        return tracks.firstOrNull { it.mentorId == charId || it.prodigyId == charId } ?: tracks.first()
    }

    fun getDuoResonanceBonusForCharacter(charId: String, bondXpMap: Map<String, Int>): Int {
        return getBondTrackForCharacter(charId, bondXpMap).resonanceBonusPercent
    }

    private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

    private data class MedalDefinition(
        val id: String,
        val title: String,
        val description: String,
        val badgeIconText: String,
        val accentColorHex: Long
    )

    private val medalDefinitions: List<MedalDefinition> = listOf(
        MedalDefinition(
            id = "medal_first_bay",
            title = "Corridor Pioneer",
            description = "Open and resolve your first Chamber Door inside the Infinite Enclave Hallway.",
            badgeIconText = "🚪",
            accentColorHex = 0xFF00F5D4
        ),
        MedalDefinition(
            id = "medal_breakthrough",
            title = "Cymatic Polymath",
            description = "Achieve a ★ Cognitive Breakthrough (82%+ Resonance) on any Chamber Door decision.",
            badgeIconText = "★",
            accentColorHex = 0xFF00F5D4
        ),
        MedalDefinition(
            id = "medal_crisis_defuser",
            title = "Faraday First Responder",
            description = "Defuse an urgent ⚠ Ticking Crisis Bulkhead before its relay timer expires.",
            badgeIconText = "⚠",
            accentColorHex = 0xFFFF3366
        ),
        MedalDefinition(
            id = "medal_vault_breaker",
            title = "Cryptographic Savant",
            description = "Unlock and resolve a 🔒 Cognitive-Locked Vault via MBTI match or Override Torch.",
            badgeIconText = "🔒",
            accentColorHex = 0xFF9B5DE5
        ),
        MedalDefinition(
            id = "medal_sanctuary_tea",
            title = "7.83 Hz Harmonizer",
            description = "Decompress inside a 🍵 Sanctuary Lounge Alcove and ignite Sibling Synergy.",
            badgeIconText = "🍵",
            accentColorHex = 0xFFD4AF37
        ),
        MedalDefinition(
            id = "medal_vex_surfer",
            title = "Cross-Polarity Maverick",
            description = "Channel a Vex Star-Sign Inversion to execute a 4th Cross-Polarity Override choice.",
            badgeIconText = "⚡",
            accentColorHex = 0xFFFF3366
        ),
        MedalDefinition(
            id = "medal_prank_architect",
            title = "Shadow Grid Instigator",
            description = "Queue or detonate a 3-turn delayed Black Market prank on the Sibling Grid.",
            badgeIconText = "🎭",
            accentColorHex = 0xFFFF9F1C
        ),
        MedalDefinition(
            id = "medal_feast_veto",
            title = "The Hub's Arbiter",
            description = "Gather all 16 Geniuses at Ryn's Nightly Feast and exercise The Hub's Veto.",
            badgeIconText = "🍲",
            accentColorHex = 0xFFD4AF37
        ),
        MedalDefinition(
            id = "medal_bond_tier3",
            title = "Found-Family Sync",
            description = "Reach Rank III+ Sibling Bond (45+ XP) with any Mentor-Prodigy duo.",
            badgeIconText = "🤝",
            accentColorHex = 0xFF00BBF9
        ),
        MedalDefinition(
            id = "medal_milestone_8",
            title = "Architect of Readiness",
            description = "Have at least 8 Grand Project Milestone Perks (50% or 100%) unlocked simultaneously.",
            badgeIconText = "🏆",
            accentColorHex = 0xFFD4AF37
        ),
        MedalDefinition(
            id = "medal_project_100",
            title = "Apex Completionist",
            description = "Bring any of the 8 Grand Projects to a full 100% completion.",
            badgeIconText = "🔬",
            accentColorHex = 0xFF00F5D4
        ),
        MedalDefinition(
            id = "medal_grand_convergence",
            title = "Grand Convergence Laureate",
            description = "Stabilize Kepler-452b in The Grand Convergence Finale or ascend to New Game+ Prestige.",
            badgeIconText = "☀",
            accentColorHex = 0xFFD4AF37
        )
    )

    fun buildAchievementMedals(unlockedIds: Set<String>): List<EnclaveAchievementMedal> {
        return medalDefinitions.map { def ->
            EnclaveAchievementMedal(
                id = def.id,
                title = def.title,
                description = def.description,
                badgeIconText = def.badgeIconText,
                accentColorHex = def.accentColorHex,
                isUnlocked = def.id in unlockedIds
            )
        }
    }

    /**
     * Evaluates state after an action and returns the updated set of unlocked medal IDs
     * along with any newly unlocked medal titles for celebratory feedback.
     */
    fun evaluateAndUnlockMedals(
        currentUnlockedIds: Set<String>,
        gatesTraversed: Int,
        dayNumber: Int,
        projects: List<GrandProject>,
        bondXpMap: Map<String, Int>,
        prestigeLevel: Int,
        resolvedDoorArchetype: DoorArchetype? = null,
        achievedBreakthrough: Boolean = false,
        usedVexGate: Boolean = false,
        queuedOrDetonatedPrank: Boolean = false,
        completedGrandConvergence: Boolean = false
    ): Pair<Set<String>, List<EnclaveAchievementMedal>> {
        val nextSet = currentUnlockedIds.toMutableSet()

        if (gatesTraversed >= 1) nextSet.add("medal_first_bay")
        if (achievedBreakthrough) nextSet.add("medal_breakthrough")
        if (resolvedDoorArchetype == DoorArchetype.TICKING_CRISIS) nextSet.add("medal_crisis_defuser")
        if (resolvedDoorArchetype == DoorArchetype.COGNITIVE_VAULT) nextSet.add("medal_vault_breaker")
        if (resolvedDoorArchetype == DoorArchetype.SANCTUARY_LOUNGE) nextSet.add("medal_sanctuary_tea")
        if (usedVexGate) nextSet.add("medal_vex_surfer")
        if (queuedOrDetonatedPrank) nextSet.add("medal_prank_architect")
        if (dayNumber >= 2) nextSet.add("medal_feast_veto")

        val bondTracks = buildAllSiblingBondTracks(bondXpMap)
        if (bondTracks.any { it.rank >= 3 }) {
            nextSet.add("medal_bond_tier3")
        }

        val unlockedPerksCount = CognitiveResonanceAndMilestonesEngine
            .buildAllProjectMilestones(projects)
            .count { it.isUnlocked }
        if (unlockedPerksCount >= 8) {
            nextSet.add("medal_milestone_8")
        }
        if (projects.any { it.progressPercent >= 100 }) {
            nextSet.add("medal_project_100")
        }
        if (completedGrandConvergence || prestigeLevel >= 1 || projects.all { it.progressPercent >= 100 }) {
            nextSet.add("medal_grand_convergence")
        }

        val newlyAddedIds = nextSet - currentUnlockedIds
        val newlyUnlockedMedals = buildAchievementMedals(nextSet).filter { it.id in newlyAddedIds }
        return nextSet to newlyUnlockedMedals
    }

    fun isReadyForGrandConvergence(projects: List<GrandProject>): Boolean {
        if (projects.isEmpty()) return false
        val allComplete = projects.all { it.progressPercent >= 100 }
        val avgProgress = projects.sumOf { it.progressPercent } / projects.size
        val completedCount = projects.count { it.progressPercent >= 100 }
        return allComplete || (avgProgress >= 85 && completedCount >= 2)
    }

    fun isEmergencyBlackoutActive(colonyMorale: Int, magneticFlux: Int): Boolean {
        return colonyMorale <= 12 || magneticFlux >= 96
    }
}
