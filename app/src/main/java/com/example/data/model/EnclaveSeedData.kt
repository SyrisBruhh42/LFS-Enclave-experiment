package com.example.data.model

object EnclaveSeedData {

    val initialCharacters: List<EnclaveCharacter> = listOf(
        // 1. SILAS (Prodigy - The First Forge)
        EnclaveCharacter(
            id = "silas",
            name = "Silas",
            roleTitle = "The Sympathetic Metallurgist",
            squad = Squad.FORGE,
            isMentor = false,
            partnerNodeId = "leo",
            mbti = "ISTJ",
            cognitiveStack = "Si > Te > Fi > Ne",
            enneagram = "1w9",
            archetype = "The Perfectionist Craftsman",
            bigFive = BigFiveProfile(openness = 14, conscientiousness = 99, extraversion = 18, agreeableness = 42, neuroticism = 64),
            vexStarSign = "Pisces / Virgo",
            vexOverrideTitle = "Zero-Dust Trance",
            vexOverrideBehavior = "Abandons pragmatic engineering for poetic intuition or obsessive 3 AM floor-scrubbing to purge microscopic entropy.",
            grandProjectId = "first_forge",
            scientificSpecialty = "Fe-Co-Cr-Ni-Mn High-Entropy Alloys & Galvanic Lattice Stability",
            geniusVsMundane = "Can stabilize resonant shield plating at atomic tolerances, yet has to whack a rusty copper-flange garden pipe with a wrench.",
            lieBelieved = "Any imperfection in the lattice is a moral failure that will let the chaos in.",
            truthToLearn = "A patched pipe that holds until tomorrow is how a family survives today.",
            stressLevel = 38
        ),
        // 2. LEO (Mentor - The First Forge)
        EnclaveCharacter(
            id = "leo",
            name = "Leo",
            roleTitle = "The Master Artificer",
            squad = Squad.FORGE,
            isMentor = true,
            partnerNodeId = "silas",
            mbti = "ENTJ",
            cognitiveStack = "Te > Ni > Se > Fi",
            enneagram = "8w9",
            archetype = "The Sage Commander",
            bigFive = BigFiveProfile(openness = 72, conscientiousness = 92, extraversion = 78, agreeableness = 65, neuroticism = 15),
            vexStarSign = "Cancer",
            vexOverrideTitle = "Sentimental Nostalgia",
            vexOverrideBehavior = "Stops barking structural commands and becomes fiercely overprotective of hand-filed brass heirlooms.",
            grandProjectId = "first_forge",
            scientificSpecialty = "Magnetorheological Ferrofluids & Rosensweig Dynamic Seals",
            geniusVsMundane = "Knows how to architect a fully automated fusion foundry, but is stuck hand-filing his thousandth identical brass screw.",
            lieBelieved = "If I stop carrying the structural weight of the Enclave, the children will fracture.",
            truthToLearn = "The prodigies have already grown strong enough to hold the sky.",
            stressLevel = 22
        ),
        // 3. KAIO (Prodigy - The Sanctuary)
        EnclaveCharacter(
            id = "kaio",
            name = "Kaio",
            roleTitle = "The Kinetic Biologist",
            squad = Squad.WILDS,
            isMentor = false,
            partnerNodeId = "zara",
            mbti = "ENFP",
            cognitiveStack = "Ne > Fi > Te > Si",
            enneagram = "7w6",
            archetype = "The Kinetic Explorer",
            bigFive = BigFiveProfile(openness = 99, conscientiousness = 12, extraversion = 94, agreeableness = 76, neuroticism = 45),
            vexStarSign = "Capricorn",
            vexOverrideTitle = "The Diamond CEO",
            vexOverrideBehavior = "Sheds all chaotic bounce and snaps into ice-cold, tactical military efficiency with zero wasted words.",
            grandProjectId = "sanctuary",
            scientificSpecialty = "Luciferin-Luciferase Prime-Number Bioluminescent Syntax",
            geniusVsMundane = "Discovered sentient logic gates in flashbang fungi, but filtered Ryn's soup stock through a 'clean' sock.",
            lieBelieved = "If I ever slow down, the silence will catch up to me.",
            truthToLearn = "Stillness is where true ecological resonance happens.",
            stressLevel = 44
        ),
        // 4. ZARA (Mentor/Anchor - The Sanctuary)
        EnclaveCharacter(
            id = "zara",
            name = "Zara",
            roleTitle = "The Canopy Beastmaster",
            squad = Squad.WILDS,
            isMentor = true,
            partnerNodeId = "kaio",
            mbti = "ISFP",
            cognitiveStack = "Fi > Se > Ni > Te",
            enneagram = "9w8",
            archetype = "The Silent Guardian",
            bigFive = BigFiveProfile(openness = 84, conscientiousness = 68, extraversion = 16, agreeableness = 82, neuroticism = 24),
            vexStarSign = "Aries",
            vexOverrideTitle = "Apex Vanguard",
            vexOverrideBehavior = "Trades silent observation for reckless, head-first confrontation with subterranean leviathans.",
            grandProjectId = "sanctuary",
            scientificSpecialty = "Deep-Biosphere PhotophoreMimicry & Chemosynthetic Ethology",
            geniusVsMundane = "Can converse with blind iron-lake cephalopods in light spirals, yet has to muck out hex-pedal goat stalls.",
            lieBelieved = "Words only clutter the bond between living creatures.",
            truthToLearn = "Speaking your fear aloud lets the pack share the watch.",
            stressLevel = 25
        ),
        // 5. AUGUSTA (Mentor - The Living Atlas)
        EnclaveCharacter(
            id = "augusta",
            name = "Augusta",
            roleTitle = "The Wireframe Cartographer",
            squad = Squad.HUB,
            isMentor = true,
            partnerNodeId = "cassia",
            mbti = "INTJ",
            cognitiveStack = "Ni > Te > Fi > Se",
            enneagram = "5w6",
            archetype = "The Grand Architect",
            bigFive = BigFiveProfile(openness = 90, conscientiousness = 95, extraversion = 15, agreeableness = 38, neuroticism = 32),
            vexStarSign = "Sagittarius",
            vexOverrideTitle = "The Gambler's Leap",
            vexOverrideBehavior = "Throws away predictive models and bets the entire Enclave's Faraday grid on an untested 1% hypothesis.",
            grandProjectId = "living_atlas",
            scientificSpecialty = "Ediacaran Geomagnetic Bifurcation & Sector 4 Flux Mapping",
            geniusVsMundane = "Models planetary magnetosphere tears in 3D holography, yet has to ground every soup spoon with copper ankle wire.",
            lieBelieved = "A threat that is mapped is a threat that is conquered.",
            truthToLearn = "The map is not the territory; love survives what equations cannot predict.",
            stressLevel = 48
        ),
        // 6. CASSIA (Prodigy - The Living Atlas)
        EnclaveCharacter(
            id = "cassia",
            name = "Cassia",
            roleTitle = "The Telemetry Prodigy",
            squad = Squad.HUB,
            isMentor = false,
            partnerNodeId = "augusta",
            mbti = "ENTP",
            cognitiveStack = "Ne > Ti > Fe > Si",
            enneagram = "5w4",
            archetype = "The Pattern Hacker",
            bigFive = BigFiveProfile(openness = 96, conscientiousness = 62, extraversion = 68, agreeableness = 54, neuroticism = 40),
            vexStarSign = "Taurus",
            vexOverrideTitle = "Immovable Lockdown",
            vexOverrideBehavior = "Refuses to recalibrate a single sensor variance, digging in with stubborn, unyielding dogmatism.",
            grandProjectId = "living_atlas",
            scientificSpecialty = "Crustal Sensor Interferometry & Non-Linear Isobar Drift",
            geniusVsMundane = "Can detect a 0.4 vector variance across a continent, but constantly trips over uncoiled sensor cables.",
            lieBelieved = "Every anomaly is just an equation I haven't solved fast enough.",
            truthToLearn = "Some anomalies are invitations to listen, not bugs to patch.",
            stressLevel = 34
        ),
        // 7. RYN (Prodigy/Executive - The Hub Kitchen)
        EnclaveCharacter(
            id = "ryn",
            name = "Ryn",
            roleTitle = "The Gastrophysics Commander",
            squad = Squad.HUB,
            isMentor = false,
            partnerNodeId = "elara",
            mbti = "ESTJ",
            cognitiveStack = "Te > Si > Ne > Fi",
            enneagram = "1w2",
            archetype = "The Hearth General",
            bigFive = BigFiveProfile(openness = 45, conscientiousness = 98, extraversion = 82, agreeableness = 50, neuroticism = 36),
            vexStarSign = "Pisces",
            vexOverrideTitle = "Chaotic Flambé",
            vexOverrideBehavior = "Abandons 40 RPM clockwise stirring and 72°C precision to throw random psychoactive spices into the cauldron.",
            grandProjectId = "genesis_garden",
            scientificSpecialty = "Methional Volatile Flavor Pairing & Chitinous Maillard Kinetics",
            geniusVsMundane = "Synthesizes buttery popcorn aromas from alien fungi via gas chromatography, while yelling at Vex not to stir counter-clockwise.",
            lieBelieved = "If the nightly Feast slips by one degree Celsius, civilization collapses.",
            truthToLearn = "The family gathers for the warmth at the table, not the perfection of the broth.",
            stressLevel = 30
        ),
        // 8. VEX (Prodigy - The Hub / Astronomer)
        EnclaveCharacter(
            id = "vex",
            name = "Vex",
            roleTitle = "The Binary Astronomer",
            squad = Squad.HUB,
            isMentor = false,
            partnerNodeId = "dorian",
            mbti = "ENTP",
            cognitiveStack = "Ne > Ti > Fe > Si",
            enneagram = "7w8",
            archetype = "The Cosmic Trickster",
            bigFive = BigFiveProfile(openness = 98, conscientiousness = 20, extraversion = 88, agreeableness = 60, neuroticism = 28),
            vexStarSign = "Scorpio",
            vexOverrideTitle = "Deadly Earnest Sentinel",
            vexOverrideBehavior = "Drops all fourth-wall irony and astrological jokes, becoming intensely brooding, secretive, and hyper-protective.",
            grandProjectId = "living_atlas",
            scientificSpecialty = "Red Giant / Blue Dwarf Magnetohydrodynamic Tides & Three-Body Orbits",
            geniusVsMundane = "Calculates binary stellar flare trajectories in his head, but blames plumbing disasters on 'Mercury in retrograde'.",
            lieBelieved = "If I treat our survival like a comedy, the cosmic terror can't touch me.",
            truthToLearn = "You are allowed to care out loud without hiding behind a punchline.",
            stressLevel = 29
        ),
        // 9. HECTOR (Mentor - The Stormbreaker)
        EnclaveCharacter(
            id = "hector",
            name = "Hector",
            roleTitle = "The Stormbreaker",
            squad = Squad.FORGE,
            isMentor = true,
            partnerNodeId = "jane",
            mbti = "ESTP",
            cognitiveStack = "Se > Ti > Fe > Ni",
            enneagram = "7w8",
            archetype = "The Lightning Rider",
            bigFive = BigFiveProfile(openness = 78, conscientiousness = 48, extraversion = 95, agreeableness = 64, neuroticism = 35),
            vexStarSign = "Virgo",
            vexOverrideTitle = "0.5-Degree Cable Panic",
            vexOverrideBehavior = "Paralyzes atop a 50-meter lightning tower in terror that a conductor coupling is 0.5 degrees off vertical.",
            grandProjectId = "stormbreaker",
            scientificSpecialty = "Laser-Induced Plasma Channels (LIPC) & Gigajoule Geode Capacitance",
            geniusVsMundane = "Harnesses planetary lightning bolts in a bedrock crystal, yet forgets to oil the latches on the roof hatch.",
            lieBelieved = "I can only honor my late wife's love of the sky by riding its deadliest storms.",
            truthToLearn = "Building a calm harbor below the clouds honors her just as deeply.",
            stressLevel = 62
        ),
        // 10. JANE (Prodigy - The Stormbreaker)
        EnclaveCharacter(
            id = "jane",
            name = "Jane",
            roleTitle = "The Chaos Coder",
            squad = Squad.FORGE,
            isMentor = false,
            partnerNodeId = "hector",
            mbti = "INTP",
            cognitiveStack = "Ti > Ne > Si > Fe",
            enneagram = "5w6",
            archetype = "The Reality Hacker",
            bigFive = BigFiveProfile(openness = 94, conscientiousness = 55, extraversion = 12, agreeableness = 44, neuroticism = 38),
            vexStarSign = "Leo",
            vexOverrideTitle = "Theatrical Showstopper",
            vexOverrideBehavior = "Demands an audience in the main hall and narrates her compiler logs like a Shakespearean monologue.",
            grandProjectId = "stormbreaker",
            scientificSpecialty = "Lorenz Butterfly Attractors & Deterministic Atmospheric Ionization",
            geniusVsMundane = "Hacked the sky to route lightning at 98% efficiency, but spent all afternoon failing to dig a straight grounding ditch.",
            lieBelieved = "Emotions are just unindexed variables that slow down compilation.",
            truthToLearn = "The messy human variables are the whole reason we run the code.",
            stressLevel = 31
        ),
        // 11. KENNA (Mentor - The Lyceum of the Mind)
        EnclaveCharacter(
            id = "kenna",
            name = "Kenna",
            roleTitle = "The Neuro-Architect",
            squad = Squad.MIND,
            isMentor = true,
            partnerNodeId = "elias",
            mbti = "ENFJ",
            cognitiveStack = "Fe > Ni > Se > Ti",
            enneagram = "2w1",
            archetype = "The Empathetic Anchor",
            bigFive = BigFiveProfile(openness = 86, conscientiousness = 88, extraversion = 84, agreeableness = 96, neuroticism = 26),
            vexStarSign = "Aquarius",
            vexOverrideTitle = "Cold Clinical Automaton",
            vexOverrideBehavior = "Loses all warm empathy, addressing family members by node numbers and reciting raw cortisol telemetry.",
            grandProjectId = "lyceum_mind",
            scientificSpecialty = "Schumann Resonance Pineal Decoupling & Sensory Deprivation Neurochemistry",
            geniusVsMundane = "Balances sixteen polymaths' serotonin receptors during magnetic storms, while brewing valerian tea at 0300 hours.",
            lieBelieved = "If anyone in this family breaks, it is because I failed to see the tremor in time.",
            truthToLearn = "Even the healer is allowed to step into the tank and rest.",
            stressLevel = 33
        ),
        // 12. ELIAS (Prodigy - The Lyceum of the Mind)
        EnclaveCharacter(
            id = "elias",
            name = "Elias",
            roleTitle = "The Somnambulist",
            squad = Squad.MIND,
            isMentor = false,
            partnerNodeId = "kenna",
            mbti = "INFJ",
            cognitiveStack = "Ni > Fe > Ti > Se",
            enneagram = "4w5",
            archetype = "The Dream Cartographer",
            bigFive = BigFiveProfile(openness = 95, conscientiousness = 74, extraversion = 22, agreeableness = 88, neuroticism = 52),
            vexStarSign = "Gemini",
            vexOverrideTitle = "Hyper-Verbal Chatterbox",
            vexOverrideBehavior = "Cannot stop rapid-fire gossiping and running simultaneous contradictory thought experiments aloud.",
            grandProjectId = "lyceum_mind",
            scientificSpecialty = "Psychoactive Spore Dosimetry & Alpha-Wave Isolation Tanks",
            geniusVsMundane = "Calibrates zero-gravity neural reset chambers, but has to scrub crystallized Epsom salt out of the drain valves.",
            lieBelieved = "To keep the peace, I must absorb everyone else's static in silence.",
            truthToLearn = "Sharing the static grounds the circuit.",
            stressLevel = 27
        ),
        // 13. WILLOW (Prodigy - The Genesis Garden)
        EnclaveCharacter(
            id = "willow",
            name = "Willow",
            roleTitle = "The Mycelial Alchemist",
            squad = Squad.WILDS,
            isMentor = false,
            partnerNodeId = "terra",
            mbti = "ISTP",
            cognitiveStack = "Ti > Se > Ni > Fe",
            enneagram = "5w6",
            archetype = "The Bio-Virtuoso",
            bigFive = BigFiveProfile(openness = 82, conscientiousness = 76, extraversion = 19, agreeableness = 58, neuroticism = 25),
            vexStarSign = "Libra",
            vexOverrideTitle = "Indecisive Diplomat",
            vexOverrideBehavior = "Refuses to make a single scalpel cut without polling all sixteen family members for unanimous aesthetic consensus.",
            grandProjectId = "genesis_garden",
            scientificSpecialty = "Hyphal Calcium Action Potentials & Biological AND/OR/NOT Logic Gates",
            geniusVsMundane = "Found the biological USB port of a valley-wide fungal supercomputer, then had to pause to turn the steaming compost heap.",
            lieBelieved = "People are far less reliable than cellular chemistry.",
            truthToLearn = "The family network reroutes around damage even faster than mycelium.",
            stressLevel = 26
        ),
        // 14. TERRA (Mentor - The Genesis Garden)
        EnclaveCharacter(
            id = "terra",
            name = "Terra",
            roleTitle = "The Master Botanist",
            squad = Squad.WILDS,
            isMentor = true,
            partnerNodeId = "willow",
            mbti = "ISFJ",
            cognitiveStack = "Si > Fe > Ti > Ne",
            enneagram = "6w5",
            archetype = "The Root Keeper",
            bigFive = BigFiveProfile(openness = 70, conscientiousness = 94, extraversion = 34, agreeableness = 92, neuroticism = 30),
            vexStarSign = "Aquarius",
            vexOverrideTitle = "Radical Mutagenicist",
            vexOverrideBehavior = "Cross-pollinates volatile silicon vines just to see what explodes.",
            grandProjectId = "genesis_garden",
            scientificSpecialty = "Anaerobic Polymer Epoxy-Saps & Chitinous Tuber Cultivation",
            geniusVsMundane = "Breeds self-healing botanical sealants that outperform titanium welds, while weeding ferro-moss by hand.",
            lieBelieved = "Tradition and caution are the only shields against an alien biosphere.",
            truthToLearn = "Adaptation requires letting wild seeds take root.",
            stressLevel = 20
        ),
        // 15. ISLA (Mentor - The Simulacrum & Mycelial Concordance)
        EnclaveCharacter(
            id = "isla",
            name = "Isla",
            roleTitle = "The 5D Crystal Architect",
            squad = Squad.MIND,
            isMentor = true,
            partnerNodeId = "orion",
            mbti = "INTJ",
            cognitiveStack = "Ni > Te > Fi > Se",
            enneagram = "1w9",
            archetype = "The Keeper of Eternity",
            bigFive = BigFiveProfile(openness = 91, conscientiousness = 96, extraversion = 18, agreeableness = 52, neuroticism = 22),
            vexStarSign = "Leo",
            vexOverrideTitle = "Grandiose Avant-Garde",
            vexOverrideBehavior = "Replaces archival Earth history projections with flamboyant, neon-gold statues of herself.",
            grandProjectId = "simulacrum",
            scientificSpecialty = "Femtosecond Laser 5D Quartz Nanostructure Optical Storage",
            geniusVsMundane = "Etches terabytes of planetary history into eternal quartz lattices, but has to hand-polish smudges off the projector lenses.",
            lieBelieved = "Only what is etched in crystal survives entropy.",
            truthToLearn = "A fleeting laugh at the dinner table outlives a billion-year stone.",
            stressLevel = 24
        ),
        // 16. ORION (Prodigy - The Simulacrum & Mycelial Concordance)
        EnclaveCharacter(
            id = "orion",
            name = "Orion",
            roleTitle = "The Cymatic Harmonist",
            squad = Squad.MIND,
            isMentor = false,
            partnerNodeId = "isla",
            mbti = "INFP",
            cognitiveStack = "Fi > Ne > Si > Te",
            enneagram = "4w5",
            archetype = "The Magnetic Poet",
            bigFive = BigFiveProfile(openness = 99, conscientiousness = 35, extraversion = 24, agreeableness = 94, neuroticism = 68),
            vexStarSign = "Capricorn",
            vexOverrideTitle = "Utilitarian Auditor",
            vexOverrideBehavior = "Declares art a waste of joules and attempts to ration synthesizer frequencies by caloric ROI.",
            grandProjectId = "mycelial_concordance",
            scientificSpecialty = "Ferrofluid Cymatics, Acoustic Resonance & Mycelial Verb Translation",
            geniusVsMundane = "Sculpts zero-gravity ferrofluid spires with C-Major chords, yet cries over a particularly symmetrical potato at dinner.",
            lieBelieved = "My sensitivity is a fragile liability in a world that needs hard steel.",
            truthToLearn = "Harmony is the physics that holds all sixteen nodes together.",
            stressLevel = 46
        )
    )

