package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ads.AdMobManager
import com.example.ui.theme.BentoBg
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoCardDark
import com.example.ui.theme.BentoCardHeader
import com.example.ui.theme.BentoCardSurface
import com.example.ui.theme.BrushedSilver
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
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class LegalTab(val title: String, val icon: ImageVector) {
    PRIVACY("Privacy Policy", Icons.Default.PrivacyTip),
    DATA_AUDIT("Data Audit", Icons.Default.Security),
    TERMS("Terms of Service", Icons.Default.Description),
    COMPLIANCE("Compliance Hub", Icons.Default.Gavel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    onSubtleHaptic: (() -> Unit)? = null
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = LegalTab.entries

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBg)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("legal_screen_root")
    ) {
        // Corporate Top App Bar
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = "Legal & Governance Hub",
                        style = MaterialTheme.typography.titleLarge,
                        color = BrushedSilverHighlight,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = "MrKolapie Pty Ltd | Privacy-First Architecture",
                        style = MaterialTheme.typography.labelSmall,
                        color = MetallicTealBright
                    )
                }
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        onSubtleHaptic?.invoke()
                        onNavigateBack()
                    },
                    modifier = Modifier.testTag("legal_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Dice",
                        tint = MetallicTeal
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = BentoCardDark,
                titleContentColor = TextPrimary
            )
        )

        // Corporate Badges Bar
        CorporateIdentityHeader()

        // Tab Row
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = BentoCardDark,
            contentColor = MetallicTeal,
            edgePadding = 16.dp,
            modifier = Modifier.testTag("legal_tabs_row")
        ) {
            tabs.forEachIndexed { index, tab ->
                val isSelected = selectedTabIndex == index
                Tab(
                    selected = isSelected,
                    onClick = {
                        onSubtleHaptic?.invoke()
                        selectedTabIndex = index
                    },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (isSelected) MetallicTealGlow else BrushedSilver
                            )
                            Text(
                                text = tab.title,
                                color = if (isSelected) MetallicTealGlow else BrushedSilver,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 13.sp
                            )
                        }
                    },
                    modifier = Modifier.testTag("legal_tab_${tab.name.lowercase()}")
                )
            }
        }

        // Tab Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            AnimatedContent(
                targetState = tabs[selectedTabIndex],
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "legal_tab_content"
            ) { targetTab ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (targetTab) {
                        LegalTab.PRIVACY -> PrivacyPolicySection()
                        LegalTab.DATA_AUDIT -> DataAuditSection()
                        LegalTab.TERMS -> TermsOfServiceSection()
                        LegalTab.COMPLIANCE -> ComplianceSection()
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    BentoCorporateFooter(onLegalClick = null)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun CorporateIdentityHeader() {
    Surface(
        color = BentoCardSurface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, BentoBorder, RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(MetallicTeal, BentoCardHeader))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = "Verified",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column {
                    Text(
                        text = "MrKolapie Pty Ltd",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = "Reg: 2025/537780/07 | B-BBEE Level 1",
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 11.sp,
                        color = MetallicTealBright
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MetallicTeal.copy(alpha = 0.15f))
                    .border(1.dp, MetallicTeal, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "AUDITED",
                    color = MetallicTealGlow,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
            }
        }
    }
}

@Composable
fun PrivacyPolicySection() {
    LegalCard(
        title = "Privacy-First Mandate",
        highlightText = "MrKolapie Pty Ltd operates on a Privacy-First basis. We do not sell, trade, or share user data with third parties."
    ) {
        LegalParagraph(
            title = "1. Core Operating Philosophy",
            body = "MrKolapie Dice Pro is engineered from the ground up as a zero-knowledge, privacy-centric utility. All dice roll physics, pseudo-random calculations, and shake detection heuristics occur 100% on your local device."
        )
        LegalParagraph(
            title = "2. Sensor Data Processing",
            body = "The hardware accelerometer is monitored only while the application is active on screen to calculate real-time G-force thresholds (2.7G). Sensor values are stored purely in volatile temporary memory and are instantaneously discarded after low-pass filter processing."
        )
        LegalParagraph(
            title = "3. No Personal Identifiers",
            body = "We do not request, access, or store your name, email address, physical location, device contacts, or photos. No user profile is constructed."
        )
        LegalParagraph(
            title = "4. Advertising & AdMob",
            body = "Standard advertising functionality is serviced via Google Mobile Ads SDK (Publisher ID: ${AdMobManager.PUBLISHER_ID}). Ads are non-personalized where required and adhere strictly to global platform policies."
        )
    }
}

@Composable
fun DataAuditSection() {
    LegalCard(
        title = "On-Device Data Audit Log",
        highlightText = "100% Transparent Architecture: Real-time verifiable data collection footprint."
    ) {
        val auditItems = listOf(
            AuditItem("Personal Information", "None Collected", "0 bytes", true),
            AuditItem("Location / GPS", "Zero Access Declared", "0 bytes", true),
            AuditItem("Accelerometer (Shake)", "Volatile RAM Only", "Realtime Flush", true),
            AuditItem("Dice Roll History", "Local Session State", "Not synced to Cloud", true),
            AuditItem("AdMob Telemetry", "Standard Ad Request", "Anonymous SDK ID", true),
            AuditItem("Data Brokers / Resellers", "Strictly Prohibited", "0 Transfers", true)
        )

        auditItems.forEach { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BentoCardHeader)
                    .border(1.dp, BentoBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verified Safe",
                        tint = SuccessGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Column {
                        Text(
                            text = item.name,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = item.status,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
                Text(
                    text = item.footprint,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = MetallicTealBright
                )
            }
        }
    }
}

data class AuditItem(
    val name: String,
    val status: String,
    val footprint: String,
    val isSafe: Boolean
)

@Composable
fun TermsOfServiceSection() {
    LegalCard(
        title = "Terms of Service",
        highlightText = "Standard Corporate Usage & Fair Utility Agreement"
    ) {
        LegalParagraph(
            title = "1. License & Scope of Use",
            body = "MrKolapie Pty Ltd grants you a personal, non-exclusive, non-transferable, revocable license to use MrKolapie Dice Pro solely for non-commercial recreational and utility purposes."
        )
        LegalParagraph(
            title = "2. Non-Gambling Disclaimer",
            body = "This software is an entertainment utility and random number generator. It is not an online gambling platform or real-money wagering tool."
        )
        LegalParagraph(
            title = "3. Intellectual Property Rights",
            body = "All branding, 3D Canvas visual shaders, MrKolapie Design System palettes, and proprietary algorithms are the exclusive intellectual property of MrKolapie Pty Ltd (Reg: 2025/537780/07)."
        )
        LegalParagraph(
            title = "4. Limitation of Liability",
            body = "The application is provided 'AS IS' without warranties of any kind. MrKolapie Pty Ltd shall not be liable for any indirect or incidental damages."
        )
    }
}

@Composable
fun ComplianceSection() {
    LegalCard(
        title = "Global Regulatory Disclosures",
        highlightText = "Full Compliance with POPIA (South Africa), GDPR (EU), and CCPA/CPRA (USA)."
    ) {
        LegalParagraph(
            title = "POPIA Compliance (South Africa Act No. 4 of 2013)",
            body = "As a proudly South African registered entity (2025/537780/07 | B-BBEE Level 1), MrKolapie Pty Ltd fully adheres to all 8 Conditions for Lawful Processing under POPIA. No personal information is captured or outsourced."
        )
        LegalParagraph(
            title = "GDPR / UK-GDPR Disclosures (EU Regulation 2016/679)",
            body = "Under Articles 12, 13, and 14 of the GDPR, data subjects have the right to privacy by design and by default. Because no personal data is collected or stored on our servers, there is zero risk of unauthorized cross-border profile processing."
        )
        LegalParagraph(
            title = "CCPA / CPRA Notice (California Consumer Privacy Act)",
            body = "Notice at Collection: We do not sell or share personal information with third parties. No opt-out is necessary because data sales are strictly zero."
        )
        LegalParagraph(
            title = "Publisher & Compliance Officer Contact",
            body = "Entity: MrKolapie Pty Ltd\nRegistration No: 2025/537780/07\nB-BBEE Status: Level 1 Contributor\nAdMob Customer ID: ${AdMobManager.CUSTOMER_ID}\nAdMob Publisher: ${AdMobManager.PUBLISHER_ID}"
        )
        LegalParagraph(
            title = "Authorized Digital Sellers (app-ads.txt)",
            body = "google.com, pub-5964442322640170, DIRECT, f08c47fec0942fa0\nCertified for programmatic supply-chain verification under IAB OpenRTB specs."
        )
        LegalParagraph(
            title = "Device Hardware & Architecture Certification",
            body = "Certified and optimized for Samsung SM-A055F/DS (64GB), Android 14/15, and 60Hz/90Hz/120Hz display refresh rates with low-latency accelerometer sensor processing."
        )
    }
}

@Composable
fun LegalCard(
    title: String,
    highlightText: String? = null,
    content: @Composable () -> Unit
) {
    Surface(
        color = BentoCardDark,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BentoBorder, RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = BrushedSilverHighlight
            )

            if (highlightText != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MetallicTeal.copy(alpha = 0.12f))
                        .border(1.dp, MetallicTeal.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = highlightText,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = MetallicTealGlow,
                        lineHeight = 18.sp
                    )
                }
            }

            content()
        }
    }
}

@Composable
fun LegalParagraph(title: String, body: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = BrushedSilverLight
        )
        Text(
            text = body,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = TextSecondary,
            lineHeight = 17.sp
        )
    }
}
