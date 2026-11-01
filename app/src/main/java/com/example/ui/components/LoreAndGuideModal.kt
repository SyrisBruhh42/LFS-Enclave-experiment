package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Squad
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
fun LoreAndGuideModal(
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("1. Prologue", "2. How to Play", "3. The 4 Squads")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .padding(12.dp)
                .widthIn(max = 620.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.90f)
                .border(1.5.dp, BrassGold, RoundedCornerShape(22.dp))
                .testTag("lore_and_guide_modal"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianNight)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
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
                            color = LuciferinCyan.copy(alpha = 0.18f),
                            shape = CircleShape,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AutoStories,
                                    contentDescription = "Field Guide",
                                    tint = LuciferinCyan
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "FIELD BRIEFING & LORE GUIDE",
                                style = MaterialTheme.typography.labelSmall,
                                color = LuciferinCyan
                            )
                            Text(
                                text = "Welcome to The Enclave of the 16",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_guide_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Guide",
                            tint = TextSecondary
                        )
                    }
                }

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = ConsoleGlass,
                    contentColor = LuciferinCyan,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    tabs.forEachIndexed { idx, title ->
                        Tab(
                            selected = selectedTab == idx,
                            onClick = { selectedTab = idx },
                            text = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (selectedTab) {
                        0 -> PrologueTabContent()
                        1 -> HowToPlayTabContent()
                        2 -> FourSquadsTabContent()
                    }
                }

                // Bottom Navigation / Enter World Footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (selectedTab < 2) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("skip_guide_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Skip to World", color = TextSecondary)
                        }
                        Button(
                            onClick = { selectedTab += 1 },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("next_guide_tab_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LuciferinCyan,
                                contentColor = ObsidianNight
                            )
                        ) {
                            Text("Next", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    } else {
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("enter_world_from_guide_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LuciferinCyan,
                                contentColor = ObsidianNight
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Explore,
                                contentDescription = "Enter 3D Enclave",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Begin Exploration (Day 1 • Relay 1)",
                                style = MaterialTheme.typography.titleSmall,
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
private fun PrologueTabContent() {
    GuideSectionCard(
        badge = "THE SETTING • KEPLER-452B",
        title = "A Sanctuary Under Twin Suns",
        accentColor = BrassGold,
        body = "Beneath the opposing magnetic fields of a Red Giant (East) and a Blue Dwarf (West), sixteen polymath scientists—8 seasoned Mentors and 8 brilliant Prodigies—inhabit a sealed bioluminescent valley known as The Enclave."
    )

    GuideSectionCard(
        badge = "CORE RULE • NO HUMAN VILLAINS",
        title = "Hard Science + Found-Family Friction",
        accentColor = LuciferinCyan,
        body = "There are no traitors or saboteurs in the Enclave. Everyone fiercely loves one another. The conflict comes from trying to tame a volatile alien biosphere (50 kV/m lightning storms, prime-number fungi, subterranean leviathans) while 16 strong-willed geniuses argue over how to fix a leaky pipe or who grounded the soup spoons."
    )

    GuideSectionCard(
        badge = "PSYCHOMETRIC FIDELITY",
        title = "16 Distinct Minds (MBTI + Big 5 OCEAN)",
        accentColor = SimulacrumViolet,
        body = "Each of the 16 characters is locked to a real psychological profile—MBTI cognitive function stack, Enneagram core fear, and Big 5 OCEAN speech cadence. When you play as Silas (ISTJ), the world is measured in tolerances and galvanic rust; when the relay passes to Kaio (ENFP), the same forest bursts into kinetic curiosity."
    )
}

@Composable
private fun HowToPlayTabContent() {
    GuideStepRow(
        icon = Icons.AutoMirrored.Filled.DirectionsWalk,
        color = LuciferinCyan,
        title = "1. Walk the Infinite Corridor & 4 Door Archetypes",
        description = "As you walk down the enclosed 3D corridor, the top HUD auto-slims so the 3D hallway fills your screen. Approach 4 distinct door types on the Left & Right walls: ⚗ Standard Lab Bays, ⚠ Ticking Crisis Bulkheads, 🔒 Cognitive-Locked Vaults, and 🍵 Sanctuary Lounge Alcoves."
    )

    GuideStepRow(
        icon = Icons.Default.Groups,
        color = ForgeAmber,
        title = "2. Cognitive Resonance & Dramatic Aftermath",
        description = "Inside every door, each choice shows a live Cognitive Resonance % based on your character's MBTI stack, OCEAN traits, and Sibling Synergy. Committing a choice reveals an immediate Dramatic Aftermath screen with partner banter and Breakthrough bonuses—or you can step back into the hallway anytime!"
    )

    GuideStepRow(
        icon = Icons.Default.Restaurant,
        color = BrassGold,
        title = "3. 8 Grand Project Milestones (50% & 100%) + Nightly Feast",
        description = "Pushing any of the 8 Grand Projects across 50% or 100% unlocks permanent Enclave superpowers (Vault Override Torch, 5D Outcome Predictor, 4-Bay Radar, Schumann +15% Resonance). Dock at the Nightly Grand Feast to detonate pranks and exercise The Hub's Veto."
    )

    GuideStepRow(
        icon = Icons.Default.Bolt,
        color = VexCrimson,
        title = "4. Vex Override (Personality Inversion)",
        description = "Watch the Vex Stress bar at the top! If a character's stress reaches 80%+ (or you tap 'Vex Flip' in the bottom bar), twin-sun magnetic cross-polarity flips them into their opposite 'Ironic Star Sign' persona and unlocks a 4th crimson Vex Override choice inside every door."
    )
}

@Composable
private fun FourSquadsTabContent() {
    Squad.entries.forEach { squad ->
        val sColor = Color(squad.primaryColorHex)
        val details = when (squad) {
            Squad.FORGE -> "Silas & Leo (The First Forge) • Hector & Jane (Stormbreaker Tower). Masters of high-entropy metallurgy, ferrofluid seals, and femtosecond laser lightning capture."
            Squad.WILDS -> "Kaio & Zara (The Sanctuary) • Willow & Terra (Genesis Garden). Masters of prime-number bioluminescence, deep-aquifer ethology, and mycelial root networks."
            Squad.MIND -> "Kenna & Elias (The Lyceum) • Isla & Orion (The Simulacrum). Masters of cymatic neuro-acoustics, Schumann resonance therapy, and 5D quartz holography."
            Squad.HUB -> "Augusta & Cassia (The Living Atlas) • Ryn & Vex (The Obsessive Table). Masters of planetary magnetosphere mapping, gastrophysics, and the circuit-breaker Hub Veto."
        }
        Surface(
            color = ConsoleGlass,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, sColor.copy(alpha = 0.55f), RoundedCornerShape(14.dp))
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${squad.badgeSymbol} ${squad.title.uppercase()} — ${squad.subtitle}",
                    style = MaterialTheme.typography.labelLarge,
                    color = sColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = details,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
private fun GuideSectionCard(
    badge: String,
    title: String,
    accentColor: Color,
    body: String
) {
    Surface(
        color = ConsoleGlass,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, accentColor.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = badge,
                style = MaterialTheme.typography.labelSmall,
                color = accentColor,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun GuideStepRow(
    icon: ImageVector,
    color: Color,
    title: String,
    description: String
) {
    Surface(
        color = ConsoleGlass,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, color.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                color = color.copy(alpha = 0.18f),
                shape = CircleShape,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = color,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}
