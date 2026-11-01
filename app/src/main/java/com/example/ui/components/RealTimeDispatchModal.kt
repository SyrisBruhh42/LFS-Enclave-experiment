package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.DailyDirective
import com.example.data.local.OfflineAutonomousReport
import com.example.data.local.RealTimeEngagementEngine
import com.example.data.local.TwinSunRealTimePhase
import com.example.ui.theme.BrassGold
import com.example.ui.theme.ConsoleGlass
import com.example.ui.theme.ForgeAmber
import com.example.ui.theme.LuciferinCyan
import com.example.ui.theme.ObsidianNight
import com.example.ui.theme.SimulacrumViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VexCrimson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RealTimeDispatchModal(
    currentPhase: TwinSunRealTimePhase,
    offlineReport: OfflineAutonomousReport?,
    dailyDirectives: List<DailyDirective>,
    overallReadinessPercent: Int,
    dayNumber: Int,
    onClaimOfflineBoost: () -> Unit,
    onDismiss: () -> Unit
) {
    val localClockText = SimpleDateFormat("HH:mm z", Locale.US).format(Date())
    val phaseColor = Color(currentPhase.skyTintHex)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            color = ObsidianNight,
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier
                .widthIn(max = 580.dp)
                .fillMaxWidth(0.95f)
                .border(1.5.dp, phaseColor, RoundedCornerShape(22.dp))
                .testTag("realtime_dispatch_modal")
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
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
                                .size(38.dp)
                                .background(phaseColor.copy(alpha = 0.2f), CircleShape)
                                .border(1.5.dp, phaseColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = "Twin-Sun Chronometer",
                                tint = phaseColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "TWIN-SUN REAL-TIME CHRONOMETER",
                                style = MaterialTheme.typography.labelSmall,
                                color = BrassGold,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${currentPhase.title} • $localClockText",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_realtime_dispatch_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Chronometer",
                            tint = TextSecondary
                        )
                    }
                }

                // 1. Live Real-Time Kepler-452b Phase Card
                Surface(
                    color = ConsoleGlass,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, phaseColor.copy(alpha = 0.65f), RoundedCornerShape(14.dp))
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
                                text = "LIVE ORBITAL WINDOW (${currentPhase.timeWindowLabel})",
                                style = MaterialTheme.typography.labelSmall,
                                color = phaseColor,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "+${RealTimeEngagementEngine.bonusForGate(currentPhase, currentPhase.favoredSquad)}% ${currentPhase.favoredSquad.title} Boost",
                                style = MaterialTheme.typography.labelSmall,
                                color = BrassGold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = currentPhase.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = currentPhase.scientificWeatherNote,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                // 2. "While You Were Away" Autonomous Simulation Card
                Surface(
                    color = ConsoleGlass,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            if (offlineReport != null) LuciferinCyan else BrassGold.copy(alpha = 0.35f),
                            RoundedCornerShape(14.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = LuciferinCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "AUTONOMOUS TERRARIUM SIMULATION (IDLE PROGRESS)",
                                style = MaterialTheme.typography.labelSmall,
                                color = LuciferinCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (offlineReport != null) {
                            Text(
                                text = "Elapsed Away Time: ${offlineReport.elapsedMinutes} mins • Lead Node: ${offlineReport.leadCharacterName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = BrassGold,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = offlineReport.narrativeDispatch,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary
                            )
                            Text(
                                text = "🍲 ${offlineReport.rynKitchenNote}",
                                style = MaterialTheme.typography.labelSmall,
                                color = ForgeAmber
                            )
                            Button(
                                onClick = onClaimOfflineBoost,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LuciferinCyan,
                                    contentColor = ObsidianNight
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("claim_offline_boost_button")
                            ) {
                                Text(
                                    text = "Claim +${offlineReport.projectBoostPercent}% Autonomous Research & +${offlineReport.moraleRecovery}% Morale",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Text(
                                text = "The 16 geniuses continue autonomous research whenever you close the app or step away for 5+ minutes. Return during different real-world hours to collect idle Grand Project breakthroughs and Ryn's kitchen dispatches.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }

                // 3. Daily Enclave Directives (Engagement Loop)
                Surface(
                    color = ConsoleGlass,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BrassGold.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "DAY $dayNumber ENCLAVE DIRECTIVES (DAILY GOALS)",
                            style = MaterialTheme.typography.labelSmall,
                            color = BrassGold,
                            fontWeight = FontWeight.Bold
                        )

                        dailyDirectives.forEach { directive ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (directive.isCompleted) {
                                        Icons.Default.CheckCircle
                                    } else {
                                        Icons.Default.RadioButtonUnchecked
                                    },
                                    contentDescription = null,
                                    tint = if (directive.isCompleted) LuciferinCyan else TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = directive.title,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (directive.isCompleted) LuciferinCyan else TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = directive.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                                Text(
                                    text = if (directive.isCompleted) "DONE" else "+${directive.rewardProjectBoost}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (directive.isCompleted) LuciferinCyan else ForgeAmber,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // 4. Overall Season 1 Endgame Readiness (The Ediacaran Bifurcation Megastorm)
                Surface(
                    color = ConsoleGlass,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SimulacrumViolet.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
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
                                text = "SEASON 1 FINALE: EDIACARAN MEGASTORM READINESS",
                                style = MaterialTheme.typography.labelSmall,
                                color = SimulacrumViolet,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$overallReadinessPercent% / 100%",
                                style = MaterialTheme.typography.labelSmall,
                                color = BrassGold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        LinearProgressIndicator(
                            progress = { overallReadinessPercent / 100f },
                            color = if (overallReadinessPercent >= 80) LuciferinCyan else SimulacrumViolet,
                            trackColor = ObsidianNight,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )
                        Text(
                            text = "Advance all 8 Grand Projects to 100% across the 16 Rashomon POVs to survive the planetary magnetic pole-flip.",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                HorizontalDivider(color = BrassGold.copy(alpha = 0.25f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Surface(
                        color = BrassGold,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .clickable(onClick = onDismiss)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Return to 3D Sandbox",
                            style = MaterialTheme.typography.labelMedium,
                            color = ObsidianNight,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
