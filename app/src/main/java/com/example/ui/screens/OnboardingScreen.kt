package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.NeonGradientButton
import com.example.ui.theme.*

data class OnboardingStep(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accentColor: Color
)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    val steps = listOf(
        OnboardingStep(
            title = "Join Daily Esports Tournaments",
            subtitle = "Compete in BGMI, Free Fire MAX, Call of Duty & Ludo with automated matchmaking and room codes.",
            icon = Icons.Default.SportsEsports,
            accentColor = CyberCyan
        ),
        OnboardingStep(
            title = "Win Instant Real Cash Rewards",
            subtitle = "Guaranteed prize pools and per-kill bounties credited directly to your withdrawal wallet.",
            icon = Icons.Default.AccountBalanceWallet,
            accentColor = CyberGold
        ),
        OnboardingStep(
            title = "Play 1v1 PvP Challenges",
            subtitle = "Challenge your gaming friends or open lobbies with custom room rules and real-time chat.",
            icon = Icons.Default.FlashOn,
            accentColor = NeonPink
        ),
        OnboardingStep(
            title = "Climb National Leaderboards",
            subtitle = "Track in-depth kills, K/D ratios, win streaks and earn sponsorship rewards from top gaming brands.",
            icon = Icons.Default.Leaderboard,
            accentColor = ElectricViolet
        )
    )

    var currentStep by remember { mutableIntStateOf(0) }
    val step = steps[currentStep]

    Scaffold(
        containerColor = DarkBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar with Skip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "APEXZONE",
                    color = CyberCyan,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
                if (currentStep < steps.size - 1) {
                    TextButton(onClick = onFinish) {
                        Text("SKIP", color = TextSecondary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Center Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(DarkSurface)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp))
                            .background(step.accentColor.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = step.icon,
                            contentDescription = step.title,
                            tint = step.accentColor,
                            modifier = Modifier.size(56.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))

                Text(
                    text = step.title,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = step.subtitle,
                    color = TextSecondary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    steps.indices.forEach { index ->
                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .width(if (index == currentStep) 24.dp else 8.dp)
                                .clip(CircleShape)
                                .background(if (index == currentStep) CyberCyan else DarkBorder)
                        )
                    }
                }
            }

            // Bottom Actions
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NeonGradientButton(
                    text = if (currentStep == steps.size - 1) "GET STARTED" else "NEXT",
                    onClick = {
                        if (currentStep < steps.size - 1) {
                            currentStep++
                        } else {
                            onFinish()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
