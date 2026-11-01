package com.example.data.model

/**
 * Core engine for:
 * 1. The 8 Grand Projects' 50% & 100% Milestone Capabilities (Active & Passive Tech Tree).
 * 2. Cognitive-Locked Vault evaluation and matching Genius Roster recommendations.
 * 3. Psychometric Cognitive Resonance Skill Checks (MBTI Stack + Big 5 OCEAN + Perks).
 * 4. Dramatic Chamber Aftermath generation with partner dialogue and breakthrough bonuses.
 */
object CognitiveResonanceAndMilestonesEngine {

    private data class MilestoneDefinition(
        val projectId: String,
        val thresholdPercent: Int,
        val title: String,
        val benefitSummary: String
    )

    private val milestoneDefinitions: List<MilestoneDefinition> = listOf(
        // 1. The First Forge (Silas & Leo)
        MilestoneDefinition(
            projectId = "first_forge",
            thresholdPercent = 50,
            title = "Bulkhead Override Torch",
            benefitSummary = "Unlocks free override of any Cognitive-Locked Vault door without Vex stress penalty."
        ),
        MilestoneDefinition(
            projectId = "first_forge",
            thresholdPercent = 100,
            title = "High-Entropy Hull Mastery",
            benefitSummary = "All Forge & Metallurgy interventions gain +4% bonus project progress and +3% Morale."
        ),
        // 2. Stormbreaker Tower (Hector & Jane)
        MilestoneDefinition(
            projectId = "stormbreaker",
            thresholdPercent = 50,
            title = "Faraday Crisis Shunt",
            benefitSummary = "Extends Ticking Crisis door fuses by +1 relay and boosts Crisis defusal project rewards by +5%."
        ),
        MilestoneDefinition(
            projectId = "stormbreaker",
            thresholdPercent = 100,
            title = "Terawatt Capacitor Grid",
            benefitSummary = "Planetary Magnetic Flux instability is permanently capped at 65% and Vex Gates grant +5% Morale."
        ),
        // 3. The Sanctuary (Kaio & Zara)
        MilestoneDefinition(
            projectId = "sanctuary",
            thresholdPercent = 50,
            title = "Luciferin Spore Empathy",
            benefitSummary = "Grants +14% Cognitive Resonance on all Wilds, Xenobiology, and Mycology chamber options."
        ),
        MilestoneDefinition(
            projectId = "sanctuary",
            thresholdPercent = 100,
            title = "Apex Bioluminescent Symbiosis",
            benefitSummary = "Every resolved Chamber Door automatically restores +3% Colony Morale across the Enclave."
        ),
        // 4. The Genesis Garden (Willow & Terra)
        MilestoneDefinition(
            projectId = "genesis_garden",
            thresholdPercent = 50,
            title = "Adaptogenic Nectar Infusion",
            benefitSummary = "Sanctuary Lounge Alcoves clear an extra -12% Vex Stress (up to -37% total stress relief)."
        ),
        MilestoneDefinition(
            projectId = "genesis_garden",
            thresholdPercent = 100,
            title = "Diazotrophic Atmospheric Bloom",
            benefitSummary = "Passively vents -4% Vex Stress across all 16 geniuses every time any Chamber Door is resolved."
        ),
        // 5. The Lyceum of the Mind (Kenna & Elias)
        MilestoneDefinition(
            projectId = "lyceum_mind",
            thresholdPercent = 50,
            title = "Schumann 7.83 Hz Entrainment",
            benefitSummary = "Boosts Cognitive Resonance by +15% across all 16 characters' chamber decisions."
        ),
        MilestoneDefinition(
            projectId = "lyceum_mind",
            thresholdPercent = 100,
            title = "Polymath Neuro-Synthesis",
            benefitSummary = "Eliminates off-stack Strain stress penalties when attempting choices outside your MBTI stack."
        ),
        // 6. The Simulacrum (Isla & Orion)
        MilestoneDefinition(
            projectId = "simulacrum",
            thresholdPercent = 50,
            title = "5D Quartz Outcome Predictor",
            benefitSummary = "Displays exact Breakthrough bonus deltas and simulated partner reactions on every door choice."
        ),
        MilestoneDefinition(
            projectId = "simulacrum",
            thresholdPercent = 100,
            title = "Birefringent Horizon Archive",
            benefitSummary = "Every Cognitive Breakthrough automatically grants +4% bonus progress to your lowest Grand Project."
        ),
        // 7. The Living Atlas (Augusta & Cassia)
        MilestoneDefinition(
            projectId = "living_atlas",
            thresholdPercent = 50,
            title = "4-Bay Corridor Radar Foresight",
            benefitSummary = "Expands the Corridor HUD radar to detect 4 bays ahead, including Vault locks and Crisis timers."
        ),
        MilestoneDefinition(
            projectId = "living_atlas",
            thresholdPercent = 100,
            title = "Planetary Magnetosphere Lock",
            benefitSummary = "Grants +12% Cognitive Resonance inside all Hub, Atlas, and Stormbreaker chambers."
        ),
        // 8. The Mycelial Concordance & Hearth (Willow, Ryn & Vex)
        MilestoneDefinition(
            projectId = "mycelial_concordance",
            thresholdPercent = 50,
            title = "72°C Methional Comfort Broth",
            benefitSummary = "Nightly Grand Feast clears -30% Vex Stress and grants a free +15% Sibling Synergy buff."
        ),
        MilestoneDefinition(
            projectId = "mycelial_concordance",
            thresholdPercent = 100,
            title = "Grounded Copper Table Harmony",
            benefitSummary = "All diplomatic & partner baton-pass choices gain +12% Resonance and +4% Colony Morale."
        )
    )

