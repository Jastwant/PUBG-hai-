package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun EsportsTopBar(
    title: String,
    subtitle: String? = null,
    walletBalance: Double? = null,
    unreadNotifCount: Int = 0,
    onWalletClick: (() -> Unit)? = null,
    onNotifClick: (() -> Unit)? = null,
    onAdminClick: (() -> Unit)? = null,
    isAdmin: Boolean = true
) {
    Surface(
        color = DarkBg,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Circular gradient avatar / logo
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(CyberCyan, OceanBlue)))
                        .border(2.dp, DarkElevated, CircleShape)
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(DarkSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title.take(1).uppercase(),
                            color = CyberCyanLight,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (!subtitle.isNullOrBlank()) subtitle.uppercase() else "PRO GAMING ARENA",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = title,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.3.sp
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (walletBalance != null && onWalletClick != null) {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = DarkSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle),
                        modifier = Modifier.clickable { onWalletClick() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(CyberCyan),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("₹", color = DarkBg, fontWeight = FontWeight.Black, fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "₹${"%.2f".format(walletBalance)}",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                if (onNotifClick != null) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(DarkSurface)
                            .border(1.dp, DarkBorderSubtle, CircleShape)
                            .clickable { onNotifClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        if (unreadNotifCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-8).dp, y = 8.dp)
                                    .clip(CircleShape)
                                    .background(CyberCyanLight)
                                    .border(1.5.dp, DarkSurface, CircleShape)
                            )
                        }
                    }
                }

                if (isAdmin && onAdminClick != null) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = OceanBlue.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, OceanBlue.copy(alpha = 0.5f)),
                        modifier = Modifier.clickable { onAdminClick() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin",
                                tint = CyberCyanLight,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ADMIN",
                                color = CyberCyanLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NeonGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    gradientColors: List<Color> = listOf(CyberCyan, OceanBlue)
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = DarkElevated
        ),
        contentPadding = PaddingValues(),
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                if (enabled) Brush.horizontalGradient(gradientColors)
                else Brush.horizontalGradient(listOf(DarkElevated, DarkElevated))
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) DarkBg else TextMuted,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = if (enabled) DarkBg else TextMuted,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun StatusBadge(text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Text(
            text = text.uppercase(),
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
        )
    }
}

@Composable
fun GameIconResolver(gameId: String, modifier: Modifier = Modifier) {
    val icon = when {
        gameId.contains("bgmi") || gameId.contains("pubg") -> Icons.Default.GpsFixed
        gameId.contains("ff") -> Icons.Default.LocalFireDepartment
        gameId.contains("codm") -> Icons.Default.Security
        gameId.contains("ludo") -> Icons.Default.Casino
        gameId.contains("fortnite") -> Icons.Default.Bolt
        else -> Icons.Default.SportsEsports
    }
    val tint = when {
        gameId.contains("bgmi") -> CyberCyan
        gameId.contains("ff") -> RadiantOrange
        gameId.contains("codm") -> OceanBlue
        gameId.contains("ludo") -> EmeraldGreen
        else -> ElectricViolet
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DarkElevated)
            .border(1.dp, DarkBorderSubtle, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(tint.copy(alpha = 0.2f))
                .border(1.dp, tint.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

