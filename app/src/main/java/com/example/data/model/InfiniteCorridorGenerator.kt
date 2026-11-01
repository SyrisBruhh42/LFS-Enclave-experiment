package com.example.data.model

import kotlin.math.abs

/**
 * Procedural Infinite Corridor & Strategic Chamber Door Generator.
 *
 * Guarantees that as the player walks down the enclosed Enclave Hallway (+Z axis),
 * new Chamber Doors continuously spawn on the Left and Right walls forever across four
 * strategic archetypes ([DoorArchetype.STANDARD_LAB], [DoorArchetype.TICKING_CRISIS],
 * [DoorArchetype.COGNITIVE_VAULT], [DoorArchetype.SANCTUARY_LOUNGE]), while strictly
 * checking [usedScenarioSignatures] so no choice scenario is ever reused.
 */
object InfiniteCorridorGenerator {

    const val CORRIDOR_HALF_WIDTH = 3.7f
    const val DOOR_SPACING_Z = 8.0f
    const val FIRST_DOOR_Z = 7.5f
    const val LOOKAHEAD_DISTANCE_Z = 34.0f

    private val cognitiveFunctions = listOf("Ti", "Ni", "Te", "Ne", "Si", "Se", "Fi", "Fe")

    private data class CanonicalChamberTemplate(
        val signature: String,
        val leadCharId: String,
        val chamberTitle: String,
        val chamberSubtitle: String,
        val scientificDomain: String,
        val biomeTheme: Biome3D,
        val dilemmaProse: String,
        val scienceNote: String,
        val preferredArchetype: DoorArchetype = DoorArchetype.STANDARD_LAB,
        val requiredFunction: String? = null,
        val requiredProjectId: String? = null
    )