    fun buildAllProjectMilestones(projects: List<GrandProject>): List<ProjectMilestonePerk> {
        val projectMap = projects.associateBy { it.id }
        return milestoneDefinitions.mapNotNull { def ->
            val proj = projectMap[def.projectId] ?: return@mapNotNull null
            ProjectMilestonePerk(
                projectId = proj.id,
                projectName = proj.name,
                squad = proj.squad,
                thresholdPercent = def.thresholdPercent,
                title = def.title,
                benefitSummary = def.benefitSummary,
                isUnlocked = proj.progressPercent >= def.thresholdPercent
            )
        }
    }

    fun isMilestoneUnlocked(
        projects: List<GrandProject>,
        projectId: String,
        thresholdPercent: Int = 50
    ): Boolean {
        val proj = projects.firstOrNull { it.id == projectId } ?: return false
        return proj.progressPercent >= thresholdPercent
    }

    fun detectNewlyUnlockedMilestones(
        previousProjects: List<GrandProject>,
        updatedProjects: List<GrandProject>
    ): List<ProjectMilestonePerk> {
        val beforeUnlocked = buildAllProjectMilestones(previousProjects)
            .filter { it.isUnlocked }
            .map { "${it.projectId}_${it.thresholdPercent}" }
            .toSet()

        return buildAllProjectMilestones(updatedProjects).filter { perk ->
            perk.isUnlocked && "${perk.projectId}_${perk.thresholdPercent}" !in beforeUnlocked
        }
    }

    private fun parseCognitiveFunctions(cognitiveStack: String): List<String> {
        return cognitiveStack.split(Regex("[>-]")).map { it.trim() }.filter { it.isNotEmpty() }
    }

    /**
     * Checks whether the active character or Enclave project state unlocks a [DoorArchetype.COGNITIVE_VAULT] door
     * without requiring a Force Breach stress penalty.
     */
    fun isVaultUnlocked(
        door: CorridorDoor3D,
        activeCharacter: EnclaveCharacter,
        projects: List<GrandProject>
    ): Boolean {
        if (door.archetype != DoorArchetype.COGNITIVE_VAULT) return true
        if (door.isVaultUnlockedByOverride) return true

        val reqFn = door.requiredCognitiveFunction
        val stackTokens = parseCognitiveFunctions(activeCharacter.cognitiveStack)
        val matchesFunction = reqFn != null && stackTokens.take(2).any { it.equals(reqFn, ignoreCase = true) }
        if (matchesFunction) return true

        val reqProjId = door.requiredProjectId
        if (reqProjId != null) {
            val proj = projects.firstOrNull { it.id == reqProjId }
            if (proj != null && proj.progressPercent >= door.requiredProjectPercent) {
                return true
            }
        }
        return false
    }

    /**
     * Returns all Geniuses whose Dominant or Auxiliary cognitive function matches the Vault's lock.
     */
    fun getCharactersMatchingVault(
        door: CorridorDoor3D,
        allCharacters: List<EnclaveCharacter>
    ): List<EnclaveCharacter> {
        val reqFn = door.requiredCognitiveFunction ?: return emptyList()
        return allCharacters.filter { ch ->
            parseCognitiveFunctions(ch.cognitiveStack).take(2).any { it.equals(reqFn, ignoreCase = true) }
        }
    }

