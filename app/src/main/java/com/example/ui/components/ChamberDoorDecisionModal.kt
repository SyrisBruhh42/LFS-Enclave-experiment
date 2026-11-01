package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ChamberAftermathReport
import com.example.data.model.ChoiceGate3D
import com.example.data.model.CognitiveResonanceAndMilestonesEngine
import com.example.data.model.CorridorDoor3D
import com.example.data.model.DoorArchetype
import com.example.data.model.DoorSide
import com.example.data.model.DoorStatus
import com.example.data.model.EnclaveCharacter
import com.example.data.model.GrandProject
import com.example.data.model.ResonanceTier
import com.example.ui.theme.BrassGold
import com.example.ui.theme.ConsoleGlass
import com.example.ui.theme.ForgeAmber
import com.example.ui.theme.LuciferinCyan
import com.example.ui.theme.ObsidianNight
import com.example.ui.theme.SimulacrumViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VexCrimson

@Composable
fun ChamberDoorDecisionModal(
    door: CorridorDoor3D,
    activeCharacter: EnclaveCharacter,
    allCharacters: List<EnclaveCharacter>,
    grandProjects: List<GrandProject>,
    hasSiblingSynergyBuff: Boolean,
    bondXpMap: Map<String, Int> = emptyMap(),
    prestigeLevel: Int = 0,
    latestAftermathReport: ChamberAftermathReport?,
    isAiGeneratingOptions: Boolean,
    onCommitDoorDecision: (door: CorridorDoor3D, option: ChoiceGate3D, switchPov: Boolean) -> Unit,
    onUnlockVaultDoor: (door: CorridorDoor3D, forceBreachWithStress: Boolean) -> Unit,
    onQuickSwitchPovForVault: (EnclaveCharacter) -> Unit,
    onRequestAiCustomOptionsForDoor: (CorridorDoor3D) -> Unit,
    onLeaveDoorAndKeepExploring: () -> Unit
) {
    val archetypeColor = Color(door.archetype.colorHex)
    val doorAccent = if (door.archetype == DoorArchetype.STANDARD_LAB) {
        Color(door.squadAlignment.primaryColorHex)
    } else {
        archetypeColor
    }
    val isResolved = door.status == DoorStatus.RESOLVED
    val isVaultUnlocked = remember(door, activeCharacter, grandProjects) {
        CognitiveResonanceAndMilestonesEngine.isVaultUnlocked(door, activeCharacter, grandProjects)
    }
    val resonanceMap = remember(door, activeCharacter, grandProjects, hasSiblingSynergyBuff, bondXpMap, prestigeLevel) {
        CognitiveResonanceAndMilestonesEngine.evaluateDoorOptionsResonance(
            door = door,
            activeCharacter = activeCharacter,
            allCharacters = allCharacters,
            projects = grandProjects,
            hasSiblingSynergyBuff = hasSiblingSynergyBuff,
            bondXpMap = bondXpMap,
            prestigeLevel = prestigeLevel
        )
    }
    val hasSimulacrumPredictor = remember(grandProjects) {
        CognitiveResonanceAndMilestonesEngine.isMilestoneUnlocked(grandProjects, "simulacrum", 50)
    }
    val hasForgeOverridePerk = remember(grandProjects) {
        CognitiveResonanceAndMilestonesEngine.isMilestoneUnlocked(grandProjects, "first_forge", 50)
    }

    Dialog(
        onDismissRequest = onLeaveDoorAndKeepExploring,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            color = ObsidianNight,
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier
                .widthIn(max = 640.dp)
                .fillMaxWidth(0.95f)
                .border(1.5.dp, if (isResolved) LuciferinCyan else doorAccent, RoundedCornerShape(22.dp))
                .testTag("chamber_door_decision_modal")
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. Top Chamber Header + "Step Back into Hallway" Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(doorAccent.copy(alpha = 0.2f), CircleShape)
                                .border(1.5.dp, doorAccent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when {
                                    isResolved -> Icons.Default.CheckCircle
                                    door.archetype == DoorArchetype.COGNITIVE_VAULT && !isVaultUnlocked -> Icons.Default.Lock
                                    door.archetype == DoorArchetype.TICKING_CRISIS -> Icons.Default.Warning
                                    else -> Icons.Default.MeetingRoom
                                },
                                contentDescription = "Chamber Door",
                                tint = if (isResolved) LuciferinCyan else doorAccent,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            val sideLabel = if (door.side == DoorSide.LEFT_WALL) "LEFT WALL" else "RIGHT WALL"
                            Text(
                                text = "${door.archetype.badgeLabel} • $sideLabel • Z=${door.position.z.toInt()}m",
                                style = MaterialTheme.typography.labelSmall,
                                color = doorAccent,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = door.chamberTitle,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onLeaveDoorAndKeepExploring,
                        border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(width = 1.dp),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("leave_door_top_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Leave Room",
                            tint = LuciferinCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Hallway",
                            style = MaterialTheme.typography.labelMedium,
                            color = LuciferinCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // 2. Archetype Status Banner (Crisis Countdown / Sanctuary Buff / Active Sibling Synergy)
                if (!isResolved && door.archetype == DoorArchetype.TICKING_CRISIS) {
                    val turnsLeft = door.crisisTurnsRemaining ?: 2
                    Surface(
                        color = VexCrimson.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, VexCrimson, RoundedCornerShape(12.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = VexCrimson,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "URGENT CRISIS BULKHEAD: Detonates in $turnsLeft door resolution(s) if ignored! Defusing now grants a Heroic Project & Morale bonus.",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                } else if (!isResolved && door.archetype == DoorArchetype.SANCTUARY_LOUNGE) {
                    Surface(
                        color = BrassGold.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BrassGold, RoundedCornerShape(12.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = BrassGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "SANCTUARY ALCOVE: Restorative sibling haven. Any choice here vents heavy Vex Stress and grants a +15% Sibling Synergy Resonance buff for your next chamber!",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                } else if (!isResolved && hasSiblingSynergyBuff) {
                    Surface(
                        color = LuciferinCyan.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, LuciferinCyan.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                    ) {
                        Text(
                            text = "🍵 SIBLING SYNERGY ACTIVE: +15% Cognitive Resonance applied to all choices in this chamber!",
                            style = MaterialTheme.typography.labelSmall,
                            color = LuciferinCyan,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                // 3. Chamber Dilemma & Hard-Science Context Box
                Surface(
                    color = ConsoleGlass,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, doorAccent.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = door.chamberSubtitle,
                                style = MaterialTheme.typography.labelMedium,
                                color = doorAccent,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "POV: ${activeCharacter.name} (${activeCharacter.mbti} • ${activeCharacter.cognitiveStack})",
                                style = MaterialTheme.typography.labelSmall,
                                color = LuciferinCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = door.roomDilemmaProse,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Science,
                                contentDescription = null,
                                tint = LuciferinCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = door.scientificCheeseNote,
                                style = MaterialTheme.typography.labelSmall,
                                color = LuciferinCyan
                            )
                        }
                    }
                }

                // 4. STAGE 2: Dramatic Aftermath Screen (when resolved) OR STAGE 1: Interactive Decision Console
                if (isResolved) {
                    DramaticAftermathSection(
                        door = door,
                        aftermath = latestAftermathReport
                    )
                } else {
                    // Check if this is a locked Cognitive Vault
                    if (door.archetype == DoorArchetype.COGNITIVE_VAULT && !isVaultUnlocked) {
                        CognitiveVaultLockCard(
                            door = door,
                            activeCharacter = activeCharacter,
                            allCharacters = allCharacters,
                            grandProjects = grandProjects,
                            hasForgeOverridePerk = hasForgeOverridePerk,
                            onUnlockVaultDoor = onUnlockVaultDoor,
                            onQuickSwitchPovForVault = onQuickSwitchPovForVault
                        )
                    } else {
                        if (door.archetype == DoorArchetype.COGNITIVE_VAULT) {
                            Surface(
                                color = SimulacrumViolet.copy(alpha = 0.16f),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, SimulacrumViolet, RoundedCornerShape(10.dp))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LockOpen,
                                        contentDescription = null,
                                        tint = LuciferinCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "COGNITIVE VAULT UNLOCKED: ${activeCharacter.name} (${activeCharacter.cognitiveStack}) or Enclave Milestone verified!",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = LuciferinCyan,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CHOOSE AN INTERVENTION (${door.options.size} APPROACHES)",
                                style = MaterialTheme.typography.labelMedium,
                                color = BrassGold,
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedButton(
                                onClick = { onRequestAiCustomOptionsForDoor(door) },
                                enabled = !isAiGeneratingOptions,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .height(32.dp)
                                    .testTag("regenerate_door_ai_options_button")
                            ) {
                                if (isAiGeneratingOptions) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        color = LuciferinCyan,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = LuciferinCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "AI Synthesize Choices",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = LuciferinCyan
                                )
                            }
                        }

                        // Render each distinct Choice Option with live Cognitive Resonance Check
                        door.options.forEachIndexed { idx, option ->
                            val optColor = if (option.isVexGate) VexCrimson else Color(option.squadAlignment.primaryColorHex)
                            val relayTarget = allCharacters.firstOrNull { it.id == option.nextPovId }
                            val eval = resonanceMap[option.id]
                            val tierColor = Color(eval?.tier?.badgeColorHex ?: 0xFF00F5D4)

                            val totalProj = option.projectDelta + (eval?.bonusProjectDelta ?: 0)
                            val totalStress = option.stressDelta + (eval?.bonusStressDelta ?: 0)
                            val totalMorale = option.moraleDelta + (eval?.bonusMoraleDelta ?: 0)
                            val stressSign = if (totalStress >= 0) "+$totalStress%" else "$totalStress%"
                            val moraleSign = if (totalMorale >= 0) "+$totalMorale%" else "$totalMorale%"

                            Surface(
                                color = ConsoleGlass,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.2.dp, optColor.copy(alpha = 0.85f), RoundedCornerShape(14.dp))
                                    .testTag("door_option_card_$idx")
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // Top Row: Squad Domain + Telemetry Deltas
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${option.squadAlignment.badgeSymbol} ${option.squadAlignment.title.uppercase()} • ${option.scientificDomain}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = optColor,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = "+$totalProj% Proj • $stressSign Stress • $moraleSign Morale",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = LuciferinCyan,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    // Cognitive Resonance Skill Check Pill
                                    if (eval != null) {
                                        Surface(
                                            color = tierColor.copy(alpha = 0.14f),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .border(1.dp, tierColor.copy(alpha = 0.65f), RoundedCornerShape(8.dp))
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "${eval.resonancePercent}% RESONANCE • ${eval.tier.label}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = tierColor,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = eval.reasonLabel,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = TextSecondary
                                                )
                                            }
                                        }
                                    }

                                    Text(
                                        text = option.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        text = option.subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )

                                    // Simulacrum 50% Milestone Perk: 5D Quartz Outcome Predictor
                                    if (hasSimulacrumPredictor && eval != null) {
                                        Text(
                                            text = "🔮 5D Simulacrum Preview: ${eval.predictedPartnerQuote}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontStyle = FontStyle.Italic,
                                            color = SimulacrumViolet
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Primary Action: Execute & Stay as Current Character
                                        Button(
                                            onClick = {
                                                onCommitDoorDecision(door, option, false)
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = optColor,
                                                contentColor = ObsidianNight
                                            ),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(42.dp)
                                                .testTag("commit_door_option_$idx")
                                        ) {
                                            Text(
                                                text = "Execute (${activeCharacter.name})",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        // Secondary Action: Execute & Relay POV to Partner Genius
                                        if (relayTarget != null && relayTarget.id != activeCharacter.id) {
                                            OutlinedButton(
                                                onClick = {
                                                    onCommitDoorDecision(door, option, true)
                                                },
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier
                                                    .height(42.dp)
                                                    .testTag("commit_and_relay_option_$idx")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.SwapHoriz,
                                                    contentDescription = null,
                                                    tint = BrassGold,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "→ ${relayTarget.name}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = BrassGold,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(color = BrassGold.copy(alpha = 0.25f))

                // 5. Bottom Action: Leave Chamber & Continue Down the Infinite Corridor
                Button(
                    onClick = onLeaveDoorAndKeepExploring,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isResolved) LuciferinCyan else BrassGold,
                        contentColor = ObsidianNight
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("leave_door_keep_exploring_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isResolved) {
                            "Return to 3D Hallway & Keep Exploring →"
                        } else {
                            "Step Back into Hallway Without Deciding Yet (Keep Exploring)"
                        },
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun CognitiveVaultLockCard(
    door: CorridorDoor3D,
    activeCharacter: EnclaveCharacter,
    allCharacters: List<EnclaveCharacter>,
    grandProjects: List<GrandProject>,
    hasForgeOverridePerk: Boolean,
    onUnlockVaultDoor: (door: CorridorDoor3D, forceBreachWithStress: Boolean) -> Unit,
    onQuickSwitchPovForVault: (EnclaveCharacter) -> Unit
) {
    val matchingGeniuses = remember(door, allCharacters) {
        CognitiveResonanceAndMilestonesEngine.getCharactersMatchingVault(door, allCharacters)
    }
    val reqLabel = remember(door, grandProjects) {
        CognitiveResonanceAndMilestonesEngine.buildVaultRequirementLabel(door, grandProjects)
    }

    Surface(
        color = SimulacrumViolet.copy(alpha = 0.14f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, SimulacrumViolet, RoundedCornerShape(16.dp))
            .testTag("cognitive_vault_lock_card")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Cognitive Lock",
                    tint = SimulacrumViolet,
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        text = "COGNITIVE-LOCKED VAULT SEAL ACTIVE",
                        style = MaterialTheme.typography.labelMedium,
                        color = SimulacrumViolet,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = reqLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Text(
                text = "Current POV ${activeCharacter.name} (${activeCharacter.mbti} • ${activeCharacter.cognitiveStack}) does not match the '${door.requiredCognitiveFunction}' cognitive lock. Choose how to proceed:",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            // Option A: Switch POV to a matching Genius
            if (matchingGeniuses.isNotEmpty()) {
                Text(
                    text = "1. CALL IN A MATCHING GENIUS (FREE COGNITIVE UNLOCK):",
                    style = MaterialTheme.typography.labelSmall,
                    color = LuciferinCyan,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    matchingGeniuses.forEach { genius ->
                        Surface(
                            color = ConsoleGlass,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .border(1.dp, LuciferinCyan, RoundedCornerShape(10.dp))
                                .clickable { onQuickSwitchPovForVault(genius) }
                                .testTag("vault_switch_pov_${genius.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = null,
                                    tint = LuciferinCyan,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "${genius.name} (${genius.mbti} • ${genius.cognitiveStack})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Option B: Use First Forge 50% Perk OR Force Breach with Vex Stress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (hasForgeOverridePerk) {
                    Button(
                        onClick = { onUnlockVaultDoor(door, false) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ForgeAmber,
                            contentColor = ObsidianNight
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("vault_forge_override_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.LockOpen,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Forge 50% Override (Free)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                OutlinedButton(
                    onClick = { onUnlockVaultDoor(door, true) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("vault_force_breach_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = VexCrimson,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Force Breach (+18% Vex Stress)",
                        style = MaterialTheme.typography.labelSmall,
                        color = VexCrimson,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun DramaticAftermathSection(
    door: CorridorDoor3D,
    aftermath: ChamberAftermathReport?
) {
    val tierColor = Color(aftermath?.tier?.badgeColorHex ?: 0xFF00F5D4)
    val chosenOpt = door.options.firstOrNull { it.id == door.chosenOptionId }

    Surface(
        color = tierColor.copy(alpha = 0.12f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, tierColor, RoundedCornerShape(16.dp))
            .testTag("door_resolved_outcome_box")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Outcome Tier Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = tierColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = aftermath?.tier?.label ?: "★ CHAMBER DECISION RECORDED ★",
                        style = MaterialTheme.typography.labelLarge,
                        color = tierColor,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (aftermath != null) {
                    Text(
                        text = "${aftermath.resonancePercent}% RESONANCE",
                        style = MaterialTheme.typography.labelSmall,
                        color = BrassGold,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Special Crisis Defused / Sanctuary Synergy Callouts
            if (aftermath?.wasCrisisDefused == true) {
                Surface(
                    color = LuciferinCyan.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "⚠ HEROIC CRISIS DEFUSAL: Bulkhead stabilized before detonation! Bonus Project & Morale awarded.",
                        style = MaterialTheme.typography.labelSmall,
                        color = LuciferinCyan,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            if (aftermath?.grantedSiblingSynergyBuff == true) {
                Surface(
                    color = BrassGold.copy(alpha = 0.20f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🍵 SIBLING SYNERGY UNLOCKED: +15% Cognitive Resonance active for your next Chamber Door!",
                        style = MaterialTheme.typography.labelSmall,
                        color = BrassGold,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            // Dramatic Consequence Prose
            Text(
                text = aftermath?.outcomeNarrative ?: door.resolvedOutcomeText ?: "Chamber crisis resolved.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )

            // Telemetry Delta Chips
            if (aftermath != null) {
                val stressText = if (aftermath.totalStressDelta >= 0) "+${aftermath.totalStressDelta}% Stress" else "${aftermath.totalStressDelta}% Stress"
                val moraleText = if (aftermath.totalMoraleDelta >= 0) "+${aftermath.totalMoraleDelta}% Morale" else "${aftermath.totalMoraleDelta}% Morale"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AftermathDeltaBadge(
                        text = "+${aftermath.totalProjectDelta}% ${aftermath.targetProjectName}",
                        color = LuciferinCyan
                    )
                    AftermathDeltaBadge(
                        text = stressText,
                        color = if (aftermath.totalStressDelta <= 0) LuciferinCyan else ForgeAmber
                    )
                    AftermathDeltaBadge(
                        text = moraleText,
                        color = BrassGold
                    )
                }
            }

            // In-Character Partner Genius Reaction Card
            if (aftermath != null) {
                Surface(
                    color = ConsoleGlass,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BrassGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "CHAMBER PARTNER REACTION • ${aftermath.partnerSpeakerName.uppercase()} — ${aftermath.partnerSpeakerRole}",
                            style = MaterialTheme.typography.labelSmall,
                            color = BrassGold,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = aftermath.partnerReactionDialogue,
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic,
                            color = TextPrimary
                        )
                    }
                }
            } else if (chosenOpt != null) {
                Text(
                    text = chosenOpt.batonPassQuote,
                    style = MaterialTheme.typography.labelMedium,
                    fontStyle = FontStyle.Italic,
                    color = BrassGold
                )
            }

            // Newly Unlocked Grand Project 50% / 100% Milestone Perks!
            if (aftermath != null && aftermath.newlyUnlockedMilestones.isNotEmpty()) {
                aftermath.newlyUnlockedMilestones.forEach { perk ->
                    Surface(
                        color = ForgeAmber.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.5.dp, ForgeAmber, RoundedCornerShape(12.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "🏆 NEW GRAND PROJECT MILESTONE UNLOCKED (${perk.projectName} ${perk.thresholdPercent}%)!",
                                style = MaterialTheme.typography.labelSmall,
                                color = ForgeAmber,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${perk.title}: ${perk.benefitSummary}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Sibling Bond XP & Newly Unlocked Career Medals!
            if (aftermath != null && (aftermath.bondXpGained > 0 || aftermath.newlyUnlockedMedals.isNotEmpty())) {
                Surface(
                    color = SimulacrumViolet.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SimulacrumViolet.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (aftermath.bondXpGained > 0) {
                            Text(
                                text = "🤝 SIBLING BOND AFFINITY: +${aftermath.bondXpGained} XP (${aftermath.duoBondTitle})",
                                style = MaterialTheme.typography.labelSmall,
                                color = LuciferinCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (aftermath.newlyUnlockedMedals.isNotEmpty()) {
                            Text(
                                text = "🎖 NEW ENCLAVE MEDAL(S) EARNED: ${aftermath.newlyUnlockedMedals.joinToString(" • ")}",
                                style = MaterialTheme.typography.labelSmall,
                                color = BrassGold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AftermathDeltaBadge(
    text: String,
    color: Color
) {
    Surface(
        color = color.copy(alpha = 0.16f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.border(1.dp, color.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
