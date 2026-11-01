package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.ai.PsychometricGuardrailEngine
import com.example.data.local.EnclaveSaveSerializer
import com.example.firebase.configureAppCheck
import com.example.ui.EnclaveSandboxViewModel
import com.example.ui.auth.AuthScreen
import com.example.ui.auth.signOutUser
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.example.ui.components.BlackMarketDialog
import com.example.ui.components.ChamberDoorDecisionModal
import com.example.ui.components.EmergencyBlackoutProtocolModal
import com.example.ui.components.GrandConvergenceFinaleModal
import com.example.ui.components.GrandFeastDialog
import com.example.ui.components.LivingAtlasAndCodexDialog
import com.example.ui.components.LoreAndGuideModal
import com.example.ui.components.RealTimeDispatchModal
import com.example.ui.components.SavePointDialog
import com.example.ui.theme.BrassGold
import com.example.ui.theme.ConsoleGlass
import com.example.ui.theme.EnclaveTheme
import com.example.ui.theme.ForgeAmber
import com.example.ui.theme.LuciferinCyan
import com.example.ui.theme.ObsidianNight
import com.example.ui.theme.SimulacrumViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VexCrimson
import com.example.ui.world3d.EnclaveWorld3DCanvas
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Mandatory App Check configuration with launch intent before any UI or Firebase calls
        configureAppCheck(this, intent)

        enableEdgeToEdge()

        setContent {
            EnclaveTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EnclaveAppNavigation()
                }
            }
        }
    }
}

internal fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { auth ->
        trySend(auth.currentUser)
    }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}

@Composable
fun EnclaveAppNavigation(auth: FirebaseAuth = Firebase.auth) {
    val currentUser by auth.authStateFlow()
        .collectAsStateWithLifecycle(initialValue = auth.currentUser)
    val user = currentUser

    var isGuestSessionActive by rememberSaveable { mutableStateOf(false) }
    var initialImportedSaveJson by rememberSaveable { mutableStateOf<String?>(null) }

    if (user == null && !isGuestSessionActive) {
        AuthScreen(
            onAuthSuccess = {},
            onEnterGuestSandbox = { importedJson ->
                initialImportedSaveJson = importedJson
                isGuestSessionActive = true
            }
        )
    } else {
        val activeUserId = user?.uid ?: EnclaveSaveSerializer.GUEST_USER_ID
        EnclaveSandboxScreen(
            currentUserId = activeUserId,
            initialImportedJson = initialImportedSaveJson,
            onExitGuestSession = {
                initialImportedSaveJson = null
                isGuestSessionActive = false
            }
        )
    }
}