    private val canonicalTemplates: List<CanonicalChamberTemplate> = listOf(
        CanonicalChamberTemplate(
            signature = "canon_silas_galvanic_bay",
            leadCharId = "silas",
            chamberTitle = "Galvanic Conduit Junction",
            chamberSubtitle = "Fe-Co-Cr-Ni-Mn Pipe vs. Copper Flange Oxidation",
            scientificDomain = "High-Entropy Metallurgy",
            biomeTheme = Biome3D.ARBORETUM,
            dilemmaProse = "Inside this humid service bay, a five-element high-entropy alloy irrigation main is weeping a single bead of galvanic rust where someone bolted a bare copper flange. Leo leans against the bulkhead grinning while Terra holds out a jar of green botanical epoxy-sap.",
            scienceNote = "Dissimilar metals in an electrolyte bath form a galvanic cell; isolating the junction or shifting the anode potential halts corrosion.",
            preferredArchetype = DoorArchetype.STANDARD_LAB
        ),
        CanonicalChamberTemplate(
            signature = "canon_augusta_sector4_annex",
            leadCharId = "augusta",
            chamberTitle = "Sector 4 Telemetry Annex",
            chamberSubtitle = "⚠ 50 kV/m Crustal Magnetic Tear Surge",
            scientificDomain = "Magnetohydrodynamic Shielding",
            biomeTheme = Biome3D.ATLAS_CHAMBER,
            dilemmaProse = "URGENT CRISIS: Augusta stands over a holo-projector where Sector 4's magnetic field lines are twisting into a braided knot. Every metal instrument in the room is humming at 60 Hz as static charges climb toward an imminent atmospheric discharge!",
            scienceNote = "Rapid magnetic pole wander induces geomagnetically induced currents (GICs) in long metallic conductors unless shunted to bedrock.",
            preferredArchetype = DoorArchetype.TICKING_CRISIS
        ),
        CanonicalChamberTemplate(
            signature = "canon_hector_femtosecond_vault",
            leadCharId = "hector",
            chamberTitle = "Stormbreaker Capacitor Vault",
            chamberSubtitle = "🔒 Precision Laser-Plasma Interlock",
            scientificDomain = "Femtosecond Terawatt Optics",
            biomeTheme = Biome3D.STORMBREAKER,
            dilemmaProse = "COGNITIVE VAULT: The ceiling iris opens to bruised violet clouds. Hector's hand hovers over the manual firing key—locked behind a high-precision Lorenz attractor interlock—while 11-year-old Jane offers to patch the targeting loop.",
            scienceNote = "Ultrashort laser pulses ionize air molecules into a conductive plasma filament, guiding lightning strikes safely into storage geodes.",
            preferredArchetype = DoorArchetype.COGNITIVE_VAULT,
            requiredFunction = "Ti",
            requiredProjectId = "first_forge"
        ),
        CanonicalChamberTemplate(
            signature = "canon_kenna_schumann_suite",
            leadCharId = "kenna",
            chamberTitle = "Schumann Decompression Sanctuary",
            chamberSubtitle = "🍵 7.83 Hz Pineal Recoupling & Sibling Lounge",
            scientificDomain = "Magnetoreceptive Neurobiology",
            biomeTheme = Biome3D.ATLAS_CHAMBER,
            dilemmaProse = "SANCTUARY ALCOVE: Helmholtz coils line the walls of this quiet sanctuary, humming at Earth's native 7.83 Hz electromagnetic heartbeat. Kenna andChef Ryn have set out warm adaptogenic moss tea and caramelized tuber tarts so exhausted geniuses can decompress together.",
            scienceNote = "Human cryptochrome flavoproteins and circadian rhythms evolved within Earth's 7.83 Hz ionospheric cavity resonance.",
            preferredArchetype = DoorArchetype.SANCTUARY_LOUNGE
        ),
        CanonicalChamberTemplate(
            signature = "canon_kaio_luciferin_airlock",
            leadCharId = "kaio",
            chamberTitle = "Xenobiology Quarantine Lock",
            chamberSubtitle = "Prime-Number Bioluminescent Spore Specimen",
            scientificDomain = "Luciferin Bio-Photons",
            biomeTheme = Biome3D.ARBORETUM,
            dilemmaProse = "Through the reinforced viewport, a hex-pedal specimen and a cluster of Mycena lucentipes fungi are pulsing light in 2-3-5-7 prime-number intervals. Kaio has his hand on the polarization dial while Zara steadies a non-lethal biopsy dart.",
            scienceNote = "Enzymatic oxidation of luciferin by luciferase emits cold 490nm light with near-100% quantum efficiency.",
            preferredArchetype = DoorArchetype.STANDARD_LAB
        ),
        CanonicalChamberTemplate(
            signature = "canon_leo_ferrofluid_quench",
            leadCharId = "leo",
            chamberTitle = "Ferrofluid Quench Bulkhead",
            chamberSubtitle = "⚠ Rosensweig Instability & Geode Overpressure",
            scientificDomain = "Magnetorheological Fluids",
            biomeTheme = Biome3D.FORGE_HALL,
            dilemmaProse = "URGENT CRISIS: A bedrock storage geode is expanding rapidly under thermal load! Leo is tuning a ring of electromagnets around a collar of black nanoscale magnetite suspended in carrier oil, sculpting spiky Rosensweig patterns to cushion the shock before the seal blows.",
            scienceNote = "Under a variable magnetic field, ferrofluids transition from free-flowing liquid to a rigid viscoelastic seal in milliseconds.",
            preferredArchetype = DoorArchetype.TICKING_CRISIS
        ),
        CanonicalChamberTemplate(
            signature = "canon_isla_quartz_cleanroom",
            leadCharId = "isla",
            chamberTitle = "5D Optical Quartz Vault",
            chamberSubtitle = "🔒 Eternal Nanostructured Silica Archive",
            scientificDomain = "Femtosecond Laser Birefringence",
            biomeTheme = Biome3D.ATLAS_CHAMBER,
            dilemmaProse = "COGNITIVE VAULT: In a zero-dust laminar flow bay, Isla's femtosecond laser etches microscopic nanogratings into fused quartz discs—storing 360 terabytes of human and Keplerian science per disc for a billion years.",
            scienceNote = "Self-assembled nanogratings in fused silica encode 5 dimensions: 3 spatial coordinates plus slow-axis orientation and retardance.",
            preferredArchetype = DoorArchetype.COGNITIVE_VAULT,
            requiredFunction = "Ni",
            requiredProjectId = "simulacrum"
        ),
        CanonicalChamberTemplate(
            signature = "canon_ryn_maillard_kitchen",
            leadCharId = "ryn",
            chamberTitle = "The Obsessive Hearth Alcove",
            chamberSubtitle = "🍵 72°C Methional Broth & Grounded Copper Spoons",
            scientificDomain = "Molecular Gastrophysics",
            biomeTheme = Biome3D.FORGE_HALL,
            dilemmaProse = "SANCTUARY ALCOVE: Ryn's induction cauldrons stir clockwise at an exact 40 RPM, filling the alcove with the unmistakable aroma of buttered movie-theater popcorn. Siblings gather on warm brass benches to trade stories and vent corridor stress.",
            scienceNote = "Reaction between amino acids and reducing sugars above 140°C generates hundreds of savory pyrazine and thiophene flavor compounds.",
            preferredArchetype = DoorArchetype.SANCTUARY_LOUNGE
        ),
        CanonicalChamberTemplate(
            signature = "canon_vex_binary_orrery",
            leadCharId = "vex",
            chamberTitle = "Binary Magnetosphere Observatory",
            chamberSubtitle = "Red Giant & Blue Dwarf Cross-Polarity Horoscope",
            scientificDomain = "Binary Stellar Astrophysics",
            biomeTheme = Biome3D.STORMBREAKER,
            dilemmaProse = "Vex lounges beneath a spinning brass-and-hologram orrery tracking the exact minute the Red Giant's solar wind collides with the Blue Dwarf's magnetotail—insisting his 'Twin-Sun Horoscopes' predict crew behavior better than Kenna's charts.",
            scienceNote = "Binary star magnetic reconnection events compress planetary magnetospheres and alter low-frequency ionospheric resonances.",
            preferredArchetype = DoorArchetype.STANDARD_LAB
        ),
        CanonicalChamberTemplate(
            signature = "canon_jane_lorenz_alcove",
            leadCharId = "jane",
            chamberTitle = "Chaos Compiler Server Vault",
            chamberSubtitle = "🔒 Non-Linear Weather & Sibling Grid Sniffer",
            scientificDomain = "Deterministic Chaos Theory",
            biomeTheme = Biome3D.ATLAS_CHAMBER,
            dilemmaProse = "COGNITIVE VAULT: Surrounded by glowing CRT and holographic monitors, Jane is simultaneously debugging the 5D quartz write-head latency and intercepting a rogue Black Market prank packet bouncing through the Enclave router.",
            scienceNote = "Lorenz strange attractors allow short-horizon trajectory prediction inside chaotic fluid and electrical systems.",
            preferredArchetype = DoorArchetype.COGNITIVE_VAULT,
            requiredFunction = "Ne",
            requiredProjectId = "stormbreaker"
        ),
        CanonicalChamberTemplate(
            signature = "canon_elias_syntax_archive",
            leadCharId = "elias",
            chamberTitle = "Bio-Linguistic Phonology Vault",
            chamberSubtitle = "Decoding the Forest's Calcium-Ion Verb Tenses",
            scientificDomain = "Computational Bio-Semiotics",
            biomeTheme = Biome3D.DEEP_AQUIFER,
            dilemmaProse = "Elias has mapped thousands of electrical action potentials from the valley's root network onto a 3D syntax tree, proving the fungal mycelium conjugates future-tense warnings before barometric drops.",
            scienceNote = "Fungal hyphae transmit electrical action potentials up to 50 Hz that cluster into zipfian word-length distributions.",
            preferredArchetype = DoorArchetype.STANDARD_LAB
        ),
        CanonicalChamberTemplate(
            signature = "canon_willow_hyphal_conservatory",
            leadCharId = "willow",
            chamberTitle = "Mycorrhizal Moss Sanctuary",
            chamberSubtitle = "🍵 Silver-Thread Hyphae & Bioluminescent Rest",
            scientificDomain = "Mycology & Symbiotic Networks",
            biomeTheme = Biome3D.ARBORETUM,
            dilemmaProse = "SANCTUARY ALCOVE: Barefoot on warm bioluminescent moss, Willow and Terra invite you to sit beneath the glowing canopy and listen to the valley's slow ecological pulse, melting away cognitive fatigue.",
            scienceNote = "Ectomycorrhizal networks exchange carbon, phosphorus, and stress-signaling allelochemicals across tree species.",
            preferredArchetype = DoorArchetype.SANCTUARY_LOUNGE
        ),
        CanonicalChamberTemplate(
            signature = "canon_terra_diazotrophic_bay",
            leadCharId = "terra",
            chamberTitle = "Genesis Hydro-Agronomy Bay",
            chamberSubtitle = "Iron-Tolerant Rhizobia & Vulcanizing Sap",
            scientificDomain = "Diazotrophic Phytochemistry",
            biomeTheme = Biome3D.ARBORETUM,
            dilemmaProse = "Crimson-leafed Kepler legumes climb to the ceiling vents. Terra is harvesting both nitrogen-fixed root nodules for dinner and tapping self-vulcanizing latex resin capable of sealing high-pressure steam pipes.",
            scienceNote = "Symbiotic rhizobia bacteria use nitrogenase enzymes with iron-molybdenum cofactors to cleave atmospheric N2 into bioavailable ammonia.",
            preferredArchetype = DoorArchetype.STANDARD_LAB
        ),
        CanonicalChamberTemplate(
            signature = "canon_zara_biopsy_armory",
            leadCharId = "zara",
            chamberTitle = "Perimeter Recon & Breach Locker",
            chamberSubtitle = "⚠ Piezoelectric Chitin & Basalt Fault Shift",
            scientificDomain = "Biomechanics & Field Telemetry",
            biomeTheme = Biome3D.DEEP_AQUIFER,
            dilemmaProse = "URGENT CRISIS: A subterranean tremor is straining the lower gorge anchors! Zara is spooling high-tensile spider-silk composite cable while inspecting a piezoelectric predator carapace under polarized light.",
            scienceNote = "Biological chitin-protein helicoidal Bouligand structures deflect impact energy and generate piezoelectric surface charges.",
            preferredArchetype = DoorArchetype.TICKING_CRISIS
        ),
        CanonicalChamberTemplate(
            signature = "canon_cassia_piezo_shaft",
            leadCharId = "cassia",
            chamberTitle = "Subterranean Aquifer Core Vault",
            chamberSubtitle = "🔒 P-Wave Refraction & Deuterium Filtration",
            scientificDomain = "Hydrogeology & Crystallography",
            biomeTheme = Biome3D.DEEP_AQUIFER,
            dilemmaProse = "COGNITIVE VAULT: A diamond core drill hangs over a flooded basalt shaft lined with giant hexagonal quartz crystals. Cassia's seismic thumper reveals an ancient acoustic resonance chamber just below the water table.",
            scienceNote = "Seismic P-wave velocity shifts sharply at solid-liquid phase boundaries, mapping subterranean aquifers in 3D.",
            preferredArchetype = DoorArchetype.COGNITIVE_VAULT,
            requiredFunction = "Se",
            requiredProjectId = "living_atlas"
        ),
        CanonicalChamberTemplate(
            signature = "canon_orion_cymatic_studio",
            leadCharId = "orion",
            chamberTitle = "Cymatic Standing-Wave Sanctuary",
            chamberSubtitle = "🍵 Acoustic Levitation & Harmonic Decompression",
            scientificDomain = "Vibro-Acoustics & Cymatics",
            biomeTheme = Biome3D.ATLAS_CHAMBER,
            dilemmaProse = "SANCTUARY ALCOVE: Orion draws a carbon-fiber bow across a brass Chladni plate. A pool of liquid mercury and water rises into a geometric cathedral of standing waves, entraining the heartbeats of everyone in the room.",
            scienceNote = "Standing acoustic waves create pressure nodes capable of levitating dense matter without physical container contact.",
            preferredArchetype = DoorArchetype.SANCTUARY_LOUNGE
        )
    )

