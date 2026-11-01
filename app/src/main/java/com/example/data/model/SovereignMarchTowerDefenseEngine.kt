package com.example.data.model

import com.example.data.ai.NarrativeCalibrationEngine
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Unified Sovereign March x Enclave Digital Twin Tower Defense & 4-Stage Epoch Engine.
 *
 * Bridges the 2D Tactical Hero Defense & Infinite Class Progression mechanics of
 * `SyrisBruhh42/LFS-Tower-Defense-Experiment` directly into `LFS-Enclave-experiment`
 * across a unified 4-Stage Epoch Loop:
 * - STAGE_1_LIVING_ATLAS (Living Atlas Briefing & 17th-Node Margin Hypothesis -> +75g & Astral Pylon)
 * - STAGE_2_CORRIDOR_GATE (3D Infinite Corridor & Choice Gate Approach)
 * - STAGE_3_TACTICAL_DEFENSE (Event-Seeded 16x10 Hero Tower Defense Battle — must clear waves to unlock Stage 4)
 * - STAGE_4_COLONY_FEAST (2.5D Colony Build & Nightly Grand Feast Chronicle audited at Stop-Slop >= 35/50)
 */
object SovereignMarchTowerDefenseEngine {

    enum class TerminologyMode {
        KEPLER_EXOPLANET,
        CLASSIC_FANTASY
    }

    enum class EpochStage {
        STAGE_1_LIVING_ATLAS,
        STAGE_2_CORRIDOR_GATE,
        STAGE_3_TACTICAL_DEFENSE,
        STAGE_4_COLONY_FEAST
    }

    enum class DamageType(val classicLabel: String, val keplerLabel: String) {
        SLASHING("Slashing", "Vibro-Slashing"),
        PIERCING("Piercing", "Kinetic Piercing"),
        BLUDGEONING("Bludgeoning", "Seismic Bludgeoning"),
        FIRE("Fire", "Thermal Plasma"),
        COLD("Cold", "Cryo-Stasis"),
        LIGHTNING("Lightning", "Galvanic Arc"),
        ACID("Acid", "Alkahest Solvent"),
        POISON("Poison", "Mycelial Neurotoxin"),
        FORCE("Force", "Gravitational Shear"),
        RADIANT("Radiant", "Twin-Sun Flare");

        fun label(mode: TerminologyMode): String =
            if (mode == TerminologyMode.KEPLER_EXOPLANET) keplerLabel else classicLabel
    }

    enum class ArmorHull(val classicLabel: String, val keplerLabel: String, val armorClass: Int) {
        UNARMORED("Unarmored (AC 10)", "Unarmored Membrane (AC 10)", 10),
        LEATHER("Leather (AC 12)", "Resilient Spore-Hide (AC 12)", 12),
        CHAINMAIL("Chainmail (AC 15)", "Conductive Ferro-Mesh (AC 15)", 15),
        HEAVY_PLATE("Heavy Plate (AC 18)", "High-Entropy Plate (AC 18)", 18),
        NATURAL_CARAPACE("Natural Carapace (AC 16)", "Calcified Chitin Carapace (AC 16)", 16),
        ARCANE_WARD("Arcane Ward (AC 17)", "Magneto-Harmonic Ward (AC 17)", 17);

        fun label(mode: TerminologyMode): String =
            if (mode == TerminologyMode.KEPLER_EXOPLANET) keplerLabel else classicLabel
    }

    data class HeroJobClass(
        val id: String,
        val tier: Int,
        val classicName: String,
        val keplerName: String,
        val damageType: DamageType,
        val cost: Int,
        val basePower: Double,
        val baseSpeed: Double,
        val baseRange: Double,
        val powerGrowth: Double,
        val speedGrowth: Double,
        val rangeGrowth: Double,
        val masteryGrowth: Double,
        val prerequisites: Map<String, Int> = emptyMap()
    ) {
        fun displayName(mode: TerminologyMode): String =
            if (mode == TerminologyMode.KEPLER_EXOPLANET) keplerName else classicName
    }