    fun buildVaultRequirementLabel(
        door: CorridorDoor3D,
        projects: List<GrandProject>
    ): String {
        val fn = door.requiredCognitiveFunction ?: "Ti"
        val projName = projects.firstOrNull { it.id == door.requiredProjectId }?.name ?: "The First Forge"
        return "Requires Dominant/Aux '$fn' Cognitive Stack OR $projName ≥ ${door.requiredProjectPercent}%"
    }

    /**
     * Evaluates Cognitive Resonance (35%..99%) for every choice inside [door] for [activeCharacter].
     */
    fun evaluateDoorOptionsResonance(
        door: CorridorDoor3D,
        activeCharacter: EnclaveCharacter,
        allCharacters: List<EnclaveCharacter>,
        projects: List<GrandProject>,
        hasSiblingSynergyBuff: Boolean,
        bondXpMap: Map<String, Int> = emptyMap(),
        prestigeLevel: Int = 0
    ): Map<String, ChoiceResonanceEvaluation> {
        return door.options.associate { opt ->
            opt.id to evaluateSingleChoiceResonance(
                door = door,
                option = opt,
                activeCharacter = activeCharacter,
                allCharacters = allCharacters,
                projects = projects,
                hasSiblingSynergyBuff = hasSiblingSynergyBuff,
                bondXpMap = bondXpMap,
                prestigeLevel = prestigeLevel
            )
        }
    }