    // Combinatorial pools for infinite, never-repeating chamber generation beyond the 16 canonical chambers
    private val combinatorialAdjectives = listOf(
        "Cryo-Magnetic", "Piezoelectric", "Bioluminescent", "Sub-Harmonic", "Hyper-Saline",
        "Magnetocaloric", "Chemosynthetic", "Femtosecond", "Supercritical", "Dia-Magnetic",
        "Thermo-Acoustic", "Phototrophic", "Barometric", "Gravitational", "Electro-Osmotic",
        "Triboelectric", "Metamaterial", "Birefringent", "Soliton-Wave", "Isotopic"
    )

    private val combinatorialApparatus = listOf(
        "Vapor Condenser", "Centrifuge Bay", "Spore Bioreactor", "Flux Gyroscope",
        "Induction Crucible", "Seismic Interferometer", "Quartz Resonator", "Mycelial Relay",
        "Plasma Divertor", "Osmotic Membrane", "Cymatic Chamber", "Faraday Airlock",
        "Cryostat Manifold", "Spectrometer Vault", "Hydroponic Sluice", "Waveguide Conduit"
    )

    private val combinatorialAnomalies = listOf(
        "is experiencing a 14-millitesla magnetic eddy current from the Blue Dwarf's flare",
        "has colonized a self-organizing mat of prime-number signaling fungi",
        "is vibrating at a 432 Hz Chladni resonance that threatens to shatter the glass seals",
        "is accumulating galvanic micro-crystals along its titanium-copper pressure seam",
        "has captured an anomalous subterranean acoustic echo from Sector 4's fault line",
        "is supercooling heavy-water isotopes faster than the thermal bus can vent",
        "has triggered a sympathetic stress spike across the partner node telemetry grid",
        "is synthesizing a high-yield biopolymer that smells unmistakably like roasted hazelnuts",
        "is deflecting ionized step-leaders into the secondary backup geode",
        "has revealed a five-dimensional birefringence pattern etched by pre-colony lightning"
    )

