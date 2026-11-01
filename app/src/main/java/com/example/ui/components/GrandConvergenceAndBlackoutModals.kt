package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.EnclaveAchievementMedal
import com.example.data.model.GrandProject
import com.example.data.model.SiblingBondTrack
import com.example.ui.theme.BlueDwarfGlow
import com.example.ui.theme.BrassGold
import com.example.ui.theme.ConsoleGlass
import com.example.ui.theme.ForgeAmber
import com.example.ui.theme.LuciferinCyan
import com.example.ui.theme.ObsidianNight
import com.example.ui.theme.SimulacrumViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VexCrimson

/**
 * Climactic Season Finale & New Game+ Prestige Modal when Kepler-452b is stabilized.
 */
@Composable
fun GrandConvergenceFinaleModal(
    dayNumber: Int,
    gatesTraversed: Int,
    prestigeLevel: Int,
    projects: List<GrandProject>,
    siblingBonds: List<SiblingBondTrack>,
    medals: List<EnclaveAchievementMedal>,
    onStartNewGamePlusPrestige: () -> Unit,
    onContinueInfiniteCorridor: () -> Unit
) {
    val topBond = siblingBonds.maxByOrNull { it.bondXp } ?: siblingBonds.firstOrNull()
    val unlockedMedalsCount = medals.count { it.isUnlocked }
    val completedProjectsCount = projects.count { it.progressPercent >= 100 }
    val avgReadiness = if (projects.isEmpty()) 100 else projects.sumOf { it.progressPercent } / projects.size

    Dialog(
        onDismissRequest = onContinueInfiniteCorridor,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .widthIn(max = 640.dp)
                .fillMaxWidth()
                .border(2.dp, BrassGold, RoundedCornerShape(24.dp))
                .testTag("grand_convergence_finale_modal"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianNight.copy(alpha = 0.98f))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = BrassGold.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, BrassGold, RoundedCornerShape(14.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Grand Convergence Victory",
                            tint = BrassGold,
                            modifier = Modifier.size(34.dp)
                        )
                        Column {
                            Text(
                                text = "☀ THE GRAND CONVERGENCE • KEPLER-452B STABILIZED",
                                style = MaterialTheme.typography.labelSmall,
                                color = BrassGold,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Ediacaran Bifurcation Megastorm Harnessed!",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = "Across $dayNumber Enclave Days and $gatesTraversed resolved Chamber Bays, all sixteen polymaths united their cognitive stacks. As the Red Giant and Blue Dwarf align over Sector 4, the Stormbreaker's femtosecond plasma channels guide a terawatt atmospheric discharge straight into the subterranean quartz geodes—powering the Sanctuary, the Genesis Garden, and the 5D Simulacrum for a thousand years.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )

                // Campaign Telemetry Summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FinaleStatChip(
                        label = "Readiness",
                        value = "$avgReadiness% ($completedProjectsCount/8 Max)",
                        color = LuciferinCyan,
                        modifier = Modifier.weight(1f)
                    )
                    FinaleStatChip(
                        label = "Career Medals",
                        value = "$unlockedMedalsCount / ${medals.size}",
                        color = BrassGold,
                        modifier = Modifier.weight(1f)
                    )
                    FinaleStatChip(
                        label = "Prestige Rank",
                        value = "Tier ${prestigeLevel + 1}",
                        color = SimulacrumViolet,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Highest-Bonded Duo Epilogue
                if (topBond != null) {
                    Surface(
                        color = ConsoleGlass,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, LuciferinCyan.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "🤝 APEX SIBLING EPILOGUE • ${topBond.mentorName.uppercase()} & ${topBond.prodigyName.uppercase()} (${topBond.rankTitle})",
                                style = MaterialTheme.typography.labelSmall,
                                color = LuciferinCyan,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = topBond.liveRelationshipQuote,
                                style = MaterialTheme.typography.bodyMedium,
                                fontStyle = FontStyle.Italic,
                                color = TextPrimary
                            )
                        }
                    }
                }

                HorizontalDivider(color = BrassGold.copy(alpha = 0.3f))

                Text(
                    text = "CHOOSE YOUR ENCLAVE HORIZON:",
                    style = MaterialTheme.typography.labelSmall,
                    color = BrassGold,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = onStartNewGamePlusPrestige,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("start_new_game_plus_prestige_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrassGold,
                        contentColor = ObsidianNight
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "New Game Plus Prestige",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ascend to New Game+ (Prestige ${prestigeLevel + 1} • Permanent +${(prestigeLevel + 1) * 5}% Resonance)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onContinueInfiniteCorridor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("continue_infinite_corridor_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Explore,
                        contentDescription = "Keep Exploring",
                        tint = LuciferinCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Keep Exploring Current Infinite Corridor",
                        style = MaterialTheme.typography.labelLarge,
                        color = LuciferinCyan,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun FinaleStatChip(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color.copy(alpha = 0.14f),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.border(1.dp, color.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Interactive Recovery Modal when Colony Morale hits critical low (<= 12%)
 * or Geomagnetic Flux hits critical overload (>= 96%).
 */
@Composable
fun EmergencyBlackoutProtocolModal(
    colonyMorale: Int,
    magneticFlux: Int,
    onVentStormbreakerCapacitors: () -> Unit,
    onExecuteSchumannEmergencyRecoupling: () -> Unit
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .widthIn(max = 580.dp)
                .fillMaxWidth()
                .border(2.dp, VexCrimson, RoundedCornerShape(24.dp))
                .testTag("emergency_blackout_modal"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianNight.copy(alpha = 0.98f))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = VexCrimson.copy(alpha = 0.20f),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, VexCrimson, RoundedCornerShape(14.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Emergency Blackout Protocol",
                            tint = VexCrimson,
                            modifier = Modifier.size(30.dp)
                        )
                        Column {
                            Text(
                                text = "⚠ MAGNETOSPHERIC BLACKOUT PROTOCOL • RED ALERT",
                                style = MaterialTheme.typography.labelSmall,
                                color = VexCrimson,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Morale: $colonyMorale% • Geomagnetic Flux: $magneticFlux%",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = "Corridor bulkheads are arcing under extreme geomagnetic pressure! Augusta and Hector have engaged the Last-Stand Circuit Breaker. Choose an emergency stabilization protocol to save Enclave 16:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )

                Button(
                    onClick = onVentStormbreakerCapacitors,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("emergency_vent_capacitors_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForgeAmber,
                        contentColor = ObsidianNight
                    )
                ) {
                    Icon(imageVector = Icons.Default.Bolt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "1. Shunt Stormbreaker Geodes (-6% Top Project → Morale 62%, Flux 44%)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onExecuteSchumannEmergencyRecoupling,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("emergency_schumann_recoupling_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BlueDwarfGlow,
                        contentColor = ObsidianNight
                    )
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "2. 7.83 Hz Enclave Recoupling (Reset All Stress to 32%, Morale 54%, Flux 55%)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