    val initialGrandProjects: List<GrandProject> = listOf(
        GrandProject(
            id = "living_atlas",
            name = "1. The Living Atlas",
            squad = Squad.HUB,
            prodigyId = "cassia",
            mentorId = "augusta",
            scientificQuestion = "Can we render a real-time predictive 3D model of the planet's chaotic magnetosphere?",
            currentObstacle = "Sector 4 magnetic tear is causing 0.4 vector variance and gigajoule ground ionization.",
            progressPercent = 64,
            unlockedBreakthroughs = listOf("Crustal Interferometry Grid", "Ediacaran Pole-Wander Model")
        ),
        GrandProject(
            id = "first_forge",
            name = "2. The First Forge",
            squad = Squad.FORGE,
            prodigyId = "silas",
            mentorId = "leo",
            scientificQuestion = "How do we smelt hyper-dense sympathetic alloys and seal expanding lightning geodes?",
            currentObstacle = "Galvanic corrosion on copper-iron junctions and thermal expansion of the containment ring.",
            progressPercent = 58,
            unlockedBreakthroughs = listOf("Fe-Co-Cr-Ni-Mn High-Entropy Pipe", "Non-Newtonian Ferrofluid Collar")
        ),
        GrandProject(
            id = "mycelial_concordance",
            name = "3. The Mycelial Concordance",
            squad = Squad.WILDS,
            prodigyId = "orion",
            mentorId = "isla",
            scientificQuestion = "Can we translate the electrical action-potential syntax of the valley-wide fungal motherboard?",
            currentObstacle = "Distinguishing nutrient-routing verbs ('Flowing', 'Listen') from geomagnetic background noise.",
            progressPercent = 52,
            unlockedBreakthroughs = listOf("Prime-Number Luciferin Decoder", "Root Transmitter 'We Hear' Handshake")
        ),
        GrandProject(
            id = "sanctuary",
            name = "4. The Sanctuary",
            squad = Squad.WILDS,
            prodigyId = "kaio",
            mentorId = "zara",
            scientificQuestion = "How do we build mutual trust with hyper-adaptive hex-pedal predators and deep-aquifer fauna?",
            currentObstacle = "Oil-slick camouflage predators stalking the ironwood ridge during Blue Dwarf auroras.",
            progressPercent = 49,
            unlockedBreakthroughs = listOf("Photophore Spiral Greeting", "Non-Lethal Flashbang Geode Lure")
        ),
        GrandProject(
            id = "genesis_garden",
            name = "5. The Genesis Garden",
            squad = Squad.WILDS,
            prodigyId = "willow",
            mentorId = "terra",
            scientificQuestion = "Can we cultivate a living library of silicon-based flora for medicine, epoxy, and gastrophysics?",
            currentObstacle = "Chitinous cell walls require precise enzymatic breakdown and methional flavor pairing.",
            progressPercent = 71,
            unlockedBreakthroughs = listOf("Pepper-Scented Hermetic Epoxy-Sap", "Maillard Popcorn Mushroom Stock")
        ),
        GrandProject(
            id = "stormbreaker",
            name = "6. The Stormbreaker",
            squad = Squad.FORGE,
            prodigyId = "jane",
            mentorId = "hector",
            scientificQuestion = "Can we guide 50 kV/m atmospheric lightning via femtosecond lasers to create calm weather bubbles?",
            currentObstacle = "Conductor coupling harmonic wobble when potential gradient spikes above 60% geode capacity.",
            progressPercent = 67,
            unlockedBreakthroughs = listOf("Laser-Induced Plasma Channel (LIPC)", "Lorenz Attractor Target Compiler")
        ),
        GrandProject(
            id = "simulacrum",
            name = "7. The Simulacrum",
            squad = Squad.MIND,
            prodigyId = "orion",
            mentorId = "isla",
            scientificQuestion = "Can we etch terabytes of storm telemetry into 5D quartz crystals to project interactive simulations?",
            currentObstacle = "High-bandwidth terabyte/sec storm streams risk thermal stress on the refractive lattice.",
            progressPercent = 61,
            unlockedBreakthroughs = listOf("5D Refractive Polarization Etcher", "Cymatic Ferrofluid Sound Sculpture")
        ),
        GrandProject(
            id = "lyceum_mind",
            name = "8. The Lyceum of the Mind",
            squad = Squad.MIND,
            prodigyId = "elias",
            mentorId = "kenna",
            scientificQuestion = "How do we shield sixteen polymaths from Schumann resonance disruption and Vex Overrides?",
            currentObstacle = "Post-storm magnetic residue suppressing melatonin and triggering 0300h Vex inversions.",
            progressPercent = 74,
            unlockedBreakthroughs = listOf("Valerian-Moss Serotonin Decoupler", "Zero-Dust Sensory Deprivation Tank")
        )
    )

