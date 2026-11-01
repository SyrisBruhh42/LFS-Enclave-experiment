package com.example.ui.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.example.data.local.EnclaveSaveSerializer
import com.example.ui.theme.BlueDwarfGlow
import com.example.ui.theme.BrassGold
import com.example.ui.theme.ConsoleGlass
import com.example.ui.theme.ForgeAmber
import com.example.ui.theme.LuciferinCyan
import com.example.ui.theme.ObsidianNight
import com.example.ui.theme.RedGiantFlare
import com.example.ui.theme.SimulacrumViolet
import com.example.ui.theme.TextSecondary
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "Auth"

fun attemptAutoSignIn(
    context: Context,
    credentialManager: CredentialManager,
    onAuthSuccess: () -> Unit,
    onUnauthenticated: () -> Unit,
    scope: CoroutineScope
) {
    if (Firebase.auth.currentUser != null) {
        onAuthSuccess()
        return
    }
    val clientId = try {
        context.getString(R.string.default_web_client_id)
    } catch (e: Exception) {
        onUnauthenticated()
        return
    }

    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(true)
        .setServerClientId(clientId)
        .setAutoSelectEnabled(true)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    scope.launch {
        try {
            val result = credentialManager.getCredential(context, request)
            val credential = result.credential
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                Firebase.auth.signInWithCredential(authCredential).await()
                onAuthSuccess()
            } else {
                onUnauthenticated()
            }
        } catch (e: Exception) {
            onUnauthenticated()
        }
    }
}

fun onGoogleSignInClicked(
    context: Context,
    credentialManager: CredentialManager,
    onAuthSuccess: () -> Unit,
    onAuthError: (String) -> Unit,
    scope: CoroutineScope,
    onAuthCancelled: () -> Unit = {}
) {
    val clientId = try {
        context.getString(R.string.default_web_client_id)
    } catch (e: Exception) {
        onAuthError("Google Sign-In configuration missing: default_web_client_id not found")
        return
    }

    val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
    val request = GetCredentialRequest.Builder()
        .addCredentialOption(signInOption)
        .build()

    scope.launch {
        try {
            val activity = context as? Activity
            val result = if (activity != null) {
                credentialManager.getCredential(activity, request)
            } else {
                credentialManager.getCredential(context, request)
            }
            val credential = result.credential
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
                val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                Firebase.auth.signInWithCredential(authCredential).await()
                onAuthSuccess()
            } else {
                onAuthError("Unexpected credential type received.")
            }
        } catch (e: GetCredentialCancellationException) {
            Log.w(TAG, "Google Sign-In cancelled or dismissed: ${e.message}", e)
            onAuthCancelled()
        } catch (e: Exception) {
            Log.e(TAG, "Google Sign-In failed", e)
            onAuthError(e.localizedMessage ?: "Sign in failed")
        }
    }
}