    /**
     * Generates the initial set of Corridor Doors along the enclosed hallway.
     */
    fun buildInitialCorridorDoors(
        characters: List<EnclaveCharacter>,
        dayNumber: Int
    ): Pair<List<CorridorDoor3D>, Set<String>> {
        val used = mutableSetOf<String>()
        val doors = mutableListOf<CorridorDoor3D>()
        for (i in 1..6) {
            val door = generateNextUniqueDoor(
                doorNumber = i,
                zPosition = FIRST_DOOR_Z + (i - 1) * DOOR_SPACING_Z,
                usedSignatures = used,
                characters = characters,
                dayNumber = dayNumber
            )
            used.add(door.scenarioSignature)
            doors.add(door)
        }
        return doors to used
    }

    /**
     * Ensures that as the player walks down the infinite hallway (+Z), there are always
     * freshly generated, never-before-used doors extending ahead of [playerZ].
     */
    fun ensureDoorsAhead(
        currentDoors: List<CorridorDoor3D>,
        usedSignatures: Set<String>,
        playerZ: Float,
        characters: List<EnclaveCharacter>,
        dayNumber: Int
    ): Pair<List<CorridorDoor3D>, Set<String>> {
        val maxDoorZ = currentDoors.maxOfOrNull { it.position.z } ?: 0f
        if (maxDoorZ - playerZ >= LOOKAHEAD_DISTANCE_Z && currentDoors.size >= 6) {
            return currentDoors to usedSignatures
        }

        val updatedDoors = currentDoors.toMutableList()
        val updatedUsed = usedSignatures.toMutableSet()
        var nextDoorNum = (updatedDoors.maxOfOrNull { it.doorNumber } ?: 0) + 1
        var nextZ = if (updatedDoors.isEmpty()) FIRST_DOOR_Z else (maxDoorZ + DOOR_SPACING_Z)

        while (nextZ - playerZ < LOOKAHEAD_DISTANCE_Z + DOOR_SPACING_Z) {
            val newDoor = generateNextUniqueDoor(
                doorNumber = nextDoorNum,
                zPosition = nextZ,
                usedSignatures = updatedUsed,
                characters = characters,
                dayNumber = dayNumber
            )
            updatedUsed.add(newDoor.scenarioSignature)
            updatedDoors.add(newDoor)
            nextDoorNum++
            nextZ += DOOR_SPACING_Z
        }

        return updatedDoors to updatedUsed
    }

