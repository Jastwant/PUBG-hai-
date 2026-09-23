package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.SecurityDecisionAction
import com.example.security.DeviceSecurityManager
import com.example.ui.components.NeonGradientButton
import com.example.ui.theme.*

@Composable
fun SecurityBlockScreen(
    action: SecurityDecisionAction,
    reason: String?,
    onCheckAgain: () -> Unit
) {
    val context = LocalContext.current
    var isRechecking by remember { mutableStateOf(false) }
    var showDevBypassSheet by remember { mutableStateOf(false) }

    val isDevOptionsBlock = action == SecurityDecisionAction.DISABLE_DEVELOPER_OPTIONS
    val isIntegrityBlock = action == SecurityDecisionAction.FAILED_INTEGRITY

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon Badge
            Surface(
                shape = CircleShape,
                color = if (isDevOptionsBlock) CrimsonRed.copy(alpha = 0.15f) else CyberGold.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(2.dp, if (isDevOptionsBlock) CrimsonRed else CyberGold),
                modifier = Modifier.size(90.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isDevOptionsBlock) Icons.Default.Lock else Icons.Default.Security,
                        contentDescription = "Security Alert",
                        tint = if (isDevOptionsBlock) CrimsonRed else CyberGold,
                        modifier = Modifier.size(46.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = if (isDevOptionsBlock) "🔒 Security Check Required" else "Device Security Check Failed",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Subtitle / Reason
            Text(
                text = if (isDevOptionsBlock) {
                    "Developer Options or USB Debugging are currently enabled."
                } else {
                    "This application cannot continue on the current device environment. Please use the official application on a supported Android device."
                },
                color = TextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (isDevOptionsBlock) {
                // Instruction Card
                Surface(
                    color = DarkSurface,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "To use this application:",
                            color = CyberCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        StepItem(number = "1", text = "Open Android Settings")
                        Spacer(modifier = Modifier.height(8.dp))
                        StepItem(number = "2", text = "Open Developer Options")
                        Spacer(modifier = Modifier.height(8.dp))
                        StepItem(number = "3", text = "Turn Developer Options OFF")
                        Spacer(modifier = Modifier.height(8.dp))
                        StepItem(number = "4", text = "Return to the application")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Button(
                    onClick = {
                        DeviceSecurityManager.openDeveloperSettings(context)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "OPEN ANDROID SETTINGS",
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        isRechecking = true
                        onCheckAgain()
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = CyberCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "CHECK AGAIN",
                        color = CyberCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            } else {
                // Generic Integrity Failure
                Surface(
                    color = DarkSurface,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonRed.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Play Integrity Evaluation Failed",
                            color = CrimsonRed,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "The device environment could not be verified by Google Play Integrity. Rooted, untrusted, or modified packages are prohibited from joining esports prize pools.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                NeonGradientButton(
                    text = "RETRY SECURITY VERIFICATION",
                    onClick = onCheckAgain,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sandbox / QA Testing Control Note
            Surface(
                color = DarkElevated,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Sandbox / Test Environment Controls",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Container ADB/Dev Options simulation toggles",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                        TextButton(onClick = { showDevBypassSheet = !showDevBypassSheet }) {
                            Text(if (showDevBypassSheet) "Hide" else "Controls", color = CyberCyan, fontSize = 11.sp)
                        }
                    }

                    AnimatedVisibility(visible = showDevBypassSheet) {
                        Column(modifier = Modifier.padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            HorizontalDivider(color = DarkBorder)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Enforce Strict Anti-Cheat", color = Color.White, fontSize = 11.sp)
                                val strict by DeviceSecurityManager.strictSecurityEnabled.collectAsState()
                                Switch(
                                    checked = strict,
                                    onCheckedChange = {
                                        DeviceSecurityManager.setStrictSecurity(it)
                                        Toast.makeText(context, "Strict enforcement set to $it", Toast.LENGTH_SHORT).show()
                                        onCheckAgain()
                                    }
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Dev Options State Override", color = Color.White, fontSize = 11.sp)
                                val devOverride by DeviceSecurityManager.overrideDevOptions.collectAsState()
                                Row {
                                    Button(
                                        onClick = {
                                            DeviceSecurityManager.setSimulatedDevOptions(false)
                                            Toast.makeText(context, "Simulated Dev Options: OFF", Toast.LENGTH_SHORT).show()
                                            onCheckAgain()
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (devOverride == false) EmeraldGreen else DarkSurface
                                        ),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("Force OFF", fontSize = 10.sp)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Button(
                                        onClick = {
                                            DeviceSecurityManager.setSimulatedDevOptions(true)
                                            Toast.makeText(context, "Simulated Dev Options: ON", Toast.LENGTH_SHORT).show()
                                            onCheckAgain()
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (devOverride == true) CrimsonRed else DarkSurface
                                        ),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("Force ON", fontSize = 10.sp)
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

@Composable
private fun StepItem(number: String, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(CyberCyan.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(number, color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(text, color = Color.White, fontSize = 13.sp)
    }
}
