package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.BlackMarketPrank
import com.example.data.model.ChronicleEntry
import com.example.data.model.EnclaveCharacter
import com.example.data.model.FeastExchange
import com.example.data.model.GrandProject
import com.example.data.model.HubVetoOption
import com.example.ui.theme.BlueDwarfGlow
import com.example.ui.theme.BrassGold
import com.example.ui.theme.ConsoleGlass
import com.example.ui.theme.ConsoleSurfaceVariant
import com.example.ui.theme.ForgeAmber
import com.example.ui.theme.LuciferinCyan
import com.example.ui.theme.ObsidianNight
import com.example.ui.theme.SimulacrumViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VexCrimson
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GrandFeastDialog(
    dayNumber: Int,
    dishOfTheNight: String,
    exchanges: List<FeastExchange>,
    detonatedPranks: List<BlackMarketPrank>,
    vetoOptions: List<HubVetoOption>,
    isGeneratingAiFeast: Boolean,
    onRequestAiFeastBanter: () -> Unit,
    onExecuteHubVeto: (HubVetoOption) -> Unit
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .widthIn(max = 680.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .border(2.dp, BrassGold, RoundedCornerShape(24.dp))
                .testTag("grand_feast_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianNight)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
                        Surface(
                            color = BrassGold.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Restaurant,
                                    contentDescription = "The Grand Feast",
                                    tint = BrassGold
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "NIGHTLY GRAND FEAST • DAY $dayNumber",
                                style = MaterialTheme.typography.labelSmall,
                                color = BrassGold
                            )
                            Text(
                                text = "All 16 Nodes at Ryn's Table",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onRequestAiFeastBanter,
                        enabled = !isGeneratingAiFeast,
                        modifier = Modifier.testTag("ai_feast_banter_button")
                    ) {
                        if (isGeneratingAiFeast) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = LuciferinCyan
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Roast",
                                tint = LuciferinCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AI Roast", style = MaterialTheme.typography.labelSmall, color = LuciferinCyan)
                    }
                }

                // Ryn's Gastrophysics Menu Banner
                Surface(
                    color = ConsoleGlass,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, ForgeAmber.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "RYN'S GASTROPHYSICS EQUATION (72°C @ 40 RPM CLOCKWISE)",
                            style = MaterialTheme.typography.labelSmall,
                            color = ForgeAmber
                        )
                        Text(
                            text = dishOfTheNight,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Black Market Pranks Detonated Tonight
                    if (detonatedPranks.isNotEmpty()) {
                        item {
                            Surface(
                                color = VexCrimson.copy(alpha = 0.14f),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, VexCrimson, RoundedCornerShape(14.dp))
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "⚡ BLACK MARKET SIBLING GRID DETONATION (3-TURN DELAY)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = VexCrimson,
                                        fontWeight = FontWeight.Bold
                                    )
                                    detonatedPranks.forEach { prank ->
                                        Text(
                                            text = "• ${prank.title}: ${prank.blindSpotOutcome}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Table Exchanges ("Vegetables Covered in Cheese")
                    items(exchanges) { ex ->
                        val sColor = Color(ex.squad.primaryColorHex)
                        Surface(
                            color = ConsoleGlass.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, sColor.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "${ex.speakerName} • ${ex.roleTitle}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = sColor
                                    )
                                    Text(
                                        text = ex.squad.title,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary
                                    )
                                }
                                Text(
                                    text = "\"${ex.dialogue}\"",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "🔬 ${ex.scientificCheeseNote}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = LuciferinCyan.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }

                    // The Hub's Veto Circuit-Breaker Section
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = "Hub Veto",
                                tint = BrassGold
                            )
                            Text(
                                text = "EXERCISE THE HUB'S VETO (Launch Next Day Cycle)",
                                style = MaterialTheme.typography.titleMedium,
                                color = BrassGold
                            )
                        }
                        Text(
                            text = "In any logical dispute among the PhDs, The Hub holds the respected circuit-breaker veto to allocate resources and begin Day ${dayNumber + 1}:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }

                    items(vetoOptions) { option ->
                        val optColor = Color(option.squad.primaryColorHex)
                        Surface(
                            color = ConsoleSurfaceVariant.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.5.dp, optColor, RoundedCornerShape(16.dp))
                                .clickable { onExecuteHubVeto(option) }
                                .testTag("hub_veto_${option.id}")
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = option.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "+${option.projectBoost}% Project",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = LuciferinCyan
                                    )
                                }
                                Text(
                                    text = "Proposed by ${option.proponentName}: ${option.scientificArgument}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                                Button(
                                    onClick = { onExecuteHubVeto(option) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = optColor,
                                        contentColor = ObsidianNight
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Approve & Begin Day ${dayNumber + 1}",
                                        style = MaterialTheme.typography.labelLarge,
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
}

@Composable
fun BlackMarketDialog(
    characters: List<EnclaveCharacter>,
    activePranks: List<BlackMarketPrank>,
    onQueueNewPrank: (instigatorId: String, targetId: String, title: String, detail: String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTargetId by remember { mutableStateOf(characters.getOrNull(2)?.id ?: "kaio") }
    val presetPranks = remember {
        listOf(
            "Copper Ankle Grounding Wire" to "Secretly solder a 12-gauge copper grounding strap to their favorite soup spoon.",
            "Sterilized Sock Filter Swap" to "Divert their morning espresso through a clean botanical sock filter.",
            "5 Extra Gravity Pushups" to "Recalibrate their quarters' magnetic floor plate by +4% local G during morning stretches.",
            "Ferro-Moss Pillow Sabotage" to "Leave a single luminescent spore on their freshly sterilized workbench."
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .widthIn(max = 600.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.86f)
                .border(1.5.dp, VexCrimson, RoundedCornerShape(24.dp))
                .testTag("black_market_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianNight)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VisibilityOff,
                            contentDescription = "Black Market",
                            tint = VexCrimson
                        )
                        Column {
                            Text(
                                text = "THE SHADOW ECONOMY • THE SIBLING GRID",
                                style = MaterialTheme.typography.labelSmall,
                                color = VexCrimson
                            )
                            Text(
                                text = "Asynchronous 3-Turn Retaliation",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimary
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close Black Market", tint = TextSecondary)
                    }
                }

                Text(
                    text = "Because all 16 geniuses assume they are the smartest person in the room, petty retaliation takes 3 Choice Gate turns to detonate—and always hits an accidental bystander who blames someone else.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                Text(
                    text = "1. SELECT TARGET SIBLING / MENTOR:",
                    style = MaterialTheme.typography.labelSmall,
                    color = BrassGold
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(characters) { char ->
                        val isSelected = char.id == selectedTargetId
                        Surface(
                            color = if (isSelected) VexCrimson else ConsoleGlass,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .border(1.dp, VexCrimson.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                .clickable { selectedTargetId = char.id }
                        ) {
                            Text(
                                text = "${char.name} (${char.mbti})",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) ObsidianNight else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "2. PLANT MICRO-AGGRESSION (3-GATE FUSE):",
                    style = MaterialTheme.typography.labelSmall,
                    color = BrassGold
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(presetPranks) { (title, detail) ->
                        Surface(
                            color = ConsoleGlass,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, VexCrimson.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = detail,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Button(
                                    onClick = {
                                        onQueueNewPrank("silas", selectedTargetId, title, detail)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = VexCrimson,
                                        contentColor = ObsidianNight
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Queue (3t)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    item {
                        HorizontalDivider(color = BrassGold.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 4.dp))
                        Text(
                            text = "ACTIVE SHADOW QUEUE (${activePranks.size} PENDING):",
                            style = MaterialTheme.typography.labelSmall,
                            color = LuciferinCyan
                        )
                    }

                    items(activePranks) { prank ->
                        Surface(
                            color = ConsoleSurfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = prank.title,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = VexCrimson
                                    )
                                    Text(
                                        text = "Detonates in ${prank.turnsRemaining} gate(s)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = BrassGold
                                    )
                                }
                                Text(
                                    text = prank.pettyDescription,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Blind-Spot Trajectory: ${prank.blindSpotOutcome}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LivingAtlasAndCodexDialog(
    projects: List<GrandProject>,
    characters: List<EnclaveCharacter>,
    chronicle: List<ChronicleEntry>,
    magneticFlux: Int,
    siblingBonds: List<com.example.data.model.SiblingBondTrack> = emptyList(),
    medals: List<com.example.data.model.EnclaveAchievementMedal> = emptyList(),
    prestigeLevel: Int = 0,
    canTriggerGrandConvergence: Boolean = false,
    onTriggerGrandConvergence: () -> Unit = {},
    onSelectPovCharacter: (EnclaveCharacter) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val unlockedMedalsCount = remember(medals) { medals.count { it.isUnlocked } }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .widthIn(max = 700.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .border(1.5.dp, LuciferinCyan, RoundedCornerShape(24.dp))
                .testTag("living_atlas_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianNight)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Living Atlas",
                            tint = LuciferinCyan
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            val prestigeTag = if (prestigeLevel > 0) " • PRESTIGE $prestigeLevel" else ""
                            Text(
                                text = "AUGUSTA'S 3D LIVING ATLAS & CODEX$prestigeTag",
                                style = MaterialTheme.typography.labelSmall,
                                color = LuciferinCyan
                            )
                            Text(
                                text = "8 Projects • 16 Geniuses • $unlockedMedalsCount/${medals.size.coerceAtLeast(12)} Medals",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close Atlas", tint = TextSecondary)
                    }
                }

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = ConsoleGlass,
                    contentColor = LuciferinCyan,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        modifier = Modifier.testTag("atlas_tab_projects"),
                        text = { Text("8 Projects", style = MaterialTheme.typography.labelSmall) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        modifier = Modifier.testTag("atlas_tab_dossier"),
                        text = { Text("16 Dossiers", style = MaterialTheme.typography.labelSmall) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        modifier = Modifier.testTag("atlas_tab_bonds_medals"),
                        text = { Text("Bonds & Medals", style = MaterialTheme.typography.labelSmall) }
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        modifier = Modifier.testTag("atlas_tab_chronicle"),
                        text = { Text("Log (${chronicle.size})", style = MaterialTheme.typography.labelSmall) }
                    )
                }

                when (selectedTab) {
                    0 -> {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (canTriggerGrandConvergence) {
                                item {
                                    Surface(
                                        color = BrassGold.copy(alpha = 0.16f),
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(1.5.dp, BrassGold, RoundedCornerShape(16.dp))
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(14.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "☀ THE GRAND CONVERGENCE READY",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = BrassGold,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "Kepler-452b Readiness Threshold Reached! Launch the Finale or ascend to New Game+ Prestige.",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = TextPrimary
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Button(
                                                onClick = onTriggerGrandConvergence,
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = BrassGold,
                                                    contentColor = ObsidianNight
                                                ),
                                                shape = RoundedCornerShape(12.dp),
                                                modifier = Modifier.testTag("atlas_launch_grand_convergence_button")
                                            ) {
                                                Text("Launch Finale", fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }

                            // 3D Wireframe Globe Visualization of Sector 4 Anomaly
                            item {
                                Surface(
                                    color = ConsoleGlass,
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(150.dp)
                                        .border(1.dp, BlueDwarfGlow.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Canvas(modifier = Modifier.size(110.dp)) {
                                            val r = size.minDimension * 0.45f
                                            val c = Offset(size.width / 2f, size.height / 2f)
                                            drawCircle(
                                                color = BlueDwarfGlow.copy(alpha = 0.25f),
                                                radius = r,
                                                center = c,
                                                style = Stroke(width = 2.5f)
                                            )
                                            for (i in 1..3) {
                                                val latR = r * (i / 4f)
                                                drawOval(
                                                    color = LuciferinCyan.copy(alpha = 0.35f),
                                                    topLeft = Offset(c.x - r, c.y - latR),
                                                    size = androidx.compose.ui.geometry.Size(r * 2f, latR * 2f),
                                                    style = Stroke(width = 1.5f)
                                                )
                                            }
                                            // Sector 4 Magnetic Tear Hotspot
                                            drawCircle(
                                                color = VexCrimson,
                                                radius = 12f,
                                                center = Offset(c.x + r * 0.35f, c.y - r * 0.2f)
                                            )
                                        }
                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text(
                                                text = "SECTOR 4 MAGNETOSPHERE WIREFRAME",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = BlueDwarfGlow
                                            )
                                            Text(
                                                text = "Geomagnetic Flux: $magneticFlux% • Ediacaran Bifurcation Active",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = "Resolve Corridor Chamber Doors or use The Hub's Veto at the Feast to advance the 8 Grand Projects toward 100%.",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                }
                            }

                            items(projects) { project ->
                                val pColor = Color(project.squad.primaryColorHex)
                                val projectPerks = com.example.data.model.CognitiveResonanceAndMilestonesEngine
                                    .buildAllProjectMilestones(projects)
                                    .filter { it.projectId == project.id }
                                Surface(
                                    color = ConsoleGlass,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, pColor.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = project.name,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "${project.progressPercent}%",
                                                style = MaterialTheme.typography.labelLarge,
                                                color = pColor
                                            )
                                        }
                                        LinearProgressIndicator(
                                            progress = { project.progressPercent / 100f },
                                            color = pColor,
                                            trackColor = ObsidianNight,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(8.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                        )
                                        Text(
                                            text = project.scientificQuestion,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "Obstacle: ${project.currentObstacle}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondary
                                        )
                                        projectPerks.forEach { perk ->
                                            val perkColor = if (perk.isUnlocked) LuciferinCyan else BrassGold.copy(alpha = 0.7f)
                                            Surface(
                                                color = perkColor.copy(alpha = if (perk.isUnlocked) 0.14f else 0.06f),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .border(1.dp, perkColor.copy(alpha = 0.55f), RoundedCornerShape(8.dp))
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(8.dp),
                                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                                ) {
                                                    Text(
                                                        text = if (perk.isUnlocked) {
                                                            "🏆 ${perk.thresholdPercent}% PERK UNLOCKED: ${perk.title}"
                                                        } else {
                                                            "🔒 ${perk.thresholdPercent}% MILESTONE PERK: ${perk.title}"
                                                        },
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = perkColor,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        text = perk.benefitSummary,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = if (perk.isUnlocked) TextPrimary else TextSecondary
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(characters) { char ->
                                val cColor = Color(char.squad.primaryColorHex)
                                val voiceLock = com.example.data.ai.PsychometricGuardrailEngine.buildVoiceLock(char)
                                val partnerBond = siblingBonds.firstOrNull {
                                    it.mentorId == char.id || it.prodigyId == char.id
                                }
                                Surface(
                                    color = ConsoleGlass,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, cColor.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
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
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "${char.name} • ${char.roleTitle}",
                                                    style = MaterialTheme.typography.titleSmall,
                                                    color = TextPrimary,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "${if (char.isMentor) "Mentor" else "Prodigy"} • ${char.mbti} (${char.cognitiveStack}) • ${char.enneagram}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = cColor
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Button(
                                                onClick = {
                                                    onSelectPovCharacter(char)
                                                    onDismiss()
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = cColor,
                                                    contentColor = ObsidianNight
                                                ),
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Text("Switch POV", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        if (partnerBond != null) {
                                            Surface(
                                                color = BrassGold.copy(alpha = 0.12f),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .border(1.dp, BrassGold.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(8.dp),
                                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                                ) {
                                                    Text(
                                                        text = "🤝 ${partnerBond.pairTitle} (${partnerBond.mentorName} & ${partnerBond.prodigyName}) • ${partnerBond.rankTitle} (+${partnerBond.resonanceBonusPercent}% Duo Resonance)",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = BrassGold,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        text = partnerBond.liveRelationshipQuote,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = TextPrimary
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = "Big 5 (OCEAN): O:${char.bigFive.openness}% C:${char.bigFive.conscientiousness}% E:${char.bigFive.extraversion}% A:${char.bigFive.agreeableness}% N:${char.bigFive.neuroticism}%",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = LuciferinCyan
                                        )
                                        Text(
                                            text = "🔒 Anti-Drift Cadence Lock: ${voiceLock.oceanSpeechCadenceRule}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = SimulacrumViolet
                                        )
                                        Text(
                                            text = "Vex Override (${char.vexStarSign} - ${char.vexOverrideTitle}): ${char.vexOverrideBehavior}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = VexCrimson.copy(alpha = 0.9f)
                                        )
                                        Text(
                                            text = "Core Lie: \"${char.lieBelieved}\" → Truth: \"${char.truthToLearn}\"",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = BrassGold
                                        )
                                        Text(
                                            text = "Genius vs. Mundane: ${char.geniusVsMundane}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            item {
                                Text(
                                    text = "8 MENTOR–PRODIGY SIBLING BONDS (PERSISTS ACROSS NEW GAME+)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BrassGold,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            items(siblingBonds) { bond ->
                                Surface(
                                    color = ConsoleGlass,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, BrassGold.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
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
                                                text = "🤝 ${bond.pairTitle}: ${bond.mentorName} & ${bond.prodigyName}",
                                                style = MaterialTheme.typography.titleSmall,
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "${bond.rankTitle} • +${bond.resonanceBonusPercent}%",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = LuciferinCyan,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        LinearProgressIndicator(
                                            progress = { (bond.bondXp.toFloat() / bond.nextRankXp.toFloat()).coerceIn(0f, 1f) },
                                            color = BrassGold,
                                            trackColor = ObsidianNight,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp))
                                        )
                                        Text(
                                            text = "Bond XP: ${bond.bondXp} / ${bond.nextRankXp} • ${bond.liveRelationshipQuote}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "ENCLAVE CAREER & DISCOVERY MEDALS ($unlockedMedalsCount/${medals.size})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = LuciferinCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            items(medals) { medal ->
                                val mColor = if (medal.isUnlocked) LuciferinCyan else TextSecondary.copy(alpha = 0.5f)
                                Surface(
                                    color = if (medal.isUnlocked) LuciferinCyan.copy(alpha = 0.10f) else ConsoleGlass,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, mColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = medal.badgeIconText,
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                            Column {
                                                Text(
                                                    text = medal.title,
                                                    style = MaterialTheme.typography.titleSmall,
                                                    color = if (medal.isUnlocked) TextPrimary else TextSecondary,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = medal.description,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = TextSecondary
                                                )
                                            }
                                        }
                                        Text(
                                            text = if (medal.isUnlocked) "UNLOCKED" else "LOCKED",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (medal.isUnlocked) BrassGold else TextSecondary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    3 -> {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (chronicle.isEmpty()) {
                                item {
                                    Text(
                                        text = "Open and resolve your first 3D Corridor Chamber Door to record telemetry in Dorian's Historian RAG Archive.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            } else {
                                items(chronicle) { entry ->
                                    Surface(
                                        color = ConsoleGlass,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = "DAY ${entry.dayNumber} • RELAY ${entry.relayStep} • POV: ${entry.povName.uppercase()} (${entry.povMbti})",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = BrassGold
                                            )
                                            Text(
                                                text = "Chamber Resolved: ${entry.gateTitle} [${entry.scientificDomain}]",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = LuciferinCyan
                                            )
                                            Text(
                                                text = entry.summary,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = TextPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