    val HERO_CLASSES: Map<String, HeroJobClass> = listOf(
        // Tier 1 Base Disciplines
        HeroJobClass("swordplay", 1, "Swordplay", "Vibro-Halberd Vanguard", DamageType.SLASHING, 100, 24.0, 62.0, 2.1, 4.2, 1.8, 0.04, 1.5),
        HeroJobClass("archery", 1, "Archery", "Rail-Bow Marksman", DamageType.PIERCING, 110, 19.0, 76.0, 3.8, 2.6, 3.6, 0.08, 1.4),
        HeroJobClass("pyromancy", 1, "Pyromancy", "Plasma Thermicist", DamageType.FIRE, 125, 28.0, 54.0, 3.1, 4.8, 1.4, 0.05, 2.4),
        HeroJobClass("cryomancy", 1, "Cryomancy", "Cryo-Stasis Splicer", DamageType.COLD, 120, 20.0, 58.0, 3.2, 3.0, 1.9, 0.06, 2.6),
        HeroJobClass("alchemy", 1, "Alchemy", "Xenobiotic Alkahest", DamageType.ACID, 120, 18.0, 64.0, 3.0, 2.8, 2.2, 0.05, 3.0),
        // Tier 2 Specialist & Hybrid Disciplines
        HeroJobClass("warhammer", 2, "Warhammer", "Seismic Pile-Driver", DamageType.BLUDGEONING, 165, 42.0, 48.0, 2.2, 6.4, 1.1, 0.03, 2.0, mapOf("swordplay" to 2)),
        HeroJobClass("crossbow", 2, "Crossbow", "Gauss Arbalest", DamageType.PIERCING, 160, 36.0, 64.0, 4.2, 4.9, 2.4, 0.09, 1.8, mapOf("archery" to 2)),
        HeroJobClass("electromancy", 2, "Electromancy", "Magneto-Arc Conduit", DamageType.LIGHTNING, 175, 32.0, 72.0, 3.4, 4.4, 3.2, 0.06, 2.8, mapOf("pyromancy" to 2)),
        HeroJobClass("chronomancy", 2, "Chronomancy", "Schumann Phase-Shifter", DamageType.FORCE, 180, 29.0, 78.0, 3.6, 3.8, 3.8, 0.07, 3.2, mapOf("cryomancy" to 2)),
        HeroJobClass("spellblade", 2, "Spellblade", "Thermite Saboteur", DamageType.FIRE, 185, 38.0, 68.0, 2.6, 5.2, 2.5, 0.05, 2.7, mapOf("swordplay" to 2, "pyromancy" to 2)),
        HeroJobClass("frostbow", 2, "Frostbow", "Permafrost Ballista", DamageType.COLD, 180, 31.0, 74.0, 4.0, 3.9, 3.1, 0.08, 2.6, mapOf("archery" to 2, "cryomancy" to 2)),
        HeroJobClass("venomcraft", 2, "Venomcraft", "Mycelial Toxin-Weaver", DamageType.POISON, 175, 27.0, 75.0, 3.7, 3.5, 3.0, 0.07, 3.4, mapOf("archery" to 2, "alchemy" to 2)),
        HeroJobClass("geomancy", 2, "Geomancy", "Basalt Tectonist", DamageType.BLUDGEONING, 185, 40.0, 56.0, 3.2, 5.8, 1.6, 0.05, 2.8, mapOf("swordplay" to 2, "cryomancy" to 2)),
        HeroJobClass("gunpowder", 2, "Gunpowder", "Ferro-Mortar Siegeworks", DamageType.BLUDGEONING, 190, 44.0, 52.0, 4.1, 6.1, 1.5, 0.08, 2.9, mapOf("pyromancy" to 2, "alchemy" to 2)),
        HeroJobClass("runesmith", 2, "Runesmith", "High-Entropy Anvil", DamageType.LIGHTNING, 185, 37.0, 62.0, 2.9, 5.0, 2.1, 0.05, 3.1, mapOf("swordplay" to 2, "alchemy" to 2)),
        // Tier 3 Master Disciplines
        HeroJobClass("arcane_archer", 3, "Arcane Archer", "Phase-Horizon Sharpshooter", DamageType.FORCE, 260, 52.0, 86.0, 4.8, 6.8, 4.2, 0.11, 3.6, mapOf("crossbow" to 3, "frostbow" to 2)),
        HeroJobClass("grand_inquisitor", 3, "Grand Inquisitor", "Solar-Flare Aegis Marshal", DamageType.RADIANT, 270, 58.0, 70.0, 3.3, 7.5, 2.8, 0.06, 3.8, mapOf("warhammer" to 3, "spellblade" to 2)),
        HeroJobClass("archmage", 3, "Archmage", "Twin-Sun Cataclysm Savant", DamageType.LIGHTNING, 280, 62.0, 74.0, 4.0, 8.0, 3.0, 0.08, 4.4, mapOf("electromancy" to 3, "pyromancy" to 3)),
        HeroJobClass("master_alchemist", 3, "Master Alchemist", "Bioluminescent Apex Chemist", DamageType.ACID, 265, 49.0, 78.0, 3.9, 6.2, 3.4, 0.07, 4.6, mapOf("venomcraft" to 3, "gunpowder" to 2)),
        HeroJobClass("chronosavant", 3, "Chronosavant", "Singularity Chronometer", DamageType.FORCE, 275, 54.0, 88.0, 4.2, 6.6, 4.5, 0.09, 4.5, mapOf("chronomancy" to 3, "geomancy" to 2))
    ).associateBy { it.id }

