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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.util.ReminderVibrationManager
import kotlinx.coroutines.delay

@Composable
fun MatchDetailScreen(
    matchId: String,
    onBack: () -> Unit,
    onNavigateWallet: () -> Unit
) {
    val context = LocalContext.current
    val matches by EsportsRepository.matches.collectAsState()
    val match = matches.find { it.id == matchId }
    val participants by EsportsRepository.participants.collectAsState()
    val matchParticipants = participants.filter { it.matchId == matchId }
    val currentUser by EsportsRepository.currentUser.collectAsState()
    val wallet by EsportsRepository.wallet.collectAsState()

    val myParticipant = matchParticipants.find { it.userId == currentUser?.id }
    val isUserJoined = myParticipant != null

    var showConfirmDialog by remember { mutableStateOf(false) }
    var joinResultSuccess by remember { mutableStateOf<JoinResult?>(null) }
    var showSubmitProofDialog by remember { mutableStateOf(false) }
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(match?.id) {
        while (true) {
            delay(1000)
            currentTime = System.currentTimeMillis()
            match?.let { EsportsRepository.checkAndTriggerReminders(context, it) }
        }
    }

    val remainingMillis = ((match?.startEpochMillis ?: 0L) - currentTime).coerceAtLeast(0L)
    val remainingMinutes = remainingMillis / (60 * 1000)
    val remainingSeconds = (remainingMillis / 1000) % 60
    val countdownText = String.format("%02d:%02d", remainingMinutes, remainingSeconds)

    fun triggerEnterMatch(m: Match) {
        val (canEnter, reason) = EsportsRepository.canUserEnterMatch(currentUser?.id, m)
        if (!canEnter) {
            Toast.makeText(context, reason, Toast.LENGTH_LONG).show()
            return
        }

        // Copy credentials to clipboard
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Match Credentials", "Room ID: ${m.roomId} | Password: ${m.roomPassword}")
        clipboard.setPrimaryClip(clip)

        // Trigger vibration
        ReminderVibrationManager.triggerMatchStartingVibration(context)

        // Attempt launch or give rich guidance
        val packageMap = mapOf(
            "game_bgmi" to "com.pubg.imobile",
            "game_ff" to "com.dts.freefireth",
            "game_ffmax" to "com.dts.freefiremax",
            "game_codm" to "com.activision.callofduty.shooter"
        )
        val packageName = packageMap[m.gameId]
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
                "🎮 Room ID: ${m.roomId} & Pass: ${m.roomPassword} copied!\nOpen ${m.gameName} -> Custom Room -> Enter Credentials",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    if (match == null) {
        Box(modifier = Modifier.fillMaxSize().background(DarkBg), contentAlignment = Alignment.Center) {
            Text("Match not found", color = Color.White)
        }
        return
    }

    Scaffold(
        topBar = {
            Surface(color = DarkSurface, tonalElevation = 4.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = match.title,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                    if (isUserJoined) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldGreen.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen)
                        ) {
                            Text(
                                text = "SLOT #${myParticipant?.slotNumber}",
                                color = EmeraldGreen,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("ENTRY FEE", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (match.entryFee == 0.0) "FREE" else "₹${"%.0f".format(match.entryFee)}",
                            color = CyberGold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    if (isUserJoined) {
                        if (match.status.isCompleted) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = CyberGold.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberGold.copy(alpha = 0.6f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = CyberGold, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("MATCH SETTLED", color = CyberGold, fontWeight = FontWeight.Black, fontSize = 12.sp)
                                }
                            }
                        } else if (match.status.isCancelled) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = CrimsonRed.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonRed.copy(alpha = 0.6f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Cancel, contentDescription = null, tint = CrimsonRed, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("CANCELLED • REFUNDED", color = CrimsonRed, fontWeight = FontWeight.Black, fontSize = 12.sp)
                                }
                            }
                        } else if (match.status.canSubmitResult) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { showSubmitProofDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = RadiantOrange),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.height(46.dp)
                                ) {
                                    Icon(Icons.Default.UploadFile, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("SUBMIT PROOF", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 12.sp)
                                }
                                if (match.status.isRoomAccessible || match.status.isLive) {
                                    OutlinedButton(
                                        onClick = { triggerEnterMatch(match) },
                                        shape = RoundedCornerShape(10.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan),
                                        modifier = Modifier.height(46.dp)
                                    ) {
                                        Text("ROOM", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        } else if (match.status.isRoomAccessible || match.status.isLive) {
                            Button(
                                onClick = { triggerEnterMatch(match) },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(46.dp)
                            ) {
                                Icon(Icons.Default.SportsEsports, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ENTER MATCH", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 13.sp)
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = EmeraldGreen.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("JOINED • SLOT #${myParticipant?.slotNumber}", color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    } else if (match.playersJoined >= match.maxSlots) {
                        Button(
                            onClick = {},
                            enabled = false,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(46.dp)
                        ) {
                            Text("MATCH FULL", color = TextMuted, fontWeight = FontWeight.Bold)
                        }
                    } else if (match.status == MatchStatus.REGISTRATION_OPEN) {
                        Button(
                            onClick = { showConfirmDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(46.dp)
                        ) {
                            Icon(Icons.Default.FlashOn, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("JOIN NOW", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 14.sp)
                        }
                    } else {
                        Button(
                            onClick = {},
                            enabled = false,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(46.dp)
                        ) {
                            Text(
                                text = when (match.status) {
                                    MatchStatus.REGISTRATION_CLOSED, MatchStatus.UPCOMING -> "REGISTRATION CLOSED"
                                    MatchStatus.ROOM_PREPARING -> "PREPARING ROOM"
                                    MatchStatus.ROOM_PUBLISHED, MatchStatus.READY -> "ROOM PUBLISHED"
                                    MatchStatus.LIVE, MatchStatus.ONGOING -> "MATCH IN PROGRESS"
                                    MatchStatus.RESULT_PENDING, MatchStatus.RESULT_UNDER_REVIEW, MatchStatus.RESULT_SUBMITTED -> "UNDER REVIEW"
                                    MatchStatus.COMPLETED, MatchStatus.SETTLED, MatchStatus.RESULT_VERIFIED -> "MATCH COMPLETED"
                                    MatchStatus.CANCELLED, MatchStatus.REFUND_PENDING, MatchStatus.REFUNDED -> "CANCELLED"
                                    else -> match.status.name
                                },
                                color = TextMuted,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        containerColor = DarkBg
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Status & Game Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusBadge(text = match.gameName.uppercase(), color = CyberCyan)
                    if (isUserJoined) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldGreen.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen)
                        ) {
                            Text(
                                text = "🟢 JOINED",
                                color = EmeraldGreen,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    } else {
                        StatusBadge(text = match.type.name, color = ElectricViolet)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = match.title,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Authoritative Match Lifecycle Status Card
            item {
                when (match.status) {
                    MatchStatus.REGISTRATION_OPEN -> {
                        Surface(
                            color = EmeraldGreen.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("🟢 REGISTRATION OPEN", color = EmeraldGreen, fontWeight = FontWeight.Black, fontSize = 12.sp)
                                    Text("Entry: ₹${"%.0f".format(match.entryFee)} • Slots: ${match.playersJoined}/${match.maxSlots} • Starts at ${match.startTime}", color = Color.White, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                    MatchStatus.REGISTRATION_CLOSED, MatchStatus.UPCOMING -> {
                        Surface(
                            color = RadiantOrange.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, RadiantOrange.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = RadiantOrange, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("🔒 REGISTRATION CLOSED", color = RadiantOrange, fontWeight = FontWeight.Black, fontSize = 12.sp)
                                    Text("Entry registration is closed. Match starts at ${match.startTime}", color = Color.White, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                    MatchStatus.ROOM_PREPARING -> {
                        Surface(
                            color = CyberGold.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberGold.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.HourglassTop, contentDescription = null, tint = CyberGold, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("⏳ ROOM DETAILS PREPARING", color = CyberGold, fontWeight = FontWeight.Black, fontSize = 12.sp)
                                    Text("Room details are being prepared. You will receive a notification when the room is available.", color = Color.White, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                    MatchStatus.ROOM_PUBLISHED, MatchStatus.READY -> {
                        Surface(
                            color = CyberCyan.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.VpnKey, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("🎮 ROOM DETAILS PUBLISHED", color = CyberCyan, fontWeight = FontWeight.Black, fontSize = 12.sp)
                                        Text("Starts in $countdownText • Join room promptly", color = Color.White, fontSize = 11.sp)
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = CyberCyan.copy(alpha = 0.2f)
                                ) {
                                    Text(countdownText, color = CyberCyan, fontWeight = FontWeight.Black, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                        }
                    }
                    MatchStatus.LIVE, MatchStatus.ONGOING -> {
                        Surface(
                            color = CrimsonRed.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonRed.copy(alpha = 0.7f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(CrimsonRed))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("🔴 MATCH LIVE", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
                                    Text("Match is currently in progress. Enter room immediately.", color = TextSecondary, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                    MatchStatus.RESULT_PENDING, MatchStatus.RESULT_SUBMITTED, MatchStatus.RESULT_UNDER_REVIEW -> {
                        Surface(
                            color = RadiantOrange.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, RadiantOrange.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.HourglassBottom, contentDescription = null, tint = RadiantOrange, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(if (match.status == MatchStatus.RESULT_UNDER_REVIEW) "🔍 RESULT UNDER REVIEW" else "⏳ RESULT PENDING", color = RadiantOrange, fontWeight = FontWeight.Black, fontSize = 12.sp)
                                        Text("Match concluded. Verify rankings and settle winnings.", color = Color.White, fontSize = 11.sp)
                                    }
                                }
                                if (isUserJoined) {
                                    Button(
                                        onClick = { showSubmitProofDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = RadiantOrange),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("SUBMIT PROOF", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                    MatchStatus.RESULT_VERIFIED, MatchStatus.SETTLED, MatchStatus.COMPLETED -> {
                        val result = EsportsRepository.matchResults.collectAsState().value.find { it.matchId == match.id }
                        Surface(
                            color = CyberGold.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberGold.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = CyberGold, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("🏆 RESULT VERIFIED & SETTLED", color = CyberGold, fontWeight = FontWeight.Black, fontSize = 12.sp)
                                    }
                                    StatusBadge(text = "PRIZE SETTLED", color = EmeraldGreen)
                                }
                                if (result != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Winner: ${result.winnerUsername} • Rank #${result.rank} • ${result.kills} Kills • Prize Won: ₹${"%.0f".format(result.totalPrizeWon)}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    MatchStatus.CANCELLED, MatchStatus.REFUND_PENDING, MatchStatus.REFUNDED -> {
                        Surface(
                            color = CrimsonRed.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonRed.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Cancel, contentDescription = null, tint = CrimsonRed, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("⚠️ MATCH CANCELLED", color = CrimsonRed, fontWeight = FontWeight.Black, fontSize = 12.sp)
                                    Text("Match has been cancelled. Entry fee (₹${"%.0f".format(match.entryFee)}) refunded to wallet.", color = Color.White, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                    else -> {}
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Stats Matrix Card
            item {
                Surface(
                    color = DarkSurface,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            StatBox("PRIZE POOL", "₹${"%.0f".format(match.prizePool)}", CyberGold)
                            StatBox("PER KILL", "₹${"%.0f".format(match.perKillReward)}", CyberCyan)
                            StatBox("MAP", match.mapName, Color.White)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = DarkBorder)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            StatBox("DATE", match.date, Color.White)
                            StatBox("START TIME", match.startTime, RadiantOrange)
                            StatBox("SLOTS", "${match.playersJoined} / ${match.maxSlots}", if (match.playersJoined >= match.maxSlots) CrimsonRed else EmeraldGreen)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Room Credentials Box (Protected: Visible to Joined Users Only)
            item {
                val isRoomAccessible = match.status.isRoomAccessible || match.status.isLive
                Surface(
                    color = if (isUserJoined && isRoomAccessible && match.roomId.isNotBlank()) DarkElevated else DarkSurface,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isUserJoined && isRoomAccessible && match.roomId.isNotBlank()) CyberCyan else DarkBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.VpnKey,
                                    contentDescription = null,
                                    tint = if (isUserJoined && isRoomAccessible && match.roomId.isNotBlank()) CyberCyan else TextMuted
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "ROOM ID & PASSWORD",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            if (!isUserJoined) {
                                StatusBadge(text = "LOCKED", color = CrimsonRed)
                            } else if (match.status == MatchStatus.ROOM_PREPARING) {
                                StatusBadge(text = "PREPARING", color = CyberGold)
                            } else if (isRoomAccessible && match.roomId.isNotBlank()) {
                                StatusBadge(text = "AVAILABLE", color = EmeraldGreen)
                            } else if (match.status.isCompleted) {
                                StatusBadge(text = "CLOSED", color = TextMuted)
                            } else {
                                StatusBadge(text = "REVEALS 10M BEFORE", color = CyberGold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (!isUserJoined) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkBg)
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = CyberGold, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Room credentials are protected. You must join this match to unlock room ID & password.",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        } else if (match.status == MatchStatus.ROOM_PREPARING) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkBg)
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.HourglassTop, contentDescription = null, tint = CyberGold, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "⏳ Room details are being prepared. You will receive a notification when the room is available.",
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                        } else if (isRoomAccessible && match.roomId.isNotBlank()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkBg)
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("ROOM ID: ${match.roomId}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 15.sp)
                                    Text("PASSWORD: ${match.roomPassword}", color = CyberCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                IconButton(onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Room Info", "ID: ${match.roomId} Pass: ${match.roomPassword}")
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Room credentials copied!", Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = CyberCyan)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { triggerEnterMatch(match) },
                                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().height(42.dp)
                            ) {
                                Icon(Icons.Default.SportsEsports, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ENTER MATCH NOW", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 13.sp)
                            }
                        } else if (match.status.isCompleted) {
                            Text(
                                text = "Match has ended. Results are recorded below.",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        } else {
                            Text(
                                text = "⏳ Room details will be available shortly (10 minutes before kickoff). Please stay ready.",
                                color = CyberGold,
                                fontSize = 12.sp
                            )
                        }

                        if (match.roomDescription.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Note: ${match.roomDescription}",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Spectate Live Stream Section
            if (match.spectateUrl.isNotBlank()) {
                item {
                    Surface(
                        color = Color(0xFF280B0B),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonRed.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(CrimsonRed),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.LiveTv, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("OFFICIAL LIVE STREAM", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Watch caster spectate & highlights", color = TextSecondary, fontSize = 11.sp)
                                }
                            }
                            Button(
                                onClick = {
                                    Toast.makeText(context, "Opening stream: ${match.spectateUrl}", Toast.LENGTH_LONG).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text("WATCH LIVE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Participants List
            item {
                Text(
                    text = "REGISTERED PLAYERS (${matchParticipants.size})",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (matchParticipants.isEmpty()) {
                item {
                    Surface(
                        color = DarkSurface,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "No players registered yet. Be the first to join!",
                            color = TextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(matchParticipants.size) { index ->
                    val p = matchParticipants[index]
                    val isMe = p.userId == currentUser?.id
                    Surface(
                        color = if (isMe) CyberCyan.copy(alpha = 0.1f) else DarkSurface,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isMe) CyberCyan.copy(alpha = 0.5f) else DarkBorderSubtle
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("#${p.slotNumber}", color = CyberGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(p.inGameName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        if (isMe) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = CyberCyan.copy(alpha = 0.2f)
                                            ) {
                                                Text("YOU", color = CyberCyan, fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                            }
                                        }
                                    }
                                    Text("@${p.username}", color = TextMuted, fontSize = 10.sp)
                                }
                            }
                            StatusBadge(text = p.status.name, color = if (p.status == ParticipationStatus.WON) CyberGold else EmeraldGreen)
                        }
                    }
                }
            }
        }

        // Join Match Confirmation Dialog
        if (showConfirmDialog) {
            MatchJoinConfirmationDialog(
                match = match,
                wallet = wallet,
                onDismiss = { showConfirmDialog = false },
                onNavigateWallet = {
                    showConfirmDialog = false
                    onNavigateWallet()
                },
                onJoinSuccess = { result ->
                    showConfirmDialog = false
                    joinResultSuccess = result
                }
            )
        }

        // Join Match Success Dialog
        joinResultSuccess?.let { result ->
            MatchJoinSuccessDialog(
                joinResult = result,
                onDismiss = { joinResultSuccess = null },
                onViewMatch = { joinResultSuccess = null }
            )
        }

        // Result Proof Submission Dialog
        if (showSubmitProofDialog) {
            SubmitResultProofDialog(
                match = match,
                participant = myParticipant,
                onDismiss = { showSubmitProofDialog = false },
                onSubmitSuccess = {
                    showSubmitProofDialog = false
                }
            )
        }
    }
}

@Composable
fun SubmitResultProofDialog(
    match: Match,
    participant: MatchParticipant?,
    onDismiss: () -> Unit,
    onSubmitSuccess: () -> Unit
) {
    val context = LocalContext.current
    var rankInput by remember { mutableStateOf(if (participant?.rank != null && participant.rank > 0) participant.rank.toString() else "1") }
    var killsInput by remember { mutableStateOf(if (participant?.kills != null && participant.kills > 0) participant.kills.toString() else "5") }
    var notesInput by remember { mutableStateOf("") }
    var proofSelected by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkElevated,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.UploadFile, contentDescription = null, tint = RadiantOrange)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Submit Result Proof", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Upload your final placement & kill count screenshot. Admins will verify against game room logs before prize settlement.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = rankInput,
                    onValueChange = { rankInput = it.filter { ch -> ch.isDigit() }.take(3) },
                    label = { Text("Rank / Placement (#)", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = DarkBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = killsInput,
                    onValueChange = { killsInput = it.filter { ch -> ch.isDigit() }.take(3) },
                    label = { Text("Total Kills Claimed", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = DarkBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (proofSelected) EmeraldGreen else DarkBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            proofSelected = true
                            Toast.makeText(context, "Proof image match_victory_proof.png selected", Toast.LENGTH_SHORT).show()
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Image, contentDescription = null, tint = if (proofSelected) EmeraldGreen else CyberCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (proofSelected) "victory_screenshot.png" else "Attach Victory Proof",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = if (proofSelected) "Scoreboard verified (1.2 MB)" else "Select screenshot from gallery",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }
                        if (proofSelected) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(18.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notesInput,
                    onValueChange = { notesInput = it.take(120) },
                    label = { Text("Notes (optional)", color = TextMuted) },
                    maxLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = DarkBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val r = rankInput.toIntOrNull() ?: 1
                    val k = killsInput.toIntOrNull() ?: 0
                    val res = EsportsRepository.submitMatchResultProof(
                        matchId = match.id,
                        rank = r,
                        kills = k,
                        screenshotUrl = "https://apexzone.gg/proofs/match_${match.id}_${System.currentTimeMillis()}.png",
                        notes = notesInput
                    )
                    if (res.isSuccess) {
                        Toast.makeText(context, res.getOrNull(), Toast.LENGTH_LONG).show()
                        onSubmitSuccess()
                    } else {
                        Toast.makeText(context, res.exceptionOrNull()?.message ?: "Submission failed", Toast.LENGTH_LONG).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RadiantOrange),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("SUBMIT PROOF", color = Color.Black, fontWeight = FontWeight.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = TextMuted)
            }
        }
    )
}

@Composable
fun StatBox(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(value, color = color, fontSize = 13.sp, fontWeight = FontWeight.Black)
    }
}
