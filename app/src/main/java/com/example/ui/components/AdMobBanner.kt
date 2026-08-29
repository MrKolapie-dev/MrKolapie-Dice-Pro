package com.example.ui.components

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ads.AdMobManager
import com.example.ui.theme.DeepSpaceNavy
import com.example.ui.theme.MetallicTeal
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyCard
import com.example.ui.theme.TextMuted
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
fun AdMobBanner(
    modifier: Modifier = Modifier,
    adUnitId: String = AdMobManager.BANNER_AD_UNIT_ID
) {
    val isPreview = LocalInspectionMode.current
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(NavyCard)
            .border(1.dp, NavyBorder, RoundedCornerShape(8.dp))
            .testTag("admob_banner_container"),
        contentAlignment = Alignment.Center
    ) {
        if (isPreview) {
            Text(
                text = "AdMob Banner [${AdMobManager.PUBLISHER_ID}]",
                color = TextMuted,
                fontSize = 11.sp,
                fontFamily = FontFamily.SansSerif
            )
        } else {
            AndroidView(
                modifier = Modifier.fillMaxWidth().testTag("admob_banner_view"),
                factory = { ctx ->
                    AdView(ctx).apply {
                        setAdSize(AdSize.BANNER)
                        this.adUnitId = adUnitId
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        adListener = object : AdListener() {
                            override fun onAdFailedToLoad(error: LoadAdError) {
                                super.onAdFailedToLoad(error)
                                // If the newly created unit is still activating in AdMob (takes up to ~1hr),
                                // seamlessly load the standard test banner so the UI displays sample ads while waiting
                                if (adUnitId != AdMobManager.TEST_BANNER_AD_UNIT_ID) {
                                    post {
                                        try {
                                            val fallbackAdView = AdView(context).apply {
                                                setAdSize(AdSize.BANNER)
                                                this.adUnitId = AdMobManager.TEST_BANNER_AD_UNIT_ID
                                                layoutParams = ViewGroup.LayoutParams(
                                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                                )
                                                loadAd(AdRequest.Builder().build())
                                            }
                                        } catch (_: Exception) {}
                                    }
                                }
                            }
                        }
                        loadAd(AdRequest.Builder().build())
                    }
                }
            )
        }
    }
}
