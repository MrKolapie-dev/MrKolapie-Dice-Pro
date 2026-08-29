package com.example.ui.screens

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.DiceSkin
import com.example.DiceUiEvent
import com.example.DiceUiState
import com.example.DiceViewModel
import com.example.R
import com.example.ui.components.AdMobBanner
import com.example.ui.components.Dice3DCanvas
import com.example.ui.theme.AccentGold
import com.example.ui.theme.BentoBg
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoBorderLight
import com.example.ui.theme.BentoBottomBar
import com.example.ui.theme.BentoCardDark
import com.example.ui.theme.BentoCardHeader
import com.example.ui.theme.BentoCardSurface
import com.example.ui.theme.BrushedSilver
import com.example.ui.theme.BrushedSilverDark
import com.example.ui.theme.BrushedSilverHighlight
import com.example.ui.theme.BrushedSilverLight
import com.example.ui.theme.DeepSpaceNavy
import com.example.ui.theme.MetallicTeal
import com.example.ui.theme.MetallicTealBright
import com.example.ui.theme.MetallicTealGlow
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavySurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: DiceViewModel,
    onNavigateToLegal: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity

    // Handle ViewModel events (e.g. interstitial ads)
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is DiceUiEvent.ShowInterstitialAd -> {
                    if (activity != null) {
                        viewModel.showInterstitialAd(activity)
                    }
                }
                else -> Unit
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("home_screen_root")
    ) {
        // 1. Bento Top Header Bar
        BentoHeader(
            onLegalClick = {
                viewModel.performSubtleHaptic()
                onNavigateToLegal()
            },
            uiState = uiState,
            onToggleHaptics = {
                viewModel.performSubtleHaptic()
                viewModel.toggleHaptics()
            },
            onToggleShake = {
                viewModel.performSubtleHaptic()
                viewModel.toggleShakeDetection()
            }
        )

        // 2. Scrollable Body containing Bento Grid modules
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Reward Celebration Banner
            AnimatedVisibility(
                visible = uiState.rewardUnlockedMessage != null,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Surface(
                    color = AccentGold.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, AccentGold, RoundedCornerShape(14.dp))
                        .padding(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = "Celebration",
                                tint = AccentGold,
                                modifier = Modifier.size(22.dp)
                            )
                            Column {
                                Text(
                                    text = "REWARD UNLOCKED!",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentGold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = uiState.rewardUnlockedMessage ?: "",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                        }
                        IconButton(
                            onClick = { viewModel.dismissRewardBanner() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Dismiss",
                                tint = BrushedSilver,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Shake Sensitivity Banner Indicator
            ShakeStatusIndicator(
                isShakeActive = uiState.shakeDetectionEnabled,
                isRolling = uiState.isRolling
            )

            // 3D Canvas-Drawn Dice Arena with Ambient Bento Pulse Ring
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background subtle pulse ring
                val infiniteTransition = rememberInfiniteTransition(label = "pulse_ring")
                val ringScale by infiniteTransition.animateFloat(
                    initialValue = 0.95f,
                    targetValue = 1.05f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1200, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "ring_scale"
                )

                val ringColor = when (uiState.selectedSkin) {
                    DiceSkin.BRUSHED_SILVER -> MetallicTeal.copy(alpha = 0.15f)
                    DiceSkin.ROYAL_GOLD -> AccentGold.copy(alpha = 0.20f)
                    DiceSkin.CYBER_NEON -> Color(0xFF00FFCC).copy(alpha = 0.20f)
                }

                Box(
                    modifier = Modifier
                        .size(230.dp)
                        .scale(ringScale)
                        .clip(CircleShape)
                        .border(1.dp, ringColor, CircleShape)
                )

                Dice3DCanvas(
                    faceValue = uiState.currentFace,
                    isRolling = uiState.isRolling,
                    onRollRequest = {
                        viewModel.performSubtleHaptic()
                        viewModel.rollDice()
                    },
                    skin = uiState.selectedSkin,
                    modifier = Modifier.size(220.dp)
                )
            }

            // Outcome / Rolling State Label
            OutcomeLabel(
                face = uiState.currentFace,
                isRolling = uiState.isRolling
            )

            // Primary Roll Button
            RollActionButton(
                isRolling = uiState.isRolling,
                onClick = {
                    viewModel.performSubtleHaptic()
                    viewModel.rollDice()
                }
            )

            // BENTO REWARDED AD MODULE: 3D Dice Vault & Rewarded Skins
            BentoDiceVaultCard(
                uiState = uiState,
                onSelectSkin = { skin ->
                    viewModel.selectSkin(skin)
                },
                onWatchRewardedAd = { skin ->
                    if (activity != null) {
                        viewModel.unlockSkinWithRewardedAd(activity, skin)
                    }
                }
            )

            // BENTO GRID ROW 1: Session Streak & Privacy Status Tiles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tile 1: Session Streak / Total Rolls
                BentoTile(
                    modifier = Modifier.weight(1f),
                    title = "SESSION STREAK",
                    value = String.format(Locale.US, "%02d", uiState.totalRolls),
                    subtext = if (uiState.totalRolls == 0) "Ready to roll" else "Avg: ${String.format(Locale.US, "%.1f", uiState.averageRoll)}"
                )

                // Tile 2: Privacy Status / Ad Milestone
                BentoTile(
                    modifier = Modifier.weight(1f),
                    title = "PRIVACY STATUS",
                    value = "Secure • Pro",
                    subtext = "Ad in ${uiState.rollsUntilNextAd} rolls",
                    isHighlight = true
                )
            }

            // BENTO GRID ROW 2: Performance Analytics & Distribution Card
            BentoAnalyticsCard(
                uiState = uiState,
                onReset = {
                    viewModel.performSubtleHaptic()
                    viewModel.resetHistory()
                }
            )

            // BENTO GRID ROW 3: Privacy-First Dashboard Widget
            BentoPrivacyDashboardWidget(
                onAuditClick = {
                    viewModel.performSubtleHaptic()
                    onNavigateToLegal()
                }
            )

            // BENTO GRID ROW 4: Recent Roll History Stream
            if (uiState.rollHistory.isNotEmpty()) {
                BentoRollHistorySection(history = uiState.rollHistory)
            }

            Spacer(modifier = Modifier.height(4.dp))
        }

        // 3. AdMob Bottom Banner & Bento Permanent Corporate Footer
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BentoBottomBar)
                .border(1.dp, BentoBorder),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            AdMobBanner()
            BentoCorporateFooter(
                onLegalClick = {
                    viewModel.performSubtleHaptic()
                    onNavigateToLegal()
                }
            )
        }
    }
}