fun signOutUser(
    context: Context,
    onSignOutComplete: () -> Unit,
    scope: CoroutineScope
) {
    val credentialManager = CredentialManager.create(context)
    Firebase.auth.signOut()
    scope.launch {
        try {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear credential state", e)
        } finally {
            onSignOutComplete()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    onEnterGuestSandbox: (initialSaveJson: String?) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val hasExistingLocalSave = remember { EnclaveSaveSerializer.hasInternalSave(context) }

    val openSaveDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val readResult = runCatching {
                context.contentResolver.openInputStream(uri)?.use { input ->
                    input.bufferedReader(Charsets.UTF_8).readText()
                } ?: error("Unable to read selected save file.")
            }
            readResult.onSuccess { jsonText ->
                val validate = EnclaveSaveSerializer.deserializeFromJsonString(jsonText)
                if (validate.isSuccess) {
                    onEnterGuestSandbox(jsonText)
                } else {
                    errorMessage = "Selected file is not a valid Enclave 16 save document."
                }
            }.onFailure { err ->
                errorMessage = err.localizedMessage ?: "Failed to read save file."
            }
        }
    }

    LaunchedEffect(Unit) {
        attemptAutoSignIn(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = onAuthSuccess,
            onUnauthenticated = {},
            scope = coroutineScope
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(ObsidianNight, Color(0xFF101C33), ObsidianNight)
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.Center
    ) {
        // Atmospheric Twin Suns & 3D Gate Wireframe Backdrop
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Red Giant East
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(RedGiantFlare.copy(alpha = 0.45f), Color.Transparent),
                    center = Offset(w * 0.22f, h * 0.18f),
                    radius = w * 0.35f
                ),
                center = Offset(w * 0.22f, h * 0.18f),
                radius = w * 0.35f
            )
            // Blue Dwarf West
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(BlueDwarfGlow.copy(alpha = 0.5f), Color.Transparent),
                    center = Offset(w * 0.8f, h * 0.14f),
                    radius = w * 0.22f
                ),
                center = Offset(w * 0.8f, h * 0.14f),
                radius = w * 0.22f
            )

            // Stylized 3D Perspective Grid & Choice Gate Silhouette
            val horizonY = h * 0.68f
            for (i in -5..5) {
                val startX = w * 0.5f + i * (w * 0.04f)
                val endX = w * 0.5f + i * (w * 0.22f)
                drawLine(
                    color = LuciferinCyan.copy(alpha = 0.15f),
                    start = Offset(startX, horizonY),
                    end = Offset(endX, h),
                    strokeWidth = 1.5f
                )
            }
            drawCircle(
                color = LuciferinCyan.copy(alpha = 0.22f),
                center = Offset(w * 0.5f, horizonY - 40f),
                radius = 78f,
                style = Stroke(width = 3f)
            )
        }

        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .widthIn(max = 440.dp)
                .fillMaxWidth()
                .border(1.5.dp, BrassGold.copy(alpha = 0.65f), RoundedCornerShape(22.dp)),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = ConsoleGlass.copy(alpha = 0.95f)
            )
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = LuciferinCyan.copy(alpha = 0.14f),
                    modifier = Modifier.border(1.dp, LuciferinCyan.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hub,
                            contentDescription = "Enclave Matrix",
                            tint = LuciferinCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "16-NODE SOCIOLOGICAL SANDBOX",
                            style = MaterialTheme.typography.labelSmall,
                            color = LuciferinCyan
                        )
                    }
                }

                Text(
                    text = "THE ENCLAVE OF THE 16",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Walk through 3D Choice Gates in third-person. Experience the Rashomon POV relay across 16 psychometric geniuses, Vex Overrides, and the nightly Grand Feast.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FeatureBadge("3D Choice Gates", LuciferinCyan)
                    FeatureBadge("MBTI/OCEAN Locked", SimulacrumViolet)
                    FeatureBadge("Local & .JSON Save", ForgeAmber)
                }

                if (errorMessage != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = errorMessage!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(10.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // 1. Instant Guest / Test Sandbox Entry (No Sign-In Required)
                Button(
                    onClick = {
                        errorMessage = null
                        onEnterGuestSandbox(null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("guest_sandbox_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LuciferinCyan,
                        contentColor = ObsidianNight
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = "Enter Guest Test Sandbox",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (hasExistingLocalSave) {
                            "Continue Guest / Test Sandbox"
                        } else {
                            "Enter as Guest / Test Account"
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 2. Upload / Restore Portable Save Document (.json) directly from Auth Screen
                OutlinedButton(
                    onClick = {
                        errorMessage = null
                        openSaveDocumentLauncher.launch(
                            arrayOf("application/json", "text/plain", "*/*")
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("restore_save_file_auth_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.UploadFile,
                        contentDescription = "Restore from Save File",
                        tint = BrassGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Upload Save Document (.json)",
                        style = MaterialTheme.typography.labelLarge,
                        color = BrassGold,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = TextSecondary.copy(alpha = 0.25f))
                    Text(
                        text = "OR CLOUD SYNC",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = TextSecondary.copy(alpha = 0.25f))
                }

                // 3. Google Sign-In for Cloud Sync
                Button(
                    onClick = {
                        errorMessage = null
                        isLoading = true
                        onGoogleSignInClicked(
                            context = context,
                            credentialManager = credentialManager,
                            onAuthSuccess = {
                                isLoading = false
                                onAuthSuccess()
                            },
                            onAuthError = { msg ->
                                isLoading = false
                                errorMessage = msg
                            },
                            scope = coroutineScope,
                            onAuthCancelled = {
                                isLoading = false
                                errorMessage = "Sign-in was cancelled. Tap 'Enter as Guest / Test Account' above to play immediately."
                            }
                        )
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("google_sign_in_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForgeAmber,
                        contentColor = ObsidianNight
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = ObsidianNight,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = "Sign in with Google",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sign in with Google",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeatureBadge(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.14f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