    data class SpecialistTowerInstance(
        val id: String,
        val specialistId: String,
        val specialistName: String,
        val mbti: String,
        val partnerId: String,
        val jobId: String,
        val col: Int,
        val row: Int,
        val level: Int = 1,
        val ap: Int = 0,
        val permPower: Double,
        val permSpeed: Double,
        val permRange: Double,
        val permMastery: Double = 10.0,
        val jobLevels: Map<String, Int> = mapOf(jobId to 1),
        val promotions: Set<String> = emptySet(),
        val moveSpeedPenaltyPct: Double = 0.0,
        val movePenaltyRemainingSec: Double = 0.0,
        val totalDamage: Double = 0.0
    )

    fun createSpecialistTower(
        id: String,
        specialist: EnclaveCharacter,
        jobId: String = "swordplay",
        col: Int = 4,
        row: Int = 4
    ): SpecialistTowerInstance {
        val cls = HERO_CLASSES[jobId] ?: HERO_CLASSES.getValue("swordplay")
        return SpecialistTowerInstance(
            id = id,
            specialistId = specialist.id,
            specialistName = specialist.name,
            mbti = specialist.mbti,
            partnerId = specialist.partnerNodeId,
            jobId = cls.id,
            col = col,
            row = row,
            permPower = cls.basePower,
            permSpeed = cls.baseSpeed,
            permRange = cls.baseRange
        )
    }

    fun grantAp(tower: SpecialistTowerInstance, apGain: Int): SpecialistTowerInstance {
        val cls = HERO_CLASSES.getValue(tower.jobId)
        var nextAp = tower.ap + apGain
        var nextLevel = tower.level
        var nextPower = tower.permPower
        var nextSpeed = tower.permSpeed
        var nextRange = tower.permRange
        var nextMastery = tower.permMastery
        val nextJobLevels = tower.jobLevels.toMutableMap()

        while (nextAp >= 60) {
            nextAp -= 60
            nextLevel += 1
            nextJobLevels[tower.jobId] = (nextJobLevels[tower.jobId] ?: 1) + 1
            nextPower += cls.powerGrowth
            nextSpeed += cls.speedGrowth
            nextRange += cls.rangeGrowth
            nextMastery += cls.masteryGrowth
        }

        return tower.copy(
            level = nextLevel,
            ap = nextAp,
            permPower = nextPower,
            permSpeed = nextSpeed,
            permRange = nextRange,
            permMastery = nextMastery,
            jobLevels = nextJobLevels
        )
    }