@Composable
fun BentoHeader(
    onLegalClick: () -> Unit,
    uiState: DiceUiState,
    onToggleHaptics: () -> Unit,
    onToggleShake: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_mrkolapie_logo),
                contentDescription = "MrKolapie Brand Logo",
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .border(1.dp, MetallicTeal.copy(alpha = 0.6f), CircleShape)
            )

            Column {
                Text(
                    text = "MrKolapie",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = BrushedSilver
                )
                Text(
                    text = "DICE PRO EDITION",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = MetallicTeal
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Haptics toggle button in Bento Card Surface
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(BentoCardSurface)
                    .border(1.dp, BentoBorder, RoundedCornerShape(14.dp))
                    .clickable(onClick = onToggleHaptics)
                    .testTag("toggle_haptics_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Vibration,
                    contentDescription = "Toggle Haptics",
                    tint = if (uiState.hapticsEnabled) MetallicTealGlow else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Shake toggle button
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(BentoCardSurface)
                    .border(1.dp, BentoBorder, RoundedCornerShape(14.dp))
                    .clickable(onClick = onToggleShake)
                    .testTag("toggle_shake_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PhoneAndroid,
                    contentDescription = "Toggle Shake Sensor",
                    tint = if (uiState.shakeDetectionEnabled) MetallicTealGlow else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Legal & Compliance Hub
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(BentoCardSurface)
                    .border(1.dp, BentoBorder, RoundedCornerShape(14.dp))
                    .clickable(onClick = onLegalClick)
                    .testTag("open_legal_hub_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Legal and Compliance Hub",
                    tint = BrushedSilverLight,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun ShakeStatusIndicator(
    isShakeActive: Boolean,
    isRolling: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shake_pulse"
    )

    Surface(
        color = BentoCardDark,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, if (isShakeActive) MetallicTeal.copy(alpha = 0.4f) else BentoBorder, RoundedCornerShape(20.dp))
            .scale(if (isRolling) pulseScale else 1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isShakeActive) MetallicTealGlow else TextMuted)
            )
            Text(
                text = if (isRolling) "Rolling in Motion..." else if (isShakeActive) "SHAKE DEVICE TO ROLL" else "SHAKE SENSOR PAUSED",
                fontSize = 11.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                color = if (isShakeActive) MetallicTeal.copy(alpha = 0.85f) else TextMuted
            )
        }
    }
}

