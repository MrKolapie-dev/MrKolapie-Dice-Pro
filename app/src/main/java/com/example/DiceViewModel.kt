package com.example

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ads.AdMobManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class DiceSkin(val displayName: String, val badge: String, val description: String) {
    BRUSHED_SILVER("Brushed Silver", "STANDARD", "Corporate Brushed Silver with Metallic Teal pips"),
    ROYAL_GOLD("24K Royal Gold", "VIP REWARD", "Polished 24K Gold with Obsidian core & ruby specular"),
    CYBER_NEON("Cyber Obsidian", "PRO REWARD", "Deep Black Titanium with Electric Cyan glow")
}

data class RollRecord(
    val id: Long = System.currentTimeMillis(),
    val face: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val rollNumber: Int
)

data class DiceUiState(
    val currentFace: Int = 1,
    val isRolling: Boolean = false,
    val totalRolls: Int = 0,
    val rollsUntilNextAd: Int = 10,
    val adMilestoneReached: Boolean = false,
    val faceCounts: Map<Int, Int> = mapOf(1 to 0, 2 to 0, 3 to 0, 4 to 0, 5 to 0, 6 to 0),
    val rollHistory: List<RollRecord> = emptyList(),
    val hapticsEnabled: Boolean = true,
    val shakeDetectionEnabled: Boolean = true,
    val shakeThresholdG: Float = 2.7f,
    val selectedSkin: DiceSkin = DiceSkin.BRUSHED_SILVER,
    val unlockedSkins: Set<DiceSkin> = setOf(DiceSkin.BRUSHED_SILVER),
    val rewardUnlockedMessage: String? = null
) {
    val averageRoll: Double
        get() = if (totalRolls == 0) 0.0 else {
            val sum = faceCounts.entries.sumOf { it.key * it.value }
            sum.toDouble() / totalRolls
        }
}

sealed interface DiceUiEvent {
    data object ShakeDetected : DiceUiEvent
    data object HeavyHapticTriggered : DiceUiEvent
    data object ShowInterstitialAd : DiceUiEvent
    data class RewardCelebration(val skin: DiceSkin) : DiceUiEvent
}

class DiceViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("mrkolapie_dice_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(
        DiceUiState(
            unlockedSkins = loadUnlockedSkins(),
            selectedSkin = loadSelectedSkin()
        )
    )
    val uiState: StateFlow<DiceUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<DiceUiEvent>()
    val events: SharedFlow<DiceUiEvent> = _events.asSharedFlow()

    private var rollJob: Job? = null

    private val vibrator: Vibrator? by lazy {
        val ctx = getApplication<Application>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = ctx.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            ctx.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    init {
        // Initialize AdMob on ViewModel initialization
        AdMobManager.initialize(application)
    }

    private fun loadUnlockedSkins(): Set<DiceSkin> {
        val saved = prefs.getStringSet("unlocked_skins", null) ?: emptySet()
        val set = mutableSetOf(DiceSkin.BRUSHED_SILVER)
        saved.forEach { name ->
            try {
                set.add(DiceSkin.valueOf(name))
            } catch (_: Exception) {}
        }
        return set
    }

    private fun loadSelectedSkin(): DiceSkin {
        val saved = prefs.getString("selected_skin", DiceSkin.BRUSHED_SILVER.name)
        return try {
            DiceSkin.valueOf(saved ?: DiceSkin.BRUSHED_SILVER.name)
        } catch (_: Exception) {
            DiceSkin.BRUSHED_SILVER
        }
    }

    private fun saveUnlockedSkins(skins: Set<DiceSkin>) {
        prefs.edit().putStringSet("unlocked_skins", skins.map { it.name }.toSet()).apply()
    }

    private fun saveSelectedSkin(skin: DiceSkin) {
        prefs.edit().putString("selected_skin", skin.name).apply()
    }

    fun selectSkin(skin: DiceSkin) {
        if (_uiState.value.unlockedSkins.contains(skin)) {
            _uiState.update { it.copy(selectedSkin = skin) }
            saveSelectedSkin(skin)
            performSubtleHaptic()
        }
    }

    /**
     * Grants the skin unlock, persists it, updates state, and plays reward celebration.
     */
    fun grantSkinReward(skinToUnlock: DiceSkin) {
        val newUnlocked = _uiState.value.unlockedSkins + skinToUnlock
        saveUnlockedSkins(newUnlocked)
        saveSelectedSkin(skinToUnlock)
        _uiState.update {
            it.copy(
                unlockedSkins = newUnlocked,
                selectedSkin = skinToUnlock,
                rewardUnlockedMessage = "Unlocked ${skinToUnlock.displayName}!"
            )
        }
        viewModelScope.launch {
            _events.emit(DiceUiEvent.RewardCelebration(skinToUnlock))
        }
        performHeavyHapticLanding()
    }

    /**
     * Shows a Rewarded Ad via AdMob and unlocks the requested VIP 3D Dice Skin.
     */
    fun unlockSkinWithRewardedAd(activity: Activity, skinToUnlock: DiceSkin) {
        performSubtleHaptic()
        AdMobManager.showRewardedAd(
            activity = activity,
            onUserEarnedReward = {
                grantSkinReward(skinToUnlock)
            }
        )
    }

    fun dismissRewardBanner() {
        _uiState.update { it.copy(rewardUnlockedMessage = null) }
    }

    /**
     * Executes the 3D dice roll:
     * - Cycles through 10-15 random faces over 0.6s with blur simulation.
     * - Delivers heavy haptic feedback on landing.
     * - Updates state & ad counter (interstitial triggered every 10 rolls).
     */
    fun rollDice() {
        if (_uiState.value.isRolling) return

        rollJob?.cancel()
        rollJob = viewModelScope.launch {
            _uiState.update { it.copy(isRolling = true) }

            // 0.6s rapid cycle through 12 random intermediate faces
            val cycleSteps = 12
            val stepDelayMs = 600L / cycleSteps

            repeat(cycleSteps) {
                val intermediateFace = Random.nextInt(1, 7)
                _uiState.update { it.copy(currentFace = intermediateFace) }
                delay(stepDelayMs)
            }

            // Determine final landed face
            val finalFace = Random.nextInt(1, 7)
            val newTotalRolls = _uiState.value.totalRolls + 1
            val updatedCounts = _uiState.value.faceCounts.toMutableMap().apply {
                this[finalFace] = (this[finalFace] ?: 0) + 1
            }

            val newRollRecord = RollRecord(
                face = finalFace,
                rollNumber = newTotalRolls
            )
            val updatedHistory = listOf(newRollRecord) + _uiState.value.rollHistory.take(29)

            // Interstitial ad triggers once every 10 rolls
            val triggerAd = (newTotalRolls % 10 == 0)
            val rollsRemaining = if (triggerAd) 10 else (10 - (newTotalRolls % 10))

            _uiState.update {
                it.copy(
                    currentFace = finalFace,
                    isRolling = false,
                    totalRolls = newTotalRolls,
                    rollsUntilNextAd = rollsRemaining,
                    adMilestoneReached = triggerAd,
                    faceCounts = updatedCounts,
                    rollHistory = updatedHistory
                )
            }

            // Heavy Haptic Feedback LongPress Effect on landing
            performHeavyHapticLanding()

            // Trigger Ad event if 10-roll milestone reached
            if (triggerAd) {
                _events.emit(DiceUiEvent.ShowInterstitialAd)
            }
        }
    }

    fun onShakeDetected() {
        if (!_uiState.value.isRolling && _uiState.value.shakeDetectionEnabled) {
            viewModelScope.launch {
                _events.emit(DiceUiEvent.ShakeDetected)
            }
            rollDice()
        }
    }

    fun toggleHaptics() {
        _uiState.update { it.copy(hapticsEnabled = !it.hapticsEnabled) }
    }

    fun toggleShakeDetection() {
        _uiState.update { it.copy(shakeDetectionEnabled = !it.shakeDetectionEnabled) }
    }

    fun resetHistory() {
        _uiState.update {
            it.copy(
                rollHistory = emptyList(),
                totalRolls = 0,
                faceCounts = mapOf(1 to 0, 2 to 0, 3 to 0, 4 to 0, 5 to 0, 6 to 0),
                rollsUntilNextAd = 10
            )
        }
    }

    fun showInterstitialAd(activity: Activity) {
        AdMobManager.showInterstitial(activity)
    }

    /**
     * Subtle haptic feedback for key UI interactions (buttons, tabs, navigation).
     * Complements the heavier landing vibration while respecting haptics toggle.
     */
    fun performSubtleHaptic() {
        if (!_uiState.value.hapticsEnabled) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(18L, 90))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(18L)
            }
        } catch (_: Exception) {
            // Graceful safety fallback
        }
    }

    private fun performHeavyHapticLanding() {
        if (!_uiState.value.hapticsEnabled) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Heavy landing long-press feel: short pre-tick followed by strong punch
                val timings = longArrayOf(0, 35, 20, 80)
                val amplitudes = intArrayOf(0, 180, 0, 255)
                val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
                vibrator?.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(100L)
            }
        } catch (_: Exception) {
            // Graceful safety fallback
        }
    }
}