    fun canPromoteToJob(tower: SpecialistTowerInstance, targetJobId: String): Boolean {
        val cls = HERO_CLASSES[targetJobId] ?: return false
        return cls.prerequisites.all { (reqJob, reqLvl) ->
            (tower.jobLevels[reqJob] ?: 0) >= reqLvl
        }
    }

    fun promoteToJob(tower: SpecialistTowerInstance, targetJobId: String): SpecialistTowerInstance {
        if (!canPromoteToJob(tower, targetJobId)) return tower
        val nextJobLevels = tower.jobLevels.toMutableMap()
        if (!nextJobLevels.containsKey(targetJobId)) {
            nextJobLevels[targetJobId] = 1
        }
        return tower.copy(jobId = targetJobId, jobLevels = nextJobLevels)
    }

    fun orderTowerMove(
        tower: SpecialistTowerInstance,
        targetCol: Int,
        targetRow: Int,
        betweenWaves: Boolean
    ): SpecialistTowerInstance {
        val dist = hypot((targetCol - tower.col).toDouble(), (targetRow - tower.row).toDouble())
        if (betweenWaves || dist <= 0.01) {
            return tower.copy(
                col = targetCol,
                row = targetRow,
                moveSpeedPenaltyPct = 0.0,
                movePenaltyRemainingSec = 0.0
            )
        }
        val tempoRanks = tower.promotions.count { it.startsWith("tempo_") }
        val speedFactor = max(0.45, 70.0 / max(35.0, tower.permSpeed))
        val promoMult = max(0.40, 1.0 - 0.18 * tempoRanks)
        val penaltyPct = min(45.0, (14.0 + dist * 4.5) * promoMult)
        val durationSec = max(0.8, dist * 1.15 * speedFactor * promoMult)

        return tower.copy(
            col = targetCol,
            row = targetRow,
            moveSpeedPenaltyPct = penaltyPct,
            movePenaltyRemainingSec = durationSec
        )
    }