@Composable
fun OutcomeLabel(face: Int, isRolling: Boolean) {
    val faceNames = listOf("One", "Two", "Three", "Four", "Five", "Six")
    val displayName = if (face in 1..6) faceNames[face - 1] else "$face"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = if (isRolling) "SHUFFLING..." else "ROLLED $displayName",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = if (isRolling) BrushedSilverDark else MetallicTealGlow,
            letterSpacing = 1.5.sp,
            modifier = Modifier.testTag("outcome_label")
        )
        Text(
            text = if (isRolling) "0.6s High-Frequency Cycle" else "Landed Value: $face",
            fontFamily = FontFamily.SansSerif,
            fontSize = 12.sp,
            color = TextSecondary
        )
    }
}

@Composable
fun RollActionButton(
    isRolling: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = !isRolling,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MetallicTeal,
            disabledContainerColor = MetallicTeal.copy(alpha = 0.4f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(listOf(BrushedSilverHighlight, MetallicTealGlow)),
                shape = RoundedCornerShape(16.dp)
            )
            .testTag("roll_dice_button")
    ) {
        Text(
            text = if (isRolling) "ROLLING..." else "ROLL 3D DICE",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            letterSpacing = 1.sp,
            color = DeepSpaceNavy
        )
    }
}

/**
 * Bento Grid Tile Component (Rounded-3xl, BentoCardDark, subtle border)
 */
@Composable
fun BentoTile(
    title: String,
    value: String,
    subtext: String,
    modifier: Modifier = Modifier,
    isHighlight: Boolean = false
) {
    Surface(
        color = BentoCardDark,
        shape = RoundedCornerShape(24.dp),
        modifier = modifier
            .border(1.dp, BentoBorder, RoundedCornerShape(24.dp))
            .testTag("bento_tile_${title.lowercase().replace(" ", "_")}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                fontSize = 10.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                color = MetallicTeal
            )
            Text(
                text = value,
                fontSize = if (value.length > 8) 16.sp else 24.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.SansSerif,
                color = BrushedSilver
            )
            Text(
                text = subtext,
                fontSize = 11.sp,
                fontFamily = FontFamily.SansSerif,
                color = TextSecondary
            )
        }
    }
}

/**
 * Bento Analytics & Distribution Card
 */