    val initialBlackMarketQueue: List<BlackMarketPrank> = listOf(
        BlackMarketPrank(
            id = "bm_1",
            instigatorId = "silas",
            intendedTargetId = "kaio",
            accidentalVictimId = "hector",
            blamedNodeId = "vex",
            title = "Magnetized Boot Shims",
            pettyDescription = "Silas secretly slipped 200-gram high-entropy iron shims into Kaio's boots for calling his pipe 'rust-art'.",
            blindSpotOutcome = "Kaio swapped boots with Hector before climbing the Stormbreaker; Hector stuck to a floor plate and blamed Vex!",
            turnsRemaining = 2
        ),
        BlackMarketPrank(
            id = "bm_2",
            instigatorId = "ryn",
            intendedTargetId = "vex",
            accidentalVictimId = "cassia",
            blamedNodeId = "kaio",
            title = "Decoy Scoville Cookie Batch",
            pettyDescription = "Ryn excluded Vex from the chocolate-tuber cookie tin for stirring the stew at 28 RPM.",
            blindSpotOutcome = "Cassia raided Vex's locker stash first, ate the pepper-sap decoy biscuit, and swore Kaio rigged the pantry!",
            turnsRemaining = 3
        )
    )

    fun buildGatesForCharacter(
        character: EnclaveCharacter,
        dayNumber: Int,
        relayStep: Int
    ): List<ChoiceGate3D> {
        val baseGates = when (character.id) {
            "silas" -> listOf(
                ChoiceGate3D(
                    id = "gate_silas_forge_${dayNumber}_$relayStep",
                    title = "Kinetic Wrench Shock",
                    subtitle = "Reseat the oxidized particulates by force & rush to the Forge",
                    scientificDomain = "Galvanic Lattice Mechanics",
                    squadAlignment = Squad.FORGE,
                    position = Vec3(-6.5f, 0f, 13f),
                    projectId = "first_forge",
                    projectDelta = 8,
                    stressDelta = 12,
                    moraleDelta = 2,
                    nextPovId = "kaio",
                    nextBiomeId = Biome3D.ARBORETUM.id,
                    outcomeNarrative = "Clang! You strike the Fe-Co-Cr-Ni-Mn conduit with a practiced kinetic shock. The drip pauses just long enough for Leo to laugh and hand off the relay to Kaio returning from the ironwood ridge.",
                    batonPassQuote = "\"Strange enough that Kaio was quiet for three minutes. Which is a statistical anomaly in itself.\""
                ),
                ChoiceGate3D(
                    id = "gate_silas_wilds_${dayNumber}_$relayStep",
                    title = "Terra's Pepper Epoxy-Sap",
                    subtitle = "Concede to botany & seal the copper flange hermetically",
                    scientificDomain = "Anaerobic Polymer Chemistry",
                    squadAlignment = Squad.WILDS,
                    position = Vec3(0f, 0f, 15f),
                    projectId = "genesis_garden",
                    projectDelta = 9,
                    stressDelta = -6,
                    moraleDelta = 6,
                    nextPovId = "kaio",
                    nextBiomeId = Biome3D.ARBORETUM.id,
                    outcomeNarrative = "Swallowing your metallurgist pride, you coat the copper-iron junction in Terra's green pepper-scented epoxy-sap. Oxygen is cut off instantly—a truce between steel and sap.",
                    batonPassQuote = "\"The particulates don't care what you call them, son. Now let's see what Kaio and Zara dragged out of the jungle.\""
                ),
                ChoiceGate3D(
                    id = "gate_silas_hub_${dayNumber}_$relayStep",
                    title = "Recalibrate Shield Harmonics",
                    subtitle = "Route arboretum micro-currents into Augusta's Living Atlas",
                    scientificDomain = "Magnetohydrodynamic Grounding",
                    squadAlignment = Squad.HUB,
                    position = Vec3(6.5f, 0f, 13f),
                    projectId = "living_atlas",
                    projectDelta = 7,
                    stressDelta = 4,
                    moraleDelta = 4,
                    nextPovId = "augusta",
                    nextBiomeId = Biome3D.ATLAS_CHAMBER.id,
                    outcomeNarrative = "You attach a braided copper ground strap to the irrigation line, turning the corroding pipe into a crustal flux antenna for Augusta's holographic projector.",
                    batonPassQuote = "\"Augusta! The arboretum line is picking up a 0.4 vector tear in Sector 4!\""
                )
            )

            "kaio" -> listOf(
                ChoiceGate3D(
                    id = "gate_kaio_wilds_${dayNumber}_$relayStep",
                    title = "Zero-Polarization Flashbang",
                    subtitle = "Unleash the prime-number fungal strobe to dazzle the predator",
                    scientificDomain = "Luciferin Enzymatic Photons",
                    squadAlignment = Squad.WILDS,
                    position = Vec3(-6.5f, 0f, 13.5f),
                    projectId = "sanctuary",
                    projectDelta = 10,
                    stressDelta = 8,
                    moraleDelta = 7,
                    nextPovId = "augusta",
                    nextBiomeId = Biome3D.ATLAS_CHAMBER.id,
                    outcomeNarrative = "You twist the canister filter to zero! Blue-blue-green-WHITE! The fungal mat fires a prime-number photon burst, dazzling the hex-pedal predator while Zara lands a clean tranquilizer dart.",
                    batonPassQuote = "\"Flashbang mushroom! Nature provides, Zara! Let's get this sequence to Augusta before the Blue Dwarf wipes it!\""
                ),
                ChoiceGate3D(
                    id = "gate_kaio_mind_${dayNumber}_$relayStep",
                    title = "Mirror the Photophore Syntax",
                    subtitle = "Pulse your suit LEDs in prime numbers to negotiate peace",
                    scientificDomain = "Mycelial Bio-Semiotics",
                    squadAlignment = Squad.MIND,
                    position = Vec3(0f, 0f, 15.5f),
                    projectId = "mycelial_concordance",
                    projectDelta = 9,
                    stressDelta = -4,
                    moraleDelta = 8,
                    nextPovId = "willow",
                    nextBiomeId = Biome3D.ARBORETUM.id,
                    outcomeNarrative = "Instead of blinding the beast, you blink your chest lamp in 2-3-5-7 prime intervals. The predator tilts its iridescent carapace, recognizing the forest's native logic gate, and melts peacefully into the ferro-moss.",
                    batonPassQuote = "\"Willow! Put down the compost fork—this geode speaks boolean logic!\""
                ),
                ChoiceGate3D(
                    id = "gate_kaio_forge_${dayNumber}_$relayStep",
                    title = "Sprint the Magnetic Ridge",
                    subtitle = "Outrun the Blue Dwarf solar flare to preserve raw bio-telemetry",
                    scientificDomain = "Signal Decay & UV Shielding",
                    squadAlignment = Squad.FORGE,
                    position = Vec3(6.5f, 0f, 13.5f),
                    projectId = "simulacrum",
                    projectDelta = 8,
                    stressDelta = 10,
                    moraleDelta = 4,
                    nextPovId = "augusta",
                    nextBiomeId = Biome3D.ATLAS_CHAMBER.id,
                    outcomeNarrative = "Skidding over metallic bark, you wrap the specimen in your copper-lined poncho and vault the ridge just as ultraviolet auroras buckle the canopy above.",
                    batonPassQuote = "\"Augusta! Lock the Living Atlas onto Sector 4—the sky is about to drop a hammer!\""
                )
            )

            "augusta" -> listOf(
                ChoiceGate3D(
                    id = "gate_augusta_hub_${dayNumber}_$relayStep",
                    title = "Ground Every Spoon",
                    subtitle = "Issue a Hub Veto to ground the entire Enclave & dining hall",
                    scientificDomain = "Faraday Force-Field Harmonics",
                    squadAlignment = Squad.HUB,
                    position = Vec3(-6.5f, 0f, 13.5f),
                    projectId = "living_atlas",
                    projectDelta = 9,
                    stressDelta = -5,
                    moraleDelta = 6,
                    nextPovId = "hector",
                    nextBiomeId = Biome3D.STORMBREAKER.id,
                    outcomeNarrative = "You slam the Hub Veto circuit breaker. Every table, conduit, and soup spoon in the Enclave is shunted to bedrock just as Sector 4's magnetic field lines snarl into a gigajoule tear.",
                    batonPassQuote = "\"Alert Leo and Ryn: ground everything, even the spoons. Hector is already on the roof!\""
                ),
                ChoiceGate3D(
                    id = "gate_augusta_forge_${dayNumber}_$relayStep",
                    title = "Overclock the Stormbreaker",
                    subtitle = "Feed Sector 4 coordinates directly to Jane's femtosecond laser",
                    scientificDomain = "Atmospheric Ionization Routing",
                    squadAlignment = Squad.FORGE,
                    position = Vec3(0f, 0f, 15.5f),
                    projectId = "stormbreaker",
                    projectDelta = 11,
                    stressDelta = 12,
                    moraleDelta = 5,
                    nextPovId = "hector",
                    nextBiomeId = Biome3D.STORMBREAKER.id,
                    outcomeNarrative = "Your fingers fly across the crystalline projector, piping real-time Ediacaran pole-wander vectors straight into Hector and Jane's targeting array.",
                    batonPassQuote = "\"Hector! Jane! You have a 50 kilovolt-per-meter step leader forming directly overhead!\""
                ),
                ChoiceGate3D(
                    id = "gate_augusta_mind_${dayNumber}_$relayStep",
                    title = "Etch the Anomaly in 5D Quartz",
                    subtitle = "Stream the bifurcation data to Isla's femtosecond crystal archive",
                    scientificDomain = "5D Optical Nanostructures",
                    squadAlignment = Squad.MIND,
                    position = Vec3(6.5f, 0f, 13.5f),
                    projectId = "simulacrum",
                    projectDelta = 9,
                    stressDelta = 2,
                    moraleDelta = 5,
                    nextPovId = "isla",
                    nextBiomeId = Biome3D.ATLAS_CHAMBER.id,
                    outcomeNarrative = "Even as static raises the hairs on your arms, you route terabytes of magnetic bifurcation data into Isla's eternal quartz sliver so the Enclave never faces this blind again.",
                    batonPassQuote = "\"Isla, open the Simulacrum write stream—terabytes per second incoming!\""
                )
            )

            "hector" -> listOf(
                ChoiceGate3D(
                    id = "gate_hector_forge_${dayNumber}_$relayStep",
                    title = "Fire the Pilot Laser",
                    subtitle = "Ionize a plasma highway into the bedrock containment geode",
                    scientificDomain = "Laser-Induced Plasma Channel",
                    squadAlignment = Squad.FORGE,
                    position = Vec3(-6.5f, 0f, 13.5f),
                    projectId = "stormbreaker",
                    projectDelta = 12,
                    stressDelta = 15,
                    moraleDelta = 8,
                    nextPovId = "leo",
                    nextBiomeId = Biome3D.FORGE_HALL.id,
                    outcomeNarrative = "A beam of violet femtosecond light pierces the bruised purple clouds! A solid pillar of white fire slams down the plasma channel, filling the bedrock geode to 60% capacity.",
                    batonPassQuote = "\"Let it sing, Jane! Leo—the geode is expanding, get the ferrofluid collar ready!\""
                ),
                ChoiceGate3D(
                    id = "gate_hector_mind_${dayNumber}_$relayStep",
                    title = "Trust Jane's Butterfly Code",
                    subtitle = "Hand manual override to Jane's Lorenz attractor compiler",
                    scientificDomain = "Deterministic Chaos Control",
                    squadAlignment = Squad.MIND,
                    position = Vec3(0f, 0f, 15.5f),
                    projectId = "stormbreaker",
                    projectDelta = 10,
                    stressDelta = -8,
                    moraleDelta = 9,
                    nextPovId = "kenna",
                    nextBiomeId = Biome3D.FORGE_HALL.id,
                    outcomeNarrative = "When your hands freeze over the 0.5-degree crooked cable, you let go and trust your prodigy. Jane's compiler severs the arc cleanly at 98% efficiency.",
                    batonPassQuote = "\"Reality hacked, Hector. Now get down to Kenna before the magnetic static scrambles your pineal gland.\""
                ),
                ChoiceGate3D(
                    id = "gate_hector_hub_${dayNumber}_$relayStep",
                    title = "Bleed Charge to Ground Rods",
                    subtitle = "Prioritize structural safety over maximum battery capture",
                    scientificDomain = "Dielectric Surge Dissipation",
                    squadAlignment = Squad.HUB,
                    position = Vec3(6.5f, 0f, 13.5f),
                    projectId = "first_forge",
                    projectDelta = 7,
                    stressDelta = -10,
                    moraleDelta = 6,
                    nextPovId = "ryn",
                    nextBiomeId = Biome3D.FORGE_HALL.id,
                    outcomeNarrative = "You shunt the excess gigajoules harmlessly into the outer copper ground array. The lights in Ryn's kitchen flicker once, then steady to a warm golden glow.",
                    batonPassQuote = "\"Tower is secure! Somebody tell Ryn to save me a bowl of that popcorn-mushroom stew!\""
                )
            )

            "leo" -> listOf(
                ChoiceGate3D(
                    id = "gate_leo_forge_${dayNumber}_$relayStep",
                    title = "Harmonic Ferrofluid Collar",
                    subtitle = "Modulate magnetic carrier viscosity to absorb geode shockwaves",
                    scientificDomain = "Magnetorheological Fluid Dynamics",
                    squadAlignment = Squad.FORGE,
                    position = Vec3(-6.5f, 0f, 13.5f),
                    projectId = "first_forge",
                    projectDelta = 11,
                    stressDelta = -4,
                    moraleDelta = 7,
                    nextPovId = "silas",
                    nextBiomeId = Biome3D.FORGE_HALL.id,
                    outcomeNarrative = "You hum a low bass note and dial the electromagnet array. The black ferrofluid blooms into symmetric Rosensweig spikes, cushioning the expanding quartz geode without cracking a single weld.",
                    batonPassQuote = "\"See, Silas? You don't fight the pressure—you give it a dance floor!\""
                ),
                ChoiceGate3D(
                    id = "gate_leo_hub_${dayNumber}_$relayStep",
                    title = "Induction Skillet Bus-Bar",
                    subtitle = "Tap excess forge eddy currents to heat Ryn's dining hall cauldrons",
                    scientificDomain = "Joule Heating & Eddy Currents",
                    squadAlignment = Squad.HUB,
                    position = Vec3(0f, 0f, 15.5f),
                    projectId = "living_atlas",
                    projectDelta = 8,
                    stressDelta = -6,
                    moraleDelta = 9,
                    nextPovId = "ryn",
                    nextBiomeId = Biome3D.FORGE_HALL.id,
                    outcomeNarrative = "With a booming laugh, you clamp a copper bus-bar from the quench tank straight under Ryn's cast-iron stockpot, bringing 40 liters of broth to a rolling boil in ninety seconds.",
                    batonPassQuote = "\"Ryn! Thermal efficiency is up 22%! Don't say The Forge never feeds The Hub!\""
                ),
                ChoiceGate3D(
                    id = "gate_leo_wilds_${dayNumber}_$relayStep",
                    title = "Slag-Mineral Soil Amendment",
                    subtitle = "Pulverize iron-rich forge slag for Terra's nitrogen-fixing beds",
                    scientificDomain = "Trace-Element Metallurgy",
                    squadAlignment = Squad.WILDS,
                    position = Vec3(6.5f, 0f, 13.5f),
                    projectId = "genesis_garden",
                    projectDelta = 9,
                    stressDelta = 4,
                    moraleDelta = 6,
                    nextPovId = "terra",
                    nextBiomeId = Biome3D.ARBORETUM.id,
                    outcomeNarrative = "You crush the cooled chelated iron slag under your power-hammer and haul two warm sacks down to the Arboretum.",
                    batonPassQuote = "\"Fresh iron ions for the Genesis Garden, Terra! Catch!\""
                )
            )

            "zara" -> listOf(
                ChoiceGate3D(
                    id = "gate_zara_wilds_${dayNumber}_$relayStep",
                    title = "Sub-Dermal Biopsy Dart",
                    subtitle = "Sample the hex-predator's chitin without breaking stride",
                    scientificDomain = "Exoskeletal Piezoelectricity",
                    squadAlignment = Squad.WILDS,
                    position = Vec3(-6.5f, 0f, 13.5f),
                    projectId = "sanctuary",
                    projectDelta = 11,
                    stressDelta = 5,
                    moraleDelta = 6,
                    nextPovId = "kaio",
                    nextBiomeId = Biome3D.ARBORETUM.id,
                    outcomeNarrative = "Exhaling once, you fire from a crouch. The biopsy needle glances off the beast's dorsal ridge and snaps back on its micro-tether with a clean sample of piezoelectric chitin.",
                    batonPassQuote = "\"Got the tissue core. Kaio, stop naming it and move!\""
                ),
                ChoiceGate3D(
                    id = "gate_zara_forge_${dayNumber}_$relayStep",
                    title = "Rig Tension Zipline Across Gorge",
                    subtitle = "Anchor spider-silk composite cable across the basalt chasm",
                    scientificDomain = "Tensile Shear Mechanics",
                    squadAlignment = Squad.FORGE,
                    position = Vec3(0f, 0f, 15.5f),
                    projectId = "first_forge",
                    projectDelta = 9,
                    stressDelta = 8,
                    moraleDelta = 5,
                    nextPovId = "cassia",
                    nextBiomeId = Biome3D.DEEP_AQUIFER.id,
                    outcomeNarrative = "You sink two titanium pitons into the wet basalt and test the line with your full weight. A 30-minute detour becomes a 12-second drop into the Deep Aquifer.",
                    batonPassQuote = "\"Line is taut. Cassia, the lower sinkhole is open for acoustic mapping.\""
                ),
                ChoiceGate3D(
                    id = "gate_zara_hub_${dayNumber}_$relayStep",
                    title = "Deploy Perimeter Motion Beacons",
                    subtitle = "Ring Sector 4 ridge with seismic trip-sensors before dusk",
                    scientificDomain = "Geophone Acoustic Triangulation",
                    squadAlignment = Squad.HUB,
                    position = Vec3(6.5f, 0f, 13.5f),
                    projectId = "living_atlas",
                    projectDelta = 8,
                    stressDelta = -3,
                    moraleDelta = 7,
                    nextPovId = "augusta",
                    nextBiomeId = Biome3D.ATLAS_CHAMBER.id,
                    outcomeNarrative = "Three thumb-sized geophones click into the ironwood roots, streaming live footstep vectors straight to Augusta's war table.",
                    batonPassQuote = "\"Ridge perimeter locked, Augusta. Nothing crosses Sector 4 unseen.\""
                )
            )

            "cassia" -> listOf(
                ChoiceGate3D(
                    id = "gate_cassia_wilds_${dayNumber}_$relayStep",
                    title = "Seismic Thumper Ping",
                    subtitle = "Map the subterranean quartz cavern beneath the Enclave",
                    scientificDomain = "P-Wave Refraction Tomography",
                    squadAlignment = Squad.WILDS,
                    position = Vec3(-6.5f, 0f, 13.5f),
                    projectId = "deep_aquifer",
                    projectDelta = 11,
                    stressDelta = 4,
                    moraleDelta = 6,
                    nextPovId = "elias",
                    nextBiomeId = Biome3D.DEEP_AQUIFER.id,
                    outcomeNarrative = "Thoom! The acoustic return paints a cathedral-sized heavy-water cavern lined with piezoelectric quartz veins directly beneath the Forge.",
                    batonPassQuote = "\"Elias! Listen to that echo—there's an entire pre-Ediacaran inscription shelf down here!\""
                ),
                ChoiceGate3D(
                    id = "gate_cassia_forge_${dayNumber}_$relayStep",
                    title = "Core-Drill the Piezo Vein",
                    subtitle = "Extract resonant quartz rods for Hector's Stormbreaker capacitor",
                    scientificDomain = "Piezoelectric Crystal Lattices",
                    squadAlignment = Squad.FORGE,
                    position = Vec3(0f, 0f, 15.5f),
                    projectId = "stormbreaker",
                    projectDelta = 10,
                    stressDelta = 7,
                    moraleDelta = 5,
                    nextPovId = "hector",
                    nextBiomeId = Biome3D.STORMBREAKER.id,
                    outcomeNarrative = "Your diamond-tipped corer pulls a flawless hexagonal quartz cylinder from the aquifer wall, humming at 432 Hz under thumb pressure.",
                    batonPassQuote = "\"Hector, catch this crystal core—it'll take 80 megajoules without fracturing!\""
                ),
                ChoiceGate3D(
                    id = "gate_cassia_mind_${dayNumber}_$relayStep",
                    title = "Filter Heavy-Water Isotope",
                    subtitle = "Separate deuterium-enriched spring water for Kenna's lab",
                    scientificDomain = "Hydrogeological Isotope Fractionation",
                    squadAlignment = Squad.MIND,
                    position = Vec3(6.5f, 0f, 13.5f),
                    projectId = "lyceum_mind",
                    projectDelta = 9,
                    stressDelta = -5,
                    moraleDelta = 8,
                    nextPovId = "kenna",
                    nextBiomeId = Biome3D.DEEP_AQUIFER.id,
                    outcomeNarrative = "You divert the cold artesian seep through a ceramic membrane, bottling pure mineral-balanced water that calms magnetic vertigo.",
                    batonPassQuote = "\"Kenna, fresh aquifer draw—zero ferric particulates.\""
                )
            )

            "ryn" -> listOf(
                ChoiceGate3D(
                    id = "gate_ryn_hub_${dayNumber}_$relayStep",
                    title = "Maillard Chitin Reduction",
                    subtitle = "Roast alien flash-fungi with methional tubers at 155°C",
                    scientificDomain = "Molecular Gastrophysics",
                    squadAlignment = Squad.HUB,
                    position = Vec3(-6.5f, 0f, 13.5f),
                    projectId = "genesis_garden",
                    projectDelta = 10,
                    stressDelta = -8,
                    moraleDelta = 12,
                    nextPovId = "terra",
                    nextBiomeId = Biome3D.FORGE_HALL.id,
                    outcomeNarrative = "Amino acids and reducing sugars collide in your grounded copper wok! The whole Enclave fills with the unmistakable aroma of buttered movie-theater popcorn and roast beef.",
                    batonPassQuote = "\"Terra! Bring me two more baskets of those methional tubers—nobody skips dinner tonight!\""
                ),
                ChoiceGate3D(
                    id = "gate_ryn_mind_${dayNumber}_$relayStep",
                    title = "Enforce Mandatory Hydration",
                    subtitle = "March into the labs with electrolyte broth for the sleep-deprived",
                    scientificDomain = "Neuro-Metabolic Homeostasis",
                    squadAlignment = Squad.MIND,
                    position = Vec3(0f, 0f, 15.5f),
                    projectId = "lyceum_mind",
                    projectDelta = 9,
                    stressDelta = -10,
                    moraleDelta = 10,
                    nextPovId = "isla",
                    nextBiomeId = Biome3D.ATLAS_CHAMBER.id,
                    outcomeNarrative = "You slap warm ceramic mugs beside Isla and Jane's keyboards, refusing to budge until every drop of magnesium-rich broth is gone.",
                    batonPassQuote = "\"Drink it, Isla. Your 5D quartz archive won't compile if your sodium-potassium pumps fail.\""
                ),
                ChoiceGate3D(
                    id = "gate_ryn_forge_${dayNumber}_$relayStep",
                    title = "Sanitize the Bio-Fermenters",
                    subtitle = "Purge rogue spores from the high-pressure autoclave vats",
                    scientificDomain = "Thermophilic Zymology",
                    squadAlignment = Squad.FORGE,
                    position = Vec3(6.5f, 0f, 13.5f),
                    projectId = "first_forge",
                    projectDelta = 8,
                    stressDelta = 6,
                    moraleDelta = 7,
                    nextPovId = "silas",
                    nextBiomeId = Biome3D.FORGE_HALL.id,
                    outcomeNarrative = "Steam hisses at 121°C as you torque the brass autoclave seals shut, saving next week's miso-yeast culture from Kaio's unwashed boots.",
                    batonPassQuote = "\"Silas! Keep Kaio out of my fermentation bay or he's on dish duty for a month!\""
                )
            )

            "vex" -> listOf(
                ChoiceGate3D(
                    id = "gate_vex_hub_${dayNumber}_$relayStep",
                    title = "Chart Binary Flare Cross-Polarity",
                    subtitle = "Calculate exact minute the Red Giant & Blue Dwarf magnetic loops collide",
                    scientificDomain = "Binary Magnetohydrodynamics",
                    squadAlignment = Squad.HUB,
                    position = Vec3(-6.5f, 0f, 13.5f),
                    projectId = "living_atlas",
                    projectDelta = 11,
                    stressDelta = 5,
                    moraleDelta = 7,
                    nextPovId = "augusta",
                    nextBiomeId = Biome3D.STORMBREAKER.id,
                    outcomeNarrative = "Lounging on the observatory rail, you flick a holographicorrery: \"Good news, darlings—coronal mass ejection arrives in 14 minutes, which means my horoscope was right to the microsecond.\"",
                    batonPassQuote = "\"Augusta, darling, adjust your shield phase by 17 degrees or we'll all glow in the dark.\""
                ),
                ChoiceGate3D(
                    id = "gate_vex_mind_${dayNumber}_$relayStep",
                    title = "Publish 'Twin-Sun Horoscopes'",
                    subtitle = "Broadcast eerily accurate psychological predictions across the comms",
                    scientificDomain = "Circadian Magnetoreception",
                    squadAlignment = Squad.MIND,
                    position = Vec3(0f, 0f, 15.5f),
                    projectId = "lyceum_mind",
                    projectDelta = 9,
                    stressDelta = -7,
                    moraleDelta = 9,
                    nextPovId = "kenna",
                    nextBiomeId = Biome3D.ATLAS_CHAMBER.id,
                    outcomeNarrative = "Half the Enclave groans on the comm-link while secretly checking their star sign, diffusing hours of pent-up lab tension in thirty seconds.",
                    batonPassQuote = "\"Admit it, Kenna—calling it 'Taurus Rising' works twice as fast as 'pineal magnetoreception'!\""
                ),
                ChoiceGate3D(
                    id = "gate_vex_forge_${dayNumber}_$relayStep",
                    title = "Align the Ionospheric Mirror",
                    subtitle = "Bounce high-frequency telemetry off the auroral plasma curtain",
                    scientificDomain = "Ionospheric Skywave Propagation",
                    squadAlignment = Squad.FORGE,
                    position = Vec3(6.5f, 0f, 13.5f),
                    projectId = "stormbreaker",
                    projectDelta = 10,
                    stressDelta = 6,
                    moraleDelta = 6,
                    nextPovId = "jane",
                    nextBiomeId = Biome3D.STORMBREAKER.id,
                    outcomeNarrative = "You angle the dish into the violet aurora, turning the magnetic storm itself into a planetary-scale parabolic reflector.",
                    batonPassQuote = "\"Jane! I just gave your compiler a 400-kilometer antenna—don't waste it!\""
                )
            )

            "jane" -> listOf(
                ChoiceGate3D(
                    id = "gate_jane_forge_${dayNumber}_$relayStep",
                    title = "Compile Lorenz Attractor Patch",
                    subtitle = "Predict lightning step-leader forks 40 milliseconds before strike",
                    scientificDomain = "Deterministic Chaos & Non-Linear Dynamics",
                    squadAlignment = Squad.FORGE,
                    position = Vec3(-6.5f, 0f, 13.5f),
                    projectId = "stormbreaker",
                    projectDelta = 12,
                    stressDelta = 8,
                    moraleDelta = 7,
                    nextPovId = "hector",
                    nextBiomeId = Biome3D.STORMBREAKER.id,
                    outcomeNarrative = "Chewing bubble-gum-flavored root bark, you hit Enter with your pinky. The femtosecond laser pre-fires into empty air—and catches the lightning bolt mid-fork.",
                    batonPassQuote = "\"Boom. Math 1, Sky 0. Hector, check the capacitor readout!\""
                ),
                ChoiceGate3D(
                    id = "gate_jane_mind_${dayNumber}_$relayStep",
                    title = "Compress the 5D Hologram Kernel",
                    subtitle = "Shave 60% latency off Isla's quartz optical read/write head",
                    scientificDomain = "Kolmogorov Complexity & Coding Theory",
                    squadAlignment = Squad.MIND,
                    position = Vec3(0f, 0f, 15.5f),
                    projectId = "simulacrum",
                    projectDelta = 11,
                    stressDelta = -3,
                    moraleDelta = 8,
                    nextPovId = "isla",
                    nextBiomeId = Biome3D.ATLAS_CHAMBER.id,
                    outcomeNarrative = "In twelve lines of recursive assembly, you eliminate the birefringence jitter that was blurring the Simulacrum's memory crystals.",
                    batonPassQuote = "\"Isla, your archive now runs at 120 frames per second. You're welcome!\""
                ),
                ChoiceGate3D(
                    id = "gate_jane_hub_${dayNumber}_$relayStep",
                    title = "Trace the Sibling Grid Packet",
                    subtitle = "Sniff the encrypted prank routing table without snitching to Augusta",
                    scientificDomain = "Zero-Knowledge Packet Routing",
                    squadAlignment = Squad.HUB,
                    position = Vec3(6.5f, 0f, 13.5f),
                    projectId = "living_atlas",
                    projectDelta = 8,
                    stressDelta = -6,
                    moraleDelta = 9,
                    nextPovId = "vex",
                    nextBiomeId = Biome3D.ATLAS_CHAMBER.id,
                    outcomeNarrative = "You spot Kaio's prank packet bouncing through three proxy nodes, add a harmless ascii smiley face to the header, and route it safely away from life-support.",
                    batonPassQuote = "\"Vex, your firewall has a Kaio-shaped hole in it!\""
                )
            )

            "kenna" -> listOf(
                ChoiceGate3D(
                    id = "gate_kenna_mind_${dayNumber}_$relayStep",
                    title = "Schumann Resonance Recoupling",
                    subtitle = "Flood the habitat with 7.83 Hz alpha-wave electromagnetic grounding",
                    scientificDomain = "Magnetoreceptive Neurobiology",
                    squadAlignment = Squad.MIND,
                    position = Vec3(-6.5f, 0f, 13.5f),
                    projectId = "lyceum_mind",
                    projectDelta = 11,
                    stressDelta = -14,
                    moraleDelta = 11,
                    nextPovId = "orion",
                    nextBiomeId = Biome3D.ATLAS_CHAMBER.id,
                    outcomeNarrative = "You engage the Helmholtz coils in the ceiling. Across the Enclave, clenched jaws relax as artificial Earth-normal 7.83 Hz resonance shields the crew's cryptochrome proteins from twin-sun static.",
                    batonPassQuote = "\"Orion, lock your cymatic bass drone onto 7.83 Hertz—let's bring everyone's cortisol down.\""
                ),
                ChoiceGate3D(
                    id = "gate_kenna_wilds_${dayNumber}_$relayStep",
                    title = "Synthesize Adaptogen Tea",
                    subtitle = "Distill neuro-protective alkaloids from Willow's glowing moss",
                    scientificDomain = "Psychopharmacology & Cortisol Modulation",
                    squadAlignment = Squad.WILDS,
                    position = Vec3(0f, 0f, 15.5f),
                    projectId = "genesis_garden",
                    projectDelta = 9,
                    stressDelta = -10,
                    moraleDelta = 9,
                    nextPovId = "willow",
                    nextBiomeId = Biome3D.ARBORETUM.id,
                    outcomeNarrative = "You titrate the silver-green moss extract to the milligram, creating a calming infusion that prevents Vex Override without dulling reflexes.",
                    batonPassQuote = "\"Willow, this moss strain is a miracle—have you tested its root signaling yet?\""
                ),
                ChoiceGate3D(
                    id = "gate_kenna_hub_${dayNumber}_$relayStep",
                    title = "Debrief Hector's Perfectionism Freeze",
                    subtitle = "Walk Hector through somatic decompression after the laser test",
                    scientificDomain = "Prefrontal Executive Recovery",
                    squadAlignment = Squad.HUB,
                    position = Vec3(6.5f, 0f, 13.5f),
                    projectId = "stormbreaker",
                    projectDelta = 8,
                    stressDelta = -12,
                    moraleDelta = 8,
                    nextPovId = "hector",
                    nextBiomeId = Biome3D.STORMBREAKER.id,
                    outcomeNarrative = "With quiet warmth, you talk Hector down from re-measuring the 0.5-degree cable for the ninth time, restoring our Chief Architect's focus.",
                    batonPassQuote = "\"The tower held, Hector. Breathe in for four, out for six.\""
                )
            )

            "elias" -> listOf(
                ChoiceGate3D(
                    id = "gate_elias_mind_${dayNumber}_$relayStep",
                    title = "Parse the Mycelial Syntax Tree",
                    subtitle = "Map the forest's calcium-ion pulses onto Chomsky hierarchy grammar",
                    scientificDomain = "Computational Bio-Linguistics",
                    squadAlignment = Squad.MIND,
                    position = Vec3(-6.5f, 0f, 13.5f),
                    projectId = "mycelial_concordance",
                    projectDelta = 12,
                    stressDelta = -4,
                    moraleDelta = 8,
                    nextPovId = "willow",
                    nextBiomeId = Biome3D.ARBORETUM.id,
                    outcomeNarrative = "Adjusting your spectacles, you realize the fungal network isn't broadcasting nouns—it conjugates entire tenses based on barometric pressure!",
                    batonPassQuote = "\"Willow! The forest has a future-subjunctive tense—it's predicting tomorrow's storm!\""
                ),
                ChoiceGate3D(
                    id = "gate_elias_hub_${dayNumber}_$relayStep",
                    title = "Standardize Enclave Jargon Creole",
                    subtitle = "Reconcile Silas's metallurgy terms with Terra's botanical taxonomy",
                    scientificDomain = "Pragmatics & Code-Switching",
                    squadAlignment = Squad.HUB,
                    position = Vec3(0f, 0f, 15.5f),
                    projectId = "living_atlas",
                    projectDelta = 9,
                    stressDelta = -6,
                    moraleDelta = 9,
                    nextPovId = "silas",
                    nextBiomeId = Biome3D.FORGE_HALL.id,
                    outcomeNarrative = "You update the Living Atlas glossary so when The Wilds says 'vascular turgor' and The Forge says 'hydraulic head,' the HUD translates both in real time.",
                    batonPassQuote = "\"Silas, Terra wasn't insulting your pipe—she was complimenting its osmotic seal!\""
                ),
                ChoiceGate3D(
                    id = "gate_elias_wilds_${dayNumber}_$relayStep",
                    title = "Transcribe Acoustic Petroglyphs",
                    subtitle = "Record the wind-carved resonance flutes in the Deep Aquifer",
                    scientificDomain = "Archaeo-Acoustics & Phonology",
                    squadAlignment = Squad.WILDS,
                    position = Vec3(6.5f, 0f, 13.5f),
                    projectId = "deep_aquifer",
                    projectDelta = 9,
                    stressDelta = 2,
                    moraleDelta = 7,
                    nextPovId = "cassia",
                    nextBiomeId = Biome3D.DEEP_AQUIFER.id,
                    outcomeNarrative = "The basalt flutes sing a five-tone pentatonic scale whenever aquifer water rises—a natural flood-warning siren carved by eons of wind.",
                    batonPassQuote = "\"Cassia, the cavern walls themselves are a hydro-barometer!\""
                )
            )

            "willow" -> listOf(
                ChoiceGate3D(
                    id = "gate_willow_wilds_${dayNumber}_$relayStep",
                    title = "Splice the Mycelial Motherboard",
                    subtitle = "Graft silver-thread hyphae into the Enclave's optical sensor grid",
                    scientificDomain = "Mycorrhizal Action Potentials",
                    squadAlignment = Squad.WILDS,
                    position = Vec3(-6.5f, 0f, 13.5f),
                    projectId = "mycelial_concordance",
                    projectDelta = 12,
                    stressDelta = -6,
                    moraleDelta = 9,
                    nextPovId = "elias",
                    nextBiomeId = Biome3D.ARBORETUM.id,
                    outcomeNarrative = "Barefoot in the warm humus, you coax the glowing fungal threads around the gold electrode. The entire valley's root network lights up on our monitors like a living neural cortex.",
                    batonPassQuote = "\"Elias, listen softly—the old ironwood trees on the ridge are whispering.\""
                ),
                ChoiceGate3D(
                    id = "gate_willow_mind_${dayNumber}_$relayStep",
                    title = "Cultivate Bioluminescent Night-Lamps",
                    subtitle = "Replace harsh LEDs with zero-draw luciferin moss globes",
                    scientificDomain = "Symbiotic Photobiology",
                    squadAlignment = Squad.MIND,
                    position = Vec3(0f, 0f, 15.5f),
                    projectId = "lyceum_mind",
                    projectDelta = 9,
                    stressDelta = -9,
                    moraleDelta = 10,
                    nextPovId = "orion",
                    nextBiomeId = Biome3D.ARBORETUM.id,
                    outcomeNarrative = "Soft cyan-green moss spheres float along the corridor ceilings, breathing oxygen while bathing the crew in soothing 490nm light.",
                    batonPassQuote = "\"Orion, watch how the moss brightens whenever you play that cello harmonic.\""
                ),
                ChoiceGate3D(
                    id = "gate_willow_forge_${dayNumber}_$relayStep",
                    title = "Grow Chitinous Insulation Foam",
                    subtitle = "Seed self-healing fungal mycelium inside the outer hull bulkheads",
                    scientificDomain = "Biopolymer Thermal Shielding",
                    squadAlignment = Squad.FORGE,
                    position = Vec3(6.5f, 0f, 13.5f),
                    projectId = "first_forge",
                    projectDelta = 10,
                    stressDelta = 3,
                    moraleDelta = 7,
                    nextPovId = "leo",
                    nextBiomeId = Biome3D.FORGE_HALL.id,
                    outcomeNarrative = "Within hours, the dense mycelial mat cures into a fireproof, acoustic-damping bulkhead liner stronger than polyurethane.",
                    batonPassQuote = "\"Leo, try hitting this fungal brick with your hammer—it absorbs 90% of the shock!\""
                )
            )

            "terra" -> listOf(
                ChoiceGate3D(
                    id = "gate_terra_wilds_${dayNumber}_$relayStep",
                    title = "Inoculate Nitrogen-Fixing Rhizobia",
                    subtitle = "Boost Kepler-Earth hybrid crop yield by 35% before the storm season",
                    scientificDomain = "Symbiotic Diazotrophic Agronomy",
                    squadAlignment = Squad.WILDS,
                    position = Vec3(-6.5f, 0f, 13.5f),
                    projectId = "genesis_garden",
                    projectDelta = 12,
                    stressDelta = -5,
                    moraleDelta = 9,
                    nextPovId = "ryn",
                    nextBiomeId = Biome3D.ARBORETUM.id,
                    outcomeNarrative = "With soil under your fingernails, you graft native iron-tolerant bacteria onto Earth heirloom legumes. The crimson-leafed vines surge three inches before sunset.",
                    batonPassQuote = "\"Ryn! Fresh snap-peas and pepper-tubers—don't over-salt them!\""
                ),
                ChoiceGate3D(
                    id = "gate_terra_forge_${dayNumber}_$relayStep",
                    title = "Distill Anaerobic Latex Sealant",
                    subtitle = "Tap vulcanizing sap from ironwood bark to patch high-pressure valves",
                    scientificDomain = "Phytochemical Elastomers",
                    squadAlignment = Squad.FORGE,
                    position = Vec3(0f, 0f, 15.5f),
                    projectId = "first_forge",
                    projectDelta = 9,
                    stressDelta = 4,
                    moraleDelta = 6,
                    nextPovId = "silas",
                    nextBiomeId = Biome3D.FORGE_HALL.id,
                    outcomeNarrative = "You hand Silas a jar of emerald-green tree resin that cross-links on contact with rust, sealing micro-fissures that welding torches would melt.",
                    batonPassQuote = "\"Here, Silas. My trees fixed what your wrench couldn't.\""
                ),
                ChoiceGate3D(
                    id = "gate_terra_hub_${dayNumber}_$relayStep",
                    title = "Balance the Greywater Transpiration Loop",
                    subtitle = "Route hydroponic humidity into the Deep Aquifer condenser",
                    scientificDomain = "Closed-Loop Evapotranspiration",
                    squadAlignment = Squad.HUB,
                    position = Vec3(6.5f, 0f, 13.5f),
                    projectId = "deep_aquifer",
                    projectDelta = 10,
                    stressDelta = -4,
                    moraleDelta = 7,
                    nextPovId = "cassia",
                    nextBiomeId = Biome3D.DEEP_AQUIFER.id,
                    outcomeNarrative = "The broad-leaf canopy acts as a living biological lung, scrubbing CO2 and condensing 200 liters of pure distilled water per cycle.",
                    batonPassQuote = "\"Cassia, the Arboretum condenser is feeding 99.9% pure water back to the cistern.\""
                )
            )

            "isla" -> listOf(
                ChoiceGate3D(
                    id = "gate_isla_mind_${dayNumber}_$relayStep",
                    title = "Inscribe 5D Quartz Memory Sliver",
                    subtitle = "Encode today's hard-science breakthroughs into eternal nanostructured glass",
                    scientificDomain = "Femtosecond Laser Birefringence",
                    squadAlignment = Squad.MIND,
                    position = Vec3(-6.5f, 0f, 13.5f),
                    projectId = "simulacrum",
                    projectDelta = 12,
                    stressDelta = -3,
                    moraleDelta = 8,
                    nextPovId = "jane",
                    nextBiomeId = Biome3D.ATLAS_CHAMBER.id,
                    outcomeNarrative = "Through cleanroom goggles, you watch the femtosecond beam etch five-dimensional voxels into fused quartz—an archive that will outlive the twin suns themselves.",
                    batonPassQuote = "\"Recorded for eternity, Jane. Even a direct coronal flare can't erase fused silica.\""
                ),
                ChoiceGate3D(
                    id = "gate_isla_hub_${dayNumber}_$relayStep",
                    title = "Cross-Index Historical Failure Modes",
                    subtitle = "Project past colony stress patterns onto Augusta's tactical map",
                    scientificDomain = "Cliodynamics & Archival Telemetry",
                    squadAlignment = Squad.HUB,
                    position = Vec3(0f, 0f, 15.5f),
                    projectId = "living_atlas",
                    projectDelta = 10,
                    stressDelta = -5,
                    moraleDelta = 7,
                    nextPovId = "augusta",
                    nextBiomeId = Biome3D.ATLAS_CHAMBER.id,
                    outcomeNarrative = "Your historical overlay reveals that every Sector 4 magnetic spike is preceded by a 12-minute drop in barometric pressure, giving The Hub an early warning trigger.",
                    batonPassQuote = "\"Augusta, look at the Day 1 overlay—the storm always telegraphs its punch.\""
                ),
                ChoiceGate3D(
                    id = "gate_isla_wilds_${dayNumber}_$relayStep",
                    title = "Holo-Scan the Sanctuary Fauna",
                    subtitle = "Build a non-invasive 3D anatomical atlas of native hex-vertebrates",
                    scientificDomain = "Optical Coherence Tomography",
                    squadAlignment = Squad.WILDS,
                    position = Vec3(6.5f, 0f, 13.5f),
                    projectId = "sanctuary",
                    projectDelta = 9,
                    stressDelta = -6,
                    moraleDelta = 8,
                    nextPovId = "zara",
                    nextBiomeId = Biome3D.ARBORETUM.id,
                    outcomeNarrative = "Your portable interferometer captures micron-resolution scans of the creature's magnetoreceptive inner ear without needing a single cage.",
                    batonPassQuote = "\"Zara, look at its inner ear—it navigates by riding magnetic field lines like rails!\""
                )
            )

            "orion" -> listOf(
                ChoiceGate3D(
                    id = "gate_orion_mind_${dayNumber}_$relayStep",
                    title = "Cymatic Standing-Wave Spire",
                    subtitle = "Sculpt ferrofluid & water into a 3D harmonic geometry to calm the crew",
                    scientificDomain = "Chladni Cymatics & Vibro-Acoustics",
                    squadAlignment = Squad.MIND,
                    position = Vec3(-6.5f, 0f, 13.5f),
                    projectId = "lyceum_mind",
                    projectDelta = 11,
                    stressDelta = -15,
                    moraleDelta = 12,
                    nextPovId = "kenna",
                    nextBiomeId = Biome3D.ATLAS_CHAMBER.id,
                    outcomeNarrative = "You draw your bow across the acoustic transducer plate. Liquid metal rises into a breathtaking cathedral of standing waves, synchronizing breathing rates across the hall.",
                    batonPassQuote = "\"Kenna, feel that C-major overtone—the static in the air just let go.\""
                ),
                ChoiceGate3D(
                    id = "gate_orion_forge_${dayNumber}_$relayStep",
                    title = "Acoustic Levitation Crucible",
                    subtitle = "Suspend molten alloy droplets mid-air using ultrasonic nodes",
                    scientificDomain = "Containerless Acoustic Levitation",
                    squadAlignment = Squad.FORGE,
                    position = Vec3(0f, 0f, 15.5f),
                    projectId = "first_forge",
                    projectDelta = 10,
                    stressDelta = 3,
                    moraleDelta = 8,
                    nextPovId = "leo",
                    nextBiomeId = Biome3D.FORGE_HALL.id,
                    outcomeNarrative = "A 40 kHz standing wave cradles the white-hot alloy bead in mid-air, preventing crucible contamination and yielding a zero-defect crystal lattice.",
                    batonPassQuote = "\"Leo, look—no crucible walls, zero impurities. Pure levitated metallurgy!\""
                ),
                ChoiceGate3D(
                    id = "gate_orion_wilds_${dayNumber}_$relayStep",
                    title = "Duet with the Canopy Stridulators",
                    subtitle = "Play back phase-inverted insect chorus to guide swarm migration",
                    scientificDomain = "Bio-Acoustic Phase Cancellation",
                    squadAlignment = Squad.WILDS,
                    position = Vec3(6.5f, 0f, 13.5f),
                    projectId = "sanctuary",
                    projectDelta = 10,
                    stressDelta = -7,
                    moraleDelta = 9,
                    nextPovId = "kaio",
                    nextBiomeId = Biome3D.ARBORETUM.id,
                    outcomeNarrative = "Matching the shimmer-wings' 2.4 kHz trill, you gently shepherd the entire bioluminescent swarm away from the intake turbines and back toward the river.",
                    batonPassQuote = "\"Kaio, you don't need a net when you know their key signature!\""
                )
            )

            else -> {
                val nextChar = initialCharacters[(initialCharacters.indexOfFirst { it.id == character.id } + 1) % initialCharacters.size]
                listOf(
                    ChoiceGate3D(
                        id = "gate_${character.id}_forge_${dayNumber}_$relayStep",
                        title = "Ferrofluid Dynamic Seal",
                        subtitle = "Modulate magnetic viscosity to contain the pressure spike",
                        scientificDomain = "Magnetorheological Physics",
                        squadAlignment = Squad.FORGE,
                        position = Vec3(-6.5f, 0f, 13.5f),
                        projectId = character.grandProjectId,
                        projectDelta = 9,
                        stressDelta = 6,
                        moraleDelta = 5,
                        nextPovId = nextChar.id,
                        nextBiomeId = Biome3D.FORGE_HALL.id,
                        outcomeNarrative = "${character.name} tunes the carrier fluid frequency, smoothing the Rosensweig spikes into a breathing non-Newtonian seal that holds the expanding crystal lattice.",
                        batonPassQuote = "\"It's not magic, ${nextChar.name}—it's physics. Your turn on the relay.\""
                    ),
                    ChoiceGate3D(
                        id = "gate_${character.id}_wilds_${dayNumber}_$relayStep",
                        title = "Decode the Hyphal Verb",
                        subtitle = "Transmit 'Listen' across the calcium-ion mycelial motherboard",
                        scientificDomain = "Mycorrhizal Signal Transduction",
                        squadAlignment = Squad.WILDS,
                        position = Vec3(0f, 0f, 15.5f),
                        projectId = "mycelial_concordance",
                        projectDelta = 10,
                        stressDelta = -4,
                        moraleDelta = 8,
                        nextPovId = nextChar.id,
                        nextBiomeId = Biome3D.DEEP_AQUIFER.id,
                        outcomeNarrative = "Through ${character.name}'s cognitive lens (${character.mbti}), the signal resolves from a noun into a verb. A rolling wave of green-gold bioluminescence answers across the valley: 'We hear.'",
                        batonPassQuote = "\"${nextChar.name}, look at the ground sensors—the entire forest just answered us!\""
                    ),
                    ChoiceGate3D(
                        id = "gate_${character.id}_mind_${dayNumber}_$relayStep",
                        title = "Cymatic Resonance Sync",
                        subtitle = "Harmonize the crew's circadian rhythm with a C-Major chord",
                        scientificDomain = "Cymatics & Neuro-Acoustics",
                        squadAlignment = Squad.MIND,
                        position = Vec3(6.5f, 0f, 13.5f),
                        projectId = "lyceum_mind",
                        projectDelta = 8,
                        stressDelta = -12,
                        moraleDelta = 10,
                        nextPovId = nextChar.id,
                        nextBiomeId = Biome3D.ATLAS_CHAMBER.id,
                        outcomeNarrative = "${character.name} channels the geomagnetic static into a breathtaking cymatic spire, decoupling the crew's stress receptors and restoring emotional equilibrium before the Feast.",
                        batonPassQuote = "\"Messy, sharp, and magnetic—that's us, ${nextChar.name}. See you at the Feast table.\""
                    )
                )
            }
        }

        val mobileAlignedGates = com.example.data.ai.PsychometricGuardrailEngine.enforceGateFidelity(
            activeChar = character,
            rawGates = baseGates.mapIndexed { idx, gate ->
                val adjustedX = when (idx) {
                    0 -> -4.5f
                    1 -> 0f
                    else -> 4.5f
                }
                gate.copy(position = gate.position.copy(x = adjustedX))
            },
            allCharacters = initialCharacters
        )

        return if (character.isVexInverted || character.stressLevel >= 75) {
            mobileAlignedGates + ChoiceGate3D(
                id = "gate_vex_${character.id}_${dayNumber}_$relayStep",
                title = "VEX OVERRIDE: ${character.vexOverrideTitle}",
                subtitle = "[${character.vexStarSign} Inversion] ${character.vexOverrideBehavior.take(68)}...",
                scientificDomain = "⚡ ${character.vexStarSign} Shadow Inversion",
                squadAlignment = Squad.MIND,
                position = Vec3(0f, 0f, 9.5f),
                isVexGate = true,
                projectId = character.grandProjectId,
                projectDelta = 14,
                stressDelta = -35,
                moraleDelta = 6,
                nextPovId = character.partnerNodeId,
                nextBiomeId = Biome3D.STORMBREAKER.id,
                outcomeNarrative = "VEX OVERRIDE ENGAGED! Under twin-sun magnetic cross-polarity, ${character.name}'s baseline ${character.mbti} personality flips completely into ${character.vexStarSign} (${character.vexOverrideTitle})! ${character.vexOverrideBehavior} Miraculously, the inverted gamble triggers a breakthrough!",
                batonPassQuote = "\"Don't look at me like that—I just optimized the variable! Take the baton!\""
            )
        } else {
            mobileAlignedGates
        }
    }

