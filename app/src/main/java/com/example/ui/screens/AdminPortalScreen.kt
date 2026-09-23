package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.*
import com.example.data.repository.EsportsRepository
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun AdminPortalScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val matches by EsportsRepository.matches.collectAsState()
    val tournaments by EsportsRepository.tournaments.collectAsState()
    val withdrawals by EsportsRepository.withdrawals.collectAsState()
    val allUsers by EsportsRepository.allUsers.collectAsState()
    val challenges by EsportsRepository.challenges.collectAsState()
    val games by EsportsRepository.games.collectAsState()
    val auditLogs by EsportsRepository.auditLogs.collectAsState()
    val appConfig by EsportsRepository.appConfig.collectAsState()
    val wallet by EsportsRepository.wallet.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        "OPERATIONS",
        "MATCHES",
        "WITHDRAWALS",
        "PLAYERS & KYC",
        "1V1 DISPUTES",
        "CONFIG & BROADCAST",
        "AUDIT TRAIL"
    )

    // Dialog state controllers
    var showCreateMatchDialog by remember { mutableStateOf(false) }
    var showRoomDialog by remember { mutableStateOf(false) }
    var selectedMatchForRoom by remember { mutableStateOf<Match?>(null) }
    var inputRoomId by remember { mutableStateOf("") }
    var inputRoomPass by remember { mutableStateOf("") }
    var inputRoomDesc by remember { mutableStateOf("") }

    var showResultDialog by remember { mutableStateOf(false) }
    var selectedMatchForResult by remember { mutableStateOf<Match?>(null) }
    var winnerUsername by remember { mutableStateOf("ApexStriker") }
    var winnerIgn by remember { mutableStateOf("APEX_Striker") }
    var winnerRank by remember { mutableStateOf("1") }
    var winnerKills by remember { mutableStateOf("6") }
    var winnerPrize by remember { mutableStateOf("1500") }

    var showCancelMatchDialog by remember { mutableStateOf(false) }
    var selectedMatchForCancel by remember { mutableStateOf<Match?>(null) }
    var cancelReasonText by remember { mutableStateOf("Game server maintenance") }

    var showBalanceAdjustDialog by remember { mutableStateOf(false) }
    var adjustAmountText by remember { mutableStateOf("100") }
    var adjustReasonText by remember { mutableStateOf("Promotional tournament bonus credit") }

    var showBroadcastDialog by remember { mutableStateOf(false) }
    var broadcastTitle by remember { mutableStateOf("") }
    var broadcastDesc by remember { mutableStateOf("") }

    var showDisputeDialog by remember { mutableStateOf(false) }
    var selectedChallengeForDispute by remember { mutableStateOf<PvPChallenge?>(null) }
    var disputeWinnerCreator by remember { mutableStateOf(true) }
    var disputeReason by remember { mutableStateOf("Verified match proof screenshot") }

    Scaffold(
        topBar = {
            Surface(
                color = DarkBg,
                modifier = Modifier.fillMaxWidth().border(1.dp, DarkBorderSubtle)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(DarkSurface)
                                .border(1.dp, DarkBorderSubtle, CircleShape)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Exit Admin", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (appConfig.isMaintenanceMode) RadiantOrange else EmeraldGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (appConfig.isMaintenanceMode) "MAINTENANCE MODE" else "SYSTEM LIVE",
                                    color = if (appConfig.isMaintenanceMode) RadiantOrange else EmeraldGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                            }
                            Text(
                                text = "ADMIN COMMAND CENTER",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = CyberCyan.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "ROOT ACCESS",
                            color = CyberCyanLight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        },
        containerColor = DarkBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Scrollable Tab Row
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkSurface,
                contentColor = CyberCyan,
                edgePadding = 12.dp,
                divider = { HorizontalDivider(color = DarkBorderSubtle) }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Black else FontWeight.Bold,
                                fontSize = 11.sp,
                                color = if (selectedTab == index) CyberCyanLight else TextMuted
                            )
                        }
                    )
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> {
                    // OPERATIONS DASHBOARD
                    AdminOperationsHub(
                        matches = matches,
                        withdrawals = withdrawals,
                        usersCount = allUsers.size,
                        challenges = challenges,
                        isMaintenance = appConfig.isMaintenanceMode,
                        onCreateMatchClick = { showCreateMatchDialog = true },
                        onBroadcastClick = { showBroadcastDialog = true },
                        onBalanceAdjustClick = { showBalanceAdjustDialog = true },
                        onToggleMaintenance = { EsportsRepository.toggleMaintenanceMode(!appConfig.isMaintenanceMode) },
                        onNavigateTab = { selectedTab = it }
                    )
                }

                1 -> {
                    // MATCHES & TOURNAMENTS
                    AdminMatchesTab(
                        matches = matches,
                        onCreateMatch = { showCreateMatchDialog = true },
                        onSetRoom = { match ->
                            selectedMatchForRoom = match
                            inputRoomId = match.roomId
                            inputRoomPass = match.roomPassword
                            inputRoomDesc = match.roomDescription
                            showRoomDialog = true
                        },
                        onDeclareResult = { match ->
                            selectedMatchForResult = match
                            showResultDialog = true
                        },
                        onCancelMatch = { match ->
                            selectedMatchForCancel = match
                            showCancelMatchDialog = true
                        },
                        onAdvanceStatus = { match, nextStatus ->
                            val res = EsportsRepository.advanceMatchStatus(match.id, nextStatus)
                            if (res.isSuccess) {
                                Toast.makeText(context, "Match status updated to ${nextStatus.name}", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, res.exceptionOrNull()?.message ?: "Failed to update status", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }

                2 -> {
                    // WITHDRAWALS & LEDGER
                    AdminWithdrawalsTab(
                        withdrawals = withdrawals,
                        onApprove = { req ->
                            EsportsRepository.adminProcessWithdrawal(req.id, true, "Instant UPI IMPS Settlement Successful")
                            Toast.makeText(context, "Withdrawal Approved & Settled!", Toast.LENGTH_SHORT).show()
                        },
                        onReject = { req ->
                            EsportsRepository.adminProcessWithdrawal(req.id, false, "Verification Failed / Invalid UPI ID")
                            Toast.makeText(context, "Withdrawal Rejected & Refunded.", Toast.LENGTH_SHORT).show()
                        },
                        onManualAdjust = { showBalanceAdjustDialog = true }
                    )
                }

                3 -> {
                    // PLAYERS & KYC
                    AdminPlayersTab(
                        users = allUsers,
                        onToggleKyc = { user ->
                            EsportsRepository.adminToggleUserKyc(user.id)
                            Toast.makeText(context, "KYC status updated for ${user.username}", Toast.LENGTH_SHORT).show()
                        },
                        onToggleBan = { user ->
                            EsportsRepository.adminToggleBanUser(user.id)
                            Toast.makeText(context, "Access status updated for ${user.username}", Toast.LENGTH_SHORT).show()
                        },
                        onToggleAdmin = { user ->
                            EsportsRepository.adminToggleUserAdminRole(user.id)
                            Toast.makeText(context, "Role updated for ${user.username}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                4 -> {
                    // 1V1 CHALLENGES & DISPUTES
                    AdminDisputesTab(
                        challenges = challenges,
                        onResolve = { challenge ->
                            selectedChallengeForDispute = challenge
                            showDisputeDialog = true
                        }
                    )
                }

                5 -> {
                    // CONFIG & BROADCAST
                    AdminConfigTab(
                        config = appConfig,
                        games = games,
                        onToggleGame = { game ->
                            EsportsRepository.adminToggleGameStatus(game.id)
                            Toast.makeText(context, "Game status updated for ${game.name}", Toast.LENGTH_SHORT).show()
                        },
                        onBroadcast = { showBroadcastDialog = true },
                        onUpdateConfig = { newConfig ->
                            EsportsRepository.adminUpdateAppConfig(newConfig)
                            Toast.makeText(context, "System configuration saved.", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                else -> {
                    // AUDIT TRAIL
                    AdminAuditTrailTab(auditLogs = auditLogs)
                }
            }
        }

        // --- DIALOGS ---

        // 1. Create Match Dialog
        if (showCreateMatchDialog) {
            AdminCreateMatchDialog(
                games = games.filter { it.active },
                onDismiss = { showCreateMatchDialog = false },
                onCreate = { gameId, title, map, type, entry, killBounty, prize, slots, time ->
                    EsportsRepository.adminCreateMatch(
                        gameId = gameId,
                        title = title,
                        mapName = map,
                        type = type,
                        entryFee = entry,
                        perKillReward = killBounty,
                        prizePool = prize,
                        maxSlots = slots,
                        startTime = time
                    )
                    showCreateMatchDialog = false
                    Toast.makeText(context, "Tournament Match Created Successfully!", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // 2. Room Info Dialog
        if (showRoomDialog && selectedMatchForRoom != null) {
            AlertDialog(
                onDismissRequest = { showRoomDialog = false },
                containerColor = DarkCard,
                title = {
                    Text("Publish Custom Room Info", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Match: ${selectedMatchForRoom!!.title}", color = CyberCyanLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = inputRoomId,
                            onValueChange = { inputRoomId = it },
                            label = { Text("Room ID / Match Code") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = inputRoomPass,
                            onValueChange = { inputRoomPass = it },
                            label = { Text("Room Password") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = inputRoomDesc,
                            onValueChange = { inputRoomDesc = it },
                            label = { Text("Slot Instructions / Notes") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            EsportsRepository.adminUpdateRoomInfo(
                                selectedMatchForRoom!!.id,
                                inputRoomId,
                                inputRoomPass,
                                inputRoomDesc
                            )
                            EsportsRepository.advanceMatchStatus(
                                selectedMatchForRoom!!.id,
                                MatchStatus.ROOM_PUBLISHED
                            )
                            showRoomDialog = false
                            Toast.makeText(context, "Room credentials published & match status set to ROOM_PUBLISHED!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                    ) {
                        Text("BROADCAST ROOM", color = DarkBg, fontWeight = FontWeight.Black)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRoomDialog = false }) { Text("Cancel", color = TextSecondary) }
                }
            )
        }

        // 3. Declare Result Dialog
        if (showResultDialog && selectedMatchForResult != null) {
            AlertDialog(
                onDismissRequest = { showResultDialog = false },
                containerColor = DarkCard,
                title = { Text("Declare Match Results", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Match: ${selectedMatchForResult!!.title}", color = CyberCyanLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = winnerUsername,
                            onValueChange = { winnerUsername = it },
                            label = { Text("Winner Username") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = winnerIgn,
                            onValueChange = { winnerIgn = it },
                            label = { Text("Winner In-Game Name (IGN)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = winnerRank,
                                onValueChange = { winnerRank = it },
                                label = { Text("Rank #") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = winnerKills,
                                onValueChange = { winnerKills = it },
                                label = { Text("Kills") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        OutlinedTextField(
                            value = winnerPrize,
                            onValueChange = { winnerPrize = it },
                            label = { Text("Prize Amount (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val kills = winnerKills.toIntOrNull() ?: 0
                            val prize = winnerPrize.toDoubleOrNull() ?: 0.0
                            val rank = winnerRank.toIntOrNull() ?: 1
                            val res = EsportsRepository.adminVerifyAndSettleResult(
                                selectedMatchForResult!!.id,
                                winnerUsername,
                                winnerIgn,
                                rank,
                                kills,
                                prize
                            )
                            showResultDialog = false
                            if (res.isSuccess) {
                                Toast.makeText(context, res.getOrNull(), Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, res.exceptionOrNull()?.message ?: "Settlement failed", Toast.LENGTH_LONG).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                    ) {
                        Text("VERIFY & SETTLE PRIZE", color = Color.White, fontWeight = FontWeight.Black)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResultDialog = false }) { Text("Cancel", color = TextSecondary) }
                }
            )
        }

        // 4. Cancel Match Dialog
        if (showCancelMatchDialog && selectedMatchForCancel != null) {
            AlertDialog(
                onDismissRequest = { showCancelMatchDialog = false },
                containerColor = DarkCard,
                title = { Text("Cancel Match & Auto-Refund", color = CrimsonRed, fontWeight = FontWeight.Black, fontSize = 16.sp) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Match: ${selectedMatchForCancel!!.title}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("Cancelling will refund entry fees (₹${selectedMatchForCancel!!.entryFee}) to all joined players.", color = TextSecondary, fontSize = 12.sp)
                        OutlinedTextField(
                            value = cancelReasonText,
                            onValueChange = { cancelReasonText = it },
                            label = { Text("Cancellation Reason") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            EsportsRepository.adminCancelMatch(selectedMatchForCancel!!.id, cancelReasonText)
                            showCancelMatchDialog = false
                            Toast.makeText(context, "Match cancelled and entry fees refunded!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                    ) {
                        Text("CONFIRM CANCEL & REFUND", color = Color.White, fontWeight = FontWeight.Black)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCancelMatchDialog = false }) { Text("Close", color = TextSecondary) }
                }
            )
        }

        // 5. Manual Balance Adjustment Dialog
        if (showBalanceAdjustDialog) {
            AlertDialog(
                onDismissRequest = { showBalanceAdjustDialog = false },
                containerColor = DarkCard,
                title = { Text("Manual Wallet Adjustment", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Add or deduct funds with an audited entry.", color = TextSecondary, fontSize = 12.sp)
                        OutlinedTextField(
                            value = adjustAmountText,
                            onValueChange = { adjustAmountText = it },
                            label = { Text("Amount (₹) [Use - for debit]") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = adjustReasonText,
                            onValueChange = { adjustReasonText = it },
                            label = { Text("Reason / Remarks for Audit") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val amt = adjustAmountText.toDoubleOrNull() ?: 0.0
                            EsportsRepository.adminManualWalletAdjustment(amt, adjustReasonText)
                            showBalanceAdjustDialog = false
                            Toast.makeText(context, "Ledger entry updated & audited.", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                    ) {
                        Text("APPLY TO LEDGER", color = DarkBg, fontWeight = FontWeight.Black)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showBalanceAdjustDialog = false }) { Text("Cancel", color = TextSecondary) }
                }
            )
        }

        // 6. Broadcast Announcement Dialog
        if (showBroadcastDialog) {
            AlertDialog(
                onDismissRequest = { showBroadcastDialog = false },
                containerColor = DarkCard,
                title = { Text("Broadcast Push Announcement", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("This will send an instant announcement & push alert to all players.", color = TextSecondary, fontSize = 12.sp)
                        OutlinedTextField(
                            value = broadcastTitle,
                            onValueChange = { broadcastTitle = it },
                            label = { Text("Headline / Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = broadcastDesc,
                            onValueChange = { broadcastDesc = it },
                            label = { Text("Announcement Message") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (broadcastTitle.isNotBlank()) {
                                EsportsRepository.adminBroadcastAnnouncement(broadcastTitle, broadcastDesc, 2)
                                showBroadcastDialog = false
                                Toast.makeText(context, "Broadcast sent to all active users!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                    ) {
                        Text("SEND BROADCAST", color = DarkBg, fontWeight = FontWeight.Black)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showBroadcastDialog = false }) { Text("Cancel", color = TextSecondary) }
                }
            )
        }

        // 7. Resolve Dispute Dialog
        if (showDisputeDialog && selectedChallengeForDispute != null) {
            val chal = selectedChallengeForDispute!!
            AlertDialog(
                onDismissRequest = { showDisputeDialog = false },
                containerColor = DarkCard,
                title = { Text("Resolve 1v1 PvP Dispute", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Game: ${chal.gameName} • Prize: ₹${chal.prizeAmount}", color = CyberCyanLight, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Select Winner:", color = TextSecondary, fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = disputeWinnerCreator,
                                onClick = { disputeWinnerCreator = true },
                                label = { Text("Creator: ${chal.creatorUsername}") }
                            )
                            FilterChip(
                                selected = !disputeWinnerCreator,
                                onClick = { disputeWinnerCreator = false },
                                label = { Text("Opponent: ${chal.opponentUsername.ifBlank { "Opponent" }}") }
                            )
                        }
                        OutlinedTextField(
                            value = disputeReason,
                            onValueChange = { disputeReason = it },
                            label = { Text("Decision / Verification Reason") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            EsportsRepository.adminResolveChallenge(chal.id, disputeWinnerCreator, disputeReason)
                            showDisputeDialog = false
                            Toast.makeText(context, "Dispute resolved and prize credited!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                    ) {
                        Text("CONFIRM RESOLUTION", color = Color.White, fontWeight = FontWeight.Black)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDisputeDialog = false }) { Text("Cancel", color = TextSecondary) }
                }
            )
        }
    }
}

// -------------------------------------------------------------
// TAB 1: OPERATIONS HUB
// -------------------------------------------------------------
@Composable
private fun AdminOperationsHub(
    matches: List<Match>,
    withdrawals: List<WithdrawalRequest>,
    usersCount: Int,
    challenges: List<PvPChallenge>,
    isMaintenance: Boolean,
    onCreateMatchClick: () -> Unit,
    onBroadcastClick: () -> Unit,
    onBalanceAdjustClick: () -> Unit,
    onToggleMaintenance: () -> Unit,
    onNavigateTab: (Int) -> Unit
) {
    val pendingWithdrawals = withdrawals.filter { it.status == WithdrawalStatus.PENDING }
    val totalPendingAmount = pendingWithdrawals.sumOf { it.amount }
    val activeMatches = matches.filter { it.status == MatchStatus.ONGOING || it.status == MatchStatus.REGISTRATION_OPEN }
    val totalPrizePool = matches.sumOf { it.prizePool }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // KPI Summary Cards Grid
        item {
            Text("PLATFORM HEALTH & REAL-TIME KPIS", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp, letterSpacing = 0.5.sp)
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KpiCard(
                    title = "ACTIVE MATCHES",
                    value = "${activeMatches.size}",
                    sub = "${matches.size} total listed",
                    color = CyberCyan,
                    icon = Icons.Default.SportsEsports,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(1) }
                )
                KpiCard(
                    title = "PENDING PAYOUTS",
                    value = "₹${"%.0f".format(totalPendingAmount)}",
                    sub = "${pendingWithdrawals.size} requests waiting",
                    color = if (pendingWithdrawals.isNotEmpty()) RadiantOrange else EmeraldGreen,
                    icon = Icons.Default.AccountBalanceWallet,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(2) }
                )
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                KpiCard(
                    title = "TOTAL PRIZE POOL",
                    value = "₹${"%.0f".format(totalPrizePool)}",
                    sub = "Disbursed / Scheduled",
                    color = OceanBlue,
                    icon = Icons.Default.EmojiEvents,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(1) }
                )
                KpiCard(
                    title = "REGISTERED GAMERS",
                    value = "$usersCount",
                    sub = "Active community",
                    color = ElectricViolet,
                    icon = Icons.Default.People,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigateTab(3) }
                )
            }
        }

        // Quick Actions Launcher
        item {
            Text("QUICK ACTION COMMANDS", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp, letterSpacing = 0.5.sp)
        }

        item {
            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = onCreateMatchClick,
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(44.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = DarkBg, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("NEW MATCH", color = DarkBg, fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }

                        Button(
                            onClick = onBroadcastClick,
                            colors = ButtonDefaults.buttonColors(containerColor = DarkElevated),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(44.dp)
                        ) {
                            Icon(Icons.Default.Campaign, contentDescription = null, tint = CyberCyanLight, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("PUSH ALERT", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = onBalanceAdjustClick,
                            colors = ButtonDefaults.buttonColors(containerColor = DarkElevated),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(44.dp)
                        ) {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = CyberGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("WALLET ADJUST", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        Button(
                            onClick = onToggleMaintenance,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isMaintenance) RadiantOrange.copy(alpha = 0.2f) else DarkElevated
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(44.dp)
                        ) {
                            Icon(
                                Icons.Default.Construction,
                                contentDescription = null,
                                tint = if (isMaintenance) RadiantOrange else TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                if (isMaintenance) "EXIT MAINT" else "MAINTENANCE",
                                color = if (isMaintenance) RadiantOrange else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Live Action Highlights
        item {
            Text("LIVE TOURNAMENTS MONITOR", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp, letterSpacing = 0.5.sp)
        }

        if (activeMatches.isEmpty()) {
            item {
                Surface(
                    color = DarkSurface,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "No live matches right now. Create a new match to start registrations!",
                        color = TextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(activeMatches.take(3)) { match ->
                Surface(
                    color = DarkSurface,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(match.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("${match.gameName} • ${match.startTime}", color = TextSecondary, fontSize = 11.sp)
                            Text(
                                text = "Room ID: ${match.roomId.ifBlank { "Not set" }} | Pass: ${match.roomPassword.ifBlank { "Not set" }}",
                                color = if (match.roomId.isNotBlank()) CyberCyanLight else TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        StatusBadge(match.status.name, if (match.status == MatchStatus.ONGOING) CrimsonRed else EmeraldGreen)
                    }
                }
            }
        }
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: String,
    sub: String,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = DarkSurface,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(2.dp))
            Text(sub, color = TextSecondary, fontSize = 10.sp)
        }
    }
}

// -------------------------------------------------------------
// TAB 2: MATCHES & TOURNAMENTS
// -------------------------------------------------------------
@Composable
private fun AdminMatchesTab(
    matches: List<Match>,
    onCreateMatch: () -> Unit,
    onSetRoom: (Match) -> Unit,
    onDeclareResult: (Match) -> Unit,
    onCancelMatch: (Match) -> Unit,
    onAdvanceStatus: (Match, MatchStatus) -> Unit
) {
    var filterStatus by remember { mutableStateOf<MatchStatus?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("MATCH CONTROLLER", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                Button(
                    onClick = onCreateMatch,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = DarkBg, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("CREATE MATCH", color = DarkBg, fontWeight = FontWeight.Black, fontSize = 10.sp)
                }
            }
        }

        // Filter chips
        item {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterStatus == null,
                    onClick = { filterStatus = null },
                    label = { Text("ALL (${matches.size})") }
                )
                FilterChip(
                    selected = filterStatus == MatchStatus.REGISTRATION_OPEN,
                    onClick = { filterStatus = MatchStatus.REGISTRATION_OPEN },
                    label = { Text("OPEN (${matches.count { it.status == MatchStatus.REGISTRATION_OPEN }})") }
                )
                FilterChip(
                    selected = filterStatus == MatchStatus.LIVE,
                    onClick = { filterStatus = MatchStatus.LIVE },
                    label = { Text("LIVE (${matches.count { it.status.isLive }})") }
                )
                FilterChip(
                    selected = filterStatus == MatchStatus.RESULT_PENDING || filterStatus == MatchStatus.RESULT_UNDER_REVIEW,
                    onClick = { filterStatus = MatchStatus.RESULT_PENDING },
                    label = { Text("REVIEW (${matches.count { it.status.canSubmitResult }})") }
                )
                FilterChip(
                    selected = filterStatus == MatchStatus.COMPLETED,
                    onClick = { filterStatus = MatchStatus.COMPLETED },
                    label = { Text("COMPLETED (${matches.count { it.status.isCompleted }})") }
                )
                FilterChip(
                    selected = filterStatus == MatchStatus.CANCELLED,
                    onClick = { filterStatus = MatchStatus.CANCELLED },
                    label = { Text("CANCELLED (${matches.count { it.status.isCancelled }})") }
                )
            }
        }

        val filteredMatches = matches.filter { filterStatus == null || it.status == filterStatus }

        if (filteredMatches.isEmpty()) {
            item {
                Surface(
                    color = DarkSurface,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Text(
                        text = "No matches found in this category.",
                        color = TextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        items(filteredMatches) { match ->
            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(match.title, color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                            Text("${match.gameName} • ${match.type.name} • ${match.mapName}", color = TextSecondary, fontSize = 11.sp)
                        }
                        StatusBadge(
                            match.status.name,
                            when {
                                match.status.isLive -> CrimsonRed
                                match.status == MatchStatus.REGISTRATION_OPEN -> EmeraldGreen
                                match.status == MatchStatus.ROOM_PUBLISHED -> CyberCyan
                                match.status.canSubmitResult -> RadiantOrange
                                match.status.isCompleted -> CyberGold
                                match.status.isCancelled -> CrimsonRed
                                else -> TextMuted
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Stats row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkElevated)
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("ENTRY", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(if (match.entryFee == 0.0) "FREE" else "₹${"%.0f".format(match.entryFee)}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("PRIZE POOL", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text("₹${"%.0f".format(match.prizePool)}", color = CyberCyanLight, fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("KILL BOUNTY", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text("₹${"%.0f".format(match.perKillReward)}", color = CyberGold, fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("SLOTS", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text("${match.playersJoined}/${match.maxSlots}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Room Info Box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkBg)
                            .border(1.dp, DarkBorderSubtle, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("ROOM CREDENTIALS", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "ID: ${match.roomId.ifBlank { "Not set" }} | Pass: ${match.roomPassword.ifBlank { "Not set" }}",
                                color = if (match.roomId.isNotBlank()) CyberCyanLight else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(
                            onClick = { onSetRoom(match) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Room", tint = CyberCyanLight, modifier = Modifier.size(16.dp))
                        }
                    }

                    // Lifecycle Status Quick Controls
                    if (!match.status.isCompleted && !match.status.isCancelled) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("ADVANCE LIFECYCLE STATE", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            when (match.status) {
                                MatchStatus.DRAFT, MatchStatus.UPCOMING -> {
                                    LifecycleActionButton("OPEN REGISTRATION", EmeraldGreen) {
                                        onAdvanceStatus(match, MatchStatus.REGISTRATION_OPEN)
                                    }
                                }
                                MatchStatus.REGISTRATION_OPEN -> {
                                    LifecycleActionButton("CLOSE REGISTRATION", RadiantOrange) {
                                        onAdvanceStatus(match, MatchStatus.REGISTRATION_CLOSED)
                                    }
                                    LifecycleActionButton("PREPARE ROOM", CyberGold) {
                                        onAdvanceStatus(match, MatchStatus.ROOM_PREPARING)
                                    }
                                }
                                MatchStatus.REGISTRATION_CLOSED -> {
                                    LifecycleActionButton("PREPARE ROOM", CyberGold) {
                                        onAdvanceStatus(match, MatchStatus.ROOM_PREPARING)
                                    }
                                    LifecycleActionButton("PUBLISH ROOM", CyberCyan) {
                                        onSetRoom(match)
                                    }
                                }
                                MatchStatus.ROOM_PREPARING -> {
                                    LifecycleActionButton("BROADCAST ROOM CREDENTIALS", CyberCyan) {
                                        onSetRoom(match)
                                    }
                                }
                                MatchStatus.ROOM_PUBLISHED, MatchStatus.READY -> {
                                    LifecycleActionButton("START MATCH (LIVE)", CrimsonRed) {
                                        onAdvanceStatus(match, MatchStatus.LIVE)
                                    }
                                }
                                MatchStatus.LIVE, MatchStatus.ONGOING -> {
                                    LifecycleActionButton("END MATCH (RESULT PENDING)", RadiantOrange) {
                                        onAdvanceStatus(match, MatchStatus.RESULT_PENDING)
                                    }
                                }
                                MatchStatus.RESULT_PENDING, MatchStatus.RESULT_SUBMITTED -> {
                                    LifecycleActionButton("MOVE TO UNDER REVIEW", ElectricViolet) {
                                        onAdvanceStatus(match, MatchStatus.RESULT_UNDER_REVIEW)
                                    }
                                    LifecycleActionButton("SETTLE & CREDIT PRIZE", EmeraldGreen) {
                                        onDeclareResult(match)
                                    }
                                }
                                MatchStatus.RESULT_UNDER_REVIEW -> {
                                    LifecycleActionButton("VERIFY & SETTLE PRIZE", EmeraldGreen) {
                                        onDeclareResult(match)
                                    }
                                }
                                else -> {}
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action Buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { onSetRoom(match) },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(36.dp)
                        ) {
                            Text("ROOM INFO", color = DarkBg, fontSize = 10.sp, fontWeight = FontWeight.Black)
                        }

                        if (!match.status.isCompleted && !match.status.isCancelled) {
                            Button(
                                onClick = { onDeclareResult(match) },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(36.dp)
                            ) {
                                Text("DECLARE RESULT", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }

                            IconButton(
                                onClick = { onCancelMatch(match) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CrimsonRed.copy(alpha = 0.2f))
                            ) {
                                Icon(Icons.Default.Cancel, contentDescription = "Cancel Match", tint = CrimsonRed, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LifecycleActionButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color.copy(alpha = 0.2f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, color),
        shape = RoundedCornerShape(6.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        modifier = Modifier.height(28.dp)
    ) {
        Text(text, color = color, fontSize = 9.sp, fontWeight = FontWeight.Black)
    }
}

// -------------------------------------------------------------
// TAB 3: WITHDRAWALS & LEDGER
// -------------------------------------------------------------
@Composable
private fun AdminWithdrawalsTab(
    withdrawals: List<WithdrawalRequest>,
    onApprove: (WithdrawalRequest) -> Unit,
    onReject: (WithdrawalRequest) -> Unit,
    onManualAdjust: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("PAYOUT & SETTLEMENT ENGINE", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                Button(
                    onClick = onManualAdjust,
                    colors = ButtonDefaults.buttonColors(containerColor = DarkElevated),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.AccountBalance, contentDescription = null, tint = CyberCyanLight, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("MANUAL ADJUST", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
            }
        }

        if (withdrawals.isEmpty()) {
            item {
                Surface(
                    color = DarkSurface,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Text(
                        text = "No withdrawal requests in queue.",
                        color = TextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        items(withdrawals) { req ->
            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(req.userName, color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                            Text("User ID: ${req.userId}", color = TextMuted, fontSize = 10.sp)
                        }
                        Text(
                            text = "₹${"%.2f".format(req.amount)}",
                            color = CyberGold,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = DarkElevated,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("PAYOUT METHOD", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text("${req.method}: ${req.paymentDetails}", color = CyberCyanLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            StatusBadge(
                                req.status.name,
                                when (req.status) {
                                    WithdrawalStatus.COMPLETED -> EmeraldGreen
                                    WithdrawalStatus.PENDING -> RadiantOrange
                                    WithdrawalStatus.REJECTED -> CrimsonRed
                                    else -> TextSecondary
                                }
                            )
                        }
                    }

                    if (req.adminNote.isNotBlank()) {
                        Text(
                            text = "Note: ${req.adminNote}",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }

                    if (req.status == WithdrawalStatus.PENDING) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { onApprove(req) },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).height(38.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("APPROVE & PAY", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                            }

                            Button(
                                onClick = { onReject(req) },
                                colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).height(38.dp)
                            ) {
                                Icon(Icons.Default.Cancel, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("REJECT & REFUND", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 4: PLAYERS & KYC
// -------------------------------------------------------------
@Composable
private fun AdminPlayersTab(
    users: List<User>,
    onToggleKyc: (User) -> Unit,
    onToggleBan: (User) -> Unit,
    onToggleAdmin: (User) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredUsers = users.filter {
        searchQuery.isBlank() ||
                it.username.contains(searchQuery, ignoreCase = true) ||
                it.fullName.contains(searchQuery, ignoreCase = true) ||
                it.email.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("USER & KYC DIRECTORY", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search by username, name or email") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        items(filteredUsers) { user ->
            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (user.isBlocked) CrimsonRed.copy(alpha = 0.5f) else DarkBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(DarkElevated),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(user.username.take(1).uppercase(), color = CyberCyanLight, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(user.fullName, color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                Text("@${user.username} • ${user.mobileNumber}", color = TextSecondary, fontSize = 11.sp)
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (user.role == UserRole.ADMIN) {
                                StatusBadge("ADMIN", ElectricViolet)
                            }
                            if (user.isBlocked) {
                                StatusBadge("BLOCKED", CrimsonRed)
                            } else if (user.isKycVerified) {
                                StatusBadge("KYC VERIFIED", EmeraldGreen)
                            } else {
                                StatusBadge("KYC PENDING", RadiantOrange)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // IGN mapping preview
                    if (user.inGameNames.isNotEmpty()) {
                        Text(
                            text = "Linked IGNs: " + user.inGameNames.entries.joinToString(" | ") { "${it.key.replace("game_", "").uppercase()}: ${it.value}" },
                            color = CyberCyanLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Action buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { onToggleKyc(user) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (user.isKycVerified) DarkElevated else EmeraldGreen
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(34.dp)
                        ) {
                            Text(
                                if (user.isKycVerified) "REVOKE KYC" else "VERIFY KYC",
                                color = if (user.isKycVerified) TextSecondary else Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Button(
                            onClick = { onToggleBan(user) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (user.isBlocked) EmeraldGreen else CrimsonRed.copy(alpha = 0.2f)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(34.dp)
                        ) {
                            Text(
                                if (user.isBlocked) "UNBAN" else "BAN USER",
                                color = if (user.isBlocked) Color.White else CrimsonRed,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Button(
                            onClick = { onToggleAdmin(user) },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkElevated),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(34.dp)
                        ) {
                            Text(
                                if (user.role == UserRole.ADMIN) "DEMOTE" else "MAKE ADMIN",
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

// -------------------------------------------------------------
// TAB 5: 1V1 DISPUTES
// -------------------------------------------------------------
@Composable
private fun AdminDisputesTab(
    challenges: List<PvPChallenge>,
    onResolve: (PvPChallenge) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("1V1 PVP DISPUTES & ANTI-CHEAT", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
        }

        if (challenges.isEmpty()) {
            item {
                Surface(
                    color = DarkSurface,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Text(
                        text = "No active 1v1 challenges or disputes.",
                        color = TextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        items(challenges) { challenge ->
            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (challenge.isDisputed) CrimsonRed.copy(alpha = 0.6f) else DarkBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(challenge.gameName, color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                        StatusBadge(
                            challenge.status.name,
                            if (challenge.isDisputed) CrimsonRed else CyberCyan
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Creator: ${challenge.creatorUsername} (${challenge.creatorInGameName}) VS Opponent: ${challenge.opponentUsername.ifBlank { "Waiting" }} (${challenge.opponentInGameName.ifBlank { "N/A" }})",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Entry: ₹${challenge.entryAmount} each | Winner Prize: ₹${challenge.prizeAmount}",
                        color = CyberGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    if (challenge.creatorProofUrl.isNotBlank() || challenge.opponentProofUrl.isNotBlank()) {
                        Text(
                            text = "Proof submitted: Creator (${if (challenge.creatorProofUrl.isNotBlank()) "YES" else "NO"}) | Opponent (${if (challenge.opponentProofUrl.isNotBlank()) "YES" else "NO"})",
                            color = CyberCyanLight,
                            fontSize = 11.sp
                        )
                    }

                    if (challenge.status != ChallengeStatus.COMPLETED && challenge.status != ChallengeStatus.CANCELLED) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { onResolve(challenge) },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(36.dp)
                        ) {
                            Text("RESOLVE DISPUTE / AWARD PRIZE", color = DarkBg, fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 6: CONFIG & BROADCAST
// -------------------------------------------------------------
@Composable
private fun AdminConfigTab(
    config: AppConfig,
    games: List<Game>,
    onToggleGame: (Game) -> Unit,
    onBroadcast: () -> Unit,
    onUpdateConfig: (AppConfig) -> Unit
) {
    var minWithdrawal by remember { mutableStateOf(config.minWithdrawalAmount.toString()) }
    var referralReward by remember { mutableStateOf(config.referralRewardDeposit.toString()) }
    var watchReward by remember { mutableStateOf(config.watchEarnRewardCash.toString()) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("GAME CATALOG TOGGLES", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
        }

        item {
            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    games.forEach { game ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(game.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(if (game.active) "Active for tournaments" else "Disabled / Hidden", color = TextMuted, fontSize = 11.sp)
                            }
                            Switch(
                                checked = game.active,
                                onCheckedChange = { onToggleGame(game) },
                                colors = SwitchDefaults.colors(checkedThumbColor = CyberCyan)
                            )
                        }
                        if (game != games.last()) {
                            HorizontalDivider(color = DarkBorderSubtle.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }

        item {
            Text("PUSH BROADCAST & ANNOUNCEMENTS", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
        }

        item {
            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Publish Global App Announcement", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Push realtime alert across all registered user home screens.", color = TextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onBroadcast,
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(40.dp)
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = null, tint = DarkBg, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("CREATE BROADCAST", color = DarkBg, fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Text("SYSTEM FINANCIAL PARAMETERS", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
        }

        item {
            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = minWithdrawal,
                        onValueChange = { minWithdrawal = it },
                        label = { Text("Minimum Withdrawal Limit (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = referralReward,
                        onValueChange = { referralReward = it },
                        label = { Text("Referral Bonus Deposit (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = watchReward,
                        onValueChange = { watchReward = it },
                        label = { Text("Watch Video Reward (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            val minW = minWithdrawal.toDoubleOrNull() ?: config.minWithdrawalAmount
                            val ref = referralReward.toDoubleOrNull() ?: config.referralRewardDeposit
                            val w = watchReward.toDoubleOrNull() ?: config.watchEarnRewardCash
                            onUpdateConfig(config.copy(
                                minWithdrawalAmount = minW,
                                referralRewardDeposit = ref,
                                watchEarnRewardCash = w
                            ))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OceanBlue),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(42.dp)
                    ) {
                        Text("SAVE CONFIGURATION", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 7: AUDIT TRAIL
// -------------------------------------------------------------
@Composable
private fun AdminAuditTrailTab(
    auditLogs: List<AuditLog>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("ROOT AUDIT TRAIL & LOGS", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
        }

        if (auditLogs.isEmpty()) {
            item {
                Surface(
                    color = DarkSurface,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Text("No audit log events recorded yet.", color = TextMuted, fontSize = 12.sp, modifier = Modifier.padding(16.dp))
                }
            }
        }

        items(auditLogs) { log ->
            Surface(
                color = DarkSurface,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusBadge(log.action, CyberCyan)
                        Text(log.module, color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(log.details, color = Color.White, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Executor: ${log.adminId}", color = TextSecondary, fontSize = 10.sp)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// CREATE MATCH MODAL DIALOG
// -------------------------------------------------------------
@Composable
private fun AdminCreateMatchDialog(
    games: List<Game>,
    onDismiss: () -> Unit,
    onCreate: (gameId: String, title: String, map: String, type: TournamentType, entry: Double, killBounty: Double, prize: Double, slots: Int, time: String) -> Unit
) {
    var selectedGameId by remember { mutableStateOf(games.firstOrNull()?.id ?: "game_bgmi") }
    var title by remember { mutableStateOf("Championship Squad Royale") }
    var mapName by remember { mutableStateOf("Erangel") }
    var type by remember { mutableStateOf(TournamentType.SQUAD) }
    var entryFeeText by remember { mutableStateOf("50") }
    var killBountyText by remember { mutableStateOf("20") }
    var prizePoolText by remember { mutableStateOf("5000") }
    var maxSlotsText by remember { mutableStateOf("100") }
    var startTimeText by remember { mutableStateOf("09:30 PM") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = {
            Text("Create New Match & Tournament", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text("Select Game:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        games.forEach { g ->
                            FilterChip(
                                selected = selectedGameId == g.id,
                                onClick = { selectedGameId = g.id },
                                label = { Text(g.name) }
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Match Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = mapName,
                            onValueChange = { mapName = it },
                            label = { Text("Map Name") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = startTimeText,
                            onValueChange = { startTimeText = it },
                            label = { Text("Time (e.g. 09:30 PM)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Text("Tournament Format:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        TournamentType.values().forEach { t ->
                            FilterChip(
                                selected = type == t,
                                onClick = { type = t },
                                label = { Text(t.name) }
                            )
                        }
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = entryFeeText,
                            onValueChange = { entryFeeText = it },
                            label = { Text("Entry Fee (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = killBountyText,
                            onValueChange = { killBountyText = it },
                            label = { Text("Per Kill (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = prizePoolText,
                            onValueChange = { prizePoolText = it },
                            label = { Text("Prize Pool (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = maxSlotsText,
                            onValueChange = { maxSlotsText = it },
                            label = { Text("Max Slots") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val entry = entryFeeText.toDoubleOrNull() ?: 0.0
                    val kill = killBountyText.toDoubleOrNull() ?: 0.0
                    val prize = prizePoolText.toDoubleOrNull() ?: 1000.0
                    val slots = maxSlotsText.toIntOrNull() ?: 50
                    onCreate(selectedGameId, title, mapName, type, entry, kill, prize, slots, startTimeText)
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
            ) {
                Text("LAUNCH TOURNAMENT", color = DarkBg, fontWeight = FontWeight.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = TextSecondary) }
        }
    )
}