@Composable
fun BentoAnalyticsCard(
    uiState: DiceUiState,
    onReset: () -> Unit
) {
    Surface(
        color = BentoCardDark,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BentoBorder, RoundedCornerShape(24.dp))
            .testTag("dice_stats_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Analytics & Distribution",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = BrushedSilverHighlight
                )

                if (uiState.totalRolls > 0) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = onReset)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Stats",
                            tint = TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Reset",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            // Distribution bars for numbers 1 to 6
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (1..6).forEach { face ->
                    val count = uiState.faceCounts[face] ?: 0
                    val maxCount = (uiState.faceCounts.values.maxOrNull() ?: 1).coerceAtLeast(1)
                    val fraction = if (uiState.totalRolls == 0) 0f else count.toFloat() / maxCount

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "$count",
                            fontSize = 10.sp,
                            color = if (count > 0) MetallicTealBright else TextMuted,
                            fontWeight = FontWeight.SemiBold
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(BentoCardHeader),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height((42 * fraction).coerceAtLeast(if (count > 0) 4f else 0f).dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(MetallicTealBright, MetallicTeal)
                                        )
                                    )
                            )
                        }
                        Text(
                            text = "D$face",
                            fontSize = 10.sp,
                            color = BrushedSilverDark,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

/**
 * Bento Privacy-First Dashboard Widget with Header Bar
 */
@Composable
fun BentoPrivacyDashboardWidget(
    onAuditClick: () -> Unit
) {
    Surface(
        color = BentoCardSurface,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, BentoBorder, RoundedCornerShape(18.dp))
            .testTag("bento_privacy_widget")
    ) {
        Column {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BentoCardHeader)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "PRIVACY-FIRST DASHBOARD",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = BrushedSilver
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MetallicTeal)
                        .border(1.dp, MetallicTealGlow, CircleShape)
                )
            }

            // Body
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Data Audit: 0KB collected. All processing occurs on-device. No third-party data sharing enabled.",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.SansSerif,
                    color = Color(0xFFCBD5E1),
                    lineHeight = 16.sp,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = onAuditClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MetallicTeal,
                        contentColor = BentoBg
                    ),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("bento_audit_button")
                ) {
                    Text(
                        text = "AUDIT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }
        }
    }
}

/**
 * Bento Roll History Stream
 */
@Composable
fun BentoRollHistorySection(history: List<com.example.RollRecord>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Recent Roll Stream",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = BrushedSilverLight
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            history.take(7).forEach { record ->
                Surface(
                    color = BentoCardDark,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .border(1.dp, BentoBorder, RoundedCornerShape(14.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "#${record.rollNumber}",
                            fontSize = 9.sp,
                            color = TextMuted
                        )
                        Text(
                            text = "${record.face}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MetallicTealGlow,
                            fontFamily = FontFamily.Serif
                        )
                    }
                }
            }
        }
    }
}

/**
 * Bento Permanent Corporate Footer with Teal Accent Line
 */