    private fun determineArchetypeForDoorNumber(doorNumber: Int): DoorArchetype {
        return when (doorNumber % 5) {
            1 -> DoorArchetype.STANDARD_LAB
            2 -> DoorArchetype.TICKING_CRISIS
            3 -> DoorArchetype.COGNITIVE_VAULT
            4 -> DoorArchetype.SANCTUARY_LOUNGE
            else -> DoorArchetype.STANDARD_LAB
        }
    }

    private fun buildSanctuaryOptions(
        doorNumber: Int,
        signature: String,
        wallX: Float,
        zPosition: Float,
        leadChar: EnclaveCharacter,
        partnerChar: EnclaveCharacter,
        biome: Biome3D
    ): List<ChoiceGate3D> {
        return listOf(
            ChoiceGate3D(
                id = "door_${doorNumber}_sanc_tea_$signature",
                title = "Share 7.83 Hz Adaptogenic Moss Tea with ${leadChar.name}",
                subtitle = "Decompress cortical static, vent -25% Vex Stress, and activate +15% Sibling Synergy",
                scientificDomain = "Schumann Neuro-Recoupling",
                squadAlignment = Squad.MIND,
                position = Vec3(wallX, 0f, zPosition),
                projectId = "lyceum_mind",
                projectDelta = 7,
                stressDelta = -25,
                moraleDelta = 12,
                nextPovId = partnerChar.id,
                nextBiomeId = biome.id,
                outcomeNarrative = "Over steaming mugs of adaptogenic moss tea inside Bay %02d, %s and the crew let their guard down—venting -25%% Vex Stress and igniting a +15%% Sibling Synergy buff for the next chamber.".format(
                    doorNumber, leadChar.name
                ),
                batonPassQuote = "\"I needed that breather. ${partnerChar.name}, your mind is sharp—take the next bay!\""
            ),
            ChoiceGate3D(
                id = "door_${doorNumber}_sanc_broth_$signature",
                title = "Sample Ryn's 72°C Methional Comfort Broth & Trade Lore",
                subtitle = "Restore +15% Colony Morale, clear -22% Stress, and boost Mycelial & Hearth Concordance",
                scientificDomain = "Molecular Gastrophysics",
                squadAlignment = Squad.HUB,
                position = Vec3(wallX, 0f, zPosition),
                projectId = "mycelial_concordance",
                projectDelta = 9,
                stressDelta = -22,
                moraleDelta = 15,
                nextPovId = leadChar.id,
                nextBiomeId = biome.id,
                outcomeNarrative = "The nostalgic Maillard aroma of roasted tubers and buttered popcorn melts away the tension in Bay %02d, boosting Colony Morale and granting Sibling Synergy.".format(
                    doorNumber
                ),
                batonPassQuote = "\"Never underestimate grounded copper spoons and hot soup. Ready for the corridor!\""
            ),
            ChoiceGate3D(
                id = "door_${doorNumber}_sanc_cymatic_$signature",
                title = "Tune Orion's Chladni Harmonic Standing Wave",
                subtitle = "Entrain heartbeats across ${leadChar.squad.title} and ${partnerChar.squad.title} (-20% Stress)",
                scientificDomain = "Vibro-Acoustic Entrainment",
                squadAlignment = leadChar.squad,
                position = Vec3(wallX, 0f, zPosition),
                projectId = leadChar.grandProjectId,
                projectDelta = 8,
                stressDelta = -20,
                moraleDelta = 10,
                nextPovId = partnerChar.id,
                nextBiomeId = biome.id,
                outcomeNarrative = "As the standing acoustic wave shimmers in Bay %02d, %s and %s synchronize their breathing and notes, unlocking a +15%% Sibling Synergy resonance boost.".format(
                    doorNumber, leadChar.name, partnerChar.name
                ),
                batonPassQuote = "\"Our telemetry is finally in phase, ${partnerChar.name}. Let's crack the next vault.\""
            )
        )
    }

