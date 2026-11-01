package com.example.ui.world3d

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.data.model.Biome3D
import com.example.data.model.ChoiceGate3D
import com.example.data.model.CorridorDoor3D
import com.example.data.model.DoorArchetype
import com.example.data.model.DoorSide
import com.example.data.model.DoorStatus
import com.example.data.model.EnclaveCharacter
import com.example.data.model.InfiniteCorridorGenerator
import com.example.data.model.Vec3
import com.example.ui.theme.BlueDwarfGlow
import com.example.ui.theme.BrassGold
import com.example.ui.theme.ConsoleGlass
import com.example.ui.theme.ForgeAmber
import com.example.ui.theme.LuciferinCyan
import com.example.ui.theme.ObsidianNight
import com.example.ui.theme.RedGiantFlare
import com.example.ui.theme.SimulacrumViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VexCrimson
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

data class ProjectedPoint(
    val screenX: Float,
    val screenY: Float,
    val depthZ: Float,
    val scale: Float,
    val visible: Boolean
)

@Composable
fun EnclaveWorld3DCanvas(
    activeCharacter: EnclaveCharacter,
    biome: Biome3D,
    corridorDoors: List<CorridorDoor3D>,
    gates: List<ChoiceGate3D>,
    playerPos: Vec3,
    playerYaw: Float,
    cameraYaw: Float,
    cameraPitch: Float,
    magneticFlux: Int,
    colonyMorale: Int = 86,
    hasSiblingSynergyBuff: Boolean = false,
    hasAtlasRadarPerk: Boolean = false,
    breakthroughBurstCount: Int = 0,
    onPlayerMovingChanged: (Boolean) -> Unit = {},
    onMoveStep: (dx: Float, dz: Float, newFacingYaw: Float?) -> Unit,
    onUpdateCorridorPosition: (newPos: Vec3, newYaw: Float) -> Unit,
    onRotateCamera: (deltaYaw: Float, deltaPitch: Float) -> Unit,
    onResetCameraBehindPlayer: () -> Unit,
    onOpenCorridorDoor: (CorridorDoor3D) -> Unit,
    onAutoWalkToGate: (ChoiceGate3D) -> Unit,
    onWalkThroughGate: (ChoiceGate3D) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val density = LocalDensity.current
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    // DrawScope-isolated 60fps coordinates in the Infinite Enclosed Corridor
    var animTime by remember { mutableFloatStateOf(0f) }
    var localPosX by remember { mutableFloatStateOf(playerPos.x) }
    var localPosZ by remember { mutableFloatStateOf(playerPos.z) }
    var localPlayerYaw by remember { mutableFloatStateOf(playerYaw) }
    var localCamYaw by remember { mutableFloatStateOf(cameraYaw) }
    var localCamPitch by remember { mutableFloatStateOf(cameraPitch) }
    var lastSyncedZ by remember { mutableFloatStateOf(playerPos.z) }
    var breakthroughBurstTimer by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(breakthroughBurstCount) {
        if (breakthroughBurstCount > 0) {
            breakthroughBurstTimer = 2.4f
        }
    }

    // Smooth auto-walk target Door when the user taps a door in 3D or on the quick-door list
    var autoWalkTargetDoor by remember { mutableStateOf<CorridorDoor3D?>(null) }

    // Door currently within 4.4m proximity threshold
    var approachedDoor by remember { mutableStateOf<CorridorDoor3D?>(null) }

    // Track last door triggered by physical doorway entry so standing outside doesn't spam re-open
    var lastAutoOpenedDoorId by remember { mutableStateOf<String?>(null) }

    val currentDoors by rememberUpdatedState(corridorDoors)
    val currentOnOpenDoor by rememberUpdatedState(onOpenCorridorDoor)
    val currentOnUpdatePosition by rememberUpdatedState(onUpdateCorridorPosition)
    val currentOnMovingChanged by rememberUpdatedState(onPlayerMovingChanged)

    // Keyboard state for WASD / Arrow Keys
    var keyW by remember { mutableStateOf(false) }
    var keyS by remember { mutableStateOf(false) }
    var keyA by remember { mutableStateOf(false) }
    var keyD by remember { mutableStateOf(false) }
    var keyRotLeft by remember { mutableStateOf(false) }
    var keyRotRight by remember { mutableStateOf(false) }

    // Mobile Virtual Thumbstick vector (-1..1)
    var stickX by remember { mutableFloatStateOf(0f) }
    var stickY by remember { mutableFloatStateOf(0f) }

    // Cached native Paint objects for zero-allocation 60fps Canvas text labels
    val titlePaint = remember(density) {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
            textSize = with(density) { 11.5.dp.toPx() }
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
    }
    val subtitlePaint = remember(density) {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = LuciferinCyan.toArgb()
            textSize = with(density) { 9.5.dp.toPx() }
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
        }
    }
    val reusablePath = remember { Path() }

    LaunchedEffect(Unit) {
        try {
            focusRequester.requestFocus()
        } catch (_: Exception) {
        }
    }

    // Sync external resets (e.g. Save Point restore or Day reset)
    LaunchedEffect(playerPos) {
        if (abs(playerPos.x - localPosX) > 3.5f || abs(playerPos.z - localPosZ) > 6.0f) {
            localPosX = playerPos.x.coerceIn(-3.1f, 3.1f)
            localPosZ = playerPos.z.coerceAtLeast(-2f)
            lastSyncedZ = localPosZ
        }
    }

    val isMovingNow = keyW || keyS || keyA || keyD ||
        abs(stickX) > 0.08f || abs(stickY) > 0.08f || autoWalkTargetDoor != null

    LaunchedEffect(isMovingNow) {
        currentOnMovingChanged(isMovingNow)
    }

    // High-efficiency 60Hz game & physics loop inside the Infinite Enclosed Hallway
    LaunchedEffect(Unit) {
        var lastNanos = 0L
        while (true) {
            withFrameNanos { nanos ->
                val dt = if (lastNanos == 0L) 0.016f else ((nanos - lastNanos) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                lastNanos = nanos
                animTime += dt
                if (breakthroughBurstTimer > 0f) {
                    breakthroughBurstTimer = (breakthroughBurstTimer - dt).coerceAtLeast(0f)
                }

                // Camera rotation via Q/E keys
                if (keyRotLeft) localCamYaw -= 1.8f * dt
                if (keyRotRight) localCamYaw += 1.8f * dt

                // Combine WASD/Arrow keys and Virtual Thumbstick
                val forwardInput = (if (keyW) 1f else 0f) - (if (keyS) 1f else 0f) - stickY
                val strafeInput = (if (keyD) 1f else 0f) - (if (keyA) 1f else 0f) + stickX
                val manualMag = sqrt(forwardInput * forwardInput + strafeInput * strafeInput)

                if (manualMag > 0.08f) {
                    autoWalkTargetDoor = null
                    val normF = forwardInput / manualMag.coerceAtLeast(1f)
                    val normS = strafeInput / manualMag.coerceAtLeast(1f)
                    val speed = 7.2f * dt
                    val sinYaw = sin(localCamYaw)
                    val cosYaw = cos(localCamYaw)

                    val worldDx = (normF * sinYaw + normS * cosYaw) * speed
                    val worldDz = (normF * cosYaw - normS * sinYaw) * speed
                    // Enclosed hallway walls at X = +/- 3.7m; Z goes infinitely forward!
                    localPosX = (localPosX + worldDx).coerceIn(-3.15f, 3.15f)
                    localPosZ = (localPosZ + worldDz).coerceAtLeast(-2.0f)
                    localPlayerYaw = atan2(worldDx, worldDz)
                } else {
                    // Smooth auto-walk down the corridor toward a tapped Door
                    val targetDoor = autoWalkTargetDoor
                    if (targetDoor != null) {
                        val standX = if (targetDoor.side == DoorSide.LEFT_WALL) -1.9f else 1.9f
                        val standZ = targetDoor.position.z
                        val dx = standX - localPosX
                        val dz = standZ - localPosZ
                        val dist = sqrt(dx * dx + dz * dz)
                        val desiredYaw = atan2(targetDoor.position.x - localPosX, targetDoor.position.z - localPosZ)
                        localPlayerYaw = desiredYaw
                        localCamYaw += (0f - localCamYaw) * (3.5f * dt).coerceIn(0f, 1f)

                        if (dist > 0.45f) {
                            val step = (9.5f * dt).coerceAtMost(dist)
                            localPosX += (dx / dist) * step
                            localPosZ += (dz / dist) * step
                        } else {
                            autoWalkTargetDoor = null
                            lastAutoOpenedDoorId = targetDoor.id
                            currentOnUpdatePosition(Vec3(localPosX, 0f, localPosZ), localPlayerYaw)
                            currentOnOpenDoor(targetDoor)
                        }
                    }
                }

                // Periodically notify ViewModel as player advances down the infinite corridor so new doors spawn ahead
                if (abs(localPosZ - lastSyncedZ) >= 2.5f) {
                    lastSyncedZ = localPosZ
                    currentOnUpdatePosition(Vec3(localPosX, 0f, localPosZ), localPlayerYaw)
                }

                // Check proximity to corridor doors
                val posVec = Vec3(localPosX, 0f, localPosZ)
                var closestDoor: CorridorDoor3D? = null
                var closestDist = Float.MAX_VALUE
                for (d in currentDoors) {
                    val dist = d.position.distanceXZ(posVec)
                    if (dist < closestDist) {
                        closestDist = dist
                        closestDoor = d
                    }
                }

                val nextApproached = if (closestDist < 4.6f) closestDoor else null
                if (approachedDoor?.id != nextApproached?.id) {
                    approachedDoor = nextApproached
                }

                // Reset auto-open latch once player walks away from that door
                if (lastAutoOpenedDoorId != null && (closestDoor?.id != lastAutoOpenedDoorId || closestDist > 2.4f)) {
                    lastAutoOpenedDoorId = null
                }

                // If player physically walks right into a door threshold on the wall (< 1.35m), pop open its Decision Menu!
                if (closestDoor != null && closestDist < 1.35f && lastAutoOpenedDoorId != closestDoor.id) {
                    lastAutoOpenedDoorId = closestDoor.id
                    autoWalkTargetDoor = null
                    // Step slightly back from the wall so the player stays comfortably in the corridor when they close the menu
                    localPosX = if (closestDoor.side == DoorSide.LEFT_WALL) -1.8f else 1.8f
                    currentOnUpdatePosition(Vec3(localPosX, 0f, localPosZ), localPlayerYaw)
                    currentOnOpenDoor(closestDoor)
                }
            }
        }
    }

    fun project3D(
        ptX: Float,
        ptY: Float,
        ptZ: Float,
        camX: Float,
        camY: Float,
        camZ: Float,
        camYaw: Float,
        camPitch: Float,
        width: Float,
        height: Float
    ): ProjectedPoint {
        val rx = ptX - camX
        val ry = ptY - camY
        val rz = ptZ - camZ

        val cosY = cos(-camYaw)
        val sinY = sin(-camYaw)
        val cx = rx * cosY + rz * sinY
        val cz0 = -rx * sinY + rz * cosY

        val cosP = cos(camPitch)
        val sinP = sin(camPitch)
        val cy = ry * cosP - cz0 * sinP
        val cz = ry * sinP + cz0 * cosP

        if (cz <= 0.25f) {
            return ProjectedPoint(0f, 0f, cz, 0f, false)
        }
        val focalLen = if (width < height) width * 0.92f else height * 0.80f
        val fovScale = focalLen / cz
        val sx = width * 0.5f + cx * fovScale
        val sy = height * 0.48f - cy * fovScale
        return ProjectedPoint(sx, sy, cz, fovScale, true)
    }

    fun handleDoorTapOrButton(door: CorridorDoor3D) {
        val dist = door.position.distanceXZ(Vec3(localPosX, 0f, localPosZ))
        if (dist < 4.2f) {
            lastAutoOpenedDoorId = door.id
            currentOnUpdatePosition(Vec3(localPosX, 0f, localPosZ), localPlayerYaw)
            onOpenCorridorDoor(door)
        } else {
            autoWalkTargetDoor = door
        }
    }

    // Visible doors near or ahead of the player for the right-side quick list (4 if Living Atlas 50% Perk unlocked!)
    val maxRadarCount = if (hasAtlasRadarPerk) 4 else 3
    val nearbyDoorsForHud = remember(corridorDoors, approachedDoor, localPosZ.roundToInt() / 4, maxRadarCount) {
        corridorDoors
            .filter { it.position.z >= localPosZ - 5f }
            .sortedBy { it.position.z }
            .take(maxRadarCount)
    }

    val hasActiveNearbyCrisis = remember(corridorDoors, localPosZ.roundToInt() / 4) {
        corridorDoors.any {
            it.archetype == DoorArchetype.TICKING_CRISIS &&
                it.status != DoorStatus.RESOLVED &&
                it.position.z in (localPosZ - 4f)..(localPosZ + 22f)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { canvasSize = it }
            .focusRequester(focusRequester)
            .focusable()
            .onPreviewKeyEvent { event ->
                val isDown = event.type == KeyEventType.KeyDown
                when (event.key) {
                    Key.W, Key.DirectionUp -> {
                        keyW = isDown
                        true
                    }
                    Key.S, Key.DirectionDown -> {
                        keyS = isDown
                        true
                    }
                    Key.A, Key.DirectionLeft -> {
                        keyA = isDown
                        true
                    }
                    Key.D, Key.DirectionRight -> {
                        keyD = isDown
                        true
                    }
                    Key.Q -> {
                        keyRotLeft = isDown
                        true
                    }
                    Key.E -> {
                        keyRotRight = isDown
                        true
                    }
                    else -> false
                }
            }
            .pointerInput(corridorDoors, canvasSize) {
                detectTapGestures { tapOffset ->
                    val w = size.width.toFloat()
                    val h = size.height.toFloat()
                    if (w > 0f && h > 0f) {
                        val camDist = 5.8f
                        val camHeight = (2.4f + sin(localCamPitch) * 1.1f).coerceIn(1.4f, 3.5f)
                        val camX = (localPosX - sin(localCamYaw) * camDist).coerceIn(-3.3f, 3.3f)
                        val camZ = localPosZ - cos(localCamYaw) * camDist

                        val hitDoor = corridorDoors
                            .filter { it.position.z in (localPosZ - 6f)..(localPosZ + 38f) }
                            .firstOrNull { door ->
                                val centerProj = project3D(
                                    door.position.x, 1.6f, door.position.z,
                                    camX, camHeight, camZ, localCamYaw, localCamPitch, w, h
                                )
                                if (!centerProj.visible) {
                                    false
                                } else {
                                    val hitRadius = (1.7f * centerProj.scale).coerceIn(48f, 165f)
                                    val dx = tapOffset.x - centerProj.screenX
                                    val dy = tapOffset.y - centerProj.screenY
                                    (dx * dx + dy * dy) <= hitRadius * hitRadius
                                }
                            }
                        if (hitDoor != null) {
                            handleDoorTapOrButton(hitDoor)
                        }
                    }
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    var nextYaw = localCamYaw + dragAmount.x * 0.0055f
                    while (nextYaw > PI.toFloat()) nextYaw -= (2.0 * PI).toFloat()
                    while (nextYaw < -PI.toFloat()) nextYaw += (2.0 * PI).toFloat()
                    localCamYaw = nextYaw
                    localCamPitch = (localCamPitch + dragAmount.y * 0.0035f).coerceIn(0.06f, 0.55f)
                    onRotateCamera(dragAmount.x * 0.0055f, dragAmount.y * 0.0035f)
                }
            }
            .testTag("world_3d_viewport")
    ) {
        // 1. 60fps 3D Enclosed Infinite Hallway & Interactive Wall Doors Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val time = animTime
            val px = localPosX
            val pz = localPosZ
            val pYaw = localPlayerYaw
            val cYaw = localCamYaw
            val cPitch = localCamPitch

            val camDist = 5.8f
            // Natural 3D walking camera head-bob when player is moving down the corridor
            val headBobY = if (isMovingNow) sin(time * 9.5f) * 0.075f else sin(time * 1.8f) * 0.015f
            val headBobX = if (isMovingNow) cos(time * 4.75f) * 0.045f else 0f
            val camHeight = (2.4f + sin(cPitch) * 1.1f + headBobY).coerceIn(1.35f, 3.55f)
            val camX = (px - sin(cYaw) * camDist + headBobX).coerceIn(-3.3f, 3.3f)
            val camZ = pz - cos(cYaw) * camDist

            fun proj(vx: Float, vy: Float, vz: Float): ProjectedPoint =
                project3D(vx, vy, vz, camX, camHeight, camZ, cYaw, cPitch, w, h)

            val horizonY = (h * 0.46f + sin(cPitch) * h * 0.26f).coerceIn(h * 0.20f, h * 0.72f)

            // Enclosed Hallway Ceiling / Skylight Backdrop (Reacts to Vex Override & Crisis Alert)
            val crisisPulse = if (hasActiveNearbyCrisis) (0.5f + 0.5f * sin(time * 5.5f)) else 0f
            val ceilingTop = when {
                activeCharacter.isVexInverted -> Color(0xFF280824)
                hasActiveNearbyCrisis -> Color(0xFF240914)
                else -> Color(biome.skyTopColor)
            }
            val ceilingHorizon = when {
                activeCharacter.isVexInverted -> Color(0xFF4B0F41)
                hasActiveNearbyCrisis -> Color(0xFF441022)
                else -> Color(biome.skyHorizonColor)
            }
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(ceilingTop, ceilingHorizon, ObsidianNight),
                    startY = 0f,
                    endY = horizonY
                ),
                size = Size(w, horizonY)
            )

            // Twin Suns Streaming Through Overhead Corridor Skylight
            val redSunX = w * 0.42f - sin(cYaw) * w * 0.25f
            drawCircle(
                color = RedGiantFlare.copy(alpha = 0.18f + 0.08f * crisisPulse),
                center = Offset(redSunX, horizonY * 0.35f),
                radius = w * 0.18f
            )
            val blueSunX = w * 0.58f - sin(cYaw) * w * 0.25f
            drawCircle(
                color = BlueDwarfGlow.copy(alpha = 0.22f),
                center = Offset(blueSunX, horizonY * 0.28f),
                radius = w * 0.13f
            )

            // Enclosed Corridor Floor Backdrop
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(ObsidianNight, Color(biome.groundColor)),
                    startY = horizonY,
                    endY = h
                ),
                topLeft = Offset(0f, horizonY),
                size = Size(w, h - horizonY)
            )

            // 3D Enclosed Hallway Geometry Window around Player's current Z (Infinite Scroll!)
            val hallHalfW = InfiniteCorridorGenerator.CORRIDOR_HALF_WIDTH
            val hallCeilingY = 3.9f
            val windowStartZ = (floor((pz - 4f) / 4f) * 4f).coerceAtLeast(-4f)
            val windowEndZ = pz + 40f
            val structColor = when {
                activeCharacter.isVexInverted -> VexCrimson
                hasSiblingSynergyBuff -> BrassGold
                else -> Color(biome.gridColor)
            }

            // Longitudinal Hallway Corner Beams, Wall Conduits, Skylight Rails & Center Floor Runner
            val zNear = (pz - 3f).coerceAtLeast(-3f)
            val zFar = pz + 40f

            fun drawLongitudinalLine(x: Float, y: Float, color: Color, widthPx: Float) {
                val p1 = proj(x, y, zNear)
                val p2 = proj(x, y, zFar)
                if (p1.visible && p2.visible) {
                    drawLine(
                        color = color,
                        start = Offset(p1.screenX, p1.screenY),
                        end = Offset(p2.screenX, p2.screenY),
                        strokeWidth = widthPx
                    )
                }
            }

            // Left & Right Wall Baseboards and Ceiling Cornices
            drawLongitudinalLine(-hallHalfW, 0f, BrassGold.copy(alpha = 0.55f), 2.5f)
            drawLongitudinalLine(hallHalfW, 0f, BrassGold.copy(alpha = 0.55f), 2.5f)
            drawLongitudinalLine(-hallHalfW, hallCeilingY, structColor.copy(alpha = 0.45f), 2.2f)
            drawLongitudinalLine(hallHalfW, hallCeilingY, structColor.copy(alpha = 0.45f), 2.2f)

            // Glowing Wall Telemetry Conduits (Pulse Crimson during Crisis!)
            val conduitColor = if (hasActiveNearbyCrisis) {
                VexCrimson.copy(alpha = 0.35f + 0.35f * crisisPulse)
            } else {
                structColor.copy(alpha = 0.28f)
            }
            drawLongitudinalLine(-hallHalfW, 1.45f, conduitColor, 1.8f)
            drawLongitudinalLine(hallHalfW, 1.45f, conduitColor, 1.8f)

            // Overhead Skylight Ribs
            drawLongitudinalLine(-1.3f, hallCeilingY, BrassGold.copy(alpha = 0.30f), 1.5f)
            drawLongitudinalLine(1.3f, hallCeilingY, BrassGold.copy(alpha = 0.30f), 1.5f)

            // Glowing Center Floor Runner Strips
            drawLongitudinalLine(-0.85f, 0.02f, structColor.copy(alpha = 0.35f), 2.0f)
            drawLongitudinalLine(0.85f, 0.02f, structColor.copy(alpha = 0.35f), 2.0f)

            // Structural Hallway Bulkhead Ribs every 4 meters (Floor -> Left Wall -> Ceiling -> Right Wall)
            var ribZ = windowStartZ
            while (ribZ <= windowEndZ) {
                val fl = proj(-hallHalfW, 0f, ribZ)
                val fr = proj(hallHalfW, 0f, ribZ)
                val cl = proj(-hallHalfW, hallCeilingY, ribZ)
                val cr = proj(hallHalfW, hallCeilingY, ribZ)

                if (fl.visible && fr.visible && cl.visible && cr.visible) {
                    val depthFade = (1f - (fl.depthZ / 42f)).coerceIn(0.08f, 0.42f)
                    val ribStroke = (0.09f * fl.scale).coerceIn(1.2f, 5.5f)

                    // Left Wall Structural Pillar
                    drawLine(
                        color = BrassGold.copy(alpha = depthFade),
                        start = Offset(fl.screenX, fl.screenY),
                        end = Offset(cl.screenX, cl.screenY),
                        strokeWidth = ribStroke
                    )
                    // Right Wall Structural Pillar
                    drawLine(
                        color = BrassGold.copy(alpha = depthFade),
                        start = Offset(fr.screenX, fr.screenY),
                        end = Offset(cr.screenX, cr.screenY),
                        strokeWidth = ribStroke
                    )
                    // Overhead Vaulted Ceiling Beam
                    drawLine(
                        color = structColor.copy(alpha = depthFade * 0.9f),
                        start = Offset(cl.screenX, cl.screenY),
                        end = Offset(cr.screenX, cr.screenY),
                        strokeWidth = ribStroke
                    )
                    // Floor Tile Joint
                    drawLine(
                        color = structColor.copy(alpha = depthFade * 0.6f),
                        start = Offset(fl.screenX, fl.screenY),
                        end = Offset(fr.screenX, fr.screenY),
                        strokeWidth = 1.2f
                    )
                }
                ribZ += 4f
            }

            // Dynamic 3D Bioluminescent Spores / Cymatic Sparks Drifting in the Hallway
            val particleColor = when {
                hasActiveNearbyCrisis -> VexCrimson
                hasSiblingSynergyBuff -> BrassGold
                colonyMorale >= 80 -> LuciferinCyan
                else -> Color(biome.accentColor)
            }
            for (i in 0 until 14) {
                val seedZ = windowStartZ + ((i * 2.9f + time * 1.2f) % 36f)
                val seedX = sin(i * 1.7f + time * 0.9f) * 2.8f
                val seedY = 0.6f + abs(cos(i * 2.3f + time * 0.7f)) * 2.6f
                val sp = proj(seedX, seedY, seedZ)
                if (sp.visible) {
                    val r = (0.055f * sp.scale).coerceIn(1.5f, 5.5f)
                    val alpha = (1f - (sp.depthZ / 38f)).coerceIn(0.15f, 0.70f)
                    drawCircle(
                        color = particleColor.copy(alpha = alpha),
                        radius = r,
                        center = Offset(sp.screenX, sp.screenY)
                    )
                }
            }

            // Depth-sort visible Corridor Doors and the Player Avatar
            val playerPosVec = Vec3(px, 0f, pz)
            val playerDepth = proj(px, 0f, pz).depthZ
            val visibleDoors = corridorDoors
                .filter { it.position.z in (pz - 5f)..(pz + 38f) }
                .sortedByDescending { proj(it.position.x, 0f, it.position.z).depthZ }

            var playerDrawn = false
            for (door in visibleDoors) {
                val doorDepth = proj(door.position.x, 0f, door.position.z).depthZ
                if (!playerDrawn && playerDepth > doorDepth) {
                    drawPlayerAvatar3D(
                        px = px,
                        pz = pz,
                        playerYaw = pYaw,
                        activeCharacter = activeCharacter,
                        hasSiblingSynergyBuff = hasSiblingSynergyBuff,
                        animTime = time,
                        isMoving = isMovingNow,
                        proj = ::proj
                    )
                    playerDrawn = true
                }
                drawCorridorDoor3DOnWall(
                    door = door,
                    playerPos = playerPosVec,
                    animTime = time,
                    reusablePath = reusablePath,
                    titlePaint = titlePaint,
                    subtitlePaint = subtitlePaint,
                    canvasWidth = w,
                    proj = ::proj
                )
            }
            if (!playerDrawn) {
                drawPlayerAvatar3D(
                    px = px,
                    pz = pz,
                    playerYaw = pYaw,
                    activeCharacter = activeCharacter,
                    hasSiblingSynergyBuff = hasSiblingSynergyBuff,
                    animTime = time,
                    isMoving = isMovingNow,
                    proj = ::proj
                )
            }

            // Celebratory Breakthrough Cymatic Wave Burst in 3D Viewport!
            if (breakthroughBurstTimer > 0f) {
                val progress = 1f - (breakthroughBurstTimer / 2.4f).coerceIn(0f, 1f)
                val centerP = proj(px, 1.3f, pz)
                if (centerP.visible) {
                    val maxRadius = w.coerceAtLeast(h) * 0.55f
                    for (ringIdx in 0..2) {
                        val ringProg = (progress + ringIdx * 0.16f).coerceIn(0f, 1f)
                        val r = 24f + ringProg * maxRadius
                        val alpha = (1f - ringProg) * 0.65f
                        drawCircle(
                            color = if (ringIdx % 2 == 0) LuciferinCyan.copy(alpha = alpha) else BrassGold.copy(alpha = alpha),
                            radius = r,
                            center = Offset(centerP.screenX, centerP.screenY),
                            style = Stroke(width = (6f * (1f - ringProg)).coerceAtLeast(1.5f))
                        )
                    }
                }
            }

            // Subtle Pulsing Crimson Screen-Edge Vignette when an unresolved Crisis Bulkhead is nearby
            if (hasActiveNearbyCrisis) {
                val edgeAlpha = 0.12f + 0.16f * crisisPulse
                drawRect(
                    color = VexCrimson.copy(alpha = edgeAlpha),
                    topLeft = Offset.Zero,
                    size = Size(w, h),
                    style = Stroke(width = 10f)
                )
            }
        }

        // 2. Proximity Door Threshold Card (When standing near a Door in the corridor)
        val nearDoor = approachedDoor
        if (nearDoor != null) {
            val doorColor = when {
                nearDoor.status == DoorStatus.RESOLVED -> LuciferinCyan
                nearDoor.archetype != DoorArchetype.STANDARD_LAB -> Color(nearDoor.archetype.colorHex)
                nearDoor.status == DoorStatus.INSPECTED -> SimulacrumViolet
                else -> Color(nearDoor.squadAlignment.primaryColorHex)
            }
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 188.dp, start = 12.dp, end = 12.dp)
                    .widthIn(max = 430.dp)
                    .fillMaxWidth()
                    .border(1.5.dp, doorColor, RoundedCornerShape(16.dp))
                    .testTag("proximity_gate_card"),
                shape = RoundedCornerShape(16.dp),
                color = ObsidianNight.copy(alpha = 0.95f)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val sideText = if (nearDoor.side == DoorSide.LEFT_WALL) "LEFT WALL" else "RIGHT WALL"
                        Text(
                            text = "${nearDoor.archetype.badgeLabel} • $sideText",
                            style = MaterialTheme.typography.labelSmall,
                            color = doorColor,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = when {
                                nearDoor.status == DoorStatus.RESOLVED -> "RESOLVED ✓"
                                nearDoor.archetype == DoorArchetype.TICKING_CRISIS -> "⚠ ${nearDoor.crisisTurnsRemaining ?: 2}t FUSE"
                                nearDoor.archetype == DoorArchetype.COGNITIVE_VAULT -> "🔒 [${nearDoor.requiredCognitiveFunction ?: "Ti"}]"
                                else -> "${nearDoor.options.size} CHOICES"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = LuciferinCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = nearDoor.chamberTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = nearDoor.chamberSubtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Button(
                        onClick = { onOpenCorridorDoor(nearDoor) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = doorColor,
                            contentColor = ObsidianNight
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("enter_choice_gate_button")
                    ) {
                        Icon(
                            imageVector = when {
                                nearDoor.status == DoorStatus.RESOLVED -> Icons.Default.CheckCircle
                                nearDoor.archetype == DoorArchetype.COGNITIVE_VAULT -> Icons.Default.Lock
                                nearDoor.archetype == DoorArchetype.TICKING_CRISIS -> Icons.Default.Warning
                                else -> Icons.Default.MeetingRoom
                            },
                            contentDescription = "Open Chamber Door",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when {
                                nearDoor.status == DoorStatus.RESOLVED -> "Review Dramatic Aftermath Log"
                                nearDoor.archetype == DoorArchetype.COGNITIVE_VAULT -> "Inspect Vault Lock & ${nearDoor.options.size} Choices"
                                nearDoor.archetype == DoorArchetype.TICKING_CRISIS -> "Defuse Crisis Bulkhead (${nearDoor.options.size} Choices)"
                                nearDoor.archetype == DoorArchetype.SANCTUARY_LOUNGE -> "Enter Sanctuary Alcove (-25% Stress)"
                                else -> "Open Door & Inspect ${nearDoor.options.size} Choices"
                            },
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 3. Mobile Thumb-Zone Controls (Left: 360° Stick + Center Cam; Right: Nearby Corridor Doors Radar)
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, bottom = 72.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            // Left Thumb Zone: Corridor Depth Badge + Center Cam + 360° Virtual Thumbstick
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    onClick = {
                        localCamYaw = 0f
                        localCamPitch = 0.22f
                        onResetCameraBehindPlayer()
                    },
                    color = ConsoleGlass.copy(alpha = 0.90f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .border(1.dp, LuciferinCyan.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .testTag("reset_camera_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CenterFocusStrong,
                            contentDescription = "Center 3D Camera Down Hallway",
                            tint = LuciferinCyan,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "Hall Z: ${localPosZ.roundToInt()}m",
                            style = MaterialTheme.typography.labelSmall,
                            color = LuciferinCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                VirtualThumbstick(
                    stickX = stickX,
                    stickY = stickY,
                    onStickChange = { x, y ->
                        stickX = x
                        stickY = y
                    }
                )
            }

            // Right Thumb Zone: Nearby Doors Ahead in the Infinite Corridor
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    color = ConsoleGlass.copy(alpha = 0.88f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (hasAtlasRadarPerk) {
                            "ATLAS 4-BAY RADAR (TAP TO OPEN)"
                        } else {
                            "CORRIDOR DOORS AHEAD (TAP TO OPEN)"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = BrassGold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                nearbyDoorsForHud.forEachIndexed { idx, door ->
                    val dColor = when {
                        door.status == DoorStatus.RESOLVED -> LuciferinCyan
                        door.archetype != DoorArchetype.STANDARD_LAB -> Color(door.archetype.colorHex)
                        door.status == DoorStatus.INSPECTED -> SimulacrumViolet
                        else -> Color(door.squadAlignment.primaryColorHex)
                    }
                    val isTargeted = autoWalkTargetDoor?.id == door.id || approachedDoor?.id == door.id
                    val sideTag = if (door.side == DoorSide.LEFT_WALL) "⬅" else "➡"
                    Surface(
                        modifier = Modifier
                            .widthIn(min = 158.dp, max = 220.dp)
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = if (isTargeted) 2.dp else 1.2.dp,
                                color = dColor,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { handleDoorTapOrButton(door) }
                            .testTag("quick_walk_gate_$idx"),
                        color = if (isTargeted) dColor.copy(alpha = 0.24f) else ObsidianNight.copy(alpha = 0.92f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = when {
                                    door.status == DoorStatus.RESOLVED -> Icons.Default.CheckCircle
                                    door.archetype == DoorArchetype.COGNITIVE_VAULT -> Icons.Default.Lock
                                    door.archetype == DoorArchetype.TICKING_CRISIS -> Icons.Default.Warning
                                    else -> Icons.Default.MeetingRoom
                                },
                                contentDescription = "Open ${door.chamberTitle}",
                                tint = dColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "$sideTag ${door.chamberTitle}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = when {
                                        door.status == DoorStatus.RESOLVED -> "Resolved ✓ • Tap for Aftermath"
                                        door.archetype == DoorArchetype.TICKING_CRISIS -> "⚠ Crisis (${door.crisisTurnsRemaining ?: 2}t) • ${(door.position.z - localPosZ).roundToInt().coerceAtLeast(0)}m"
                                        door.archetype == DoorArchetype.COGNITIVE_VAULT -> "🔒 Vault [${door.requiredCognitiveFunction ?: "Ti"}] • ${(door.position.z - localPosZ).roundToInt().coerceAtLeast(0)}m"
                                        door.archetype == DoorArchetype.SANCTUARY_LOUNGE -> "🍵 Sanctuary (-25% Stress) • ${(door.position.z - localPosZ).roundToInt().coerceAtLeast(0)}m"
                                        isTargeted -> "Tap to Open Door (${door.options.size} Choices) →"
                                        else -> "${door.options.size} Choices • ${(door.position.z - localPosZ).roundToInt().coerceAtLeast(0)}m"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = dColor,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
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
private fun VirtualThumbstick(
    stickX: Float,
    stickY: Float,
    onStickChange: (Float, Float) -> Unit
) {
    val outerRadiusDp = 46.dp
    val density = LocalDensity.current
    val maxDistPx = with(density) { 30.dp.toPx() }

    Box(
        modifier = Modifier
            .size(outerRadiusDp * 2)
            .clip(CircleShape)
            .background(ConsoleGlass.copy(alpha = 0.80f))
            .border(1.5.dp, LuciferinCyan.copy(alpha = 0.55f), CircleShape)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = { onStickChange(0f, 0f) },
                    onDragCancel = { onStickChange(0f, 0f) }
                ) { change, _ ->
                    change.consume()
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val delta = change.position - center
                    val dist = sqrt(delta.x * delta.x + delta.y * delta.y)
                    val clampedDist = dist.coerceAtMost(maxDistPx)
                    val angle = atan2(delta.y, delta.x)
                    val nx = if (dist > 1f) (cos(angle) * clampedDist) / maxDistPx else 0f
                    val ny = if (dist > 1f) (sin(angle) * clampedDist) / maxDistPx else 0f
                    onStickChange(nx, ny)
                }
            }
            .testTag("virtual_thumbstick"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            drawCircle(
                color = LuciferinCyan.copy(alpha = 0.18f),
                radius = size.minDimension * 0.34f,
                center = center,
                style = Stroke(width = 2f)
            )
            val knobCenter = Offset(
                x = center.x + stickX * maxDistPx,
                y = center.y + stickY * maxDistPx
            )
            drawCircle(
                color = LuciferinCyan,
                radius = 15.dp.toPx(),
                center = knobCenter
            )
        }
    }
}

/**
 * Renders a 3D Recessed Bulkhead Door along the Left or Right Wall of the Enclosed Corridor,
 * with distinct architectural styling for Standard Labs, Cognitive Vaults, Ticking Crisis Bulkheads,
 * and Sanctuary Lounge Alcoves.
 */
private fun DrawScope.drawCorridorDoor3DOnWall(
    door: CorridorDoor3D,
    playerPos: Vec3,
    animTime: Float,
    reusablePath: Path,
    titlePaint: Paint,
    subtitlePaint: Paint,
    canvasWidth: Float,
    proj: (Float, Float, Float) -> ProjectedPoint
) {
    val doorColor = when {
        door.status == DoorStatus.RESOLVED -> LuciferinCyan
        door.archetype != DoorArchetype.STANDARD_LAB -> Color(door.archetype.colorHex)
        door.status == DoorStatus.INSPECTED -> SimulacrumViolet
        else -> Color(door.squadAlignment.primaryColorHex)
    }

    val wallX = door.position.x
    val inwardSign = if (door.side == DoorSide.LEFT_WALL) 1f else -1f
    val dzHalf = 1.25f
    val doorH = 2.85f
    val zNear = door.position.z - dzHalf
    val zFar = door.position.z + dzHalf

    val bNear = proj(wallX, 0f, zNear)
    val bFar = proj(wallX, 0f, zFar)
    val tNear = proj(wallX, doorH, zNear)
    val tFar = proj(wallX, doorH, zFar)
    val lintelApex = proj(wallX + inwardSign * 0.25f, doorH + 0.35f, door.position.z)

    if (!bNear.visible || !bFar.visible || !tNear.visible || !tFar.visible) return

    val dist = door.position.distanceXZ(playerPos)
    val proximityBoost = (1f - (dist / 10f)).coerceIn(0f, 1f)

    // 1. Floor Welcome Threshold Mat extending into the Hallway
    val matInwardX = wallX + inwardSign * 1.15f
    val mNear = proj(matInwardX, 0.02f, zNear)
    val mFar = proj(matInwardX, 0.02f, zFar)
    if (mNear.visible && mFar.visible) {
        reusablePath.reset()
        reusablePath.moveTo(bNear.screenX, bNear.screenY)
        reusablePath.lineTo(mNear.screenX, mNear.screenY)
        reusablePath.lineTo(mFar.screenX, mFar.screenY)
        reusablePath.lineTo(bFar.screenX, bFar.screenY)
        reusablePath.close()

        drawPath(
            path = reusablePath,
            color = doorColor.copy(alpha = 0.18f + 0.28f * proximityBoost)
        )
        drawPath(
            path = reusablePath,
            color = doorColor.copy(alpha = 0.65f),
            style = Stroke(width = 2f)
        )
    }

    // 2. Interior Glowing Chamber Backdrop
    reusablePath.reset()
    reusablePath.moveTo(bNear.screenX, bNear.screenY)
    reusablePath.lineTo(tNear.screenX, tNear.screenY)
    reusablePath.lineTo(tFar.screenX, tFar.screenY)
    reusablePath.lineTo(bFar.screenX, bFar.screenY)
    reusablePath.close()

    val pulseSpeed = if (door.archetype == DoorArchetype.TICKING_CRISIS) 6.5f else 3.0f
    val interiorGlowAlpha = (0.35f + 0.35f * proximityBoost + 0.10f * sin(animTime * pulseSpeed)).coerceIn(0.25f, 0.86f)
    drawPath(
        path = reusablePath,
        color = doorColor.copy(alpha = interiorGlowAlpha)
    )

    // 3. Sliding Blast-Door Panels
    val openFraction = when {
        door.status != DoorStatus.UNEXPLORED -> 0.78f
        door.archetype == DoorArchetype.COGNITIVE_VAULT && !door.isVaultUnlockedByOverride -> 0.12f
        dist < 5.5f -> ((5.5f - dist) / 3.5f).coerceIn(0.1f, 0.85f)
        else -> 0.08f
    }
    val closedLeafSpanZ = dzHalf * (1f - openFraction)
    if (closedLeafSpanZ > 0.05f) {
        // Near door leaf
        val leaf1EndZ = zNear + closedLeafSpanZ
        val l1B = proj(wallX, 0f, leaf1EndZ)
        val l1T = proj(wallX, doorH, leaf1EndZ)
        if (l1B.visible && l1T.visible) {
            reusablePath.reset()
            reusablePath.moveTo(bNear.screenX, bNear.screenY)
            reusablePath.lineTo(tNear.screenX, tNear.screenY)
            reusablePath.lineTo(l1T.screenX, l1T.screenY)
            reusablePath.lineTo(l1B.screenX, l1B.screenY)
            reusablePath.close()
            drawPath(path = reusablePath, color = ObsidianNight.copy(alpha = 0.90f))
            drawPath(path = reusablePath, color = BrassGold, style = Stroke(width = 2f))
        }

        // Far door leaf
        val leaf2StartZ = zFar - closedLeafSpanZ
        val l2B = proj(wallX, 0f, leaf2StartZ)
        val l2T = proj(wallX, doorH, leaf2StartZ)
        if (l2B.visible && l2T.visible) {
            reusablePath.reset()
            reusablePath.moveTo(l2B.screenX, l2B.screenY)
            reusablePath.lineTo(l2T.screenX, l2T.screenY)
            reusablePath.lineTo(tFar.screenX, tFar.screenY)
            reusablePath.lineTo(bFar.screenX, bFar.screenY)
            reusablePath.close()
            drawPath(path = reusablePath, color = ObsidianNight.copy(alpha = 0.90f))
            drawPath(path = reusablePath, color = BrassGold, style = Stroke(width = 2f))
        }
    }

    // Archetype-Specific 3D Door Detail: Cognitive Vault X-Bracing or Crisis Strobe Beacon
    if (door.archetype == DoorArchetype.COGNITIVE_VAULT && door.status != DoorStatus.RESOLVED) {
        drawLine(
            color = SimulacrumViolet.copy(alpha = 0.85f),
            start = Offset(bNear.screenX, bNear.screenY),
            end = Offset(tFar.screenX, tFar.screenY),
            strokeWidth = 3f
        )
        drawLine(
            color = SimulacrumViolet.copy(alpha = 0.85f),
            start = Offset(tNear.screenX, tNear.screenY),
            end = Offset(bFar.screenX, bFar.screenY),
            strokeWidth = 3f
        )
    } else if (door.archetype == DoorArchetype.TICKING_CRISIS && door.status != DoorStatus.RESOLVED && lintelApex.visible) {
        val strobeAlpha = (0.5f + 0.5f * sin(animTime * 8f)).coerceIn(0.2f, 1f)
        drawCircle(
            color = VexCrimson.copy(alpha = strobeAlpha),
            radius = (0.28f * lintelApex.scale).coerceIn(6f, 22f),
            center = Offset(lintelApex.screenX, lintelApex.screenY)
        )
    }

    // 4. Heavy Brass & Neon 3D Doorframe Surround
    reusablePath.reset()
    reusablePath.moveTo(bNear.screenX, bNear.screenY)
    reusablePath.lineTo(tNear.screenX, tNear.screenY)
    reusablePath.lineTo(tFar.screenX, tFar.screenY)
    reusablePath.lineTo(bFar.screenX, bFar.screenY)
    reusablePath.close()

    val frameStroke = (0.18f * bNear.scale).coerceIn(3f, 12f)
    drawPath(
        path = reusablePath,
        color = doorColor,
        style = Stroke(width = frameStroke, cap = StrokeCap.Square)
    )

    // 5. Floating 3D Holographic Bay Plaque projecting into the Hallway
    if (lintelApex.visible) {
        val plaqueW = 136.dp.toPx()
        val plaqueH = 38.dp.toPx()
        val plaqueCenterX = lintelApex.screenX.coerceIn(plaqueW * 0.55f, canvasWidth - plaqueW * 0.55f)
        val plaqueTopY = (lintelApex.screenY - plaqueH - 4.dp.toPx()).coerceAtLeast(94.dp.toPx())

        drawRoundRect(
            color = ObsidianNight.copy(alpha = 0.92f),
            topLeft = Offset(plaqueCenterX - plaqueW * 0.5f, plaqueTopY),
            size = Size(plaqueW, plaqueH),
            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
        )
        drawRoundRect(
            color = doorColor,
            topLeft = Offset(plaqueCenterX - plaqueW * 0.5f, plaqueTopY),
            size = Size(plaqueW, plaqueH),
            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
            style = Stroke(width = 1.5.dp.toPx())
        )

        subtitlePaint.color = doorColor.toArgb()
        val statusBadge = when {
            door.status == DoorStatus.RESOLVED -> "RESOLVED ✓"
            door.archetype == DoorArchetype.COGNITIVE_VAULT -> "🔒 VAULT [${door.requiredCognitiveFunction ?: "Ti"}] • ${dist.roundToInt()}m"
            door.archetype == DoorArchetype.TICKING_CRISIS -> "⚠ CRISIS (${door.crisisTurnsRemaining ?: 2}t) • ${dist.roundToInt()}m"
            door.archetype == DoorArchetype.SANCTUARY_LOUNGE -> "🍵 SANCTUARY • ${dist.roundToInt()}m"
            door.status == DoorStatus.INSPECTED -> "OPEN • ${door.options.size} CHOICES"
            else -> "${door.options.size} CHOICES • ${dist.roundToInt()}m"
        }
        val shortTitle = if (door.chamberTitle.length > 19) door.chamberTitle.take(18) + "…" else door.chamberTitle

        drawContext.canvas.nativeCanvas.apply {
            drawText(
                statusBadge,
                plaqueCenterX,
                plaqueTopY + 13.dp.toPx(),
                subtitlePaint
            )
            drawText(
                shortTitle,
                plaqueCenterX,
                plaqueTopY + 29.dp.toPx(),
                titlePaint
            )
        }
    }
}

private fun DrawScope.drawPlayerAvatar3D(
    px: Float,
    pz: Float,
    playerYaw: Float,
    activeCharacter: EnclaveCharacter,
    hasSiblingSynergyBuff: Boolean,
    animTime: Float,
    isMoving: Boolean,
    proj: (Float, Float, Float) -> ProjectedPoint
) {
    val bobOffset = if (isMoving) abs(sin(animTime * 9f)) * 0.12f else 0f
    val feetProj = proj(px, 0f, pz)
    val torsoProj = proj(px, 1.05f + bobOffset, pz)
    val headProj = proj(px, 1.72f + bobOffset, pz)

    if (!feetProj.visible || !torsoProj.visible || !headProj.visible) return

    val squadColor = if (activeCharacter.isVexInverted) {
        VexCrimson
    } else {
        Color(activeCharacter.squad.primaryColorHex)
    }
    val scale = torsoProj.scale

    // 1. Ground Shadow & Magnetic Grounding Halo (Plus Golden Cymatic Ring when Sibling Synergy is active!)
    val shadowW = (0.95f * feetProj.scale).coerceIn(16f, 76f)
    val shadowH = shadowW * 0.35f
    drawOval(
        color = squadColor.copy(alpha = 0.45f),
        topLeft = Offset(feetProj.screenX - shadowW * 0.5f, feetProj.screenY - shadowH * 0.5f),
        size = Size(shadowW, shadowH),
        style = Stroke(width = 2.5f)
    )
    if (hasSiblingSynergyBuff) {
        val synW = shadowW * 1.32f
        val synH = shadowH * 1.32f
        drawOval(
            color = BrassGold.copy(alpha = 0.65f),
            topLeft = Offset(feetProj.screenX - synW * 0.5f, feetProj.screenY - synH * 0.5f),
            size = Size(synW, synH),
            style = Stroke(width = 2f)
        )
    }

    // Facing direction indicator arrow on corridor floor
    val forwardTip = proj(
        px + sin(playerYaw) * 1.3f,
        0.06f,
        pz + cos(playerYaw) * 1.3f
    )
    if (forwardTip.visible) {
        drawLine(
            color = squadColor,
            start = Offset(feetProj.screenX, feetProj.screenY),
            end = Offset(forwardTip.screenX, forwardTip.screenY),
            strokeWidth = 3.2f,
            cap = StrokeCap.Round
        )
    }

    // 2. Legs (Animated stride when walking down the corridor)
    val stride = if (isMoving) sin(animTime * 9f) * 0.16f * feetProj.scale else 0f
    val hipY = (feetProj.screenY + torsoProj.screenY) * 0.52f
    val legSpread = (0.18f * scale).coerceIn(4f, 15f)
    drawLine(
        color = BrassGold,
        start = Offset(torsoProj.screenX - legSpread, hipY),
        end = Offset(feetProj.screenX - legSpread - stride, feetProj.screenY),
        strokeWidth = (0.14f * scale).coerceIn(3f, 10f),
        cap = StrokeCap.Round
    )
    drawLine(
        color = BrassGold,
        start = Offset(torsoProj.screenX + legSpread, hipY),
        end = Offset(feetProj.screenX + legSpread + stride, feetProj.screenY),
        strokeWidth = (0.14f * scale).coerceIn(3f, 10f),
        cap = StrokeCap.Round
    )

    // 3. Squad Explorer Cloak & Steampunk Brass Field Pack
    val torsoW = (0.66f * scale).coerceIn(14f, 52f)
    val torsoH = abs(hipY - headProj.screenY).coerceAtLeast(16f)
    drawRoundRect(
        color = squadColor.copy(alpha = 0.88f),
        topLeft = Offset(torsoProj.screenX - torsoW * 0.5f, headProj.screenY + torsoW * 0.25f),
        size = Size(torsoW, torsoH),
        cornerRadius = CornerRadius(8f, 8f)
    )
    drawRoundRect(
        color = BrassGold,
        topLeft = Offset(torsoProj.screenX - torsoW * 0.5f, headProj.screenY + torsoW * 0.25f),
        size = Size(torsoW, torsoH),
        cornerRadius = CornerRadius(8f, 8f),
        style = Stroke(width = 2.2f)
    )

    // 4. Head & Bioluminescent Visor Crown
    val headRadius = (0.28f * headProj.scale).coerceIn(7f, 22f)
    drawCircle(
        color = ObsidianNight,
        radius = headRadius,
        center = Offset(headProj.screenX, headProj.screenY)
    )
    drawCircle(
        color = squadColor,
        radius = headRadius,
        center = Offset(headProj.screenX, headProj.screenY),
        style = Stroke(width = 2.5f)
    )
}
