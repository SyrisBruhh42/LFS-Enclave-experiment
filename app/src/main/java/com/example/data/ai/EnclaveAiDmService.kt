package com.example.data.ai

import android.util.Log
import com.example.data.model.Biome3D
import com.example.data.model.ChoiceGate3D
import com.example.data.model.EnclaveCharacter
import com.example.data.model.EnclaveSeedData
import com.example.data.model.FeastExchange
import com.example.data.model.Squad
import com.example.data.model.Vec3
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val TAG = "EnclaveAiDmService"

data class ProceduralSceneResult(
    val sceneTitle: String,
    val narrativeProse: String,
    val scientificCheeseNote: String,
    val psychometricAuditNote: String,
    val gates: List<ChoiceGate3D>
)

class EnclaveAiDmService {

    private val sceneResponseSchema = Schema.obj(
        properties = mapOf(
            "psychometricAudit" to Schema.string(
                description = "Brief verification of how the active POV character's MBTI cognitive stack, Big 5 OCEAN speech cadence, and Enneagram lie govern this scene without personality drift"
            ),
            "sceneTitle" to Schema.string(description = "Title of the scientific anomaly or family dilemma"),
            "narrativeProse" to Schema.string(description = "2-3 vivid paragraphs strictly locked to the active POV character's MBTI cognitive stack and Big 5 OCEAN cadence ('Vegetables Covered in Cheese')"),
            "scientificCheeseNote" to Schema.string(description = "1-sentence hard-science explanation of the physics/biology behind the scene"),
            "gates" to Schema.array(
                items = Schema.obj(
                    properties = mapOf(
                        "title" to Schema.string(description = "Short 2-4 word label for the 3D Choice Gate portal"),
                        "subtitle" to Schema.string(description = "Action description when walking through this gate"),
                        "scientificDomain" to Schema.string(description = "Hard science field + MBTI cognitive function involved"),
                        "squadId" to Schema.enumeration(
                            values = listOf("forge", "wilds", "mind", "hub"),
                            description = "Squad alignment of this gate"
                        ),
                        "projectId" to Schema.enumeration(
                            values = listOf(
                                "living_atlas",
                                "first_forge",
                                "mycelial_concordance",
                                "sanctuary",
                                "genesis_garden",
                                "stormbreaker",
                                "simulacrum",
                                "lyceum_mind"
                            ),
                            description = "Grand project impacted"
                        ),
                        "projectDelta" to Schema.integer(description = "Progress boost between 5 and 15"),
                        "stressDelta" to Schema.integer(description = "Stress change between -12 and 15"),
                        "moraleDelta" to Schema.integer(description = "Morale change between 2 and 12"),
                        "nextPovId" to Schema.enumeration(
                            values = listOf(
                                "silas", "leo", "kaio", "zara", "augusta", "cassia",
                                "ryn", "vex", "hector", "jane", "kenna", "elias",
                                "willow", "terra", "isla", "orion"
                            ),
                            description = "Next character ID in the Rashomon POV relay"
                        ),
                        "nextBiomeId" to Schema.enumeration(
                            values = listOf("arboretum", "stormbreaker", "forge_hall", "atlas_chamber", "deep_aquifer"),
                            description = "3D biome ID for the next scene"
                        ),
                        "outcomeNarrative" to Schema.string(description = "What happens immediately when walking through this 3D gate"),
                        "batonPassQuote" to Schema.string(description = "Spoken dialogue line matching the character's exact OCEAN Extraversion/Conscientiousness cadence")
                    )
                ),
                description = "Exactly 3 distinct 3D Choice Gates for the player to walk through"
            )
        )
    )

    private val feastResponseSchema = Schema.obj(
        properties = mapOf(
            "dishOfTheNight" to Schema.string(description = "Ryn's gastrophysics dish and Maillard/flavor-pairing note"),
            "exchanges" to Schema.array(
                items = Schema.obj(
                    properties = mapOf(
                        "speakerId" to Schema.enumeration(
                            values = listOf(
                                "silas", "leo", "kaio", "zara", "augusta", "cassia",
                                "ryn", "vex", "hector", "jane", "kenna", "elias",
                                "willow", "terra", "isla", "orion"
                            )
                        ),
                        "speakerName" to Schema.string(),
                        "roleTitle" to Schema.string(),
                        "squadId" to Schema.enumeration(values = listOf("forge", "wilds", "mind", "hub")),
                        "dialogue" to Schema.string(description = "In-character dialogue strictly adhering to the speaker's Big 5 OCEAN & MBTI stack"),
                        "scientificCheeseNote" to Schema.string(description = "Psychometric & hard-science subtext of this line")
                    )
                ),
                description = "4 witty dinner table exchanges at The Grand Feast"
            )
        )
    )