    /**
     * Generates a single guaranteed-unique [CorridorDoor3D] with 3 (or 4 if Vex Override)
     * distinct tactical choices inside the room and strategic archetype properties.
     */
    fun generateNextUniqueDoor(
        doorNumber: Int,
        zPosition: Float,
        usedSignatures: Set<String>,
        characters: List<EnclaveCharacter>,
        dayNumber: Int
    ): CorridorDoor3D {
        val side = if (doorNumber % 2 == 1) DoorSide.LEFT_WALL else DoorSide.RIGHT_WALL
        val wallX = if (side == DoorSide.LEFT_WALL) -CORRIDOR_HALF_WIDTH else CORRIDOR_HALF_WIDTH

        // 1. First pick any unused canonical template
        val unusedCanonical = canonicalTemplates.firstOrNull { it.signature !in usedSignatures }
        if (unusedCanonical != null) {
            val leadChar = characters.firstOrNull { it.id == unusedCanonical.leadCharId }
                ?: characters[(doorNumber - 1) % characters.size]
            val partnerChar = characters.firstOrNull { it.id == leadChar.partnerNodeId }
                ?: characters[doorNumber % characters.size]

            val arch = unusedCanonical.preferredArchetype
            val baseOptions = if (arch == DoorArchetype.SANCTUARY_LOUNGE) {
                buildSanctuaryOptions(
                    doorNumber = doorNumber,
                    signature = unusedCanonical.signature,
                    wallX = wallX,
                    zPosition = zPosition,
                    leadChar = leadChar,
                    partnerChar = partnerChar,
                    biome = unusedCanonical.biomeTheme
                )
            } else {
                EnclaveSeedData.buildGatesForCharacter(
                    character = leadChar,
                    dayNumber = dayNumber,
                    relayStep = ((doorNumber - 1) % 3) + 1
                ).mapIndexed { idx, gate ->
                    gate.copy(
                        id = "door_${doorNumber}_opt_${idx}_${unusedCanonical.signature}",
                        position = Vec3(wallX, 0f, zPosition)
                    )
                }
            }

            return CorridorDoor3D(
                id = "door_$doorNumber",
                doorNumber = doorNumber,
                scenarioSignature = unusedCanonical.signature,
                chamberTitle = "Bay %02d • %s".format(doorNumber, unusedCanonical.chamberTitle),
                chamberSubtitle = unusedCanonical.chamberSubtitle,
                scientificDomain = unusedCanonical.scientificDomain,
                squadAlignment = leadChar.squad,
                biomeTheme = unusedCanonical.biomeTheme,
                side = side,
                position = Vec3(wallX, 0f, zPosition),
                roomDilemmaProse = unusedCanonical.dilemmaProse,
                scientificCheeseNote = unusedCanonical.scienceNote,
                leadNpcId = leadChar.id,
                options = baseOptions,
                archetype = arch,
                requiredCognitiveFunction = if (arch == DoorArchetype.COGNITIVE_VAULT) {
                    unusedCanonical.requiredFunction ?: "Ti"
                } else null,
                requiredProjectId = if (arch == DoorArchetype.COGNITIVE_VAULT) {
                    unusedCanonical.requiredProjectId ?: leadChar.grandProjectId
                } else null,
                requiredProjectPercent = 75,
                crisisTurnsRemaining = if (arch == DoorArchetype.TICKING_CRISIS) 3 else null
            )
        }

        // 2. Otherwise synthesize a guaranteed-unique combinatorial Hard-Science Chamber
        var attempt = 0
        val arch = determineArchetypeForDoorNumber(doorNumber)
        while (true) {
            val seedIndex = doorNumber * 17 + attempt * 31
            val adj = combinatorialAdjectives[abs(seedIndex) % combinatorialAdjectives.size]
            val app = combinatorialApparatus[abs(seedIndex / 3 + attempt) % combinatorialApparatus.size]
            val anom = combinatorialAnomalies[abs(seedIndex / 7 + attempt) % combinatorialAnomalies.size]
            val leadChar = characters[abs(doorNumber + attempt) % characters.size]
            val partnerChar = characters.firstOrNull { it.id == leadChar.partnerNodeId }
                ?: characters[(doorNumber + 1) % characters.size]
            val thirdChar = characters[(doorNumber + 5 + attempt) % characters.size]

            val signature = "proc_${adj.lowercase()}_${app.lowercase().replace(" ", "_")}_${leadChar.id}_$doorNumber"
            if (signature !in usedSignatures || attempt > 50) {
                val biome = Biome3D.entries[abs(doorNumber + attempt) % Biome3D.entries.size]
                val chamberName = when (arch) {
                    DoorArchetype.SANCTUARY_LOUNGE -> "$adj Tea & Cymatic Alcove"
                    DoorArchetype.COGNITIVE_VAULT -> "$adj $app Vault"
                    DoorArchetype.TICKING_CRISIS -> "$adj $app Breach"
                    DoorArchetype.STANDARD_LAB -> "$adj $app"
                }

                val options = if (arch == DoorArchetype.SANCTUARY_LOUNGE) {
                    buildSanctuaryOptions(
                        doorNumber = doorNumber,
                        signature = signature,
                        wallX = wallX,
                        zPosition = zPosition,
                        leadChar = leadChar,
                        partnerChar = partnerChar,
                        biome = biome
                    )
                } else {
                    listOf(
                        ChoiceGate3D(
                            id = "door_${doorNumber}_opt_forge_$signature",
                            title = "Overclock $app via ${leadChar.name}'s Protocol",
                            subtitle = "Apply ${leadChar.scientificSpecialty.substringBefore("&").trim()} to force a high-yield breakthrough",
                            scientificDomain = leadChar.scientificSpecialty,
                            squadAlignment = leadChar.squad,
                            position = Vec3(wallX, 0f, zPosition),
                            projectId = leadChar.grandProjectId,
                            projectDelta = 11,
                            stressDelta = 7,
                            moraleDelta = 6,
                            nextPovId = partnerChar.id,
                            nextBiomeId = biome.id,
                            outcomeNarrative = "Inside Bay %02d, %s locks the %s into a high-efficiency feedback loop, converting the anomaly into +11%% progress for %s.".format(
                                doorNumber, leadChar.name, chamberName, leadChar.grandProjectId.replace("_", " ")
                            ),
                            batonPassQuote = "\"%s, the %s is holding steady—take the next corridor bay!\"".format(
                                partnerChar.name, app
                            )
                        ),
                        ChoiceGate3D(
                            id = "door_${doorNumber}_opt_partner_$signature",
                            title = "Synthesize with ${partnerChar.name}'s ${partnerChar.mbti} Lens",
                            subtitle = "Cross-couple ${partnerChar.scientificSpecialty.substringBefore(",").trim()} for a low-stress symbiotic fix",
                            scientificDomain = partnerChar.scientificSpecialty,
                            squadAlignment = partnerChar.squad,
                            position = Vec3(wallX, 0f, zPosition),
                            projectId = partnerChar.grandProjectId,
                            projectDelta = 9,
                            stressDelta = -6,
                            moraleDelta = 8,
                            nextPovId = partnerChar.id,
                            nextBiomeId = biome.id,
                            outcomeNarrative = "Combining ${leadChar.name}'s precision with ${partnerChar.name}'s ${partnerChar.mbti} intuition resolves the $chamberName anomaly while lowering chamber cortisol.",
                            batonPassQuote = "\"See? When ${leadChar.mbti} and ${partnerChar.mbti} stop arguing, the physics solves itself.\""
                        ),
                        ChoiceGate3D(
                            id = "door_${doorNumber}_opt_hub_$signature",
                            title = "Route Telemetry to ${thirdChar.name} & Ground the Bay",
                            subtitle = "Prioritize Enclave morale, safety grounding, and 5D archival logging",
                            scientificDomain = thirdChar.scientificSpecialty,
                            squadAlignment = thirdChar.squad,
                            position = Vec3(wallX, 0f, zPosition),
                            projectId = thirdChar.grandProjectId,
                            projectDelta = 8,
                            stressDelta = -10,
                            moraleDelta = 10,
                            nextPovId = thirdChar.id,
                            nextBiomeId = biome.id,
                            outcomeNarrative = "You shunt the excess charge from the $chamberName into the corridor grounding bus and stream clean telemetry to ${thirdChar.name}'s station.",
                            batonPassQuote = "\"Bay %02d grounded and logged, %s. Keep moving down the hall!\"".format(
                                doorNumber, thirdChar.name
                            )
                        )
                    )
                }

                val reqFn = if (arch == DoorArchetype.COGNITIVE_VAULT) {
                    cognitiveFunctions[abs(doorNumber) % cognitiveFunctions.size]
                } else null

                return CorridorDoor3D(
                    id = "door_$doorNumber",
                    doorNumber = doorNumber,
                    scenarioSignature = signature,
                    chamberTitle = "Bay %02d • %s".format(doorNumber, chamberName),
                    chamberSubtitle = "${arch.shortTag} • ${leadChar.name} & ${partnerChar.name}",
                    scientificDomain = leadChar.scientificSpecialty,
                    squadAlignment = leadChar.squad,
                    biomeTheme = biome,
                    side = side,
                    position = Vec3(wallX, 0f, zPosition),
                    roomDilemmaProse = "Opening the heavy brass-sealed door to Bay %02d (%s) reveals the %s, which %s. %s (%s) and %s (%s) are debating three distinct interventions.".format(
                        doorNumber, arch.badgeLabel, chamberName, anom, leadChar.name, leadChar.mbti, partnerChar.name, partnerChar.mbti
                    ),
                    scientificCheeseNote = "${leadChar.scientificSpecialty}: Coupling ${adj.lowercase()} gradients across ${partnerChar.scientificSpecialty.lowercase()} stabilizes non-equilibrium states.",
                    leadNpcId = leadChar.id,
                    options = options,
                    archetype = arch,
                    requiredCognitiveFunction = reqFn,
                    requiredProjectId = if (arch == DoorArchetype.COGNITIVE_VAULT) leadChar.grandProjectId else null,
                    requiredProjectPercent = 75,
                    crisisTurnsRemaining = if (arch == DoorArchetype.TICKING_CRISIS) 3 else null
                )
            }
            attempt++
        }
    }
}
