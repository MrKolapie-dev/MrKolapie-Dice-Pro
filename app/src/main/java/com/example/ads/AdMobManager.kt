package com.example.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

/**
 * AdMobManager
 *
 * Handles preloading and display of AdMob Interstitial, Banner, and Rewarded ads.
 * Configured with MrKolapie publisher identifiers and official test ad units for zero-crash stability.
 */
object AdMobManager {
    private const val TAG = "MrKolapieAdMob"

    const val PUBLISHER_ID = "pub-5964442322640170"
    const val CUSTOMER_ID = "971-636-1263"
    const val APP_ID = "ca-app-pub-5964442322640170~1689712492"

    // Production AdMob Ad Unit IDs (MrKolapie Dice Pro)
    const val BANNER_AD_UNIT_ID = "ca-app-pub-5964442322640170/2442672330" // Main_Bottom_Banner
    const val REWARDED_AD_UNIT_ID = "ca-app-pub-5964442322640170/8666184381" // Golden_Dice_Rewarded

    // Standard Google AdMob Test Ad Units (safe for development & propagation warmup)
    const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    const val TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    const val TEST_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"

    // Active Ad Unit IDs used by the application
    var activeBannerAdUnitId: String = BANNER_AD_UNIT_ID
    var activeRewardedAdUnitId: String = REWARDED_AD_UNIT_ID

    private var interstitialAd: InterstitialAd? = null
    private var isInterstitialLoading = false

    private var rewardedAd: RewardedAd? = null
    private var isRewardedLoading = false

    private var isInitialized = false

    fun initialize(context: Context) {
        if (isInitialized) return
        try {
            MobileAds.initialize(context) { initializationStatus ->
                isInitialized = true
                Log.d(TAG, "AdMob MobileAds initialized: $initializationStatus")
                preloadInterstitial(context)
                preloadRewarded(context)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize MobileAds", e)
        }
    }

    fun preloadInterstitial(context: Context) {
        if (interstitialAd != null || isInterstitialLoading) return

        isInterstitialLoading = true
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            TEST_INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isInterstitialLoading = false
                    Log.d(TAG, "MrKolapie Interstitial Ad preloaded successfully.")

                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            Log.d(TAG, "Interstitial ad dismissed.")
                            interstitialAd = null
                            // Preload next interstitial for the next 10-roll milestone
                            preloadInterstitial(context)
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            Log.e(TAG, "Interstitial failed to show: ${adError.message}")
                            interstitialAd = null
                            preloadInterstitial(context)
                        }

                        override fun onAdShowedFullScreenContent() {
                            Log.d(TAG, "Interstitial showed fullscreen content.")
                        }
                    }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.w(TAG, "Interstitial ad failed to load: ${loadAdError.message}")
                    interstitialAd = null
                    isInterstitialLoading = false
                }
            }
        )
    }

    fun showInterstitial(activity: Activity, onAdClosed: (() -> Unit)? = null): Boolean {
        return if (interstitialAd != null) {
            val ad = interstitialAd
            val currentCallback = ad?.fullScreenContentCallback
            ad?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    currentCallback?.onAdDismissedFullScreenContent()
                    onAdClosed?.invoke()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    currentCallback?.onAdFailedToShowFullScreenContent(adError)
                    onAdClosed?.invoke()
                }

                override fun onAdShowedFullScreenContent() {
                    currentCallback?.onAdShowedFullScreenContent()
                }
            }
            ad?.show(activity)
            true
        } else {
            Log.d(TAG, "Interstitial not ready yet. Preloading...")
            preloadInterstitial(activity)
            onAdClosed?.invoke()
            false
        }
    }

    fun preloadRewarded(context: Context, useFallbackOnFail: Boolean = true) {
        if (rewardedAd != null || isRewardedLoading) return

        isRewardedLoading = true
        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(
            context,
            activeRewardedAdUnitId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    isRewardedLoading = false
                    Log.d(TAG, "MrKolapie Golden Dice Rewarded Ad ($activeRewardedAdUnitId) loaded successfully.")

                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            Log.d(TAG, "Rewarded ad dismissed.")
                            rewardedAd = null
                            preloadRewarded(context)
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            Log.e(TAG, "Rewarded ad failed to show: ${adError.message}")
                            rewardedAd = null
                            preloadRewarded(context)
                        }

                        override fun onAdShowedFullScreenContent() {
                            Log.d(TAG, "Rewarded ad showed fullscreen content.")
                        }
                    }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.w(TAG, "Rewarded ad ($activeRewardedAdUnitId) failed to load: ${loadAdError.message} (Code: ${loadAdError.code})")
                    rewardedAd = null
                    isRewardedLoading = false

                    // If newly created production ad unit is still propagating (up to 1 hour), fallback to sample ad unit
                    if (useFallbackOnFail && activeRewardedAdUnitId != TEST_REWARDED_AD_UNIT_ID) {
                        Log.d(TAG, "Warmup fallback: loading sample rewarded unit while new unit propagates...")
                        activeRewardedAdUnitId = TEST_REWARDED_AD_UNIT_ID
                        preloadRewarded(context, useFallbackOnFail = false)
                    }
                }
            }
        )
    }

    fun isRewardedAdReady(): Boolean = rewardedAd != null

    fun showRewardedAd(
        activity: Activity,
        onUserEarnedReward: () -> Unit,
        onAdClosed: (() -> Unit)? = null
    ): Boolean {
        return if (rewardedAd != null) {
            val ad = rewardedAd
            val currentCallback = ad?.fullScreenContentCallback
            ad?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    currentCallback?.onAdDismissedFullScreenContent()
                    onAdClosed?.invoke()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    currentCallback?.onAdFailedToShowFullScreenContent(adError)
                    onAdClosed?.invoke()
                }

                override fun onAdShowedFullScreenContent() {
                    currentCallback?.onAdShowedFullScreenContent()
                }
            }
            ad?.show(activity, OnUserEarnedRewardListener { rewardItem ->
                Log.d(TAG, "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
                onUserEarnedReward()
            })
            true
        } else {
            Log.d(TAG, "Rewarded Ad not ready yet. Preloading and triggering fallback reward...")
            preloadRewarded(activity)
            // If ad is not preloaded (e.g. offline mode), still grant reward gracefully so user isn't stuck
            onUserEarnedReward()
            onAdClosed?.invoke()
            false
        }
    }
}