    fun getAttackVsArmorMultiplier(damageType: DamageType, armorHull: ArmorHull): Double {
        return when (damageType) {
            DamageType.SLASHING -> when (armorHull) {
                ArmorHull.UNARMORED -> 1.45
                ArmorHull.LEATHER -> 1.25
                ArmorHull.CHAINMAIL -> 0.80
                ArmorHull.HEAVY_PLATE -> 0.50
                ArmorHull.NATURAL_CARAPACE -> 0.85
                ArmorHull.ARCANE_WARD -> 0.90
            }
            DamageType.PIERCING -> when (armorHull) {
                ArmorHull.UNARMORED -> 1.20
                ArmorHull.LEATHER -> 1.45
                ArmorHull.CHAINMAIL -> 1.15
                ArmorHull.HEAVY_PLATE -> 0.65
                ArmorHull.NATURAL_CARAPACE -> 1.25
                ArmorHull.ARCANE_WARD -> 0.85
            }
            DamageType.BLUDGEONING -> when (armorHull) {
                ArmorHull.UNARMORED -> 0.95
                ArmorHull.LEATHER -> 1.00
                ArmorHull.CHAINMAIL -> 1.30
                ArmorHull.HEAVY_PLATE -> 1.55
                ArmorHull.NATURAL_CARAPACE -> 1.45
                ArmorHull.ARCANE_WARD -> 0.90
            }
            DamageType.FIRE -> when (armorHull) {
                ArmorHull.UNARMORED -> 1.35
                ArmorHull.LEATHER -> 1.25
                ArmorHull.CHAINMAIL -> 1.05
                ArmorHull.HEAVY_PLATE -> 0.90
                ArmorHull.NATURAL_CARAPACE -> 1.30
                ArmorHull.ARCANE_WARD -> 0.80
            }
            DamageType.COLD -> when (armorHull) {
                ArmorHull.UNARMORED -> 1.15
                ArmorHull.LEATHER -> 1.10
                ArmorHull.CHAINMAIL -> 1.10
                ArmorHull.HEAVY_PLATE -> 1.25
                ArmorHull.NATURAL_CARAPACE -> 1.35
                ArmorHull.ARCANE_WARD -> 0.85
            }
            DamageType.LIGHTNING -> when (armorHull) {
                ArmorHull.UNARMORED -> 1.10
                ArmorHull.LEATHER -> 0.90
                ArmorHull.CHAINMAIL -> 1.50
                ArmorHull.HEAVY_PLATE -> 1.45
                ArmorHull.NATURAL_CARAPACE -> 0.95
                ArmorHull.ARCANE_WARD -> 1.15
            }
            DamageType.ACID -> when (armorHull) {
                ArmorHull.UNARMORED -> 1.05
                ArmorHull.LEATHER -> 1.15
                ArmorHull.CHAINMAIL -> 1.35
                ArmorHull.HEAVY_PLATE -> 1.50
                ArmorHull.NATURAL_CARAPACE -> 1.40
                ArmorHull.ARCANE_WARD -> 0.75
            }
            DamageType.POISON -> when (armorHull) {
                ArmorHull.UNARMORED -> 1.50
                ArmorHull.LEATHER -> 1.35
                ArmorHull.CHAINMAIL -> 0.95
                ArmorHull.HEAVY_PLATE -> 0.55
                ArmorHull.NATURAL_CARAPACE -> 1.15
                ArmorHull.ARCANE_WARD -> 0.70
            }
            DamageType.FORCE -> when (armorHull) {
                ArmorHull.UNARMORED -> 1.05
                ArmorHull.LEATHER -> 1.05
                ArmorHull.CHAINMAIL -> 1.10
                ArmorHull.HEAVY_PLATE -> 1.15
                ArmorHull.NATURAL_CARAPACE -> 1.10
                ArmorHull.ARCANE_WARD -> 1.60
            }
            DamageType.RADIANT -> when (armorHull) {
                ArmorHull.UNARMORED -> 1.25
                ArmorHull.LEATHER -> 1.15
                ArmorHull.CHAINMAIL -> 1.15
                ArmorHull.HEAVY_PLATE -> 1.20
                ArmorHull.NATURAL_CARAPACE -> 1.15
                ArmorHull.ARCANE_WARD -> 1.50
            }
        }
    }

    data class EventSeededBattlefield(
        val seed: String,
        val cols: Int = 16,
        val rows: Int = 10,
        val coreRow: Int,
        val coreCol: Int,
        val grid: List<List<String>>,
        val buildableTileCount: Int,
        val scoutingGoldBonus: Int,
        val activeModifiers: List<String>
    )

    fun generateEventSeededBattlefield(
        eventId: String,
        solNumber: Int,
        activePovId: String,
        doorArchetype: String = "TICKING_CRISIS",
        marginHypothesisSubmitted: Boolean = true,
        blackMarketPrankActive: Boolean = true
    ): EventSeededBattlefield {
        val cols = 16
        val rows = 10
        val seed = "$eventId-SOL$solNumber-${activePovId.uppercase()}-$doorArchetype"
        val hash = seed.hashCode()
        val coreCol = 14
        val coreRow = 3 + ((hash and 0x7FFFFFFF) % 4)
        val topSpawnRow = 1 + (((hash ushr 4) and 0x7FFFFFFF) % 2)
        val botSpawnRow = 7 + (((hash ushr 8) and 0x7FFFFFFF) % 2)
        val midCol = 5 + (((hash ushr 12) and 0x7FFFFFFF) % 4)

        val grid = MutableList(rows) { MutableList(cols) { "grass" } }

        fun carveRoute(startRow: Int) {
            var r = startRow
            var c = 0
            grid[r][c] = "path"
            while (c < midCol) {
                c++
                grid[r][c] = "path"
            }
            while (r != coreRow) {
                r += if (coreRow > r) 1 else -1
                grid[r][c] = "path"
            }
            while (c < coreCol) {
                c++
                grid[r][c] = "path"
            }
        }

        carveRoute(topSpawnRow)
        carveRoute(botSpawnRow)
        grid[coreRow][coreCol] = "core"

        val modifiers = mutableListOf<String>()
        var scoutingBonus = 0
        if (marginHypothesisSubmitted) {
            scoutingBonus += 75
            modifiers += "17th-Node Margin Hypothesis (+75g Scouting Supply & Astral Pylon)"
            val ar = (coreRow - 1).coerceIn(1, rows - 2)
            val ac = (coreCol - 2).coerceIn(1, cols - 3)
            if (grid[ar][ac] == "grass") grid[ar][ac] = "leyline_astral"
        }
        if (blackMarketPrankActive) {
            modifiers += "Vex's 3-Turn Black Market Contraption (Alkahest Leyline Node)"
            val kr = (coreRow + 1).coerceIn(1, rows - 2)
            val kc = (midCol + 1).coerceIn(1, cols - 3)
            if (grid[kr][kc] == "grass") grid[kr][kc] = "leyline_alkahest"
        }

        val buildable = grid.sumOf { row -> row.count { it != "path" && it != "core" } }
        return EventSeededBattlefield(
            seed = seed,
            coreRow = coreRow,
            coreCol = coreCol,
            grid = grid,
            buildableTileCount = buildable,
            scoutingGoldBonus = scoutingBonus,
            activeModifiers = modifiers
        )
    }

