package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.models.*
import com.example.data.repository.EsportsRepository
import com.example.security.DeviceSecurityManager
import com.example.ui.components.NeonGradientButton
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun ProfileSettingsScreen(
    onNavigateAdmin: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by EsportsRepository.currentUser.collectAsState()
    val wallet by EsportsRepository.wallet.collectAsState()
    val sessions by EsportsRepository.userSessions.collectAsState()
    val devices by EsportsRepository.userDevices.collectAsState()
    val gameProfiles by EsportsRepository.userGameProfiles.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showSessionsDialog by remember { mutableStateOf(false) }
    var showLinkUidDialog by remember { mutableStateOf(false) }

    var editFullName by remember { mutableStateOf(currentUser?.fullName ?: "") }
    var selectedGameToLink by remember { mutableStateOf("game_bgmi") }
    var linkPlayerUid by remember { mutableStateOf("") }
    var linkPlayerIgn by remember { mutableStateOf("") }

    var appLanguage by remember { mutableStateOf("English (Default)") }
    var notificationsEnabled by remember { mutableStateOf(true) }

    val mySessions = remember(sessions, currentUser) {
        sessions.filter { it.userId == currentUser?.id }
    }

    val isDevOptionsOn = remember { DeviceSecurityManager.isDeveloperOptionsEnabled(context) }
    val isUsbDebugOn = remember { DeviceSecurityManager.isUsbDebuggingEnabled(context) }
    val integrityVerdict by DeviceSecurityManager.integrityVerdict.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile Header Card
        item {
            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(ElectricViolet.copy(alpha = 0.2f))
                                    .border(2.dp, CyberCyan, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(30.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(currentUser?.fullName ?: "Apex Player", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                                Text("@${currentUser?.username ?: "gamer"}", color = CyberCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(currentUser?.email ?: "", color = TextSecondary, fontSize = 11.sp)
                            }
                        }
                        IconButton(onClick = {
                            editFullName = currentUser?.fullName ?: ""
                            showEditProfileDialog = true
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = CyberCyan)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = DarkBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("ACCOUNT STATUS", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            StatusBadge(
                                text = currentUser?.status?.name ?: "ACTIVE",
                                color = when (currentUser?.status) {
                                    AccountStatus.ACTIVE -> EmeraldGreen
                                    AccountStatus.PENDING_VERIFICATION -> CyberGold
                                    else -> CrimsonRed
                                }
                            )
                        }
                        Column {
                            Text("KYC STATUS", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            StatusBadge(text = if (currentUser?.isKycVerified == true) "VERIFIED" else "PENDING", color = if (currentUser?.isKycVerified == true) EmeraldGreen else CyberGold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("POINTS", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text("${wallet.points} pts", color = CyberGold, fontWeight = FontWeight.Black, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // DEVICE SECURITY & ANTI-CHEAT STATUS
        item {
            Text("DEVICE SECURITY & ANTI-CHEAT INTEGRITY", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        item {
            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = EmeraldGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Device Security Policy", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Single account lock active", color = TextSecondary, fontSize = 11.sp)
                            }
                        }
                        StatusBadge("PLAY VERIFIED", EmeraldGreen)
                    }

                    HorizontalDivider(color = DarkBorder)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Developer Options Check", color = TextSecondary, fontSize = 12.sp)
                            Text(
                                text = if (isDevOptionsOn) "ENABLED (Blocked in Strict)" else "OFF (Secure)",
                                color = if (isDevOptionsOn) CrimsonRed else EmeraldGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Button(
                            onClick = { DeviceSecurityManager.openDeveloperSettings(context) },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkElevated),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Android Settings", fontSize = 10.sp, color = CyberCyan)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Active Device Sessions", color = TextSecondary, fontSize = 12.sp)
                            Text("${mySessions.count { it.status == SessionStatus.ACTIVE }} Active Sessions", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { showSessionsDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan.copy(alpha = 0.15f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Manage Sessions", fontSize = 10.sp, color = CyberCyan, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Linked Game Profiles & Unique UIDs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("LINKED GAME PROFILES & UIDS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                TextButton(onClick = { showLinkUidDialog = true }) {
                    Text("+ Link UID", color = CyberCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val myProfiles = gameProfiles.filter { it.userId == currentUser?.id }
                    if (myProfiles.isEmpty()) {
                        Text("No official game UIDs linked yet. Link your UID to join tournament rooms.", color = TextMuted, fontSize = 12.sp)
                    } else {
                        myProfiles.forEachIndexed { index, profile ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(profile.gameName, color = TextSecondary, fontSize = 11.sp)
                                    Text("${profile.playerName} (UID: ${profile.playerUid})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                                StatusBadge("1-ACCOUNT VERIFIED", EmeraldGreen)
                            }
                            if (index < myProfiles.size - 1) {
                                HorizontalDivider(color = DarkBorder.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }
        }

        // Referral Card
        item {
            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberGold.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = CyberGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("REFER & EARN ₹25 / FRIEND", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                        }
                        StatusBadge("UNLIMITED", CyberGold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Share your exclusive referral code. When your friend signs up and plays their first match, you get ₹25 cash directly credited!",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkElevated)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CODE: ${currentUser?.referralCode ?: "APEX998"}",
                            color = CyberGold,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            letterSpacing = 1.sp
                        )
                        IconButton(onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Referral Code", currentUser?.referralCode ?: "APEX998")
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Referral Code Copied!", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = CyberCyan)
                        }
                    }
                }
            }
        }

        // App Settings & Preferences
        item {
            Text("PREFERENCES & SUPPORT", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        item {
            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Push Notifications", color = Color.White, fontSize = 13.sp)
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyberCyan)
                        )
                    }

                    HorizontalDivider(color = DarkBorder.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier.fillMaxWidth().clickable {
                            Toast.makeText(context, "Support Desk: support@apexzone.gg or Telegram @ApexSupport", Toast.LENGTH_LONG).show()
                        },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("24x7 Customer Support Desk", color = Color.White, fontSize = 13.sp)
                        Icon(Icons.Default.HeadsetMic, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        // Admin Portal Entry Button
        item {
            Button(
                onClick = onNavigateAdmin,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = CyberCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("OPEN ADMIN PORTAL & SECURITY HUB", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
            }
        }

        // Logout
        item {
            OutlinedButton(
                onClick = onLogout,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonRed.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = CrimsonRed)
                Spacer(modifier = Modifier.width(8.dp))
                Text("LOGOUT ACCOUNT", color = CrimsonRed, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }

    // Modal: Active Sessions Management
    if (showSessionsDialog) {
        AlertDialog(
            onDismissRequest = { showSessionsDialog = false },
            containerColor = DarkCard,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Devices, contentDescription = null, tint = CyberCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Active Device Sessions", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Sessions authorized for your account. Terminate suspicious or old sessions immediately.", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    mySessions.forEach { sess ->
                        Surface(
                            color = DarkElevated,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (sess.isCurrentSession) CyberCyan.copy(alpha = 0.4f) else DarkBorderSubtle),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(sess.deviceName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        if (sess.isCurrentSession) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            StatusBadge("THIS DEVICE", CyberCyan)
                                        }
                                    }
                                    Text("IP: ${sess.ipHash.take(16)} • OS: ${sess.osVersion}", color = TextMuted, fontSize = 10.sp)
                                    Text("Status: ${sess.status.name}", color = if (sess.status == SessionStatus.ACTIVE) EmeraldGreen else CrimsonRed, fontSize = 10.sp)
                                }
                                if (sess.status == SessionStatus.ACTIVE && !sess.isCurrentSession) {
                                    IconButton(onClick = {
                                        EsportsRepository.revokeSession(sess.sessionId)
                                        Toast.makeText(context, "Session revoked", Toast.LENGTH_SHORT).show()
                                    }) {
                                        Icon(Icons.Default.DeleteOutline, contentDescription = "Revoke", tint = CrimsonRed)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = {
                            EsportsRepository.logoutAllDevices()
                            Toast.makeText(context, "Logged out of all other devices", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("LOGOUT ALL OTHER DEVICES", color = CrimsonRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSessionsDialog = false }) {
                    Text("Close", color = CyberCyan)
                }
            }
        )
    }

    // Modal: Link Game UID
    if (showLinkUidDialog) {
        AlertDialog(
            onDismissRequest = { showLinkUidDialog = false },
            containerColor = DarkCard,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.SportsEsports, contentDescription = null, tint = CyberCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Link In-Game UID", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column {
                    Text(
                        "Policy: 1 Game UID can only be linked to 1 account. Duplicate linking is blocked by anti-cheat.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("game_bgmi" to "BGMI", "game_ff" to "Free Fire", "game_codm" to "COD:M").forEach { (gid, label) ->
                            Button(
                                onClick = { selectedGameToLink = gid },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedGameToLink == gid) CyberCyan else DarkSurface
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(label, fontSize = 11.sp, color = if (selectedGameToLink == gid) Color.Black else Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = linkPlayerUid,
                        onValueChange = { linkPlayerUid = it },
                        label = { Text("Game UID (Numbers)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = linkPlayerIgn,
                        onValueChange = { linkPlayerIgn = it },
                        label = { Text("Exact In-Game Name (IGN)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val userId = currentUser?.id ?: return@Button
                        val res = EsportsRepository.linkGameProfile(userId, selectedGameToLink, linkPlayerUid, linkPlayerIgn)
                        if (res.isSuccess) {
                            Toast.makeText(context, "Game UID linked successfully!", Toast.LENGTH_SHORT).show()
                            showLinkUidDialog = false
                            linkPlayerUid = ""
                            linkPlayerIgn = ""
                        } else {
                            Toast.makeText(context, res.exceptionOrNull()?.message ?: "Failed to link UID", Toast.LENGTH_LONG).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                ) {
                    Text("VERIFY & LINK", color = Color.Black, fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLinkUidDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // Modal: Edit Profile
    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            containerColor = DarkCard,
            title = { Text("Edit Gamer Profile", color = Color.White, fontWeight = FontWeight.Black) },
            text = {
                Column {
                    OutlinedTextField(
                        value = editFullName,
                        onValueChange = { editFullName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        EsportsRepository.updateProfile(fullName = editFullName, ignMap = currentUser?.inGameNames ?: emptyMap())
                        showEditProfileDialog = false
                        Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                ) {
                    Text("SAVE", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) { Text("Cancel", color = TextSecondary) }
            }
        )
    }
}

@Composable
fun IgnRow(game: String, ign: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(game, color = TextSecondary, fontSize = 12.sp)
        Text(ign, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}
