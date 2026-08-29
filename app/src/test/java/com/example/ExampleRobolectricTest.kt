package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("MrKolapie Dice Pro", appName)
    }

    @Test
    fun `viewModel toggles haptics and shake properly`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = DiceViewModel(app)

        assertTrue(viewModel.uiState.value.hapticsEnabled)
        viewModel.toggleHaptics()
        assertFalse(viewModel.uiState.value.hapticsEnabled)
        viewModel.toggleHaptics()
        assertTrue(viewModel.uiState.value.hapticsEnabled)

        assertTrue(viewModel.uiState.value.shakeDetectionEnabled)
        viewModel.toggleShakeDetection()
        assertFalse(viewModel.uiState.value.shakeDetectionEnabled)
    }

    @Test
    fun `viewModel subtle haptic executes without crash`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = DiceViewModel(app)
        viewModel.performSubtleHaptic()
    }

    @Test
    fun `viewModel dice skin initial state and reward unlocking works`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = DiceViewModel(app)

        // Initial skin is BRUSHED_SILVER and unlocked
        assertEquals(DiceSkin.BRUSHED_SILVER, viewModel.uiState.value.selectedSkin)
        assertTrue(viewModel.uiState.value.unlockedSkins.contains(DiceSkin.BRUSHED_SILVER))

        // Direct selection of locked skin is prevented
        viewModel.selectSkin(DiceSkin.ROYAL_GOLD)
        // Remains BRUSHED_SILVER if not unlocked yet
        assertEquals(DiceSkin.BRUSHED_SILVER, viewModel.uiState.value.selectedSkin)

        // Simulate rewarded ad unlock
        viewModel.grantSkinReward(DiceSkin.ROYAL_GOLD)
        assertTrue(viewModel.uiState.value.unlockedSkins.contains(DiceSkin.ROYAL_GOLD))
        assertEquals(DiceSkin.ROYAL_GOLD, viewModel.uiState.value.selectedSkin)
        assertTrue(viewModel.uiState.value.rewardUnlockedMessage?.contains("24K Royal Gold") == true)

        // Dismiss reward message
        viewModel.dismissRewardBanner()
        assertEquals(null, viewModel.uiState.value.rewardUnlockedMessage)

        // Now can switch between unlocked skins
        viewModel.selectSkin(DiceSkin.BRUSHED_SILVER)
        assertEquals(DiceSkin.BRUSHED_SILVER, viewModel.uiState.value.selectedSkin)
        viewModel.selectSkin(DiceSkin.ROYAL_GOLD)
        assertEquals(DiceSkin.ROYAL_GOLD, viewModel.uiState.value.selectedSkin)
    }
}