@Composable
fun EnclaveSandboxScreen(
    currentUserId: String,
    initialImportedJson: String? = null,
    onExitGuestSession: () -> Unit = {}
) {
    val viewModel: EnclaveSandboxViewModel = viewModel(
        key = "$currentUserId:${initialImportedJson.hashCode()}",
        factory = EnclaveSandboxViewModel.provideFactory(
            currentUserId = currentUserId,
            initialImportedJson = initialImportedJson
        )
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activeChar = uiState.activeCharacter
    val voiceLock = remember(activeChar) { PsychometricGuardrailEngine.buildVoiceLock(activeChar) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isStoryHudExpanded by rememberSaveable { mutableStateOf(true) }
    var isPlayerMoving by remember { mutableStateOf(false) }
    var hasSeenInitialGuide by rememberSaveable { mutableStateOf(false) }
    var showLoreGuide by rememberSaveable {
        mutableStateOf(initialImportedJson == null && uiState.gatesTraversed == 0)
    }
    var showLivingAtlas by rememberSaveable { mutableStateOf(false) }
    var showBlackMarket by rememberSaveable { mutableStateOf(false) }
    var showSaveManager by rememberSaveable { mutableStateOf(false) }
    var showRealTimeDispatch by rememberSaveable { mutableStateOf(false) }

    // Zero-permission SAF launchers for exporting and importing portable .json save documents
    val exportSaveDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            viewModel.exportSaveToUri(uri)
        }
    }

    val importSaveDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            viewModel.importSaveFromUri(uri)
            showSaveManager = false
        }
    }

    val haptic = LocalHapticFeedback.current
    LaunchedEffect(uiState.breakthroughBurstCount, uiState.inspectedDoorId, uiState.isEmergencyBlackoutActive) {
        if (uiState.isAudioAndHapticsEnabled && (uiState.breakthroughBurstCount > 0 || uiState.inspectedDoorId != null || uiState.isEmergencyBlackoutActive)) {
            runCatching {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Full-Screen 60fps 3D Enclosed Infinite Corridor & Interactive Wall Doors Viewport
        EnclaveWorld3DCanvas(
            activeCharacter = activeChar,
            biome = uiState.currentBiome,
            corridorDoors = uiState.corridorDoors,
            gates = uiState.activeGates,
            playerPos = uiState.playerPos,
            playerYaw = uiState.playerYaw,
            cameraYaw = uiState.cameraYaw,
            cameraPitch = uiState.cameraPitch,
            magneticFlux = uiState.magneticFlux,
            colonyMorale = uiState.colonyMorale,
            hasSiblingSynergyBuff = uiState.hasSiblingSynergyBuff,
            hasAtlasRadarPerk = uiState.hasAtlasRadarPerk,
            breakthroughBurstCount = uiState.breakthroughBurstCount,
            onPlayerMovingChanged = { isPlayerMoving = it },
            onMoveStep = viewModel::onMoveStep,
            onUpdateCorridorPosition = viewModel::onUpdateCorridorPosition,
            onRotateCamera = viewModel::onRotateCamera,
            onResetCameraBehindPlayer = viewModel::resetCameraBehindPlayer,
            onOpenCorridorDoor = viewModel::onOpenCorridorDoor,
            onAutoWalkToGate = viewModel::onAutoWalkToGate,
            onWalkThroughGate = viewModel::onWalkThroughGate,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Cohesive Mobile-First Top Mission & Story Briefing Card
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                color = ObsidianNight.copy(alpha = 0.92f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .widthIn(max = 660.dp)
                    .fillMaxWidth()
                    .border(
                        width = 1.2.dp,
                        color = if (activeChar.isVexInverted) VexCrimson else Color(activeChar.squad.primaryColorHex),
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Row 1: Active POV Genius + Guide Button + Story Collapse Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showLivingAtlas = true }
                                .testTag("active_pov_header")
                        ) {
                            Surface(
                                color = Color(activeChar.squad.primaryColorHex).copy(alpha = 0.22f),
                                shape = CircleShape,
                                modifier = Modifier
                                    .size(34.dp)
                                    .border(1.5.dp, Color(activeChar.squad.primaryColorHex), CircleShape)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = activeChar.squad.badgeSymbol,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Color(activeChar.squad.primaryColorHex)
                                    )
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                val synergyTag = if (uiState.hasSiblingSynergyBuff) " • 🍵 +15%" else ""
                                val prestigeTag = if (uiState.prestigeLevel > 0) " • ★P${uiState.prestigeLevel}" else ""
                                Text(
                                    text = "DAY ${uiState.dayNumber} • Z=${uiState.playerPos.z.toInt()}m • BAYS: ${uiState.gatesTraversed} • 🏅${uiState.unlockedMedalIds.size}/12$prestigeTag$synergyTag",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BrassGold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "POV: ${activeChar.name} (${activeChar.mbti} • ${activeChar.cognitiveStack})",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 1-Tap Procedural Sci-Fi Audio & Haptics Toggle Chip
                            Surface(
                                color = if (uiState.isAudioAndHapticsEnabled) LuciferinCyan.copy(alpha = 0.16f) else ConsoleGlass,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .height(32.dp)
                                    .border(
                                        1.dp,
                                        if (uiState.isAudioAndHapticsEnabled) LuciferinCyan.copy(alpha = 0.7f) else TextSecondary.copy(alpha = 0.4f),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { viewModel.toggleAudioAndHaptics() }
                                    .testTag("toggle_audio_haptics_button")
                            ) {
                                Box(
                                    modifier = Modifier.padding(horizontal = 7.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (uiState.isAudioAndHapticsEnabled) "🔊" else "🔇",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }

                            // Live Real-Time Twin-Sun Phase & Directives Button
                            val phaseColor = Color(uiState.realTimePhase.skyTintHex)
                            val completedDirectivesCount = uiState.dailyDirectives.count { it.isCompleted }
                            val offlineReport = uiState.pendingOfflineReport
                            Surface(
                                color = phaseColor.copy(alpha = 0.18f),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .height(32.dp)
                                    .border(
                                        1.dp,
                                        if (offlineReport != null) ForgeAmber else phaseColor.copy(alpha = 0.75f),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { showRealTimeDispatch = true }
                                    .testTag("open_realtime_dispatch_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = if (offlineReport != null) {
                                            "⏱ +${offlineReport.projectBoostPercent}% Idle"
                                        } else {
                                            "☀ $completedDirectivesCount/3 • ${uiState.overallReadinessPercent}%"
                                        },
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (offlineReport != null) ForgeAmber else phaseColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Guide / Lore Briefing Button
                            Surface(
                                color = LuciferinCyan.copy(alpha = 0.16f),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .height(32.dp)
                                    .border(1.dp, LuciferinCyan.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
                                    .clickable { showLoreGuide = true }
                                    .testTag("open_lore_guide_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                        contentDescription = "Open Lore & How to Play Guide",
                                        tint = LuciferinCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "Guide",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = LuciferinCyan,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Collapse / Expand Story Briefing Button
                            Surface(
                                color = ConsoleGlass,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .height(32.dp)
                                    .border(1.dp, BrassGold.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                    .clickable { isStoryHudExpanded = !isStoryHudExpanded }
                                    .testTag("toggle_story_hud_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(
                                        text = if (isStoryHudExpanded) "Minimize" else "Mission",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = BrassGold,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Icon(
                                        imageVector = if (isStoryHudExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = "Toggle Story Panel",
                                        tint = BrassGold,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Row 2: Psychometric Lock + Stress Progress Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = voiceLock.hudBadgeText,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (activeChar.isVexInverted) VexCrimson else LuciferinCyan,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("psychometric_lock_badge")
                        )
                        Text(
                            text = "Stress ${activeChar.stressLevel}% • Morale ${uiState.colonyMorale}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }

                    LinearProgressIndicator(
                        progress = { activeChar.stressLevel / 100f },
                        color = if (activeChar.isVexInverted || activeChar.stressLevel >= 75) VexCrimson else ForgeAmber,
                        trackColor = ConsoleGlass,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                    )
                }
            }

            // Expandable Cohesive Mission & Story Card (Auto-slims while walking so 3D corridor fills the screen!)
            AnimatedVisibility(visible = isStoryHudExpanded && !isPlayerMoving) {
                Surface(
                    color = ObsidianNight.copy(alpha = 0.90f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .widthIn(max = 660.dp)
                        .fillMaxWidth()
                        .heightIn(max = 178.dp)
                        .border(1.dp, BrassGold.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
                        .testTag("story_narrative_card")
                ) {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = uiState.currentSceneTitle,
                                style = MaterialTheme.typography.titleSmall,
                                color = BrassGold,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = uiState.cloudSyncStatus,
                                style = MaterialTheme.typography.labelSmall,
                                color = LuciferinCyan,
                                modifier = Modifier
                                    .clickable { showSaveManager = true }
                                    .testTag("save_status_label")
                            )
                        }

                        Text(
                            text = uiState.currentNarrativeProse,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary
                        )

                        Text(
                            text = "🔬 Science Note: ${uiState.currentScientificNote}",
                            style = MaterialTheme.typography.labelSmall,
                            color = LuciferinCyan
                        )

                        HorizontalDivider(color = BrassGold.copy(alpha = 0.25f))

                        Text(
                            text = "🎯 YOUR OBJECTIVE: Walk down the Infinite Corridor and open any Left or Right Wall Door to inspect its 3+ choices—commit a choice inside or step back into the hall to keep exploring!",
                            style = MaterialTheme.typography.labelSmall,
                            color = ForgeAmber,
                            fontWeight = FontWeight.SemiBold
                        )

                        if (uiState.chronicle.isNotEmpty()) {
                            val latest = uiState.chronicle.first()
                            Text(
                                text = "↪ Previous Relay (${latest.povName}): Passed via '${latest.gateTitle}' — ${uiState.lastBatonPassQuote}",
                                style = MaterialTheme.typography.labelSmall,
                                fontStyle = FontStyle.Italic,
                                color = TextSecondary
                            )
                        }

                        if (uiState.aiStatusBanner != null) {
                            Text(
                                text = uiState.aiStatusBanner!!,
                                style = MaterialTheme.typography.labelSmall,
                                color = SimulacrumViolet
                            )
                        }
                    }
                }
            }
        }

        // 3. Mobile-First Ergonomic Bottom Command Bar
        Surface(
            color = ObsidianNight.copy(alpha = 0.95f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .border(1.dp, BrassGold.copy(alpha = 0.35f), RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomCommandItem(
                    icon = Icons.Default.AutoAwesome,
                    label = "AI Crisis",
                    tint = LuciferinCyan,
                    isLoading = uiState.isAiGenerating,
                    testTag = "ai_dm_simulate_button",
                    onClick = { viewModel.triggerAiDmProceduralScene() }
                )

                BottomCommandItem(
                    icon = Icons.Default.Bolt,
                    label = if (activeChar.isVexInverted) "Vex ON" else "Vex Flip",
                    tint = if (activeChar.isVexInverted) VexCrimson else ForgeAmber,
                    testTag = "vex_override_button",
                    onClick = { viewModel.toggleVexOverrideForActiveCharacter() }
                )

                BottomCommandItem(
                    icon = Icons.Default.VisibilityOff,
                    label = "Pranks",
                    tint = VexCrimson,
                    testTag = "open_black_market_button",
                    onClick = { showBlackMarket = true }
                )

                BottomCommandItem(
                    icon = Icons.Default.Public,
                    label = "Atlas/16",
                    tint = SimulacrumViolet,
                    testTag = "open_living_atlas_button",
                    onClick = { showLivingAtlas = true }
                )

                BottomCommandItem(
                    icon = Icons.Default.Restaurant,
                    label = "Feast",
                    tint = ForgeAmber,
                    testTag = "open_grand_feast_button",
                    onClick = { viewModel.openGrandFeastFromCorridor() }
                )

                BottomCommandItem(
                    icon = Icons.Default.Save,
                    label = "Save",
                    tint = BrassGold,
                    testTag = "open_save_manager_button",
                    onClick = { showSaveManager = true }
                )

                BottomCommandItem(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    label = if (viewModel.isGuestMode) "Exit" else "Sign Out",
                    tint = TextSecondary,
                    testTag = "sign_out_button",
                    onClick = {
                        if (viewModel.isGuestMode) {
                            viewModel.saveLocalCheckpoint()
                            onExitGuestSession()
                        } else {
                            signOutUser(context = context, onSignOutComplete = {}, scope = scope)
                        }
                    }
                )
            }
        }

        // 4. Modals: Chamber Door Decision Menu, Onboarding/Lore Guide, Nightly Grand Feast, Black Market, Living Atlas, Endgame & Save Point Manager
        val activeInspectedDoor = uiState.inspectedDoor
        if (activeInspectedDoor != null) {
            ChamberDoorDecisionModal(
                door = activeInspectedDoor,
                activeCharacter = activeChar,
                allCharacters = uiState.characters,
                grandProjects = uiState.grandProjects,
                hasSiblingSynergyBuff = uiState.hasSiblingSynergyBuff,
                bondXpMap = uiState.siblingBondXp,
                prestigeLevel = uiState.prestigeLevel,
                latestAftermathReport = uiState.latestAftermathReport,
                isAiGeneratingOptions = uiState.isAiGenerating,
                onCommitDoorDecision = viewModel::onCommitDoorDecision,
                onUnlockVaultDoor = viewModel::onUnlockVaultDoor,
                onQuickSwitchPovForVault = viewModel::switchPovCharacter,
                onRequestAiCustomOptionsForDoor = viewModel::requestAiCustomOptionsForDoor,
                onLeaveDoorAndKeepExploring = viewModel::onLeaveCorridorDoor
            )
        }

        if (uiState.isEmergencyBlackoutActive) {
            EmergencyBlackoutProtocolModal(
                colonyMorale = uiState.colonyMorale,
                magneticFlux = uiState.magneticFlux,
                onVentStormbreakerCapacitors = { viewModel.resolveEmergencyBlackout(true) },
                onExecuteSchumannEmergencyRecoupling = { viewModel.resolveEmergencyBlackout(false) }
            )
        }

        if (uiState.showGrandConvergenceFinale) {
            GrandConvergenceFinaleModal(
                dayNumber = uiState.dayNumber,
                gatesTraversed = uiState.gatesTraversed,
                prestigeLevel = uiState.prestigeLevel,
                projects = uiState.grandProjects,
                siblingBonds = uiState.siblingBondTracks,
                medals = uiState.achievementMedals,
                onStartNewGamePlusPrestige = viewModel::startNewGamePlusPrestige,
                onContinueInfiniteCorridor = viewModel::dismissGrandConvergenceFinale
            )
        }

        if (showLoreGuide && !hasSeenInitialGuide || showLoreGuide) {
            LoreAndGuideModal(
                onDismiss = {
                    hasSeenInitialGuide = true
                    showLoreGuide = false
                }
            )
        }

        if (uiState.isAtGrandFeast) {
            GrandFeastDialog(
                dayNumber = uiState.dayNumber,
                dishOfTheNight = uiState.feastDishOfTheNight,
                exchanges = uiState.feastExchanges,
                detonatedPranks = uiState.detonatedPranksTonight,
                vetoOptions = uiState.hubVetoOptions,
                isGeneratingAiFeast = uiState.isAiGenerating,
                onRequestAiFeastBanter = viewModel::requestAiGrandFeastBanter,
                onExecuteHubVeto = viewModel::executeHubVetoAndStartNextDay
            )
        }

        if (showBlackMarket) {
            BlackMarketDialog(
                characters = uiState.characters,
                activePranks = uiState.blackMarketQueue,
                onQueueNewPrank = viewModel::queueBlackMarketPrank,
                onDismiss = { showBlackMarket = false }
            )
        }

        if (showLivingAtlas) {
            LivingAtlasAndCodexDialog(
                projects = uiState.grandProjects,
                characters = uiState.characters,
                chronicle = uiState.chronicle,
                magneticFlux = uiState.magneticFlux,
                siblingBonds = uiState.siblingBondTracks,
                medals = uiState.achievementMedals,
                prestigeLevel = uiState.prestigeLevel,
                canTriggerGrandConvergence = uiState.canTriggerGrandConvergence,
                onTriggerGrandConvergence = {
                    showLivingAtlas = false
                    viewModel.triggerGrandConvergenceFinale()
                },
                onSelectPovCharacter = viewModel::switchPovCharacter,
                onDismiss = { showLivingAtlas = false }
            )
        }

        if (showSaveManager) {
            SavePointDialog(
                uiState = uiState,
                isGuestMode = viewModel.isGuestMode,
                onSaveLocalCheckpoint = viewModel::saveLocalCheckpoint,
                onExportSaveFile = {
                    val fileName = "enclave16_day${uiState.dayNumber}_relay${uiState.relayStep}_${uiState.activePovId}.json"
                    exportSaveDocumentLauncher.launch(fileName)
                },
                onImportSaveFile = {
                    importSaveDocumentLauncher.launch(
                        arrayOf("application/json", "text/plain", "*/*")
                    )
                },
                onRestoreFromJsonString = viewModel::restoreFromJsonString,
                onResetTimeline = viewModel::resetTimelineToDayOne,
                onDeleteAccountAndCloudData = {
                    viewModel.deleteAccountAndCloudData {
                        showSaveManager = false
                        if (viewModel.isGuestMode) {
                            onExitGuestSession()
                        } else {
                            signOutUser(context = context, onSignOutComplete = {}, scope = scope)
                        }
                    }
                },
                onDismiss = { showSaveManager = false }
            )
        }

        if (showRealTimeDispatch) {
            RealTimeDispatchModal(
                currentPhase = uiState.realTimePhase,
                offlineReport = uiState.pendingOfflineReport,
                dailyDirectives = uiState.dailyDirectives,
                overallReadinessPercent = uiState.overallReadinessPercent,
                dayNumber = uiState.dayNumber,
                onClaimOfflineBoost = viewModel::claimOfflineAutonomousBoost,
                onDismiss = { showRealTimeDispatch = false }
            )
        }
    }
}

@Composable
private fun BottomCommandItem(
    icon: ImageVector,
    label: String,
    tint: Color,
    testTag: String,
    isLoading: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(enabled = !isLoading, onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = tint,
                strokeWidth = 2.dp
            )
        } else {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = tint,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}
