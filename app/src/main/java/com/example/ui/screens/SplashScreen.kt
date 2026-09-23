package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.AppConfig
import com.example.ui.components.NeonGradientButton
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    appConfig: AppConfig,
    onSecurityBlocked: (com.example.data.models.SecurityDecisionAction, String?) -> Unit,
    onNavigateNext: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var isChecking by remember { mutableStateOf(true) }
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        delay(1400)
        // 1. Evaluate device security & integrity
        val decision = com.example.security.DeviceSecurityManager.evaluateSecurity(context)
        isChecking = false

        if (!decision.allowed) {
            onSecurityBlocked(decision.action, decision.reason)
        } else if (!appConfig.isMaintenanceMode) {
            onNavigateNext()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg),
        contentAlignment = Alignment.Center
    ) {
        if (appConfig.isMaintenanceMode && !isChecking) {
            // Maintenance Mode View
            Column(
                modifier = Modifier
                    .padding(28.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(CyberGold.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Maintenance",
                        tint = CyberGold,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "SERVER UNDER MAINTENANCE",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = appConfig.maintenanceMessage,
                    color = TextSecondary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(28.dp))
                NeonGradientButton(
                    text = "Bypass for Admin Preview",
                    onClick = onNavigateNext,
                    modifier = Modifier.fillMaxWidth(0.85f)
                )
            }
        } else {
            // Normal Splash
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(CyberCyan, ElectricViolet, DarkSurface)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = "App Logo",
                        tint = Color.Black,
                        modifier = Modifier.size(60.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "APEXZONE",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp
                )
                Text(
                    text = "ESPORTS TOURNAMENT ARENA",
                    color = CyberCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(36.dp))
                CircularProgressIndicator(
                    color = CyberCyan,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Connecting to Secure Servers...",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }
    }
}