    fun evaluateSingleChoiceResonance(
        door: CorridorDoor3D,
        option: ChoiceGate3D,
        activeCharacter: EnclaveCharacter,
        allCharacters: List<EnclaveCharacter>,
        projects: List<GrandProject>,
        hasSiblingSynergyBuff: Boolean,
        bondXpMap: Map<String, Int> = emptyMap(),
        prestigeLevel: Int = 0
    ): ChoiceResonanceEvaluation {
        val partner = allCharacters.firstOrNull { it.id == door.leadNpcId && it.id != activeCharacter.id }
            ?: allCharacters.firstOrNull { it.id == activeCharacter.partnerNodeId }
            ?: allCharacters.first()

        // Sanctuary Lounge choices are always deeply resonant & restorative
        if (door.archetype == DoorArchetype.SANCTUARY_LOUNGE) {
            val garden50 = isMilestoneUnlocked(projects, "genesis_garden", 50)
            val extraStressRelief = if (garden50) -12 else 0
            return ChoiceResonanceEvaluation(
                optionId = option.id,
                resonancePercent = 96,
                tier = ResonanceTier.BREAKTHROUGH,
                reasonLabel = "${activeCharacter.mbti} Sanctuary Recoupling" + if (garden50) " + Adaptogenic Nectar" else "",
                bonusProjectDelta = 3,
                bonusStressDelta = extraStressRelief,
                bonusMoraleDelta = 5,
                predictedPartnerQuote = "\"Take a breath, ${activeCharacter.name}. Nobody solves Kepler-452b on an empty cup.\""
            )
        }

        // Vex Override Gate
        if (option.isVexGate) {
            return ChoiceResonanceEvaluation(
                optionId = option.id,
                resonancePercent = 98,
                tier = ResonanceTier.BREAKTHROUGH,
                reasonLabel = "Vex Cross-Polarity Surge (${activeCharacter.vexStarSign})",
                bonusProjectDelta = 5,
                bonusStressDelta = 0,
                bonusMoraleDelta = if (isMilestoneUnlocked(projects, "stormbreaker", 100)) 5 else 0,
                predictedPartnerQuote = "\"${activeCharacter.name} just bypassed every safety interlock—and somehow the telemetry is holding!\""
            )
        }

        var score = 56
        val reasons = mutableListOf<String>()

        // 1. Squad & Cognitive Stack Alignment
        val stackParts = parseCognitiveFunctions(activeCharacter.cognitiveStack)
        val domFn = stackParts.getOrNull(0) ?: "Ti"
        val auxFn = stackParts.getOrNull(1) ?: "Ne"

        if (activeCharacter.squad == option.squadAlignment) {
            score += 18
            reasons.add("${activeCharacter.squad.title} Specialist")
        } else if (
            (option.squadAlignment == Squad.FORGE && (domFn in listOf("Te", "Ti", "Si", "Se") || auxFn in listOf("Te", "Ti"))) ||
            (option.squadAlignment == Squad.WILDS && (domFn in listOf("Ne", "Se", "Fi") || auxFn in listOf("Ne", "Se"))) ||
            (option.squadAlignment == Squad.MIND && (domFn in listOf("Ni", "Ti", "Fi") || auxFn in listOf("Ni", "Ne"))) ||
            (option.squadAlignment == Squad.HUB && (domFn in listOf("Fe", "Te", "Si") || auxFn in listOf("Fe", "Te")))
        ) {
            score += 12
            reasons.add("$domFn-$auxFn Cross-Synergy")
        } else {
            score -= 7
            reasons.add("Off-Stack ($domFn-$auxFn)")
        }

        // 2. Big 5 OCEAN Trait Fit
        if (option.projectDelta >= 10 && activeCharacter.bigFive.conscientiousness >= 75) {
            score += 8
            reasons.add("High-C Precision")
        } else if (option.stressDelta < 0 && activeCharacter.bigFive.agreeableness >= 70) {
            score += 9
            reasons.add("High-A Empathy")
        } else if (activeCharacter.bigFive.openness >= 85) {
            score += 7
            reasons.add("High-O Lateral Leap")
        }

        // 3. Active Sibling Synergy Buff from Sanctuary Lounge or Mycelial Concordance 100%
        if (hasSiblingSynergyBuff || isMilestoneUnlocked(projects, "mycelial_concordance", 100)) {
            score += 15
            reasons.add("Sibling Synergy +15%")
        }

        // 4. Grand Project 50% / 100% Milestone Perks
        if (isMilestoneUnlocked(projects, "lyceum_mind", 50)) {
            score += 15
            reasons.add("Lyceum 7.83Hz +15%")
        }
        if (option.squadAlignment == Squad.WILDS && isMilestoneUnlocked(projects, "sanctuary", 50)) {
            score += 14
            reasons.add("Spore Empathy +14%")
        }
        if ((door.biomeTheme == Biome3D.ATLAS_CHAMBER || door.biomeTheme == Biome3D.STORMBREAKER) &&
            isMilestoneUnlocked(projects, "living_atlas", 100)
        ) {
            score += 12
            reasons.add("Atlas Lock +12%")
        }

        // 5. Sibling Bond Duo Affinity Bonus & New Game+ Prestige Bonus
        if (bondXpMap.isNotEmpty()) {
            val duoBonus = EngagementAndEndgameEngine.getDuoResonanceBonusForCharacter(activeCharacter.id, bondXpMap)
            if (duoBonus > 0) {
                score += duoBonus
                if (duoBonus >= 7) {
                    reasons.add("Duo Bond +$duoBonus%")
                }
            }
        }
        if (prestigeLevel > 0) {
            val prestigeBonus = (prestigeLevel * 5).coerceAtMost(25)
            score += prestigeBonus
            reasons.add("Prestige +$prestigeBonus%")
        }

        // 6. High Stress drag
        if (activeCharacter.stressLevel >= 75 && !activeCharacter.isVexInverted) {
            score -= 8
        }

        val clampedScore = score.coerceIn(38, 99)
        val tier = when {
            clampedScore >= 82 -> ResonanceTier.BREAKTHROUGH
            clampedScore >= 58 -> ResonanceTier.RESONANT
            else -> ResonanceTier.STRAINED
        }

        val lyceum100 = isMilestoneUnlocked(projects, "lyceum_mind", 100)
        val forge100Bonus = if (option.squadAlignment == Squad.FORGE && isMilestoneUnlocked(projects, "first_forge", 100)) 4 else 0
        val crisisDefuseBonus = if (door.archetype == DoorArchetype.TICKING_CRISIS) {
            if (isMilestoneUnlocked(projects, "stormbreaker", 50)) 9 else 4
        } else 0

        val bonusProj = when (tier) {
            ResonanceTier.BREAKTHROUGH -> 4 + forge100Bonus + crisisDefuseBonus
            ResonanceTier.RESONANT -> 1 + forge100Bonus + crisisDefuseBonus
            ResonanceTier.STRAINED -> forge100Bonus + crisisDefuseBonus
        }

        val bonusStress = when (tier) {
            ResonanceTier.BREAKTHROUGH -> -4
            ResonanceTier.RESONANT -> 0
            ResonanceTier.STRAINED -> if (lyceum100) 0 else 6
        }

        val bonusMorale = when (tier) {
            ResonanceTier.BREAKTHROUGH -> 4
            ResonanceTier.RESONANT -> 1
            ResonanceTier.STRAINED -> -2
        }

        val partnerQuote = when (tier) {
            ResonanceTier.BREAKTHROUGH ->
                "\"Textbook ${activeCharacter.mbti} brilliance, ${activeCharacter.name}! Your ${activeCharacter.cognitiveStack} instincts just shaved three days off our calibration curve.\""
            ResonanceTier.RESONANT ->
                "\"Solid call, ${activeCharacter.name}. ${partner.name} nods as the chamber gauges stabilize within nominal tolerance.\""
            ResonanceTier.STRAINED ->
                "\"We forced it through, ${activeCharacter.name}, though working against your ${activeCharacter.cognitiveStack} grain definitely spiked the room's blood pressure!\""
        }

        return ChoiceResonanceEvaluation(
            optionId = option.id,
            resonancePercent = clampedScore,
            tier = tier,
            reasonLabel = reasons.take(2).joinToString(" • ").ifBlank { "${activeCharacter.mbti} Stack" },
            bonusProjectDelta = bonusProj,
            bonusStressDelta = bonusStress,
            bonusMoraleDelta = bonusMorale,
            predictedPartnerQuote = partnerQuote
        )
    }

