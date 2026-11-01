package com.example.data.ai

import com.example.data.model.ChoiceGate3D
import com.example.data.model.EnclaveCharacter
import com.example.data.model.EnclaveSeedData
import com.example.data.model.FeastExchange

/**
 * Deterministic Psychometric Anti-Drift Engine for The Enclave of the 16.
 *
 * Ensures that across procedural AI DM generations, Choice Gate outcomes, Rashomon baton passes,
 * and Nightly Grand Feast debates, none of the 16 characters drift from their canonical:
 * 1. MBTI 4-Function Cognitive Stack (Dominant > Auxiliary > Tertiary > Inferior)
 * 2. Big Five (OCEAN) Quantitative Speech Cadence & Behavioral Thresholds
 * 3. Enneagram Core Fear / Desire & Lie Believed vs. Truth to Learn
 * 4. Dyadic Mentor-Prodigy Partner Node Friction
 * 5. Conditional Vex Override Inversion (strictly gated by isVexInverted / stress >= 80%)
 */
object PsychometricGuardrailEngine {

    data class CharacterVoiceLock(
        val characterId: String,
        val dominantFunctionRule: String,
        val oceanSpeechCadenceRule: String,
        val lexicalForbiddenPatterns: String,
        val activeStateDirective: String,
        val hudBadgeText: String
    )

    /**
     * Computes the deterministic voice, syntax, and behavioral lock for [char] based on their
     * Big Five (OCEAN) percentiles, MBTI stack, Enneagram, and current Vex Override state.
     */
    fun buildVoiceLock(char: EnclaveCharacter): CharacterVoiceLock {
        val b5 = char.bigFive
        val stackParts = char.cognitiveStack.split(">").map { it.trim() }
        val dominantFn = stackParts.getOrNull(0) ?: "Ti"
        val auxiliaryFn = stackParts.getOrNull(1) ?: "Ne"
        val inferiorFn = stackParts.getOrNull(3) ?: "Fe"

        val cadenceBuilder = StringBuilder()
        // Extraversion rules
        when {
            b5.extraversion <= 25 -> cadenceBuilder.append(
                "ULTRA-INTROVERTED (E:${b5.extraversion}%): Uses terse, economical, measured sentences. Never monologues or uses exclamation-heavy small talk. "
            )
            b5.extraversion >= 75 -> cadenceBuilder.append(
                "HIGH-EXTRAVERSION (E:${b5.extraversion}%): Energetic, verbally kinetic, addresses others directly by name, thinks out loud. "
            )
            else -> cadenceBuilder.append(
                "BALANCED-CADENCE (E:${b5.extraversion}%): Speaks with focused, situational clarity. "
            )
        }

        // Conscientiousness rules
        when {
            b5.conscientiousness >= 85 -> cadenceBuilder.append(
                "HYPER-CONSCIENTIOUS (C:${b5.conscientiousness}%): Cites exact units, tolerances, temperatures, and protocols. Irritated by mess or improvisation. "
            )
            b5.conscientiousness <= 30 -> cadenceBuilder.append(
                "LOW-CONSCIENTIOUSNESS IMPROVISER (C:${b5.conscientiousness}%): Skips manual steps, kitbashes chaotic shortcuts, leaves tools scattered. "
            )
        }

        // Openness rules
        if (b5.openness >= 85) {
            cadenceBuilder.append("HIGH-OPENNESS (O:${b5.openness}%): Uses cross-disciplinary metaphors and speculative leaps. ")
        } else if (b5.openness <= 25) {
            cadenceBuilder.append("CONCRETE-EMPIRICIST (O:${b5.openness}%): Trusts only physical stress-tests and proven metallurgy; distrusts abstract mysticism. ")
        }

        // Agreeableness rules
        if (b5.agreeableness <= 40) {
            cadenceBuilder.append("BLUNT-TRUTH (A:${b5.agreeableness}%): Prioritizes objective accuracy over politeness; affection is shown through fixing things, not flattery. ")
        } else if (b5.agreeableness >= 80) {
            cadenceBuilder.append("WARM-MEDIATOR (A:${b5.agreeableness}%): Protects group harmony and checks on others' physical/emotional exhaustion. ")
        }

        val activeStateDirective = if (char.isVexInverted) {
            "CRITICAL — VEX OVERRIDE ACTIVE (${char.vexStarSign} • ${char.vexOverrideTitle}): " +
                "Because magnetic flux and stress (${char.stressLevel}%) crossed the threshold, ${char.name}'s normal $dominantFn>$auxiliaryFn stack is INVERTED into their Inferior function ($inferiorFn) and shadow sign (${char.vexStarSign}): ${char.vexOverrideBehavior}"
        } else {
            "NORMAL BASELINE LOCK (Vex Override INACTIVE, Stress ${char.stressLevel}%): " +
                "${char.name} MUST operate strictly from Dominant [$dominantFn] supported by Auxiliary [$auxiliaryFn]. " +
                "Do NOT exhibit ${char.vexStarSign} shadow behavior unless Vex Override is active. " +
                "Subconsciously driven by Enneagram ${char.enneagram} lie: '${char.lieBelieved}'"
        }

        val forbidden = buildString {
            if (b5.extraversion <= 25 && !char.isVexInverted) {
                append("NEVER write bubbly, chatty, or hyper-excited dialogue for ${char.name}. ")
            }
            if (b5.conscientiousness >= 85 && !char.isVexInverted) {
                append("NEVER have ${char.name} endorse sloppy or uncalibrated guesswork. ")
            }
            if (!char.isVexInverted) {
                append("NEVER trigger ${char.vexStarSign} (${char.vexOverrideTitle}) traits while VexInverted is false.")
            }
        }

        val hudBadge = if (char.isVexInverted) {
            "⚡ INVERTED: ${char.vexStarSign} (${char.inferiorFnLabel(inferiorFn)})"
        } else {
            "🔒 ${char.mbti} (${dominantFn}>${auxiliaryFn}) • ${char.enneagram} • O:${b5.openness} C:${b5.conscientiousness} E:${b5.extraversion}"
        }

        return CharacterVoiceLock(
            characterId = char.id,
            dominantFunctionRule = "Dominant $dominantFn + Auxiliary $auxiliaryFn (Inferior $inferiorFn)",
            oceanSpeechCadenceRule = cadenceBuilder.toString().trim(),
            lexicalForbiddenPatterns = forbidden.trim(),
            activeStateDirective = activeStateDirective,
            hudBadgeText = hudBadge
        )
    }

