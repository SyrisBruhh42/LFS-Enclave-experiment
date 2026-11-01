package com.example.data.ai

import com.example.data.model.EnclaveCharacter
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * Cross-Medium Narrative Architecture & Computational Prose Calibration Engine (Kotlin / Android).
 *
 * Mirrors `engine/narrative_calibration_engine.py` in `3-lfs-aegis-linux/capsules/enclave-digital-twin`:
 * 1. Biomechanical Narrative Propulsion:
 *    - Willingham's Four Cs (Causality, Conflict, Complications, Character)
 *    - Andrew Stanton's "2 + 2" Unifying Theory (anchored to Character Spine; withholds "= 4")
 *    - Trey Parker & Matt Stone's "But and Therefore" Rule (bans "and then" logs)
 *    - Dwight Swain's Scene (Goal -> Conflict -> Disaster [FLAT_NO | YES_BUT | NO_AND_FURTHERMORE | INVERTED_DISASTER])
 *      <-> Sequel (Reaction -> Dilemma [Coyne's Best Bad Choice] -> Decision) & Micro MRUs.
 * 2. Dramatic Compression & Eradication of Info-Dumping:
 *    - David Mamet's 3-Question Litmus Gate (Who wants what? What happens if not? Why now?)
 *    - Three Structural Danger Signals & The Silent Movie Constraint.
 * 3. Temporal Information Asymmetry:
 *    - Hitchcock's "Bomb Under the Table" Dramatic Irony Suspense
 *    - Ronald Knox's 10 Commandments of Fair Play (Commandment VIII Camouflaged Flourishing)
 *    - Agatha Christie's 5-Step Backward Revision Foreshadowing & 5 Camouflage Tactics.
 * 4. Orality, Vulnerability & Pedagogical Pacing:
 *    - Ira Glass (Anecdote with Bait + Moment of Reflection)
 *    - Jad Abumrad (47% Gut Churn Index + No Plexi-Glass specialist doubt)
 *    - Dan Carlin (Multi-Perspective Source Interrogation + Visceral Extremes).
 * 5. Emergent Interactive Topology:
 *    - Justin Alexander's Situation Toolbox + Inverted Three-Clue Rule (in-degree >= 3)
 *    - Raymond Chandler's Door-Kick + Brennan Lee Mulligan's Hard Scene Framing.
 * 6. Prose Mechanics & 3-Pass De-AI Calibration (StoryScope / Sepia / Hermes / Stop-Slop):
 *    - Chuck Palahniuk's Thought-Verb Unpacking (submerging abstract mental states into action)
 *    - Ursula K. Le Guin's Acoustic Rhythm & Sentence-Length Spread (sigma >= 5.0 words)
 *    - 3-Pass De-AI Protocol & Quantitative 5-Dimension Stop-Slop Scorer (>= 35 / 50).
 */
object NarrativeCalibrationEngine {

    enum class DisasterVariant {
        FLAT_NO,
        YES_BUT,
        NO_AND_FURTHERMORE,
        INVERTED_DISASTER
    }

    enum class CamouflageTactic {
        BURY_IN_TEXTURE,
        PAIR_WITH_EMOTION,
        DISGUISE_AS_CHARACTER_BUSINESS,
        PLACE_ADJACENT_TO_RED_HERRING,
        DUAL_MEANING
    }

    data class StopSlopAudit(
        val directness: Int,
        val rhythm: Int,
        val trust: Int,
        val authenticity: Int,
        val density: Int,
        val totalScore: Int,
        val passed: Boolean,
        val sentenceStdDev: Double,
        val slopHits: Int
    )

    private val prohibitedReplacements: List<Pair<Regex, String>> = listOf(
        // Pass 1: Moralizing tail / theme-explaining suffixes (Hemingway Iceberg omission)
        Regex("""\s*(?:It was a (?:stark |quiet )?reminder that|In the end,?\s+they realized that|A testament to their resilience[^.]*\.)\s*$""", RegexOption.IGNORE_CASE) to "",
        // Pass 2: Rhetorical Question -> Immediate Self-Answer discourse template
        Regex("""\b(?:Why\?\s*Because|Did it matter\?\s*Not when|Was it dangerous\?\s*Certainly,?\s*but)\s+""", RegexOption.IGNORE_CASE) to "",
        // Pass 3: Pattern 30 (explaining a metaphor immediately after deploying it)
        Regex(""",\s*a\s+(?:stark\s+|vivid\s+)?(?:symbol|metaphor|testament|reminder)\s+of\s+[^,.]+(?=[,.])""", RegexOption.IGNORE_CASE) to "",
        // Pass 3: Pattern 31 (staccato subjectless mic-drop poster fragments)
        Regex("""\s+(?:Pure silence\.\s*Nothing more\.|Full stop\.|No turning back now\.)\s*$""", RegexOption.IGNORE_CASE) to "",
        // Pass 3: Prohibited AI-isms & stock clichés
        Regex("""\bdelve(?:d|s|ing)?\s+into\b""", RegexOption.IGNORE_CASE) to "probed",
        Regex("""\ba\s+rich\s+tapestry\s+of\b""", RegexOption.IGNORE_CASE) to "a braided lattice of",
        Regex("""\btapestry\b""", RegexOption.IGNORE_CASE) to "lattice",
        Regex("""\btestament\s+to\b""", RegexOption.IGNORE_CASE) to "proof of",
        Regex("""\bin\s+the\s+realm\s+of\b""", RegexOption.IGNORE_CASE) to "within",
        Regex("""\bcacophony\b""", RegexOption.IGNORE_CASE) to "clatter",
        Regex("""\bunwavering\b""", RegexOption.IGNORE_CASE) to "steady",
        Regex("""\bparamount\b""", RegexOption.IGNORE_CASE) to "critical",
        Regex("""\bpivotal\b""", RegexOption.IGNORE_CASE) to "load-bearing",
        Regex("""\bshivers\s+down\s+(?:his|her|their)\s+spine\b""", RegexOption.IGNORE_CASE) to "a cold prickle across the neck",
        Regex("""\ba\s+lump\s+in\s+(?:his|her|their)\s+throat\b""", RegexOption.IGNORE_CASE) to "a dry catch in the glottis",
        Regex("""\bcould(?:n't|\s+not)\s+help\s+but\s+feel\b""", RegexOption.IGNORE_CASE) to "registered",
        Regex("""\bbarely\s+above\s+a\s+whisper\b""", RegexOption.IGNORE_CASE) to "under the vent hum",
        Regex("""\bever\s+so\s+slightly\b""", RegexOption.IGNORE_CASE) to "by two millimeters",
        Regex("""\b(inexorably|languidly|fervently|coyly|knowingly|dreamily)\b""", RegexOption.IGNORE_CASE) to ""
    )

    private val slopDetector = Regex(
        """\b(tapestry|testament|delve|delved|cacophony|unwavering|paramount|showcasing|inexorably|languidly|fervently|coyly|dreamily)\b|(shivers down (?:his|her|their) spine|lump in (?:his|her|their) throat|couldn't help but feel|barely above a whisper|ever so slightly|Why\?\s*Because)""",
        RegexOption.IGNORE_CASE
    )

    private val thoughtVerbDetector = Regex(
        """\b(realized|believed|imagined|desired|remembered|wondered)\b""",
        RegexOption.IGNORE_CASE
    )

    /**
     * Runs the 3-Pass De-AI Calibration pipeline on [rawText].
     */
    fun calibrateProseThreePass(rawText: String): String {
        var text = rawText.trim()
        if (text.isEmpty()) return ""
        for ((pattern, replacement) in prohibitedReplacements) {
            text = pattern.replace(text, replacement)
        }
        text = text.replace(Regex("""\s{2,}"""), " ")
        text = text.replace(Regex("""\s+([,.;:!?])"""), "$1").trim()
        return text
    }

    /**
     * Computes the 5-Dimension Quantitative Stop-Slop Audit (Directness, Rhythm, Trust, Authenticity, Density).
     * Requires totalScore >= 35 / 50 to pass.
     */
    fun scoreStopSlop5D(text: String): StopSlopAudit {
        val clean = text.trim()
        if (clean.isEmpty()) {
            return StopSlopAudit(1, 1, 1, 1, 1, 5, false, 0.0, 0)
        }
        val slopHits = slopDetector.findAll(clean).count()
        val thoughtHits = thoughtVerbDetector.findAll(clean).count()
        val sentences = clean.split(Regex("""(?<=[.!?])\s+""")).map { it.trim() }.filter { it.isNotEmpty() }
        val wordLengths = sentences.map { s ->
            Regex("""[A-Za-z0-9'-]+""").findAll(s).count()
        }.filter { it > 0 }

        val stdDev = if (wordLengths.size > 1) {
            val mean = wordLengths.average()
            sqrt(wordLengths.map { (it - mean) * (it - mean) }.average())
        } else {
            0.0
        }

        val hedges = Regex("""\b(somewhat|arguably|it is worth noting|needless to say|sort of)\b""", RegexOption.IGNORE_CASE)
            .findAll(clean).count()
        val directness = (9 - hedges * 2 - if (slopHits > 0) 1 else 0).coerceIn(1, 10)
        val rhythm = when {
            wordLengths.size <= 1 -> 7
            stdDev >= 5.5 -> 9
            stdDev >= 4.0 -> 8
            stdDev >= 2.5 -> 6
            else -> 4
        }
        val trustPenalties = Regex("""(Why\?\s*Because|which symboli[sz]ed|a reminder that|teaching them that)""", RegexOption.IGNORE_CASE)
            .findAll(clean).count()
        val trust = (9 - trustPenalties * 3).coerceIn(1, 10)
        val authenticity = (9 - slopHits * 3 - thoughtHits).coerceIn(1, 10)

        val allWords = Regex("""[A-Za-z0-9'-]+""").findAll(clean).map { it.value }.toList()
        val specificCount = allWords.count { w -> w.length >= 6 || w.any { it.isDigit() } }
        val ratio = specificCount.toDouble() / allWords.size.coerceAtLeast(1).toDouble()
        val density = ((5.0 + (ratio * 14.0).coerceAtMost(4.5)).roundToInt() - if (slopHits > 1) 1 else 0).coerceIn(1, 10)

        val total = directness + rhythm + trust + authenticity + density
        return StopSlopAudit(
            directness = directness,
            rhythm = rhythm,
            trust = trust,
            authenticity = authenticity,
            density = density,
            totalScore = total,
            passed = total >= 35,
            sentenceStdDev = (stdDev * 100.0).roundToInt() / 100.0,
            slopHits = slopHits
        )
    }

    /**
     * Builds the Split-Model Biomechanical & Computational Prose Calibration contract block
     * injected into AI DM and Choice Gate prompts.
     */
    fun buildBiomechanicalCalibrationPromptBlock(
        activeChar: EnclaveCharacter,
        solNumber: Int
    ): String {
        val disasterVariant = listOf(
            DisasterVariant.FLAT_NO,
            DisasterVariant.YES_BUT,
            DisasterVariant.NO_AND_FURTHERMORE
        )[solNumber.coerceAtLeast(1) % 3]
        val camouflage = CamouflageTactic.entries[solNumber.coerceAtLeast(1) % CamouflageTactic.entries.size]

        return """
            === CROSS-MEDIUM NARRATIVE ARCHITECTURE & STOP-SLOP CALIBRATION (MANDATORY) ===
            1. BIOMECHANICAL PROPULSION (PARKER-STONE 'BUT/THEREFORE' & SWAIN SCENE/SEQUEL):
               - NEVER connect story beats with chronological "and then" logs.
               - Proactive Scene: Immediate Concrete Goal -> Escalating Conflict -> Disaster ($disasterVariant).
               - Bridge via macro "BUT" into Reactive Sequel: Visceral Physical Reaction -> Shawn Coyne Best-Bad-Choice Dilemma (two costly options) -> Decision.
               - Bridge via macro "THEREFORE" into the Baton-Pass Handoff to ${activeChar.partnerNodeId}.
               - Enforce Micro-MRU paragraph order: External Sensory Stimulus -> Visceral Reflex -> Physical Action -> Terse Speech.
            2. DAVID MAMET'S 3-QUESTION LITMUS GATE & SILENT MOVIE CONSTRAINT:
               - Every beat must answer: (1) Who wants what? (2) What happens if denied? (3) Why now?
               - Ban Outline Logline "discussions", Third-Party Triad gossip, and "As you know, Bob" exposition.
               - Convey ${activeChar.name}'s PhD expertise (${activeChar.scientificSpecialty}) through physical tool manipulation.
            3. ANDREW STANTON '2 + 2' & RONALD KNOX COMMANDMENT VIII ($camouflage):
               - Supply two concrete sensory clues ("2 + 2") anchored to ${activeChar.name}'s spine ("${activeChar.lieBelieved}" vs. "${activeChar.truthToLearn}"), but WITHHOLD the explicit "= 4" synthesis.
               - Plant at least one fair-play clue using $camouflage; maintain Alexander's Inverted Three-Clue Rule (>= 3 clue vectors per discovery node).
            4. 3-PASS DE-AI CALIBRATION & STOP-SLOP SCORE (>= 35/50):
               - Unpack Chuck Palahniuk thought-verbs (realized, knew, felt, believed) into physical courtroom evidence.
               - Maintain Ursula K. Le Guin sentence-length variance (sigma >= 5.0 words: contrast winding technical clauses with 3-5 word percussive beats).
               - Pass 1 (Hemingway Iceberg): Zero moralizing or theme-explaining endings.
               - Pass 2 (Discourse Flow): Zero "Why? Because..." rhetorical Q&A templates.
               - Pass 3 (Surface Lexical Purge): Zero Pattern 30 metaphor explanations, zero Pattern 31 mic-drop fragments, and zero banned AI-isms (tapestry, testament, delve, cacophony, unwavering, shivers down spine).
        """.trimIndent()
    }
}
