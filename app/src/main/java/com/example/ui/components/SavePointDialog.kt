package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.EnclaveSaveSerializer
import com.example.ui.EnclaveSandboxUiState
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
fun SavePointDialog(
    uiState: EnclaveSandboxUiState,
    isGuestMode: Boolean,
    onSaveLocalCheckpoint: () -> Unit,
    onExportSaveFile: () -> Unit,
    onImportSaveFile: () -> Unit,
    onRestoreFromJsonString: (String) -> Boolean,
    onResetTimeline: () -> Unit,
    onDeleteAccountAndCloudData: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    var showRawJsonPanel by remember { mutableStateOf(false) }
    var confirmResetTimeline by remember { mutableStateOf(false) }
    var confirmDeleteAccount by remember { mutableStateOf(false) }
    var rawJsonText by remember(uiState) {
        mutableStateOf(EnclaveSaveSerializer.serializeToJsonString(uiState))
    }
    var feedbackBanner by remember { mutableStateOf<String?>(null) }
    var isErrorBanner by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .widthIn(max = 600.dp)
                .fillMaxWidth()
                .border(1.5.dp, LuciferinCyan.copy(alpha = 0.7f), RoundedCornerShape(24.dp))
                .testTag("save_point_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = ObsidianNight.copy(alpha = 0.96f))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isGuestMode) {
                                "GUEST & TEST SANDBOX • SAVE & PRIVACY MANAGER"
                            } else {
                                "ENCLAVE CLOUD & LOCAL • SAVE & PRIVACY MANAGER"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = LuciferinCyan
                        )
                        Text(
                            text = "Save Points, Campaign & Data Control",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("close_save_dialog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Save Manager",
                            tint = TextSecondary
                        )
                    }
                }

                // Current Save Point Telemetry Summary
                Surface(
                    color = ConsoleGlass,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BrassGold.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
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
                                text = "ACTIVE TIMELINE SNAPSHOT",
                                style = MaterialTheme.typography.labelSmall,
                                color = BrassGold
                            )
                            Text(
                                text = uiState.cloudSyncStatus,
                                style = MaterialTheme.typography.labelSmall,
                                color = LuciferinCyan
                            )
                        }
                        Text(
                            text = "Day ${uiState.dayNumber} • Relay ${uiState.relayStep}/3 • POV: ${uiState.activeCharacter.name} (${uiState.activeCharacter.mbti})",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Sector: ${uiState.currentBiome.title} • Bays: ${uiState.gatesTraversed} • Medals: ${uiState.unlockedMedalIds.size}/12 • Prestige: ${uiState.prestigeLevel}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                if (feedbackBanner != null) {
                    Surface(
                        color = (if (isErrorBanner) VexCrimson else LuciferinCyan).copy(alpha = 0.16f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = feedbackBanner!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isErrorBanner) VexCrimson else LuciferinCyan,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                // 1. Instant On-Device Local Checkpoint
                Button(
                    onClick = {
                        onSaveLocalCheckpoint()
                        isErrorBanner = false
                        feedbackBanner = "Checkpoint saved to local device storage (Day ${uiState.dayNumber}, Relay ${uiState.relayStep})."
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("save_local_checkpoint_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LuciferinCyan,
                        contentColor = ObsidianNight
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Save Local Checkpoint",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save Local Checkpoint Now",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 2. Download / Export & Upload / Restore Portable Save Document (.json)
                Text(
                    text = "PORTABLE SAVE DOCUMENT (.JSON FILE)",
                    style = MaterialTheme.typography.labelSmall,
                    color = BrassGold
                )
                Text(
                    text = "Export your current Enclave timeline as a standalone .json document to your device, or upload a saved document at any time to restore that exact save point.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onExportSaveFile,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("export_save_document_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ForgeAmber,
                            contentColor = ObsidianNight
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Export Save File",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Save to .JSON File",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onImportSaveFile,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("import_save_document_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SimulacrumViolet,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.UploadFile,
                            contentDescription = "Upload Save File",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Upload .JSON Save",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // 3. Direct Copy / Paste JSON Save Inspector
                OutlinedButton(
                    onClick = {
                        rawJsonText = EnclaveSaveSerializer.serializeToJsonString(uiState)
                        showRawJsonPanel = !showRawJsonPanel
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("toggle_raw_json_save_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = "Raw Save JSON",
                        tint = LuciferinCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (showRawJsonPanel) "Hide Raw Save Document JSON" else "View / Paste Raw Save Document JSON",
                        color = LuciferinCyan
                    )
                }

                AnimatedVisibility(visible = showRawJsonPanel) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ConsoleGlass, RoundedCornerShape(14.dp))
                            .border(1.dp, LuciferinCyan.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = rawJsonText,
                            onValueChange = { rawJsonText = it },
                            label = { Text("Save Document JSON Payload") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .testTag("raw_save_json_input"),
                            textStyle = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                color = TextPrimary
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LuciferinCyan,
                                unfocusedBorderColor = BrassGold.copy(alpha = 0.4f)
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val fresh = EnclaveSaveSerializer.serializeToJsonString(uiState)
                                    rawJsonText = fresh
                                    clipboardManager.setText(AnnotatedString(fresh))
                                    isErrorBanner = false
                                    feedbackBanner = "Copied Save Point JSON to clipboard!"
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("copy_save_json_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Save JSON",
                                    tint = BrassGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Copy JSON", color = BrassGold)
                            }

                            Button(
                                onClick = {
                                    val ok = onRestoreFromJsonString(rawJsonText)
                                    if (ok) {
                                        isErrorBanner = false
                                        feedbackBanner = "Save Point restored from JSON!"
                                    } else {
                                        isErrorBanner = true
                                        feedbackBanner = "Invalid JSON format. Could not restore save point."
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("apply_pasted_json_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LuciferinCyan,
                                    contentColor = ObsidianNight
                                )
                            ) {
                                Text("Restore from JSON", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // 4. Campaign Reset & Google Play Store Account/Data Deletion Compliance
                Text(
                    text = "CAMPAIGN RESET & ACCOUNT DATA CONTROL",
                    style = MaterialTheme.typography.labelSmall,
                    color = BrassGold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            if (confirmResetTimeline) {
                                onResetTimeline()
                                confirmResetTimeline = false
                                isErrorBanner = false
                                feedbackBanner = "Campaign reset to Day 1, Relay 1."
                            } else {
                                confirmResetTimeline = true
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("reset_timeline_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Reset Timeline to Day 1",
                            tint = ForgeAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (confirmResetTimeline) "Tap to Confirm Reset" else "New Campaign (Day 1)",
                            color = ForgeAmber,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            if (confirmDeleteAccount) {
                                confirmDeleteAccount = false
                                onDeleteAccountAndCloudData()
                            } else {
                                confirmDeleteAccount = true
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("delete_account_data_button")
                    ) {
                        Text(
                            text = if (confirmDeleteAccount) {
                                "Confirm Permanent Erasure"
                            } else {
                                "Delete Cloud Save & Account"
                            },
                            color = VexCrimson,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // 5. Privacy & Data Usage Notice (Play Store User Data Policy)
                Surface(
                    color = ConsoleGlass,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, LuciferinCyan.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "🔒 PRIVACY & DATA USAGE NOTICE",
                            style = MaterialTheme.typography.labelSmall,
                            color = LuciferinCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "• Local Storage: Enclave 16 stores your campaign state, Sibling Bonds, and Medals locally on your device as a portable JSON checkpoint.\n" +
                                "• Cloud Sync: When signed in with Google, your campaign snapshot syncs to your private user document in Firebase Firestore (`users/{uid}/enclave_saves/primary`).\n" +
                                "• AI Synthesis: Optional AI Crisis & Feast prompts send only in-game character and scientific context to Firebase AI Logic (Gemini); no personal device data is shared.\n" +
                                "• Data Erasure: Tapping 'Delete Cloud Save & Account' permanently deletes your local save file, erases your Firestore cloud save, and removes your Firebase Auth account.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}