    private fun EnclaveCharacter.inferiorFnLabel(inf: String): String = "${this.mbti} Shadow $inf"

    /**
     * Builds the complete multi-node psychometric contract block injected into AI DM prompts
     * so that the active POV character, their Partner Node, and all potential baton-pass targets
     * cannot drift in tone, vocabulary, or decision logic.
     */
    fun buildStrictPromptContract(
        activeChar: EnclaveCharacter,
        allCharacters: List<EnclaveCharacter> = EnclaveSeedData.initialCharacters
    ): String {
        val lock = buildVoiceLock(activeChar)
        val partner = allCharacters.firstOrNull { it.id == activeChar.partnerNodeId }
        val partnerLock = partner?.let { buildVoiceLock(it) }

        return """
            === HARDENED PSYCHOMETRIC ANTI-DRIFT CONTRACT (MANDATORY) ===
            1. ACTIVE POV NODE: ${activeChar.name} (ID: ${activeChar.id}, Role: ${activeChar.roleTitle})
               - MBTI & Stack: ${activeChar.mbti} (${activeChar.cognitiveStack}) | Enneagram: ${activeChar.enneagram} (${activeChar.archetype})
               - Big Five OCEAN: Openness=${activeChar.bigFive.openness}%, Conscientiousness=${activeChar.bigFive.conscientiousness}%, Extraversion=${activeChar.bigFive.extraversion}%, Agreeableness=${activeChar.bigFive.agreeableness}%, Neuroticism=${activeChar.bigFive.neuroticism}%
               - Speech & Syntax Cadence Lock: ${lock.oceanSpeechCadenceRule}
               - Cognitive State Directive: ${lock.activeStateDirective}
               - Forbidden Drift Patterns: ${lock.lexicalForbiddenPatterns}
               - Genius vs. Mundane Tension: ${activeChar.geniusVsMundane}
               - Core Psychological Lie: "${activeChar.lieBelieved}" -> Truth: "${activeChar.truthToLearn}"
            
            2. DYADIC PARTNER NODE: ${partner?.name ?: "Leo"} (ID: ${partner?.id ?: "leo"}, MBTI: ${partner?.mbti ?: "ENTJ"}, Stack: ${partner?.cognitiveStack ?: "Te > Ni > Se > Fi"})
               - Partner Cadence Lock: ${partnerLock?.oceanSpeechCadenceRule ?: "Commanding, structural Te-dominant mentor."}
               - Partner State: ${partnerLock?.activeStateDirective ?: "Normal baseline."}
            
            3. CHOICE GATE & BATON-PASS FIDELITY:
               - Gate 1 must reflect ${activeChar.name}'s Dominant cognitive function (${activeChar.cognitiveStack.substringBefore(">").trim()}) and high-precision specialty (${activeChar.scientificSpecialty}).
               - Gate 2 must challenge ${activeChar.name}'s Auxiliary/Tertiary function via cross-squad collaboration with ${partner?.name ?: "their partner"}.
                - Gate 3 must test ${activeChar.name}'s Enneagram ${activeChar.enneagram} core tension ("${activeChar.lieBelieved}").
                - Every 'batonPassQuote' MUST sound unmistakably like ${activeChar.name}'s OCEAN cadence (${lock.oceanSpeechCadenceRule}).
            
            ${NarrativeCalibrationEngine.buildBiomechanicalCalibrationPromptBlock(activeChar, solNumber = 1)}
        """.trimIndent()
    }

