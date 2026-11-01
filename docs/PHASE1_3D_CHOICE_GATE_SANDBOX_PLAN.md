# Enclave of the 16: 3D Choice Gate Sandbox

An interactive 3D narrative world and sociological sci-fi sandbox based on *The Enclave of the 16*, where players explore an alien exoplanet in third-person and shape a living story by physically walking their character through glowing 3D **Choice Gates** rather than clicking text menus.

## User Review & Critical Decisions

> [!IMPORTANT]
> All core gameplay and narrative preferences from Phase 1 have been locked into this architecture. Please review the summary below and click **Proceed** when ready to build.

- **Confirmed Movement & Camera**: **Third-Person 3D Exploration** following the active POV character avatar, supporting both **WASD / Arrow keys + Mouse look** (for desktop/chromebook/emulator keyboard play) and **Mobile-Friendly Touch Controls** (virtual thumbstick + touch-drag camera orbit).
- **Confirmed Narrative Engine**: **Fully Procedural AI-Driven Sandbox Simulation** every day cycle powered by Firebase AI Logic (`gemini-3.8-flash`), dynamically generating hard-science crises ("Vegetables Covered in Cheese"), multi-disciplinary threats, and 3D Choice Gates grounded in the Master System Bible.
- **Confirmed Interactive Systems**: All five core systems from the Enclave Bible are integrated into the daily loop:
  1. **Rashomon POV Relay Switching** across all 16 polymaths (8 Mentors & 8 Prodigies).
  2. **Vex Override Stress Gauge & Personality Inversion** triggered by twin-sun magnetic flux.
  3. **Nightly Grand Feast Table Sync & Diplomacy** with Ryn's culinary chemistry and Hub Veto circuit-breakers.
  4. **Black Market Sibling Grid** with asynchronous 3-turn petty favors, pranks, and blind-spot chain reactions.
  5. **3D Living Atlas & Codex** covering all 8 Grand Projects and 16 psychometric profiles (Big 5, MBTI, Enneagram).

---

## 1. Overview & Core Concept