@Composable
fun BentoCorporateFooter(
    onLegalClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 12.dp, start = 16.dp, end = 16.dp)
            .then(
                if (onLegalClick != null) {
                    Modifier.clickable(onClick = onLegalClick)
                } else Modifier
            )
            .testTag("permanent_corporate_footer"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        // Teal Accent Divider Line
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(1.dp)
                .background(MetallicTeal)
        )

        Text(
            text = "MrKolapie Pty Ltd | Reg: 2025/537780/07",
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 10.sp,
            color = BrushedSilver,
            textAlign = TextAlign.Center
        )
        Text(
            text = "B-BBEE LEVEL 1 • PRIVACY CENTRIC UTILITY",
            fontFamily = FontFamily.SansSerif,
            fontSize = 8.sp,
            letterSpacing = 1.sp,
            color = TextMuted,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Bento 3D Dice Vault & Rewarded Skins Card
 * Allows user to choose skins or watch a rewarded video ad to unlock VIP 3D Royal Gold & Cyber Obsidian skins.
 */
@Composable
fun BentoDiceVaultCard(
    uiState: DiceUiState,
    onSelectSkin: (DiceSkin) -> Unit,
    onWatchRewardedAd: (DiceSkin) -> Unit
) {
    Surface(
        color = BentoCardSurface,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, BentoBorder, RoundedCornerShape(18.dp))
            .testTag("bento_dice_vault_card")
    ) {
        Column {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BentoCardHeader)
                    .padding(horizontal = 16.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Dice Vault",
                        tint = AccentGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "3D DICE VAULT & REWARD SKINS",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = BrushedSilver
                    )
                }

                Text(
                    text = "${uiState.unlockedSkins.size}/${DiceSkin.entries.size} UNLOCKED",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MetallicTealGlow,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Body: Skin Selection Rows
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DiceSkin.entries.forEach { skin ->
                    val isUnlocked = uiState.unlockedSkins.contains(skin)
                    val isSelected = uiState.selectedSkin == skin

                    Surface(
                        color = if (isSelected) NavyDark else BentoCardDark,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) {
                                    if (skin == DiceSkin.ROYAL_GOLD) AccentGold else MetallicTeal
                                } else BentoBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                if (isUnlocked) {
                                    onSelectSkin(skin)
                                } else {
                                    onWatchRewardedAd(skin)
                                }
                            }
                            .testTag("skin_row_${skin.name}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Left: Badge + Title + Description
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (skin) {
                                                DiceSkin.BRUSHED_SILVER -> Brush.linearGradient(
                                                    listOf(BrushedSilverLight, BrushedSilverDark)
                                                )
                                                DiceSkin.ROYAL_GOLD -> Brush.linearGradient(
                                                    listOf(Color(0xFFFFE082), AccentGold, Color(0xFFC79100))
                                                )
                                                DiceSkin.CYBER_NEON -> Brush.linearGradient(
                                                    listOf(Color(0xFF00FFCC), Color(0xFF0091EA))
                                                )
                                            }
                                        )
                                        .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isUnlocked) Icons.Filled.LockOpen else Icons.Filled.Lock,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = skin.displayName,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else BrushedSilverLight
                                        )
                                        Text(
                                            text = skin.badge,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (skin) {
                                                DiceSkin.BRUSHED_SILVER -> MetallicTeal
                                                DiceSkin.ROYAL_GOLD -> AccentGold
                                                DiceSkin.CYBER_NEON -> Color(0xFF00FFCC)
                                            },
                                            modifier = Modifier
                                                .background(
                                                    color = when (skin) {
                                                        DiceSkin.BRUSHED_SILVER -> MetallicTeal.copy(alpha = 0.15f)
                                                        DiceSkin.ROYAL_GOLD -> AccentGold.copy(alpha = 0.15f)
                                                        DiceSkin.CYBER_NEON -> Color(0xFF00FFCC).copy(alpha = 0.15f)
                                                    },
                                                    shape = RoundedCornerShape(4.dp)
                                                )
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = skin.description,
                                        fontSize = 10.sp,
                                        color = TextMuted,
                                        lineHeight = 13.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Right: Action Button (Equip / Watch Video)
                            if (isSelected) {
                                Surface(
                                    color = MetallicTeal.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.border(1.dp, MetallicTeal, RoundedCornerShape(8.dp))
                                ) {
                                    Text(
                                        text = "EQUIPPED",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MetallicTealGlow,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                    )
                                }
                            } else if (isUnlocked) {
                                Button(
                                    onClick = { onSelectSkin(skin) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = BentoCardSurface,
                                        contentColor = BrushedSilverLight
                                    ),
                                    modifier = Modifier
                                        .height(30.dp)
                                        .border(1.dp, BentoBorderLight, RoundedCornerShape(8.dp))
                                ) {
                                    Text(
                                        text = "EQUIP",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Button(
                                    onClick = { onWatchRewardedAd(skin) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (skin == DiceSkin.ROYAL_GOLD) AccentGold else MetallicTeal,
                                        contentColor = Color.Black
                                    ),
                                    modifier = Modifier
                                        .height(32.dp)
                                        .testTag("unlock_reward_ad_button_${skin.name}")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.PlayCircle,
                                            contentDescription = "Watch Ad",
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = "UNLOCK FREE",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold
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