    // CRITICAL: Lazy initialization of Firebase.ai.generativeModel per skill rules
    private val sceneModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = sceneResponseSchema
            }
        )
    }

    private val feastModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = feastResponseSchema
            }
        )
    }

    suspend fun generateProceduralScene(
        activeChar: EnclaveCharacter,
        biome: Biome3D,
        dayNumber: Int,
        relayStep: Int,
        magneticFlux: Int,
        previousOutcomeSummary: String,
        allCharacters: List<EnclaveCharacter> = EnclaveSeedData.initialCharacters
    ): Result<ProceduralSceneResult> {
        val psychometricContract = PsychometricGuardrailEngine.buildStrictPromptContract(
            activeChar = activeChar,
            allCharacters = allCharacters
        )

        val prompt = """
            You are the Autonomous DM Engine for 'The Enclave of the 16', a hard sci-fi + cozy found-family 3D Choice Gate sandbox.
            Current Day: $dayNumber | Rashomon Relay Step: $relayStep of 3
            Current 3D Biome: ${biome.title} (${biome.subtitle}) | Twin-Sun Magnetic Flux: $magneticFlux%
            Previous Relay Handoff: $previousOutcomeSummary
            
            $psychometricContract
            
            Rules from the System Bible:
            1. ZERO PERSONALITY DRIFT: First complete 'psychometricAudit' verifying ${activeChar.name}'s MBTI stack (${activeChar.cognitiveStack}), Big 5 OCEAN cadence, and Enneagram ${activeChar.enneagram}. Never blend voices across nodes.
            2. Strictly PvE Sanctuary: No human villains or betrayals. All 16 nodes love each other fiercely while arguing with PhD-level competence and petty sibling humor.
            3. "Vegetables Covered in Cheese": Wrap real hard science in affectionate banter matching ${activeChar.name}'s exact speech cadence.
            4. Generate 3 distinct 3D Choice Gates (Dominant Function gate, Partner Synergy gate, Enneagram Growth gate), ending with a Rashomon baton pass to the next character.
        """.trimIndent()

        return try {
            val response = withTimeout(60_000L) {
                sceneModel.generateContent(prompt)
            }
            val jsonText = response.text ?: throw IllegalStateException("AI DM returned empty response.")
            val root = JSONObject(jsonText)
            val psychometricAudit = root.optString(
                "psychometricAudit",
                PsychometricGuardrailEngine.buildVoiceLock(activeChar).hudBadgeText
            )
            val sceneTitle = root.getString("sceneTitle")
            val narrativeProse = root.getString("narrativeProse")
            val scientificCheeseNote = root.getString("scientificCheeseNote")
            val gatesArray = root.getJSONArray("gates")

            val positions = listOf(
                Vec3(-5.2f, 0f, 13.5f),
                Vec3(0f, 0f, 15.5f),
                Vec3(5.2f, 0f, 13.5f)
            )

            val parsedGates = mutableListOf<ChoiceGate3D>()
            for (i in 0 until gatesArray.length().coerceAtMost(3)) {
                val g = gatesArray.getJSONObject(i)
                parsedGates.add(
                    ChoiceGate3D(
                        id = "ai_gate_${dayNumber}_${relayStep}_$i",
                        title = g.getString("title"),
                        subtitle = g.getString("subtitle"),
                        scientificDomain = g.getString("scientificDomain"),
                        squadAlignment = Squad.fromId(g.getString("squadId")),
                        position = positions.getOrElse(i) { Vec3(0f, 0f, 14f) },
                        isVexGate = false,
                        projectId = g.getString("projectId"),
                        projectDelta = g.optInt("projectDelta", 8).coerceIn(4, 18),
                        stressDelta = g.optInt("stressDelta", 4).coerceIn(-20, 20),
                        moraleDelta = g.optInt("moraleDelta", 5).coerceIn(-10, 15),
                        nextPovId = g.getString("nextPovId"),
                        nextBiomeId = g.getString("nextBiomeId"),
                        outcomeNarrative = g.getString("outcomeNarrative"),
                        batonPassQuote = g.getString("batonPassQuote")
                    )
                )
            }

            // Deterministic post-generation psychometric guardrail validation
            val validatedGates = PsychometricGuardrailEngine.enforceGateFidelity(
                activeChar = activeChar,
                rawGates = parsedGates,
                allCharacters = allCharacters
            )

            // If character is in Vex Override or high stress, append the 4th Vex Override gate in the center-front
            val finalGates = if (activeChar.isVexInverted || activeChar.stressLevel >= 75) {
                validatedGates + ChoiceGate3D(
                    id = "ai_vex_gate_${dayNumber}_$relayStep",
                    title = "VEX OVERRIDE: ${activeChar.vexOverrideTitle}",
                    subtitle = "[${activeChar.vexStarSign} Inversion] ${activeChar.vexOverrideBehavior.take(65)}...",
                    scientificDomain = "⚡ ${activeChar.vexStarSign} Shadow Inversion",
                    squadAlignment = Squad.MIND,
                    position = Vec3(0f, 0f, 9.5f),
                    isVexGate = true,
                    projectId = activeChar.grandProjectId,
                    projectDelta = 14,
                    stressDelta = -35,
                    moraleDelta = 7,
                    nextPovId = activeChar.partnerNodeId,
                    nextBiomeId = biome.id,
                    outcomeNarrative = "VEX OVERRIDE! Under twin-sun magnetic cross-polarity, ${activeChar.name} flips into ${activeChar.vexStarSign} (${activeChar.vexOverrideTitle}) and executes an unorthodox breakthrough!",
                    batonPassQuote = "\"Don't look at me like that—I just optimized the variable!\""
                )
            } else {
                validatedGates
            }

            Result.success(
                ProceduralSceneResult(
                    sceneTitle = sceneTitle,
                    narrativeProse = narrativeProse,
                    scientificCheeseNote = scientificCheeseNote,
                    psychometricAuditNote = psychometricAudit,
                    gates = finalGates.ifEmpty {
                        EnclaveSeedData.buildGatesForCharacter(activeChar, dayNumber, relayStep)
                    }
                )
            )
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "AI DM scene generation timed out after 60s", e)
            Result.failure(Exception("AI DM Engine timed out after 60 seconds. Please retry or walk through a current Choice Gate."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "AI DM scene generation failed", e)
            Result.failure(e)
        }
    }

    suspend fun generateGrandFeastBanter(
        dayNumber: Int,
        recentChronicleSummary: String,
        detonatedPrankSummary: String?,
        allCharacters: List<EnclaveCharacter> = EnclaveSeedData.initialCharacters
    ): Result<Pair<String, List<FeastExchange>>> {
        val rosterContract = PsychometricGuardrailEngine.buildGrandFeastRosterContract(allCharacters)
        val prompt = """
            Generate the Nightly Grand Feast for Day $dayNumber of 'The Enclave of the 16'.
            Today's Rashomon Relay events: $recentChronicleSummary
            Black Market Prank Detonated Tonight: ${detonatedPrankSummary ?: "None tonight, just grounded copper spoons."}
            
            $rosterContract
            
            Ensure each speaker's dialogue strictly obeys their Big 5 OCEAN Extraversion/Conscientiousness cadence and MBTI dominant function without any personality drift.
        """.trimIndent()

        return try {
            val response = withTimeout(60_000L) {
                feastModel.generateContent(prompt)
            }
            val jsonText = response.text ?: throw IllegalStateException("Empty Feast response.")
            val root = JSONObject(jsonText)
            val dish = root.getString("dishOfTheNight")
            val arr = root.getJSONArray("exchanges")
            val rawList = (0 until arr.length()).map { i ->
                val item = arr.getJSONObject(i)
                FeastExchange(
                    speakerId = item.getString("speakerId"),
                    speakerName = item.getString("speakerName"),
                    roleTitle = item.getString("roleTitle"),
                    squad = Squad.fromId(item.getString("squadId")),
                    dialogue = item.getString("dialogue"),
                    scientificCheeseNote = item.getString("scientificCheeseNote")
                )
            }
            val validatedList = PsychometricGuardrailEngine.enforceFeastFidelity(rawList, allCharacters)
            Result.success(dish to validatedList)
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Grand Feast generation timed out", e)
            Result.failure(Exception("Grand Feast AI generation timed out after 60 seconds."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Grand Feast generation failed", e)
            Result.failure(e)
        }
    }
}