    val defaultFeastDialogue: List<FeastExchange> = listOf(
        FeastExchange(
            speakerId = "ryn",
            speakerName = "Ryn",
            roleTitle = "The Chef (ESTJ)",
            squad = Squad.HUB,
            dialogue = "Sit. Stirring velocity was 40 RPM clockwise at 72°C. Yes, Silas, your spoon is still grounded to your ankle. Eat.",
            scientificCheeseNote = "Gastrophysics: Methional volatile pairing + Maillard reaction simulates buttered popcorn from chitinous alien fungi."
        ),
        FeastExchange(
            speakerId = "silas",
            speakerName = "Silas",
            roleTitle = "The Engineer (ISTJ)",
            squad = Squad.FORGE,
            dialogue = "Soup tastes 14% more metallic with a braided copper ground strap on my boot. Also, the arboretum pipe held.",
            scientificCheeseNote = "Galvanic Protection: Sacrificial anode grounding prevents micro-current oxidation during solar flares."
        ),
        FeastExchange(
            speakerId = "kaio",
            speakerName = "Kaio",
            roleTitle = "The Scout (ENFP)",
            squad = Squad.WILDS,
            dialogue = "For the record, the sock I used on the water filter was sterilized! Mostly! And the mushroom literally flashed prime numbers at us!",
            scientificCheeseNote = "Third-Quarter Release Valve: Shared absurdity lowers group cortisol in isolated extreme environments."
        ),
        FeastExchange(
            speakerId = "vex",
            speakerName = "Vex",
            roleTitle = "The Astronomer (ENTP)",
            squad = Squad.HUB,
            dialogue = "You're all ignoring the obvious astrophysical truth: Mercury is spiritually in retrograde across two suns.",
            scientificCheeseNote = "Binary Magnetosphere: Opposing magnetic polarities of the Red Giant and Blue Dwarf compress the auroral oval."
        )
    )

