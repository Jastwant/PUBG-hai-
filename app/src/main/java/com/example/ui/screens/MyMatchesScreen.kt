package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
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
import com.example.ui.components.GameIconResolver
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.util.ReminderVibrationManager

@Composable
fun MyMatchesScreen(
    onSelectMatch: (Match) -> Unit
) {
    val currentUser by EsportsRepository.currentUser.collectAsState()
    val matches by EsportsRepository.matches.collectAsState()
    val participants by EsportsRepository.participants.collectAsState()
    val matchResults by EsportsRepository.matchResults.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("ALL", "UPCOMING", "LIVE", "COMPLETED")

    // Filter matches user joined - "ALL" represents ALL matches joined by the user
    val myParticipants = participants.filter { it.userId == currentUser?.id }
    val myJoinedMatchIds = myParticipants.map { it.matchId }.toSet()
    val myMatches = matches.filter { it.id in myJoinedMatchIds }

    val filteredList = when (selectedTab) {
        0 -> myMatches // ALL user-joined matches
        1 -> myMatches.filter { !it.status.isLive && !it.status.isCompleted && !it.status.isCancelled }
        2 -> myMatches.filter { it.status.isLive || it.status == MatchStatus.RESULT_PENDING || it.status == MatchStatus.RESULT_UNDER_REVIEW || it.status == MatchStatus.RESULT_SUBMITTED }
        else -> myMatches.filter { it.status.isCompleted || it.status.isCancelled }
    }

    Scaffold(
        containerColor = DarkBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Selector
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkSurface,
                contentColor = CyberCyan,
                divider = { HorizontalDivider(color = DarkBorder) }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Black else FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = if (selectedTab == index) CyberCyan else TextSecondary
                            )
                        }
                    )
                }
            }

            // Match Items List
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "No ${tabs[selectedTab].lowercase()} matches",
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Join tournaments from Home or Games to compete and earn real cash winnings.",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList) { match ->
                        val result = matchResults.find { it.matchId == match.id }
                        val participant = myParticipants.find { it.matchId == match.id }
                        MyMatchCard(
                            match = match,
                            participant = participant,
                            result = result,
                            onClick = { onSelectMatch(match) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MyMatchCard(
    match: Match,
    participant: MatchParticipant?,
    result: MatchResult?,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by EsportsRepository.currentUser.collectAsState()
    val isLive = match.status.isLive
    val isCompleted = match.status.isCompleted
    val isCancelled = match.status.isCancelled

    fun triggerEnterMatch() {
        val (canEnter, reason) = EsportsRepository.canUserEnterMatch(currentUser?.id, match)
        if (!canEnter) {
            Toast.makeText(context, reason, Toast.LENGTH_LONG).show()
            return
        }

        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Match Room", "ID: ${match.roomId} Pass: ${match.roomPassword}")
        clipboard.setPrimaryClip(clip)
        ReminderVibrationManager.triggerMatchStartingVibration(context)

        val packageMap = mapOf(
            "game_bgmi" to "com.pubg.imobile",
            "game_ff" to "com.dts.freefireth",
            "game_ffmax" to "com.dts.freefiremax",
            "game_codm" to "com.activision.callofduty.shooter"
        )
        val packageName = packageMap[match.gameId]
        var launched = false
        if (packageName != null) {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                try {
                    context.startActivity(intent)
                    launched = true
                } catch (_: Exception) {}
            }
        }
        if (!launched) {
            Toast.makeText(
                context,
                "Room credentials copied! Open ${match.gameName} and join Room #${match.roomId}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Surface(
        color = DarkSurface,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isLive) CrimsonRed.copy(alpha = 0.6f) else DarkBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Status & Game Label
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GameIconResolver(gameId = match.gameId, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = match.gameName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Match ID: #${match.id.takeLast(8).uppercase()}",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                if (isLive) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = CrimsonRed.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonRed)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(CrimsonRed))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("LIVE", color = Color.White, fontWeight = FontWeight.Black, fontSize = 10.sp)
                        }
                    }
                } else if (isCompleted) {
                    StatusBadge(text = "COMPLETED", color = EmeraldGreen)
                } else if (match.status == MatchStatus.RESULT_PENDING || match.status == MatchStatus.RESULT_UNDER_REVIEW || match.status == MatchStatus.RESULT_SUBMITTED) {
                    StatusBadge(text = "UNDER REVIEW", color = RadiantOrange)
                } else if (isCancelled) {
                    StatusBadge(text = "CANCELLED", color = CrimsonRed)
                } else if (match.status == MatchStatus.ROOM_PREPARING) {
                    StatusBadge(text = "PREPARING ROOM", color = CyberGold)
                } else if (match.status == MatchStatus.ROOM_PUBLISHED || match.status == MatchStatus.READY) {
                    StatusBadge(text = "ROOM READY", color = CyberCyan)
                } else {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = EmeraldGreen.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "🟢 JOINED",
                            color = EmeraldGreen,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = match.title,
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Info Bar: Slot, IGN, Schedule, Prize
            Surface(
                color = DarkElevated,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("YOUR SLOT", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Slot #${participant?.slotNumber ?: 1}",
                            color = CyberCyanLight,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                        participant?.inGameName?.let { ign ->
                            Text(
                                text = "IGN: $ign",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("SCHEDULE", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${match.date} • ${match.startTime}",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("PRIZE POOL", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "₹${"%.0f".format(match.prizePool)}",
                            color = CyberGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Fee: ₹${"%.0f".format(match.entryFee)}",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Room Credentials Box (if published)
            if (match.roomId.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = CyberCyan.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.VpnKey, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ROOM READY", color = CyberCyan, fontWeight = FontWeight.Black, fontSize = 11.sp)
                            }
                            Text("ID: ${match.roomId}  |  Pass: ${match.roomPassword}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        IconButton(onClick = { triggerEnterMatch() }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = CyberCyan)
                        }
                    }
                }
            }

            // Results if completed
            if (result != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = EmeraldGreen.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("RANK #${result.rank} • ${result.kills} KILLS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Prize Won: ₹${"%.0f".format(result.totalPrizeWon)}", color = EmeraldGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = "+₹${"%.0f".format(result.totalPrizeWon)}",
                            color = CyberGold,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (match.roomId.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ready to Play", color = EmeraldGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Text("Room details 10m before start", color = TextMuted, fontSize = 11.sp)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (match.roomId.isNotBlank() || isLive) {
                        Button(
                            onClick = { triggerEnterMatch() },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.SportsEsports, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ENTER MATCH", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }
                    }

                    OutlinedButton(
                        onClick = onClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("DETAILS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