- **What It Does**: Transforms *The Enclave of the 16* into a playable 3D third-person narrative world. Players control the active Point-of-View (POV) genius in a stylized 3D exoplanet environment lit by a Red Giant and Blue Dwarf binary star system. When an environmental anomaly or scientific dispute arises ("Schrödinger's Harvest"), 2 to 4 physical **Choice Gates** materialize in the 3D terrain around the player—each representing a distinct scientific approach, squad philosophy, or Vex Override reaction. Walking the character avatar through a gate commits to that choice, seamlessly relays the POV baton to the next character, and advances the day toward the mandatory nightly **Grand Feast**.
- **Target Audience / Persona**: Fans of hard sci-fi competence (*The Martian*), cozy found-family banter (*Schitt's Creek*), and interactive fiction (*Choice of Games*) who want spatial, embodied decision-making in a 3D world.
- **Key Value**: Replaces static button-list text choices with spatial 3D exploration, physical Choice Gate portals, and an autonomous multi-agent AI simulation that stays true to each character's psychometric profile.

---

## 2. User Experience & Visual Design

### Key User Flows

1. **Authentication & Enclave Initialization**:
   - Players sign in with Google to load or create their persistent Enclave timeline in Firestore (with an instant offline/local fallback mode if desired during testing, while prioritizing authenticated cloud sync).
2. **3D Daytime Exploration & Choice Gate Loop (The Rashomon Relay)**:
   - **Third-Person 3D Viewport**: The player sees their active POV character (e.g., *Silas – ISTJ – The Forge* or *Kaio – ENFP – The Wilds*) standing in one of the Enclave's 3D biomes (Bioluminescent Arboretum, Stormbreaker Tower, Ferrofluid Forge, Subterranean Lake, or Crystalline Atlas Chamber).
   - **Dual Input Controls**: Move in 3D space using `WASD` or `Up/Left/Down/Right` keys and mouse drag to orbit the camera, or use the on-screen **Virtual Thumbstick** and touch-look gesture on mobile screens.
   - **Narrative & Scientific Telemetry HUD**: A translucent glassmorphic story panel displays the current procedural scene ("Vegetables Covered in Cheese" hard-science problem + witty family banter) filtered through the active character's MBTI cognitive stack.
   - **Walking Through 3D Choice Gates**: Radiant, color-coded 3D archways stand in the world ahead of the avatar, complete with floating holographic titles, squad emblems, and particle fields. As the player walks closer to a gate, a proximity preview highlights its projected impact on Grand Project progress, magnetic stress, and family dynamics. Walking directly through the portal triggers a kinetic warp transition, resolves the outcome via the AI DM Engine, and passes the Rashomon baton to the next POV character.
3. **Vex Override Inversion Events**:
   - As twin-sun geomagnetic storms intensify, the character's **Vex Override Gauge** fills. At 100% stress, the HUD and 3D lighting invert to their **Ironic Star Sign** (e.g., chaotic Kaio snaps into ruthless *Capricorn CEO* mode; thrill-seeking Hector freezes in *Virgo Perfectionist* cable-alignment obsession), unlocking special inverted Choice Gates in the 3D arena.
4. **The Black Market (Sibling Grid) Drawer**:
   - At any time, players can open the underground **Sibling Grid** to plant petty micro-aggressions or secret favors (e.g., *grounding Silas's soup spoon*, *filtering soup stock through a clean sock*, *withholding black-market cookies*). Because of the **3-Chapter Delay** and **Main Character Blind Spot**, Node A targets Node B, which accidentally inconveniences Node C, who blames Node A three turns later.
5. **The Main Bus: Nightly Grand Feast**:
   - After completing 3 POV relays in a day cycle, all 16 nodes dock at Ryn's dining table. Players experience the generated "Roast" banter, witness any Black Market pranks detonating, exercise **The Hub's Veto** on deadlocked scientific debates, and restore crew morale before the next day cycle begins.
6. **The Living Atlas & 8 Grand Projects Codex**:
   - A dedicated interactive 3D holographic globe and reference codex where players inspect real-time progress on all 8 Grand Projects (*The Living Atlas, The First Forge, The Mycelial Concordance, The Sanctuary, The Genesis Garden, The Stormbreaker, The Simulacrum, The Lyceum of the Mind*) and view the full psychometric dossier of all 16 family members.

### Visual Identity & Theme

- **Aesthetic Direction**: **Hand-Built Steampunk Cyberpunk & Bioluminescent Exoplanet**. Combines brass/copper analog instrumentation and ferrofluid geometry with glowing mycelial cyan, ultraviolet auroras, and crystalline 5D holography.
- **Color Palette & Mood**:
  - `--background` / Deep Exoplanet Night: `#090D16` (Obsidian Magnetosphere)
  - `--surface` / Steampunk Console Glass: `#131B2E` with subtle brass borders (`#D4AF37`)
  - `--primary` / Bioluminescent Luciferin Cyan: `#00F5D4`
  - `--secondary` / Forge Copper & High-Entropy Amber: `#FF9F1C`
  - `--accent` / Blue Dwarf Ultraviolet & Simulacrum Violet: `#9B5DE5`
  - `--danger` / Red Giant Solar Flare & Vex Override Crimson: `#F15BB5`
- **Typography & Hierarchy**:
  - **Display / Headers & 3D Gate Banners**: *Space Grotesk* or *Cinzel* (bundled locally via `font-util`) for architectural sci-fi authority.
  - **Narrative Prose & Dialogue**: *Lora* or *Merriweather* for warm, literary readability during Rashomon story vignettes.
  - **Telemetry, MBTI Stack & Scientific Readouts**: *JetBrains Mono* / *Roboto Mono* for sensor variance, kV/m gradients, and Big 5 percentile badges.
- **Interactive Feedback & Motion**:
  - Real-time 3D perspective projection with smooth camera interpolation, animated binary suns, floating spore particles, ferrofluid Rosensweig spikes, and pulsing gate rings that expand as the player's 3D avatar approaches within collision distance.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Custom Hardware-Accelerated 3D Projection Engine in Jetpack Compose**
  - *Chosen Approach*: Build a dedicated 3D perspective rendering engine on Jetpack Compose `Canvas` with full 3D-to-2D camera matrix transforms, depth-sorted (`z`-buffered) geometry, 3D terrain grids, volumetric Choice Gate archways, character avatar rig, twin-sun skybox, and spatial collision detection.
  - *Why*: Delivers rock-solid 60 FPS performance in the streaming Android emulator, supports seamless overlay of crisp 3D world-space text banners above Choice Gates, and integrates natively with both hardware keyboard (`onPreviewKeyEvent` for WASD/Arrows), pointer/mouse drag, and multi-touch thumbsticks without native NDK/OpenGL driver incompatibilities.
  - *Alternatives Considered*: External OpenGL/Filament wrappers, which often lack crisp text projection for narrative choice gates and can struggle with streaming emulator shader compilation.
- **Decision 2: Procedural AI DM Engine with Rich Deterministic Seed Fallback**
  - *Chosen Approach*: Use Firebase AI Logic (`gemini-3.8-flash`) with a strict `Schema.obj(...)` JSON schema to generate each day's scientific crisis, POV internal monologue, 3D Choice Gate options, and Grand Feast banter—backed by a rich library of System Bible modules (Silas's rusting HEA pipe, Kaio's prime-number mushroom, Augusta's Sector 4 magnetic tear, Hector's femtosecond lightning rod, etc.) so the world is immediately playable on launch and while AI responses stream in.
  - *Why*: Guarantees zero downtime, instant responsiveness when walking through gates, and infinite replayability faithful to the 16 characters' psychological profiles.
- **Decision 3: Cloud Persistence via Firebase Firestore + Google Sign-In**
  - *Chosen Approach*: Store the player's Enclave simulation state (Day cycle, active POV index, 16 character stress/trust states, 8 Grand Project progress bars, Black Market 3-turn delay queue, and Historian Chronicle log) in Firestore under `/users/{userId}/enclave_saves/{saveId}`.
  - *Why*: Protects the player's evolving civilization story across sessions and devices in full compliance with zero-trust security rules.

---

## 4. Technical Architecture & Data Strategy *(Technical Reference)*

### Architecture & Component Diagram

```
┌────────────────────────────────────────────────────────────────────────────┐
│                        MainActivity (Edge-to-Edge)                         │
│     configureAppCheck(this, intent) ──► AuthGate (Google Sign-In Flow)     │
└───────────────────────────────────────┬────────────────────────────────────┘
                                        │
                                        ▼
┌────────────────────────────────────────────────────────────────────────────┐
│                      EnclaveSandboxViewModel (MVVM)                        │
│  ┌──────────────────────┐  ┌───────────────────────┐  ┌─────────────────┐  │
│  │ 3D World Physics &   │  │ Rashomon POV & Vex    │  │ Black Market    │  │
│  │ Gate Collision Loop  │  │ Override State Engine │  │ 3-Turn Queue    │  │
│  └──────────┬───────────┘  └───────────┬───────────┘  └────────┬────────┘  │
└─────────────┼──────────────────────────┼───────────────────────┼───────────┘
              │                          │                       │
      ┌───────┴────────┐         ┌───────┴────────┐      ┌───────┴────────┐
      ▼                ▼         ▼                ▼      ▼                ▼
┌───────────┐  ┌─────────────────────────┐  ┌─────────────────┐  ┌───────────┐
│ 3D Canvas │  │ Firebase AI Logic (DM)  │  │ Firestore Repo  │  │ 3D Living │
│ Viewport  │  │ gemini-3.8-flash Schema │  │ /users/{uid}/.. │  │ Atlas UI  │
│ + Controls│  │ Crisis / Gates / Feast  │  │ Rules Verified  │  │ & Codex   │
└───────────┘  └─────────────────────────┘  └─────────────────┘  └───────────┘
```

### Data Model & State

- **`EnclaveCharacter` (16 Canonical Nodes)**:
  - `id`, `name`, `role`, `squad` (`FORGE`, `WILDS`, `MIND`, `HUB`), `isMentor`, `mbti`, `cognitiveStack`, `enneagram`, `bigFive` (OCEAN percentiles), `vexStarSign` (ironic inversion), `stressLevel` (0–100), `isVexInverted` (Boolean), `grandProjectId`, `lieBelieved`, `truthToLearn`.
- **`ChoiceGate3D`**:
  - `id`, `title`, `subtitle`, `scientificDomain`, `squadAlignment`, `worldPosition` (`Vec3`), `portalColor`, `isVexGate`, `projectImpact` (`projectId` + delta), `stressDelta`, `nextPovCharacterId`, `outcomeNarrative`.
- **`BlackMarketAction` (The Sibling Grid)**:
  - `id`, `instigatorId`, `intendedTargetId`, `accidentalVictimId`, `blamedNodeId`, `prankDescription`, `chaptersRemaining` (starts at 3, decrements each gate traversal, detonates at 0).
- **`GrandProject` (8 Canonical Projects)**:
  - `id`, `name`, `leadProdigyId`, `mentorId`, `progressPercent`, `currentObstacle`, `unlockedBreakthroughs`.
- **`SimulationState`**:
  - `dayNumber`, `relayStep` (1..3 before Feast), `activePovCharacterId`, `playerPosition` (`Vec3`), `cameraYaw`, `cameraPitch`, `currentSceneTitle`, `currentNarrativeProse`, `activeGates` (`List<ChoiceGate3D>`), `isAtFeast` (Boolean), `feastDialogue` (`List<FeastLine>`), `blackMarketQueue`, `chronicleLog`.

### Interactive Component & State Mapping

- **3D Exploration & Gate Collision (`World3DViewport`)**:
  - Captures keyboard events (`W/A/S/D`, `Up/Down/Left/Right`, `Q/E` camera orbit) via `Modifier.onPreviewKeyEvent` and pointer drag via `Modifier.pointerInput`, plus an on-screen `VirtualThumbstick` and `LookPad` for mobile ergonomics.
  - Updates `playerPosition` (`x, z`) at 60Hz. Computes Euclidean distance to each `ChoiceGate3D` in `activeGates`.
  - At distance `< 4.0f`, displays the floating Gate Inspection HUD. At distance `< 1.4f`, triggers `onWalkThroughGate(gate)`, plays the portal warp animation, logs the decision to Firestore, decrements the Black Market 3-turn counters, and invokes the AI DM Engine for the next Rashomon POV relay (or transitions to the **Grand Feast** on step 3).
- **AI DM Engine (`EnclaveAiDmService`)**:
  - Uses `Firebase.ai.generativeModel("gemini-3.8-flash", generationConfig)` with `Schema.obj(...)` to generate new daily anomalies, 3D Choice Gate coordinates/labels, Vex Override twists, and Grand Feast table debates.
- **Grand Feast & Hub Veto Modal (`GrandFeastScreen`)**:
  - Renders the 16-seat circular/long dining table, displays Ryn's gastrophysics dish of the night, reveals any Black Market pranks that reached turn 0, and lets the player invoke **The Hub's Veto** to resolve resource conflicts and boost project progress.
