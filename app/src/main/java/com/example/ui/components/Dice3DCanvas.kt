package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.DiceSkin
import com.example.ui.theme.AccentGold
import com.example.ui.theme.BrushedSilver
import com.example.ui.theme.BrushedSilverDark
import com.example.ui.theme.BrushedSilverHighlight
import com.example.ui.theme.BrushedSilverLight
import com.example.ui.theme.MetallicTeal
import com.example.ui.theme.MetallicTealBright
import com.example.ui.theme.MetallicTealDark
import com.example.ui.theme.MetallicTealGlow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

/**
 * Dice3DCanvas
 *
 * A Canvas-drawn high-fidelity 3D-effect dice.
 * Features:
 * - Physics-based multi-axis tumbling rotation during rapid roll phase.
 * - Multi-stage realistic landing physics: Impact squash, Primary rebound apex,
 *   Secondary bounce, Angular tilt wobble damping, and Dynamic ground shadow.
 * - Multi-skin support: Brushed Silver, 24K Royal Gold, and Cyber Obsidian.
 * - Specular highlights, glowing halos, and dynamic motion blur trails.
 */
@Composable
fun Dice3DCanvas(
    faceValue: Int,
    isRolling: Boolean,
    onRollRequest: () -> Unit,
    modifier: Modifier = Modifier,
    skin: DiceSkin = DiceSkin.BRUSHED_SILVER
) {
    // Rotations on Z and simulated 3D tilt
    val rollRotationZ = remember { Animatable(0f) }
    val tiltAngle = remember { Animatable(0f) }

    // Physics Bounce & Deformation properties
    val bounceOffsetY = remember { Animatable(0f) } // in px
    val squashScaleX = remember { Animatable(1f) }
    val squashScaleY = remember { Animatable(1f) }
    val popScale = remember { Animatable(1f) }

    LaunchedEffect(isRolling) {
        if (isRolling) {
            // Rapid multi-axis tumble during roll
            coroutineScope {
                launch {
                    rollRotationZ.animateTo(
                        targetValue = rollRotationZ.value + 1080f,
                        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
                    )
                }
                launch {
                    tiltAngle.animateTo(
                        targetValue = 18f,
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                    )
                    tiltAngle.animateTo(
                        targetValue = -12f,
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                    )
                }
                launch {
                    bounceOffsetY.animateTo(
                        targetValue = -25f,
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                    )
                }
                launch {
                    popScale.animateTo(0.94f, tween(150))
                    squashScaleX.animateTo(0.96f, tween(150))
                    squashScaleY.animateTo(1.04f, tween(150))
                }
            }
        } else {
            // --- Realistic Multi-Stage Physics Bounce Sequence ---
            coroutineScope {
                // 1. Primary Drop & Ground Squash Impact
                launch {
                    bounceOffsetY.animateTo(0f, tween(70, easing = FastOutLinearInEasing))
                    // Impact 1: Squash horizontally, compress vertically
                    squashScaleX.animateTo(1.18f, tween(50, easing = FastOutSlowInEasing))
                    squashScaleY.animateTo(0.82f, tween(50, easing = FastOutSlowInEasing))

                    // Rebound 1: Elastic Apex Launch
                    bounceOffsetY.animateTo(-45f, tween(140, easing = LinearOutSlowInEasing))
                    squashScaleX.animateTo(0.92f, tween(100))
                    squashScaleY.animateTo(1.12f, tween(100))

                    // Impact 2: Secondary Ground Strike
                    bounceOffsetY.animateTo(0f, tween(100, easing = FastOutLinearInEasing))
                    squashScaleX.animateTo(1.08f, tween(40))
                    squashScaleY.animateTo(0.92f, tween(40))

                    // Rebound 2: Micro-bounce
                    bounceOffsetY.animateTo(-12f, tween(80, easing = LinearOutSlowInEasing))
                    squashScaleX.animateTo(0.98f, tween(60))
                    squashScaleY.animateTo(1.03f, tween(60))

                    // Final Ground Lock & Settle
                    bounceOffsetY.animateTo(
                        0f,
                        spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                    squashScaleX.animateTo(
                        1.0f,
                        spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                    squashScaleY.animateTo(
                        1.0f,
                        spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                }

                // 2. Rotational Tilt & Angular Momentum Damping
                launch {
                    tiltAngle.animateTo(14f, tween(120, easing = FastOutSlowInEasing))
                    tiltAngle.animateTo(-7f, tween(110, easing = FastOutSlowInEasing))
                    tiltAngle.animateTo(3.5f, tween(90, easing = FastOutSlowInEasing))
                    tiltAngle.animateTo(
                        0f,
                        spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                }

                // 3. Complementary Pop Spring
                launch {
                    popScale.animateTo(
                        targetValue = 1.14f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                    popScale.animateTo(
                        targetValue = 1.0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                }
            }
        }
    }

    val glowColor = when (skin) {
        DiceSkin.BRUSHED_SILVER -> MetallicTealGlow
        DiceSkin.ROYAL_GOLD -> AccentGold
        DiceSkin.CYBER_NEON -> Color(0xFF00FFCC)
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .scale(popScale.value)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = 130.dp, color = glowColor),
                onClick = onRollRequest
            )
            .testTag("dice_3d_canvas_container"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .testTag("dice_canvas_element")
        ) {
            val canvasSize = size.minDimension
            val diceSize = canvasSize * 0.80f
            val cornerRadius = diceSize * 0.18f
            val center = Offset(size.width / 2f, size.height / 2f)

            val currentRotationZ = if (isRolling) rollRotationZ.value else 0f
            val currentTilt = tiltAngle.value
            val currentBounceY = bounceOffsetY.value * density
            val currentSquashX = squashScaleX.value
            val currentSquashY = squashScaleY.value

            // 1. Dynamic Physics-Based Ground Cast Shadow (Stretches / soft-fades as dice bounces into air)
            val airborneFraction = (-currentBounceY / (50.dp.toPx())).coerceIn(0f, 1f)
            val shadowBaseY = center.y + 14.dp.toPx() + (airborneFraction * 10.dp.toPx())
            val shadowBaseX = center.x + 8.dp.toPx()
            val shadowScale = (1f + (airborneFraction * 0.15f)) * currentSquashX
            val shadowAlpha = (0.65f - (airborneFraction * 0.35f)).coerceIn(0.2f, 0.7f)

            drawRoundRect(
                color = Color.Black.copy(alpha = shadowAlpha),
                topLeft = Offset(
                    shadowBaseX - (diceSize * shadowScale) / 2f,
                    shadowBaseY - (diceSize * (0.85f - airborneFraction * 0.2f)) / 2f
                ),
                size = Size(diceSize * shadowScale, diceSize * (0.85f - airborneFraction * 0.2f)),
                cornerRadius = CornerRadius(cornerRadius * 1.2f, cornerRadius * 1.2f)
            )

            // 2. Ambient Color Glow Under Dice
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        glowColor.copy(alpha = if (isRolling) 0.50f else (0.28f + (1f - airborneFraction) * 0.12f)),
                        Color.Transparent
                    ),
                    center = Offset(center.x, center.y + currentBounceY),
                    radius = diceSize * (0.85f + (if (isRolling) 0.1f else 0f))
                ),
                center = Offset(center.x, center.y + currentBounceY)
            )

            // 3. 3D Body & Face Transformations (Bounce + Rotations + Angular Tilt + Squash/Stretch)
            translate(top = currentBounceY) {
                rotate(degrees = currentRotationZ + currentTilt, pivot = center) {
                    scale(scaleX = currentSquashX, scaleY = currentSquashY, pivot = center) {

                        val depthOffset = 6.dp.toPx()
                        val diceTopLeft = Offset(center.x - diceSize / 2f, center.y - diceSize / 2f)

                        // A. 3D Bevel Rim / Bottom Depth Edge
                        val bevelColors = when (skin) {
                            DiceSkin.BRUSHED_SILVER -> listOf(BrushedSilverDark, Color(0xFF485263), Color(0xFF28303E))
                            DiceSkin.ROYAL_GOLD -> listOf(Color(0xFFB28704), Color(0xFF7A5800), Color(0xFF3B2A00))
                            DiceSkin.CYBER_NEON -> listOf(Color(0xFF0E131F), Color(0xFF080B12), Color(0xFF030508))
                        }
                        drawRoundRect(
                            brush = Brush.linearGradient(
                                colors = bevelColors,
                                start = Offset(center.x - diceSize / 2f, center.y - diceSize / 2f),
                                end = Offset(center.x + diceSize / 2f, center.y + diceSize / 2f + depthOffset)
                            ),
                            topLeft = Offset(center.x - diceSize / 2f, center.y - diceSize / 2f + depthOffset),
                            size = Size(diceSize, diceSize),
                            cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                        )

                        // B. Main Dice Face Body Gradient
                        val bodyColors = when (skin) {
                            DiceSkin.BRUSHED_SILVER -> listOf(
                                BrushedSilverHighlight,
                                BrushedSilverLight,
                                BrushedSilver,
                                BrushedSilverDark,
                                Color(0xFFA0AAB8)
                            )
                            DiceSkin.ROYAL_GOLD -> listOf(
                                Color(0xFFFFFBEA),
                                Color(0xFFFFE082),
                                Color(0xFFFFD54F),
                                Color(0xFFFFB300),
                                Color(0xFFC79100)
                            )
                            DiceSkin.CYBER_NEON -> listOf(
                                Color(0xFF242C3D),
                                Color(0xFF1B2230),
                                Color(0xFF141924),
                                Color(0xFF0E121B),
                                Color(0xFF080A10)
                            )
                        }
                        drawRoundRect(
                            brush = Brush.linearGradient(
                                colors = bodyColors,
                                start = diceTopLeft,
                                end = Offset(diceTopLeft.x + diceSize, diceTopLeft.y + diceSize)
                            ),
                            topLeft = diceTopLeft,
                            size = Size(diceSize, diceSize),
                            cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                        )

                        // C. Outer Beveled Border Highlight
                        val borderColors = when (skin) {
                            DiceSkin.BRUSHED_SILVER -> listOf(
                                Color.White.copy(alpha = 0.95f),
                                BrushedSilverLight,
                                BrushedSilverDark.copy(alpha = 0.6f),
                                Color(0xFF505A68)
                            )
                            DiceSkin.ROYAL_GOLD -> listOf(
                                Color.White.copy(alpha = 0.95f),
                                Color(0xFFFFE082),
                                Color(0xFFC79100).copy(alpha = 0.8f),
                                Color(0xFF7A5800)
                            )
                            DiceSkin.CYBER_NEON -> listOf(
                                Color(0xFF00FFFF).copy(alpha = 0.9f),
                                Color(0xFF00B0FF),
                                Color(0xFF1A237E).copy(alpha = 0.6f),
                                Color(0xFF0D121D)
                            )
                        }
                        drawRoundRect(
                            brush = Brush.linearGradient(
                                colors = borderColors,
                                start = diceTopLeft,
                                end = Offset(diceTopLeft.x + diceSize, diceTopLeft.y + diceSize)
                            ),
                            topLeft = diceTopLeft,
                            size = Size(diceSize, diceSize),
                            cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                            style = Stroke(width = 2.5.dp.toPx())
                        )

                        // D. Inner Face Inset Recess
                        val innerPadding = diceSize * 0.05f
                        val innerSize = diceSize - (innerPadding * 2)
                        val innerTopLeft = Offset(diceTopLeft.x + innerPadding, diceTopLeft.y + innerPadding)
                        val innerHighlight = when (skin) {
                            DiceSkin.BRUSHED_SILVER -> Color.White.copy(alpha = 0.35f)
                            DiceSkin.ROYAL_GOLD -> Color(0xFFFFF8E1).copy(alpha = 0.5f)
                            DiceSkin.CYBER_NEON -> Color(0xFF00E5FF).copy(alpha = 0.25f)
                        }
                        drawRoundRect(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    innerHighlight,
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.15f)
                                ),
                                center = center,
                                radius = innerSize * 0.7f
                            ),
                            topLeft = innerTopLeft,
                            size = Size(innerSize, innerSize),
                            cornerRadius = CornerRadius(cornerRadius * 0.85f, cornerRadius * 0.85f)
                        )

                        // E. Render Pips (Dots) according to skin
                        drawDicePips(
                            face = faceValue,
                            center = center,
                            diceSize = diceSize,
                            isRolling = isRolling,
                            skin = skin
                        )

                        // F. Motion Blur Ghost lines during rapid roll
                        if (isRolling) {
                            drawMotionBlurTrails(center = center, diceSize = diceSize, glowColor = glowColor)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Draws the 3D-styled pips based on face value (1..6) and active skin.
 */
private fun DrawScope.drawDicePips(
    face: Int,
    center: Offset,
    diceSize: Float,
    isRolling: Boolean,
    skin: DiceSkin
) {
    val pipRadius = diceSize * 0.092f
    val offset = diceSize * 0.25f

    val cx = center.x
    val cy = center.y

    // Calculate positions based on standard dice layout
    val pipPositions = when (face) {
        1 -> listOf(Offset(cx, cy))
        2 -> listOf(
            Offset(cx - offset, cy - offset),
            Offset(cx + offset, cy + offset)
        )
        3 -> listOf(
            Offset(cx - offset, cy - offset),
            Offset(cx, cy),
            Offset(cx + offset, cy + offset)
        )
        4 -> listOf(
            Offset(cx - offset, cy - offset),
            Offset(cx + offset, cy - offset),
            Offset(cx - offset, cy + offset),
            Offset(cx + offset, cy + offset)
        )
        5 -> listOf(
            Offset(cx - offset, cy - offset),
            Offset(cx + offset, cy - offset),
            Offset(cx, cy),
            Offset(cx - offset, cy + offset),
            Offset(cx + offset, cy + offset)
        )
        6 -> listOf(
            Offset(cx - offset, cy - offset),
            Offset(cx + offset, cy - offset),
            Offset(cx - offset, cy),
            Offset(cx + offset, cy),
            Offset(cx - offset, cy + offset),
            Offset(cx + offset, cy + offset)
        )
        else -> listOf(Offset(cx, cy))
    }

    pipPositions.forEach { pos ->
        // Outer Inset Shadow for 3D engraved look
        drawCircle(
            color = Color.Black.copy(alpha = 0.45f),
            radius = pipRadius * 1.12f,
            center = Offset(pos.x + 1.2.dp.toPx(), pos.y + 1.6.dp.toPx())
        )

        // Outer Glow
        val pipGlowColor = when (skin) {
            DiceSkin.BRUSHED_SILVER -> MetallicTealGlow
            DiceSkin.ROYAL_GOLD -> AccentGold
            DiceSkin.CYBER_NEON -> Color(0xFF00E5FF)
        }

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    pipGlowColor.copy(alpha = if (isRolling) 0.6f else 0.35f),
                    Color.Transparent
                ),
                center = pos,
                radius = pipRadius * 2.2f
            ),
            center = pos
        )

        // Pip Base Radial Gradient
        val pipGradientColors = when (skin) {
            DiceSkin.BRUSHED_SILVER -> listOf(
                MetallicTealBright,
                MetallicTeal,
                MetallicTealDark
            )
            DiceSkin.ROYAL_GOLD -> listOf(
                Color(0xFF303642),
                Color(0xFF1A1F26),
                Color(0xFF0B0E14)
            )
            DiceSkin.CYBER_NEON -> listOf(
                Color(0xFFE0FFFF),
                Color(0xFF00E5FF),
                Color(0xFF0091EA)
            )
        }

        drawCircle(
            brush = Brush.radialGradient(
                colors = pipGradientColors,
                center = Offset(pos.x - pipRadius * 0.25f, pos.y - pipRadius * 0.25f),
                radius = pipRadius
            ),
            radius = pipRadius,
            center = pos
        )

        // Pip Rim Highlight
        val rimColor = when (skin) {
            DiceSkin.BRUSHED_SILVER -> MetallicTealGlow.copy(alpha = 0.8f)
            DiceSkin.ROYAL_GOLD -> Color(0xFFFFD54F).copy(alpha = 0.9f)
            DiceSkin.CYBER_NEON -> Color(0xFF00FFFF).copy(alpha = 0.9f)
        }
        drawCircle(
            color = rimColor,
            radius = pipRadius,
            center = pos,
            style = Stroke(width = 1.2.dp.toPx())
        )

        // Specular Pin-Light Reflection
        drawCircle(
            color = Color.White.copy(alpha = 0.90f),
            radius = pipRadius * 0.28f,
            center = Offset(pos.x - pipRadius * 0.35f, pos.y - pipRadius * 0.35f)
        )
    }
}

/**
 * Draws dynamic motion blur streak lines during the 0.6s shake / roll phase.
 */
private fun DrawScope.drawMotionBlurTrails(
    center: Offset,
    diceSize: Float,
    glowColor: Color
) {
    val trailColor = glowColor.copy(alpha = 0.25f)
    val streakCount = 8
    val radius = diceSize * 0.55f

    for (i in 0 until streakCount) {
        val angle = (i * (360f / streakCount)) * (Math.PI / 180f)
        val startX = center.x + (radius * 0.8f * cos(angle)).toFloat()
        val startY = center.y + (radius * 0.8f * sin(angle)).toFloat()
        val endX = center.x + (radius * 1.25f * cos(angle + 0.3f)).toFloat()
        val endY = center.y + (radius * 1.25f * sin(angle + 0.3f)).toFloat()

        drawLine(
            color = trailColor,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

