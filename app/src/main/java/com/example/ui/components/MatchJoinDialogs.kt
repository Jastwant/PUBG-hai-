package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.models.JoinResult
import com.example.data.models.Match
import com.example.data.models.Wallet
import com.example.data.repository.EsportsRepository
import com.example.ui.theme.*
import com.example.util.ReminderVibrationManager

/**
 * State-based Join Confirmation Dialog:
 * Shows Match Details, Entry Fee, Wallet Balance, Post-join Balance, Start Time, IGN input, and confirmation disclaimer.
 */
@Composable
fun MatchJoinConfirmationDialog(
    match: Match,
    wallet: Wallet,
    onDismiss: () -> Unit,
    onNavigateWallet: () -> Unit,
    onJoinSuccess: (JoinResult) -> Unit
) {
    val context = LocalContext.current
    val currentUser by EsportsRepository.currentUser.collectAsState()
    var inGameName by remember {
        mutableStateOf(currentUser?.inGameNames?.get(match.gameId) ?: currentUser?.username ?: "")
    }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    val hasEnoughBalance = wallet.totalBalance >= match.entryFee
    val balanceAfterJoin = (wallet.totalBalance - match.entryFee).coerceAtLeast(0.0)

    Dialog(onDismissRequest = { if (!isSubmitting) onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = DarkCard,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, CyberCyan.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyberCyan.copy(alpha = 0.15f))
                                .border(1.dp, CyberCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsEsports,
                                contentDescription = null,
                                tint = CyberCyanLight,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "CONFIRM MATCH JOIN",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "${match.gameName} • ${match.type.name}",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        enabled = !isSubmitting,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Match Title Box
                Surface(
                    color = DarkElevated,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = match.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Map: ${match.mapName}", color = TextSecondary, fontSize = 11.sp)
                            Text("•", color = TextMuted, fontSize = 11.sp)
                            Text("Starts: ${match.startTime}", color = CyberCyanLight, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Financial Breakdown Matrix
                Surface(
                    color = DarkSurface,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Entry Fee
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Entry Fee", color = TextSecondary, fontSize = 13.sp)
                            Text(
                                text = if (match.entryFee == 0.0) "FREE" else "₹${"%.0f".format(match.entryFee)}",
                                color = CyberGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = DarkBorderSubtle)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Wallet Balance
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Wallet Balance", color = TextSecondary, fontSize = 13.sp)
                            Text(
                                text = "₹${"%.2f".format(wallet.totalBalance)}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Balance After Join
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Balance After Join", color = TextSecondary, fontSize = 13.sp)
                            Text(
                                text = if (hasEnoughBalance) "₹${"%.2f".format(balanceAfterJoin)}" else "Insufficient",
                                color = if (hasEnoughBalance) EmeraldGreen else CrimsonRed,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // In-Game Name (IGN) Input
                Text(
                    text = "IN-GAME NAME (IGN)",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = inGameName,
                    onValueChange = {
                        inGameName = it
                        errorText = null
                    },
                    placeholder = { Text("e.g. APEX_Striker", color = TextMuted, fontSize = 13.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Warning / Disclaimer Note
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberGold.copy(alpha = 0.1f))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = CyberGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Entry fee will be deducted immediately after confirmation. Room credentials will reveal 10m before start.",
                        color = Color(0xFFFFD54F),
                        fontSize = 10.sp,
                        lineHeight = 13.sp
                    )
                }

                // Error message display
                if (errorText != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorText!!,
                        color = CrimsonRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        enabled = !isSubmitting,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) {
                        Text("CANCEL", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    if (hasEnoughBalance) {
                        Button(
                            onClick = {
                                if (inGameName.isBlank()) {
                                    errorText = "Please enter your In-Game Character Name"
                                    return@Button
                                }
                                isSubmitting = true
                                val result = EsportsRepository.joinMatchAtomic(match.id, inGameName)
                                isSubmitting = false
                                if (result.isSuccess) {
                                    val joinResult = result.getOrNull()!!
                                    // Trigger 2-minute reminder vibration simulation
                                    ReminderVibrationManager.triggerMatchStartingVibration(context)
                                    onJoinSuccess(joinResult)
                                } else {
                                    errorText = result.exceptionOrNull()?.message ?: "Failed to join match."
                                }
                            },
                            enabled = !isSubmitting,
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(44.dp)
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(color = DarkBg, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            } else {
                                Text("CONFIRM JOIN", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 12.sp)
                            }
                        }
                    } else {
                        Button(
                            onClick = {
                                onDismiss()
                                onNavigateWallet()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberGold),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(44.dp)
                        ) {
                            Icon(Icons.Default.AddCard, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ADD MONEY", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

/**
 * State-based Join Success Modal:
 * Displays celebratory confirmation with assigned Slot number, match time, fee, and [ VIEW MATCH ] action.
 */
@Composable
fun MatchJoinSuccessDialog(
    joinResult: JoinResult,
    onDismiss: () -> Unit,
    onViewMatch: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = DarkCard,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, EmeraldGreen.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Celebration Icon
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(EmeraldGreen.copy(alpha = 0.15f))
                        .border(2.dp, EmeraldGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = EmeraldGreen,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "🎉 MATCH JOINED SUCCESSFULLY!",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = joinResult.matchTitle,
                    color = CyberCyanLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Success Details Card
                Surface(
                    color = DarkElevated,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Entry Fee:", color = TextSecondary, fontSize = 12.sp)
                            Text("₹${"%.0f".format(joinResult.entryFee)}", color = CyberGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Match Time:", color = TextSecondary, fontSize = 12.sp)
                            Text(joinResult.startTime, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Assigned Slot:", color = TextSecondary, fontSize = 12.sp)
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldGreen.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen)
                            ) {
                                Text(
                                    text = "SLOT #${joinResult.slotNumber}",
                                    color = EmeraldGreen,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "You are registered for this match.",
                        color = EmeraldGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) {
                        Text("CLOSE", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            onDismiss()
                            onViewMatch()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp)
                    ) {
                        Text("VIEW MATCH", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