    /**
     * Builds the full [ChamberAftermathReport] shown immediately inside the Chamber Modal after committing a choice.
     */
    fun buildAftermathReport(
        door: CorridorDoor3D,
        chosenOption: ChoiceGate3D,
        evaluation: ChoiceResonanceEvaluation,
        executedBy: EnclaveCharacter,
        relayedTo: EnclaveCharacter?,
        allCharacters: List<EnclaveCharacter>,
        targetProjectName: String,
        totalProjectDelta: Int,
        totalStressDelta: Int,
        totalMoraleDelta: Int,
        newlyUnlockedMilestones: List<ProjectMilestonePerk>,
        bondXpGained: Int = 0,
        duoBondTitle: String = "",
        newlyUnlockedMedals: List<String> = emptyList()
    ): ChamberAftermathReport {
        val partner = allCharacters.firstOrNull { it.id == door.leadNpcId && it.id != executedBy.id }
            ?: allCharacters.firstOrNull { it.id == executedBy.partnerNodeId }
            ?: allCharacters.first { it.id != executedBy.id }

        val enhancedNarrative = buildString {
            append(chosenOption.outcomeNarrative)
            when (evaluation.tier) {
                ResonanceTier.BREAKTHROUGH ->
                    append(" Because ${executedBy.name}'s ${executedBy.cognitiveStack} cognitive stack resonated at ${evaluation.resonancePercent}%, the intervention achieved a ★ Cognitive Breakthrough (+${evaluation.bonusProjectDelta}% bonus progress)!")
                ResonanceTier.RESONANT ->
                    append(" ${executedBy.name}'s ${executedBy.mbti} execution held steady at ${evaluation.resonancePercent}% resonance.")
                ResonanceTier.STRAINED ->
                    append(" Operating off-stack (${evaluation.resonancePercent}% resonance) required extra grit, stabilizing the chamber under heavy strain.")
            }
            if (door.archetype == DoorArchetype.TICKING_CRISIS) {
                append(" [⚠ CRISIS BULKHEAD DEFUSED BEFORE DETONATION!]")
            }
            if (door.archetype == DoorArchetype.SANCTUARY_LOUNGE) {
                append(" [🍵 SIBLING SYNERGY BUFF (+15% RESONANCE) ACTIVATED FOR NEXT CHAMBER!]")
            }
        }

        return ChamberAftermathReport(
            doorId = door.id,
            chamberTitle = door.chamberTitle,
            archetype = door.archetype,
            executedByCharacterName = executedBy.name,
            executedByMbti = executedBy.mbti,
            relayedToCharacterName = relayedTo?.name,
            chosenOptionTitle = chosenOption.title,
            resonancePercent = evaluation.resonancePercent,
            tier = evaluation.tier,
            outcomeNarrative = enhancedNarrative,
            partnerSpeakerName = partner.name,
            partnerSpeakerRole = "${partner.roleTitle} (${partner.mbti})",
            partnerReactionDialogue = evaluation.predictedPartnerQuote,
            batonPassQuote = chosenOption.batonPassQuote,
            totalProjectDelta = totalProjectDelta,
            targetProjectName = targetProjectName,
            totalStressDelta = totalStressDelta,
            totalMoraleDelta = totalMoraleDelta,
            wasCrisisDefused = door.archetype == DoorArchetype.TICKING_CRISIS,
            grantedSiblingSynergyBuff = door.archetype == DoorArchetype.SANCTUARY_LOUNGE,
            newlyUnlockedMilestones = newlyUnlockedMilestones,
            bondXpGained = bondXpGained,
            duoBondTitle = duoBondTitle,
            newlyUnlockedMedals = newlyUnlockedMedals
        )
    }
}