    val hubVetoOptions: List<HubVetoOption> = listOf(
        HubVetoOption(
            id = "veto_forge",
            title = "Allocate Geode Joules to The First Forge",
            proponentName = "Leo & Silas",
            squad = Squad.FORGE,
            scientificArgument = "Use captured lightning megajoules to smelt sympathetic high-entropy shield plating and ferrofluid seals.",
            projectId = "first_forge",
            projectBoost = 12,
            moraleBoost = 6,
            fluxReduction = 15
        ),
        HubVetoOption(
            id = "veto_wilds",
            title = "Amplify the Mycelial Root Transmitter",
            proponentName = "Willow & Kaio",
            squad = Squad.WILDS,
            scientificArgument = "Route power into the valley fungal motherboard to translate the planet's biological weather warnings.",
            projectId = "mycelial_concordance",
            projectBoost = 12,
            moraleBoost = 8,
            fluxReduction = 10
        ),
        HubVetoOption(
            id = "veto_mind",
            title = "Power the 5D Simulacrum & Lyceum Tanks",
            proponentName = "Kenna, Isla & Orion",
            squad = Squad.MIND,
            scientificArgument = "Prioritize zero-dust sensory reset tanks and cymatic harmony to clear Vex Override stress across all 16 nodes.",
            projectId = "lyceum_mind",
            projectBoost = 12,
            moraleBoost = 14,
            fluxReduction = 20
        )
    )
}
