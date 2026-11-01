package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import kotlin.math.sqrt

@IgnoreExtraProperties
data class EnclaveSaveState(
    @DocumentId val id: String = "",
    val userId: String = "",
    val dayNumber: Long = 1L,
    val relayStep: Long = 1L,
    val activePovId: String = "silas",
    val currentBiome: String = "arboretum",
    val colonyMorale: Long = 86L,
    val magneticFlux: Long = 42L,
    val gatesTraversed: Long = 0L,
    val statePayloadJson: String = "{}",
    val lastSummary: String = "Arrived at the Bioluminescent Arboretum.",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

enum class Squad(
    val id: String,
    val title: String,
    val subtitle: String,
    val primaryColorHex: Long,
    val badgeSymbol: String
) {
    FORGE(
        id = "forge",
        title = "The Forge",
        subtitle = "Industry, Metallurgy & Physics",
        primaryColorHex = 0xFFFF9F1C,
        badgeSymbol = "⚒"
    ),
    WILDS(
        id = "wilds",
        title = "The Wilds",
        subtitle = "Mycology, Fauna & Exploration",
        primaryColorHex = 0xFF00F5D4,
        badgeSymbol = "🌿"
    ),
    MIND(
        id = "mind",
        title = "The Mind",
        subtitle = "Psychology, Cymatics & Simulacrum",
        primaryColorHex = 0xFF9B5DE5,
        badgeSymbol = "◈"
    ),
    HUB(
        id = "hub",
        title = "The Hub",
        subtitle = "Logistics, Atlas, Feast & Veto",
        primaryColorHex = 0xFFD4AF37,
        badgeSymbol = "❂"
    );

    companion object {
        fun fromId(id: String): Squad =
            entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: FORGE
    }
}

enum class Biome3D(
    val id: String,
    val title: String,
    val subtitle: String,
    val skyTopColor: Long,
    val skyHorizonColor: Long,
    val groundColor: Long,
    val gridColor: Long,
    val accentColor: Long
) {
    ARBORETUM(
        id = "arboretum",
        title = "Bioluminescent Arboretum",
        subtitle = "Mycena Lucentipes & High-Entropy Alloy Conduits",
        skyTopColor = 0xFF06101E,
        skyHorizonColor = 0xFF102A3A,
        groundColor = 0xFF081822,
        gridColor = 0xFF00F5D4,
        accentColor = 0xFF38EF7D
    ),
    STORMBREAKER(
        id = "stormbreaker",
        title = "Stormbreaker Tower Roof",
        subtitle = "50 kV/m Potential Gradient & Femtosecond Laser Array",
        skyTopColor = 0xFF170B2C,
        skyHorizonColor = 0xFF3C1654,
        groundColor = 0xFF121324,
        gridColor = 0xFFFF9F1C,
        accentColor = 0xFFF15BB5
    ),
    FORGE_HALL(
        id = "forge_hall",
        title = "The First Forge",
        subtitle = "Rosensweig Ferrofluid Collar & Geode Containment",
        skyTopColor = 0xFF1B0D0A,
        skyHorizonColor = 0xFF3D1E12,
        groundColor = 0xFF161010,
        gridColor = 0xFFFF9F1C,
        accentColor = 0xFFD4AF37
    ),
    ATLAS_CHAMBER(
        id = "atlas_chamber",
        title = "Crystalline Atlas Chamber",
        subtitle = "Sector 4 Magnetic Tear & 5D Quartz Holography",
        skyTopColor = 0xFF090D24,
        skyHorizonColor = 0xFF1A2652,
        groundColor = 0xFF0B1126,
        gridColor = 0xFF4CC9F0,
        accentColor = 0xFF9B5DE5
    ),
    DEEP_AQUIFER(
        id = "deep_aquifer",
        title = "Subterranean Iron Lake",
        subtitle = "Photophore Cephalopods & Chemosynthetic Vents",
        skyTopColor = 0xFF040B14,
        skyHorizonColor = 0xFF0A1E32,
        groundColor = 0xFF05111C,
        gridColor = 0xFF00BBF9,
        accentColor = 0xFF00F5D4
    );

    companion object {
        fun fromId(id: String): Biome3D =
            entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: ARBORETUM
    }
}

data class BigFiveProfile(
    val openness: Int,
    val conscientiousness: Int,
    val extraversion: Int,
    val agreeableness: Int,
    val neuroticism: Int
)

data class EnclaveCharacter(
    val id: String,
    val name: String,
    val roleTitle: String,
    val squad: Squad,
    val isMentor: Boolean,
    val partnerNodeId: String,
    val mbti: String,
    val cognitiveStack: String,
    val enneagram: String,
    val archetype: String,
    val bigFive: BigFiveProfile,
    val vexStarSign: String,
    val vexOverrideTitle: String,
    val vexOverrideBehavior: String,
    val grandProjectId: String,
    val scientificSpecialty: String,
    val geniusVsMundane: String,
    val lieBelieved: String,
    val truthToLearn: String,
    val stressLevel: Int = 28,
    val isVexInverted: Boolean = false
)

data class Vec3(
    val x: Float,
    val y: Float,
    val z: Float
) {
    fun distanceXZ(other: Vec3): Float {
        val dx = x - other.x
        val dz = z - other.z
        return sqrt(dx * dx + dz * dz)
    }
}

data class ChoiceGate3D(
    val id: String,
    val title: String,
    val subtitle: String,
    val scientificDomain: String,
    val squadAlignment: Squad,
    val position: Vec3,
    val isVexGate: Boolean = false,
    val projectId: String,
    val projectDelta: Int,
    val stressDelta: Int,
    val moraleDelta: Int,
    val nextPovId: String,
    val nextBiomeId: String,
    val outcomeNarrative: String,
    val batonPassQuote: String
)

data class GrandProject(
    val id: String,
    val name: String,
    val squad: Squad,
    val prodigyId: String,
    val mentorId: String,
    val scientificQuestion: String,
    val currentObstacle: String,
    val progressPercent: Int,
    val unlockedBreakthroughs: List<String>
)

data class BlackMarketPrank(
    val id: String,
    val instigatorId: String,
    val intendedTargetId: String,
    val accidentalVictimId: String,
    val blamedNodeId: String,
    val title: String,
    val pettyDescription: String,
    val blindSpotOutcome: String,
    val turnsRemaining: Int
)

data class FeastExchange(
    val speakerId: String,
    val speakerName: String,
    val roleTitle: String,
    val squad: Squad,
    val dialogue: String,
    val scientificCheeseNote: String
)

data class HubVetoOption(
    val id: String,
    val title: String,
    val proponentName: String,
    val squad: Squad,
    val scientificArgument: String,
    val projectId: String,
    val projectBoost: Int,
    val moraleBoost: Int,
    val fluxReduction: Int
)

data class ChronicleEntry(
    val id: String,
    val dayNumber: Int,
    val relayStep: Int,
    val povName: String,
    val povMbti: String,
    val gateTitle: String,
    val scientificDomain: String,
    val summary: String
)

enum class DoorSide {
    LEFT_WALL,
    RIGHT_WALL
}

enum class DoorStatus {
    UNEXPLORED,
    INSPECTED,
    RESOLVED
}

enum class DoorArchetype(
    val badgeLabel: String,
    val shortTag: String,
    val colorHex: Long,
    val description: String
) {
    STANDARD_LAB(
        badgeLabel = "⚗ STANDARD LAB BAY",
        shortTag = "LAB BAY",
        colorHex = 0xFF00F5D4,
        description = "Active Enclave experiment bay with 3+ scientific & diplomatic approaches."
    ),
    COGNITIVE_VAULT(
        badgeLabel = "🔒 COGNITIVE VAULT",
        shortTag = "🔒 VAULT",
        colorHex = 0xFF9B5DE5,
        description = "Precision-sealed chamber requiring a matching MBTI Cognitive Function, Grand Project milestone, or Vault Override."
    ),
    TICKING_CRISIS(
        badgeLabel = "⚠ CRISIS BULKHEAD",
        shortTag = "⚠ CRISIS",
        colorHex = 0xFFFF3366,
        description = "High-voltage emergency bay! Defuse before its relay timer expires for a Heroic Breakthrough bonus."
    ),
    SANCTUARY_LOUNGE(
        badgeLabel = "🍵 SANCTUARY ALCOVE",
        shortTag = "🍵 SANCTUARY",
        colorHex = 0xFFD4AF37,
        description = "Restorative found-family alcove. Vent Vex stress and forge a +15% Sibling Synergy Resonance buff."
    )
}

data class CorridorDoor3D(
    val id: String,
    val doorNumber: Int,
    val scenarioSignature: String,
    val chamberTitle: String,
    val chamberSubtitle: String,
    val scientificDomain: String,
    val squadAlignment: Squad,
    val biomeTheme: Biome3D,
    val side: DoorSide,
    val position: Vec3,
    val roomDilemmaProse: String,
    val scientificCheeseNote: String,
    val leadNpcId: String,
    val options: List<ChoiceGate3D>,
    val status: DoorStatus = DoorStatus.UNEXPLORED,
    val chosenOptionId: String? = null,
    val resolvedOutcomeText: String? = null,
    val archetype: DoorArchetype = DoorArchetype.STANDARD_LAB,
    val requiredCognitiveFunction: String? = null,
    val requiredProjectId: String? = null,
    val requiredProjectPercent: Int = 75,
    val crisisTurnsRemaining: Int? = null,
    val isVaultUnlockedByOverride: Boolean = false
)

enum class ResonanceTier(
    val label: String,
    val badgeColorHex: Long
) {
    BREAKTHROUGH("★ COGNITIVE BREAKTHROUGH", 0xFF00F5D4),
    RESONANT("✓ COGNITIVE RESONANCE", 0xFFD4AF37),
    STRAINED("⚡ OFF-STACK STRAIN", 0xFFFF9F1C)
}

data class ChoiceResonanceEvaluation(
    val optionId: String,
    val resonancePercent: Int,
    val tier: ResonanceTier,
    val reasonLabel: String,
    val bonusProjectDelta: Int,
    val bonusStressDelta: Int,
    val bonusMoraleDelta: Int,
    val predictedPartnerQuote: String
)

data class ProjectMilestonePerk(
    val projectId: String,
    val projectName: String,
    val squad: Squad,
    val thresholdPercent: Int, // 50 or 100
    val title: String,
    val benefitSummary: String,
    val isUnlocked: Boolean
)

data class ChamberAftermathReport(
    val doorId: String,
    val chamberTitle: String,
    val archetype: DoorArchetype,
    val executedByCharacterName: String,
    val executedByMbti: String,
    val relayedToCharacterName: String?,
    val chosenOptionTitle: String,
    val resonancePercent: Int,
    val tier: ResonanceTier,
    val outcomeNarrative: String,
    val partnerSpeakerName: String,
    val partnerSpeakerRole: String,
    val partnerReactionDialogue: String,
    val batonPassQuote: String,
    val totalProjectDelta: Int,
    val targetProjectName: String,
    val totalStressDelta: Int,
    val totalMoraleDelta: Int,
    val wasCrisisDefused: Boolean = false,
    val grantedSiblingSynergyBuff: Boolean = false,
    val newlyUnlockedMilestones: List<ProjectMilestonePerk> = emptyList(),
    val bondXpGained: Int = 0,
    val duoBondTitle: String = "",
    val newlyUnlockedMedals: List<String> = emptyList()
)