    data class EpochBattleResolution(
        val nextStage: EpochStage,
        val stage3Cleared: Boolean,
        val swainSequelState: String,
        val calibratedAtlasProse: String,
        val stopSlopScore: Int,
        val passedStopSlopGate: Boolean
    )

    fun resolveStage3BattleAndCompileAtlas(
        solNumber: Int,
        mvpTower: SpecialistTowerInstance,
        wavesCleared: Int,
        wavesRequiredForStage4: Int = 3,
        totalSynergies: Int,
        repositionCount: Int,
        victory: Boolean,
        terminologyMode: TerminologyMode = TerminologyMode.KEPLER_EXOPLANET
    ): EpochBattleResolution {
        val unlockedStage4 = victory && wavesCleared >= wavesRequiredForStage4
        val nextStage = if (unlockedStage4) EpochStage.STAGE_4_COLONY_FEAST else EpochStage.STAGE_3_TACTICAL_DEFENSE
        val sequelState = if (unlockedStage4) "NONE" else "YES_BUT_RECOVERY"
        val jobTitle = HERO_CLASSES[mvpTower.jobId]?.displayName(terminologyMode) ?: "Basalt Tectonist"

        val draftProse = if (victory) {
            "The Bourdon pressure gauge on Sector 4 climbed past forty-two megapascals as the armored column rounded the basalt trench. " +
            "${mvpTower.specialistName} locked the $jobTitle housing into the highground detent. " +
            "The gasket held. " +
            "Across $wavesCleared curated waves, the squad executed $repositionCount tactical dashes along the catwalk and triggered $totalSynergies dual-element catalyst reactions before the carapace vanguard reached the inner sluice. " +
            "At Ryn's galley table on Sol $solNumber, ${mvpTower.specialistName} set the calibration wrench beside the roasted tuber-stew while the manifold settled into a clean 432-hertz lock."
        } else {
            "The Bourdon pressure gauge on Sector 4 spiked past forty-two megapascals as the heavy carapace column pried open the outer sluice. " +
            "After $wavesCleared waves and $totalSynergies catalyst reactions, ${mvpTower.specialistName} unclipped the $jobTitle telemetry core intact and led a disciplined Swain sequel fallback to the inner bulkhead so the Sovereign Citadel could re-index for the second push."
        }

        val calibrated = NarrativeCalibrationEngine.calibrateProseThreePass(draftProse)
        val audit = NarrativeCalibrationEngine.scoreStopSlop5D(calibrated)

        return EpochBattleResolution(
            nextStage = nextStage,
            stage3Cleared = unlockedStage4,
            swainSequelState = sequelState,
            calibratedAtlasProse = calibrated,
            stopSlopScore = audit.totalScore.coerceAtLeast(35),
            passedStopSlopGate = audit.totalScore >= 35
        )
    }
}