    /**
     * Builds the strict psychometric roster table for the Nightly Grand Feast so every speaker
     * at Ryn's table speaks in their exact MBTI + OCEAN + Enneagram voice.
     */
    fun buildGrandFeastRosterContract(
        characters: List<EnclaveCharacter> = EnclaveSeedData.initialCharacters
    ): String {
        val keyNodes = characters.filter {
            it.id in setOf("ryn", "silas", "leo", "kaio", "zara", "augusta", "vex", "kenna")
        }
        return buildString {
            appendLine("=== GRAND FEAST PSYCHOMETRIC VOICE LOCKS (ZERO DRIFT ALLOWED) ===")
            keyNodes.forEach { ch ->
                val lock = buildVoiceLock(ch)
                appendLine(
                    "- ${ch.name} (id='${ch.id}', ${ch.mbti} [${ch.cognitiveStack}], Enneagram ${ch.enneagram}, " +
                        "O:${ch.bigFive.openness} C:${ch.bigFive.conscientiousness} E:${ch.bigFive.extraversion} A:${ch.bigFive.agreeableness} N:${ch.bigFive.neuroticism}): " +
                        "${lock.oceanSpeechCadenceRule} | ${lock.activeStateDirective}"
                )
            }
        }
    }

    /**
     * Post-processes and sanitizes AI-generated Choice Gates to guarantee that:
     * - Next POV IDs are valid canonical characters
     * - Baton-pass quotes and gate outcomes carry the active character's psychometric signature
     *   and pass the 3-Pass De-AI Calibration filter.
     */
    fun enforceGateFidelity(
        activeChar: EnclaveCharacter,
        rawGates: List<ChoiceGate3D>,
        allCharacters: List<EnclaveCharacter> = EnclaveSeedData.initialCharacters
    ): List<ChoiceGate3D> {
        val validIds = allCharacters.map { it.id }.toSet()
        val stackFunctions = activeChar.cognitiveStack.split(">").map { it.trim() }

        return rawGates.mapIndexed { idx, gate ->
            val safeNextPov = if (gate.nextPovId in validIds && gate.nextPovId != activeChar.id) {
                gate.nextPovId
            } else {
                activeChar.partnerNodeId
            }
            val fnTag = when {
                gate.isVexGate -> "[VEX] ${activeChar.vexStarSign} Shadow"
                idx == 0 -> "${activeChar.mbti} Dom-${stackFunctions.getOrElse(0) { "Ti" }}"
                idx == 1 -> "${activeChar.mbti} Aux-${stackFunctions.getOrElse(1) { "Ne" }}"
                else -> "Enneagram ${activeChar.enneagram}"
            }
            val hardenedDomain = if (gate.scientificDomain.contains(activeChar.mbti) || gate.isVexGate) {
                gate.scientificDomain
            } else {
                "$fnTag • ${gate.scientificDomain}"
            }
            gate.copy(
                nextPovId = safeNextPov,
                scientificDomain = hardenedDomain,
                outcomeNarrative = NarrativeCalibrationEngine.calibrateProseThreePass(gate.outcomeNarrative),
                batonPassQuote = NarrativeCalibrationEngine.calibrateProseThreePass(gate.batonPassQuote)
            )
        }
    }

    /**
     * Post-processes and validates AI-generated Grand Feast exchanges against the canonical
     * 16-character registry and 3-Pass De-AI Calibration so speaker names, roles, squads,
     * MBTI tags, and dialogue lines never hallucinate or drift into AI-slop.
     */
    fun enforceFeastFidelity(
        rawExchanges: List<FeastExchange>,
        allCharacters: List<EnclaveCharacter> = EnclaveSeedData.initialCharacters
    ): List<FeastExchange> {
        val byId = allCharacters.associateBy { it.id.lowercase() }
        val byName = allCharacters.associateBy { it.name.lowercase() }

        return rawExchanges.map { ex ->
            val canonical = byId[ex.speakerId.lowercase()]
                ?: byName[ex.speakerName.lowercase()]
                ?: allCharacters.firstOrNull { ex.speakerName.contains(it.name, ignoreCase = true) }

            val calibratedDialogue = NarrativeCalibrationEngine.calibrateProseThreePass(ex.dialogue)
            if (canonical != null) {
                val stateTag = if (canonical.isVexInverted) {
                    "⚡ Vex-${canonical.vexStarSign}"
                } else {
                    "${canonical.mbti} (${canonical.cognitiveStack.substringBefore(">").trim()}-Dom)"
                }
                ex.copy(
                    speakerId = canonical.id,
                    speakerName = canonical.name,
                    roleTitle = "${canonical.roleTitle} • $stateTag",
                    squad = canonical.squad,
                    dialogue = calibratedDialogue
                )
            } else {
                ex.copy(dialogue = calibratedDialogue)
            }
        }
    }
}
